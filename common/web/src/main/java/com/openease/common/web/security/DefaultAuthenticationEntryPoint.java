package com.openease.common.web.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Default {@link AuthenticationEntryPoint}
 *
 * @author Alan Czajkowski
 */
@Component
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final transient Logger LOG = LogManager.getLogger(DefaultAuthenticationEntryPoint.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  @Override
  public void commence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException exception) throws IOException, ServletException {
    LOG.debug("HTTP Request: {} {}", httpRequest::getMethod, httpRequest::getRequestURL);
    LOG.warn("{}: {}", () -> exception.getClass().getSimpleName(), exception::getMessage);
    httpResponse.sendError(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase());
  }

}
