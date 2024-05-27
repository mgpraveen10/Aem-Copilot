package com.royalcybercopilot.core.servlets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponse {

  List<ImageData> data;
  Error error;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ImageData {

    private String url;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Error {

    private String code;
    private String message;
  }
}
