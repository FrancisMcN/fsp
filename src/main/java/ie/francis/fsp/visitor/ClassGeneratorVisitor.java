/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.visitor;

import static ie.francis.fsp.runtime.type.Type.FUNCTION;
import static ie.francis.fsp.runtime.type.Type.MACRO;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.runtime.builtin.Read;
import ie.francis.fsp.runtime.type.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
  private int blockDepth = 0;
  private final Map<String, Integer> vars;

  private final LocalVariablesSorter lvs;
  private final Acceptor acceptor;

  public ClassGeneratorVisitor(String className, List<String> parameters, Environment environment) {

    this.acceptor = new AcceptorImpl();
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

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", descriptor, null, null);
    lvs = new LocalVariablesSorter(0, "()Ljava/lang/Object;", mv);
    for (int i = 0; i < parameters.size(); i++) {
      vars.put(parameters.get(i), i);
    }

    mv.visitCode();
  }

  @Override
  public void visit(Integer integer) {
    mv.visitLdcInsn(integer);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
  }

  @Override
  public void visit(Float floating) {
    mv.visitLdcInsn(floating);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
  }

  @Override
  public void visit(Cons cons) {

    if (this.quoteDepth > 0) {
      compileListOfCons(cons);
      return;
    }

    if (cons.getCar() instanceof Symbol) {
      Symbol car = (Symbol) cons.getCar();
      if (isSpecialForm(car)) {
        compileSpecialForm(car, cons.getCdr());
      } else if (isFunction(car)) {
        Function f = ((Function) environment.get(car));
        compileArguments(f, cons.getCdr(), true);
        compileFunctionCall(car);
      } else if (isMacro(car)) {
        Function f = ((Function) environment.get(car));
        compileArguments(f, cons.getCdr(), false);
        compileMacroCall(car, cons.getCdr());
      }
    } else {
      while (cons != null) {
        acceptor.accept(cons.getCar(), this);
        cons = cons.getCdr();
      }
    }
  }

  @Override
  public void visit(Boolean bool) {
    mv.visitLdcInsn(bool);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
  }

  private void compileListOfCons(Cons cons) {

    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fsp/runtime/helper/ConsBuilder");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESPECIAL, "ie/francis/fsp/runtime/helper/ConsBuilder", "<init>", "()V", false);

    int id =
        lvs.newLocal(org.objectweb.asm.Type.getType("Lie/francis/fsp/runtime/helper/ConsBuilder"));
    lvs.visitVarInsn(ASTORE, id);

    while (cons != null) {
      lvs.visitVarInsn(ALOAD, id);
      acceptor.accept(cons.getCar(), this);
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/fsp/runtime/helper/ConsBuilder",
          "add",
          "(Ljava/lang/Object;)V",
          false);
      cons = cons.getCdr();
    }

    lvs.visitVarInsn(ALOAD, id);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "ie/francis/fsp/runtime/helper/ConsBuilder",
        "getCons",
        "()Lie/francis/fsp/runtime/type/Cons;",
        false);
  }

  private void compileMacroCall(Symbol symbol, Cons cons) {
    Macro macro = (Macro) environment.get(symbol);
    macro.expand(cons, environment, this);
  }

  private void compileFunctionCall(Symbol symbol) {
    Function function = (Function) environment.get(symbol);
    String owner = function.name().split("\\.")[0];
    String functionName = function.name().split("\\.")[1];
    mv.visitMethodInsn(INVOKESTATIC, owner, functionName, function.descriptor(), false);
  }

  private boolean isMacro(Symbol symbol) {
    if (environment.contains(symbol)) {
      return ((DataType) environment.get(symbol)).type() == MACRO;
    }
    return false;
  }

  private void compileArguments(Function f, Cons cons, boolean evaluate) {
    if (!evaluate) {
      this.quoteDepth++;
    }
    int restParam = -1;
    List<String> params = f.getParams();
    if (params.get((params.size() - 2) % params.size()).equals("&rest")) {
      restParam = (params.size() - 2) % params.size();
    }
    int i = 0;
    while (cons != null) {

      if (i == restParam) {
        compileListOfCons(cons);
        mv.visitInsn(DUP);
        break;
      }
      acceptor.accept(cons.getCar(), this);
      cons = cons.getCdr();
      i++;
    }
    if (!evaluate) {
      this.quoteDepth--;
    }
  }

  private boolean isFunction(Symbol symbol) {
    if (environment.contains(symbol)) {
      return ((DataType) environment.get(symbol)).type() == FUNCTION;
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
      case "defmacro":
        compileDefMacroSpecialForm(cons);
        break;
      case "load":
        compileLoadSpecialForm(cons);
        break;
      case "let":
        compileLetSpecialForm(cons);
        break;
      case "if":
        compileIfSpecialForm(cons);
        break;
      case "progn":
        compilePrognSpecialForm(cons);
        break;
    }
  }

  private void compileLetSpecialForm(Cons cons) {
    String name = cons.getCar().toString();
    if (blockDepth > 0) {
      String descriptor = findTypeDescriptor(cons.getCdr().getCar());
      int id;
      if (!vars.containsKey(name)) {
        id = lvs.newLocal(org.objectweb.asm.Type.getType(descriptor));
        vars.put(name, id);
      } else {
        id = vars.get(name);
      }

      acceptor.accept(cons.getCdr().getCar(), this);
      lvs.visitVarInsn(ASTORE, id);
    } else {
      environment.put(name, cons.getCdr().getCar());
    }

    mv.visitInsn(ACONST_NULL);
  }

  private String findTypeDescriptor(Object object) {
    if (object instanceof DataType) {
      return ((DataType) object).descriptor();
    } else {
      return "Ljava/lang/Object;";
    }
  }

  private void compileLoadSpecialForm(Cons cons) {
    String data;
    try {
      data = Files.readString(Path.of(cons.getCar().toString()));
      Read.run(data);
      mv.visitInsn(ACONST_NULL);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void compilePrognSpecialForm(Cons cons) {
    while (cons != null) {
      acceptor.accept(cons.getCar(), this);
      // Ignore all but final return value
      if (cons.getCdr() != null) {
        mv.visitInsn(POP);
      }
      cons = cons.getCdr();
    }
  }

  private void compileDefMacroSpecialForm(Cons cons) {
    String macroName = ((Symbol) (cons.getCar())).name();
    String className = macroName.substring(0, 1).toUpperCase() + macroName.substring(1);
    className = className.replace(".", "_dot_");
    List<String> args = new ArrayList<>();
    Cons argCons = (Cons) cons.getCdr().getCar();
    int paramCount = argCons.size();
    while (argCons != null) {
      args.add(((DataType) argCons.getCar()).name());
      argCons = argCons.getCdr();
    }

    String descriptor = "(" + "Ljava/lang/Object;".repeat(paramCount) + ")Ljava/lang/Object;";

    environment.put(macroName, new Macro(className + ".run", descriptor, args));
    ClassGeneratorVisitor cgv = new ClassGeneratorVisitor(className, args, environment);
    cons = cons.getCdr().getCdr();
    acceptor.accept(cons.getCar(), cgv);
    cgv.write();

    environment.loadClass(className, cgv.generate());
    mv.visitInsn(ACONST_NULL);
  }

  private void compileIfSpecialForm(Cons cons) {

    // Compile condition, expecting a Cons cell
    acceptor.accept(cons.getCar(), this);

    mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
    Label startOfElseLabel = new Label();
    Label endOfIfLabel = new Label();
    // Compare result of condition
    mv.visitJumpInsn(IFEQ, startOfElseLabel);

    // Compile true block
    if (cons.getCdr() != null) {
      acceptor.accept(cons.getCdr().getCar(), this);
    }

    // Jump past the false block after entering the true block if it exists
    mv.visitJumpInsn(GOTO, endOfIfLabel);
    mv.visitLabel(startOfElseLabel);

    // Compile false block if it exists
    if (cons.getCdr() != null && cons.getCdr().getCdr() != null) {
      acceptor.accept(cons.getCdr().getCdr().getCar(), this);
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitLabel(endOfIfLabel);
  }

  private void compileFuncSpecialForm(Cons cons) {
    String functionName = ((Symbol) (cons.getCar())).name();
    String className = functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
    className = className.replace(".", "_dot_");

    List<String> args = new ArrayList<>();
    Cons argCons = (Cons) cons.getCdr().getCar();
    int paramCount = argCons.size();
    while (paramCount > 0 && argCons != null) {
      args.add(((DataType) argCons.getCar()).name());
      argCons = argCons.getCdr();
    }

    String descriptor = "(" + "Ljava/lang/Object;".repeat(paramCount) + ")Ljava/lang/Object;";

    environment.put(functionName, new Function(className + ".run", descriptor, args));
    ClassGeneratorVisitor cgv = new ClassGeneratorVisitor(className, args, environment);
    cgv.blockDepth++;
    cons = cons.getCdr().getCdr();
    while (cons != null) {
      acceptor.accept(cons.getCar(), cgv);
      cons = cons.getCdr();
    }
    cgv.blockDepth--;
    cgv.write();

    environment.loadClass(className, cgv.generate());
    mv.visitInsn(ACONST_NULL);
  }

  private void compileQuoteSpecialForm(Cons cons) {
    this.quoteDepth++;
    while (cons != null) {
      acceptor.accept(cons.getCar(), this);
      cons = cons.getCdr();
    }
    this.quoteDepth--;
  }

  private boolean isSpecialForm(Symbol symbol) {
    switch (symbol.name()) {
      case "quote":
      case "func":
      case "defmacro":
      case "if":
      case "let":
      case "load":
      case "progn":
        return true;
    }
    return false;
  }

  @Override
  public void visit(Symbol symbol) {
    if (quoteDepth > 0) {
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/fsp/runtime/type/Symbol");
      mv.visitInsn(DUP);
      mv.visitLdcInsn(symbol.name());
      mv.visitMethodInsn(
          INVOKESPECIAL,
          "ie/francis/fsp/runtime/type/Symbol",
          "<init>",
          "(Ljava/lang/String;)V",
          false);

      return;
    }
    if (vars.containsKey(symbol.name())) {
      lvs.visitVarInsn(ALOAD, vars.get(symbol.name()));
    } else if (environment.contains(symbol)) {
      acceptor.accept(environment.get(symbol), this);
    }
  }

  @Override
  public void visit(String str) {
    mv.visitLdcInsn(str);
  }

  @Override
  public void visit(Atom atom) {
    switch (atom.type()) {
      case STRING:
        acceptor.accept(atom.getString(), this);
        break;
      case SYMBOL:
        acceptor.accept(atom.getSymbol(), this);
        break;
      case NUMBER:
        if (atom.getFloat() != null) {
          acceptor.accept(atom.getFloat(), this);
        } else {
          acceptor.accept(atom.getInteger(), this);
        }
        break;
      case BOOL:
        acceptor.accept(atom.getBool(), this);
        break;
    }
  }

  @Override
  public void visit(Function function) {}

  @Override
  public void visit(Macro macro) {}

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
