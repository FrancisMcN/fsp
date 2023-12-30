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
  private final Map<String, Integer> locals;

  public Compiler() {
    quoteDepth = 0;
    className = String.format("Lambda%d", new Random().nextInt(1000000000));
    artifacts = new ArrayList<>();
    locals = new HashMap<>();
    locals.put("this", 0);
  }

  public void compile(Object object) {

    // Pre class-generation
    start();
    // Add code to call() method with arity 0
    mv = cw.visitMethod(ACC_PUBLIC, "call", "()Ljava/lang/Object;", null, null);
    mv.visitCode();

    _compile(object);

    // Post class-generation
    finish();

    this.artifacts.add(new Artifact(className, cw.toByteArray()));
  }

  public void compileLambda(Object object) {

    // Pre class-generation
    start();

    // Record local variables and determine function arity
    Cons cons = ((Cons) (((Cons) object).getCdr().getCar()));
    while (cons != null) {
      locals.put(cons.getCar().toString(), locals.size());
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

    Object body = ((Cons) object).getCdr().getCdr().getCar();

    _compile(body);

    // Post class-generation
    finish();

    this.artifacts.add(new Artifact(className, cw.toByteArray()));
  }

  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  public void _compile(Object object) {

    if (object instanceof Symbol) {
      compileSymbol((Symbol) object);
    } else if (object instanceof String) {
      compileString((String) object);
    } else if (object instanceof Boolean) {
      compileBoolean((Boolean) object);
    } else if (object instanceof Integer) {
      compileNumber((Integer) object);
    } else if (object instanceof Float) {
      compileNumber((Float) object);
    } else if (object instanceof Cons) {
      compileCons((Cons) object);
    } else if (object == null) {
      mv.visitInsn(ACONST_NULL);
    }
  }

  private void finish() {
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

  private void compileNumber(Integer integer) {
    mv.visitLdcInsn(integer);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
  }

  private void compileNumber(Float floating) {
    mv.visitLdcInsn(floating);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
  }

  private void compileBoolean(Boolean bool) {
    mv.visitLdcInsn(bool);
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
  }

  private void compileString(String string) {
    mv.visitLdcInsn(string);
  }

  private void compileSymbol(Symbol symbol) {
    if (isQuoted()) {
      mv.visitTypeInsn(Opcodes.NEW, "ie/francis/lisp/type/Symbol");
      mv.visitInsn(DUP);
      mv.visitLdcInsn(symbol.getValue());
      mv.visitMethodInsn(
          INVOKESPECIAL, "ie/francis/lisp/type/Symbol", "<init>", "(Ljava/lang/String;)V", false);
      return;
    }

    if (locals.containsKey(symbol.getValue())) {
      mv.visitVarInsn(ALOAD, locals.get(symbol.getValue()));
      return;
    }

    mv.visitLdcInsn("demo symbol since symbol resolution doesn't work");
  }

  private void compileCons(Cons cons) {

    if (isQuoted()) {
      compileConsWithoutEvaluating(cons);
      return;
    }

    Object first = cons.getCar();
    if (isSpecialForm(cons)) {
      compileSpecialForm(cons);
      return;
    }

    if (first instanceof Cons) {
      Object car = ((Cons) first).getCar();
      if (car instanceof Symbol && ((Symbol) car).getValue().equals("lambda")) {
        compileSpecialForm((Cons) first);
        String lambda = artifacts.get(artifacts.size() - 1).getName();
        compileLambdaCall(lambda, cons.getCdr());
        return;
      }
    }
    throw new InvalidConsException("expected a function in first cons cell");
  }

  private void compileLambdaCall(String lambda, Cons cons) {

    String descriptor = "(" + "Ljava/lang/Object;".repeat(cons.size()) + ")Ljava/lang/Object;";
    while (cons != null) {
      _compile(cons.getCar());
      cons = cons.getCdr();
    }
    mv.visitMethodInsn(INVOKEVIRTUAL, lambda, "call", descriptor, false);
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

  private void compileSpecialForm(Cons cons) {
    Symbol symbol = (Symbol) cons.getCar();
    switch (symbol.getValue()) {
      case "lambda":
        {
          Compiler compiler = new Compiler();
          compiler.compileLambda(cons);
          String lambda = compiler.artifacts.get(compiler.artifacts.size() - 1).getName();
          mv.visitTypeInsn(Opcodes.NEW, lambda);
          mv.visitInsn(DUP);
          mv.visitMethodInsn(INVOKESPECIAL, lambda, "<init>", "()V", false);
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
    }
  }

  private void compileLetSpecialForm(Cons cons) {}

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
