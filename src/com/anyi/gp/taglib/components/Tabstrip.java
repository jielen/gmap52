package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.anyi.gp.taglib.TabTag;

public class Tabstrip implements Component {

  private final static Logger log = Logger.getLogger(Tabstrip.class);

  public static final String ORIENTATION_UP = "UP";

  public static final String ORIENTATION_DOWN = "DOWN";

  private static final String IMG_NORM_LEFT_U = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_norm_left_u.gif";

  private static final String IMG_NORM_MID_U = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_norm_mid_u.gif";

  private static final String IMG_NORM_RIGHT_U = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_norm_right_u.gif";

  private static final String IMG_NORM_LEFT_D = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_norm_left_d.gif";

  private static final String IMG_NORM_MID_D = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_norm_mid_d.gif";

  private static final String IMG_NORM_RIGHT_D = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_norm_right_d.gif";

  private static final String IMG_SEL_BAR_LEFT_U = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_sel_bar_left_u.gif";

  private static final String IMG_SEL_BAR_MID_U = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_sel_bar_mid_u.gif";

  private static final String IMG_SEL_BAR_RIGHT_U = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_sel_bar_right_u.gif";

  private static final String IMG_SEL_BAR_LEFT_D = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_sel_bar_left_d.gif";

  private static final String IMG_SEL_BAR_MID_D = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_sel_bar_mid_d.gif";

  private static final String IMG_SEL_BAR_RIGHT_D = Page.LOCAL_RESOURCE_PATH + "style/img/gp5/tab/tab_sel_bar_right_d.gif";

  private String id = "";

  private String idsuffix = "_TAB";

  private String orientation = Tabstrip.ORIENTATION_UP;

  private boolean isvisible = true;

  private int tabindex = 0;

  private String cssclass = "";

  private String style = "";

  private String oninit = "";

  //以上进入标记属性.

  private String outerpanelclass = "clsTabstripOuterPanel4";

  private String innerpanelclass = "clsTabstripInnerPanel4";

  private List tabList = new ArrayList();

  private Page ownerPage = null;
  
  private Writer out = null;

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void writeHTML(Writer out) throws IOException {
    this.out = out;
    if (this.getId() == null || this.getId().length() == 0) {
      this.setId("Tabstrip_" + this.getIdsuffix() + this.idsuffix);
    }
    make();
  }

  public void make() {
    try {
      out.write(this.makeOuterPanel_1());
      if (this.getOrientation().equalsIgnoreCase(Tabstrip.ORIENTATION_DOWN)) {
        this.makeContents(out);
        out.write(this.makeDownLabels());
      } else {
        out.write(this.makeUpLabels());
        this.makeContents(out);
      }
      out.write(this.makeOuterPanel_2());
    } catch (IOException e) {
      log.error(e);
      throw new RuntimeException(e);
    }
  }

  private void makeContents(Writer out) throws IOException {
    out.write("<div id=\"Tabstrip_InnerDiv" + getId() + "\" ");
    out.write("class=\"");
    out.write(this.getInnerpanelclass());
    out.write(" ");
    out.write(this.getCssclass());
    out.write("\" ");
    out.write("style=\"");
    out.write(this.getStyle());
    out.write(this.getAdjustInnerPanelStyle());
    out.write(";\" ");
    out.write(">\n");
    TabTag tab = null;
    for (int i = 0, len = this.tabList.size(); i < len; i++) {
      tab = (TabTag) this.tabList.get(i);
      out.write("<div id=\"Tabstrip_UserPanel_");
      out.write(getId());
      out.write(tab.getId());
      out.write("\" ");
      out.write("style=\"");
      out.write(tab.getStyle());
      out.write(this.getUserPanelStyle());
      out.write("\" ");
      out.write(">\n");
      out.write(tab.getBodytext() + "\n");
      out.write("</div>\n");
    }
    out.write("</div>\n");
  }

