package com.openease.service.www.api.enumeration;

import com.openease.common.data.model.account.Gender;
import com.openease.common.data.model.account.OAuth2Provider;
import com.openease.common.data.model.account.Tier;
import com.openease.common.web.api.ApiVersion;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.common.web.api.enumeration.Region;
import com.openease.common.web.util.ApiUtils;
import com.openease.service.www.manager.subscription.Plan;
import com.openease.service.www.manager.subscription.SubscriptionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.service.www.api.enumeration.EnumsController.ENUMS_CONTEXT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Enums controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = ENUMS_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class EnumsController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(EnumsController.class);

  public static final String ENUMS = "enums";
  public static final String ENUMS_CONTEXT = "/" + ENUMS;

  public static final String[] ENDPOINTS = {
      ENUMS_CONTEXT + "/api-versions",
      ENUMS_CONTEXT + "/regions",
      ENUMS_CONTEXT + "/oauth2/providers",
      ENUMS_CONTEXT + "/account/genders",
      ENUMS_CONTEXT + "/account/tiers",
      ENUMS_CONTEXT + "/account/subscription/plans"
  };

  @Autowired
  private SubscriptionManager subscriptionManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  @GetMapping(path = {"", "/"})
  public SuccessApiResponse<String[]> getEndpoints() {
    SuccessApiResponse<String[]> response = ApiUtils.createSuccessApiResponse(ENDPOINTS);
    LOG.debug("response: {}", response);
    return response;
  }

  @GetMapping(path = "/api-versions")
  public SuccessApiResponse<ApiVersion[]> getApiVersions() {
    SuccessApiResponse<ApiVersion[]> response = ApiUtils.createSuccessApiResponse(ApiVersion.values());
    LOG.debug("response: {}", response);
    return response;
  }

  @GetMapping(path = "/regions")
  public SuccessApiResponse<List<Map<String, Object>>> getRegions() {
    SuccessApiResponse<List<Map<String, Object>>> response = ApiUtils.createSuccessApiResponse(Region.regions());
    LOG.debug("response: {}", response);
    return response;
  }

  @GetMapping(path = "/oauth2/providers")
  public SuccessApiResponse<OAuth2Provider[]> getOAuth2Providers() {
    SuccessApiResponse<OAuth2Provider[]> response = ApiUtils.createSuccessApiResponse(OAuth2Provider.values());
    LOG.debug("response: {}", response);
    return response;
  }

  @GetMapping(path = "/account/genders")
  public SuccessApiResponse<Gender[]> getAccountGenders() {
    SuccessApiResponse<Gender[]> response = ApiUtils.createSuccessApiResponse(Gender.values());
    LOG.debug("response: {}", response);
    return response;
  }

  @GetMapping(path = "/account/tiers")
  public SuccessApiResponse<Tier[]> getAccountTiers() {
    SuccessApiResponse<Tier[]> response = ApiUtils.createSuccessApiResponse(Tier.values());
    LOG.debug("response: {}", response);
    return response;
  }

  @GetMapping(path = "/account/subscription/plans")
  public SuccessApiResponse<List<Plan>> getAccountSubscriptionPlans() {
    SuccessApiResponse<List<Plan>> response = ApiUtils.createSuccessApiResponse(subscriptionManager.getPlans());
    LOG.debug("response: {}", response);
    return response;
  }

}
