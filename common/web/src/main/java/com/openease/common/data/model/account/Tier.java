package com.openease.common.data.model.account;

import static com.openease.common.data.lang.MessageKeys.ENUM_TIER_TIER0_NAME;
import static com.openease.common.data.lang.MessageKeys.ENUM_TIER_TIER1_NAME;
import static com.openease.common.data.lang.MessageKeys.ENUM_TIER_TIER2_NAME;

/**
 * Tier enum
 *
 * @author Alan Czajkowski
 */
public enum Tier {

  TIER0(ENUM_TIER_TIER0_NAME),
  TIER1(ENUM_TIER_TIER1_NAME),
  TIER2(ENUM_TIER_TIER2_NAME);

  private String nameKey;

  Tier(String nameKey) {
    this.nameKey = nameKey;
  }

  public String getNameKey() {
    return nameKey;
  }

}
