package com.openease.service.www.api.security;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.security.SecurityManager;
import com.openease.common.manager.security.request.SecurityCreateAuthRequest;
import com.openease.common.manager.security.response.SecurityCreateAuthResponse;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.openease.common.data.lang.MessageKeys.CRUD_DELETE_SUCCESS;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_CREDENTIALS_INVALID;
import static com.openease.common.manager.security.SecurityManager.SECURITY;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.security.BaseAuthSecurityConfig.getSignedInAccount;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.www.api.security.SecurityController.SECURITY_CONTEXT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Security controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = SECURITY_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class SecurityController extends BaseApiController implements LogoutHandler {

  private static final transient Logger LOG = LogManager.getLogger(SecurityController.class);

  public static final String SECURITY_CONTEXT = "/" + SECURITY;

  @Autowired
  private SecurityManager securityManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Create security authentication (sign-in)
   */
  @PostMapping(path = {"/auth", "/auth/"})
  public SuccessApiResponse<SecurityCreateAuthResponse> create(@RequestBody @Valid SecurityCreateAuthRequest request, HttpServletRequest httpRequest) {
    LOG.trace("{}.create()", SecurityController.class::getSimpleName);
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse<SecurityCreateAuthResponse> response;

    if (request.getCaptchaUserToken() != null) {
      verifyCaptcha(request.getCaptchaUserToken(), httpRequest);
    }

    try {
      SecurityCreateAuthResponse securityCreateAuthResponse = securityManager.create(request);
      response = createSuccessApiResponse(securityCreateAuthResponse, CREATED);
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case MANAGER_ACCOUNT_CREDENTIALS_INVALID:
          throw new ApiException(FORBIDDEN, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account signed in", null, getSignedInAccount());
    LOG.trace("response: {}", response);
    return response;
  }

  /**
   * Delete security authentication (sign-out)
   */
  @DeleteMapping(path = {"/auth", "/auth/"})
  public SuccessApiResponse delete(HttpServletRequest httpRequest) {
    LOG.trace("{}.delete()", SecurityController.class::getSimpleName);
    SuccessApiResponse response;

    Account signedInAccount = getSignedInAccount();

    securityManager.delete();
    response = createSuccessApiResponse(messageSource, CRUD_DELETE_SUCCESS);

    createEventLog(httpRequest, "Account signed out", null, signedInAccount);
    LOG.trace("response: {}", response);
    return response;
  }

  @Override
  public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Authentication authentication) {
    LOG.trace("{}.logout()", SecurityController.class::getSimpleName);
    LOG.trace("{} is of type: {}", Authentication.class::getSimpleName, () -> authentication.getClass().getSimpleName());
    delete(httpRequest);
  }

}
