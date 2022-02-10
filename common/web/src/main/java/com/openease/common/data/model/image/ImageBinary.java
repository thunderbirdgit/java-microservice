package com.openease.common.data.model.image;

import com.openease.common.data.model.base.BaseAuditDataModel;
import org.springframework.data.elasticsearch.annotations.Document;

import static com.openease.common.data.model.image.ImageType.JPEG;

/**
 * Image binary data model
 *
 * @author Alan Czajkowski
 */
@Document(indexName = ImageBinary.IMAGE_BINARIES)
public class ImageBinary extends BaseAuditDataModel<ImageBinary> {

  public static final String IMAGE_BINARIES = "image-binaries";

  private String imageId;

  private ImageType type;

  private int width;

  private int height;

  private byte[] data;

  public ImageBinary() {
    this(null);
  }

  public ImageBinary(String id) {
    super(id);
    this.imageId = null;
    this.type = JPEG;
    this.width = 0;
    this.height = 0;
    this.data = null;
  }

  public String getImageId() {
    return imageId;
  }

  public ImageBinary setImageId(String imageId) {
    this.imageId = imageId;
    return this;
  }

  public ImageType getType() {
    return type;
  }

  public ImageBinary setType(ImageType type) {
    this.type = type;
    return this;
  }

  public int getWidth() {
    return width;
  }

  public ImageBinary setWidth(int width) {
    this.width = width;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public ImageBinary setHeight(int height) {
    this.height = height;
    return this;
  }

  public byte[] getData() {
    return data;
  }

  public ImageBinary setData(byte[] data) {
    this.data = data;
    return this;
  }

}
