package com.royalcybercopilot.core.servlets;

import com.google.gson.JsonObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.royalcybercopilot.core.services.impl.AiTokenConfigurationsImpl;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.Servlet;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION +
        "=JSON Servlet to read the data for RTE ChatGPT Integration",
    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
    "sling.servlet.paths=" + "/bin/GenAI",
})
public class GenAIServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 4438376868274173005L;
  private static final Logger log = LoggerFactory.getLogger(GenAIServlet.class);
  private static final String MODEL = "model";
  private static final String MAX_TOKENS = "max_tokens";
  private static final String PROMPT = "prompt";
  private static final String MODEL_VALUE = "gpt-3.5-turbo-instruct";

  @Reference
  AiTokenConfigurationsImpl aiTokenConfigurations;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  protected void doPost(
      SlingHttpServletRequest req,
      SlingHttpServletResponse res) throws IOException {
    int socketTimeout = 20;
    int connectionTimeout = 10;
    final String CHATGPT_ENDPOINT = "https://api.openai.com/v1/completions";
    String result = "";
    String promptValue = convertToASC(req.getParameter("promptvalue"));
    String language = convertToASC(req.getParameter("lang"));
    String targetField = convertToASC(req.getParameter("targetField"));
    String feature = convertToASC(req.getParameter("Feature"));
    req.setCharacterEncoding("UTF-8");
    res.setCharacterEncoding("UTF-8");

    JsonObject requestJson = new JsonObject();
    requestJson.addProperty(MODEL, MODEL_VALUE);

    if (feature != null && feature.equals("ContentGeneration")) {

      String promptModification = "Generate ";
      String targetFieldInLower = targetField.toLowerCase();
      if (targetFieldInLower.contains("heading")) {
        promptModification += " SEO Heading in less than 6 words ";
      } else if (targetFieldInLower.contains("pretitle")) {
        promptModification += " Short Description less than 40 words in ";
      } else if (targetFieldInLower.contains("title")) {
        promptModification += "Title less than 10 words  ";
      } else if (targetFieldInLower.contains("text") || targetFieldInLower.contains("description")) {
        promptModification += " Long Description in ";
      }

      final String PROMPT_VALUE = promptModification + language + " Language :"
          + promptValue;
      String requestBody = MAPPER.writeValueAsString(
          TextRequest
              .builder()
              .prompt(
                  PROMPT_VALUE)
              .n(3)
              .build());
      log.info("prompt value {}", requestBody);
      final int MAX_TOKENS_VALUE = 1248;
      requestJson.addProperty(PROMPT, requestBody);
      // requestJson.addProperty(PROMPT, PROMPT_VALUE);
      requestJson.addProperty(MAX_TOKENS, MAX_TOKENS_VALUE);
    } else if (feature != null && feature.equals("TextCorrection")) {
      final String PROMPT_VALUE = "Correct this to standard " +
          language +
          " Language and spell check: " +
          promptValue;
      final int MAX_TOKENS_VALUE = 624;
      requestJson.addProperty(PROMPT, PROMPT_VALUE);
      requestJson.addProperty(MAX_TOKENS, MAX_TOKENS_VALUE);
    } else if (feature != null && feature.equals("summarize")) {
      final String PROMPT_VALUE = "Generate brief summarize in language " + language + ": " + promptValue;
      final int MAX_TOKENS_VALUE = 624;
      requestJson.addProperty(PROMPT, PROMPT_VALUE);
      requestJson.addProperty(MAX_TOKENS, MAX_TOKENS_VALUE);
    } else {
      res.getWriter().println("Please choose feature and provide content");
    }

    HttpPost httpPost = new HttpPost(CHATGPT_ENDPOINT);
    httpPost.setHeader(
        "Authorization",
        "Bearer " + aiTokenConfigurations.getOpenAiToken());
    httpPost.setHeader("Content-type", "application/json");

    StringEntity stringEntity = new StringEntity(requestJson.toString());
    httpPost.setEntity(stringEntity);

    RequestConfig config = RequestConfig
        .custom()
        .setConnectTimeout(connectionTimeout * 2000)
        .setSocketTimeout(socketTimeout * 2000)
        .build();

    try (
        CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(config)
            .build();
        CloseableHttpResponse response = httpClient.execute(httpPost)) {
      result = EntityUtils.toString(response.getEntity());
      res.getWriter().println(result);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      res.getWriter().println(e.getMessage());
    }
  }

  public String convertToASC(String str) {
    byte[] englishBytes;
    englishBytes = str.getBytes();
    return new String(englishBytes, StandardCharsets.US_ASCII);
  }
}
