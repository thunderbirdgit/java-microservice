package com.openease.service.www.manager.account.oauth2;

import com.openease.common.config.Config;
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
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import static com.openease.common.util.JsonUtils.toJson;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * JSON Web Token (JWT) provider
 *
 * @author Alan Czajkowski
 */
@Component
public class JwtProvider {

  private static final transient Logger LOG = LogManager.getLogger(JwtProvider.class);

  @Autowired
  private Config config;

  public String createJwt(Authentication authentication) {
    String token = null;

    String username = null;
    if (authentication == null) {
      LOG.trace("No authentication JWT in session");
    } else {
      LOG.debug("{} is of type: {}", Authentication.class::getSimpleName, () -> authentication.getClass().getSimpleName());
      Object principal = authentication.getPrincipal();
      if (principal != null) {
        LOG.debug("{}.principal is of type: {}", () -> authentication.getClass().getSimpleName(), () -> principal.getClass().getSimpleName());
        //TODO: this is GOOGLE
        if (principal instanceof DefaultOidcUser) {
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
          .setSubject(username)
          .setIssuedAt(new Date(now))
          .setExpiration(new Date(expiry))
          .signWith(key, config.getAuth().getJwtSignatureAlgorithm())
          .compact();
    }

    return token;
  }

  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(config.getAuth().getJwtSecretBase64())
        .build()
        .parseClaimsJws(token)
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
