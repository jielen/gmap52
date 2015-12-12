package com.anyi.gp.core.action;

import java.util.Iterator;
import java.util.List;

import com.anyi.gp.pub.LangResource;
import com.anyi.gp.util.StringTools;

public class GetLanTransAction extends AjaxAction {

  private static final long serialVersionUID = 913808683193025168L;

  private String fieldStr;

  public void setFieldStr(String fieldStr) {
    this.fieldStr = fieldStr;
  }

  public String doExecute() {
    StringBuffer result = new StringBuffer();
    result.append("<ASLANTRANS>\n");
    result.append("<meta pageindex=\"0\" fromrow=\"0\" torow=\"0\" rowcountofpage=\"0\" rowcountofdb=\"0\">\n</meta>");
    result.append("<rowset>\n");
    result.append("<row>\n");
    
    String fieldName;    
    LangResource resource = LangResource.getInstance();
    List fields = StringTools.split(fieldStr, ",");
    
    for (Iterator iter = fields.iterator(); iter.hasNext();) {
      fieldName = (String) iter.next();
      result.append("<" + fieldName + ">");
      result.append(resource.getLang(fieldName));
      result.append("</" + fieldName + ">\n");
    }
    
    result.append("</row>\n");
    result.append("</rowset>\n");
    result.append("</ASLANTRANS>\n");
    
    this.resultstring = result.toString();
    
    return SUCCESS;
  }

}
