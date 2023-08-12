/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import static ie.francis.fsp.runtime.type.Type.FUNCTION;
import static ie.francis.fsp.runtime.type.Type.MACRO;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.runtime.type.*;
import ie.francis.fsp.runtime.type.Number;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class ClassGeneratorVisitor implements Visitor {

  private final String className;
  private final Environment environment;
  private final ClassWriter cw;
  private final MethodVisitor mv;

  private int quoteDepth = 0;
  private final Map<String, Integer> vars;

  private final LocalVariablesSorter lvs;

  public ClassGeneratorVisitor(
      String className, List<String> parameters, Environment environment) {

    this.className = className;
    this.environment = environment;
    this.vars = new HashMap<>();

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        ACC_PUBLIC + ACC_MODULE,
        String.format("%s", className),
        null,
        "java/lang/Object",
        new String[] {});

    String descriptor =
        "(" + "Ljava/lang/Object;".repeat(parameters.size()) + ")Ljava/lang/Object;";

    for (int i = 0; i < parameters.size(); i++) {
      vars.put(parameters.get(i), i);
    }

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", descriptor, null, null);
    lvs = new LocalVariablesSorter(0, "()Ljava/lang/Object;", mv);
    mv.visitCode();
  }

  @Override
  public void visit(Cons cons) {

    if (this.quoteDepth > 0) {
      compileQuotedList(cons);
      return;
    }

    if (cons.getCar() instanceof Symbol) {
      Symbol car = (Symbol) cons.getCar();
      if (isSpecialForm(car)) {
        compileSpecialForm(car, cons.getCdr());
      } else if (isFunction(car)) {
        if (isVariadic(car)) {
          compileArrayOfArguments(cons.getCdr(), true);
        } else {
          compileArguments(cons.getCdr(), true);
        }
        compileFunctionCall(car);
      } else if (isMacro(car)) {
        if (isVariadic(car)) {
          compileArrayOfArguments(cons.getCdr(), false);
        } else {
          compileArguments(cons.getCdr(), false);
        }
        compileMacroCall(car, cons.getCdr());
      }
    }
  }

  private void compileQuotedList(Cons cons) {

    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fsp/runtime/helper/ConsBuilder");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESPECIAL, "ie/francis/fsp/runtime/helper/ConsBuilder", "<init>", "()V", false);

    int id =
        lvs.newLocal(org.objectweb.asm.Type.getType("Lie/francis/fsp/runtime/helper/ConsBuilder"));
    mv.visitVarInsn(ASTORE, id);

    while (cons != null) {
      mv.visitVarInsn(ALOAD, id);
      ((DataType) cons.getCar()).accept(this);
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/fsp/runtime/helper/ConsBuilder",
          "add",
          "(Ljava/lang/Object;)V",
          false);
      cons = cons.getCdr();
    }

    mv.visitVarInsn(ALOAD, id);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "ie/francis/fsp/runtime/helper/ConsBuilder",
        "getCons",
        "()Lie/francis/fsp/runtime/type/Cons;",
        false);
  }

  private void compileMacroCall(Symbol symbol, Cons cons) {
    Macro macro = (Macro) environment.get(symbol);
    String owner = macro.name().split("\\.")[0];
    String macroName = macro.name().split("\\.")[1];
    List<DataType> params = new ArrayList<>();
    while (cons != null) {
      params.add((DataType) cons.getCar());
      cons = cons.getCdr();
    }
    Class<?>[] paramTypes = new Class[params.size()];
    Arrays.fill(paramTypes, Object.class);
    try {
      Object output =
          environment.loadClass(owner).getMethod("run", paramTypes).invoke(null, params.toArray());
      ((DataType) output).accept(this);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private void compileFunctionCall(Symbol symbol) {
    Function function = (Function) environment.get(symbol);
    String owner = function.name().split("\\.")[0];
    String functionName = function.name().split("\\.")[1];
    mv.visitMethodInsn(INVOKESTATIC, owner, functionName, function.descriptor(), false);
  }

  private boolean isMacro(Symbol symbol) {
    if (environment.contains(symbol)) {
      return environment.get(symbol).type() == MACRO;
    }
    return false;
  }

  private void compileArguments(Cons cons, boolean evaluate) {
    if (!evaluate) {
      this.quoteDepth++;
    }
    while (cons != null) {
      ((DataType) cons.getCar()).accept(this);
      cons = cons.getCdr();
    }
    if (!evaluate) {
      this.quoteDepth--;
    }
  }

  private void compileArrayOfArguments(Cons cons, boolean evaluate) {
    int size = cons.size();
    // Specify array size first
    mv.visitLdcInsn(size);
    // Create a new array of Objects
    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
    mv.visitInsn(DUP);
    if (!evaluate) {
      this.quoteDepth++;
    }
    int i = 0;
    while (cons != null) {
      mv.visitLdcInsn(i);
      ((DataType) cons.getCar()).accept(this);
      cons = cons.getCdr();
      mv.visitInsn(AASTORE);
      if (i < size - 1) {
        mv.visitInsn(DUP);
      }
      i++;
    }
    if (!evaluate) {
      this.quoteDepth--;
    }
  }

  private boolean isVariadic(Symbol symbol) {
    return environment.get(symbol).descriptor().startsWith("([");
  }

  private boolean isFunction(Symbol symbol) {
    if (environment.contains(symbol)) {
      return environment.get(symbol).type() == FUNCTION;
    }
    return false;
  }

  private void compileSpecialForm(Symbol car, Cons cons) {
    switch (car.name()) {
      case "quote":
        compileQuoteSpecialForm(cons);
        break;
      case "func":
        compileFuncSpecialForm(cons);
        break;
      case "macro":
        compileMacroSpecialForm(cons);
        break;
      case "if":
        compileIfSpecialForm(cons);
        break;
      case "progn":
        //        compilePrognSpecialForm(cons);
        break;
    }
  }

  private void compileMacroSpecialForm(Cons cons) {
    String macroName = ((Symbol) (cons.getCar())).name();
    String className = macroName.substring(0, 1).toUpperCase() + macroName.substring(1);

    List<String> args = new ArrayList<>();
    Cons argCons = (Cons) cons.getCdr().getCar();
    int paramCount = argCons.size();
    while (argCons != null) {
      args.add(((DataType) argCons.getCar()).name());
      argCons = argCons.getCdr();
    }

    String descriptor = "(" + "Ljava/lang/Object;".repeat(paramCount) + ")Ljava/lang/Object;";

    environment.put(macroName, new Macro(className + ".run", descriptor));
    ClassGeneratorVisitor cgv = new ClassGeneratorVisitor(className, args, environment);
    cons = cons.getCdr().getCdr();
    while (cons != null) {
      ((DataType) cons.getCar()).accept(cgv);
      cons = cons.getCdr();
    }
    cgv.write();

    environment.loadClass(className, cgv.generate());
    mv.visitInsn(ACONST_NULL);
  }

  private void compileIfSpecialForm(Cons cons) {

    // Compile condition, expecting a Cons cell
    ((Cons) cons.getCar()).accept(this);

    mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
    Label startOfElseLabel = new Label();
    Label endOfIfLabel = new Label();
    // Compare result of condition
    mv.visitJumpInsn(IFEQ, startOfElseLabel);

    // Compile true block
    ((DataType) cons.getCdr().getCar()).accept(this);

    // Jump past the false block after entering the true block if it exists
    mv.visitJumpInsn(GOTO, endOfIfLabel);
    mv.visitLabel(startOfElseLabel);

    // Compile false block if it exists
    if ((cons.getCdr().getCdr() != null)) {
      ((DataType) cons.getCdr().getCdr().getCar()).accept(this);
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitLabel(endOfIfLabel);
  }

  private void compileFuncSpecialForm(Cons cons) {
    String functionName = ((Symbol) (cons.getCar())).name();
    String className = functionName.substring(0, 1).toUpperCase() + functionName.substring(1);

    List<String> args = new ArrayList<>();
    Cons argCons = (Cons) cons.getCdr().getCar();
    int paramCount = argCons.size();
    while (argCons != null) {
      args.add(((DataType) argCons.getCar()).name());
      argCons = argCons.getCdr();
    }

    String descriptor = "(" + "Ljava/lang/Object;".repeat(paramCount) + ")Ljava/lang/Object;";

    environment.put(functionName, new Function(className + ".run", descriptor));
    ClassGeneratorVisitor cgv = new ClassGeneratorVisitor(className, args, environment);
    cons = cons.getCdr().getCdr();
    while (cons != null) {
      ((DataType) cons.getCar()).accept(cgv);
      cons = cons.getCdr();
    }
    cgv.write();

    environment.loadClass(className, cgv.generate());
    mv.visitInsn(ACONST_NULL);
  }

  private void compileQuoteSpecialForm(Cons cons) {
    this.quoteDepth++;
    while (cons != null) {
      ((DataType) cons.getCar()).accept(this);
      cons = cons.getCdr();
    }
    this.quoteDepth--;
  }

  private boolean isSpecialForm(Symbol symbol) {
    switch (symbol.name()) {
      case "quote":
      case "func":
      case "macro":
      case "if":
      case "progn":
        return true;
    }
    return false;
  }

  @Override
  public void visit(FspString fspString) {
    mv.visitLdcInsn(fspString.name());
  }

  @Override
  public void visit(Bool bool) {
    mv.visitLdcInsn(String.valueOf(bool.getValue()));
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Boolean",
        "valueOf",
        "(Ljava/lang/String;)Ljava/lang/Boolean;",
        false);
  }

  @Override
  public void visit(Symbol symbol) {
    if (quoteDepth > 0) {
      mv.visitLdcInsn(symbol.name());
      return;
    }
    if (vars.containsKey(symbol.name())) {

      lvs.visitVarInsn(ALOAD, vars.get(symbol.name()));
    }
  }

  @Override
  public void visit(Function function) {}

  @Override
  public void visit(Macro macro) {}

  @Override
  public void visit(Number number) {
    if (number.isFloat()) {
      mv.visitLdcInsn(number.getFValue());
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
    } else {
      mv.visitLdcInsn(number.getIValue());
      mv.visitMethodInsn(
          INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
    }
  }

  public byte[] generate() {
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
    return cw.toByteArray();
  }

  public void write() {
    try (FileOutputStream fos = new FileOutputStream(this.className + ".class")) {
      fos.write(this.generate());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getClassName() {
    return className;
  }
}
