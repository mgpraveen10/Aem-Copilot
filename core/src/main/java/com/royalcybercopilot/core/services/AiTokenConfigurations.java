package com.royalcybercopilot.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Open Ai Token Configurations", description = "Configure Open Ai Token for Co-pilot")
public @interface AiTokenConfigurations {
  static final String OPENAITOKEN = "";

  @AttributeDefinition(name = "open.ai.token", description = "Provide your Open Ai Token")
  String openAiToken() default OPENAITOKEN;
}
