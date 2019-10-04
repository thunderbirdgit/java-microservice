package com.openease.common;

import static com.openease.common.Env.Constants.DEV;
import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.PROD;
import static com.openease.common.Env.Constants.STAGE;

/**
 * Environment enum
 *
 * @author Alan Czajkowski
 */
public enum Env {

  local(LOCAL, "d", "Local"),
  dev(DEV, "d", "Development"),
  stage(STAGE, "s", "Stage/Pre-production"),
  prod(PROD, "p", "Production");

  private String name;

  private String symbol;

  private String description;

  Env(String name, String symbol, String description) {
    this.name = name;
    this.symbol = symbol;
    this.description = description;
  }

  public static final class Constants {
    private Constants() {
      // not publicly instantiable
    }

    public static final String NOT = "!";
    public static final String TEST = "test";
    public static final String LOCAL = "local";
    public static final String DEV = "dev";
    public static final String STAGE = "stage";
    public static final String PROD = "prod";
  }

  public String getName() {
    return name;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getDescription() {
    return description;
  }

}
