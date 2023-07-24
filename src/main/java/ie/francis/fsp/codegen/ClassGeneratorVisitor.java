/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.codegen;

import static ie.francis.fsp.ast.NodeType.*;
import static org.objectweb.asm.Opcodes.*;

import ie.francis.fsp.ast.*;
import ie.francis.fsp.sym.FunctionSymbol;
import ie.francis.fsp.sym.Symbol;
import ie.francis.fsp.sym.SymbolTable;
import ie.francis.fsp.sym.SymbolType;
import java.util.List;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassGeneratorVisitor implements Visitor {

  private final SymbolTable symbolTable;
  private final ClassWriter cw;
  private final MethodVisitor mv;

  private boolean isString;

  public ClassGeneratorVisitor(String className) {
    symbolTable = new SymbolTable();
    symbolTable.put(
        "+", new FunctionSymbol("ie/francis/fsp/runtime/builtin/Builtin.plus", "([I)I"));
    symbolTable.put(
        "-", new FunctionSymbol("ie/francis/fsp/runtime/builtin/Builtin.minus", "([I)I"));
    symbolTable.put(
        "*", new FunctionSymbol("ie/francis/fsp/runtime/builtin/Builtin.multiply", "([I)I"));
    symbolTable.put(
        "/", new FunctionSymbol("ie/francis/fsp/runtime/builtin/Builtin.divide", "([I)I"));
    symbolTable.put(
        "concat",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.concat",
            "([Ljava/lang/String;)Ljava/lang/String;"));
    symbolTable.put(
        "sprint",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.print", "(Ljava/lang/String;)V"));

    symbolTable.put(
        "iprint", new FunctionSymbol("ie/francis/fsp/runtime/builtin/Builtin.print", "(I)V"));

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        Opcodes.ACC_PUBLIC + ACC_MODULE,
        String.format("ie/francis/%s", className),
        null,
        "java/lang/Object",
        new String[] {});

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "()V", null, null);
    mv.visitCode();
    isString = false;
    //        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
  }

  @Override
  public void visit(SxprNode sxprNode) {
    sxprNode.getCar().accept(this);
    sxprNode.getCdr().accept(this);
  }

  @Override
  public void visit(ListNode listNode) {
    List<Node> nodes = listNode.getNodes();
    Node first = nodes.remove(0);

    if (isFunction(first)) {
      Symbol symbol = symbolTable.get(first.value());
      if (isVariadic(symbol)) {
        compileArrayOfArguments(nodes);
      } else {
        compileArguments(nodes);
      }
      compileFunctionCall(symbol);
    }
  }

  private void compileFunctionCall(Symbol symbol) {
    String owner = symbol.name().split("\\.")[0];
    String functionName = symbol.name().split("\\.")[1];
    mv.visitMethodInsn(INVOKESTATIC, owner, functionName, symbol.descriptor(), false);
  }

  private void compileArguments(List<Node> nodes) {
    for (Node node : nodes) {
      node.accept(this);
    }
  }

  private void compileArrayOfArguments(List<Node> nodes) {
    // Specify array size first
    mv.visitLdcInsn(nodes.size());
    // Specify array type based on type of first argument
    Node firstArgument = nodes.get(0);
    switch (firstArgument.type()) {
      case NUMBER_NODE:
        mv.visitIntInsn(NEWARRAY, Opcodes.T_INT);
        break;
      case STRING_NODE:
        mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
        break;
      case SYMBOL_NODE:
      case LIST_NODE:
      case SXPR_NODE:
    }
    mv.visitInsn(DUP);

    for (int i = 0; i < nodes.size(); i++) {

      mv.visitLdcInsn(i);
      nodes.get(i).accept(this);

      switch (nodes.get(i).type()) {
        case NUMBER_NODE:
          // Store int in array of arguments
          mv.visitInsn(IASTORE);
          break;
        case STRING_NODE:
        case SYMBOL_NODE:
        case LIST_NODE:
          // Store reference in array of arguments
          mv.visitInsn(AASTORE);
          break;
        case SXPR_NODE:
          break;
      }
      if (i < nodes.size() - 1) {
        mv.visitInsn(DUP);
      }
    }
  }

  private boolean isFunction(Node first) {
    String value = first.value();
    if (first.type() == SYMBOL_NODE) {
      if (symbolTable.contains(value)) {
        Symbol symbol = symbolTable.get(value);
        return symbol.type() == SymbolType.FUNCTION;
      }
    }
    return false;
  }

  private boolean isVariadic(Symbol function) {
    return function.descriptor().startsWith("([");
  }

  @Override
  public void visit(NumberNode numberNode) {
    mv.visitLdcInsn(numberNode.getIntValue());
  }

  @Override
  public void visit(StringNode stringNode) {
    mv.visitLdcInsn(stringNode.value());
  }

  @Override
  public void visit(SymbolNode symbolNode) {
    System.out.println(symbolNode);
  }

  public byte[] generate() {
    //    String descriptor = "(I)V";
    //    if (isString) {
    //      descriptor = "(Ljava/lang/String;)V";
    //    }
    //    mv.visitMethodInsn(
    //        INVOKESTATIC, "ie/francis/fsp/runtime/builtin/Builtin", "println", descriptor, false);
    //    //        mv.visitInsn(POP);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
    return cw.toByteArray();
  }
}
