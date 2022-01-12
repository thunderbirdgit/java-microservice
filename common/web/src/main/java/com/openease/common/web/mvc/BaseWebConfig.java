package com.openease.common.web.mvc;

import com.openease.common.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.openease.common.web.util.AssetUtils.FRONTEND_ASSET_CLASSPATH;
import static com.openease.common.web.util.AssetUtils.STATIC_ASSET_CLASSPATH;

/**
 * Base web config
 *
 * @author Alan Czajkowski
 */
public abstract class BaseWebConfig implements WebMvcConfigurer, ApplicationContextAware {

  private static final transient Logger LOG = LogManager.getLogger(BaseWebConfig.class);

  @Autowired
  private Config config;

  @Autowired
  @Qualifier("messageSource")
  protected MessageSource messageSource;

  private ApplicationContext applicationContext;

  public void init() {
    LOG.debug("{} loaded: {}", MessageSource.class::getSimpleName, () -> (messageSource != null));
    LOG.debug("{} loaded: {}", ApplicationContext.class::getSimpleName, () -> (applicationContext != null));
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(config.getCors().getAllowedOrigins().toArray(String[]::new))
        .allowedOriginPatterns(config.getCors().getAllowedOriginPatterns().toArray(String[]::new))
        .allowedMethods(config.getCors().getAllowedMethods().toArray(String[]::new))
        .allowedHeaders(config.getCors().getAllowedHeaders().toArray(String[]::new))
        .exposedHeaders(config.getCors().getExposedHeaders().toArray(String[]::new))
        .allowCredentials(config.getCors().isAllowCredentials())
        .maxAge(config.getCors().getMaxAgeSeconds());
  }

  /**
   * {@link Validator} bean for API request objects
   *
   * @return {@link Validator}
   */
  @Override
  public Validator getValidator() {
    LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
    localValidatorFactoryBean.setValidationMessageSource(messageSource);
    return localValidatorFactoryBean;
  }

  /**
   * Add handler for static asset resources
   *
   * @param registry {@link ResourceHandlerRegistry}
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
        // static assets location
        .addResourceLocations("classpath:" + STATIC_ASSET_CLASSPATH + "/")
        // front-end UI location
        .addResourceLocations("classpath:" + FRONTEND_ASSET_CLASSPATH + "/");
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

}