  private String getUserPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("position:absolute;");
    voBuf.append("overflow:auto;");
    voBuf.append("scrollbar-3dlight-color: gray;");
    voBuf.append("scrollbar-highlight-color: white;");
    voBuf.append("scrollbar-shadow-color: gray;");
    voBuf.append("scrollbar-darkshadow-color: white;");
    voBuf.append("scrollbar-base-color: #E1E1E1;");
    voBuf.append("left:-10000px;");
    voBuf.append("top:-10000px;");
    voBuf.append("width:100%;");
    voBuf.append("height:100%;");
    return voBuf.toString();
  }

  private String makeUpLabels() {
    StringBuffer buf = new StringBuffer();
    buf.append(makeLabels_Begin());
    buf.append(makeLabels_HeadTR(IMG_SEL_BAR_LEFT_U, IMG_SEL_BAR_MID_U,
      IMG_SEL_BAR_RIGHT_U, 1));
    buf.append(makeLabels_BodyTR(IMG_NORM_LEFT_U, IMG_NORM_MID_U, IMG_NORM_RIGHT_U,
      3));
    buf.append(makeLabels_End());
    buf.append(makeTopBorder());
    return buf.toString();
  }

  private String makeDownLabels() {
    StringBuffer buf = new StringBuffer();
    buf.append(makeTopBorder());
    buf.append(makeLabels_Begin());
    buf.append(makeLabels_BodyTR(IMG_NORM_LEFT_D, IMG_NORM_MID_D, IMG_NORM_RIGHT_D,
      3));
    buf.append(makeLabels_HeadTR(IMG_SEL_BAR_LEFT_D, IMG_SEL_BAR_MID_D,
      IMG_SEL_BAR_RIGHT_D, 1));
    buf.append(makeLabels_End());
    return buf.toString();
  }

  private String makeLabels_Begin() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id=\"Tabstrip_LabelsDiv" + getId()
      + "\" style='cursor:hand;text-align:left;'>\n");
    voBuf
      .append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size:9pt;color:black;text-align:left;\">\n");
    voBuf.append("<tbody>\n");
    return voBuf.toString();
  }

  private String makeLabels_End() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("</tbody>\n");
    voBuf.append("</table>\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private String makeLabels_BodyTR(String sLeftImg, String sMidImg, String sRightImg,
    int iTopPadding) {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<tr id=\"Tabstrip_LabelsBodyTR\" style=\"height:18px;font-size:9pt;\">\n");
    TabTag tab = null;
    for (int i = 0, len = this.tabList.size(); i < len; i++) {
      tab = (TabTag) this.tabList.get(i);
      voBuf.append("<td id='Tabstrip_Label_Body_" + getId() + tab.getId() + "'>\n");
      voBuf.append(makeLabel(tab, sLeftImg, sMidImg, sRightImg, iTopPadding));
      voBuf.append("</td>\n");
    }
    voBuf.append("</tr>\n");
    return voBuf.toString();
  }

  private String makeLabels_HeadTR(String sLeftImg, String sMidImg, String sRightImg,
    int iTopPadding) {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<tr id=\"Tabstrip_LabelsHeadTR\" style=\"height:3px;font-size:1px;\">\n");
    TabTag tab = null;
    for (int i = 0, len = this.tabList.size(); i < len; i++) {
      tab = (TabTag) this.tabList.get(i);
      voBuf.append("<td id='Tabstrip_Label_Head_" + getId() + tab.getId()
        + "' style=\"visibility:hidden;\">\n");
      String caption = tab.getCaption();
      tab.setCaption("");
      voBuf.append(makeLabel(tab, sLeftImg, sMidImg, sRightImg, iTopPadding));
      tab.setCaption(caption);
      voBuf.append("</td>\n");
    }
    voBuf.append("</tr>\n");
    return voBuf.toString();
  }

  private String makeLabel(TabTag tab, String sLeftImg, String sMidImg, String sRightImg,
    int iTopPadding) {
    StringBuffer voBuf = new StringBuffer();
    voBuf
      .append("<table border='0' cellspacing='0' cellpadding='0' style=\"height:100%\">\n");
    voBuf.append("<tbody>\n");
    voBuf.append("<tr>\n");
    voBuf.append("<td>");
    voBuf
      .append("<div id=\"Tabstrip_Tab_Label_Left_Div\" style=\"border:solid 0 gray;width:8px;height:100%;overflow:hidden;\">");
    voBuf.append("<img id=\"Tabstrip_Tab_Label_Left_Img\" src='" + sLeftImg
      + "' width=\"8px\">");
    voBuf.append("</div>");
    voBuf.append("</td>\n");
    voBuf.append("<td id=\"Tabstrip_Tab_Label_Mid_TD\" ");
    if (sMidImg != null && !sMidImg.trim().equals("")) {
      voBuf.append("background='" + sMidImg + "' ");
    }
    voBuf
      .append("valign=center align=center nowrap style='font:9pt;width:100%;padding-top:"
        + iTopPadding + ";'>");
    voBuf.append(tab.getCaption());
    voBuf.append("</td>\n");
    voBuf.append("<td>");
    voBuf
      .append("<div id=\"Tabstrip_Tab_Label_Right_Div\" style=\"border:solid 0 gray;width:8px;height:100%;overflow:hidden;\">");
    voBuf.append("<img id=\"Tabstrip_Tab_Label_Right_Img\" src='" + sRightImg
      + "' width=\"8px\">");
    voBuf.append("</div>");
    voBuf.append("</td>\n");
    voBuf.append("</tr>\n");
    voBuf.append("</tbody>\n");
    voBuf.append("</table>\n");
    return voBuf.toString();
  }

  private String makeTopBorder() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id=\"Tabstrip_TopBorderDiv" + getId() + "\" ");
    voBuf.append("style=\"width:100%;height:1px;overflow:hidden;\">\n");
    voBuf
      .append("<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size:1px;height:1px;\">\n");
    voBuf.append("<tr>\n");
    voBuf
      .append("<td id=\"Tabstrip_Top_Border_Left_Line_TD\" style=\"width:50%;background-color:gray;\">&nbsp;</td>\n");
    voBuf
      .append("<td id=\"Tabstrip_Top_Border_Mid_Line_TD\" style=\"width:0px;\"></td>\n");
    voBuf
      .append("<td id=\"Tabstrip_Top_Border_Right_Line_TD\" style=\"width:50%;background-color:gray;\">&nbsp;</td>\n");
    voBuf.append("</tr>\n");
    voBuf.append("</table>\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private String makeOuterPanel_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='");
    voBuf.append(this.getId());
    voBuf.append("' ");
    voBuf.append(this.makeAttr());
    voBuf.append(" class=\"");
    voBuf.append(this.getOuterpanelclass());
    voBuf.append(" ");
    voBuf.append(this.getCssclass());
    voBuf.append("\" ");
    voBuf.append(" style='");
    voBuf.append(this.getOuterPanelStyle());
    voBuf.append(this.getStyle());
    voBuf.append(this.getAdjustOuterPanelStyle());
    voBuf.append(";' ");
    voBuf.append("hidefocus='true'");
    voBuf.append(">\n");
    return voBuf.toString();
  }

  private String makeOuterPanel_2() {
    return "</div>\n";
  }

  private String makeAttr() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("orientation='" + this.orientation + "' ");
    voBuf.append("isvisible='" + this.isvisible + "' ");
    voBuf.append("tabindex=" + this.tabindex + " ");
    return voBuf.toString();
  }

  public boolean addTab(TabTag tab) {
    if (tab == null)
      return false;
    TabTag newTab = new TabTag();
    newTab.setId(tab.getId());
    newTab.setCaption(tab.getCaption());
    newTab.setBodytext(tab.getBodytext());
    newTab.setIsselected(tab.isIsselected());
    newTab.setCssclass(tab.getCssclass());
    newTab.setStyle(tab.getStyle());
    this.tabList.add(newTab);
    return true;
  }

  private String getOuterPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("display:" + (this.isIsvisible() ? "" : "none") + ";");
    voBuf.append("z-index:" + this.getTabindex() + ";");
    return voBuf.toString();
  }

  private String getAdjustOuterPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("overflow:hidden;");
    voBuf.append("padding:0px;");
    voBuf.append("margin:0px;");
    voBuf.append("border-width:0px;");
    return voBuf.toString();
  }

  private String getAdjustInnerPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("position:relative;");
    voBuf.append("overflow:hidden;");
    voBuf.append("border-style:solid;");
    voBuf.append("display:;");
    voBuf.append("visibility:visible;");
    voBuf.append("margin:0px;");
    voBuf.append("left:0;");
    voBuf.append("top:;");
    voBuf.append("width:100%;");
    voBuf.append("height:10%;");
    if (ORIENTATION_DOWN.equals(getOrientation())) {
      voBuf.append("border-width:1 1 0 1;");
    } else {
      voBuf.append("border-width:0 1 1 1;");
    }
    return voBuf.toString();
  }

  public void writeInitScript(Writer out) throws IOException {
    out.write("var voTabstrip = new Tabstrip();\n");
    out.write("voTabstrip.make('" + this.getId() + "');\n");
    out.write("voTabstrip.init();\n");
    out.write("if (voTabstrip.tHasResize== false){\n");
    out.write("voTabstrip.resize();\n");
    out.write("}\n");
    String vsSelId = "";
    for (int i = 0; i < this.tabList.size(); i++) {
      TabTag voTab = (TabTag) this.tabList.get(i);
      if (i == 0)
        vsSelId = voTab.getId();
      if (voTab.isIsselected()) {
        vsSelId = voTab.getId();
        break;
      }
    }
    out.write("voTabstrip.selTab('" + vsSelId + "');\n");
    out.write("PageX.regCtrlObj(\"" + this.getId() + "\", voTabstrip);\n");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIdsuffix() {
    return idsuffix;
  }

  public void setIdsuffix(String idsuffix) {
    this.idsuffix = idsuffix;
  }

  public String getOrientation() {
    return orientation;
  }

  public void setOrientation(String orientation) {
    this.orientation = orientation;
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

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getOninit() {
    return oninit;
  }

  public void setOninit(String oninit) {
    this.oninit = oninit;
  }

  public String getOuterpanelclass() {
    return outerpanelclass;
  }

  public String getInnerpanelclass() {
    return innerpanelclass;
  }

  public String getCssclass() {
    return cssclass;
  }

  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
  }

}
