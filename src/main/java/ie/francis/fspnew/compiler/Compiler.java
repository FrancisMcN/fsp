/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.compiler;

import ie.francis.fspnew.analyser.Analyser;
import ie.francis.fspnew.analyser.SymbolTable;
import ie.francis.fspnew.node.Node;
import ie.francis.fspnew.parser.Parser;
import ie.francis.fspnew.scanner.Scanner;
import ie.francis.fspnew.visitor.JavaClassGeneratorVisitor;
import java.util.List;

public class Compiler {

  //  private final Generator generator;
  private final JavaClassGeneratorVisitor visitor;

  public Compiler(JavaClassGeneratorVisitor visitor) {
    this.visitor = visitor;
  }
  //  public Compiler(Generator generator) {
  //    this.generator = generator;
  //  }

  public List<Artifact> compile(String input) {

    List<Node> nodes = compileToAst(input);
    return compileAst(nodes);
  }

  public List<Node> compileToAst(String input) {
    Scanner scanner = new Scanner(input);
    Parser parser = new Parser(scanner);
    return parser.parse();
  }

  public List<Artifact> compileAst(Node node) {
    Analyser analyser = new Analyser(new SymbolTable());
    analyser.analyse(node);
    node.accept(visitor);
    return visitor.getArtifacts();
  }

  public List<Artifact> compileAst(List<Node> nodes) {
    Analyser analyser = new Analyser(new SymbolTable());
    for (Node node : nodes) {
      analyser.analyse(node);
    }
    for (Node node : nodes) {
      node.accept(visitor);
    }
    return visitor.getArtifacts();
  }
}
