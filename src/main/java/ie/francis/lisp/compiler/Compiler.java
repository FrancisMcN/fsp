/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.lisp.exception.InvalidConsException;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Symbol;
import java.util.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Compiler {

  private ClassWriter cw;
  private MethodVisitor mv;

  private int quoteDepth;

  private final String className;
  private final List<Artifact> artifacts;
  //  private final Stack<Map<String, Integer>> locals;

  private final LocalTable locals;

  private int stackSize;

  public Compiler() {
    quoteDepth = 0;
    stackSize = 0;
    className = String.format("Lambda%d", new Random().nextInt(1000000000));
    artifacts = new ArrayList<>();
    locals = new LocalTable();
    locals.add("this", null);
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

  public Metadata compileLambda(Object object) {

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

    return new Metadata(Metadata.Type.LAMBDA, className);
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
    if (stackSize == 0) {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
  }

  private void start() {
    cw = new ClassWriter(COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        ACC_PUBLIC + ACC_MODULE,
        className,
        null,
        "java/lang/Object",
        new String[] {"ie/francis/lisp/type/Lambda"});

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
    stackSize++;
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
    return new Metadata(Metadata.Type.INTEGER, integer.toString());
  }

  private Metadata compileNumber(Float floating) {
    mv.visitLdcInsn(floating);
    stackSize++;
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
    return new Metadata(Metadata.Type.FLOAT, floating.toString());
  }

  private Metadata compileBoolean(Boolean bool) {
    mv.visitLdcInsn(bool);
    stackSize++;
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
    return new Metadata(Metadata.Type.BOOLEAN, bool.toString());
  }

  private Metadata compileString(String string) {
    mv.visitLdcInsn(string);
    stackSize++;
    return new Metadata(Metadata.Type.STRING, string);
  }

  private Metadata compileSymbol(Symbol symbol) {
    Metadata meta = new Metadata(Metadata.Type.SYMBOL, symbol.getValue());
    stackSize++;
    if (isQuoted()) {
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

    mv.visitLdcInsn("demo symbol since symbol resolution doesn't work");
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
      Object car = ((Cons) first).getCar();
      if (car instanceof Symbol && ((Symbol) car).getValue().equals("lambda")) {
        compileSpecialForm((Cons) first);
        String lambda = artifacts.get(artifacts.size() - 1).getName();
        compileLambdaCall(lambda, cons.getCdr());
        return meta;
      }
    }
    if (first instanceof Symbol) {
      Symbol symbol = (Symbol) first;
      // Lookup local vars for lambda
      LocalTable.Local local = locals.get(symbol.getValue());
      Metadata localMetadata = local.getMetadata();
      if (localMetadata.getType() == Metadata.Type.LAMBDA) {
        String lambdaName = localMetadata.getValue();
        mv.visitVarInsn(ALOAD, local.getLocalId());
        compileLambdaCall(lambdaName, cons.getCdr());
        return localMetadata;
      }
    }
    throw new InvalidConsException("expected a function in first cons cell");
  }

  private void compileLambdaCall(String lambda, Cons cons) {
    String descriptor = "(" + "Ljava/lang/Object;".repeat(cons.size()) + ")Ljava/lang/Object;";
    stackSize += cons.size();
    while (cons != null) {
      _compile(cons.getCar());
      cons = cons.getCdr();
    }
    stackSize--;
    mv.visitMethodInsn(INVOKEVIRTUAL, lambda, "call", descriptor, false);
    stackSize++;
  }

  private void compileConsWithoutEvaluating(Cons cons) {
    Cons head = cons;
    while (cons != null) {

      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Cons");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "ie/francis/lisp/type/Cons", "<init>", "()V", false);
      _compile(cons.getCar());
      stackSize--;
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/lisp/type/Cons",
          "setCar",
          "(Ljava/lang/Object;)Lie/francis/lisp/type/Cons;",
          false);
      stackSize++;
      cons = cons.getCdr();
    }

    cons = head.getCdr();
    while (cons != null) {
      stackSize--;
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "ie/francis/lisp/type/Cons",
          "setCdr",
          "(Lie/francis/lisp/type/Cons;)Lie/francis/lisp/type/Cons;",
          false);
      stackSize++;
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
          stackSize++;
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
    }
    return meta;
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
      stackSize--;
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

  private void compileIfSpecialForm(Cons cons) {}

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
        case "quote":
        case "if":
        case "let":
          return true;
      }
    }
    return false;
  }

  private boolean isQuoted() {
    return quoteDepth > 0;
  }
}
