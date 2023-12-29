/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.repl;

import java.util.HashMap;
import java.util.Map;

public class Environment {

  private final Map<String, Object> environment;

  public Environment() {
    this.environment = new HashMap<>();
  }

  public void put(String key, Object value) {
    this.environment.put(key, value);
  }

  public Object get(String key) {
    return this.environment.get(key);
  }

  public boolean contains(String key) {
    return this.environment.containsKey(key);
  }
}
