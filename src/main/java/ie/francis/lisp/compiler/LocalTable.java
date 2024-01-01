/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LocalTable {

  public static class Local {
    private final Integer localId;
    private final Metadata metadata;

    public Local(Integer localId, Metadata meta) {
      this.localId = localId;
      this.metadata = meta;
    }

    public Integer getLocalId() {
      return localId;
    }

    public Metadata getMetadata() {
      return metadata;
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

  public void add(String local, Metadata metadata) {
    if (!this.locals.peek().containsKey(local)) {
      int frameSize = size();
      this.locals.peek().put(local, new Local(frameSize, metadata));
    } else {
      Local existingLocal = this.locals.peek().get(local);
      this.locals.peek().put(local, new Local(existingLocal.localId, metadata));
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
