package com.openease.common.web;

import com.openease.common.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.openease.common.util.JsonPasswordSerializer.MASK;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.util.ResourceUtils.readResourceIntoString;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.containsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Base web application
 * - Spring Boot main driver
 *
 * @author Alan Czajkowski
 */
public abstract class BaseWebApplication extends SpringBootServletInitializer implements ApplicationRunner, ApplicationContextAware {

  private static final transient Logger LOG = LogManager.getLogger(BaseWebApplication.class);

  private static final String INDENT2 = "  ";
  private static final String INDENT4 = INDENT2 + INDENT2;

  private static ApplicationContext applicationContext;

  @Autowired
  private Environment environment;

  @Autowired
  private Config config;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    LOG.trace("class: {}", () -> this.getClass().getSimpleName());
    return application.sources(this.getClass());
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LOG.debug("Application Arguments:{}{}", System::lineSeparator, () -> getArgsAsString(args));
    LOG.debug("Environment Variables:{}{}", System::lineSeparator, () -> getMapAsString(((AbstractEnvironment) environment).getSystemEnvironment()));
    LOG.debug("System Properties:{}{}", System::lineSeparator, () -> getMapAsString(((AbstractEnvironment) environment).getSystemProperties()));
    LOG.debug("Application Properties:{}{}", System::lineSeparator, () -> getEnvironmentAsString(environment));
    LOG.info("Application Information:{}{}{}", System::lineSeparator, System::lineSeparator, () -> getAppInfo(config, environment));
  }

  public static void start(Class clazz, String... args) {
    LOG.info("Starting up ...");
    applicationContext = SpringApplication.run(clazz, args);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Get serialization of {@link ApplicationArguments} keys and values as a {@link String}
   *
   * @param args {@link ApplicationArguments} to serialize
   *
   * @return serialization of {@link ApplicationArguments} keys and values
   */
  private static String getArgsAsString(ApplicationArguments args) {
    StringBuilder sb = new StringBuilder();

    if (!args.getOptionNames().isEmpty()) {
      for (String arg : args.getOptionNames()) {
        appendToStringBuilder(sb, INDENT2, arg, toJson(args.getOptionValues(arg)));
      }
    }

    return sb.toString();
  }

  /**
   * Get serialization of {@link Map} keys and values as a {@link String}
   *
   * @param map {@link Map} to serialize
   *
   * @return serialization of {@link Map} keys and values
   */
  private static String getMapAsString(Map<String, Object> map) {
    StringBuilder sb = new StringBuilder();

    Map<String, Object> sortedMap = new TreeMap<>();
    sortedMap.putAll(map);
    for (String key : sortedMap.keySet()) {
      appendToStringBuilder(sb, INDENT2, key, sortedMap.get(key));
    }

    return sb.toString();
  }

  /**
   * Get serialization of {@link Environment} property keys and values as a {@link String}
   *
   * @param environment Spring {@link Environment}
   *
   * @return serialization of {@link Environment} property keys and values
   */
  private static String getEnvironmentAsString(Environment environment) {
    StringBuilder sb = new StringBuilder();

    Map<String, String> sortedMap = new TreeMap<>();
    PropertySources propertySources = ((AbstractEnvironment) environment).getPropertySources();
    for (PropertySource propertySource : propertySources) {
      if (propertySource instanceof MapPropertySource && startsWithIgnoreCase(propertySource.getName(), "config resource")) {
        List<String> propertyKeys = Arrays.asList(((MapPropertySource) propertySource).getPropertyNames());
        for (String key : propertyKeys) {
          sortedMap.put(key, trim(environment.getProperty(key)));
        }
      }
    }

    for (String key : sortedMap.keySet()) {
      appendToStringBuilder(sb, INDENT2, key, sortedMap.get(key));
    }

    return sb.toString();
  }

  /**
   * Get application information
   *
   * @param config {@link Config}
   *
   * @return application information
   */
  private static String getAppInfo(Config config, Environment environment) {
    StringBuilder sb = new StringBuilder();

    String logoAscii = readResourceIntoString(config.getVendor().getLogoAsciiResourcePath());

    sb.append(logoAscii).append(lineSeparator());
    sb.append(INDENT2).append("Application:").append(lineSeparator());
    sb.append(INDENT4).append(config.getName()).append(lineSeparator());
    sb.append(INDENT4).append(config.getVersion()).append(lineSeparator());
    sb.append(INDENT2).append("Company:").append(lineSeparator());
    sb.append(INDENT4).append(config.getVendor().getName()).append(lineSeparator());
    sb.append(INDENT4).append(config.getVendor().getUrl()).append(lineSeparator());
    sb.append(lineSeparator());
    sb.append(INDENT2).append("Environment: ").append(config.getEnv()).append(lineSeparator());
    sb.append(INDENT2).append("Port: ").append(environment.getProperty("server.port")).append(lineSeparator());

    return sb.toString();
  }

  private static void appendToStringBuilder(StringBuilder sb, String prefix, String key, Object value) {
    if (containsAnyIgnoreCase(key, "password", "private", "secret")) {
      value = MASK;
    }
    sb.append(prefix).append(key).append("=").append(value).append(lineSeparator());
  }

}
