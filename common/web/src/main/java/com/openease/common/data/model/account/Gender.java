package com.openease.common.data.model.account;

import static com.openease.common.data.lang.MessageKeys.ENUM_GENDER_FEMALE_NAME;
import static com.openease.common.data.lang.MessageKeys.ENUM_GENDER_MALE_NAME;
import static com.openease.common.data.lang.MessageKeys.ENUM_GENDER_OTHER_NAME;
import static com.openease.common.data.lang.MessageKeys.ENUM_GENDER_UNKNOWN_NAME;

/**
 * Gender enum
 *
 * @author Alan Czajkowski
 */
public enum Gender {

  FEMALE(ENUM_GENDER_FEMALE_NAME),
  MALE(ENUM_GENDER_MALE_NAME),
  OTHER(ENUM_GENDER_OTHER_NAME),
  UNKNOWN(ENUM_GENDER_UNKNOWN_NAME);

  private String nameKey;

  Gender(String nameKey) {
    this.nameKey = nameKey;
  }

  public String getNameKey() {
    return nameKey;
  }

}
