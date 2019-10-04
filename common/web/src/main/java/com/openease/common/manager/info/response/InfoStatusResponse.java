package com.openease.common.manager.info.response;

import com.openease.common.manager.base.model.BaseManagerModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Info: status response
 *
 * @author Alan Czajkowski
 */
public class InfoStatusResponse extends BaseManagerModel {

  public static final String USER_LOCALE = "locale";

  private String name;

  private String version;

  private String environment;

  private String baseApiUrl;

  private Map<String, Object> user;

  public InfoStatusResponse() {
    super();
    this.user = new LinkedHashMap<>();
  }

  public String getName() {
    return name;
  }

  public InfoStatusResponse setName(String name) {
    this.name = name;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public InfoStatusResponse setVersion(String version) {
    this.version = version;
    return this;
  }

  public String getEnvironment() {
    return environment;
  }

  public InfoStatusResponse setEnvironment(String environment) {
    this.environment = environment;
    return this;
  }

  public String getBaseApiUrl() {
    return baseApiUrl;
  }

  public InfoStatusResponse setBaseApiUrl(String baseApiUrl) {
    this.baseApiUrl = baseApiUrl;
    return this;
  }

  public Map<String, Object> getUser() {
    return user;
  }

  public InfoStatusResponse setUser(Map<String, Object> user) {
    this.user = user;
    return this;
  }

}
