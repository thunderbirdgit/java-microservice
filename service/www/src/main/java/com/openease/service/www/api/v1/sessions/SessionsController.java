package com.openease.service.www.api.v1.sessions;

import com.openease.common.data.model.account.Account;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.session.SessionManager;
import com.openease.common.manager.session.request.SessionCreateRequest;
import com.openease.common.manager.session.response.SessionCreateResponse;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.openease.common.data.lang.MessageKeys.CRUD_DELETE_SUCCESS;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_CREDENTIALS_INVALID;
import static com.openease.common.manager.session.SessionManager.SESSIONS;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.ApiVersion.Constants.V1_CONTEXT;
import static com.openease.common.web.security.BaseSecurityConfig.getSignedInAccount;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.www.api.v1.sessions.SessionsController.SESSIONS_CONTEXT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Sessions controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = V1_CONTEXT + SESSIONS_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class SessionsController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(SessionsController.class);

  public static final String SESSIONS_CONTEXT = "/" + SESSIONS;

  @Autowired
  private SessionManager sessionManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Create session (sign-in)
   */
  @PostMapping(path = {"", "/"})
  public SuccessApiResponse<SessionCreateResponse> create(@RequestBody @Valid SessionCreateRequest request, HttpServletRequest httpRequest) {
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse<SessionCreateResponse> response;

    if (request.getCaptchaUserToken() != null) {
      verifyCaptcha(request.getCaptchaUserToken(), httpRequest);
    }

    try {
      SessionCreateResponse sessionCreateResponse = sessionManager.create(request);
      response = createSuccessApiResponse(sessionCreateResponse, CREATED);
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
   * Delete session (sign-out)
   */
  @DeleteMapping(path = {"", "/"})
  public SuccessApiResponse delete(HttpServletRequest httpRequest) {
    SuccessApiResponse response;

    Account signedInAccount = getSignedInAccount();

    sessionManager.delete();
    response = createSuccessApiResponse(messageSource, CRUD_DELETE_SUCCESS);

    createEventLog(httpRequest, "Account signed out", null, signedInAccount);
    LOG.trace("response: {}", response);
    return response;
  }

}
