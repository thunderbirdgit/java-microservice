package com.openease.service.sms;

import com.openease.common.web.BaseWebApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * SMS web application
 *
 * @author Alan Czajkowski
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan({
    "com.openease.common.util",
    "com.openease.common.config",
    "com.openease.common.web.lang",
    "com.openease.common.service.elasticsearch",
    "com.openease.common.manager.info",
    "com.openease.common.web.filter",
    "com.openease.common.web.task",
    "com.openease.common.web.mvc.error",
    "com.openease.common.web.api.information",
    "com.openease.service.sms"
})
public class SmsWebApplication extends BaseWebApplication {

  public static void main(String... args) {
    start(SmsWebApplication.class, args);
  }

}
