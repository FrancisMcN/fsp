/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.environment;

public interface Entry {
  String name();

  String descriptor();

  EntryType type();

  Object value();
}
