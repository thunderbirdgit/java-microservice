package com.openease.common.manager.info.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.openease.common.manager.base.model.BaseManagerModel;

import java.util.Locale;

/**
 * Info: Status request
 *
 * @author Alan Czajkowski
 */
public class InfoStatusRequest extends BaseManagerModel {

  @JsonProperty
  private Locale locale;

  public InfoStatusRequest() {
    this(DEFAULT_LOCALE);
  }

  public InfoStatusRequest(Locale locale) {
    this.locale = locale;
  }

  @JsonIgnore
  public Locale getLocale() {
    return locale;
  }

  @JsonGetter("locale")
  public String getLocaleString() {
    return locale != null
        ? locale.toLanguageTag()
        : DEFAULT_LOCALE.toLanguageTag();
  }

  @JsonIgnore
  public InfoStatusRequest setLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  public InfoStatusRequest setLocale(String locale) {
    this.locale = Locale.forLanguageTag(locale);
    return this;
  }

}
