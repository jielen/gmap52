package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;

import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.util.XMLTools;

public class TreeView implements HtmlComponent {
  private static final int PAGE_SIZE = 100;

  private static final int MAKE_TYPE_LEVEL = 1;

  private String img_opened = "";

  private String img_closed = "";

  private String id = "";

  private String idsuffix = "_TREE";

  private String tablename = "";

  private boolean isfromdb = true;

  private int maketype = MAKE_TYPE_LEVEL;

  private int pagesize = PAGE_SIZE;

  private String root = "根结点";

  private String roottip = "";

  private String rootselimg = img_opened;

  private String rootnormalimg = img_closed;

  private String codefield = "";

  private String namefield = "";

  private String pcodefield = "";

  private String checkfield = "__CHECK";

  private String tipfield = "";

  private String selimgfield = "";

  private String normalimgfield = "";

  private String checkedvalue = "Y";

  private String uncheckedvalue = "N";

  private boolean isuseleafimg = true;

  private boolean iscodeandname = false;

  private boolean isvisible = true;

  private boolean isreadonly = false;

  private boolean isexistcheck = false;

  private boolean isrelaparent = false;

  private boolean isrelachildren = false;

  private boolean isautoappear = true;

  private boolean iscollapseondblclick = true;

  private int initlevel = 0;

  private int tabindex = 0;

  private String cssclass = "";

  private String style = "";

  private String oninit = "";

  // 以上属性进入 tag;

  private String islowestfield = "";

  private boolean isupdatedata = true;

  private String dataXml = "";

  private Page ownerPage = null;

  private boolean initialized = false;

  //图片资源;
  public String getCompoName() {
    return null;
  }

  public String getIdsuffix() {
    return this.idsuffix;
  }

  public String getScriptVarName() {
    return this.getId() + "_TreeV";
  }

  public int getTabindex() {
    return this.tabindex;
  }

  public String getTableName() {
    return this.tablename;
  }

  public boolean isIsvisible() {
    return this.isvisible;
  }

  public void setIdsuffix(String idsuffix) {
    this.idsuffix = idsuffix;
  }

  public void writeHTML(Writer out) throws IOException {
    if (!this.initialized) {
      init();
    }
    out.write(this.makeOuterPanel_1());
    out.write(makeInnerPanel());
    out.write(this.makeOuterPanel_2());
    out.write(this.makeParamXML());
  }

  public void writeInitScript(Writer out) throws IOException {
    if (!this.initialized) {
      init();
    }
    out.write("var voTV = new TreeView();\n");
    out.write("voTV.make('" + this.getId() + "');\n");
    out.write("voTV.init();\n");

    // 向 PageX 中注册 TreeView 对象;
    out.write("PageX.regCtrlObj(\"" + this.getId() + "\", voTV);\n");
    if (isautoappear) {
      out.write("voTV.loadData();\n");
    }
    if (this.oninit != null && this.oninit.length() > 0) {
      out.write("var me = voTV;\n");
      out.write(this.getOninit() + ";\n");
      out.write("me=null;\n");
      out.write("voTV=null;\n");
    }
  }

  public void setOwnerPage(Page ownerPage) {
    this.ownerPage = ownerPage;
  }

  public void init() {
    img_opened = Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(this.ownerPage.getCurrRequest())
      + "/gp/image/ico/folderopened_16x16.gif";
    img_closed = Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(this.ownerPage.getCurrRequest())
      + "/gp/image/ico/folderclosed_16x16.gif";
    if (rootselimg == null || rootselimg.equals(""))
      rootselimg = img_opened;
    if (rootnormalimg == null || rootnormalimg.equals(""))
      rootnormalimg = img_closed;
    if (this.checkfield == null || this.checkfield.trim().length() == 0)
      this.checkfield = "__CHECK";
    if (this.getId() == null || this.getId().length() == 0)
      this.setId(this.getTablename() + "_" + this.getIdsuffix());
    this.initialized = true;
  }

