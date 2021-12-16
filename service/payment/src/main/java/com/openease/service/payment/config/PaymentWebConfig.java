package com.openease.service.payment.config;

import com.openease.common.web.mvc.BaseWebConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;

/**
 * Web config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebMvc
@DependsOn({"paymentDataConfig"})
public class PaymentWebConfig extends BaseWebConfig {

  private static final transient Logger LOG = LogManager.getLogger(PaymentWebConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

}
