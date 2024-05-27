package com.royalcybercopilot.core.servlets;

import com.royalcybercopilot.core.constants.TrialCoPilotConstants;
import java.io.IOException;
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
    "sling.servlet.paths=" + "/bin/multi/component/nodename",
})
public class AllMultiNodeNameServlet extends SlingSafeMethodsServlet {

  private static final Logger log = LoggerFactory.getLogger(
      AllMultiNodeNameServlet.class);

  @Override
  protected void doGet(
      SlingHttpServletRequest request,
      SlingHttpServletResponse response) throws ServletException, IOException {
    String contentPath = request.getParameter(
        TrialCoPilotConstants.CONTENT_PATH);
    log.info(contentPath);

    // Get the resource resolver from the request.
    ResourceResolver resourceResolver = request.getResourceResolver();

    // Resolve the component path.
    Resource componentResource = resourceResolver.getResource(contentPath);
    if (componentResource != null) {
      Node componentNode = componentResource.adaptTo(Node.class);

      try {
        NodeIterator nodeIterator = componentNode.getNodes();
        while (nodeIterator.hasNext()) {
          Node childNode = nodeIterator.nextNode();
          log.info(childNode.getName());
          response.getWriter().println(childNode.getName());
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
  }
}
