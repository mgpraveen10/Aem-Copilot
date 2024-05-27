package com.royalcybercopilot.core.servlets;

import com.google.gson.JsonObject;
import com.royalcybercopilot.core.constants.TrialCoPilotConstants;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION +
        "=JSON Servlet to read the data for component",
    "sling.servlet.methods=" + HttpConstants.METHOD_GET,
    "sling.servlet.paths=" + "/bin/component",
})
public class TrialCoPilotServlet extends SlingSafeMethodsServlet {

  private static final Logger log = LoggerFactory.getLogger(
      TrialCoPilotServlet.class);
  ResourceResolver resourceResolver;

  @Override
  protected void doGet(
      SlingHttpServletRequest request,
      SlingHttpServletResponse response) throws ServletException, IOException {
    String path = request.getParameter(TrialCoPilotConstants.COMPONENT_PATH);

    // Get the resource resolver from the request.
    resourceResolver = request.getResourceResolver();

    // Resolve the component path.
    Resource componentResource = resourceResolver.getResource(path);
    String mf = "";
    Node componentNode = null;
    if (componentResource != null) {
      componentNode = componentResource.adaptTo(Node.class);

      try {
        if (!componentNode.hasNode(TrialCoPilotConstants.CQ_DIALOG_STRING)) {
          path = TrialCoPilotConstants.LIBS_PATH +
              componentNode
                  .getProperty(
                      TrialCoPilotConstants.SLING_RESOURCE_SUPER_TYPE_STRING)
                  .getValue()
                  .getString();
          componentResource = resourceResolver.getResource(path);
          componentNode = componentResource.adaptTo(Node.class);
        }
      } catch (Exception e) {
        log.error("checking the error == {}", e.getMessage());
      }
    }

    try {
      if (Objects.nonNull(componentNode) &&
          componentNode.hasNode(TrialCoPilotConstants.CQ_DIALOG_STRING)) {
        Resource dialogResource = componentResource.getChild(
            TrialCoPilotConstants.CQ_DIALOG_STRING);

        if (dialogResource != null) {
          Node dialogNode = dialogResource.adaptTo(Node.class);
          try {
            traverseNode(dialogNode, response, mf);
          } catch (RepositoryException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
  }

  private void traverseNode(
      Node node,
      SlingHttpServletResponse response,
      String mf) throws RepositoryException, IOException {
    NodeIterator nodeIterator = node.getNodes();

    while (nodeIterator.hasNext()) {
      Node childNode = nodeIterator.nextNode();
      // Process properties of the child node
      if (checkCondition(childNode) &&
          checkAvailability(
              childNode
                  .getProperty(TrialCoPilotConstants.NAME_STRING)
                  .getValue()
                  .getString())) {
        String propertyName = childNode
            .getProperty(TrialCoPilotConstants.NAME_STRING)
            .getValue()
            .getString()
            .replace("./", "") +
            mf;
        String propertyLabel = propertyName;
        if (childNode
            .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
            .getValue()
            .getString()
            .contains("/fileupload")) {
          propertyName = childNode
              .getProperty("fileReferenceParameter")
              .getValue()
              .getString()
              .replace("./", "") +
              mf +
              "_./img";
        }

        if (childNode.hasProperty(TrialCoPilotConstants.FIELD_LABEL_STRING)) {
          propertyLabel = childNode
              .getProperty(TrialCoPilotConstants.FIELD_LABEL_STRING)
              .getValue()
              .getString();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(propertyName, propertyLabel);
        response.getWriter().println(jsonObject.toString());
      } else if ((childNode.hasProperty("path") &&
          childNode
              .getProperty("path")
              .getValue()
              .getString()
              .contains("/image"))
          &&
          (childNode.hasProperty(
              TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING) &&
              childNode
                  .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
                  .getValue()
                  .getString()
                  .contains("/include"))) {
        Resource includeResource = null;
        Node includeNode = null;
        String includePath = "";
        try {
          includePath = TrialCoPilotConstants.LIBS_PATH +
              childNode.getProperty("path").getValue().getString();
          includeResource = resourceResolver.getResource(includePath);
          includeNode = includeResource.adaptTo(Node.class);
        } catch (Exception e) {
          log.error("checking the error == {}", e.getMessage());
        }
        try {
          if (Objects.nonNull(includeNode)) {
            traverseNode(includeNode, response, mf);
          }
        } catch (RepositoryException e) {
          e.printStackTrace();
        }
      }
      String updatedMf = mf;
      if ((childNode.hasProperty(
          TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)) &&
          childNode
              .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
              .getValue()
              .getString()
              .contains("/multifield")) {
        NodeIterator mNodeIterator = childNode.getNodes();
        Node mNode = mNodeIterator.nextNode();
        String multifieldNodeName = mNode
            .getProperty(TrialCoPilotConstants.NAME_STRING)
            .getValue()
            .getString();
        updatedMf = "_" + multifieldNodeName;
        traverseNode(childNode, response, updatedMf);
      } else
        traverseNode(childNode, response, mf);
    }
  }

  public boolean checkCondition(Node childNode) throws RepositoryException {
    return (childNode.hasProperty(TrialCoPilotConstants.NAME_STRING) &&
        childNode.hasProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING) &&
        (childNode
            .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
            .getValue()
            .getString()
            .contains(TrialCoPilotConstants.TEXTFIELD_STRING) ||
            childNode
                .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
                .getValue()
                .getString()
                .contains(TrialCoPilotConstants.TEXTAREA_STRING)
            ||
            childNode
                .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
                .getValue()
                .getString()
                .contains(TrialCoPilotConstants.RICHTEXT_STRING)
            ||
            childNode
                .getProperty(TrialCoPilotConstants.SLING_RESOURCE_TYPE_STRING)
                .getValue()
                .getString()
                .contains("/fileupload")));
  }

  public boolean checkAvailability(String propertyName) {
    String[] listToExclude = {
        "id",
        "label",
        "button",
        "btn",
        "CTA",
        "action",
    };
    String tempProprtyName = propertyName.replace("./", "");

    return Arrays
        .stream(listToExclude)
        .noneMatch(eachExclude -> tempProprtyName.toLowerCase().contains(eachExclude.toLowerCase()));
  }
}
