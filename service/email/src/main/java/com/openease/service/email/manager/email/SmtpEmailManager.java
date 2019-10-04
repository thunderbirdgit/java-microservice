package com.openease.service.email.manager.email;

import com.openease.common.manager.email.request.EmailAttachment;
import com.openease.common.manager.email.request.EmailContact;
import com.openease.common.manager.email.request.EmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.service.email.manager.email.task.SendEmailTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import static com.openease.common.manager.lang.MessageKeys.MANAGER_EMAIL_ERROR_ADDRESSFORMAT;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_EMAIL_ERROR_GENERALFAILURE;
import static com.openease.common.util.JavaUtils.notNull;
import static com.openease.common.util.JsonPasswordSerializer.MASK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;
import static javax.mail.Part.ATTACHMENT;
import static javax.mail.Part.INLINE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * SMTP Email manager
 *
 * @author Alan Czajkowski
 */
@Service
public class SmtpEmailManager implements EmailManager {

  private static final transient Logger LOG = LogManager.getLogger(SmtpEmailManager.class);

  public static final String PART_HEADER_CONTENT_ID = "Content-ID";
  public static final String PART_HEADER_CONTENT_ID_PREFIX = "<";
  public static final String PART_HEADER_CONTENT_ID_SUFFIX = ">";

  @Value("${manager.email.protocol}")
  private String transportProtocol;

  @Value("${manager.email.smtp.host}")
  private String smtpHost;

  @Value("${manager.email.smtp.port}")
  private String smtpPort;

  @Value("${manager.email.smtp.starttls.enabled}")
  private String smtpStartTlsEnabled;

  @Value("${manager.email.smtp.ssl.enabled}")
  private String smtpSslEnabled;

  @Value("${manager.email.smtp.username}")
  private String smtpUsername;

  @Value("${manager.email.smtp.password}")
  private String smtpPassword;

  @Value("${manager.email.sender.address}")
  private String senderEmailAddress;

  @Value("${manager.email.sender.name}")
  private String senderName;

  @Value("${manager.email.replyTo.address:}")
  private String replyToEmailAddress;

  @Value("${manager.email.replyTo.name:}")
  private String replyToName;

  @Value("${manager.email.message.mimeType}")
  private String messageMimeType;

  @Autowired
  @Qualifier("defaultAsyncTaskExecutor")
  private AsyncTaskExecutor asyncTaskExecutor;

  private Properties settings;

  private Authenticator authenticator;

  private Address fromAddress;

  private Address replyToAddress;

