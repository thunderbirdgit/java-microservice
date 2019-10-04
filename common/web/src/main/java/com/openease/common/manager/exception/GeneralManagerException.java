package com.openease.common.manager.exception;

import com.openease.common.exception.GeneralException;
import com.openease.common.util.spring.BeanLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * General manager exception with internationalized (i18n) messages
 *
 * @author Alan Czajkowski
 */
public class GeneralManagerException extends GeneralException {

  public GeneralManagerException(String key) {
    super(key, BeanLocator.byClassAndName(MessageSource.class, "messageSource").getMessage(key, null, LocaleContextHolder.getLocale()));
  }

  public GeneralManagerException(String key, Throwable cause) {
    super(key, cause);
  }

  public GeneralManagerException(String key, String message) {
    super(key, message);
  }

  public GeneralManagerException(String key, String message, Throwable cause) {
    super(key, message, cause);
  }

}
