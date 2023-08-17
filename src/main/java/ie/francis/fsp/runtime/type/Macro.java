/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

import static ie.francis.fsp.runtime.type.Type.MACRO;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.lambda.SpecialParameterGroup;
import ie.francis.fsp.lambda.SpecialParameterParser;
import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.visitor.Visitor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Macro extends Function {

  public Macro(String name, String descriptor, List<Object> params) {
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

    SpecialParameterParser specialParameterParser = new SpecialParameterParser(getParams());
    SpecialParameterGroup specialLambdaParameters = specialParameterParser.parse();
    List<Object> p = new ArrayList<>();
    int i = 0;

    while (cons != null) {
      if (i == specialLambdaParameters.getRestParameter()) {
        ConsBuilder consBuilder = new ConsBuilder();
        while (cons != null) {
          consBuilder.add(cons.getCar());
          cons = cons.getCdr();
        }
        p.add(consBuilder.getCons());

        break;
      }
      p.add(cons.getCar());
      cons = cons.getCdr();
      i++;
    }
    Class<?>[] paramTypes = new Class[p.size()];
    Arrays.fill(paramTypes, Object.class);
    try {
      return environment.loadClass(owner).getMethod("run", paramTypes).invoke(null, p.toArray());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
