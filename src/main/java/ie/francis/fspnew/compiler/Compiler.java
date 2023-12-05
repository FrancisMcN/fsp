/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.compiler;

import ie.francis.fspnew.analyser.Analyser;
import ie.francis.fspnew.analyser.SymbolTable;
import ie.francis.fspnew.node.Node;
import ie.francis.fspnew.parser.Parser;
import ie.francis.fspnew.scanner.Scanner;
import java.util.List;

public class Compiler {

  private final Generator generator;

  public Compiler(Generator generator) {
    this.generator = generator;
  }

  public void compile(String input) {

    Scanner scanner = new Scanner(input);
    Parser parser = new Parser(scanner);
    Analyser analyser = new Analyser(new SymbolTable());
    List<Node> nodes = parser.parse();
    for (Node node : nodes) {
      System.out.println(node);
    }
    for (Node node : nodes) {
      analyser.analyse(node);
    }
    System.out.println(analyser.getSymbolTable());
  }
}
