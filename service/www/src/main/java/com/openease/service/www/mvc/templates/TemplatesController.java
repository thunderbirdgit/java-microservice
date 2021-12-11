package com.openease.service.www.mvc.templates;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.template.TemplateManager;
import com.openease.common.web.mvc.base.BaseMvcController;
import com.openease.common.web.mvc.base.exception.MvcException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.openease.common.manager.template.TemplateManager.SUFFIX_HTML;
import static com.openease.common.manager.template.TemplateManager.SUFFIX_TXT;
import static com.openease.common.manager.template.TemplateManager.TEMPLATES;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_EMAIL;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_FIRSTNAME;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_LASTNAME;
import static com.openease.common.manager.template.TemplateManager.TEMPLATE_MODEL_SUBJECT;
import static com.openease.common.web.util.HttpUtils.CHARSET_UTF_8_VALUE;
import static com.openease.service.www.mvc.templates.TemplatesController.TEMPLATES_CONTEXT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Templates controller
 * - only used to demonstrate what a template would render as using dummy data
 *
 * @author Alan Czajkowski
 */
@Controller
@ResponseBody
@RequestMapping(path = TEMPLATES_CONTEXT)
public class TemplatesController extends BaseMvcController {

  private static final transient Logger LOG = LogManager.getLogger(TemplatesController.class);

  public static final String TEMPLATES_CONTEXT = "/" + TEMPLATES;

  @Value("${dummy-account.email}")
  private String dummyAccountEmail;

  @Value("${dummy-account.first-name}")
  private String dummyAccountFirstName;

  @Value("${dummy-account.last-name}")
  private String dummyAccountLastName;

  @Autowired
  private TemplateManager templateManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("dummy account email: {}", dummyAccountEmail);
    LOG.debug("dummy account first name: {}", dummyAccountFirstName);
    LOG.debug("dummy account last name: {}", dummyAccountLastName);

    LOG.debug("Init finished");
  }

  @GetMapping(path = {"/{templatePath1}" + SUFFIX_TXT, "/{templatePath1}/{templatePath2}" + SUFFIX_TXT}, produces = TEXT_PLAIN_VALUE + ";charset=" + CHARSET_UTF_8_VALUE)
  public String renderTextTemplate(@PathVariable String templatePath1, @PathVariable(required = false) String templatePath2) {
    return renderTemplate(templatePath1, templatePath2, SUFFIX_TXT);
  }

  @GetMapping(path = {"/{templatePath1}" + SUFFIX_HTML, "/{templatePath1}/{templatePath2}" + SUFFIX_HTML}, produces = TEXT_HTML_VALUE + ";charset=" + CHARSET_UTF_8_VALUE)
  public String renderHtmlTemplate(@PathVariable String templatePath1, @PathVariable(required = false) String templatePath2) {
    return renderTemplate(templatePath1, templatePath2, SUFFIX_HTML);
  }

  private String renderTemplate(String templatePath1, String templatePath2, String templateSuffix) {
    String templatePath = "/" + templatePath1;
    if (isNotBlank(templatePath2)) {
      templatePath += "/" + templatePath2;
    }
    templatePath += templateSuffix;
    LOG.debug("templatePath: {}", templatePath);
    Locale locale = LocaleContextHolder.getLocale();
    LOG.debug("locale: {}", locale);

    final TemplateManager.Template template = TemplateManager.Template.findByPath(templatePath);
    LOG.debug("template: {}", template);
    Map<String, Object> templateModel = initTemplateModel();

    switch (template) {

      /* email templates */
      case Email_HTML_Welcome:
        // add to templateModel if necessary
        break;
      case Email_HTML_VerifyAccount:
        // add to templateModel if necessary
        break;
      case Email_HTML_ResetPassword:
        // add to templateModel if necessary
        break;
      case Email_HTML_NotifyPasswordChange:
        // add to templateModel if necessary
        break;
      case Email_HTML_NotifyUsernameChange:
        // add to templateModel if necessary
        break;

      /* SMS templates */
      case SMS_TEXT_NotifyUsernameChange:
        // add to templateModel if necessary
        break;

      default:
        LOG.error("Template not found: {}", templatePath);
        throw new MvcException(NOT_FOUND);
    }

    String subject = messageSource.getMessage(template.getTitleKey(), null, locale);
    templateModel.put(TEMPLATE_MODEL_SUBJECT, subject);

    String renderedContent;
    try {
      renderedContent = templateManager.render(template, templateModel, locale);
      LOG.debug("renderedContent:{}{}", System::lineSeparator, () -> renderedContent);
    } catch (GeneralManagerException me) {
      LOG.warn(me::getMessage, me);
      throw new MvcException(INTERNAL_SERVER_ERROR, me.getMessage(), me);
    }

    return renderedContent;
  }

  private Map<String, Object> initTemplateModel() {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put(TEMPLATE_MODEL_EMAIL, dummyAccountEmail);
    templateModel.put(TEMPLATE_MODEL_FIRSTNAME, dummyAccountFirstName);
    templateModel.put(TEMPLATE_MODEL_LASTNAME, dummyAccountLastName);
    return templateModel;
  }

}
