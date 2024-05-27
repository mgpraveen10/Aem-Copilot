package com.royalcybercopilot.core.servlets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InputData {

  private String prompt;
  private String damfolderpath;
  private String imagename;
  private String operationname;
  private String componentpath;
  private String remoreImageURL;
  private String sizevalue;
}
