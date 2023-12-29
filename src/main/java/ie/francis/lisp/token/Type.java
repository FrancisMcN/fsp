/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.token;

public enum Type {
  LPAREN,
  RPAREN,
  HASH,
  QUOTE,
  RMACRO,
  DOT,
  SYMBOL,
  STRING,
  NUMBER,
  BOOLEAN,
  EOF
}
