/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

import ie.francis.fsp.runtime.type.Function;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Load extends Function {

  public Load(String name, String descriptor) {
    super(name, descriptor);
  }

  public Load() {
    super(
        String.format("%s.run", Load.class.getCanonicalName().replace(".", "/")),
        "(Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public static Object run(Object value) throws IOException {
    String data = Files.readString(Path.of(value.toString()));
    return Read.run(data);
  }
}