  private String makeInnerPanel() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id=\"TreeView_InnerPanel\" ");
    voBuf.append("style=\"overflow:visible;\" ");
    voBuf.append("hidefocus='true' ");
    voBuf.append(">\n");
    voBuf.append("</div>\n");
    return voBuf.toString();
  }

  private String makeOuterPanel_1() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<div id='");
    voBuf.append(getId());
    voBuf.append("' ");
    voBuf.append("class='clsTreeDiv4 ");
    voBuf.append(getCssclass());
    voBuf.append("' ");
    voBuf.append("style='");
    voBuf.append(getOuterPanelStyle());
    voBuf.append(getStyle());
    voBuf.append("' ");
    voBuf.append("hidefocus='true' ");
    if (isIsreadonly()) {
      voBuf.append("disabled ");
    }
    voBuf.append(makeAttr());
    voBuf.append(">\n");
    return voBuf.toString();
  }

  private String makeOuterPanel_2() {
    return "</div>\n";
  }

  private String getOuterPanelStyle() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append(";");
    voBuf.append("display:" + (this.isIsvisible() ? "" : "none") + "; ");
    return voBuf.toString();
  }

  private String makeAttr() {
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("tablename='" + this.tablename + "' ");
    // voBuf.append("isfromdb='"+ this.isIsfromdb()+ "' ");
    voBuf.append("maketype=" + this.maketype + " ");
    voBuf.append("pagesize=" + this.pagesize + " ");

    voBuf.append("roottext='" + this.root + "' ");
    voBuf.append("roottip='" + this.roottip + "' ");
    voBuf.append("rootselimg='" + this.rootselimg + "' ");
    voBuf.append("rootnormalimg='" + this.rootnormalimg + "' ");

    voBuf.append("codefield='" + this.codefield + "' ");
    voBuf.append("namefield='" + this.namefield + "' ");
    voBuf.append("pcodefield='" + this.pcodefield + "' ");
    voBuf.append("islowestfield='" + this.islowestfield + "' ");
    voBuf.append("checkfield='" + this.checkfield + "' ");
    voBuf.append("tipfield='" + this.tipfield + "' ");
    voBuf.append("selimgfield='" + this.selimgfield + "' ");
    voBuf.append("normalimgfield='" + this.normalimgfield + "' ");
    voBuf.append("checkedvalue='" + this.checkedvalue + "' ");
    voBuf.append("uncheckedvalue='" + this.uncheckedvalue + "' ");

    voBuf.append("isuseleafimg='" + this.isuseleafimg + "' ");
    voBuf.append("iscodeandname='" + this.iscodeandname + "' ");

    voBuf.append("isvisible='" + this.isvisible + "' ");
    voBuf.append("isexistcheck='" + this.isexistcheck + "' ");
    voBuf.append("isrelaparent='" + this.isrelaparent + "' ");
    voBuf.append("isrelachildren='" + this.isrelachildren + "' ");
    voBuf.append("isupdatedata='" + this.isupdatedata + "' ");
    voBuf.append("iscollapseondblclick='" + iscollapseondblclick + "' ");
    voBuf.append("isautoappear='" + this.isIsautoappear() + "' ");
    voBuf.append("initlevel=" + this.initlevel + " ");
    voBuf.append("tabindex=" + this.tabindex + " ");
    return voBuf.toString();
  }

  private String makeParamXML() {
    if (this.isIsfromdb())
      return "";
    if (this.dataXml == null || this.dataXml.trim().length() < 10)
      return "";
    StringBuffer voBuf = new StringBuffer();
    String vsDataXmlId = "TableData_" + this.getTablename() + "_XML";
    voBuf.append("<xml id=\"" + vsDataXmlId
      + "\" asynch=\"false\" encoding=\"GBK\">\n");
    voBuf.append(this.dataXml + "\n");
    voBuf.append("</xml>\n");
    return voBuf.toString();
  }

  public void setTagAttributes(Node tagNode) {
    this.setId(XMLTools.getNodeAttr(tagNode, "id", this.id));
    this.setIdsuffix(XMLTools.getNodeAttr(tagNode, "idsuffix", this.idsuffix));
    this.setMaketype(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "maketype", ""
      + this.maketype)));
    this.setTablename(XMLTools.getNodeAttr(tagNode, "tablename", this.tablename));
    this.setIsfromdb(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isfromdb", "" + this.isfromdb)).booleanValue());
    this.setTabindex(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "tabindex", ""
      + this.tabindex)));
    this.setPagesize(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "pagesize", ""
      + this.pagesize)));
    this.setInitlevel(Integer.parseInt(XMLTools.getNodeAttr(tagNode, "initlevel", ""
      + this.initlevel)));

    this.setRoot(XMLTools.getNodeAttr(tagNode, "root", this.root));
    this.setRoottip(XMLTools.getNodeAttr(tagNode, "roottip", this.roottip));
    this.setRootselimg(XMLTools.getNodeAttr(tagNode, "rootselimg", this.rootselimg));
    this.setRootnormalimg(XMLTools.getNodeAttr(tagNode, "rootnormalimg",
      this.rootnormalimg));

    this.setCodefield(XMLTools.getNodeAttr(tagNode, "codefield", this.codefield));
    this.setNamefield(XMLTools.getNodeAttr(tagNode, "namefield", this.namefield));
    this.setPcodefield(XMLTools.getNodeAttr(tagNode, "pcodefield", this.pcodefield));
    this.setIslowestfield(XMLTools.getNodeAttr(tagNode, "islowestfield",
      this.islowestfield));
    this.setCheckfield(XMLTools.getNodeAttr(tagNode, "checkfield", this.checkfield));
    this.setTipfield(XMLTools.getNodeAttr(tagNode, "tipfield", this.tipfield));
    this.setSelimgfield(XMLTools.getNodeAttr(tagNode, "selimgfield",
      this.selimgfield));
    this.setNormalimgfield(XMLTools.getNodeAttr(tagNode, "normalimgfield",
      this.normalimgfield));
    this.setCheckedvalue(XMLTools.getNodeAttr(tagNode, "checkedvalue",
      this.checkedvalue));
    this.setUncheckedvalue(XMLTools.getNodeAttr(tagNode, "uncheckedvalue",
      this.uncheckedvalue));

    this.setIsreadonly(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isreadonly", "" + this.isreadonly))
      .booleanValue());
    this.setIsuseleafimg(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isuseleafimg", "" + this.isuseleafimg))
      .booleanValue());
    this.setIscodeandname(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "iscodeandname", "" + this.iscodeandname))
      .booleanValue());
    this.setIsexistcheck(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isexistcheck", "" + this.isexistcheck))
      .booleanValue());
    this.setIsrelaparent(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isrelaparent", "" + this.isrelaparent))
      .booleanValue());
    this.setIsRelachildren(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isrelachildren", "" + this.isrelachildren))
      .booleanValue());
    this.setIsupdatedata(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isupdatedata", "" + this.isupdatedata))
      .booleanValue());
    this.setIsvisible(Boolean.valueOf(
      XMLTools.getNodeAttr(tagNode, "isvisible", "" + this.isvisible))
      .booleanValue());

    this.setCssclass(XMLTools.getNodeAttr(tagNode, "cssclass", this.cssclass));
    this.setStyle(XMLTools.getNodeAttr(tagNode, "style", this.style));
    this.setOninit(XMLTools.getNodeAttr(tagNode, "oninit", this.oninit));
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTablename() {
    return tablename;
  }

  public void setTablename(String tablename) {
    this.tablename = tablename;
  }

  public boolean isIsfromdb() {
    return isfromdb;
  }

  public void setIsfromdb(boolean isfromdb) {
    this.isfromdb = isfromdb;
  }

  public int getMaketype() {
    return maketype;
  }

  public void setMaketype(int maketype) {
    this.maketype = maketype;
  }

  public int getPagesize() {
    return pagesize;
  }

  public void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String getRoottip() {
    return roottip;
  }

  public void setRoottip(String roottip) {
    this.roottip = roottip;
  }

  public String getRootselimg() {
    return rootselimg;
  }

  public void setRootselimg(String rootselimg) {
    this.rootselimg = rootselimg;
  }

  public String getRootnormalimg() {
    return rootnormalimg;
  }

  public void setRootnormalimg(String rootnormalimg) {
    this.rootnormalimg = rootnormalimg;
  }

  public String getCodefield() {
    return codefield;
  }

  public void setCodefield(String codefield) {
    this.codefield = codefield;
  }

  public String getNamefield() {
    return namefield;
  }

  public void setNamefield(String namefield) {
    this.namefield = namefield;
  }

  public String getPcodefield() {
    return pcodefield;
  }

  public void setPcodefield(String pcodefield) {
    this.pcodefield = pcodefield;
  }

  public String getCheckfield() {
    return checkfield;
  }

  public void setCheckfield(String checkfield) {
    this.checkfield = checkfield;
  }

  public String getTipfield() {
    return tipfield;
  }

  public void setTipfield(String tipfield) {
    this.tipfield = tipfield;
  }

  public String getSelimgfield() {
    return selimgfield;
  }

  public void setSelimgfield(String selimgfield) {
    this.selimgfield = selimgfield;
  }

  public String getNormalimgfield() {
    return normalimgfield;
  }

  public void setNormalimgfield(String normalimgfield) {
    this.normalimgfield = normalimgfield;
  }

  public String getCheckedvalue() {
    return checkedvalue;
  }

  public void setCheckedvalue(String checkedvalue) {
    this.checkedvalue = checkedvalue;
  }

  public String getUncheckedvalue() {
    return uncheckedvalue;
  }

  public void setUncheckedvalue(String uncheckedvalue) {
    this.uncheckedvalue = uncheckedvalue;
  }

  public boolean isIsuseleafimg() {
    return isuseleafimg;
  }

  public void setIsuseleafimg(boolean isuseleafimg) {
    this.isuseleafimg = isuseleafimg;
  }

  public boolean isIscodeandname() {
    return iscodeandname;
  }

  public void setIscodeandname(boolean iscodeandname) {
    this.iscodeandname = iscodeandname;
  }

  public boolean isIsreadonly() {
    return isreadonly;
  }

  public void setIsreadonly(boolean isreadonly) {
    this.isreadonly = isreadonly;
  }

  public boolean isIsexistcheck() {
    return isexistcheck;
  }

  public void setIsexistcheck(boolean isexistcheck) {
    this.isexistcheck = isexistcheck;
  }

  public boolean isIsrelaparent() {
    return isrelaparent;
  }

  public void setIsrelaparent(boolean isrelaparent) {
    this.isrelaparent = isrelaparent;
  }

  public boolean isIsrelachildren() {
    return isrelachildren;
  }

  public void setIsRelachildren(boolean isrelachildren) {
    this.isrelachildren = isrelachildren;
  }

  public boolean isIsautoappear() {
    return isautoappear;
  }

  public void setIsautoappear(boolean isautoappear) {
    this.isautoappear = isautoappear;
  }

  public boolean isIscollapseondblclick() {
    return iscollapseondblclick;
  }

  public void setIscollapseondblclick(boolean iscollapseondblclick) {
    this.iscollapseondblclick = iscollapseondblclick;
  }

  public int getInitlevel() {
    return initlevel;
  }

  public void setInitlevel(int initlevel) {
    this.initlevel = initlevel;
  }

  public String getCssclass() {
    return cssclass;
  }

  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
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

  public void setIsvisible(boolean isvisible) {
    this.isvisible = isvisible;
  }

  public void setTabindex(int tabindex) {
    this.tabindex = tabindex;
  }

  public void setIslowestfield(String islowestfield) {
    this.islowestfield = islowestfield;
  }

  public void setIsupdatedata(boolean isupdatedata) {
    this.isupdatedata = isupdatedata;
  }

}
