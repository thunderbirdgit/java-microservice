package com.openease.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * JSON Password serializer
 *
 * @author Alan Czajkowski
 */
public class JsonPasswordSerializer extends JsonSerializer<String> {

  public static final String MASK = "********";

  @Override
  public void serialize(String value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    generator.writeString(MASK);
  }

}
