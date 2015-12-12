package com.anyi.gp.print.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anyi.gp.util.XMLTools;

public class ParamConverter{
  public ParamConverter(){
  }

  /**
   * ��ҳ�洫����ҳ��Ԫ�طֽ��Java��������
   *
   * @param l
   *          List �����б�
   * @return TextElement[] ��������
   */
  public static TextElement[] split(List l){
    TextElement[] result = new TextElement[l.size()];
    int count = 0;
    for(Iterator iter = l.iterator(); iter.hasNext(); ){
      TextElement ele = (TextElement)iter.next();
      result[count] = ele;
      count++;
    }
    return result;
  }

  /**
   * ��ҳ�洫����ҳ��Ԫ�طֽ��Java��������
   *
   * @param l
   *          List �����б�
   * @return TextElement[] ��������
   */
  public static ParamFieldInfor[] splitParam(List l){
    ParamFieldInfor[] result = new ParamFieldInfor[l.size()];
    int count = 0;
    for(Iterator iter = l.iterator(); iter.hasNext(); ){
      ParamFieldInfor ele = (ParamFieldInfor)iter.next();
      result[count] = ele;
      count++;
    }
    return result;
  }

  /**
   * ��ҳ�洫����ҳ��Ԫ�طֽ��Java��������
   *
   * @param l
   *          List �����б�
   * @return TextElement[] ��������
   */
  public static StaticTextInfor[] splitLabel(List l){
    StaticTextInfor[] result = new StaticTextInfor[l.size()];
    int count = 0;
    for(Iterator iter = l.iterator(); iter.hasNext(); ){
      StaticTextInfor ele = (StaticTextInfor)iter.next();
      result[count] = ele;
      count++;
    }
    return result;
  }

  /**
   * ���ַ�����ʽ��ҳ��Ԫ����Ϣ������Java�����б�
   *
   * @param s
   *          String Ԫ���ֶδ�
   * @return List �����б�
   */
  public static List extractElement(String s){
    List result = new ArrayList();
    if(!s.equalsIgnoreCase("")){
      Document doc = XMLTools.stringToDocument(s);
      NodeList elements = doc.getElementsByTagName("element");
      for(int i = 0, j = elements.getLength(); i < j; i++){
        Node node = elements.item(i);
        TextElement ele = new TextElement(node);
        result.add(ele);
      }
    }
    return result;
  }

  /**
   * ���ַ�����ʽ��ҳ��Ԫ����Ϣ������Java�����б�
   *
   * @param s
   *          String Ԫ���ֶδ�
   * @return List �����б�
   */
  public static List extractLabel(String s){
    List result = new ArrayList();
    if(!s.equalsIgnoreCase("")){
      Document doc = XMLTools.stringToDocument(s);
      NodeList elements = doc.getElementsByTagName("element");
      for(int i = 0, j = elements.getLength(); i < j; i++){
        Node node = elements.item(i);
        StaticTextInfor ele = new StaticTextInfor(node);
        result.add(ele);
      }
    }
    return result;
  }

  /**
   * ���ַ�����ʽ��ҳ��Ԫ����Ϣ������Java�����б�
   *
   * @param s
   *          String Ԫ���ֶδ�
   * @return Object �����б�,��һ��Ԫ���ǲ��������б�,�ڶ��������ǲ���ֵ
   */
  public static Object[] extractParam(String s){
    Object[] results = new Object[2];
    List result = new ArrayList();
    Map paramValues = new HashMap();
    if(!s.equalsIgnoreCase("")){
      Document doc = XMLTools.stringToDocument(s);
      NodeList elements = doc.getElementsByTagName("element");
      for(int i = 0, j = elements.getLength(); i < j; i++){
        Node node = elements.item(i);
        ParamFieldInfor ele = new ParamFieldInfor(node);
        result.add(ele);
      }
      NodeList values = doc.getElementsByTagName("content");
      for(int i = 0, j = elements.getLength(); i < j; i++){
        Node node = values.item(i);
        String value = node.getFirstChild().getNodeValue();
        String name = node.getAttributes().item(0).getNodeValue();
        paramValues.put(name, value);
      }
    }
    results[0] = result;
    results[1] = paramValues;
    return results;
  }

  /**
   * �ַ�����ʽ����ת����HashMap
   *
   * @param str
   *          String �ַ�������
   * @return Map
   */
  public static Map splitParam(String str){
    Map result = new HashMap();
    Document doc = XMLTools.stringToDocument(str);
    NodeList elements = doc.getElementsByTagName("param");
    for(int i = 0, j = elements.getLength(); i < j; i++){
      Node param = elements.item(i);
      NodeList childs = param.getChildNodes();
      String paramName = null;
      String paramValue = null;
      for(int m = 0, n = childs.getLength(); m < n; m++){
        Node child = childs.item(m);
        if(child.getNodeName().equalsIgnoreCase("name")){
          paramName = child.getFirstChild().getNodeValue();
        } else{
          if(child.hasChildNodes()){
            paramValue = child.getFirstChild().getNodeValue();
          } else{
            paramValue = "";
          }
        }
      }
      result.put(paramName, paramValue);
    }
    return result;
  }
}
