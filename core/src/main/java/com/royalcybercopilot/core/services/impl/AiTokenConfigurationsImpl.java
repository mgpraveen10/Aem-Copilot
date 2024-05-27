package com.royalcybercopilot.core.services.impl;

import com.royalcybercopilot.core.services.AiTokenConfigurations;
import lombok.Getter;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = AiTokenConfigurationsImpl.class, immediate = true)
@Designate(ocd = AiTokenConfigurations.class)
public class AiTokenConfigurationsImpl {

  @Getter
  private String openAiToken;

  @Activate
  @Modified
  protected void activate(final AiTokenConfigurations config) {
    openAiToken = config.openAiToken();
  }
}
