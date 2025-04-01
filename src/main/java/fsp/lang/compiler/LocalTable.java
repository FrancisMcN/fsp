/*
 * (c) 2025 Francis McNamee
 * */
 
package fsp.lang.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LocalTable {

  public static class Local {
    private final Integer localId;
    private final Object local;

    public Local(Integer localId, Object local) {
      this.localId = localId;
      this.local = local;
    }

    public Integer getLocalId() {
      return localId;
    }

    public Object getLocal() {
      return local;
    }
  }

  private final Stack<Map<String, Local>> locals;

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

  public void add(String local, Object value) {
    if (!this.locals.peek().containsKey(local)) {
      int frameSize = size();
      this.locals.peek().put(local, new Local(frameSize, value));
    } else {
      Local existingLocal = this.locals.peek().get(local);
      this.locals.peek().put(local, new Local(existingLocal.localId, value));
    }
  }

  public Local get(String local) {
    for (Map<String, Local> frame : locals) {
      if (frame.containsKey(local)) {
        return frame.get(local);
      }
    }
    return null;
  }

  public boolean contains(String local) {
    return get(local) != null;
  }

  public int size() {
    int sum = 0;
    for (Map<String, Local> frame : locals) {
      sum += frame.size();
    }
    return sum;
  }
}
