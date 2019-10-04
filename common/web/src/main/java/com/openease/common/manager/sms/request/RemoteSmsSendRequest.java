package com.openease.common.manager.sms.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.manager.template.TemplateManager;

import java.util.Locale;
import java.util.Map;

/**
 * Remote SMS: Send request
 *
 * @author Alan Czajkowski
 */
public class RemoteSmsSendRequest extends BaseManagerModel {

//TODO:  @NotBlank(message = "{" + VALIDATION_REMOTESMSSENDREQUEST_RECIPIENTPHONENUMBER_NOTBLANK + "}")
  private String recipientPhoneNumber;

//TODO:  @NotNull(message = "{" + VALIDATION_REMOTESMSSENDREQUEST_TEMPLATE_NOTNULL + "}")
  private TemplateManager.Template template;

  private Map<String, Object> templateModel;

  @JsonProperty
  private Locale locale;

  public RemoteSmsSendRequest() {
    this.recipientPhoneNumber = null;
    this.locale = DEFAULT_LOCALE;
  }

  public String getRecipientPhoneNumber() {
    return recipientPhoneNumber;
  }

  public RemoteSmsSendRequest setRecipientPhoneNumber(String recipientPhoneNumber) {
    this.recipientPhoneNumber = recipientPhoneNumber;
    return this;
  }

  public TemplateManager.Template getTemplate() {
    return template;
  }

  public RemoteSmsSendRequest setTemplate(TemplateManager.Template template) {
    this.template = template;
    return this;
  }

  public Map<String, Object> getTemplateModel() {
    return templateModel;
  }

  public RemoteSmsSendRequest setTemplateModel(Map<String, Object> templateModel) {
    this.templateModel = templateModel;
    return this;
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
  public RemoteSmsSendRequest setLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  public RemoteSmsSendRequest setLocale(String locale) {
    this.locale = Locale.forLanguageTag(locale);
    return this;
  }

}
