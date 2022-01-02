package com.openease.common.data.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.openease.common.data.model.base.BaseModel;

import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Objects;

import static com.openease.common.data.model.base.BaseDataModel.DEFAULT_LOCALE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Phone number model
 *
 * @author Alan Czajkowski
 */
public class PhoneNumber extends BaseModel<PhoneNumber> {

  private String regionCode;

  private String number;

  public PhoneNumber() {
    this.regionCode = DEFAULT_LOCALE.getCountry();
    this.number = EMPTY;
  }

  public String getRegionCode() {
    return regionCode;
  }

  public PhoneNumber setRegionCode(String regionCode) {
    Locale locale = DEFAULT_LOCALE;
    if (isNotBlank(regionCode)) {
      try {
        locale = new Locale.Builder()
            .setRegion(trim(regionCode))
            .build();
      } catch (IllformedLocaleException ile) {
        // ignore
      }
    }
    this.regionCode = locale.getCountry();
    return this;
  }

  @JsonIgnore
  public PhoneNumber setRegionCode(Locale locale) {
    return setRegionCode(
        locale != null
            ? locale.getCountry()
            : DEFAULT_LOCALE.getCountry()
    );
  }

  public String getNumber() {
    return number;
  }

  public PhoneNumber setNumber(String number) {
    this.number = trim(number);
    return this;
  }

  /**
   * @return phone number in {@link PhoneNumberFormat#E164} (SMS-friendly) format
   */
  @JsonIgnore
  public String toFormatE164() {
    String phoneNumber = null;
    try {
      Phonenumber.PhoneNumber parsedPhoneNumber = PhoneNumberUtil.getInstance().parse(number, regionCode);
      phoneNumber = PhoneNumberUtil.getInstance().format(parsedPhoneNumber, PhoneNumberFormat.E164);
    } catch (NumberParseException npe) {
      // ignore
    }
    return phoneNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PhoneNumber)) {
      return false;
    }
    PhoneNumber that = (PhoneNumber) o;
    return getRegionCode().equals(that.getRegionCode())
        && getNumber().equals(that.getNumber());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRegionCode(), getNumber());
  }

}
