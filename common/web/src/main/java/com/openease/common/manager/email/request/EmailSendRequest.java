package com.openease.common.manager.email.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILSENDREQUEST_HTMLBODY_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILSENDREQUEST_SUBJECT_NOTBLANK;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILSENDREQUEST_TORECIPIENTS_NOTEMPTY;

/**
 * Email: Send request
 *
 * @author Alan Czajkowski
 */
public class EmailSendRequest extends BaseManagerModel {

  @Valid
  private EmailContact replyTo;

  @Valid
  @NotEmpty(message = "{" + VALIDATION_EMAILSENDREQUEST_TORECIPIENTS_NOTEMPTY + "}")
  private List<EmailContact> toRecipients;

  @Valid
  private List<EmailContact> ccRecipients;

  @Valid
  private List<EmailContact> bccRecipients;

  @NotBlank(message = "{" + VALIDATION_EMAILSENDREQUEST_SUBJECT_NOTBLANK + "}")
  private String subject;

  @NotNull(message = "{" + VALIDATION_EMAILSENDREQUEST_HTMLBODY_NOTNULL + "}")
  private String htmlBody;

  @Valid
  private List<EmailAttachment> attachments;

  @Valid
  private List<EmailAttachment> inlineImages;

  public EmailSendRequest() {
    this.replyTo = null;
    this.toRecipients = new ArrayList<>();
    this.ccRecipients = new ArrayList<>();
    this.bccRecipients = new ArrayList<>();
    this.subject = "";
    this.htmlBody = "";
    this.attachments = new ArrayList<>();
    this.inlineImages = new ArrayList<>();
  }

  public EmailSendRequest(EmailContact recipient, String subject, String htmlBody) {
    this();
    this.toRecipients.add(recipient);
    this.subject = subject;
    this.htmlBody = htmlBody;
  }

  public EmailSendRequest addToRecipient(EmailContact recipient) {
    toRecipients.add(recipient);
    return this;
  }

  public EmailSendRequest addToRecipients(EmailContact... recipients) {
    toRecipients.addAll(Arrays.asList(recipients));
    return this;
  }

  public EmailSendRequest addToRecipients(List<EmailContact> recipients) {
    toRecipients.addAll(recipients);
    return this;
  }

  public EmailSendRequest addCcRecipient(EmailContact recipient) {
    ccRecipients.add(recipient);
    return this;
  }

  public EmailSendRequest addCcRecipients(EmailContact... recipients) {
    ccRecipients.addAll(Arrays.asList(recipients));
    return this;
  }

  public EmailSendRequest addCcRecipients(List<EmailContact> recipients) {
    ccRecipients.addAll(recipients);
    return this;
  }

  public EmailSendRequest addBccRecipient(EmailContact recipient) {
    bccRecipients.add(recipient);
    return this;
  }

  public EmailSendRequest addBccRecipients(EmailContact... recipients) {
    bccRecipients.addAll(Arrays.asList(recipients));
    return this;
  }

  public EmailSendRequest addBccRecipients(List<EmailContact> recipients) {
    bccRecipients.addAll(recipients);
    return this;
  }

  public EmailSendRequest addAttachment(EmailAttachment attachment) {
    attachments.add(attachment);
    return this;
  }

  public EmailSendRequest addAttachments(EmailAttachment... attachments) {
    return addAttachments(Arrays.asList(attachments));
  }

  public EmailSendRequest addAttachments(List<EmailAttachment> attachments) {
    this.attachments.addAll(attachments);
    return this;
  }

  public EmailSendRequest addInlineImages(EmailAttachment... inlineImages) {
    return addInlineImages(Arrays.asList(inlineImages));
  }

  public EmailSendRequest addInlineImages(List<EmailAttachment> inlineImages) {
    this.inlineImages.addAll(inlineImages);
    return this;
  }

  public EmailContact getReplyTo() {
    return replyTo;
  }

  public EmailSendRequest setReplyTo(EmailContact replyTo) {
    this.replyTo = replyTo;
    return this;
  }

  public List<EmailContact> getToRecipients() {
    return toRecipients;
  }

  public EmailSendRequest setToRecipients(List<EmailContact> toRecipients) {
    this.toRecipients = toRecipients;
    return this;
  }

  public List<EmailContact> getCcRecipients() {
    return ccRecipients;
  }

  public EmailSendRequest setCcRecipients(List<EmailContact> ccRecipients) {
    this.ccRecipients = ccRecipients;
    return this;
  }

  public List<EmailContact> getBccRecipients() {
    return bccRecipients;
  }

  public EmailSendRequest setBccRecipients(List<EmailContact> bccRecipients) {
    this.bccRecipients = bccRecipients;
    return this;
  }

  public String getSubject() {
    return subject;
  }

  public EmailSendRequest setSubject(String subject) {
    this.subject = subject;
    return this;
  }

  public String getHtmlBody() {
    return htmlBody;
  }

  public EmailSendRequest setHtmlBody(String htmlBody) {
    this.htmlBody = htmlBody;
    return this;
  }

  public List<EmailAttachment> getAttachments() {
    return attachments;
  }

  public EmailSendRequest setAttachments(List<EmailAttachment> attachments) {
    this.attachments = attachments;
    return this;
  }

  public List<EmailAttachment> getInlineImages() {
    return inlineImages;
  }

  public EmailSendRequest setInlineImages(List<EmailAttachment> inlineImages) {
    this.inlineImages = inlineImages;
    return this;
  }

}
