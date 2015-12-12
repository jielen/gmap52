package com.anyi.gp.taglib.components;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.XMLTools;

public abstract class AbstractMenu {

  protected final static String TAG_INTERFACE_FILTER_FUNC_MAP = "toolbartag_interface_filter_func_map";

  protected static final String TYPE_COMMAND = "command";

  protected static final String TYPE_CHECK = "check";

  protected static final String TYPE_SEPARATOR = "separator";

  protected static final String TYPE_GROUP = "group";

  protected final static String IMG_ICO_DEFAULT = Page.LOCAL_RESOURCE_PATH
    + "style/img/gp5/ico/leafdark_16x16.gif";

  protected final static String IMG_TOOLBAR_LEFT = Page.LOCAL_RESOURCE_PATH
    + "style/img/gp5/toolbar/toolbar_left.jpg";

  protected final static String IMG_TOOLBAR_MIDDLE = Page.LOCAL_RESOURCE_PATH
    + "style/img/gp5/toolbar/toolbar_middle.jpg";

  protected final static String IMG_TOOLBAR_RIGHT = Page.LOCAL_RESOURCE_PATH
    + "style/img/gp5/toolbar/toolbar_right.jpg";

  private int paddingLR = 2;

  private int paddingImgCap = 3;

  private String id = "";

  private String componame = "";

  private boolean isfromdb = true;

  private String bodytext = "";

  private String idsuffix = "_Toolbar";

  private boolean isvisible = true;

  private int tabindex = 0;

  private Map filterFuncMap = null;

  protected String makeOuterPanel_1() {
    String vsText = "<div id='" + this.getId() + "' " + this.makeAttr()
      + " class='clsToolbarContainer4' hidefocus='true' "
      + this.getOuterPanelStyle() + ">\n";
    return vsText;
  }

  protected String makeOuterPanel_2() {
    return "</div>\n";
  }

