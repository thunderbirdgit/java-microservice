package com.openease.service.www.manager.image;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.image.RemoteImageManager;
import com.openease.common.manager.image.request.ImageCreateRequest;
import com.openease.common.manager.image.response.ImageCreateResponse;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.common.web.util.RestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.gargoylesoftware.htmlunit.HttpMethod.POST;
import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.NOT;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_IMAGE_ERROR_GENERALFAILURE;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_IMAGE_ERROR_REMOTEAPIFAILURE;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.util.JsonUtils.toObject;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Default remote image manager
 *
 * @author Alan Czajkowski
 */
@Profile({NOT + LOCAL})
@Service
public class DefaultRemoteImageManager implements RemoteImageManager {

  private static final transient Logger LOG = LogManager.getLogger(DefaultRemoteImageManager.class);

  @Value("${manager.remoteImage.api.baseUrl}")
  private String remoteImageApiBaseUrl;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("remote image API base URL: {}", remoteImageApiBaseUrl);
    LOG.debug("Init finished");
  }

  @Override
  public ImageCreateResponse create(ImageCreateRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    ImageCreateResponse response;

    try {
      // call remote image API
      LOG.debug("API call: {} {} | Payload:{}{}", () -> POST, () -> remoteImageApiBaseUrl, System::lineSeparator, () -> request);
      WebResponse webResponse = RestUtils.jsonRequest(POST, remoteImageApiBaseUrl, request);

      // check response
      HttpStatus.Series httpStatusSeries = HttpStatus.Series.valueOf(webResponse.getStatusCode());
      String payload = webResponse.getContentAsString(UTF_8);
      switch (httpStatusSeries) {
        case SUCCESSFUL:
          LOG.debug("Call to remote image API succeeded");
          SuccessApiResponse<ImageCreateResponse> apiResponse = toObject(payload, SuccessApiResponse.class);
          response = toObject(toJson(apiResponse.getResult()), ImageCreateResponse.class);
          break;
        default:
          LOG.error("Call to remote image API failed:{}{}", System::lineSeparator, () -> payload);
          throw new GeneralManagerException(MANAGER_IMAGE_ERROR_REMOTEAPIFAILURE, "Call to remote image API failed");
      }
    } catch (Exception e) {
      LOG.error("Problem creating image", e);
      throw new GeneralManagerException(MANAGER_IMAGE_ERROR_GENERALFAILURE, e);
    }

    return response;
  }

}
