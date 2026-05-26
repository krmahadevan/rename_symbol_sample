package com.playground;

import org.codehaus.jackson.map.ObjectMapper;
import java.util.Map;

public class ConfigReader {

  private final ObjectMapper mapper;

  public ConfigReader(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public Map<String, String> read(String json) throws Exception {
    return mapper.readValue(json, Map.class);
  }
}