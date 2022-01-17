package com.openease.service.www;

import com.openease.common.data.model.account.OAuth2Provider;
import com.openease.common.util.ResourceUtils;
import com.openease.common.web.BaseWebApplication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.openease.common.data.model.account.OAuth2Provider.OAUTH2_APPLE_CLIENT_SECRET;
import static com.openease.common.util.JsonUtils.toJson;

/**
 * WWW web application
 *
 * @author Alan Czajkowski
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan({
    "com.openease.*"
})
public class WwwWebApplication extends BaseWebApplication {

  private static final transient Logger LOG = LogManager.getLogger(WwwWebApplication.class);

  public static void main(String... args) {
    Properties oAuth2AppleProperties = createOAuth2AppleProperties();
    LOG.debug("oAuth2AppleProperties: {}", () -> toJson(oAuth2AppleProperties, true));
    start(WwwWebApplication.class, oAuth2AppleProperties, args);
  }

  /**
   * Creates a (dynamic) signed JSON Web Token (JWT) {@link OAUTH2_APPLE_CLIENT_SECRET}
   * for Apple OAuth 2.0 sign-in, to be injected into the (static) OAuth 2.0
   * property: {@code spring.security.oauth2.client.registration.apple.client-secret}
   * <b>application.yaml</b>:
   * <pre>
   *   spring:
   *     security:
   *       oauth2:
   *         client:
   *           registration:
   *             apple:
   *               client-secret: "${OAUTH2_APPLE_CLIENT_SECRET:...}"
   * </pre>
   * This dynamic JWT creation needs to done before the Spring Boot application
   * starts because it needs to be injected into Spring's static OAuth 2.0 run-time
   * bean configuration.
   *
   * @return Apple OAuth 2.0 sign-in {@link Properties} (containing client secret JWT)
   */
  @SuppressWarnings("unchecked")
  private static Properties createOAuth2AppleProperties() {
    BufferedReader applicationConfigReader = ResourceUtils.readResourceIntoBufferedReader("/application.yaml");
    Map<String, Object> applicationConfig = new Yaml().load(applicationConfigReader);
    Map<String, Object> config = (Map<String, Object>) applicationConfig.get("config");
    Map<String, Object> appleOAuth2Details = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) config.get("auth")).get("oauth2")).get("provider")).get(OAuth2Provider.APPLE.name());

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.valueOf(appleOAuth2Details.get("jwt-signature-algorithm").toString());

    PrivateKey privateKey = null;
    try (Reader pemReader = new StringReader(appleOAuth2Details.get("private-key").toString()); PEMParser pemParser = new PEMParser(pemReader)) {
      PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
      privateKey = new JcaPEMKeyConverter().getPrivateKey(object);
    } catch (IOException ioe) {
      LOG.error(ioe.getMessage(), ioe);
    }

    Map<String, Object> headers = new HashMap<>();
    headers.put("kid", appleOAuth2Details.get("key-id").toString());
    headers.put("alg", signatureAlgorithm);

    int jwtExpirationDays;
    try {
      String jwtExpirationDaysStr = appleOAuth2Details.get("jwt-expiration-days").toString();
      jwtExpirationDays = Integer.parseInt(jwtExpirationDaysStr);
    } catch (NumberFormatException nfe) {
      // default
      jwtExpirationDays = 180;
    }
    Date now = new Date();
    Date expiry = DateUtils.addDays(now, jwtExpirationDays);

    String clientSecret = Jwts.builder()
        .setHeader(headers)
        .setSubject(appleOAuth2Details.get("client-id").toString())
        .setIssuer(appleOAuth2Details.get("team-id").toString())
        .setAudience(appleOAuth2Details.get("audience").toString())
        .setIssuedAt(now)
        // set expiration to maximum 6 months ahead
        .setExpiration(expiry)
        .signWith(privateKey, signatureAlgorithm)
        .compact();

    Properties properties = new Properties();
    properties.setProperty(OAUTH2_APPLE_CLIENT_SECRET, clientSecret);
    return properties;
  }

}