  @PostConstruct
  public void init() throws GeneralManagerException {
    LOG.debug("Init started");

    LOG.debug("email transport protocol: {}", transportProtocol);
    LOG.debug("email SMTP host: {}", smtpHost);
    LOG.debug("email SMTP port: {}", smtpPort);
    LOG.debug("email SMTP start-TLS enabled: {}", smtpStartTlsEnabled);
    LOG.debug("email SMTP SSL enabled: {}", smtpSslEnabled);
    LOG.debug("email SMTP username: {}", smtpUsername);
    LOG.debug("email SMTP password: {}", MASK);
    LOG.debug("email sender (from) address: {}", senderEmailAddress);
    LOG.debug("email sender (from) name: {}", senderName);
    LOG.debug("email reply-to address: {}", replyToEmailAddress);
    LOG.debug("email reply-to name: {}", replyToName);
    LOG.debug("email message MIME-type: {}", messageMimeType);

    settings = new Properties();
    settings.setProperty("mail.protocol", transportProtocol);
    settings.setProperty("mail.smtp.host", smtpHost);
    settings.setProperty("mail.smtp.port", smtpPort);
    settings.setProperty("mail.smtp.auth", "true");
    settings.setProperty("mail.smtp.starttls.enable", smtpStartTlsEnabled);
    settings.setProperty("mail.smtp.ssl.enable", smtpSslEnabled);
    settings.setProperty("mail.smtp.socketFactory.port", smtpPort);
    settings.setProperty("mail.smtp.socketFactory.class", SSLSocketFactory.class.getName());

    final PasswordAuthentication authentication = new PasswordAuthentication(smtpUsername, smtpPassword);
    authenticator = new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
      }
    };

    try {
      // set REPLY-TO address
      if (isNotBlank(replyToEmailAddress)) {
        replyToAddress = new InternetAddress(replyToEmailAddress, isNotBlank(replyToName) ? replyToName : replyToEmailAddress, UTF_8.name());
      } else {
        LOG.warn("reply-to email address is missing");
        replyToAddress = null;
      }

      // set FROM address
      if (isNotBlank(senderEmailAddress)) {
        fromAddress = new InternetAddress(senderEmailAddress, senderName, UTF_8.name());
      } else {
        throw new GeneralManagerException("sender email address is invalid: [" + senderEmailAddress + "]");
      }
    } catch (UnsupportedEncodingException uee) {
      LOG.error(uee::getMessage, uee);
    }

    LOG.debug("Init finished");
  }

  @Override
  public void submit(EmailSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      SendEmailTask task = new SendEmailTask(request);
      LOG.debug("Submitting {} with id: {}", () -> task.getClass().getSimpleName(), task::getId);
      //TODO: deal with this future somehow (since task may fail)
      Future<Boolean> taskFuture = asyncTaskExecutor.submit(task);
    } catch (Exception e) {
      LOG.error("Problem creating email task", e);
      throw new GeneralManagerException(MANAGER_EMAIL_ERROR_GENERALFAILURE, "Problem creating email task", e);
    }
  }

  @Override
  public void send(EmailSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      Session session = Session.getInstance(settings, authenticator);
      session.setDebug(LOG.isDebugEnabled());
      Message message = new MimeMessage(session);
      Multipart multipart = new MimeMultipart();

      message.setFrom(fromAddress);
      if (request.getReplyTo() != null) {
        addReplyTo(message, request.getReplyTo());
      } else {
        message.setReplyTo(new Address[]{replyToAddress});
      }

      message.setSubject(request.getSubject());

      addRecipients(message, TO, request.getToRecipients());
      addRecipients(message, CC, request.getCcRecipients());
      addRecipients(message, BCC, request.getBccRecipients());

      addMimeBodyParts(multipart, ATTACHMENT, request.getAttachments());
      addMimeBodyParts(multipart, INLINE, request.getInlineImages());

      // set body
      BodyPart htmlBodyPart = new MimeBodyPart();
      htmlBodyPart.setContent(request.getHtmlBody(), messageMimeType);
      multipart.addBodyPart(htmlBodyPart);

      message.setContent(multipart);

      // save and send
      message.saveChanges();
      LOG.debug("Send email message");
      Transport.send(message);
    } catch (AddressException ae) {
      LOG.error("Address is improperly formatted", ae);
      throw new GeneralManagerException(MANAGER_EMAIL_ERROR_ADDRESSFORMAT, "Address is improperly formatted", ae);
    } catch (Exception e) {
      LOG.error("Problem sending email", e);
      throw new GeneralManagerException(MANAGER_EMAIL_ERROR_GENERALFAILURE, "Problem sending email", e);
    }
  }

  @Override
  public void send(String recipientAddress, String recipientName, String subject, String htmlBody) throws GeneralManagerException {
    EmailContact recipient = new EmailContact()
        .setEmail(recipientAddress)
        .setName(recipientName);
    EmailSendRequest emailSendRequest = new EmailSendRequest(recipient, subject, htmlBody);
    send(emailSendRequest);
  }

  @Override
  public void send(String recipientAddress, String subject, String htmlBody) throws GeneralManagerException {
    EmailContact recipient = new EmailContact()
        .setEmail(recipientAddress);
    EmailSendRequest emailSendRequest = new EmailSendRequest(recipient, subject, htmlBody);
    send(emailSendRequest);
  }

  private void addReplyTo(Message message, EmailContact replyTo) throws MessagingException, UnsupportedEncodingException {
    if (replyTo != null) {
      InternetAddress replyToAddress = new InternetAddress();
      replyToAddress.setAddress(replyTo.getEmail());
      if (isNotBlank(replyTo.getName())) {
        replyToAddress.setPersonal(replyTo.getName(), UTF_8.name());
      }
      message.setReplyTo(new Address[]{replyToAddress});
    }
  }

  private void addRecipients(Message message, RecipientType recipientType, List<EmailContact> recipients) throws MessagingException, UnsupportedEncodingException {
    for (EmailContact recipient : notNull(recipients)) {
      LOG.debug("Adding [{}] recipient: \"{}\" <{}>", () -> recipientType, recipient::getName, recipient::getEmail);
      InternetAddress recipientAddress = new InternetAddress();
      recipientAddress.setAddress(recipient.getEmail());
      if (isNotBlank(recipient.getName())) {
        recipientAddress.setPersonal(recipient.getName(), UTF_8.name());
      }
      message.addRecipient(recipientType, recipientAddress);
    }
  }

  private void addMimeBodyParts(Multipart multipart, String disposition, List<EmailAttachment> attachments) throws MessagingException {
    for (EmailAttachment attachment : notNull(attachments)) {
      DataSource source = new ByteArrayDataSource(attachment.getData(), attachment.getMimeType());
      BodyPart attachmentBodyPart = new MimeBodyPart();
      attachmentBodyPart.setDisposition(disposition);
      attachmentBodyPart.setFileName(attachment.getFileName());
      attachmentBodyPart.setDataHandler(new DataHandler(source));
      if (StringUtils.equals(disposition, INLINE)) {
        attachmentBodyPart.setHeader(PART_HEADER_CONTENT_ID, PART_HEADER_CONTENT_ID_PREFIX + attachment.getFileName() + PART_HEADER_CONTENT_ID_SUFFIX);
      }
      multipart.addBodyPart(attachmentBodyPart);
    }
  }

}
