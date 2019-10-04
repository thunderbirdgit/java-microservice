package com.openease.common.manager.template;

import com.openease.common.manager.exception.GeneralManagerException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static com.openease.common.manager.base.model.BaseManagerModel.DEFAULT_LOCALE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_EMAIL_HTML_NOTIFYPASSWORDCHANGE_TITLE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_EMAIL_HTML_NOTIFYUSERNAMECHANGE_TITLE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_EMAIL_HTML_RESETPASSWORD_TITLE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_EMAIL_HTML_VERIFYACCOUNT_TITLE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_EMAIL_HTML_WELCOME_TITLE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_SMS_TEXT_NOTIFYPASSWORDCHANGE_TITLE;
import static com.openease.common.manager.lang.MessageKeys.TEMPLATES_SMS_TEXT_NOTIFYUSERNAMECHANGE_TITLE;
import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Template manager
 *
 * @author Alan Czajkowski
 */
@Service
public class TemplateManager implements ApplicationContextAware {

  private static final transient Logger LOG = LogManager.getLogger(TemplateManager.class);

  public static final String TEMPLATES = "templates";

  public static final String EMAIL = "email";
  public static final String EMAIL_PATH = "/" + EMAIL;

  public static final String SMS = "sms";
  public static final String SMS_PATH = "/" + SMS;

  public static final String SUFFIX_TXT = ".txt";
  public static final String SUFFIX_HTML = ".html";

  public static final String TEMPLATE_MODEL_SUBJECT = "subject";
  public static final String TEMPLATE_MODEL_EMAIL = "email";
  public static final String TEMPLATE_MODEL_FIRSTNAME = "firstName";
  public static final String TEMPLATE_MODEL_LASTNAME = "lastName";
  public static final String TEMPLATE_MODEL_INLINE_LOGO_CID = "logoFileName";

  /**
   * Template enum
   */
  public enum Template {
    Email_HTML_Welcome(TEMPLATES_EMAIL_HTML_WELCOME_TITLE, EMAIL_PATH + "/welcome" + SUFFIX_HTML),
    Email_HTML_VerifyAccount(TEMPLATES_EMAIL_HTML_VERIFYACCOUNT_TITLE, EMAIL_PATH + "/verify-account" + SUFFIX_HTML),
    Email_HTML_ResetPassword(TEMPLATES_EMAIL_HTML_RESETPASSWORD_TITLE, EMAIL_PATH + "/reset-password" + SUFFIX_HTML),
    Email_HTML_NotifyPasswordChange(TEMPLATES_EMAIL_HTML_NOTIFYPASSWORDCHANGE_TITLE, EMAIL_PATH + "/notify-password-change" + SUFFIX_HTML),
    Email_HTML_NotifyUsernameChange(TEMPLATES_EMAIL_HTML_NOTIFYUSERNAMECHANGE_TITLE, EMAIL_PATH + "/notify-username-change" + SUFFIX_HTML),

    SMS_TEXT_NotifyPasswordChange(TEMPLATES_SMS_TEXT_NOTIFYPASSWORDCHANGE_TITLE, SMS_PATH + "/notify-password-change" + SUFFIX_TXT),
    SMS_TEXT_NotifyUsernameChange(TEMPLATES_SMS_TEXT_NOTIFYUSERNAMECHANGE_TITLE, SMS_PATH + "/notify-username-change" + SUFFIX_TXT),

    None(null, null);

    private String titleKey;

    private String path;

    Template(String titleKey, String path) {
      this.titleKey = titleKey;
      this.path = path;
    }

    public String getTitleKey() {
      return titleKey;
    }

    public String getPath() {
      return path;
    }

    public static Template findByPath(String path) {
      Template templateFound = None;
      for (Template template : Template.values()) {
        if (StringUtils.equals(template.getPath(), path)) {
          templateFound = template;
          break;
        }
      }
      return templateFound;
    }
  }

  @Value("${manager.template.prefix}")
  private String templatePrefix;

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  private SpringTemplateEngine templateEngine;

  private ApplicationContext applicationContext;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("template prefix: {}", templatePrefix);

    templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateEngineMessageSource(messageSource);

    SpringResourceTemplateResolver templateResolver;

    // TEXT template resolver
    templateResolver = new SpringResourceTemplateResolver();
//    templateResolver.setOrder(1);
    templateResolver.setPrefix(templatePrefix);
//    templateResolver.setResolvablePatterns(Collections.singleton(EMAIL_CONTEXT + "/*"));
//    templateResolver.setSuffix(templateTextSuffix);
    templateResolver.setTemplateMode(TemplateMode.TEXT);
    templateResolver.setCharacterEncoding(UTF_8.name());
    templateResolver.setCacheable(false);
    templateResolver.setCheckExistence(true);
    templateResolver.setApplicationContext(applicationContext);
    templateEngine.addTemplateResolver(templateResolver);

    // HTML template resolver
    templateResolver = new SpringResourceTemplateResolver();
//    templateResolver.setOrder(1);
    templateResolver.setPrefix(templatePrefix);
//    templateResolver.setResolvablePatterns(Collections.singleton(EMAIL_CONTEXT + "/*"));
//    templateResolver.setSuffix(templateHtmlSuffix);
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCharacterEncoding(UTF_8.name());
    templateResolver.setCacheable(false);
    templateResolver.setCheckExistence(true);
    templateResolver.setApplicationContext(applicationContext);
    templateEngine.addTemplateResolver(templateResolver);

    LOG.debug("Init finished");
  }

  /**
   * Render template
   *
   * @param template      {@link Template} to render
   * @param templateModel map of attributes to be used in rendering the template
   *
   * @return rendered content (as a {@link String})
   */
  public String render(Template template, Map<String, Object> templateModel) throws GeneralManagerException {
    return render(template.getPath(), templateModel, DEFAULT_LOCALE);
  }

  /**
   * Render template
   *
   * @param template      {@link Template} to render
   * @param templateModel map of attributes to be used in rendering the template
   * @param locale        locale
   *
   * @return rendered content (as a {@link String})
   */
  public String render(Template template, Map<String, Object> templateModel, Locale locale) throws GeneralManagerException {
    return render(template.getPath(), templateModel, locale);
  }

  /**
   * Render template
   *
   * @param template {@link Template} to render
   *
   * @return rendered content (as a {@link String})
   */
  public String render(Template template) throws GeneralManagerException {
    return render(template, Collections.emptyMap());
  }

  private String render(String templatePath, Map<String, Object> templateModel, Locale locale) throws GeneralManagerException {
    LOG.debug("templatePath: {}", templatePath);
    LOG.debug("templateModel: {}", () -> toJson(templateModel));
    LOG.debug("locale: {}", locale);

    String renderedContent;
    Context context = new Context(locale, templateModel);

    try {
      renderedContent = templateEngine.process(templatePath, context);
    } catch (RuntimeException re) {
      LOG.warn("{}: {}", () -> re.getClass().getSimpleName(), re::getMessage);
      throw new GeneralManagerException(this.getClass().getSimpleName() + ": failed to render template", re);
    }

    return renderedContent;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

}
