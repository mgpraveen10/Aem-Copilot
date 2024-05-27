package com.royalcybercopilot.core.servlets;

import java.io.IOException;
import java.util.Iterator;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
    "sling.servlet.methods=GET", "sling.servlet.paths=/bin/updateProperty",
})
public class GenAiUpdateNodeServlet extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(
      GenAiUpdateNodeServlet.class);

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
      String name = request.getParameter("propertyName");
      LOG.info(name);
      String value = request.getParameter("propertyValue");
      LOG.info(value);
      Resource resource = resourceResolver.getResource(path);
      if (resource != null) {
        if (name.contains("_./")) {
          addMultiNode(path, name, value, request, response);
        } else {
          addProperty(path, name, value, resource, response);
        }
      }
    } catch (Exception e) {
      LOG.error("error while reading the {}", e);
    }
  }

  private void addProperty(
      String path,
      String name,
      String value,
      Resource resource,
      SlingHttpServletResponse response) throws IOException {
    ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
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

  private void addMultiNode(
      String parentNodePath,
      String name,
      String value,
      SlingHttpServletRequest request,
      SlingHttpServletResponse response) {
    try {
      LOG.info(parentNodePath);
      Session session = request.getResourceResolver().adaptTo(Session.class);
      Node parentNode = session.getNode(parentNodePath);
      String[] names = name.split("_./");
      String newNodeName = names[1];
      name = names[0];
      // to check if multifield node is already created
      Node mNodeIterator = null;

      if (!checkNodeExists(parentNodePath, session, newNodeName)) {
        mNodeIterator = parentNode.addNode(newNodeName);
      } else {
        mNodeIterator = parentNode.getNode(newNodeName);
      }

      LOG.info("node name {}", mNodeIterator.getName());
      int nextNodeNumber = nextNodeNumber(mNodeIterator);
      Node newItemNode = mNodeIterator.addNode("item" + nextNodeNumber);
      LOG.info("index {}", nextNodeNumber);

      // Set properties for the new node
      newItemNode.setProperty(name, value);

      // Save the session to persist the changes
      session.save();
      LOG.info("myNewNode {}", newNodeName);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
  }

  private boolean checkNodeExists(
      String path,
      Session session,
      String nodeName) {
    try {
      String nodePath = path + "/" + nodeName;
      LOG.info("nodpatj {}", nodePath);
      return session.nodeExists(nodePath);
    } catch (RepositoryException e) {
      e.printStackTrace();
      return false;
    }
  }

  private int nextNodeNumber(Node parent) throws RepositoryException {
    NodeIterator nodeIterator = parent.getNodes();
    int max = 0;
    if (nodeIterator.getSize() == max) {
      return max;
    }
    while (nodeIterator.hasNext()) {
      Node childNode = nodeIterator.nextNode();
      LOG.info("child node name {}", childNode.getName());
      int nodeNumber = Integer.parseInt(childNode.getName().substring(4));
      LOG.info("child node number {}", nodeNumber);
      if (nodeNumber > max) {
        max = nodeNumber;
      }
    }
    return max + 1;
  }
}
