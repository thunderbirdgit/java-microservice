package com.openease.service.www.api.v1.images;

import com.openease.common.data.model.image.ImageType;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.image.RemoteImageManager;
import com.openease.common.manager.image.request.ImageCreateRequest;
import com.openease.common.manager.image.response.ImageCreateResponse;
import com.openease.common.manager.security.SecurityManager;
import com.openease.common.util.exception.GeneralUtilException;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.common.web.util.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.awt.Dimension;
import java.io.IOException;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.data.lang.MessageKeys.CRUD_OPERATION_UNSUPPORTED;
import static com.openease.common.data.model.image.Image.IMAGES;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.ApiVersion.Constants.V1_CONTEXT;
import static com.openease.common.web.lang.MessageKeys.CONTROLLER_API_BASE_FIELDERROR;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.www.api.v1.images.ImagesController.IMAGES_CONTEXT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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

  @Autowired
  private RemoteImageManager remoteImageManager;

  @Autowired
  private SecurityManager securityManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Create image
   */
  @PreAuthorize("isAuthenticated()")
  @PostMapping(path = {"", "/"}, consumes = APPLICATION_JSON_VALUE)
  public SuccessApiResponse<ImageCreateResponse> create(@RequestBody @Valid ImageCreateRequest request) {
    LOG.trace("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      throw new ApiException();
    }
    String accountId = securityManager.getSignedInAccountId();
    if (accountId == null) {
      throw new ApiException(FORBIDDEN, CRUD_OPERATION_UNSUPPORTED);
    }

    SuccessApiResponse<ImageCreateResponse> response;

    try {
      ImageCreateResponse imageCreateResponse = remoteImageManager.create(request);
      response = createSuccessApiResponse(imageCreateResponse, CREATED);
    } catch (GeneralManagerException me) {
      throw new ApiException(me);
    }

//    createEventLog(eventLogManager, httpRequest, "Image created", null, image);
    LOG.trace("response: {}", response);
    return response;
  }

  /**
   * Create image
   */
  @PreAuthorize("isAuthenticated()")
  @PostMapping(path = {"", "/"}, consumes = MULTIPART_FORM_DATA_VALUE)
  public SuccessApiResponse<ImageCreateResponse> handleFileUpload(@RequestParam("fileData") MultipartFile fileData) {
    LOG.trace("fileData.originalFilename: {}", fileData::getOriginalFilename);
    LOG.trace("fileData.contentType: {}", fileData::getContentType);

    String accountId = securityManager.getSignedInAccountId();
    if (accountId == null) {
      throw new ApiException(FORBIDDEN, CRUD_OPERATION_UNSUPPORTED);
    }

    SuccessApiResponse<ImageCreateResponse> response;

    try {
      String fileName = fileData.getOriginalFilename();
      String[] fileNameComponents = fileName.split("[.]");
      String fileExtension = fileNameComponents[fileNameComponents.length - 1];
      ImageType imageType = ImageType.findByFileExtension(fileExtension);
      Dimension dimension = ImageUtils.getImageDimensions(fileData.getInputStream());

      ImageCreateRequest imageCreateRequest = new ImageCreateRequest()
          .setName(fileName)
          .setType(imageType)
          .setWidth(dimension.width)
          .setHeight(dimension.height)
          .setData(fileData.getBytes());
      response = create(imageCreateRequest);
    } catch (EnumConstantNotPresentException | IOException e) {
      LOG.error(e::getMessage);
      throw new ApiException(BAD_REQUEST, CONTROLLER_API_BASE_FIELDERROR, "Invalid image type");
    } catch (GeneralUtilException ue) {
      throw new ApiException(CRUD_BADREQUEST, ue.getMessage(), ue);
    }

    //    createEventLog(eventLogManager, httpRequest, "Image created", null, image);
    LOG.trace("response: {}", response);
    return response;
  }

}
