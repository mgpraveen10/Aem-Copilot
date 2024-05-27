package com.royalcybercopilot.core.servlets;

import com.google.gson.JsonObject;
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
        "=JSON Servlet to read the data from the external webservice",
    "sling.servlet.methods=" + HttpConstants.METHOD_POST,
    "sling.servlet.paths=" + "/bin/chatgpt/seoexpertjob",
})
public class ChatGptSeoServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 4438376868274173005L;

  private static final Logger log = LoggerFactory.getLogger(
      ChatGptSeoServlet.class);
  @Reference
  AiTokenConfigurationsImpl aiTokenConfigurations;

  @Override
  protected void doGet(
      SlingHttpServletRequest request,
      SlingHttpServletResponse response) {
    try {
      response.getWriter().println("this is doget response");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  protected void doPost(
      SlingHttpServletRequest req,
      SlingHttpServletResponse res) throws IOException {
    String result = "";
    HttpPost httpPost = new HttpPost("https://api.openai.com/v1/completions");
    httpPost.setHeader(
        "Authorization",
        "Bearer " + aiTokenConfigurations.getOpenAiToken());
    httpPost.setHeader("Content-Type", "application/json");

    String seocontent = req.getParameter("bodyContent");

    byte[] englishBytes = seocontent.getBytes();
    String asciiEncondedEnglishString = new String(
        englishBytes,
        StandardCharsets.US_ASCII);
    JsonObject newjson = new JsonObject();
    newjson.addProperty("model", "gpt-3.5-turbo-instruct");
    newjson.addProperty(
        "prompt",
        "Provide description,keywords and title for SEO for this content as a json: " +
            asciiEncondedEnglishString);
    newjson.addProperty("max_tokens", 2048);

    StringEntity stringEntity = new StringEntity(newjson.toString());
    httpPost.setEntity(stringEntity);
    int timeout = 15;
    RequestConfig config = RequestConfig
        .custom()
        .setConnectTimeout(timeout * 5000)
        .setSocketTimeout(timeout * 1000)
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
      res.getWriter().println(e.getMessage());
    }
  }
}
