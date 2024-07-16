/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.repl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Environment {

  private final Stack<Map<String, Integer>> locals;
  private final Stack<Map<String, Object>> environments;

  public Environment() {
    this.environments = new Stack<>();
    this.environments.push(new HashMap<>());
    this.locals = new Stack<>();
    this.locals.push(new HashMap<>());
  }

  public void addFrame() {
    this.environments.push(new HashMap<>());
    this.locals.push(new HashMap<>());
  }

  public void dropFrame() {
    this.environments.pop();
    this.locals.pop();
  }

  public void putLocal(String key, Integer pos) {
    this.locals.peek().put(key, pos);
  }

  public Integer getLocal(String key) {
    for (Map<String, Integer> localEnvironment : this.locals) {
      if (localEnvironment.containsKey(key)) {
        return localEnvironment.get(key);
      }
    }
    return null;
  }

  public void put(String key, Object value) {
    this.environments.peek().put(key, value);
  }

  public Object get(String key) {
    for (Map<String, Object> environment : this.environments) {
      if (environment.containsKey(key)) {
        return environment.get(key);
      }
    }
    return null;
  }

  public boolean contains(String key) {
    for (Map<String, Object> environment : this.environments) {
      if (environment.containsKey(key)) {
        return true;
      }
    }
    return false;
  }
}
