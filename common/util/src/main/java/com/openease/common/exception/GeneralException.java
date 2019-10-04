package com.openease.common.exception;

/**
 * General exception with internationalized (i18n) messages
 *
 * @author Alan Czajkowski
 */
public abstract class GeneralException extends Exception {

  /**
   * Internationalized (i18n) message key
   */
  private final String key;

  public GeneralException(String key) {
    super(key);
    this.key = key;
  }

  public GeneralException(String key, Throwable cause) {
    super(cause);
    this.key = key;
  }

  public GeneralException(String key, String message) {
    super(message);
    this.key = key;
  }

  public GeneralException(String key, String message, Throwable cause) {
    super(message, cause);
    this.key = key;
  }

  public String getKey() {
    return key;
  }

}
