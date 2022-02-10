package com.openease.common.data.model.image;

import com.openease.common.data.model.base.BaseAuditDataModel;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Image data model
 *
 * @author Alan Czajkowski
 */
@Document(indexName = Image.IMAGES)
public class Image extends BaseAuditDataModel<Image> {

  public static final String IMAGES = "images";

  private String name;

  private boolean approved;

  private boolean visible;

  public Image() {
    this(null);
  }

  public Image(String id) {
    super(id);
    this.name = null;
    this.approved = false;
    this.visible = false;
  }

  public String getName() {
    return name;
  }

  public Image setName(String name) {
    this.name = name;
    return this;
  }

  public boolean isApproved() {
    return approved;
  }

  public Image setApproved(boolean approved) {
    this.approved = approved;
    return this;
  }

  public boolean isVisible() {
    return visible;
  }

  public Image setVisible(boolean visible) {
    this.visible = visible;
    return this;
  }

}
