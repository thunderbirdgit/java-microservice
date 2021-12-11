package com.openease.common.manager.captcha;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.openease.common.manager.captcha.request.CaptchaVerifyRequest;
import com.openease.common.manager.captcha.response.CaptchaVerifyResponse;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.util.exception.GeneralUtilException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.openease.common.util.JsonPasswordSerializer.MASK;
import static com.openease.common.util.JsonUtils.toObject;
import static com.openease.common.web.util.RestUtils.httpPostRequest;

/**
 * Captcha manager
 *
 * @author Alan Czajkowski
 */
@Service
public class CaptchaManager {

  private static final transient Logger LOG = LogManager.getLogger(CaptchaManager.class);

  private static final String PRIVATE_KEY_NAME = "secret";
  private static final String USER_TOKEN_NAME = "response";
  private static final String USER_IP_NAME = "remoteip";

  @Value("${manager.captcha.enabled}")
  private boolean enabled;

  @Value("${manager.captcha.rest-url}")
  private String restUrl;

  @Value("${manager.captcha.api.key.public}")
  private String apiPublicKey;

  @Value("${manager.captcha.api.key.private}")
  private String apiPrivateKey;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("enabled: {}", enabled);
    LOG.debug("rest URL: {}", restUrl);
    LOG.debug("API public key: {}", apiPublicKey);
    LOG.debug("API private key: {}", MASK);

    LOG.debug("Init finished");
  }

  public CaptchaVerifyResponse verify(CaptchaVerifyRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    // set default success to true so that any environment with captcha disabled will always pass
    CaptchaVerifyResponse response = new CaptchaVerifyResponse()
        .setSuccess(true);

    if (enabled) {
      if (request != null) {
        List<NameValuePair> requestParams = new ArrayList<>();
        requestParams.add(new NameValuePair(PRIVATE_KEY_NAME, apiPrivateKey));
        requestParams.add(new NameValuePair(USER_TOKEN_NAME, request.getUserToken()));
        requestParams.add(new NameValuePair(USER_IP_NAME, request.getUserIp()));

        String captchaJsonResponse = httpPostRequest(restUrl, requestParams);
        System.out.println(captchaJsonResponse);
        try {
          response = toObject(captchaJsonResponse, CaptchaVerifyResponse.class);
        } catch (GeneralUtilException ue) {
          LOG.error("{}: {}", () -> ue.getClass().getSimpleName(), ue::getMessage);
          //TODO: fix me
          throw new GeneralManagerException(ue.getMessage());
        }
      } else {
        //TODO: fix me
        throw new GeneralManagerException("request is null");
      }
    }

    return response;
  }

}
