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

  public ClassGeneratorVisitor(String className) {
    symbolTable = new SymbolTable();
    symbolTable.put(
        "+",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.plus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "-",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.minus",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "*",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.multiply",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "/",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.divide",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "concat",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.concat",
            "([Ljava/lang/Object;)Ljava/lang/Object;"));
    symbolTable.put(
        "println",
        new FunctionSymbol(
            "ie/francis/fsp/runtime/builtin/Builtin.println", "(Ljava/lang/Object;)V"));

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(
        V1_5,
        Opcodes.ACC_PUBLIC + ACC_MODULE,
        String.format("ie/francis/%s", className),
        null,
        "java/lang/Object",
        new String[] {});

    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "run", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    mv.visitInsn(ACONST_NULL);
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
    // Create a new array of Objects
    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
    mv.visitInsn(DUP);

    for (int i = 0; i < nodes.size(); i++) {

      mv.visitLdcInsn(i);
      nodes.get(i).accept(this);
      mv.visitInsn(AASTORE);

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
    mv.visitMethodInsn(
        INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
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
    //    mv.visitMethodInsn(
    //        INVOKESTATIC,
    //        "ie/francis/fsp/runtime/builtin/Builtin",
    //        "println",
    //        "(Ljava/lang/Object;)V",
    //        false);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    cw.visitEnd();
    return cw.toByteArray();
  }
}
