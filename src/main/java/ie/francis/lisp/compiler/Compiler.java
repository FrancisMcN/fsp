/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.lisp.exception.InvalidConsException;
import ie.francis.lisp.function.Lambda;
import ie.francis.lisp.type.Cons;
import ie.francis.lisp.type.Symbol;
import java.util.Random;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Compiler {

  private ClassWriter cw;
  private MethodVisitor mv;

  private int quoteDepth;

  private final String className;

  public Compiler() {
    quoteDepth = 0;
    className = String.format("Lambda%d", new Random().nextInt(1000000000));
  }

  public Artifact compile(Object object) {

    // Pre class-generation
    start();

    _compile(object);

    // Post class-generation
    finish();
    return new Artifact(className, cw.toByteArray());
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

    mv = cw.visitMethod(ACC_PUBLIC, "call", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
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
    } else {
      mv.visitLdcInsn("demo symbol since symbol resolution doesn't work");
    }
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
    } else if (first instanceof Lambda) {
      compileLambda(cons);
      compileLambdaCall(cons.getCdr());
      return;
    }
    throw new InvalidConsException("expected a function in first cons cell");
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
        compileLambdaSpecialForm(cons);
        break;
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

  private void compileLambdaSpecialForm(Cons cons) {}

  private void compileLambdaCall(Cons cdr) {}

  private void compileLambda(Cons cons) {}

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
