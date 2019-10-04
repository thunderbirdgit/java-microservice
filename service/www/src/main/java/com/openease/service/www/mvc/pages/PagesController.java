package com.openease.service.www.mvc.pages;

import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.account.request.AccountResetPasswordRequest;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.page.PageManager;
import com.openease.common.web.mvc.base.BaseMvcController;
import com.openease.common.web.mvc.base.exception.MvcException;
import com.openease.service.www.api.v1.accounts.AccountsController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import static com.openease.common.manager.page.PageManager.PAGES_CONTEXT;
import static com.openease.common.manager.page.PageManager.PASSWORDRESET_CONTEXT;
import static com.openease.common.manager.page.PageManager.VERIFICATION_CONTEXT;
import static com.openease.common.util.JsonUtils.toJson;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * Pages controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = PAGES_CONTEXT, produces = TEXT_HTML_VALUE)
public class PagesController extends BaseMvcController {

  private static final transient Logger LOG = LogManager.getLogger(PagesController.class);

  @Autowired
  private PageManager pageManager;

  @Autowired
  private AccountManager accountManager;

  @Autowired
  private AccountsController accountsController;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  @GetMapping(path = VERIFICATION_CONTEXT + "/{code}")
  public String verify(@PathVariable String code, Model model, HttpServletResponse httpResponse) {
    String responseView = PAGES_CONTEXT + VERIFICATION_CONTEXT;

    LOG.debug("verificationCode: {}", code);
    model.addAttribute("verificationCode", code);

    boolean codeFound;
    try {
      codeFound = accountManager.verify(code);
    } catch (GeneralManagerException me) {
      LOG.error(me::getMessage, me);
      throw new MvcException(INTERNAL_SERVER_ERROR);
    }
    LOG.debug("codeFound: {}", codeFound);
    model.addAttribute("codeFound", codeFound);

    /* commented-out because it allows malicious actors to do harvesting */
//    if (!codeFound) {
//      httpResponse.setStatus(SC_NOT_FOUND);
//    }

    return responseView;
  }

  @GetMapping(path = PASSWORDRESET_CONTEXT + "/{code}")
  public String resetPassword(@PathVariable String code, Model model, HttpServletResponse httpResponse) {
    String responseView = PAGES_CONTEXT + PASSWORDRESET_CONTEXT;

    LOG.debug("passwordResetCode: {}", code == null ? null : (substring(code, 0, 5) + "..." + substring(code, -5)));
    model.addAttribute("passwordResetCode", code);

    String apiCall = accountsController.getResetPasswordApiUrl();
    LOG.debug("apiCall: {}", apiCall);
    model.addAttribute("apiCall", apiCall);

    boolean codeFound = accountManager.findPasswordResetCode(code);
    LOG.debug("codeFound: {}", codeFound);
    model.addAttribute("codeFound", codeFound);

    model.addAttribute("request", new AccountResetPasswordRequest().setPasswordResetCode(code));

    /* commented-out because it allows malicious actors to do harvesting */
//    if (!codeFound) {
//      httpResponse.setStatus(SC_NOT_FOUND);
//    }

    return responseView;
  }

}
