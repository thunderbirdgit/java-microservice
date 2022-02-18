package com.openease.service.www.manager.image;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.image.RemoteImageManager;
import com.openease.common.manager.image.request.ImageCreateRequest;
import com.openease.common.manager.image.response.ImageCreateResponse;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_IMAGE_ERROR_GENERALFAILURE;
import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Mock remote image manager
 *
 * @author Alan Czajkowski
 */
@Profile({LOCAL})
@Service
public class MockRemoteImageManager extends DefaultRemoteImageManager implements RemoteImageManager {

  private static final transient Logger LOG = LogManager.getLogger(MockRemoteImageManager.class);

  private static final String FILE_PATH_PREFIX = "./target/";
  private static final String FILE_NAME_SUFFIX = ".json";

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  @Override
  public ImageCreateResponse create(ImageCreateRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    String dateAndTime = LocalDateTime.now()
        .format(ISO_LOCAL_DATE_TIME)
        .replace("-", EMPTY)
        .replace(":", EMPTY);
    try {
      String filePath = FILE_PATH_PREFIX + RemoteImageManager.class.getSimpleName() + "/" + request.getClass().getSimpleName() + "-" + dateAndTime + FILE_NAME_SUFFIX;
      Path path = Paths.get(filePath);
      byte[] bytes = toJson(request).getBytes(UTF_8);
      FileUtils.forceMkdir(path.getParent().toFile());
      Files.write(path, bytes, CREATE);
    } catch (Exception e) {
      LOG.error("Problem creating image", e);
      throw new GeneralManagerException(MANAGER_IMAGE_ERROR_GENERALFAILURE, e);
    }

    return new ImageCreateResponse()
        .setId(dateAndTime)
        .setName(request.getName())
        .setType(request.getType())
        .setWidth(request.getWidth())
        .setHeight(request.getHeight());
  }

}
