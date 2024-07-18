/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.lisp.function;

import ie.francis.lisp.type.Lambda;

public class Apply extends BaseLambda implements Lambda {
  @Override
  public Object call(Object lambda) {
    return ((Lambda) lambda).call();
  }

  @Override
  public Object call(Object lambda, Object arg) {
    return ((Lambda) lambda).call(arg);
  }

  @Override
  public Object call(Object lambda, Object arg, Object arg2) {
    return ((Lambda) lambda).call(arg, arg2);
  }

  @Override
  public Object call(Object lambda, Object arg, Object arg2, Object arg3) {
    return ((Lambda) lambda).call(arg, arg2, arg3);
  }

  @Override
  public Object call(Object lambda, Object arg, Object arg2, Object arg3, Object arg4) {
    return ((Lambda) lambda).call(arg, arg2, arg3, arg4);
  }

  @Override
  public Object call(Object[] args) {
    Lambda lambda = (Lambda) args[0];
    Object[] lambdaArgs = new Object[args.length - 1];
    for (int i = 1; i < args.length; i++) {
      lambdaArgs[i - 1] = args[i];
    }
    return lambda.call(lambdaArgs);
  }
}
