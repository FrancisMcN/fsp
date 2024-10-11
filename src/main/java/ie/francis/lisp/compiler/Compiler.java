/*
 * (c) 2024 Francis McNamee
 * */
 
package ie.francis.lisp.compiler;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.lisp.Environment;
import ie.francis.lisp.exception.InvalidConsException;
import ie.francis.lisp.function.*;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Macro;
import ie.francis.lisp.type.Symbol;
import java.util.*;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Compiler {

  private ClassWriter cw;
  private MethodVisitor mv;

  private int quoteDepth;

  private String className;
  private final List<Artifact> artifacts;

  private final LocalTable locals;

  private boolean isMacro;

  public Compiler() {
    quoteDepth = 0;
    isMacro = false;
    className = String.format("Lambda%d", new Random().nextInt(1000000000));
    artifacts = new ArrayList<>();
    locals = new LocalTable();
    locals.add("this", null);
  }

  public Compiler(boolean isMacro) {
    this();
    this.isMacro = isMacro;
    className = String.format("Macro%d", new Random().nextInt(1000000000));
  }

  public Metadata compile(Object object) {

    // Pre class-generation
    start();
    // Add code to call() method with arity 0
    mv = cw.visitMethod(ACC_PUBLIC, "call", "()Ljava/lang/Object;", null, null);
    mv.visitCode();

    Metadata meta = _compile(object);

    // Post class-generation
    finish();

    this.artifacts.add(new Artifact(className, cw.toByteArray()));

    return meta;
  }

  public Metadata compileLambdaOrMacro(Object object) {
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

    this.artifacts.add(new Artifact(className, cw.toByteArray()));
    if (isMacro) {
      return new Metadata(Metadata.Type.MACRO, className);
    }
    return new Metadata(Metadata.Type.LAMBDA, className);
  }

  public Metadata compileLambda(Object object) {
    return compileLambdaOrMacro(object);
  }

  public Metadata compileMacro(Object object) {
    return compileLambdaOrMacro(object);
  }

  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  public Metadata _compile(Object object) {
    Metadata meta = new Metadata(Metadata.Type.NIL, "nil");
    if (object instanceof Symbol) {
      meta = compileSymbol((Symbol) object);
    } else if (object instanceof String) {
      meta = compileString((String) object);
    } else if (object instanceof Boolean) {
      meta = compileBoolean((Boolean) object);
    } else if (object instanceof Integer) {
      meta = compileNumber((Integer) object);
    } else if (object instanceof Float) {
      meta = compileNumber((Float) object);
    } else if (object instanceof Cons) {
      meta = compileCons((Cons) object);
    } else if (object == null) {
      mv.visitInsn(ACONST_NULL);
    }
    return meta;
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

  private Metadata compileNumber(Integer integer) {
    mv.visitLdcInsn(integer);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
    return new Metadata(Metadata.Type.INTEGER, integer.toString());
  }

  private Metadata compileNumber(Float floating) {
    mv.visitLdcInsn(floating);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
    return new Metadata(Metadata.Type.FLOAT, floating.toString());
  }

  private Metadata compileBoolean(Boolean bool) {
    mv.visitLdcInsn(bool);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
    return new Metadata(Metadata.Type.BOOLEAN, bool.toString());
  }

  private Metadata compileString(String string) {
    mv.visitLdcInsn(string);
    return new Metadata(Metadata.Type.STRING, string);
  }

  private Metadata compileSymbol(Symbol symbol) {
    Metadata meta = new Metadata(Metadata.Type.SYMBOL, symbol.getValue());
    if (isQuoted() && !symbol.getValue().equals("nil")) {
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Symbol");
      mv.visitInsn(DUP);
      mv.visitLdcInsn(symbol.getValue());
      mv.visitMethodInsn(
          INVOKESPECIAL, "ie/francis/lisp/type/Symbol", "<init>", "(Ljava/lang/String;)V", false);
      return meta;
    }

    if (locals.contains(symbol.getValue())) {
      mv.visitVarInsn(ALOAD, locals.get(symbol.getValue()).getLocalId());
      return meta;
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

    return meta;
  }

  private Metadata compileCons(Cons cons) {
    Metadata meta = new Metadata(Metadata.Type.CONS, cons.toString());
    if (isQuoted()) {
      compileConsWithoutEvaluating(cons);
      return meta;
    }

    Object first = cons.getCar();
    if (isSpecialForm(cons)) {
      return compileSpecialForm(cons);
    }

    if (first instanceof Cons) {
      Metadata metadata = _compile(first);
      if (metadata.getType() == Metadata.Type.LAMBDA) {
        mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/function/Apply");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "ie/francis/lisp/function/Apply", "<init>", "()V", false);
        compileLambdaCall(cons);
      }
      return meta;
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
      return meta;
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
    int size = 0;
    if (cons != null) {
      size = cons.size();
    }
    String descriptor = "([Ljava/lang/Object;)Ljava/lang/Object;";

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
    mv.visitMethodInsn(
        INVOKEVIRTUAL, Apply.class.getName().replace(".", "/"), "call", descriptor, false);
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

  private Metadata compileSpecialForm(Cons cons) {
    Metadata meta = new Metadata(Metadata.Type.NIL, "nil");
    Symbol symbol = (Symbol) cons.getCar();
    switch (symbol.getValue()) {
      case "lambda":
        {
          Compiler compiler = new Compiler();
          meta = compiler.compileLambda(cons);
          String lambda = compiler.artifacts.get(compiler.artifacts.size() - 1).getName();
          mv.visitTypeInsn(Opcodes.NEW, lambda);
          mv.visitInsn(DUP);
          mv.visitMethodInsn(INVOKESPECIAL, lambda, "<init>", "()V", false);
          this.artifacts.addAll(compiler.artifacts);
          break;
        }
      case "macro":
        {
          Compiler compiler = new Compiler(true);
          meta = compiler.compileMacro(cons);
          String macro = compiler.artifacts.get(compiler.artifacts.size() - 1).getName();
          mv.visitTypeInsn(Opcodes.NEW, macro);
          mv.visitInsn(DUP);
          mv.visitMethodInsn(INVOKESPECIAL, macro, "<init>", "()V", false);
          this.artifacts.addAll(compiler.artifacts);
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
      case "def":
        compileDefSpecialForm(cons);
        break;
      case "do":
        compileDoSpecialForm(cons);
        break;
    }
    return meta;
  }

  private void compileDoSpecialForm(Cons cons) {
    Cons body = cons.getCdr();
    while (body != null) {
      _compile(body.getCar());
      body = body.getCdr();
    }
  }

  private void compileDefSpecialForm(Cons cons) {
    Symbol symbol = (Symbol) cons.getCdr().getCar();
    quoteDepth++;
    _compile(symbol);
    quoteDepth--;
    _compile(cons.getCdr().getCdr().getCar());
    mv.visitMethodInsn(
        INVOKESTATIC,
        "ie/francis/lisp/Environment",
        "put",
        "(Lie/francis/lisp/type/Symbol;Ljava/lang/Object;)Ljava/lang/Object;",
        false);
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
      Metadata metadata = _compile(value);
      locals.add(symbol.getValue(), metadata);
      mv.visitVarInsn(ASTORE, locals.get(symbol.getValue()).getLocalId());
      // Move to next assignment if there is one
      assignments = assignments.getCdr().getCdr();
    }

    // Compile body of let statement if exists
    Cons letBody = cons.getCdr().getCdr();
    while (letBody != null) {
      _compile(letBody.getCar());
      letBody = letBody.getCdr();
    }

    locals.popScope();
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
        case "do":
          return true;
      }
    }
    return false;
  }

  private boolean isQuoted() {
    return quoteDepth > 0;
  }
}
