package com.openease.common.web.api.enumeration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Region enum
 *
 * @author Alan Czajkowski
 */
public enum Region {

  CA(1, Locale.CANADA, Locale.CANADA_FRENCH),
  US(1, Locale.US),
  MX(52, new Locale("es", "MX"));

  private String label;

  private Map<String, Object> phone;

  private List<Map<String, Object>> languages;

  private Map<String, Object> map;

  Region(int phoneCode, Locale... locales) {
    this.phone = new HashMap<>(1);
    this.phone.put("code", phoneCode);
    this.label = locales[0].getDisplayCountry();
    this.languages = new ArrayList<>(locales.length);
    for (Locale locale : locales) {
      Map<String, Object> language = new HashMap<>();
      language.put("code", locale.getLanguage());
      language.put("label", locale.getDisplayLanguage());
      this.languages.add(language);
    }

    this.map = new HashMap<>(4);
    this.map.put("code", name());
    this.map.put("label", this.label);
    this.map.put("phone", this.phone);
    this.map.put("languages", this.languages);
  }

  public String getLabel() {
    return label;
  }

  public Map<String, Object> getPhone() {
    return phone;
  }

  public List<Map<String, Object>> getLanguages() {
    return languages;
  }

  public Map<String, Object> getMap() {
    return map;
  }

  public static List<Map<String, Object>> regions() {
    List<Map<String, Object>> regions = new ArrayList<>();
    for (Region region : Region.values()) {
      regions.add(region.getMap());
    }
    return regions;
  }

}
