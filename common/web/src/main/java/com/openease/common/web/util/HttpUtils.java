package com.openease.common.web.util;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import static com.google.common.net.HttpHeaders.X_FORWARDED_FOR;
import static com.google.common.net.HttpHeaders.X_USER_IP;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * HTTP utilities
 *
 * @author Alan Czajkowski
 */
public final class HttpUtils {

  private static final transient Logger LOG = LogManager.getLogger(HttpUtils.class);

  public static final String CHARSET_UTF_8_VALUE = "UTF-8";
  public static final String HTTP_HEADER_X_REAL_IP = "X-Real-IP";

  private static UserAgentParser USER_AGENT_PARSER;

  static {
    try {
      USER_AGENT_PARSER = new UserAgentService().loadParser();
    } catch (IOException | ParseException e) {
      LOG.error(e::getMessage, e);
      throw new RuntimeException("Something really bad happened", e);
    }
  }

  private HttpUtils() {
    // not publicly instantiable
  }

  public static Capabilities getUserAgentDetails(HttpServletRequest httpRequest) {
    String userAgent = httpRequest.getHeader(USER_AGENT);
    return userAgent == null
        ? null
        : USER_AGENT_PARSER.parse(userAgent);
  }

  /**
   * Get IP address from HTTP request
   * - attempts to get client IP address (even if client is behind a proxy)
   *
   * @param httpRequest HTTP servlet request
   *
   * @return IP address found in HTTP request
   */
  public static String getIpAddress(HttpServletRequest httpRequest) {
    String ipAddress = EMPTY;

    if (httpRequest != null) {
      ipAddress = httpRequest.getHeader(HTTP_HEADER_X_REAL_IP);
      if (isBlank(ipAddress)) {
        ipAddress = httpRequest.getHeader(X_USER_IP);
      }
      if (isBlank(ipAddress)) {
        ipAddress = httpRequest.getHeader(X_FORWARDED_FOR);
        if (isNotBlank(ipAddress) && ipAddress.contains(",")) {
          String[] ipAddresses = ipAddress.split(",");
          ipAddress = ipAddresses[0].trim();
        }
      }
      if (isBlank(ipAddress)) {
        ipAddress = httpRequest.getRemoteAddr();
      }
    }

    return ipAddress;
  }

  public static Map<String, String> getHeadersAsMap(HttpServletRequest httpRequest) {
    Map<String, String> map = new HashMap<>();
    Enumeration<String> headers = httpRequest.getHeaderNames();
    while (headers.hasMoreElements()) {
      String headerName = headers.nextElement();
      String headerValue = httpRequest.getHeader(headerName);
      map.put(headerName, headerValue);
    }
    return map;
  }

  public static String getHeadersAsString(HttpServletRequest httpRequest, String prefix) {
    StringBuilder sb = new StringBuilder();
    Enumeration<String> headers = httpRequest.getHeaderNames();
    while (headers.hasMoreElements()) {
      String headerName = headers.nextElement().toString();
      String headerValue = httpRequest.getHeader(headerName);
      sb.append(prefix).append(headerName).append(": ").append(headerValue).append(lineSeparator());
    }
    return sb.toString();
  }

  public static Map<String, String> getHeadersAsMap(HttpServletResponse httpResponse) {
    Map<String, String> map = new HashMap<>();
    Collection<String> headers = httpResponse.getHeaderNames();
    for (String headerName : headers) {
      String headerValue = httpResponse.getHeader(headerName);
      map.put(headerName, headerValue);
    }
    return map;
  }

  public static String getHeadersAsString(HttpServletResponse httpResponse, String prefix) {
    StringBuilder sb = new StringBuilder();
    Collection<String> headers = httpResponse.getHeaderNames();
    for (String headerName : headers) {
      String headerValue = httpResponse.getHeader(headerName);
      sb.append(prefix).append(headerName).append(": ").append(headerValue).append(lineSeparator());
    }
    return sb.toString();
  }

  public static Optional<Cookie> getCookie(HttpServletRequest httpRequest, String name) {
    Cookie[] cookies = httpRequest.getCookies();

    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        if (StringUtils.equals(cookie.getName(), name)) {
          return Optional.of(cookie);
        }
      }
    }

    return Optional.empty();
  }

  public static void addCookie(HttpServletResponse httpResponse, String name, String value, int maxAgeSeconds) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAgeSeconds);
    httpResponse.addCookie(cookie);
  }

  public static void deleteCookie(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String name) {
    Cookie[] cookies = httpRequest.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          cookie.setValue(EMPTY);
          cookie.setPath("/");
          cookie.setMaxAge(0);
          httpResponse.addCookie(cookie);
        }
      }
    }
  }

}
