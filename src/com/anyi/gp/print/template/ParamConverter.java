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
   * 将页面传来的页面元素分解成Java对象数组
   *
   * @param l
   *          List 对象列表
   * @return TextElement[] 对象数组
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
   * 将页面传来的页面元素分解成Java对象数组
   *
   * @param l
   *          List 对象列表
   * @return TextElement[] 对象数组
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
   * 将页面传来的页面元素分解成Java对象数组
   *
   * @param l
   *          List 对象列表
   * @return TextElement[] 对象数组
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
   * 将字符串格式的页面元素信息解析成Java对象列表
   *
   * @param s
   *          String 元素字段串
   * @return List 对象列表
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
   * 将字符串格式的页面元素信息解析成Java对象列表
   *
   * @param s
   *          String 元素字段串
   * @return List 对象列表
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
   * 将字符串格式的页面元素信息解析成Java对象列表
   *
   * @param s
   *          String 元素字段串
   * @return Object 对象列表,第一个元素是参数对象列表,第二个对象是参数值
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
   * 字符串格式参数转换成HashMap
   *
   * @param str
   *          String 字符串参数
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
