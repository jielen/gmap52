/*$Id: XMLTools.java,v 1.2 2008/06/02 13:42:26 huangcb Exp $*/
package com.anyi.gp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.anyi.gp.BusinessException;

public class XMLTools {

  private static final Logger logger = Logger.getLogger(XMLTools.class);
  
  private XMLTools() {
  }
  
  public static Node selectSingleNode(Node node,String name){
    try {
      return XPathAPI.selectSingleNode(node,name);
    } catch (TransformerException e) {
      logger.error(e);
      throw new RuntimeException(e);
    }
  }

  public static NodeList selectNodeList(Node node,String name){
    try {
      return XPathAPI.selectNodeList(node, name);
    } catch (TransformerException e) {
      logger.error(e);
      throw new RuntimeException(e);
    }
  }
  
  public static Document inputStreamToDocument(InputStream is) {
    if (is == null) {
      throw new IllegalArgumentException(
        "XMLTools类的inputStreamToDocument方法：参数为null。");
    }
    return inputSourceToDocument(new InputSource(is));
  }

  private static DocumentBuilderFactory g_dbf = DocumentBuilderFactory.newInstance();

  public static Document inputSourceToDocument(InputSource is) {
    Document doc = null;
    try {
      DocumentBuilder db = g_dbf.newDocumentBuilder();
      doc = db.parse(is);
    } catch (ParserConfigurationException e) {
      logger.error(e);
      throw new RuntimeException("XMLTools类的inputStreamToDocument方法",e);
    } catch (SAXException e) {
      logger.error(e);
      throw new RuntimeException("XMLTools类的inputStreamToDocument方法",e);
    } catch (IOException e) {
      logger.error(e);
      throw new RuntimeException("XMLTools类的inputStreamToDocument方法",e);      
    }
    return doc;
  }

  public static Document stringToDocument(String s) {
    if (s == null || s.trim().equals(""))
      return null;
    InputSource is = new InputSource(new StringReader(s));
    return inputSourceToDocument(is);
  }

