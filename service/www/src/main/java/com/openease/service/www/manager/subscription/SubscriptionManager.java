package com.openease.service.www.manager.subscription;

import com.openease.common.data.model.account.Tier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.openease.common.data.model.account.Tier.TIER0;
import static com.openease.common.util.JsonUtils.toJson;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Subscription manager
 *
 * @author Alan Czajkowski
 */
@Service
public class SubscriptionManager {

  private static final transient Logger LOG = LogManager.getLogger(SubscriptionManager.class);

  @Autowired
  private Config config;

  @Autowired
  @Qualifier("messageSource")
  private MessageSource messageSource;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("{} loaded: {}", MessageSource.class::getSimpleName, () -> (messageSource != null));
    LOG.debug("plans: {}", () -> toJson(getPlans(), true));
    LOG.debug("Init finished");
  }

  public List<Plan> getPlans() {
    return config.getPlans();
  }

  @Configuration
  @ConfigurationProperties(prefix = "manager.subscription")
  protected class Config {
    private List<Plan> plans;

    public List<Plan> getPlans() {
      return plans;
    }

    public Config setPlans(List<Plan> plans) {
      this.plans = plans;
      return this;
    }
  }

  public Plan findPlanByTier(Tier tier) {
    Optional<Plan> optionalPlan = config.getPlans()
        .stream()
        .filter(p -> p.getTier().equals(tier))
        .findFirst();
    return optionalPlan.orElse(null);
  }

  public Plan findPlanByTier(String tierName) {
    Tier tier = TIER0;
    if (isNotBlank(tierName)) {
      try {
        tier = Tier.valueOf(tierName);
      } catch (IllegalArgumentException iae) {
        // ignore
      }
    }
    return findPlanByTier(tier);
  }

}
