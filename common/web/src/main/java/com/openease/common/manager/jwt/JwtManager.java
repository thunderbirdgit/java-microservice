package com.openease.common.manager.jwt;

import com.openease.common.config.Config;
import com.openease.common.data.model.account.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import static com.openease.common.util.JsonUtils.toJson;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * JSON Web Token (JWT) manager
 *
 * @author Alan Czajkowski
 */
@Service
public class JwtManager {

  private static final transient Logger LOG = LogManager.getLogger(JwtManager.class);

  @Autowired
  private Config config;

  public String createJwt(Authentication authentication) {
    String token = null;

    String username = null;
    if (authentication == null) {
      LOG.trace("No authentication provided");
    } else {
      LOG.debug("{} is of type: {}", Authentication.class::getSimpleName, () -> authentication.getClass().getSimpleName());
      Object principal = authentication.getPrincipal();
      if (principal != null) {
        LOG.debug("{}.principal is of type: {}", () -> authentication.getClass().getSimpleName(), () -> principal.getClass().getSimpleName());
        if (principal instanceof Account) { // local account
          Account account = (Account) principal;
          username = account.getUsername();
          LOG.trace("{}: {}", () -> account.getClass().getSimpleName(), account::toStringUsingMixIn);
        } else if (principal instanceof DefaultOidcUser) {  // Google account
          DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
          username = oidcUser.getEmail();
          LOG.trace("{}: {}", () -> oidcUser.getClass().getSimpleName(), () -> toJson(oidcUser, true));
        } else {
          LOG.warn("{}.principal is *not* of type: {}", () -> authentication.getClass().getSimpleName(), DefaultOidcUser.class::getSimpleName);
        }
      } else {
        LOG.warn("{}.principal is null", () -> authentication.getClass().getSimpleName());
      }
    }

    if (username != null) {
      long now = System.currentTimeMillis();
      long expiry = now + Duration.ofSeconds(config.getAuth().getJwtExpirationSeconds()).toMillis();
      byte[] keyBytes = Decoders.BASE64.decode(config.getAuth().getJwtSecretBase64());
      Key key = Keys.hmacShaKeyFor(keyBytes);
      token = Jwts.builder()
          // set JWT Claims sub (subject) value
          .setSubject(username)
          .setIssuedAt(new Date(now))
          .setExpiration(new Date(expiry))
          .signWith(key, config.getAuth().getJwtSignatureAlgorithm())
          .compact();
    }

    return token;
  }

  public String getUsernameFromJwt(String jwt) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(config.getAuth().getJwtSecretBase64())
        .build()
        .parseClaimsJws(jwt)
        .getBody();
    return claims.getSubject();
  }

  public boolean validateJwt(String jwt) {
    boolean jwtValid = false;

    if (isNotBlank(jwt)) {
      try {
        Jwts.parserBuilder()
            .setSigningKey(config.getAuth().getJwtSecretBase64())
            .build()
            .parseClaimsJws(jwt);
        jwtValid = true;
      } catch (SignatureException e) {
        LOG.error("Invalid JWT signature: {}", e::getLocalizedMessage);
      } catch (MalformedJwtException e) {
        LOG.error("Invalid JWT: {}", e::getLocalizedMessage);
      } catch (ExpiredJwtException e) {
        LOG.error("Expired JWT: {}", e::getLocalizedMessage);
      } catch (UnsupportedJwtException e) {
        LOG.error("Unsupported JWT: {}", e::getLocalizedMessage);
      } catch (IllegalArgumentException e) {
        LOG.error("JWT claims string is empty: {}", e::getLocalizedMessage);
      }
    }

    return jwtValid;
  }

}
