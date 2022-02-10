package com.openease.common.data.model.image;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.MediaType;

import java.util.Arrays;

public enum ImageType {

  JPEG("jpeg", "jpeg", "jpg"),
  PNG("png", "png");

  public static final String MIME_TYPE_PREFIX = "image/";

  private String mimeTypeSuffix;

  private String[] fileExtensions;

  ImageType(String mimeTypeSuffix, String... fileExtensions) {
    this.mimeTypeSuffix = mimeTypeSuffix;
    this.fileExtensions = fileExtensions;
  }

  public static MediaType getMediaType(ImageType type) {
    return MediaType.valueOf(MIME_TYPE_PREFIX + type.getMimeTypeSuffix());
  }

  public static ImageType findByFileExtension(String fileExtension) {
    return Arrays.stream(ImageType.values())
        .filter(entry -> ArrayUtils.contains(entry.getFileExtensions(), fileExtension.toLowerCase()))
        .findFirst()
        .orElseThrow(() -> {
          throw new EnumConstantNotPresentException(ImageType.class, fileExtension);
        });
  }

  public String getMimeTypeSuffix() {
    return mimeTypeSuffix;
  }

  public String[] getFileExtensions() {
    return fileExtensions;
  }

}
