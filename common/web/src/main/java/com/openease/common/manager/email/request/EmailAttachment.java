package com.openease.common.manager.email.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILATTACHMENT_DATA_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILATTACHMENT_FILENAME_NOTBLANK;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_EMAILATTACHMENT_MIMETYPE_NOTBLANK;

/**
 * Email attachment
 *
 * @author Alan Czajkowski
 */
public class EmailAttachment extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_EMAILATTACHMENT_FILENAME_NOTBLANK + "}")
  private String fileName;

  @NotBlank(message = "{" + VALIDATION_EMAILATTACHMENT_MIMETYPE_NOTBLANK + "}")
  private String mimeType;

  @NotNull(message = "{" + VALIDATION_EMAILATTACHMENT_DATA_NOTNULL + "}")
  private byte[] data;

  public String getFileName() {
    return fileName;
  }

  public EmailAttachment setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public String getMimeType() {
    return mimeType;
  }

  public EmailAttachment setMimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  public byte[] getData() {
    return data;
  }

  public EmailAttachment setData(byte[] data) {
    this.data = data;
    return this;
  }

}
