package com.openease.common.web.util;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.openease.common.util.JsonUtils;
import com.openease.common.util.exception.GeneralUtilException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static com.gargoylesoftware.htmlunit.HttpMethod.HEAD;
import static com.gargoylesoftware.htmlunit.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * ReST utilities
 *
 * @author Alan Czajkowski
 */
public class RestUtils {

  private static final transient Logger LOG = LogManager.getLogger(RestUtils.class);

  public static final int PAGE_TIMEOUT_MS = 90000;

  private RestUtils() {
    // not publicly instantiable
  }

  public static WebClient createWebClient() {
    WebClient webClient = new WebClient();
    webClient.getOptions().setTimeout(PAGE_TIMEOUT_MS);
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setJavaScriptEnabled(false);
    webClient.getOptions().setCssEnabled(false);
    return webClient;
  }

  public static String httpRequest(HttpMethod httpMethod, String url, List<NameValuePair> requestParams) {
    String payload;

    try {
      WebClient webClient = createWebClient();

      WebRequest webRequest = new WebRequest(new URL(url), httpMethod);
      if (isNotEmpty(requestParams)) {
        webRequest.setRequestParameters(requestParams);
      }

      LOG.trace("Web request: {}", webRequest);
      Page page = webClient.getPage(webRequest);
      WebResponse webResponse = page.getWebResponse();
      LOG.trace("Status: {}", webResponse::getStatusCode);

      payload = webResponse.getContentAsString(UTF_8);

      // check response
      HttpStatus.Series httpStatusSeries = HttpStatus.Series.valueOf(webResponse.getStatusCode());
      switch (httpStatusSeries) {
        case SUCCESSFUL:
          LOG.debug("Call to [{}] succeeded", url);
          break;
        default:
          LOG.error("Call to [{}] failed, HTTP status code: {}", () -> url, webResponse::getStatusCode);
          throw new GeneralUtilException("Call to [" + url + "] failed");
      }
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException(e.getMessage(), e);
    }

    return payload;
  }

  /**
   * Perform an HTTP POST request on the supplied URL and return response in a {@link String}
   *
   * @param url           URL to fetch
   * @param requestParams HTTP POST request parameters
   *
   * @return {@link String} containing the payload of the response
   */
  public static String httpPostRequest(String url, List<NameValuePair> requestParams) {
    return httpRequest(POST, url, requestParams);
  }

  /**
   * Perform an HTTP GET request on the supplied URL and return response in a {@link String}
   *
   * @param url               URL to fetch
   * @param queryStringParams HTTP GET query string parameters
   *
   * @return {@link String} containing the payload of the response
   */
  public static String httpGetRequest(String url, List<NameValuePair> queryStringParams) {
    return httpRequest(GET, url, queryStringParams);
  }

  /**
   * Perform an HTTP GET request on the supplied URL and return response in a {@link String}
   *
   * @param url URL to fetch
   *
   * @return {@link String} containing the payload of the response
   */
  public static String httpGetRequest(String url) {
    return httpGetRequest(url, null);
  }

  /**
   * Perform an HTTP request using the given HTTP method on the supplied URL with a JSON payload (representing the
   * request object)
   *
   * @param httpMethod HTTP method (e.g. {@link HttpMethod#GET})
   * @param url        URL to fetch
   * @param request    request object to be used as the JSON payload
   *
   * @return {@link WebResponse} containing the full response
   */
  public static WebResponse jsonRequest(HttpMethod httpMethod, String url, Object request) {
    WebResponse webResponse;

    try {
      WebClient webClient = createWebClient();

      WebRequest webRequest = new WebRequest(new URL(url), httpMethod);

      webRequest.setAdditionalHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE + ";charset=" + UTF_8);
      webRequest.setRequestBody(JsonUtils.toJson(request));

      LOG.trace("Web Request: {}", webRequest);
      Page page = webClient.getPage(webRequest);
      webResponse = page.getWebResponse();
      LOG.trace("Status: {}", webResponse::getStatusCode);
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException(e.getMessage(), e);
    }

    return webResponse;
  }

  /**
   * Perform an HTTP request using the given HTTP method on the supplied URL with a payload
   *
   * @param httpMethod  HTTP method (e.g. {@link HttpMethod#GET})
   * @param url         URL to fetch
   * @param contentType content type of the requestBody string
   * @param requestBody string payload to be used in the request body
   *
   * @return {@link WebResponse} containing the full response
   */
  public static WebResponse httpRequest(HttpMethod httpMethod, String url, String contentType, String requestBody) {
    return httpRequest(httpMethod, url, contentType, requestBody, null);
  }

  /**
   * Perform an HTTP request using the given HTTP method on the supplied URL with a payload
   *
   * @param httpMethod  HTTP method (e.g. {@link HttpMethod#GET})
   * @param url         URL to fetch
   * @param contentType content type of the requestBody string
   * @param requestBody string payload to be used in the request body
   * @param headers     string/string map of headers to add
   *
   * @return {@link WebResponse} containing the full response
   */
  public static WebResponse httpRequest(HttpMethod httpMethod, String url, String contentType, String requestBody, Map<String, String> headers) {
    WebResponse webResponse;

    try {
      WebClient webClient = createWebClient();

      WebRequest webRequest = new WebRequest(new URL(url), httpMethod);
      if (headers != null) {
        webRequest.setAdditionalHeaders(headers);
      }
      webRequest.setAdditionalHeader(CONTENT_TYPE, contentType + ";charset=" + UTF_8);

      // set request body if HTTP method is POST or PUT, ignore otherwise
      switch (httpMethod) {
        case POST:
        case PUT:
          if (isNotBlank(requestBody)) {
            webRequest.setRequestBody(requestBody);
          } else {
            LOG.trace("Request body is empty for HTTP {}", httpMethod);
          }
          break;
        default:
          LOG.warn("Request body ignored for HTTP {}", httpMethod);
      }

      LOG.trace("Web Request: {}", webRequest);
      Page page = webClient.getPage(webRequest);
      webResponse = page.getWebResponse();
      LOG.trace("Status: {}", webResponse::getStatusCode);
    } catch (Exception e) {
      LOG.error(e::getMessage, e);
      throw new GeneralUtilException(e.getMessage(), e);
    }

    return webResponse;
  }

  /**
   * TODO: re-factor to use HTMLUnit
   * Pings an HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
   * the 200-399 range.
   *
   * @param url     The HTTP URL to be pinged.
   * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that the
   *                total timeout is effectively two times the given timeout.
   *
   * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
   * given timeout, otherwise <code>false</code>.
   */
  public static boolean pingUrl(String url, int timeout) {
    // Otherwise an exception may be thrown on invalid SSL certificates:
    //url = url.replaceFirst("^https", "http");
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(timeout);
      connection.setReadTimeout(timeout);
      connection.setRequestMethod(HEAD.toString());
      int responseCode = connection.getResponseCode();

      // check response
      HttpStatus.Series httpStatusSeries = HttpStatus.Series.valueOf(responseCode);
      switch (httpStatusSeries) {
        case SUCCESSFUL:
          LOG.debug("Call to [{}] succeeded", url);
          break;
        default:
          LOG.error("Call to [{}] failed, HTTP status code: {}", url, responseCode);
          throw new GeneralUtilException("Call to [" + url + "] failed");
      }
      return true;
    } catch (Exception exception) {
      LOG.debug(exception.toString(), exception);
      return false;
    }
  }

}
