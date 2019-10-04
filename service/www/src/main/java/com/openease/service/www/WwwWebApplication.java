package com.openease.service.www;

import com.openease.common.web.BaseWebApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * WWW web application
 *
 * @author Alan Czajkowski
 */
@EnableAutoConfiguration
@EnableWebMvc
@SpringBootConfiguration
@ComponentScan({"com.openease.*"})
public class WwwWebApplication extends BaseWebApplication {

  public static void main(String... args) {
    start(WwwWebApplication.class, args);
  }

}
