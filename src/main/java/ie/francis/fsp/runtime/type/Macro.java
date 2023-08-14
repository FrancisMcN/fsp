/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.Type.MACRO;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.visitor.AcceptorImpl;
import ie.francis.fsp.visitor.Visitor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Macro extends Function {

  public Macro(String name, String descriptor, List<String> params) {
    super(name, descriptor, params);
  }

  @Override
  public Type type() {
    return MACRO;
  }

  @Override
  public String toString() {
    return String.format("#%s", name);
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public Object expand(Cons cons, Environment environment, Visitor visitor) {
    String owner = name().split("\\.")[0];
    List<Object> params = new ArrayList<>();
    while (cons != null) {
      params.add(cons.getCar());
      cons = cons.getCdr();
    }
    Class<?>[] paramTypes = new Class[params.size()];
    Arrays.fill(paramTypes, Object.class);
    try {
      System.out.println(params);
      Object output =
          environment.loadClass(owner).getMethod("run", paramTypes).invoke(null, params.toArray());
      new AcceptorImpl().accept(output, visitor);
      return output;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
