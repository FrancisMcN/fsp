/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.lambda;

public class SpecialParameterGroup {

  private final int restParameter;
  private final int optionalParameter;
  private final int keyParameter;

  public SpecialParameterGroup(int restParameter, int optionalParameter, int keyParameter) {
    this.restParameter = restParameter;
    this.optionalParameter = optionalParameter;
    this.keyParameter = keyParameter;
  }

  public int getRestParameter() {
    return restParameter;
  }

  public int getOptionalParameter() {
    return optionalParameter;
  }

  public int getKeyParameter() {
    return keyParameter;
  }
}
