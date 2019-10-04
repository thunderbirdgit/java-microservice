package com.openease.service.www.config;

import com.openease.common.web.security.BaseSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.annotation.PostConstruct;

/**
 * Security config
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableWebSecurity
@DependsOn({"wwwDataConfig"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WwwSecurityConfig extends BaseSecurityConfig {

  private static final transient Logger LOG = LogManager.getLogger(WwwSecurityConfig.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    super.configure(http);
//    http.authorizeRequests()
//        .antMatchers(
//            "/",
//            "/index.html",
//            "/error",
//            "**/static/**",
//            "**/js/**",
//            "**/css/**",
//            "**/img/**",
//            "**/media/**",
//            "**/*.ico"
//        ).permitAll()
//        .anyRequest().authenticated().and()
//        .oauth2Login();
//  }

}
