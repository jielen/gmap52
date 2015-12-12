package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.taglib.ResourceManager;

public class PopupMenu extends AbstractMenu {

  private static final Logger logger = Logger.getLogger(PopupMenu.class);

  private Toolbar toolbar = null;

  public PopupMenu(Toolbar toolbar) {
    this.setIdsuffix("_PopupMenu");
    this.setIsvisible(false);
    this.setId("PopupMenuId_" + Pub.getUID());
    this.toolbar = toolbar;
  }

  public void write(Writer out) {
    try {
      out.write(make());
    } catch (IOException e) {
      logger.debug(e);
      throw new RuntimeException(e);
    }
  }

  public String make() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(makeOuterPanel_1());
    voBuf.append(makeFocusBtn());
    voBuf.append(makeCalls(toolbar.getOwnerPage().getCurrRequest()));
    voBuf.append(makeOuterPanel_2());
    return voBuf.toString();
  }

  protected String makeOuterPanel_1() {
    String vsText = "<div id='" + this.getId() + "' " + super.makeAttr()
        + " class='clsPopupMenuContainer5' hidefocus='true' "
        + super.getOuterPanelStyle() + ">\n";
    return vsText;
  }

  String makeFocusBtn() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<input type='button' ");
    voBuf.append("id='" + Page.DOMID_FOCUS_BUTTON + "' ");
    voBuf.append("tabindex='0' ");
    voBuf.append("onblur='PopupMenu_FocusButton_onblur(\"" + this.getPopupMenuId()
      + "\")' ");
    voBuf.append("style='position:absolute;left:-1000px;top:-1000px;'>\n");
    return voBuf.toString();
  }

  protected String makeCallsHtml(NodeList nodes, Map callMap
    , String userId, String coCode, String orgCode, String posiCode) {
    StringBuffer result = new StringBuffer();
    result
      .append("<table id=\"CallsTable\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:solid 1px #DCDCDD;font-size:9pt;width:80px;\">\n");

    Set funcSet = RightUtil.getAllowedFuncs(userId, getComponame(), coCode, orgCode, posiCode);
    
    for (int i = 0, j = nodes.getLength(); i < j; i++) {
      Element node = (Element) nodes.item(i);
      String id = node.getAttribute("id");
      String type = node.getAttribute("type");
      String caption = node.getAttribute("caption");
      String tip = node.getAttribute("tip");
      String accesskey = node.getAttribute("accesskey");
      String img = node.getAttribute("img");
      boolean isgranttoall = Boolean.valueOf(node.getAttribute("isgranttoall"))
        .booleanValue();
      if (img == null || img.equals("")) {
        img = (String) ResourceManager.getToolbarImgMap().get(id);
      }
      if (img == null || img.equals("")) {
        img = AbstractMenu.IMG_ICO_DEFAULT;
      }
      LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
      if (id.equals("fexport")) {
        if (!licenseManager.canExport()) {
          continue;
        }
      }
      if (isAllowCall(funcSet, id, isgranttoall, callMap, userId, coCode, orgCode, posiCode)) {
        result.append("<tr style=\"font-size:1px;\">\n");
        result.append("<td style=\"font-size:1px;\">\n");
        result.append(this.makeCommandCall(id, type, caption, tip, accesskey, img));
        result.append("</td>\n");
        result.append("</tr>\n");
      }
    }
    result.append("</table>\n");
    return result.toString();
  }

  protected String getToolbarId(){
    return this.toolbar.getId();
  }

  public String makeJS() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("var voPopupMenu = new PopupMenu('" + this.getId() + "');\n");

    // 向 PageX 中注册 Box 对象;
    voBuf.append("PageX.regCtrlObj(\"" + this.getId() + "\", voPopupMenu);\n");
    return voBuf.toString();
  }

  protected String getPopupMenuId() {
    return this.getId();
  }
  
}
