package com.anyi.gp;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;

import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;

public class Pub {
  private static int increment = 0;

  private static long uidtime = 0;

  private Pub() {
  }

  // 获取 UID;
  synchronized public static String getUID() {
    long vlTime = new Date().getTime();
    if (vlTime == Pub.uidtime) {
      Pub.increment++;
    } else {
      Pub.uidtime = vlTime;
      Pub.increment = 0;
    }
    String vsUID = String.valueOf(vlTime) + String.valueOf(Pub.increment);
    return vsUID;
  }

  public static int parseInt(Object value) {
    if (value == null)
      return 0;
    if (value.toString().length() == 0)
      return 0;
    return Integer.parseInt(value.toString());
  }

  public static double parseDouble(Object value) {
    if (value == null)
      return 0;
    if (value.toString().length() == 0)
      return 0;
    return Double.parseDouble(value.toString());
  }

  public static boolean parseBool(Object value) {
    if (value == null)
      return false;
    
    String val = value.toString();
    if (val.length() == 0)
      return false;
    if (val.equalsIgnoreCase("N") || val.equals("0"))
      return false;
    if (val.equalsIgnoreCase("Y") || val.equals("1"))
    	return true;

    boolean result = Boolean.valueOf(val).booleanValue();
    return result;
  }
  
  /**
   * 返回对象 obj ，null 时返回 defaultValue
   * @param obj 对象
   * @param defaultValue 缺省值
   * @return (null == obj) ? defaultValue : obj
   */
  public static Object isNull(Object obj, Object defaultValue) {
    return (null == obj) ? defaultValue : obj;
  }

  public static String getYear(GregorianCalendar currentDate) {
    return String.valueOf(currentDate.get(Calendar.YEAR));
  }

  public static String getMonth(GregorianCalendar currentDate) {
    String imonth = String.valueOf(currentDate.get(Calendar.MONTH) + 1);
    if (imonth.length() == 1) {
      imonth = "0" + imonth;
    }
    return imonth;
  }

  public static String getDay(GregorianCalendar currentDate) {
    String iday = String.valueOf(currentDate.get(Calendar.DATE));
    if (iday.length() == 1) {
      iday = "0" + iday;
    }
    return iday;
  }
 
