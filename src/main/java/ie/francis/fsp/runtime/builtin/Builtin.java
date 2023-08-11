/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.runtime.builtin;

//import ie.francis.fsp.parser.Parser;
import ie.francis.fsp.runtime.helper.ConsBuilder;
import ie.francis.fsp.runtime.type.Cons;
import ie.francis.fsp.scanner.Scanner;

import java.util.Arrays;

public class Builtin {

  public Builtin() {}

  public static Object println(Object value) {
    System.out.println(value);
    return null;
  }

//  public static Object read(Object value) {
//    Scanner scanner = new Scanner(value.toString());
//    Parser parser = new Parser(scanner);
//    return parser.parse();
//  }

  public static Object car(Object value) {
    return ((Cons) value).getCar();
  }

  public static Object cdr(Object value) {
    return ((Cons) value).getCdr();
  }

  public static Object list(Object... values) {
    ConsBuilder consBuilder = new ConsBuilder();
    for (Object val : values) {
      consBuilder.add(val);
    }
    return consBuilder.getCons();
  }

  public static Object concat(Object... values) {
    StringBuilder sb = new StringBuilder();
    for (Object val : values) {
      sb.append(val);
    }
    return sb.toString();
  }

  public static Object lessThan(Object a, Object b) {
    return ((Integer) a).compareTo((Integer) b) < 0;
  }

  public static Object greaterThan(Object a, Object b) {
    return ((Integer) a).compareTo((Integer) b) > 0;
  }

  public static Object equals(Object a, Object b) {
    if (a instanceof Integer) {
      return ((Integer) a).compareTo((Integer) b) == 0;
    }
    return a.equals(b);
  }

  public static Object plus(Object... nums) {
    Integer sum = 0;
    for (Object o : nums) {
      sum += ((Integer) o);
    }
    return sum;
  }

  public static Object minus(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum -= ((Integer) nums[i]);
    }
    return sum;
  }

  public static Object multiply(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum *= ((Integer) nums[i]);
    }
    return sum;
  }

  public static Object divide(Object... nums) {
    Integer sum = (Integer) nums[0];
    for (int i = 1; i < nums.length; i++) {
      sum /= ((Integer) nums[i]);
    }
    return sum;
  }
}