  protected String getOuterPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(" style='");
    voBuf.append("display:" + (this.isIsvisible() ? "" : "none") + "; ");
    voBuf.append("' ");
    return voBuf.toString();
  }

  protected String makeAttr() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("componame=\"" + this.getComponame() + "\" ");
    voBuf.append("isfromdb=" + this.isIsfromdb() + " ");
    voBuf.append("tabindex=" + this.tabindex + " ");
    voBuf.append("popupmenuid=\"" + this.getPopupMenuId() + "\" ");
    return voBuf.toString();
  }

  protected abstract String getPopupMenuId();

  protected boolean isAllowCall(Set funcSet, String callId, boolean isGrantToAll,
    Map callMap, String userId, String coCode, String orgCode, String posiCode) {
    if (isGrantToAll) {
      return true;
    }
    Map funcFilterMap = getFilterFuncMap();

    if (funcFilterMap != null && funcFilterMap.size() > 0) {
      Collection values = funcFilterMap.values();
      funcSet.retainAll(values);
    }

    if (!isIsfromdb()
      || ((callMap != null && callMap.containsKey(callId)) && (userId == null
        || getComponame() == null || getComponame().trim().length() == 0 || RightUtil
        .isAllowed(funcSet, userId, callId, getComponame(), coCode, orgCode, posiCode)))) {
      return true;
    }

    return false;
  }

  protected String makeCommandCall(String sId, String sType, String sCaption,
    String sTip, String sAccessKey, String img) {
    StringBuffer buf = new StringBuffer();
    buf.append("<div callId=\"");
    buf.append(sId);
    buf.append("\" type=\"");
    buf.append(sType);
    buf.append("\" title=\"");
    sAccessKey = sAccessKey.trim();
    if ((sTip != null && !sTip.equals(""))
      || (sAccessKey != null && !sAccessKey.equals(""))) {
      if (sTip == null || sTip.equals(""))
        sTip = "";
      else if (sAccessKey != null && !sAccessKey.equals("")) {
        sTip += " ";
      }
      if (sAccessKey != null && !sAccessKey.equals("")) {
        sTip += "ALT+" + sAccessKey;
      }
    }
    buf.append(sTip);
    buf.append("\" accessKey=\"");
    buf.append(sAccessKey);
    buf.append("\" style=\"font-size:9pt;width:100%;padding:1px;\" ");
    addCallEvent(buf);
    buf.append(">\n");
    buf.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"");
    buf.append(" style=\"font-size:9pt;\">\n");
    buf.append("<tr height=\"18px\" valign=\"bottom\">\n");
    buf.append("<td id=\"LeftSpaceTD\" style=\"font-size:").append(paddingLR);
    buf.append("px;\">&nbsp;</td>\n");
    buf.append("<td id=\"ImageTD\" width=\"16px\">");
    buf.append("<img border=\"0\" width=\"16px\" height=\"16px\" src=\"");
    buf.append(img).append("\"></img></td>\n");
    buf.append("<td style=\"font-size:").append(paddingImgCap);
    buf.append("px;\">&nbsp;</td>\n");
    buf.append("<td id=\"CaptionTD\" nowrap>");
    buf.append(sCaption).append("</td>\n");
    buf.append("<td id=\"RightSpaceTD\" style=\"font-size:");
    buf.append(paddingLR).append("px;\">&nbsp;</td>\n");
    buf.append("</tr>\n");
    buf.append("</table>\n");
    buf.append("</div>\n");
    return buf.toString();
  }

  private void addCallEvent(StringBuffer buf) {
    buf.append("onkeydown='this.fireEvent(\"onmousedown\");' ");
    buf.append("onkeyup='this.fireEvent(\"onmouseup\");' ");
    buf.append("onmouseover='Toolbar_callOnMouseOver(this,\"" + this.getToolbarId()
      + "\");' ");
    buf.append("onmouseout='Toolbar_callOnMouseOut(this,\"" + this.getToolbarId()
      + "\");' ");
    buf.append("onmouseup='Toolbar_callOnMouseUp(this,\"" + this.getToolbarId()
      + "\");' ");
  }

  protected abstract String getToolbarId();

  protected String makeCalls(HttpServletRequest request) {
    if (request != null) {
      if (this.getComponame() == null || this.getComponame().trim().length() == 0) {
        this.setComponame(request.getParameter("componame"));
        if (this.getComponame() == null || this.getComponame().trim().length() == 0)
          this.setComponame((String) request.getAttribute("componame"));
      }
    }

    Map voCallMap = null;
    if (this.isIsfromdb()) {
      voCallMap = DataTools.getCalls(this.getComponame());
    }

    StringBuffer vsb = new StringBuffer();
    String vsBodyContent = this.getBodytext();
    vsb.append("<calls>");
    vsb.append(vsBodyContent);
    vsb.append("</calls>");
    Document doc = XMLTools.stringToDocument(vsb.toString());
    NodeList nodes = doc.getElementsByTagName("call");
    return makeCallsHtml(nodes, voCallMap, SessionUtils.getAttribute(request,
      "svUserID"), SessionUtils.getAttribute(request, "svCoCode"), SessionUtils
      .getAttribute(request, "svOrgCode"), SessionUtils
      .getAttribute(request, "svPoCode"));
  }

  protected abstract String makeCallsHtml(NodeList nodes, Map callMap,
    String userId, String coCode, String orgCode, String posiCode);

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getComponame() {
    return componame;
  }

  public void setComponame(String componame) {
    this.componame = componame;
  }

  public boolean isIsfromdb() {
    return isfromdb;
  }

  public void setIsfromdb(boolean isfromdb) {
    this.isfromdb = isfromdb;
  }

  public String getBodytext() {
    return bodytext;
  }

  public void setBodytext(String bodytext) {
    this.bodytext = bodytext;
  }

  public String getIdsuffix() {
    return idsuffix;
  }

  public void setIdsuffix(String idsuffix) {
    this.idsuffix = idsuffix;
  }

  public boolean isIsvisible() {
    return isvisible;
  }

  public void setIsvisible(boolean isvisible) {
    this.isvisible = isvisible;
  }

  public int getTabindex() {
    return tabindex;
  }

  public void setTabindex(int tabindex) {
    this.tabindex = tabindex;
  }

  public Map getFilterFuncMap() {
    return filterFuncMap;
  }

  public void setFilterFuncMap(Map filterFuncMap) {
    this.filterFuncMap = filterFuncMap;
  }
  
}
