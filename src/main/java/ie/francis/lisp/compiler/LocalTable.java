/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LocalTable {

  private final Stack<Map<String, Integer>> locals;

  public LocalTable() {
    this.locals = new Stack<>();
    pushScope();
  }

  public void pushScope() {
    this.locals.push(new HashMap<>());
  }

  public void popScope() {
    this.locals.pop();
  }

  public void add(String local) {
    int frameSize = this.locals.peek().size();
    this.locals.peek().put(local, this.locals.peek().getOrDefault(local, frameSize));
  }

  public Integer get(String local) {
    for (Map<String, Integer> frame : locals) {
      if (frame.containsKey(local)) {
        return frame.get(local);
      }
    }
    return -1;
  }

  public boolean contains(String local) {
    return get(local) != -1;
  }

  public int size() {
    int sum = 0;
    for (Map<String, Integer> frame : locals) {
      sum += frame.size();
    }
    return sum;
  }
}
