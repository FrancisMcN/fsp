/*
 * (c) 2025 Francis McNamee
 * */
 
package ie.francis.lisp.compiler;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.lisp.Environment;
import ie.francis.lisp.exception.InvalidConsException;
import ie.francis.lisp.exception.UndefinedSymbolException;
import ie.francis.lisp.function.*;
import ie.francis.lisp.loader.LispClassLoader;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Lambda;
import ie.francis.lisp.type.Macro;
import ie.francis.lisp.type.Symbol;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Compiler {

  private ClassWriter cw;
  private MethodVisitor mv;

  private int quoteDepth;

  private String className;

  private final LocalTable locals;

  private boolean isMacro;

  private boolean writeClassesToDisk;

  static LispClassLoader lispClassLoader = new LispClassLoader();

  public Compiler() {
    quoteDepth = 0;
    isMacro = false;
    className = String.format("Lambda%d", new Random().nextInt(1000000000));
    locals = new LocalTable();
    locals.add("this", null);
    writeClassesToDisk = false;
  }

  public Compiler(boolean isMacro) {
    this();
    this.isMacro = isMacro;
    className = String.format("Macro%d", new Random().nextInt(1000000000));
  }

  public Object compile(Object object) {

    // Pre class-generation
    start();
    // Add code to call() method with arity 0
    mv = cw.visitMethod(ACC_PUBLIC, "call", "()Ljava/lang/Object;", null, null);
    mv.visitCode();

    _compile(object);

    // Post class-generation
    finish();

    lispClassLoader.defineClass(className, cw.toByteArray());
    if (writeClassesToDisk) {
      writeClassToDisk(className);
    }
    return loadClass(className);
  }

  public Object compileLambdaOrMacro(Object object) {
    // Pre class-generation
    start();

    // Record local variables and determine function arity
    Cons cons = ((Cons) (((Cons) object).getCdr().getCar()));
    while (cons != null) {
      locals.add(cons.getCar().toString(), null);
      cons = cons.getCdr();
    }

    int arity = locals.size() - 1;

    // Add code to call method with required arity
    mv =
        cw.visitMethod(
            ACC_PUBLIC,
            "call",
            "(" + "Ljava/lang/Object;".repeat(arity) + ")Ljava/lang/Object;",
            null,
            null);
    mv.visitCode();

    Cons body = ((Cons) object).getCdr().getCdr();
    while (body != null) {
      _compile(body.getCar());
      body = body.getCdr();
    }

    // Post class-generation
    finish();

    lispClassLoader.defineClass(className, cw.toByteArray());
    if (writeClassesToDisk) {
      writeClassToDisk(className);
    }
    return loadClass(className);
  }

  public void setWriteClassesToDisk(boolean writeClassesToDisk) {
    this.writeClassesToDisk = writeClassesToDisk;
  }

  private void writeClassToDisk(String className) {
    try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
      fos.write(cw.toByteArray());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private Object loadClass(String className) {
    try {
      Class<?> c = lispClassLoader.loadClass(className);
      Constructor<?> ctor = c.getConstructor();
      return ctor.newInstance();
    } catch (ClassNotFoundException
        | InvocationTargetException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public Object compileLambda(Object object) {
    return compileLambdaOrMacro(object);
  }

  public Object compileMacro(Object object) {
    return compileLambdaOrMacro(object);
  }

  public Object _compile(Object object) {
    if (object instanceof Symbol) {
      return compileSymbol((Symbol) object);
    } else if (object instanceof String) {
      return compileString((String) object);
    } else if (object instanceof Boolean) {
      return compileBoolean((Boolean) object);
    } else if (object instanceof Integer) {
      return compileNumber((Integer) object);
    } else if (object instanceof BigInteger) {
      return compileNumber((BigInteger) object);
    } else if (object instanceof Float) {
      return compileNumber((Float) object);
    } else if (object instanceof Cons) {
      return compileCons((Cons) object);
    } else if (object == null) {
      mv.visitInsn(ACONST_NULL);
    }
    return null;
  }

  private void finish() {
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
  }

  private void start() {
    String impl = "ie/francis/lisp/type/Lambda";
    if (isMacro) {
      impl = "ie/francis/lisp/type/Macro";
    }
    cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5, ACC_PUBLIC + ACC_MODULE, className, null, "java/lang/Object", new String[] {impl});

    // Add a constructor to the generated class
    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private Integer compileNumber(Integer integer) {
    mv.visitLdcInsn(integer);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
    return integer;
  }

  private BigInteger compileNumber(BigInteger integer) {

    mv.visitTypeInsn(Opcodes.NEW, "java/math/BigInteger");
    mv.visitInsn(DUP);
    mv.visitLdcInsn(integer.toString());
    mv.visitMethodInsn(
        INVOKESPECIAL, "java/math/BigInteger", "<init>", "(Ljava/lang/String;)V", false);
    return integer;
  }

  private Float compileNumber(Float floating) {
    mv.visitLdcInsn(floating);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
    return floating;
  }

  private Boolean compileBoolean(Boolean bool) {
    mv.visitLdcInsn(bool);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
    return bool;
  }

  private String compileString(String string) {
    mv.visitLdcInsn(string);
    return string;
  }

  private Object compileSymbol(Symbol symbol) {
    if (isQuoted() && !symbol.getValue().equals("nil")) {
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Symbol");
      mv.visitInsn(DUP);
      mv.visitLdcInsn(symbol.getValue());
      mv.visitMethodInsn(
          INVOKESPECIAL, "ie/francis/lisp/type/Symbol", "<init>", "(Ljava/lang/String;)V", false);
      return symbol;
    }

    if (locals.contains(symbol.getValue())) {
      LocalTable.Local local = locals.get(symbol.getValue());
      mv.visitVarInsn(ALOAD, local.getLocalId());
      return local.getLocal();
    }

    mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Symbol");
    mv.visitInsn(DUP);
    mv.visitLdcInsn(symbol.getValue());
    mv.visitMethodInsn(
        INVOKESPECIAL, "ie/francis/lisp/type/Symbol", "<init>", "(Ljava/lang/String;)V", false);
    mv.visitMethodInsn(
        INVOKESTATIC,
        "ie/francis/lisp/Environment",
        "get",
        "(Lie/francis/lisp/type/Symbol;)Ljava/lang/Object;",
        false);

    return symbol;
  }

  private Object compileCons(Cons cons) {
    if (isQuoted()) {
      compileConsWithoutEvaluating(cons);
      return cons;
    }

    Object first = cons.getCar();
    if (isSpecialForm(cons)) {
      return compileSpecialForm(cons);
    }

    if (first instanceof Cons) {
      Object compiled = _compile(first);
      if (compiled instanceof Lambda) {
        mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/function/Apply");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "ie/francis/lisp/function/Apply", "<init>", "()V", false);
        compileLambdaCall(cons);
      }
      return cons;
    }
    if (first instanceof Symbol) {
      Symbol symbol = (Symbol) first;
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/function/Apply");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "ie/francis/lisp/function/Apply", "<init>", "()V", false);
      if (Environment.contains(symbol) && Environment.get(symbol) instanceof Macro) {
        _compile(expandMacro(cons));
      } else {
        compileLambdaCall(cons);
      }
      if (Environment.contains(symbol)) {
        return Environment.get(symbol);
      }
      return null;
    }

    throw new InvalidConsException("expected a function in first cons cell");
  }

  private Object expandMacro(Cons cons) {
    return new MacroExpand1().call(cons);
  }

  private void compileLambdaCall(Cons cons) {
    int size = 0;
    if (cons != null) {
      size = cons.size();
    }
    if (size > 5) {
      compileVariadicCall(cons, true);
    } else {
      compileRegularCall(cons, true);
    }
  }

  private void compileRegularCall(Cons cons, boolean evaluateArgs) {
    int size = 0;
    if (cons != null) {
      size = cons.size();
    }
    String descriptor = "(" + "Ljava/lang/Object;".repeat(size) + ")Ljava/lang/Object;";

    if (cons != null) {
      _compile(cons.getCar());
    }
    cons = cons.getCdr();
    if (!evaluateArgs) {
      quoteDepth++;
    }
    while (cons != null) {
      _compile(cons.getCar());
      cons = cons.getCdr();
    }
    if (!evaluateArgs) {
      quoteDepth--;
    }

    mv.visitMethodInsn(
        INVOKEVIRTUAL, Apply.class.getName().replace(".", "/"), "call", descriptor, false);
  }

  private void compileVariadicCall(Cons cons, boolean evaluateArgs) {
    compileVariadicArgs(cons, evaluateArgs);
    String descriptor = "([Ljava/lang/Object;)Ljava/lang/Object;";
    mv.visitMethodInsn(
        INVOKEVIRTUAL, Apply.class.getName().replace(".", "/"), "call", descriptor, false);
  }

  private void compileVariadicArgs(Cons cons, boolean evaluateArgs) {
    int size = 0;
    if (cons != null) {
      size = cons.size();
    }

    mv.visitLdcInsn(size);
    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
    mv.visitInsn(DUP);
    int i = 0;
    if (cons != null) {
      mv.visitLdcInsn(i);
      _compile(cons.getCar());
      mv.visitInsn(AASTORE);
      if (i < (size - 1)) {
        mv.visitInsn(DUP);
      }
      cons = cons.getCdr();
      i++;
    }
    if (!evaluateArgs) {
      quoteDepth++;
    }
    while (cons != null) {
      mv.visitLdcInsn(i);
      _compile(cons.getCar());
      mv.visitInsn(AASTORE);
      if (i < (size - 1)) {
        mv.visitInsn(DUP);
      }
      cons = cons.getCdr();
      i++;
    }
    if (!evaluateArgs) {
      quoteDepth--;
    }
  }

  private void compileConsWithoutEvaluating(Cons cons) {
    Cons head = cons;
    while (cons != null) {

      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Cons");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "ie/francis/lisp/type/Cons", "<init>", "()V", false);
      _compile(cons.getCar());
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/lisp/type/Cons",
          "setCar",
          "(Ljava/lang/Object;)Lie/francis/lisp/type/Cons;",
          false);
      cons = cons.getCdr();
    }

    cons = head.getCdr();
    while (cons != null) {
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/lisp/type/Cons",
          "setCdr",
          "(Lie/francis/lisp/type/Cons;)Lie/francis/lisp/type/Cons;",
          false);
      cons = cons.getCdr();
    }
  }

  private Object compileSpecialForm(Cons cons) {
    Object ret = null;
    Symbol symbol = (Symbol) cons.getCar();
    switch (symbol.getValue()) {
      case "lambda":
        {
          Compiler compiler = new Compiler();
          ret = compiler.compileLambda(cons);
          Lambda lambda = (Lambda) ret;
          mv.visitTypeInsn(Opcodes.NEW, lambda.getClass().getName());
          mv.visitInsn(DUP);
          mv.visitMethodInsn(INVOKESPECIAL, lambda.getClass().getName(), "<init>", "()V", false);
          break;
        }
      case "macro":
        {
          Compiler compiler = new Compiler(true);
          ret = compiler.compileMacro(cons);
          Macro macro = (Macro) ret;
          mv.visitTypeInsn(Opcodes.NEW, macro.getClass().getName());
          mv.visitInsn(DUP);
          mv.visitMethodInsn(INVOKESPECIAL, macro.getClass().getName(), "<init>", "()V", false);
          break;
        }
      case "quote":
        compileQuoteSpecialForm(cons);
        break;
      case "if":
        compileIfSpecialForm(cons);
        break;
      case "let":
        compileLetSpecialForm(cons);
        break;
      case "set":
        compileSetSpecialForm(cons);
        break;
      case "def":
        compileDefSpecialForm(cons);
        break;
      case "do":
        compileDoSpecialForm(cons);
        break;
      case ".":
        compileDotSpecialForm(cons);
        break;
    }
    return ret;
  }

  private void compileDotSpecialForm(Cons cons) {
    Cons body = cons.getCdr();
    _compile(body.getCar());

    Symbol method = (Symbol) body.getCdr().getCar();
    mv.visitLdcInsn(method.getValue());

    Cons args = body.getCdr().getCdr();

    String descriptor = "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;";
    if (args != null) {
      compileVariadicArgs(args, true);
      descriptor = "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;";
    }

    mv.visitMethodInsn(
        INVOKESTATIC, "ie/francis/lisp/reflect/Reflector", "invoke", descriptor, false);
  }

  private void compileDoSpecialForm(Cons cons) {
    Cons body = cons.getCdr();
    if (body == null) {
      mv.visitInsn(ACONST_NULL);
    }
    while (body != null) {
      _compile(body.getCar());
      body = body.getCdr();
      if (body != null) {
        mv.visitInsn(POP);
      }
    }
  }

  private void compileDefSpecialForm(Cons cons) {
    Symbol symbol = (Symbol) cons.getCdr().getCar();
    quoteDepth++;
    Object name = _compile(symbol);
    quoteDepth--;
    Object value = _compile(cons.getCdr().getCdr().getCar());
    mv.visitMethodInsn(
        INVOKESTATIC,
        "ie/francis/lisp/Environment",
        "put",
        "(Lie/francis/lisp/type/Symbol;Ljava/lang/Object;)Ljava/lang/Object;",
        false);
    Environment.put((Symbol) name, value);
  }

  // (let ([<a> <b>]+) form*)
  private void compileLetSpecialForm(Cons cons) {

    // Create new scope for let form
    locals.pushScope();

    // Compile each symbol:assignment pair
    Cons assignments = (Cons) cons.getCdr().getCar();
    while (assignments != null) {
      Symbol symbol = (Symbol) assignments.getCar();
      Object value = assignments.getCdr().getCar();
      Object local = _compile(value);
      locals.add(symbol.getValue(), local);
      mv.visitVarInsn(ASTORE, locals.get(symbol.getValue()).getLocalId());
      // Move to next assignment if there is one
      assignments = assignments.getCdr().getCdr();
    }

    // Compile body of let statement if exists
    Cons letBody = cons.getCdr().getCdr();
    if (letBody == null) {
      mv.visitInsn(ACONST_NULL);
    }
    while (letBody != null) {
      _compile(letBody.getCar());
      letBody = letBody.getCdr();
    }

    locals.popScope();
  }

  private void compileSetSpecialForm(Cons cons) {

    Cons body = cons.getCdr();
    while (body != null) {
      Symbol symbol = (Symbol) body.getCar();
      Object value = body.getCdr().getCar();
      Object local = _compile(value);
      if (!locals.contains(symbol.getValue())) {
        throw new UndefinedSymbolException(symbol.getValue());
      }
      locals.add(symbol.getValue(), local);
      mv.visitVarInsn(ASTORE, locals.get(symbol.getValue()).getLocalId());
      body = body.getCdr().getCdr();
    }
    mv.visitInsn(ACONST_NULL);
  }

  private void compileIfSpecialForm(Cons cons) {

    // Compile condition, expecting a Cons cell
    _compile(cons.getCdr().getCar());

    mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
    Label startOfElseLabel = new Label();
    Label endOfIfLabel = new Label();
    // Compare result of condition
    mv.visitJumpInsn(IFEQ, startOfElseLabel);

    // Compile true block
    if (cons.getCdr().getCdr() != null) {
      _compile(cons.getCdr().getCdr().getCar());
    }

    // Jump past the false block after entering the true block if it exists
    mv.visitJumpInsn(GOTO, endOfIfLabel);
    mv.visitLabel(startOfElseLabel);

    // Compile false block if it exists
    if (cons.getCdr().getCdr() != null && cons.getCdr().getCdr().getCdr() != null) {
      _compile(cons.getCdr().getCdr().getCdr().getCar());
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitLabel(endOfIfLabel);
  }

  private void compileQuoteSpecialForm(Cons cons) {
    quoteDepth++;
    _compile(cons.getCdr().getCar());
    quoteDepth--;
  }

  private boolean isSpecialForm(Cons cons) {
    Object first = cons.getCar();
    if (first instanceof Symbol) {
      Symbol symbol = (Symbol) first;
      switch (symbol.getValue()) {
        case "lambda":
        case "macro":
        case "def":
        case "quote":
        case "if":
        case "let":
        case "set":
        case "do":
        case ".":
          return true;
      }
    }
    return false;
  }

  private boolean isQuoted() {
    return quoteDepth > 0;
  }
}
