package com.openease.common.web.filter;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.log.EventLogManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.security.BaseAuthSecurityConfig.getSignedInAccount;
import static com.openease.common.web.util.HttpUtils.getHeadersAsString;
import static com.openease.common.web.util.HttpUtils.getIpAddress;
import static com.openease.common.web.util.HttpUtils.getUserAgentDetails;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Debug filter
 * - log debug information
 *
 * @author Alan Czajkowski
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class DebugFilter implements Filter {

  private static final transient Logger LOG = LogManager.getLogger(DebugFilter.class);

  @Value("${config.filters.debug.enabled}")
  private boolean enabled;

  @Autowired
  private EventLogManager eventLogManager;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    LOG.trace("Init started");
    LOG.trace("enabled: {}", enabled);
    LOG.trace("Init finished");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;

      boolean debugEnabled = (enabled && LOG.isDebugEnabled());
      LOG.trace("Debug filter {}", () -> debugEnabled ? "enabled" : "disabled");

      if (debugEnabled) {
        String ipAddress = getIpAddress(httpRequest);
        String queryString = isNotBlank(httpRequest.getQueryString())
            ? ("?" + httpRequest.getQueryString())
            : "";
        LOG.debug("[IP:{}] HTTP Request:{}  HTTP {} {}{}{}  Headers:{}{}", () -> ipAddress, System::lineSeparator, httpRequest::getMethod, httpRequest::getRequestURL, () -> queryString, System::lineSeparator, System::lineSeparator, () -> getHeadersAsString(httpRequest, "    "));
        LOG.debug("User Agent: {}", () -> toJson(getUserAgentDetails(httpRequest), true));
      }

      try {
        eventLogManager.create(httpRequest, getSignedInAccount());
      } catch (GeneralManagerException me) {
        LOG.error(me::getMessage, me);
      }

      // continue filter chain
      chain.doFilter(request, response);

      if (debugEnabled) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String ipAddress = getIpAddress(httpRequest);
        LOG.debug("[IP:{}] HTTP Response:{}  HTTP Status Code {}{}  Headers:{}{}", () -> ipAddress, System::lineSeparator, httpResponse::getStatus, System::lineSeparator, System::lineSeparator, () -> getHeadersAsString(httpResponse, "    "));
      }
    } else {
      // continue filter chain
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
    LOG.trace("Destroy started");
    // do nothing
    LOG.trace("Destroy finished");
  }

}
