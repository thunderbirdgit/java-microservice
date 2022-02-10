package com.openease.common.manager.image.response;

import com.openease.common.data.model.image.ImageType;
import com.openease.common.manager.base.model.BaseManagerModel;

/**
 * Image: Create response
 *
 * @author Alan Czajkowski
 */
public class ImageCreateResponse extends BaseManagerModel {

  private String id;

  private String name;

  private ImageType type;

  private int width;

  private int height;

  public String getId() {
    return id;
  }

  public ImageCreateResponse setId(String id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public ImageCreateResponse setName(String name) {
    this.name = name;
    return this;
  }

  public ImageType getType() {
    return type;
  }

  public ImageCreateResponse setType(ImageType type) {
    this.type = type;
    return this;
  }

  public int getWidth() {
    return width;
  }

  public ImageCreateResponse setWidth(int width) {
    this.width = width;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public ImageCreateResponse setHeight(int height) {
    this.height = height;
    return this;
  }

}
