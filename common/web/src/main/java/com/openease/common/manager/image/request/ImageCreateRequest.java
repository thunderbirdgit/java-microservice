package com.openease.common.manager.image.request;

import com.openease.common.data.model.image.ImageType;
import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_IMAGECREATEREQUEST_DATA_MINMAX;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_IMAGECREATEREQUEST_DATA_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_IMAGECREATEREQUEST_HEIGHT_MIN;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_IMAGECREATEREQUEST_NAME_NOTBLANK;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_IMAGECREATEREQUEST_TYPE_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_IMAGECREATEREQUEST_WIDTH_MIN;

/**
 * Image: Create request
 *
 * @author Alan Czajkowski
 */
public class ImageCreateRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_IMAGECREATEREQUEST_NAME_NOTBLANK + "}")
  private String name;

  @NotNull(message = "{" + VALIDATION_IMAGECREATEREQUEST_TYPE_NOTNULL + "}")
  private ImageType type;

  @Min(value = 1, message = "{" + VALIDATION_IMAGECREATEREQUEST_WIDTH_MIN + "}")
  private int width;

  @Min(value = 1, message = "{" + VALIDATION_IMAGECREATEREQUEST_HEIGHT_MIN + "}")
  private int height;

  @NotNull(message = "{" + VALIDATION_IMAGECREATEREQUEST_DATA_NOTNULL + "}")
  @Size(min = 10, max = 1048576, message = "{" + VALIDATION_IMAGECREATEREQUEST_DATA_MINMAX + "}")
  private byte[] data;

  public String getName() {
    return name;
  }

  public ImageCreateRequest setName(String name) {
    this.name = name;
    return this;
  }

  public ImageType getType() {
    return type;
  }

  public ImageCreateRequest setType(ImageType type) {
    this.type = type;
    return this;
  }

  public int getWidth() {
    return width;
  }

  public ImageCreateRequest setWidth(int width) {
    this.width = width;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public ImageCreateRequest setHeight(int height) {
    this.height = height;
    return this;
  }

  public byte[] getData() {
    return data;
  }

  public ImageCreateRequest setData(byte[] data) {
    this.data = data;
    return this;
  }

}
