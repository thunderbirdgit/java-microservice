package com.openease.common.manager.captcha.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.openease.common.manager.base.model.BaseManagerModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Captcha verify response
 * - as defined by Google reCaptcha API
 *
 * @author Alan Czajkowski
 */
public class CaptchaVerifyResponse extends BaseManagerModel {

  private boolean success;

  /**
   * timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
   */
  @JsonAlias("challenge_ts")
  private String challengeTs;

  /**
   * hostname of the site where the reCAPTCHA was solved
   */
  private String hostname;

  @JsonAlias("error-codes")
  private ErrorCode[] errorCodes;

  /**
   * Google reCaptcha API error codes
   * <pre>
   * Error Code	             Description
   * --------------------------------------------------------------------------
   * missing-input-secret	   The secret parameter is missing.
   * invalid-input-secret	   The secret parameter is invalid or malformed.
   * missing-input-response	 The response parameter is missing.
   * invalid-input-response	 The response parameter is invalid or malformed.
   * bad-request	           The request is invalid or malformed.
   * timeout-or-duplicate	   The response is no longer valid: either is too old or has been used previously.
   * </pre>
   *
   * @see <a href="https://developers.google.com/recaptcha/docs/verify">Google Developers | reCaptcha | verify</a>
   */
  public enum ErrorCode {
    MissingSecret("missing-input-secret"),
    InvalidSecret("invalid-input-secret"),
    MissingResponse("missing-input-response"),
    InvalidResponse("invalid-input-response"),
    BadRequest("bad-request"),
    TimeoutOrDuplicate("timeout-or-duplicate");

    private String value;

    ErrorCode(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @JsonCreator
    public static ErrorCode findByValue(String value) {
      ErrorCode errorCodeFound = null;
      for (ErrorCode errorCode : ErrorCode.values()) {
        if (StringUtils.equals(errorCode.getValue(), value)) {
          errorCodeFound = errorCode;
          break;
        }
      }
      return errorCodeFound;
    }
  }

  public boolean getSuccess() {
    return success;
  }

  public CaptchaVerifyResponse setSuccess(boolean success) {
    this.success = success;
    return this;
  }

  public String getChallengeTs() {
    return challengeTs;
  }

  public CaptchaVerifyResponse setChallengeTs(String challengeTs) {
    this.challengeTs = challengeTs;
    return this;
  }

  public String getHostname() {
    return hostname;
  }

  public CaptchaVerifyResponse setHostname(String hostname) {
    this.hostname = hostname;
    return this;
  }

  public ErrorCode[] getErrorCodes() {
    return errorCodes;
  }

  public CaptchaVerifyResponse setErrorCodes(ErrorCode[] errorCodes) {
    this.errorCodes = errorCodes;
    return this;
  }

}
