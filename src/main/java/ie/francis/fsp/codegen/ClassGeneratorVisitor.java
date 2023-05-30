/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.codegen;

import static ie.francis.fsp.ast.NodeType.SYMBOL_NODE;
import static org.objectweb.asm.Opcodes.ACC_MODULE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.IASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_5;

import ie.francis.fsp.ast.ListNode;
import ie.francis.fsp.ast.Node;
import ie.francis.fsp.ast.NumberNode;
import ie.francis.fsp.ast.StringNode;
import ie.francis.fsp.ast.SxprNode;
import ie.francis.fsp.ast.SymbolNode;
import ie.francis.fsp.ast.Visitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassGeneratorVisitor implements Visitor {

  private Map<String, String> symbolTable;
  private final ClassWriter cw;
  private MethodVisitor mv;

  public ClassGeneratorVisitor(String className) {
    symbolTable = new HashMap<>();
    symbolTable.put("+", "plus");
    symbolTable.put("-", "minus");
    symbolTable.put("*", "multiply");
    symbolTable.put("/", "divide");

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
    if (first.type() == SYMBOL_NODE && symbolTable.containsKey(first.value())) {
      mv.visitLdcInsn(nodes.size());
      mv.visitIntInsn(NEWARRAY, Opcodes.T_INT);
      mv.visitInsn(DUP);
    }
    for (int i = 0; i < nodes.size(); i++) {
      mv.visitLdcInsn(i);
      nodes.get(i).accept(this);
      mv.visitInsn(IASTORE);
      mv.visitInsn(DUP);
    }
    if (first.type() == SYMBOL_NODE && symbolTable.containsKey(first.value())) {
      mv.visitMethodInsn(
          INVOKESTATIC,
          "ie/francis/fsp/runtime/builtin/Builtin",
          symbolTable.get(first.value()),
          "([I)I");
      mv.visitInsn(POP);
      mv.visitInsn(RETURN);
      mv.visitMaxs(0, 0);
      mv.visitEnd();
    }
  }

  @Override
  public void visit(NumberNode numberNode) {
    mv.visitLdcInsn(numberNode.getIntValue());
  }

  @Override
  public void visit(StringNode stringNode) {
    System.out.println(stringNode);
  }

  @Override
  public void visit(SymbolNode symbolNode) {
    System.out.println(symbolNode);
  }

  public byte[] generate() {
    cw.visitEnd();
    return cw.toByteArray();
  }
}
