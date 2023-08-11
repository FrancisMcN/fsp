///*
// * (c) 2023 Francis McNamee
// * */
//
//package ie.francis.fsp.visitor;
//
//import static ie.francis.fsp.ast.NodeType.LIST_NODE;
//import static ie.francis.fsp.ast.NodeType.SYMBOL_NODE;
//
//import ie.francis.fsp.ast.*;
//import ie.francis.fsp.macro.Macro;
//import java.util.*;
//
//public class MacroExpanderVisitor implements Visitor {
//
//  private final Map<String, Macro> macros;
//  private Stack<String> currentSymbol;
//  private Stack<Node> expansion;
//  private Stack<ListNode> parent;
//
//  private int quoteDepth = 0;
//
//  public MacroExpanderVisitor() {
//    macros = new HashMap<>();
//    expansion = new Stack<>();
//    parent = new Stack<>();
//    currentSymbol = new Stack<>();
//    ListNode testMacroBody = new ListNode();
//    testMacroBody.addNode(new SymbolNode("+"));
//    testMacroBody.addNode(new SymbolNode("a"));
//    testMacroBody.addNode(new SymbolNode("b"));
//
//    List<String> args = new ArrayList<>();
//    args.add("a");
//    args.add("b");
//
//    Macro testMacro = new Macro("test-macro", args, testMacroBody);
//    macros.put(testMacro.getName(), testMacro);
//  }
//
//  @Override
//  public void visit(ProgramNode programNode) {
//    for (Node node : programNode.getNodes()) {
//      node.accept(this);
//    }
//  }
//
//  @Override
//  public void visit(SxprNode sxprNode) {
//    sxprNode.getCar().accept(this);
//    sxprNode.getCdr().accept(this);
//  }
//
//  @Override
//  public void visit(ListNode listNode) {
//    List<Node> nodes = listNode.getNodes();
//
//    Node first = nodes.get(0);
//    if (isQuote(first)) {
//      return;
//    }
//    if (isMacroSpecialForm(first)) {
//    } else if (isMacro(first)) {
//      parent.push(listNode);
//      expandMacro(listNode);
//      parent.pop();
//    } else {
//      for (Node node : nodes) {
//        node.accept(this);
//      }
//    }
//  }
//
//  private boolean isQuote(Node first) {
//    String value = first.value();
//    if (first.type() == SYMBOL_NODE) {
//      return value.equals("quote");
//    }
//    return false;
//  }
//
//  private void expandMacro(ListNode listNode) {
//    List<Node> nodes = listNode.getNodes();
//    Node macroName = nodes.get(0);
//    if (macroName.type() == SYMBOL_NODE && macros.containsKey(macroName.value())) {
//      Macro macro = macros.get(macroName.value());
//      listNode.setNodes(macro.duplicate().getNodes());
//      for (int i = 0; i < macro.getArgs().size(); i++) {
//        currentSymbol.push(macro.getArgs().get(i));
//        Node currentExpansion = nodes.get(i + 1);
//        if (currentExpansion.type() == LIST_NODE) {
//          ListNode ex = ((ListNode) currentExpansion);
//          if (ex.getNodes().get(0).value().equals("quote")) {
//            currentExpansion = ex.getNodes().get(1);
//          }
//        }
//
//        expansion.push(currentExpansion);
//        for (Node n : listNode.getNodes()) {
//          n.accept(this);
//        }
//        for (Node n : listNode.getNodes()) {
//          n.accept(this);
//        }
//        currentSymbol.pop();
//        expansion.pop();
//      }
//    }
//  }
//
//  private boolean isMacro(Node first) {
//    return first.type() == SYMBOL_NODE && first.value().equals("test-macro");
//  }
//
//  private boolean isMacroSpecialForm(Node first) {
//    String value = first.value();
//    if (first.type() == SYMBOL_NODE) {
//      switch (value) {
//        case "defmacro":
//        case "expandmacro":
//        case "quote":
//          return true;
//      }
//    }
//    return false;
//  }
//
//  @Override
//  public void visit(NumberNode numberNode) {}
//
//  @Override
//  public void visit(StringNode stringNode) {}
//
//  @Override
//  public void visit(SymbolNode symbolNode) {
//    if (!currentSymbol.isEmpty()) {
//      if (symbolNode.value().equals(currentSymbol.peek())) {
//        List<Node> parentNodes = parent.peek().getNodes();
//        int idx = parentNodes.indexOf(symbolNode);
//        parentNodes.set(idx, expansion.peek());
//      }
//    }
//  }
//
//  @Override
//  public void visit(BooleanNode booleanNode) {}
//}
