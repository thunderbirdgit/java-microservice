package com.openease.common.manager.info;

import com.openease.common.config.Config;
import com.openease.common.manager.info.request.InfoStatusRequest;
import com.openease.common.manager.info.response.InfoStatusResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.openease.common.manager.info.response.InfoStatusResponse.USER_LOCALE;

/**
 * Info manager
 *
 * @author Alan Czajkowski
 */
@Service
public class InfoManager {

  private static final transient Logger LOG = LogManager.getLogger(InfoManager.class);

  public static final String INFO = "info";

  @Autowired
  private Config config;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  public InfoStatusResponse status(InfoStatusRequest request) {
    LOG.debug("request: {}", request);

    InfoStatusResponse infoStatusResponse = new InfoStatusResponse()
        .setName(config.getName())
        .setVersion(config.getVersion())
        .setEnvironment(config.getEnv().toString())
        .setBaseApiUrl(config.getUrlWithCurrentApiVersion());
    infoStatusResponse.getUser().put(USER_LOCALE, request.getLocale());

    return infoStatusResponse;
  }

}