  /**
   * 按指定的顺序元素对List中的内容顺序进行重组;
   *
   * @param list
   * @param orderItems
   *            顺序元素组;
   * @param itemSeparator
   */
  public static void recomposeList(List list, String orderItems, String itemSeparator) {
    List newList = StringTools.split(orderItems, itemSeparator);
    if (newList == null)
      return;
    for (int i = 0; i < newList.size(); i++) {
      Object item = newList.get(i);
      if (list.contains(item))
        continue;
      newList.remove(item);
      i--;
    }
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      Object item = iter.next();
      if (newList.contains(item))
        continue;
      newList.add(item);
    }
    while (list.size() > 0) {
      list.remove(0);
    }
    for (Iterator iter = newList.iterator(); iter.hasNext();) {
      list.add(iter.next());
    }
  }

  public static String getWebRoot(HttpServletRequest request) {
    if (request == null)
      return null;
    String vsPath = request.getContextPath();
    if (vsPath == null)
      return null;
    if (vsPath.length() == 1)
      return "";
    vsPath = vsPath.substring(1);
    return vsPath;
  }
  
  /**
   * 判断applicationPath对应的文件或者目录是否为web应用
   * @param applicationPath
   * @return
   */
  public static boolean isApplication(String applicationPath){
    boolean result = false;
    
    if(applicationPath == null || applicationPath.length() == 0)
      return result;
    
    File file = new File(applicationPath);
    if(file == null || !file.exists())
      return result;
    
    if(file.isFile() && file.getName().endsWith(".war"))
      result = true;
    
    if(file.isDirectory()){
      String path = file.getPath() + "/WEB-INF";
      if(new File(path).exists())
        result = true;
    }
    
    return result;
  }
  
  public static String makeBusinessExceptionString(BusinessException e) {
    StringBuffer result = new StringBuffer(
        "<response id=\"result\" success=\"false\">\n");
    result.append("<![CDATA[");
    result.append("<info success=\"false\">\n");
    result.append("<result>\n");
    result.append("</result>\n");
    result.append("<message>\n");
    result.append(e.toString());
    result.append("</message>\n");
    result.append("<digest>\n");
    result.append("</digest>\n");
    result.append("</info>\n");
    result.append("]]>\n");
    result.append("</response>");
    return result.toString();
  }

  public static String makeRuntimeExceptionString(RuntimeException e) {
    StringBuffer result = new StringBuffer(
        "<response id=\"result\" success=\"false\">\n");
    result.append("<![CDATA[");
    result.append("<info success=\"false\">\n");
    result.append("<result>\n");
    result.append("</result>\n");
    result.append("<message>\n");
    result.append("系统出现异常，请与系统管理员联系.");
    result.append("</message>\n");
    result.append("<digest>\n");
    result.append("</digest>\n");
    result.append("</info>\n");
    result.append("]]>\n");
    result.append("</response>");
    return result.toString();
  }

  /**
   * 业务处理后的返回值
   * @param tSuccess
   * @param sDigest
   * @param sText
   * @return
   */
  public static String makeRetString(boolean tSuccess, String sDigest,
    String sText) {
    StringBuffer voSB = new StringBuffer();
    voSB.append("<response id=\"result\" success=\"" + tSuccess + "\">\n");
    voSB.append("<info digest=\"" + sDigest + "\">");
    voSB.append(sText);
    voSB.append("</info>\n");
    voSB.append("</response>\n");
    return voSB.toString();
  }
  
  public static String makeRetString(boolean tSuccess, String sMsg) {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<info success=\"");
    voBuf.append(String.valueOf(tSuccess));
    voBuf.append("\" >\n");
    voBuf.append(sMsg);
    voBuf.append("</info>\n");
    return voBuf.toString();
  }
  

  /**
   * 保存后的返回值;
   *
   * @param success
   * @param resultMsg
   * @param busiMsg
   * @param digest
   * @return
   */
  public static String makeRetInfo2(boolean success, String resultMsg,
    String busiMsg, String digest) {
    StringBuffer s = new StringBuffer();
    s.append("<info success=\"" + success + "\">\n");
    s.append("<result>\n");
    s.append(resultMsg);
    s.append("\n");
    s.append("</result>\n");
    s.append("<message>\n");
    s.append(XMLTools.getValidStringForXML(busiMsg));
    s.append("\n");
    s.append("</message>\n");
    s.append("<digest>\n");
    s.append(digest);
    s.append("\n");
    s.append("</digest>\n");
    s.append("</info\n>");
    return s.toString();
  }
  
  /**
   * 计算页码
   * @param currentPage
   * @param direction
   * @param pageSize 页面大小，用于计算最后一页的页码
   * @param totalCount 记录总数，用于计算最后一页的页码
   * @return 如果 direction 不是有效值，返回 0
   */
  public static int calcPageIndex(int currentPage, String direction,
    int pageSize, int totalCount) {
    if (pageSize <= 0) {
      return 1;
    } 
    else if (DIRECTION_FIRST.equals(direction)) {
      return 1;
    } 
    else if (DIRECTION_TO.equals(direction)) {
    	if(currentPage * pageSize > totalCount){
    		return (totalCount + pageSize - 1) / pageSize;
    	}
      return currentPage;
    } 
    else if (DIRECTION_NEXT.equals(direction)) {
      return currentPage + 1;
    } 
    else if (DIRECTION_PREV.equals(direction)) {
      return (currentPage > 1) ? currentPage - 1 : 1;
    } 
    else if (DIRECTION_LAST.equals(direction)) {
      if (pageSize <= 0) {
        return 1;
      }
      return (totalCount + pageSize - 1) / pageSize;
    }
    return 0;
  }
  
  public static String encodeUrl(String path){
    return encodeUrl(path, KEY_APPLUS_STATE);
  }
  
  public static String encodeUrl(String path, String paramName){
    int pos = path.indexOf(paramName);
    if(pos > 0){
      return path;
    }
    
    pos = path.indexOf("?");
    if(pos > 0){
      String tmp = path.substring(pos + 1);
//      if(tmp.indexOf("userId") < 0){// no userId parameter
//        HttpServletRequest request = ServletActionContext.getRequest();
//        if(request != null){
//          tmp += "&userId=" + SessionUtils.getAttribute(request, "svUserID");
//        }
//      }
      return path + "&" + paramName + "=" + encodeHex(tmp);
    }
    
    return path;
  }
  
  public static String encodeHex(String source){
    return new String(Hex.encodeHex(source.getBytes()));
  }
  
  public static final String KEY_APPLUS_STATE = "_APPLUS_STATE";
  
  public final static String DIRECTION_NEXT = "next";
  
  public final static String DIRECTION_PREV = "prev";

  public final static String DIRECTION_FIRST = "first";

  public final static String DIRECTION_LAST = "last";

  public final static String DIRECTION_TO = "to";

}