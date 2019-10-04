package com.openease.common.data.exception;

import com.openease.common.exception.GeneralException;
import com.openease.common.util.spring.BeanLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * General data exception with internationalized (i18n) messages
 *
 * @author Alan Czajkowski
 */
public class GeneralDataException extends GeneralException {

  public GeneralDataException(String key) {
    super(key, BeanLocator.byClassAndName(MessageSource.class, "messageSource").getMessage(key, null, LocaleContextHolder.getLocale()));
  }

  public GeneralDataException(String key, Throwable cause) {
    super(key, cause);
  }

  public GeneralDataException(String key, String message) {
    super(key, message);
  }

  public GeneralDataException(String key, String message, Throwable cause) {
    super(key, message, cause);
  }

}
