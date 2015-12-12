package com.anyi.gp.taglib.components;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Pub;
import com.anyi.gp.util.StringTools;

public class IncludeUtil {

  private HttpServletRequest request = null;

  public IncludeUtil(HttpServletRequest request){
    this.request = request;
  }
  
  public String make(String bcStr) {
    StringBuffer vsb = new StringBuffer();
    List fileList = StringTools.split(bcStr, ";");
    for (int i = 0; i < fileList.size(); i++) {
      String fileName = (String) fileList.get(i);
      if (fileName == null)
        continue;
      fileName = fileName.trim();
      if (fileName.equals(""))
        continue;
      if ("gp.page.Toolbar".equals(fileName)
        && !fileList.contains("gp.page.PopupMenu")) {
        fileList.add("gp.page.PopupMenu");
      }
      vsb.append(getJSScript(fileName));
    }
    return vsb.toString();
  }

  public String getJSScript(String jsFullClassName) {
    return "<script language=\"javascript\" src=\""
      + getJSFileUrl(jsFullClassName) + "\"></script>\n";
  }

  private String getJSFileUrl(String jsFullClassName) {
    String afterFileName = StringTools.replaceAll(jsFullClassName, ".", "/");
    return Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(request) + "/" + afterFileName + ".js";
  }

}
