package com.openease.common.data.model.base;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_CREATED_NOTNULL;
import static com.openease.common.data.lang.MessageKeys.VALIDATION_LASTMODIFIED_NOTNULL;

/**
 * Base audit data model
 *
 * @author Alan Czajkowski
 */
public abstract class BaseAuditDataModel<T extends BaseDataModel> extends BaseDataModel<T> {

  @NotNull(message = "{" + VALIDATION_CREATED_NOTNULL + "}")
  private Date created;

  @NotNull(message = "{" + VALIDATION_LASTMODIFIED_NOTNULL + "}")
  private Date lastModified;

  protected BaseAuditDataModel(String id) {
    super(id);
    created = new Date();
    lastModified = new Date();
  }

  public Date getCreated() {
    return created;
  }

  @SuppressWarnings("unchecked")
  public T setCreated(Date created) {
    this.created = created;
    return (T) this;
  }

  public Date getLastModified() {
    return lastModified;
  }

  @SuppressWarnings("unchecked")
  public T setLastModified(Date lastModified) {
    this.lastModified = lastModified;
    return (T) this;
  }

}
