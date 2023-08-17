/*
 * (c) 2023 Francis McNamee
 * */

package ie.francis.fsp.lambda;

import java.util.List;

public class SpecialParameterParser {

  private final List<Object> parameters;

  public SpecialParameterParser(List<Object> parameters) {
    this.parameters = parameters;
  }

  public SpecialParameterGroup parse() {

    int restParameter = -1;
    int optionalParameter = -1;
    int keyParameter = -1;

    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i).toString().equals("&rest")) {
        restParameter = i;
      }
      if (parameters.get(i).toString().equals("&optional")) {
        optionalParameter = i;
      }
      if (parameters.get(i).toString().equals("&key")) {
        keyParameter = i;
      }
    }

    return new SpecialParameterGroup(restParameter, optionalParameter, keyParameter);
  }
}
