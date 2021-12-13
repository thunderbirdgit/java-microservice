package com.openease.common.config;

import com.openease.common.Env;
import com.openease.common.web.api.ApiVersion;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.openease.common.Env.local;

/**
 * Class definition of the <code>config</code> object inside the
 * <code>application*.yaml</code> configuration files
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

  private boolean mask5xxErrors;

  private Filters filters;

  private Cors cors;

  private Auth auth;

  public Config() {
    this.mask5xxErrors = true;
  }

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

  public boolean getMask5xxErrors() {
    return mask5xxErrors;
  }

  public Config setMask5xxErrors(boolean mask5xxErrors) {
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

  public Cors getCors() {
    return cors;
  }

  public Config setCors(Cors cors) {
    this.cors = cors;
    return this;
  }

  public Auth getAuth() {
    return auth;
  }

  public Config setAuth(Auth auth) {
    this.auth = auth;
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
    private boolean enabled;

    public Debug() {
      this.enabled = false;
    }

    public boolean getEnabled() {
      return enabled;
    }

    public Debug setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }
  }

  public static class Cors {
    /**
     * Comma-separated list of origins to allow. '*' allows all origins.
     * When credentials are allowed, '*' cannot be used and origin patterns
     * should be configured instead. When no allowed origins (or allowed origin
     * patterns) are set, CORS support is disabled.
     */
    private List<String> allowedOrigins;

    /**
     * Comma-separated list of origin patterns to allow. Unlike allowed origins
     * which only supports '*', origin patterns are more flexible (for example:
     * 'https://*.example.com') and can be used when credentials are allowed.
     * When no allowed origin patterns (or allowed origins) are set, CORS
     * support is disabled.
     */
    private List<String> allowedOriginPatterns;

    /**
     * Comma-separated list of methods to allow. '*' allows all methods.
     * When not set, defaults to GET.
     */
    private List<String> allowedMethods;

    /**
     * Comma-separated list of headers to allow in a request. '*' allows all headers.
     */
    private List<String> allowedHeaders;

    /**
     * Comma-separated list of headers to include in a response.
     */
    private List<String> exposedHeaders;

    /**
     * Whether credentials are supported.
     */
    private boolean allowCredentials;

    /**
     * How long in seconds the response from a pre-flight request can be cached by clients.
     */
    private long maxAgeSeconds;

    public Cors() {
      this.allowedOrigins = new ArrayList<>();
      this.allowedOriginPatterns = new ArrayList<>();
      this.allowedMethods = new ArrayList<>();
      this.allowedHeaders = new ArrayList<>();
      this.exposedHeaders = new ArrayList<>();
      this.allowCredentials = false;
      this.maxAgeSeconds = Duration.ofMinutes(30).toSeconds();
    }

    public List<String> getAllowedOrigins() {
      return allowedOrigins;
    }

    public Cors setAllowedOrigins(List<String> allowedOrigins) {
      this.allowedOrigins = allowedOrigins;
      return this;
    }

    public List<String> getAllowedOriginPatterns() {
      return allowedOriginPatterns;
    }

    public Cors setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
      this.allowedOriginPatterns = allowedOriginPatterns;
      return this;
    }

    public List<String> getAllowedMethods() {
      return allowedMethods;
    }

    public Cors setAllowedMethods(List<String> allowedMethods) {
      this.allowedMethods = allowedMethods;
      return this;
    }

    public List<String> getAllowedHeaders() {
      return allowedHeaders;
    }

    public Cors setAllowedHeaders(List<String> allowedHeaders) {
      this.allowedHeaders = allowedHeaders;
      return this;
    }

    public List<String> getExposedHeaders() {
      return exposedHeaders;
    }

    public Cors setExposedHeaders(List<String> exposedHeaders) {
      this.exposedHeaders = exposedHeaders;
      return this;
    }

    public boolean isAllowCredentials() {
      return allowCredentials;
    }

    public Cors setAllowCredentials(boolean allowCredentials) {
      this.allowCredentials = allowCredentials;
      return this;
    }

    public long getMaxAgeSeconds() {
      return maxAgeSeconds;
    }

    public Cors setMaxAgeSeconds(long maxAgeSeconds) {
      this.maxAgeSeconds = maxAgeSeconds;
      return this;
    }
  }

  public static class Auth {
    private SignatureAlgorithm jwtSignatureAlgorithm;
    private String jwtSecretBase64;
    private long jwtExpirationSeconds;
    private OAuth2 oAuth2;

    public Auth() {
      this.jwtExpirationSeconds = 0;
    }

    public SignatureAlgorithm getJwtSignatureAlgorithm() {
      return jwtSignatureAlgorithm;
    }

    public Auth setJwtSignatureAlgorithm(SignatureAlgorithm jwtSignatureAlgorithm) {
      this.jwtSignatureAlgorithm = jwtSignatureAlgorithm;
      return this;
    }

    public String getJwtSecretBase64() {
      return jwtSecretBase64;
    }

    public Auth setJwtSecretBase64(String jwtSecretBase64) {
      this.jwtSecretBase64 = jwtSecretBase64;
      return this;
    }

    public long getJwtExpirationSeconds() {
      return jwtExpirationSeconds;
    }

    public Auth setJwtExpirationSeconds(long jwtExpirationSeconds) {
      this.jwtExpirationSeconds = jwtExpirationSeconds;
      return this;
    }

    public OAuth2 getOAuth2() {
      return oAuth2;
    }

    public Auth setOAuth2(OAuth2 oAuth2) {
      this.oAuth2 = oAuth2;
      return this;
    }
  }

  public static class OAuth2 {
    private List<String> authorizedRedirectUris;

    public List<String> getAuthorizedRedirectUris() {
      return authorizedRedirectUris;
    }

    public OAuth2 setAuthorizedRedirectUris(List<String> authorizedRedirectUris) {
      this.authorizedRedirectUris = authorizedRedirectUris;
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
