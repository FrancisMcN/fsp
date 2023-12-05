/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fspnew.node;

public enum NodeType {
  SYMBOL_NODE,
  STRING_NODE,
  INTEGER_NODE,
  FLOAT_NODE,
  BOOLEAN_NODE,
  IF_NODE,
  QUOTE_NODE,
  LAMBDA_NODE,
  LIST_NODE,

  LET_NODE
}
