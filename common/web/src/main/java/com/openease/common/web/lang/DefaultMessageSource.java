package com.openease.common.web.lang;

import com.openease.common.util.spring.BeanLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.ArrayUtils.addAll;

/**
 * Default {@link MessageSource}
 * - internationalization (i18n) bean that loads all language message keys
 *
 * @author Alan Czajkowski
 * @see {@link ReloadableResourceBundleMessageSource}
 */
@Component("messageSource")
public class DefaultMessageSource extends ReloadableResourceBundleMessageSource implements MessageSource {

  private static final transient Logger LOG = LogManager.getLogger(DefaultMessageSource.class);

  @Value("${message-source.basenames}")
  private String[] messageSourceBasenames;

  @Autowired
  private BeanLocator beanLocator;

  protected static final String[] DEFAULT_MESSAGE_SOURCE_BASENAMES = {
      "classpath:/com/openease/common/data/lang/common-data-messages",
      "classpath:/com/openease/common/manager/lang/common-manager-messages",
      "classpath:/com/openease/common/web/lang/common-web-messages"
  };

  public DefaultMessageSource() {
    super();
  }

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("default message source basenames: {}", () -> toJson(DEFAULT_MESSAGE_SOURCE_BASENAMES));
    LOG.debug("message source basenames: {}", () -> toJson(messageSourceBasenames));

    setBasenames(addAll(DEFAULT_MESSAGE_SOURCE_BASENAMES, messageSourceBasenames));
    setDefaultEncoding(UTF_8.name());
    setUseCodeAsDefaultMessage(true);

    LOG.debug("Init finished");
  }

}
