package com.royalcybercopilot.core.servlets;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = Servlet.class, property = {
    "sling.servlet.methods=GET",
    "sling.servlet.paths=/bin/updateMultiNode/ItemProperty",
})
public class GenAiUpdateMultiNodePropServlet extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(
      GenAiUpdateMultiNodePropServlet.class);

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Override
  protected void doGet(
      SlingHttpServletRequest request,
      SlingHttpServletResponse response) {
    ResourceResolver resourceResolver = request.getResourceResolver();
    try {
      String path = request.getParameter("targetNode");
      LOG.info(path);
      String nodeName = request.getParameter("targetMultiNodeName");
      LOG.info(nodeName);
      String name = request.getParameter("propertyName");
      LOG.info(name);
      String value = request.getParameter("propertyValue");
      LOG.info(value);
      String[] names = name.split("_./");
      String completePath = path + "/" + names[1] + "/" + nodeName;
      LOG.info("complete node path {}", completePath);
      Resource resource = resourceResolver.getResource(completePath);
      LOG.info("path= {}", resource);
      if (resource != null) {
        addProperty(names[0], value, resource, response);
      }
    } catch (Exception e) {
      LOG.error("error while reading the {}", e);
    }
  }

  private void addProperty(
      String name,
      String value,
      Resource resource,
      SlingHttpServletResponse response) throws IOException {
    ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
    Iterator<String> i = valueMap.keySet().iterator();
    while (i.hasNext())
      LOG.info(i.next().toString());
    if (valueMap != null) {
      if (valueMap.containsKey(name)) {
        valueMap.replace(name, value);
        LOG.info("info info info");
      } else {
        valueMap.putIfAbsent(name, value);
        LOG.info("Extra Info");
      }
    }
    response.getWriter().println("getServletName()");
    resource.getResourceResolver().commit();
  }
}
