/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.environment.Environment;
import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.runtime.type.Function;
import ie.francis.fsp.runtime.type.Macro;
import ie.francis.fsp.runtime.type.Symbol;
import ie.francis.fsp.visitor.ClassGeneratorVisitor;
import java.util.ArrayList;
import java.util.List;

public class Macroexpand extends Function {

  public Macroexpand() {
    super(
        String.format("%s.run", Macroexpand.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        new ArrayList<>(List.of("macro")));
  }

  public static Object run(Object value) {
    Cons cons = (Cons) value;
    Environment environment = Environment.singleton();
    return ((Macro) (environment.get((Symbol) ((Cons) value).getCar())))
        .expand(
            cons.getCdr(),
            environment,
            new ClassGeneratorVisitor(
                "Test" + ((Symbol) ((Cons) value).getCar()).name(),
                new ArrayList<>(),
                environment));
  }
}
