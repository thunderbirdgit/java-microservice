package com.openease.common.util.exception;

/**
 * General exception to be used in utility classes
 *
 * @author Alan Czajkowski
 */
public class GeneralUtilException extends RuntimeException {

  public GeneralUtilException(Throwable throwable) {
    super(throwable);
  }

  public GeneralUtilException(String message) {
    super(message);
  }

  public GeneralUtilException(String message, Throwable cause) {
    super(message, cause);
  }

}
