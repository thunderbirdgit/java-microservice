package com.openease.common.web.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Asset utilities
 *
 * @author Alan Czajkowski
 */
public final class AssetUtils {

  private static final transient Logger LOG = LogManager.getLogger(AssetUtils.class);

  public static final String STATIC_ASSET_CLASSPATH = "/static";
  public static final String FRONTEND_ASSET_CLASSPATH = "/public";

  private AssetUtils() {
    // not publicly instantiable
  }

}
