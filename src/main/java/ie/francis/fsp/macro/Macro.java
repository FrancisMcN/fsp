/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.macro;

import ie.francis.fsp.ast.ListNode;
import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.scanner.Scanner;
import java.util.List;

public class Macro {

  private final String name;
  private final List<String> args;

  private final ListNode body;

  public Macro(String name, List<String> args, ListNode body) {
    this.name = name;
    this.args = args;
    this.body = body;
  }

  public String getName() {
    return name;
  }

  public List<String> getArgs() {
    return args;
  }

  public ListNode getBody() {
    return body;
  }

  public ListNode duplicate() {
    Scanner scanner = new Scanner(body.toString());
    Parser parser = new Parser(scanner);
    return (ListNode) parser.list();
  }
}
