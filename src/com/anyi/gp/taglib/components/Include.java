package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.anyi.gp.Pub;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.util.StringTools;

public class Include implements Component {

  public final static String TAG_INTERFACE_OUT_JS = "includetag_interface_out_js";

  public final static String INCLUDED_SCRIPT_ID = "included_script_id_20051210";

  private static Map jsGlobalObj = new HashMap();

  private String bodyContent = null;

  private Page ownerPage = null;

  static {
    jsGlobalObj.put("gp.page.Page4", "PageX");
    jsGlobalObj.put("gp.page.PrintX", "PrintX");
    jsGlobalObj.put("gp.pub.PublicFunction", "PF");
    jsGlobalObj.put("gp.pub.DataTools4", "DataTools");
    jsGlobalObj.put("gp.pub.Information", "Info");
    jsGlobalObj.put("gp.pub.Constant", "Const");
    jsGlobalObj.put("gp.pub.Develope", "Dev");
    jsGlobalObj.put("gp.workflow.WFPage4", "PageX");
    jsGlobalObj.put("gp.workflow.WFInterface", "WFInterface");
    jsGlobalObj.put("gp.workflow.WFDataTools4", "DataTools");
    jsGlobalObj.put("gp.workflow.WFConst", "WFConst");
  }

  public Include() {
  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void setBodyContent(String bodyContent) {
    this.bodyContent = bodyContent;
  }

  public void writeHTML(Writer out) throws IOException {
    out.write(generateLinkStyle());
    out.write(makeBaseUrl());
    out.write(getHeadJS());
    out.write(getFormEnc());
    out.write((new IncludeUtil(this.ownerPage.getCurrRequest())).make(getAllJS()));
  }

  private String generateLinkStyle() {
    StringBuffer vsb = new StringBuffer();
    vsb.append("<script language='javascript'>var t0 = (new Date()).getTime();</script>\n");
    vsb.append("<STYLE>v\\:*{behavior:url(#default#VML);}</STYLE>\n");
    vsb.append("<link href=\"" + Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(this.ownerPage.getCurrRequest())
      + "/script/applus.css\" rel=\"stylesheet\" type=\"text/css\" />\n");
    vsb.append("<link href=\"" + Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(this.ownerPage.getCurrRequest())
      + "/gp/css/pagestyle.css\" rel=\"stylesheet\" type=\"text/css\" />\n");
    return vsb.toString();
  }

  private String makeBaseUrl() {
    return "<script>var BASE_URL=\"/" + Pub.getWebRoot(this.ownerPage.getCurrRequest()) + "\";</script>\n";
  }

  private String getHeadJS() {
    StringBuffer buf = new StringBuffer();
    buf.append("<script language=\"javascript\" src=\"\" id=\"" + INCLUDED_SCRIPT_ID);
    buf.append("\"></script>\n");
    buf.append((new IncludeUtil(this.ownerPage.getCurrRequest()))
      .getJSScript("gp.page.Head"));
    return buf.toString();
  }

  private String getFormEnc() {
    return "<script language=\"VBScript\" src=\"" + Page.LOCAL_RESOURCE_PATH
      + Pub.getWebRoot(this.ownerPage.getCurrRequest())
      + "/script/formenctype.vbs\"></script>\n";
  }

  private String getAllJS() {
    StringBuffer buf = new StringBuffer();
    buf.append(getCommonPlatJS());
    buf.append(getCommonObjectJS());
    buf.append(getTagInterfaceOutJS());
    buf.append(";");
    String vbcStr = StringTools.replaceAll(bodyContent, "\r\n", "");
    buf.append(vbcStr);
    return buf.toString();
  }

  private String getTagInterfaceOutJS() {
    String vsJS = (String) this.ownerPage.getCurrRequest().getAttribute(
      TAG_INTERFACE_OUT_JS);
    if (vsJS == null)
      vsJS = "";
    return vsJS;
  }

  private String getCommonObjectJS() {
    StringBuffer vcommonFile = new StringBuffer();
    vcommonFile.append("gp.common.List;");
    vcommonFile.append("gp.common.StringBuffer;");
    vcommonFile.append("gp.common.Map;");
    vcommonFile.append("gp.common.Rect;");
    vcommonFile.append("gp.common.Listener;");
    vcommonFile.append("gp.common.Base;");
    vcommonFile.append("gp.pub.Constant;");
    vcommonFile.append("gp.pub.PublicFunction;");
    vcommonFile.append("gp.pub.Information;");
    vcommonFile.append("gp.pub.DataTools4;");
    vcommonFile.append("gp.pub.Develope;");
    vcommonFile.append("gp.page.FreeManager;");
    vcommonFile.append("gp.page.TableManager;");
    vcommonFile.append("gp.page.RowManager;");
    vcommonFile.append("gp.page.DataManager;");
    vcommonFile.append("gp.page.PrintX;");
    String compoName = this.ownerPage.getCurrRequest().getParameter("componame");
    if (compoName != null && compoName.length() > 0) {
      CompoMeta meta = MetaManager.getCompoMeta(compoName);
      if (meta != null && meta.isCompoSupportWF()) {
        vcommonFile.append("gp.workflow.WFPage4;");
        vcommonFile.append("gp.workflow.WFData;");
        vcommonFile.append("gp.workflow.WFConst;");
        vcommonFile.append("gp.workflow.WFDataTools4;");
      }
    }
    vcommonFile.append("gp.page.Page4;");
    vcommonFile.append("gp.workflow.WFInterface;");

    return vcommonFile.toString();
  }

  private String getCommonPlatJS() {
    StringBuffer vcommonFile = new StringBuffer();
    vcommonFile.append("script.Community;");
    vcommonFile.append("script.General;");
    vcommonFile.append("script.Base64;");
    return vcommonFile.toString();
  }

  public void writeInitScript(Writer out) throws IOException {
  }

  public String getId() {
    return "include_id";
  }

}
