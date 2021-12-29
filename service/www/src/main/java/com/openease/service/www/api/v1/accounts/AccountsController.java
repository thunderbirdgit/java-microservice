package com.openease.service.www.api.v1.accounts;

import com.openease.common.data.model.account.Account;
import com.openease.common.data.model.account.AccountScrubbed;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.account.request.AccountCheckEmailAvailableRequest;
import com.openease.common.manager.account.request.AccountCreateRequest;
import com.openease.common.manager.account.request.AccountResetPasswordRequest;
import com.openease.common.manager.account.request.AccountSendPasswordResetCodeRequest;
import com.openease.common.manager.account.request.AccountUpdatePasswordRequest;
import com.openease.common.manager.account.response.AccountUpdateResponse;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.jwt.JwtManager;
import com.openease.common.manager.security.SecurityManager;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.openease.common.data.lang.MessageKeys.CRUD_DISABLE_FAILURE;
import static com.openease.common.data.lang.MessageKeys.CRUD_DISABLE_SUCCESS;
import static com.openease.common.data.lang.MessageKeys.CRUD_NOTFOUND;
import static com.openease.common.data.lang.MessageKeys.CRUD_UPDATE_STALE;
import static com.openease.common.data.lang.MessageKeys.CRUD_UPDATE_SUCCESS;
import static com.openease.common.data.model.account.Account.ACCOUNTS;
import static com.openease.common.data.model.base.BaseDataModel.ID_REGEX_RELAXED;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_CREDENTIALS_INVALID;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_DISABLED;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_LOCKED;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_ACCOUNT_USERNAME_UNAVAILABLE;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.ApiVersion.Constants.V1_CONTEXT;
import static com.openease.common.web.util.ApiUtils.checkIdMatch;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.www.api.v1.accounts.AccountsController.ACCOUNTS_CONTEXT;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Accounts controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = V1_CONTEXT + ACCOUNTS_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class AccountsController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(AccountsController.class);

  public static final String ACCOUNTS_CONTEXT = "/" + ACCOUNTS;

  private static final String RESETPASSWORD_CONTEXT = "/_resetPassword";
  private static final String RESETPASSWORD_CONTEXT_ABSOLUTE = ACCOUNTS_CONTEXT + RESETPASSWORD_CONTEXT;

  @Autowired
  private AccountManager accountManager;

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private JwtManager jwtManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Create account (sign-up)
   */
  @PostMapping(path = {"", "/"})
  public SuccessApiResponse<AccountScrubbed> create(@RequestBody @Valid AccountCreateRequest request, HttpServletRequest httpRequest) {
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse<AccountScrubbed> response;

    verifyCaptcha(request.getCaptchaUserToken(), httpRequest);

    Account account;
    try {
      account = accountManager.create(request);
      response = createSuccessApiResponse(accountManager.scrub(account), CREATED);
    } catch (GeneralManagerException me) {
      throw new ApiException(me);
    }

    createEventLog(httpRequest, "Account created", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/me")
  public SuccessApiResponse read(HttpServletRequest httpRequest) {
    String accountId = securityManager.getSignedInAccountId();
    return read(accountId, httpRequest);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping(path = "/{id:" + ID_REGEX_RELAXED + "}")
  public SuccessApiResponse<AccountScrubbed> read(@PathVariable String id, HttpServletRequest httpRequest) {
    LOG.trace("id: {}", id);
    if (id == null) {
      throw new ApiException(NOT_FOUND, CRUD_NOTFOUND);
    }

    checkIdMatch(id, securityManager.getSignedInAccountId());

    SuccessApiResponse<AccountScrubbed> response;

    Account account;
    try {
      account = accountManager.read(id);
      response = createSuccessApiResponse(accountManager.scrub(account));
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account read", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping(path = "/{id:" + ID_REGEX_RELAXED + "}")
  public SuccessApiResponse<AccountUpdateResponse> update(@PathVariable String id, @RequestBody @Valid AccountScrubbed accountScrubbed, HttpServletRequest httpRequest) {
    LOG.trace("id: {}", id);
    LOG.trace("accountScrubbed: {}", () -> (accountScrubbed == null ? null : accountScrubbed.toStringUsingMixIn()));

    checkIdMatch(id, securityManager.getSignedInAccountId());
    checkIdMatch(id, accountScrubbed.getId());

    SuccessApiResponse<AccountUpdateResponse> response;

    Account account;
    try {
      account = accountManager.update(accountScrubbed);

      LOG.debug("Update security context with new authentication");
      AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
      //TODO: authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
      LOG.trace("Updating authentication in security context: {}", () -> (authentication == null ? null : authentication.getClass().getSimpleName()));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      LOG.debug("Creating JWT for account username: [{}]", account::getUsername);
      //TODO: throw GeneralManagerException
      String jwt = jwtManager.createJwt(authentication);
      if (jwt == null) {
        throw new RuntimeException("Sorry! Unable to create JWT");
      }

      AccountUpdateResponse accountUpdateResponse = new AccountUpdateResponse()
          .setJwt(jwt)
          .setAccount(accountManager.scrub(account));

      response = createSuccessApiResponse(accountUpdateResponse, messageSource, CRUD_UPDATE_SUCCESS);
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case MANAGER_ACCOUNT_DISABLED:
        case MANAGER_ACCOUNT_LOCKED:
        case MANAGER_ACCOUNT_CREDENTIALS_INVALID:
          throw new ApiException(FORBIDDEN, me);
        case CRUD_UPDATE_STALE:
          throw new ApiException(CONFLICT, me);
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account updated", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(path = "/{id:" + ID_REGEX_RELAXED + "}")
  public SuccessApiResponse<AccountScrubbed> disable(@PathVariable String id, HttpServletRequest httpRequest) {
    LOG.trace("id: {}", id);

    checkIdMatch(id, securityManager.getSignedInAccountId());

    SuccessApiResponse<AccountScrubbed> response;

    Account account;
    try {
      account = securityManager.getSignedInAccount();
      account = accountManager.disable(account);
      response = createSuccessApiResponse(accountManager.scrub(account), messageSource, CRUD_DISABLE_SUCCESS);
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case CRUD_DISABLE_FAILURE:
          throw new ApiException(CONFLICT, me);
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account disabled", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(path = "/{id:" + ID_REGEX_RELAXED + "}/_sendVerificationCode")
  public SuccessApiResponse sendVerificationCode(@PathVariable String id, HttpServletRequest httpRequest) {
    checkIdMatch(id, securityManager.getSignedInAccountId());

    SuccessApiResponse response;

    Account account;
    try {
      account = securityManager.getSignedInAccount();
      accountManager.sendVerificationCode(account);
      response = createSuccessApiResponse();
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account sent verification code", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(path = "/{id:" + ID_REGEX_RELAXED + "}/_updatePassword")
  public SuccessApiResponse<AccountUpdateResponse> updatePassword(@PathVariable String id, @RequestBody @Valid AccountUpdatePasswordRequest request, HttpServletRequest httpRequest) {
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }

    checkIdMatch(id, securityManager.getSignedInAccountId());

    SuccessApiResponse<AccountUpdateResponse> response;

    Account account;
    try {
      account = securityManager.getSignedInAccount();
      accountManager.updatePassword(account, request);
      accountManager.verifyPassword(account, request.getNewPassword());

      LOG.debug("Update security context with new authentication");
      AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
      //TODO: authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
      LOG.trace("Updating authentication in security context: {}", () -> (authentication == null ? null : authentication.getClass().getSimpleName()));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      LOG.debug("Creating JWT for account username: [{}]", account::getUsername);
      //TODO: throw GeneralManagerException
      String jwt = jwtManager.createJwt(authentication);
      if (jwt == null) {
        throw new RuntimeException("Sorry! Unable to create JWT");
      }

      AccountUpdateResponse accountUpdateResponse = new AccountUpdateResponse()
          .setJwt(jwt)
          .setAccount(accountManager.scrub(account));

      response = createSuccessApiResponse(accountUpdateResponse, messageSource, CRUD_UPDATE_SUCCESS);
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case MANAGER_ACCOUNT_DISABLED:
        case MANAGER_ACCOUNT_LOCKED:
        case MANAGER_ACCOUNT_CREDENTIALS_INVALID:
          throw new ApiException(FORBIDDEN, me);
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account updated password", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PostMapping(path = "/_checkEmailAvailable")
  public SuccessApiResponse checkEmailAvailable(@RequestBody @Valid AccountCheckEmailAvailableRequest request, HttpServletRequest httpRequest) {
    LOG.trace("request: {}", request);
    if (request == null) {
      throw new ApiException();
    }

    boolean emailAvailable = accountManager.checkEmailAvailable(request);
    if (!emailAvailable) {
      throw new ApiException(MANAGER_ACCOUNT_USERNAME_UNAVAILABLE);
    }

    SuccessApiResponse response = createSuccessApiResponse();

    createEventLog(httpRequest, "Checked username (email) available", request.getEmail(), null);
    LOG.trace("response: {}", response);
    return response;
  }

  @PostMapping(path = "/_sendPasswordResetCode")
  public SuccessApiResponse sendPasswordResetCode(@RequestBody @Valid AccountSendPasswordResetCodeRequest request, HttpServletRequest httpRequest) {
    LOG.trace("request: {}", request);
    if (request == null) {
      throw new ApiException();
    }

    verifyCaptcha(request.getCaptchaUserToken(), httpRequest);

    SuccessApiResponse response;

    Account account = null;
    try {
      account = accountManager.sendPasswordResetCode(request);
      response = createSuccessApiResponse();
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case CRUD_NOTFOUND:
          LOG.debug("account *not* found");
          /* commented-out because it allows malicious actors to do harvesting */
//          throw new ApiException(NOT_FOUND, me);
          response = createSuccessApiResponse();
          break;
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account sent password reset code", request.getEmail(), account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PostMapping(path = RESETPASSWORD_CONTEXT)
  public SuccessApiResponse resetPassword(@RequestBody @Valid AccountResetPasswordRequest request, HttpServletRequest httpRequest) {
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse response;

    Account account;
    try {
      account = accountManager.resetPassword(request);
      response = createSuccessApiResponse();
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

    createEventLog(httpRequest, "Account reset password", null, account);
    LOG.trace("response: {}", response);
    return response;
  }

  @PostMapping(path = RESETPASSWORD_CONTEXT + "_basic", consumes = APPLICATION_FORM_URLENCODED_VALUE)
  public SuccessApiResponse resetPasswordBasic(@ModelAttribute(value = "request") @Valid AccountResetPasswordRequest request, HttpServletRequest httpRequest) {
    return resetPassword(request, httpRequest);
  }

  public String getResetPasswordApiUrl() {
    return config.getUrlWithCurrentApiVersion() + RESETPASSWORD_CONTEXT_ABSOLUTE;
  }

}
