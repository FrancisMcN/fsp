/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.type;

public interface Type {
  DataType type();

  String name();

  String descriptor();

  String toString();
}
