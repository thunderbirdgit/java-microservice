package com.openease.common.web.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

/**
 * Base internationalization (i18n) config
 *
 * @author Alan Czajkowski
 */
public abstract class BaseI18nConfig {

  private static final transient Logger LOG = LogManager.getLogger(BaseI18nConfig.class);

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  public void init() {
    LOG.debug("{} bean instance of: {}", MessageSource.class::getSimpleName, () -> messageSource.getClass().getSimpleName());
  }

}
