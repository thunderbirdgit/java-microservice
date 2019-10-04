package com.openease.common.util.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Bean locator
 * - locates Spring beans
 *
 * @author Alan Czajkowski
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class BeanLocator implements ApplicationContextAware {

  private static final transient Logger LOG = LogManager.getLogger(BeanLocator.class);

  private static ApplicationContext applicationContext = null;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("{} loaded: {}", ApplicationContext.class::getSimpleName, () -> (applicationContext != null));
    LOG.debug("Init finished");
  }

  public static <T> T byClass(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

  public static <T> T byClassAndName(Class<T> clazz, String name) {
    return applicationContext.getBean(name, clazz);
  }

  public static <T> Collection<T> allByClass(Class<T> clazz) {
    return applicationContext.getBeansOfType(clazz).values();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    BeanLocator.applicationContext = applicationContext;
  }

}
