package com.openease.service.image.api.v1.images;

import com.openease.common.data.model.image.ImageBinary;
import com.openease.common.data.model.image.ImageType;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.image.request.ImageCreateRequest;
import com.openease.common.manager.image.response.ImageCreateResponse;
import com.openease.common.web.annotation.HeadMapping;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.service.image.manager.image.ImageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import static com.openease.common.data.lang.MessageKeys.CRUD_NOTFOUND;
import static com.openease.common.data.model.base.BaseDataModel.ID_REGEX_RELAXED;
import static com.openease.common.data.model.image.Image.IMAGES;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.ApiVersion.Constants.V1_CONTEXT;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.image.api.v1.images.ImagesController.IMAGES_CONTEXT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Images controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = V1_CONTEXT + IMAGES_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class ImagesController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(ImagesController.class);

  public static final String IMAGES_CONTEXT = "/" + IMAGES;

  public static final String HTTP_HEADER_X_IMAGE_WIDTH = "X-Image-Width";
  public static final String HTTP_HEADER_X_IMAGE_HEIGHT = "X-Image-Height";
  public static final String IMAGE_FILENAME_REGEX = ID_REGEX_RELAXED + "[.][0-9A-Za-z]+";

  @Autowired
  private ImageManager imageManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Create image
   */
  @PostMapping(path = {"", "/"})
  public SuccessApiResponse<ImageCreateResponse> create(@RequestBody @Valid ImageCreateRequest request) {
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse<ImageCreateResponse> response;

    try {
      ImageCreateResponse imageCreateResponse = imageManager.create(request);
      response = createSuccessApiResponse(imageCreateResponse, CREATED);
    } catch (GeneralManagerException me) {
      throw new ApiException(me);
    }

//    createEventLog(eventLogManager, httpRequest, "Image created", null, image);
    LOG.trace("response: {}", response);
    return response;
  }

  /**
   * Check image
   */
  @HeadMapping(path = "/{fileName:" + IMAGE_FILENAME_REGEX + "}")
  public ResponseEntity<byte[]> check(@PathVariable String fileName) {
    ResponseEntity<byte[]> response = read(fileName);
    response = ResponseEntity.ok()
        .headers(response.getHeaders())
        .body(new byte[0]);

//    createEventLog(eventLogManager, httpRequest, "Image checked", null, image);
    LOG.trace("response headers:\n{}", response::getHeaders);
    return response;
  }

  /**
   * Read image
   */
  @GetMapping(path = "/{fileName:" + IMAGE_FILENAME_REGEX + "}")
  public ResponseEntity<byte[]> read(@PathVariable String fileName) {
    LOG.trace("fileName: {}", () -> fileName);

    ResponseEntity<byte[]> response;

    try {
      ImageBinary imageBinary = imageManager.readImageBinary(fileName.toUpperCase());
      MediaType mediaType = ImageType.getMediaType(imageBinary.getType());
      LOG.trace("media type: {}", () -> mediaType);
      response = ResponseEntity.ok()
          .contentType(mediaType)
          .contentLength(imageBinary.getData().length)
          .header(HTTP_HEADER_X_IMAGE_WIDTH, Integer.toString(imageBinary.getWidth()))
          .header(HTTP_HEADER_X_IMAGE_HEIGHT, Integer.toString(imageBinary.getHeight()))
          .body(imageBinary.getData());
    } catch (GeneralManagerException me) {
      switch (me.getKey()) {
        case CRUD_NOTFOUND:
          throw new ApiException(NOT_FOUND, me);
        default:
          throw new ApiException(me);
      }
    }

//    createEventLog(eventLogManager, httpRequest, "Image read", null, image);
    LOG.trace("response headers:\n{}", response::getHeaders);
    return response;
  }

}
