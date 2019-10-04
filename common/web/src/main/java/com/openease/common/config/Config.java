package com.openease.common.config;

import com.openease.common.Env;
import com.openease.common.web.api.ApiVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.openease.common.Env.local;

/**
 * Class definition of the <code>config</code> object inside the <code>application*.yaml</code> configuration files
 *
 * @author Alan Czajkowski
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "config")
public class Config {

  private static final transient Logger LOG = LogManager.getLogger(Config.class);

  private static Env env;

  private String name;
  private String description;
  private String simpleName;
  private String url;
  private String groupId;
  private String artifactId;
  private String version;
  private Integer buildDateYear;
  private String encoding;

  private Vendor vendor;

  private SystemProperties systemProperties;

  private Boolean mask5xxErrors;

  private Filters filters;

  public String getName() {
    return name;
  }

  public Config setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Config setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public Config setSimpleName(String simpleName) {
    this.simpleName = simpleName;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public Config setUrl(String url) {
    this.url = url;
    return this;
  }

  public String getGroupId() {
    return groupId;
  }

  public Config setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public Config setArtifactId(String artifactId) {
    this.artifactId = artifactId;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public Config setVersion(String version) {
    this.version = version;
    return this;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public Config setVendor(Vendor vendor) {
    this.vendor = vendor;
    return this;
  }

  public SystemProperties getSystemProperties() {
    return systemProperties;
  }

  public Config setSystemProperties(SystemProperties systemProperties) {
    this.systemProperties = systemProperties;
    return this;
  }

  public Integer getBuildDateYear() {
    return buildDateYear;
  }

  public Config setBuildDateYear(Integer buildDateYear) {
    this.buildDateYear = buildDateYear;
    return this;
  }

  public String getEncoding() {
    return encoding;
  }

  public Config setEncoding(String encoding) {
    this.encoding = encoding;
    return this;
  }

  public Boolean getMask5xxErrors() {
    return mask5xxErrors;
  }

  public Config setMask5xxErrors(Boolean mask5xxErrors) {
    this.mask5xxErrors = mask5xxErrors;
    return this;
  }

  public Filters getFilters() {
    return filters;
  }

  public Config setFilters(Filters filters) {
    this.filters = filters;
    return this;
  }

  public static class SystemProperties {
    private String environmentKey;
    private String configLocationKey;

    public String getEnvironmentKey() {
      return environmentKey;
    }

    public SystemProperties setEnvironmentKey(String environmentKey) {
      this.environmentKey = environmentKey;
      return this;
    }

    public String getConfigLocationKey() {
      return configLocationKey;
    }

    public SystemProperties setConfigLocationKey(String configLocationKey) {
      this.configLocationKey = configLocationKey;
      return this;
    }
  }

  public static class Vendor {
    private String name;
    private String simpleName;
    private String url;
    private String logoAsciiResourcePath;

    public String getName() {
      return name;
    }

    public Vendor setName(String name) {
      this.name = name;
      return this;
    }

    public String getSimpleName() {
      return simpleName;
    }

    public Vendor setSimpleName(String simpleName) {
      this.simpleName = simpleName;
      return this;
    }

    public String getUrl() {
      return url;
    }

    public Vendor setUrl(String url) {
      this.url = url;
      return this;
    }

    public String getLogoAsciiResourcePath() {
      return logoAsciiResourcePath;
    }

    public Vendor setLogoAsciiResourcePath(String logoAsciiResourcePath) {
      this.logoAsciiResourcePath = logoAsciiResourcePath;
      return this;
    }
  }

  public static class Filters {
    private Debug debug;

    public Debug getDebug() {
      return debug;
    }

    public Filters setDebug(Debug debug) {
      this.debug = debug;
      return this;
    }
  }

  public static class Debug {
    private Boolean enabled;

    public Boolean getEnabled() {
      return enabled;
    }

    public Debug setEnabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }
  }

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("Environment system property key: {}", () -> getSystemProperties().getEnvironmentKey());
    // get environment name (default to LOCAL)
    final String envName = System.getProperty(getSystemProperties().getEnvironmentKey(), local.toString());
    try {
      env = Env.valueOf(envName);
    } catch (IllegalArgumentException iae) {
      LOG.warn("{}: {}", () -> iae.getClass().getSimpleName(), iae::getMessage);
      // default environment to LOCAL
      env = local;
    }
    LOG.debug("Environment: {}", env);

    LOG.debug("Init finished");
  }

  public Env getEnv() {
    return env;
  }

  public String getFullyQualifiedUrl(String relativeUrl) {
    return getUrl() + relativeUrl;
  }

  public String getUrlWithCurrentApiVersion() {
    return getFullyQualifiedUrl(ApiVersion.Constants.CURRENT_VERSION_CONTEXT);
  }

}
