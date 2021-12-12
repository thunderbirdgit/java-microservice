package com.openease.service.www.manager.account.oauth2;

import com.openease.common.config.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
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
      Date nowDate = new Date(now);
      long expiry = now + Duration.ofSeconds(config.getAuth().getTokenExpirationSeconds()).toMillis();
      Date expiryDate = new Date(expiry);
      byte[] keyBytes = Decoders.BASE64.decode(config.getAuth().getTokenSecret());
      Key key = Keys.hmacShaKeyFor(keyBytes);
      token = Jwts.builder()
          .setSubject(username)
          .setIssuedAt(nowDate)
          .setExpiration(expiryDate)
          .signWith(key, SignatureAlgorithm.HS512)
          .compact();
    }

    return token;
  }

  public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(config.getAuth().getTokenSecret())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  public boolean validateJwt(String jwt) {
    if (isNotBlank(jwt)) {
      try {
        Jwts.parserBuilder()
            .setSigningKey(config.getAuth().getTokenSecret())
            .build()
            .parseClaimsJws(jwt);
        return true;
      } catch (SignatureException ex) {
        LOG.error("Invalid JWT signature");
      } catch (MalformedJwtException ex) {
        LOG.error("Invalid JWT");
      } catch (ExpiredJwtException ex) {
        LOG.error("Expired JWT");
      } catch (UnsupportedJwtException ex) {
        LOG.error("Unsupported JWT");
      } catch (IllegalArgumentException ex) {
        LOG.error("JWT claims string is empty");
      }
    }
    return false;
  }

}
