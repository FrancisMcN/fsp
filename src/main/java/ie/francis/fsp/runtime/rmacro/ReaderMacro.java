/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.rmacro;

public interface ReaderMacro {

  Object expand(Object object);

  String character();
}
