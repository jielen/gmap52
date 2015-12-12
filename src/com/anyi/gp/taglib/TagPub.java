package com.anyi.gp.taglib;

import java.util.Iterator;
import java.util.List;

public class TagPub {
  
  public static String makeIdListSpan(String spanId, List idList, String fieldName){
    if (idList== null) return "";
    StringBuffer buf= new StringBuffer();
    buf.append("<span id=\"");
    buf.append(spanId);
    buf.append("\" style=\"display:none;\">\n");
    for (Iterator iter= idList.iterator(); iter.hasNext();){
      buf.append("<span ");
      buf.append(fieldName);
      buf.append("=\"");
      buf.append(iter.next());
      buf.append("\">");
      buf.append("</span>\n");
    }
    buf.append("</span>\n");
    return buf.toString();
  }
  
}