  public static void nodeToStringWithLimitedDeep(Node node, StringBuffer result,
    StringBuffer indent, int deep) {
    deep = deep + 1;
    if (node == null) {
      return;
    }
    switch (node.getNodeType()) {
    case Node.DOCUMENT_NODE:
      if (node.hasChildNodes()) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
          nodeToStringWithLimitedDeep(nodeList.item(i), result, indent, deep);
        }
      }
      break;
    case Node.ATTRIBUTE_NODE:
      String strNode = node.getNodeValue();
      if (strNode != null) {
        strNode = StringTools.lt(strNode);
        strNode = StringTools.gt(strNode);
      }
      result.append(" " + node.getNodeName() + "=\"" + strNode + "\"");
      break;
    case Node.CDATA_SECTION_NODE:
      result.append(indent.toString() + "<![CDATA\n"
        + ((CDATASection) node).getData() + "\n" + indent.toString() + "]]>\n");
      break;
    case Node.COMMENT_NODE:
      result.append(indent.toString() + "<!--" + ((Comment) node).getData()
        + " -->\n");
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      result.append(indent.toString() + "<" + node.getNodeName());
      if (node.hasAttributes()) {
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
          nodeToStringWithLimitedDeep(nnm.item(i), result, indent, deep);
        }
      }
      if (!node.hasChildNodes()) {
        result.append("/>");
      } else {
        result.append(">\n");
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
          nodeToStringWithLimitedDeep(nl.item(i), result, indent.append("  "), deep);
        }
        result.append("</" + node.getNodeName() + ">\n");
      }
      break;
    case Node.DOCUMENT_TYPE_NODE:
      DocumentType docType = (DocumentType) node;
      result.append(indent.toString() + "<!DOCTYPE " + docType.getName() + " ");
      result.append((docType.getPublicId() != "") ? "PUBLIC \""
        + docType.getPublicId() + "\">" : "");
      result.append((docType.getSystemId() != "") ? "SYSTEM \""
        + docType.getSystemId() + "\">" : "");
      break;
    case Node.ELEMENT_NODE:
      Element element = (Element) node;
      result.append("<" + element.getNodeName() + " ");
      if (element.hasAttributes()) { //
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
          nodeToStringWithLimitedDeep(nnm.item(i), result, indent, deep);
        }
      }
      if (!element.hasChildNodes()) {
        result.append("/>");
      } else {
        result.append(">");// zhangcheng remove the \n
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
          nodeToStringWithLimitedDeep(nl.item(i), result, indent.append("  "), deep);
        }
        result.append("</" + element.getNodeName() + ">");
      }
      break;
    case Node.ENTITY_NODE:
      break;
    case Node.ENTITY_REFERENCE_NODE:
      break;
    case Node.NOTATION_NODE:
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      ProcessingInstruction pi = (ProcessingInstruction) node;
      result.append(indent.toString() + "<?" + pi.getTarget() + " " + pi.getData()
        + " ?>\n");
      break;
    case Node.TEXT_NODE:
      Text txt = (Text) node;
      String strText = txt.getData();
      if (strText != null) {
        strText = StringTools.lt(strText);
        strText = StringTools.gt(strText);
      }
      result.append(strText);// zhangcheng remove the \n 4 xpath
      break;
    default:
      break;
    }
  }

  public static void nodeToString(Node node, StringBuffer result, StringBuffer indent) {
    if (node == null) {
      return;
    }
    switch (node.getNodeType()) {
    case Node.DOCUMENT_NODE:
      if (node.hasChildNodes()) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
          nodeToString(nodeList.item(i), result, indent);
        }
      }
      break;
    case Node.ATTRIBUTE_NODE:
      String strNode = node.getNodeValue();
      if (strNode != null) {
        strNode = StringTools.lt(strNode);
        strNode = StringTools.gt(strNode);
      }
      result.append(" " + node.getNodeName() + "=\"" + strNode + "\"");
      break;
    case Node.CDATA_SECTION_NODE:
      result.append(indent.toString() + "<![CDATA\n"
        + ((CDATASection) node).getData() + "\n" + indent.toString() + "]]>\n");
      break;
    case Node.COMMENT_NODE:
      result.append(indent.toString() + "<!--" + ((Comment) node).getData()
        + " -->\n");
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      result.append(indent.toString() + "<" + node.getNodeName());
      if (node.hasAttributes()) {
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
          nodeToString(nnm.item(i), result, indent);
        }
      }
      if (!node.hasChildNodes()) {
        result.append("/>");
      } else {
        result.append(">\n");
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
          nodeToString(nl.item(i), result, indent.append("  "));
        }
        result.append("</" + node.getNodeName() + ">\n");
      }
      break;
    case Node.DOCUMENT_TYPE_NODE:
      DocumentType docType = (DocumentType) node;
      result.append(indent.toString() + "<!DOCTYPE " + docType.getName() + " ");
      result.append((docType.getPublicId() != "") ? "PUBLIC \""
        + docType.getPublicId() + "\">" : "");
      result.append((docType.getSystemId() != "") ? "SYSTEM \""
        + docType.getSystemId() + "\">" : "");
      break;
    case Node.ELEMENT_NODE:
      Element element = (Element) node;
      result.append("<" + element.getNodeName() + " ");
      if (element.hasAttributes()) { //
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
          nodeToString(nnm.item(i), result, indent);
        }
      }
      if (!element.hasChildNodes()) {
        result.append("/>");
      } else {
        result.append(">");// zhangcheng remove the \n
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
          nodeToString(nl.item(i), result, indent.append("  "));
        }
        result.append("</" + element.getNodeName() + ">");
      }
      break;
    case Node.ENTITY_NODE:
      break;
    case Node.ENTITY_REFERENCE_NODE:
      break;
    case Node.NOTATION_NODE:
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      ProcessingInstruction pi = (ProcessingInstruction) node;
      result.append(indent.toString() + "<?" + pi.getTarget() + " " + pi.getData()
        + " ?>\n");
      break;
    case Node.TEXT_NODE:
      Text txt = (Text) node;
      String strText = txt.getData();
      if (strText != null) {
        strText = StringTools.lt(strText);
        strText = StringTools.gt(strText);
      }
      result.append(strText);// zhangcheng remove the \n 4 xpath
      break;
    default:
      break;
    }
  }

  public static final String nodeToStringWithLimitedDeep(Node node) {
    if (node == null) {
      return "";
    }
    StringBuffer result = new StringBuffer(512);
    StringBuffer indent = new StringBuffer();
    nodeToStringWithLimitedDeep(node, result, indent, 0);
    return result.toString();
  }

  public static final String nodeToString(Node node) {
    if (node == null) {
      return "";
    }
    StringBuffer result = new StringBuffer(""), indent = new StringBuffer("");
    nodeToString(node, result, indent);
    return result.toString();
  }

  // leidh; 20041219;
  public static String getNodeText(Node node) {
    return getNodeText(node, 1);
  }

  private static String getNodeText(Node node, int level) {
    if (node == null)
      return "";
    if (level < 0)
      return "";
    level--;
    StringBuffer result = new StringBuffer("");
    if (node.getNodeType() == Node.TEXT_NODE) {
      result.append(getNodeValue(node));
    } else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
      result.append(getNodeValue(node));
    } else if (node.getNodeType() == Node.ELEMENT_NODE) {
      if (node.hasChildNodes()) {
        for (int i = 0, len = node.getChildNodes().getLength(); i < len; i++) {
          result.append(XMLTools.getNodeText(node.getChildNodes().item(i), level));
        }
      }
    } else {
      // 其他不用处理;
    }
    return result.toString();
  }

  public static void setNodeText(Node node, String text) {
    if (node == null)
      return;
    if (node.getNodeType() == Node.TEXT_NODE) {
      node.setNodeValue(text);
    } else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
      node.setNodeValue(text);
    } else if (node.getNodeType() == Node.ELEMENT_NODE) {
      if (node.hasChildNodes()) {
        Node voNode = null;
        for (int i = 0, len = node.getChildNodes().getLength(); i < len; i++) {
          voNode = node.getChildNodes().item(i);
          if (voNode.getNodeType() == Node.TEXT_NODE) {
            voNode.setNodeValue(text);
            break;
          } else if (voNode.getNodeType() == Node.CDATA_SECTION_NODE) {
            voNode.setNodeValue(text);
            break;
          } else {
            // 其他不用处理;
          }
        }
      } else {
        node.appendChild(node.getOwnerDocument().createTextNode(text));
      }
    }
  }

  /**
   * 获取 node value;
   *
   * @param node
   * @return
   */
  public static String getNodeValue(Node node) {
    if (node == null)
      return "";
    String vsValue = node.getNodeValue();
    return vsValue == null ? "" : vsValue;
  }

  // leidh; 20050328;
  public static String getNodeAttr(Node node, String attrname) {
    return XMLTools.getNodeAttr(node, attrname, "");
  }

  /**
   * 获取结点属性;
   * @param node
   * @param attrname
   * @param defReturn
   * @return String;
   */
  public static String getNodeAttr(Node node, String attrname, String defreturn) {
    if (node == null)
      return defreturn;
    if (node.hasAttributes() == false)
      return defreturn;
    Node voAttr = node.getAttributes().getNamedItem(attrname);
    if (voAttr == null)
      return defreturn;
    return getNodeValue(voAttr);
  }

  /**
   * 设置节点的属性值.
   *
   * @param node
   * @param attrname
   * @param value
   */
  public static void setNodeAttr(Node node, String attrname, String value) {
    if (node == null)
      return;
    if (node.getNodeType() != Node.ELEMENT_NODE){
      return;
    }
    ((Element)node).setAttribute(attrname, value);
  }

  // leidh;20040823;
  // 检查字符串中是否有 XML 拼串中的敏感字符;
  public static boolean isValidStringForXML(String text) {
    if (text == null)
      return true;
    if (text.trim().length() == 0)
      return true;
    if (text.indexOf("<") >= 0)
      return false;
    if (text.indexOf("\"") >= 0)
      return false;
    if (text.indexOf("\'") >= 0)
      return false;
    return true;
  }

  // leidh;20050915;
  // 获取对于 xml 的有效字符串;
  public static String getValidStringForXML(String text) {
    if (text == null)
      return "";
    if (text.trim().length() == 0)
      return "";
    String vsText = StringTools.replaceAll(text, "<", "&lt;");
    vsText = StringTools.replaceAll(vsText, ">", "&gt;");
    vsText = StringTools.replaceAll(vsText, "\"", "&quot;");
    return vsText;
  }

  /**
   * 获取有效的 Node List;
   *
   * @param oNode
   * @return List
   */
  public static List getValidChildNodeList(Node oNode) {
    NodeList voNodeList = oNode.getChildNodes();
    List voList = new ArrayList();
    for (int i = 0, len = voNodeList.getLength(); i < len; i++) {
      Node child = voNodeList.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        voList.add(child);
      }
      if (child.getNodeType() == Node.TEXT_NODE
        || child.getNodeType() == Node.CDATA_SECTION_NODE) {
        String value = getNodeText(child);
        if (value != null && !value.trim().equals("")) {
          voList.add(child);
        }
      }
    }
    return voList;
  }

  public static void trimChildNodes(Node node) {
    if (node == null)
      return;
    for (int i = node.getChildNodes().getLength() - 1; i >= 0; i--) {
      Node child = node.getChildNodes().item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE)
        continue;
      if (child.getNodeType() == Node.TEXT_NODE
        || child.getNodeType() == Node.CDATA_SECTION_NODE) {
        String value = getNodeText(child);
        if (value == null || value.trim().equals("")) {
          node.removeChild(child);
        }
      } else if (child.getNodeType() == Node.COMMENT_NODE
        || child.getNodeType() == Node.NOTATION_NODE) {
        node.removeChild(child);
      }
    }
  }

  /**
   * 将TableData的xml格式转换成string格式
   * toXml()不考虑子表，所以这里也不考虑子表
   * dataXml格式：<row>  <field1>value1</field1>  <field2>value2</field2>...</row>
   * @return 返回的格式:<entity name="name"><field name="field1" value="value1">
   *               <field name="field1" value="value1"/>
   *            </entity>
   *
   */
  public static String parseTableDataXml2Str(String dataXml, String tableName) {
    StringBuffer dataStr = new StringBuffer();
    dataStr.append("<entity name=\"");
    dataStr.append(tableName);
    dataStr.append("\">\n");
    Document xmlData = XMLTools.stringToDocument(dataXml);
    Node rowNode = xmlData.getFirstChild();
    NodeList fieldList = rowNode.getChildNodes();
    Node field;
    String fieldName;
    String fieldValue;
    for (int i = 0; i < fieldList.getLength(); i++) {
      field = fieldList.item(i);
      fieldName = field.getNodeName();
      fieldValue = XMLTools.getNodeText(field);
      if (fieldName.indexOf("#") >= 0)
        continue;
      dataStr.append("<field name=\"");
      dataStr.append(fieldName);
      dataStr.append("\" value=\"");
      dataStr.append(fieldValue);
      dataStr.append("\" />\n");
    }
    dataStr.append("</entity>");
    return dataStr.toString();
  }

  public static String xmlToText(String xml) {
    return xmlToText(xml, 0);
  }

  public static String xmlToText(String xml, int level) {
    xml = xml.replaceAll("<", "%LESSTHEN" + level + "%");
    xml = xml.replaceAll(">", "%MORETHEN" + level + "%");
    return xml;
  }

  public static String textToXml(String text) {
    return textToXml(text, 0);
  }

  public static String textToXml(String text, int level) {
    text = text.replaceAll("%LESSTHEN" + level + "%", "<");
    text = text.replaceAll("%MORETHEN" + level + "%", ">");
    return text;
  }

  public static String escapeXmlMark2(String xml) {
    xml = xml.replaceAll("<", "&lt;");
    xml = xml.replaceAll(">", "&gt;");
    xml = xml.replaceAll("\"", "&quot;");
    return xml;
  }

  public static String escapeXmlMark(String xml) {
    xml = xml.replaceAll("<", "&lt;");
    xml = xml.replaceAll(">", "&gt;");
    return xml;
  }

  public static void escapeXmlMark(Node node) throws BusinessException {
    if (node == null)
      return;
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      for (int i = 0; i < node.getAttributes().getLength(); i++) {
        Node attr = node.getAttributes().item(i);
        attr.setNodeValue(escapeXmlMark2(attr.getNodeValue()));
      }
    } else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
      setNodeText(node, escapeXmlMark2(getNodeText(node)));
    } else if (node.getNodeType() == Node.TEXT_NODE) {
      setNodeText(node, escapeXmlMark2(getNodeText(node)));
    } else if (node.getNodeType() == Node.COMMENT_NODE) {
    } else {
    }
    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      escapeXmlMark(node.getChildNodes().item(i));
    }
  }

  /**
   * 一般用于不同Document之间的结点或同一Document中不结点名称结点之间的属性复制;
   * @param from
   * @param to
   */
  public static void copyAttrs(Node from, Node to) {
    if (from == null)
      return;
    if (from.getNodeType() != Node.ELEMENT_NODE)
      return;
    if (to == null)
      return;
    if (to.getNodeType() != Node.ELEMENT_NODE)
      return;
    for (int i = 0; i < from.getAttributes().getLength(); i++) {
      Node fromAttr = from.getAttributes().item(i);
      Node toAttr = to.getAttributes().getNamedItem(fromAttr.getNodeName());
      if (toAttr == null) {
        toAttr = to.getOwnerDocument().createAttribute(fromAttr.getNodeName());
        to.getAttributes().setNamedItem(toAttr);
      }
      toAttr.setNodeValue(fromAttr.getNodeValue());
    }
  }

  /**
   * 一般用于不同Document之间的结点复制;
   * @param sourceNode
   * @param containerNode
   * @param isDeep
   * @return
   * @throws BusinessException
   */
  public static Node copyNode(Node sourceNode, Node containerNode, boolean isDeep)
    throws BusinessException {
    if (sourceNode == null)
      return null;
    if (containerNode == null)
      return null;
    Node newNode = null;
    if (sourceNode.getNodeType() == Node.ELEMENT_NODE) {
      newNode = containerNode.getOwnerDocument().createElement(
        sourceNode.getNodeName());
      copyAttrs(sourceNode, newNode);
    } else if (sourceNode.getNodeType() == Node.CDATA_SECTION_NODE) {
      newNode = containerNode.getOwnerDocument().createCDATASection(
        sourceNode.getNodeValue());
    } else if (sourceNode.getNodeType() == Node.TEXT_NODE) {
      newNode = containerNode.getOwnerDocument().createTextNode(
        sourceNode.getNodeValue());
    } else if (sourceNode.getNodeType() == Node.COMMENT_NODE) {
      newNode = containerNode.getOwnerDocument().createComment(
        sourceNode.getNodeValue());
    } else {
      throw new BusinessException("\nXMLTools.copyNode():\n不可复制的结点;");
    }
    containerNode.appendChild(newNode);
    if (!isDeep)
      return newNode;
    for (int i = 0; i < sourceNode.getChildNodes().getLength(); i++) {
      copyNode(sourceNode.getChildNodes().item(i), newNode, isDeep);
    }
    return newNode;
  }
}
