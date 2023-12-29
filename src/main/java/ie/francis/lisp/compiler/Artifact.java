/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.compiler;

public class Artifact {

  private final String name;
  private final byte[] data;

  public Artifact(String name, byte[] data) {
    this.name = name;
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public byte[] getData() {
    return data;
  }

  @Override
  public String toString() {
    return String.format("(artifact %s)", this.name);
  }
}
