package com.openease.common.data.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Pattern;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.openease.common.data.lang.MessageKeys.VALIDATION_ID_PATTERNMISMATCH;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

/**
 * Base data model
 *
 * @author Alan Czajkowski
 */
public abstract class BaseDataModel<T extends BaseDataModel> extends BaseModel<T> {

  private static final transient Logger LOG = LogManager.getLogger(BaseDataModel.class);

  public static final int ID_LENGTH = 20;
  public static final String ID_REGEX = "[2-9A-HJ-NP-Z]{" + ID_LENGTH + "}";
  public static final String ID_REGEX_RELAXED = "[0-9A-Za-z]{" + ID_LENGTH + "}";
  public static final java.util.regex.Pattern ID_REGEX_PATTERN;

  public static final Locale DEFAULT_LOCALE = Locale.US;

  static {
    ID_REGEX_PATTERN = java.util.regex.Pattern.compile(ID_REGEX);
  }

  /**
   * Generate a model ID that satisfies the {@link BaseDataModel#ID_REGEX} definition.
   */
  public static String generateId() {
    String id = randomAlphanumeric(ID_LENGTH).toUpperCase();
    Matcher matcher = ID_REGEX_PATTERN.matcher(id);
    // re-generate ID if ID contains any bad characters
    while (!matcher.matches()) {
      LOG.trace("Generated bad ID [{}], re-generating ...", id);
      id = randomAlphanumeric(ID_LENGTH).toUpperCase();
      matcher.reset(id);
    }

    LOG.trace("Generated ID: {}", id);
    return id;
  }

  @Id
  @Pattern(regexp = ID_REGEX, message = "{" + VALIDATION_ID_PATTERNMISMATCH + "}")
  protected String id;

  protected BaseDataModel(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @SuppressWarnings("unchecked")
  public T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public T setId() {
    this.id = generateId();
    return (T) this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseDataModel)) {
      return false;
    }
    BaseDataModel<?> that = (BaseDataModel<?>) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

}
