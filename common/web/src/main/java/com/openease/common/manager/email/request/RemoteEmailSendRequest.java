package com.openease.common.manager.email.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.openease.common.manager.base.model.BaseManagerModel;
import com.openease.common.manager.template.TemplateManager;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEEMAILSENDREQUEST_TEMPLATE_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEEMAILSENDREQUEST_TORECIPIENTS_NOTEMPTY;

/**
 * Remote email: Send request
 *
 * @author Alan Czajkowski
 */
public class RemoteEmailSendRequest extends BaseManagerModel {

  @Valid
  @NotEmpty(message = "{" + VALIDATION_REMOTEEMAILSENDREQUEST_TORECIPIENTS_NOTEMPTY + "}")
  private List<EmailContact> toRecipients;

  @NotNull(message = "{" + VALIDATION_REMOTEEMAILSENDREQUEST_TEMPLATE_NOTNULL + "}")
  private TemplateManager.Template template;

  private Map<String, Object> templateModel;

  @JsonProperty
  private Locale locale;

  @Valid
  private List<EmailAttachment> attachments;

  @Valid
  private List<EmailAttachment> inlineImages;

  public RemoteEmailSendRequest() {
    this.toRecipients = new ArrayList<>();
    this.locale = DEFAULT_LOCALE;
    this.attachments = new ArrayList<>();
    this.inlineImages = new ArrayList<>();
  }

  public List<EmailContact> getToRecipients() {
    return toRecipients;
  }

  public RemoteEmailSendRequest setToRecipients(List<EmailContact> toRecipients) {
    this.toRecipients = toRecipients;
    return this;
  }

  public TemplateManager.Template getTemplate() {
    return template;
  }

  public RemoteEmailSendRequest setTemplate(TemplateManager.Template template) {
    this.template = template;
    return this;
  }

  public Map<String, Object> getTemplateModel() {
    return templateModel;
  }

  public RemoteEmailSendRequest setTemplateModel(Map<String, Object> templateModel) {
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
  public RemoteEmailSendRequest setLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  public RemoteEmailSendRequest setLocale(String locale) {
    this.locale = Locale.forLanguageTag(locale);
    return this;
  }

  public List<EmailAttachment> getAttachments() {
    return attachments;
  }

  public RemoteEmailSendRequest setAttachments(List<EmailAttachment> attachments) {
    this.attachments = attachments;
    return this;
  }

  public RemoteEmailSendRequest addAttachment(EmailAttachment attachment) {
    getAttachments().add(attachment);
    return this;
  }

  public List<EmailAttachment> getInlineImages() {
    return inlineImages;
  }

  public RemoteEmailSendRequest setInlineImages(List<EmailAttachment> inlineImages) {
    this.inlineImages = inlineImages;
    return this;
  }

  public RemoteEmailSendRequest addInlineImage(EmailAttachment inlineImage) {
    getInlineImages().add(inlineImage);
    return this;
  }

}
