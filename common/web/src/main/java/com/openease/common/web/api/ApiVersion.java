package com.openease.common.web.api;

import static com.openease.common.web.api.ApiVersion.Constants.V1;
import static com.openease.common.web.api.ApiVersion.Constants.V2;

/**
 * API version enum
 *
 * @author Alan Czajkowski
 */
public enum ApiVersion {

  v1(V1),
  v2(V2);

  ApiVersion(String name) {
    // do nothing
  }

  public static final class Constants {
    private Constants() {
      // not publicly instantiable
    }

    public static final ApiVersion CURRENT_VERSION = v1;
    public static final String CURRENT_VERSION_CONTEXT = "/" + CURRENT_VERSION;

    public static final String V1 = "v1";
    public static final String V1_CONTEXT = "/" + V1;

    public static final String V2 = "v2";
    public static final String V2_CONTEXT = "/" + V2;
  }

}
