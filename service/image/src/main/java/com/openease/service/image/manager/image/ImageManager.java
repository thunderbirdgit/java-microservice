package com.openease.service.image.manager.image;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.openease.common.data.model.image.Image;
import com.openease.common.data.model.image.ImageBinary;
import com.openease.common.data.model.image.ImageType;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.image.request.ImageCreateRequest;
import com.openease.common.manager.image.response.ImageCreateResponse;
import com.openease.service.image.data.dao.image.ImageBinaryDao;
import com.openease.service.image.data.dao.image.ImageDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.data.lang.MessageKeys.CRUD_NOTFOUND;

/**
 * Image manager
 *
 * @author Alan Czajkowski
 */
@Service
public class ImageManager {

  private static final transient Logger LOG = LogManager.getLogger(ImageManager.class);

  private Cache<String, ImageBinary> cache;

  @Value("${manager.image.cache.size}")
  private long cacheSize;

  @Autowired
  private ImageDao imageDao;

  @Autowired
  private ImageBinaryDao imageBinaryDao;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("cache size: {}", cacheSize);
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(cacheSize)
        .build();

    LOG.debug("Init finished");
  }

  public ImageCreateResponse create(ImageCreateRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", () -> (request == null ? null : request.toStringUsingMixIn()));
    if (request == null) {
      LOG.warn("request is null");
      throw new GeneralManagerException(CRUD_BADREQUEST, "request is null");
    }

    Image image = new Image()
        .setId()
        .setName(request.getName())
        .setApproved(true)
        .setVisible(true);
    image = imageDao.save(image);

    ImageBinary imageBinary = new ImageBinary()
        .setImageId(image.getId())
        .setType(request.getType())
        .setWidth(request.getWidth())
        .setHeight(request.getHeight())
        .setData(request.getData());
    imageBinary = imageBinaryDao.save(imageBinary);

    return new ImageCreateResponse()
        .setId(image.getId())
        .setName(image.getName())
        .setType(imageBinary.getType())
        .setWidth(imageBinary.getWidth())
        .setHeight(imageBinary.getHeight());
  }

  public ImageBinary readImageBinary(String imageId, ImageType type) throws GeneralManagerException {
    LOG.trace("imageId: {}", () -> imageId);
    LOG.trace("type: {}", () -> type);

    ImageBinary imageBinary;

    Optional<Image> optionalImage = imageDao.findById(imageId);
    if (optionalImage.isPresent()) {
      Image image = optionalImage.get();
      if (image.isApproved() && image.isVisible()) {
        Optional<ImageBinary> optionalImageBinary = imageBinaryDao.findByImageIdAndType(imageId, type);
        if (optionalImageBinary.isPresent()) {
          imageBinary = optionalImageBinary.get();
        } else {
          throw new GeneralManagerException(CRUD_NOTFOUND, "Image binary with image id [" + imageId + "] and image type [" + type + "] *not* found");
        }
      } else {
        throw new GeneralManagerException(CRUD_NOTFOUND, "Image binary with image id [" + imageId + "] and image type [" + type + "] *not* found");
      }
    } else {
      throw new GeneralManagerException(CRUD_NOTFOUND, "Image binary with image id [" + imageId + "] and image type [" + type + "] *not* found");
    }

    return imageBinary;
  }

  public ImageBinary readImageBinary(String fileName) throws GeneralManagerException {
    LOG.trace("fileName: {}", () -> fileName);

    String[] imageComponents = fileName.split("[.]");

    LOG.debug("cache size: {}", () -> cache.size());
    ImageBinary imageBinary = cache.getIfPresent(fileName);
    boolean cacheMiss = (imageBinary == null);
    LOG.debug("cache {}", () -> (cacheMiss ? "miss" : "hit"));

    String imageId = imageComponents[0];
    LOG.trace("image id: {}", () -> imageId);
    String fileExtension = imageComponents[1];
    LOG.trace("file extension: {}", () -> fileExtension);

    Image image = null;
    if (cacheMiss) {
      ImageType imageType;
      try {
        imageType = ImageType.findByFileExtension(fileExtension);
        LOG.trace("image type: {}", () -> imageType);
      } catch (EnumConstantNotPresentException e) {
        LOG.warn("{}", e::getMessage);
        throw new GeneralManagerException(CRUD_NOTFOUND, "Image binary with image id [" + imageId + "] and image type [" + fileExtension + "] *not* found");
      }
      imageBinary = readImageBinary(imageId, imageType);
      cache.put(fileName, imageBinary);
    } else {  // cache hit
      Optional<Image> optionalImage = imageDao.findById(imageBinary.getImageId());
      if (optionalImage.isPresent()) {
        image = optionalImage.get();
        if (!image.isApproved() || !image.isVisible()) {
          cache.invalidate(fileName);
          throw new GeneralManagerException(CRUD_NOTFOUND, "Image binary with image id [" + imageId + "] and image type [" + fileExtension + "] *not* found");
        }
      } else {
        cache.invalidate(fileName);
        throw new GeneralManagerException(CRUD_NOTFOUND, "Image binary with image id [" + imageId + "] and image type [" + fileExtension + "] *not* found");
      }
    }

    setImageLastAccessed(image, imageBinary.getImageId());

    return imageBinary;
  }

  private void setImageLastAccessed(Image image, String imageId) {
    if (image == null) {
      Optional<Image> optionalImage = imageDao.findById(imageId);
      if (optionalImage.isPresent()) {
        image = optionalImage.get();
      }
    }

    if (image != null) {
      image.setLastAccessed(new Date());
      imageDao.save(image);
    }
  }

}
