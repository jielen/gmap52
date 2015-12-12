package com.kingdrive.framework.controller.web;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MappingsXMLDao {

  private static final String EXCEPTION_MAPPING = "exception-mapping";

  private static final String URL_MAPPING = "url-mapping";

  private static final String WEB_ACTION_CLASS = "web-action-class";

  private static final String HANDLE_EVENT = "handle-event";

  private static final String HANDLE_RESULT = "handle-result";

  private static final String URL = "url";

  private static final String SCREEN = "screen";

  private static final String CLEARPARAMETER = "clearparameter";

  private static final String CLEARTEMP = "cleartemp";

  private static final String HANDLE_EVENT_NAME = "name";

  private static final String HANDLE_RESULT_NAME = "name";

  public MappingsXMLDao() {
  }

  public static Element loadDocument(String location) {
    Document doc = null;
    try {
      URL url = new URL(location);
      InputSource xmlInp = new InputSource(url.openStream());

      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
          .newInstance();
      DocumentBuilder parser = docBuilderFactory.newDocumentBuilder();
      doc = parser.parse(xmlInp);
      Element root = doc.getDocumentElement();
      root.normalize();
      return root;
    } catch (SAXParseException err) {
      System.err.println("URLMappingsXmlDAO ** Parsing error" + ", line "
          + err.getLineNumber() + ", uri " + err.getSystemId());
      System.err.println("URLMappingsXmlDAO error: " + err.getMessage());
    } catch (SAXException e) {
      System.err.println("URLMappingsXmlDAO error: " + e);
    } catch (java.net.MalformedURLException mfx) {
      System.err.println("URLMappingsXmlDAO error: " + mfx);
    } catch (java.io.IOException e) {
      System.err.println("URLMappingsXmlDAO error: " + e);
    } catch (Exception pce) {
      System.err.println("URLMappingsXmlDAO error: " + pce);
    }
    return null;
  }

  public static HashMap loadRequestMappings(String location) {
    Element root = loadDocument(location);
    return getRequestMappings(root);
  }

  public static String loadExceptionScreen(String location) {
    String screen = null;

    Element root = loadDocument(location);
    NodeList list = root.getElementsByTagName(EXCEPTION_MAPPING);
    Node node = list.item(0);
    if (node != null && node instanceof Element) {
      Element element = (Element) node;
      screen = element.getAttribute(SCREEN);
    }

    return screen;
  }

  public static HashMap getRequestMappings(Element root) {
    HashMap urlMappings = new HashMap();
    NodeList list = root.getElementsByTagName(URL_MAPPING);
    for (int loop = 0; loop < list.getLength(); loop++) {
      Node urlNode = list.item(loop);
      if (urlNode != null) {
        String url = "";
        String defaultScreen = null;
        String webActionClass = null;
        List eventMappings = null;
        boolean isClearParameter = false;
        boolean isClearTemp = false;
        if (urlNode instanceof Element) {
          Element urlElement = (Element) urlNode;
          url = urlElement.getAttribute(URL);
          defaultScreen = urlElement.getAttribute(SCREEN);
          String clearParameter = urlElement.getAttribute(CLEARPARAMETER);
          if (clearParameter != null && "true".equals(clearParameter)) {
            isClearParameter = true;
          }
          String clearTemp = urlElement.getAttribute(CLEARTEMP);
          if (clearTemp != null && "true".equals(clearTemp)) {
            isClearTemp = true;
          }
          webActionClass = getSubTagValue(urlElement, WEB_ACTION_CLASS);

          NodeList eventNodeList = urlElement
              .getElementsByTagName(HANDLE_EVENT);
          if (eventNodeList.getLength() > 0) {
            eventMappings = new ArrayList();
          }
          for (int eventloop = 0; eventloop < eventNodeList.getLength(); eventloop++) {
            Node eventNode = eventNodeList.item(eventloop);
            EventMapping eventMapping = null;
            String eventName = "";
            if (eventNode instanceof Element) {
              Element eventElement = (Element) eventNode;
              eventName = eventElement.getAttribute(HANDLE_EVENT_NAME);
              eventMapping = new EventMapping(eventName);
              NodeList resultList = eventElement
                  .getElementsByTagName(HANDLE_RESULT);
              for (int resultLoop = 0; resultLoop < resultList.getLength(); resultLoop++) {
                Node resultNode = resultList.item(resultLoop);
                if (resultNode instanceof Element) {
                  Element resultElement = (Element) resultNode;
                  String result = resultElement
                      .getAttribute(HANDLE_RESULT_NAME);
                  String screen = resultElement.getAttribute(SCREEN);
                  eventMapping.putScreen(result, screen);
                }
              }
            }
            if (eventMapping != null) {
              eventMappings.add(eventMapping);
            }
          }

          URLMapping urlMapping = new URLMapping(url, defaultScreen,
              webActionClass, eventMappings, isClearParameter, isClearTemp);
          urlMappings.put(url, urlMapping);
        }
      }
    }
    return urlMappings;
  }

  private static String getSubTagValue(Node node, String subTagName) {
    String returnString = null;
    if (node != null) {
      NodeList children = node.getChildNodes();
      for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
        Node child = children.item(innerLoop);
        if ((child != null) && (child.getNodeName() != null)
            && child.getNodeName().equals(subTagName)) {
          Node grandChild = child.getFirstChild();
          if (grandChild.getNodeValue() != null)
            return grandChild.getNodeValue();
        }
      }
    }
    return returnString;
  }

  public static void main(String args[]) {
    // HashMap map=
    // loadRequestMappings("D:\\resin-2.1.9\\webapps\\workflow\\WEB-INF\\classes\\mappings.xml");//
    // System.out.println(map);
  }
}
