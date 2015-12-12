package com.anyi.gp.taglib.components;

import org.w3c.dom.Node;

import com.anyi.gp.Pub;
import com.anyi.gp.meta.Foreign;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.util.XMLTools;

public class ForeignBox extends TextBox {

  public static final String COMPO_FOREIGN_FOR_SQL = "AS_FOREIGN_FOR_SQL";

  private boolean isstrictinput = true;
  private boolean isclearinput = true;
  private boolean isautofilldata = true;
  private String noclearfields = "";
  private String searchedfields = "";
  private String defsearchtext = "";
  private boolean isfuzzymatch = true;
  private boolean isallcheckvisible = false;
  private boolean isallowrollback = true;
  private String sqlid = "";
  private String condition = "";
  private boolean istreeview = false;
  private boolean isboxconddisabled = true;
  private String usercond = "";
  private boolean isMultiSel = false;
  // 以上进入标记属性;
  
  private Foreign foreignMeta = null;
  private String paramXmlId = "";
  private String bodyText = "";
  private boolean hasOutParamXml = false;

  // 图片资源;
  private String select_button_img = "";
  
  public ForeignBox(){
    this.setBoxtype("ForeignBox");
  }

  public void init(){
    super.init();
    select_button_img = Page.LOCAL_RESOURCE_PATH + Pub.getWebRoot(this.getContainer().getPage().getCurrRequest()) + "/gp/image/ico/search_16x16.gif";
    this.paramXmlId = "Foreign_" + Pub.getUID() + "_XML";
    if(this.isIsfromdb()){
      String refName = this.getFieldmeta().getRefName();
      if(this.getOuterField() != null)
        refName = XMLTools.getNodeAttr(this.getOuterField(), "foreignname", refName);
      
      TableMeta tm = MetaManager.getTableMeta(this.getTableName());
      foreignMeta = tm.getForeign(refName);
    }
  }

  protected String makeOtherTD(){
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<td width=\"16px\"><img id='selectButton' src=\"");
    voBuf.append(select_button_img);
    voBuf.append("\" tabindex='32766'  align=\"ABSBOTTOM\" ");
    if (this.isIsreadonly()){
      voBuf.append("disabled ");
    }
    voBuf.append("></td>\n");
    voBuf.append(this.makeParamTD());
    return voBuf.toString();
  }

  private String makeParamTD(){
    // 关于 foreign 定义的 xml 只输出一次.
    if(hasOutParamXml)
      return "";
    if(foreignMeta != null)
      bodyText = foreignMeta.toXML();
    
    if(bodyText == null || bodyText.length() == 0)
      return "";
    
    if(bodyText.indexOf("istreeview") < 0){
      int pos = bodyText.indexOf("<foreign");
      if(pos > 0){
        bodyText = "<foreign istreeview=\"" + istreeview + "\"" + bodyText.trim().substring(8);
      }
    }
    
    int pos = bodyText.indexOf("</foreign>");
    if(pos > 0){
      bodyText = bodyText.substring(0, pos) + "<sqlid>" + sqlid + "</sqlid>\n"
                 + "<condition>" + condition + "</condition>\n</foreign>";
    }
    
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<td style=\"display:none\"><xml id=\"");
    voBuf.append(this.paramXmlId);
    voBuf.append("\" asynch=\"false\" encoding=\"GBK\">\n");
    voBuf.append(bodyText);
    voBuf.append("\n</xml></td>\n");
    hasOutParamXml = true;
    return voBuf.toString();
  }

  protected String makeAttr(){
    StringBuffer voSBuf = new StringBuffer();
    voSBuf.append(super.makeAttr());
    voSBuf.append("isstrictinput=\"" + this.isIsstrictinput() + "\" ");
    voSBuf.append("isclearinput=\"" + this.isIsclearinput() + "\" ");
    voSBuf.append("isautofilldata=\"" + this.isIsautofilldata() + "\" ");
    voSBuf.append("isboxconddisabled=\"" + this.isIsboxconddisabled() + "\" ");
    voSBuf.append("usercond=\"" + this.usercond + "\" ");
    voSBuf.append("isMultiSel=\"" + this.isMultiSel + "\" ");
    voSBuf.append("noclearfields=\"" + this.getNoclearfields() + "\" ");
    voSBuf.append("searchedfields=\"" + this.getSearchedfields() + "\" ");
    voSBuf.append("isfuzzymatch=\"" + this.isIsfuzzymatch() + "\" ");
    voSBuf.append("isallcheckvisible=\"" + isallcheckvisible + "\" ");
    voSBuf.append("isallowrollback=\"" + isallowrollback + "\" ");
    voSBuf.append("foreignxmlid=\"" + this.paramXmlId + "\" ");
    return voSBuf.toString();
  }

  public void setBodytext(String text){
    this.bodyText = text;
  }

  /**
   * <applus:area> 接口方法的实现;
   */
  public void initByField(Node tagNode){
    super.initByField(tagNode);
    this.setIsstrictinput(Boolean.valueOf(XMLTools.getNodeAttr(tagNode, "isstrictinput", "" + this.isstrictinput)).booleanValue());
    this.setIsclearinput(Boolean.valueOf(XMLTools.getNodeAttr(tagNode, "isclearinput", "" + this.isclearinput)).booleanValue());
    this.setIsautofilldata(Boolean.valueOf(XMLTools.getNodeAttr(tagNode, "isautofilldata", "" + this.isautofilldata)).booleanValue());
    this.setIsboxconddisabled(Boolean.valueOf(XMLTools.getNodeAttr(tagNode, "isboxconddisabled", "" + this.isboxconddisabled)).booleanValue());
    this.setSqlid(XMLTools.getNodeAttr(tagNode, "sqlid", this.sqlid));
    this.setCondition(XMLTools.getNodeAttr(tagNode, "condition", this.condition));
    this.setUsercond(XMLTools.getNodeAttr(tagNode, "usercond", this.usercond));
    this.setIstreeview(Pub.parseBool(XMLTools.getNodeAttr(tagNode, "istreeview", this.istreeview + "")));
  }

  public void setIsstrictinput(boolean isstrictinput) {
    this.isstrictinput = isstrictinput;
  }

  public void setIsclearinput(boolean isclearinput) {
    this.isclearinput = isclearinput;
  }

  public void setIsautofilldata(boolean isautofilldata) {
    this.isautofilldata = isautofilldata;
  }

  public void setNoclearfields(String noclearfields) {
    this.noclearfields = noclearfields;
  }

  public void setSearchedfields(String searchedfields) {
    this.searchedfields = searchedfields;
  }

  public void setDefsearchtext(String defsearchtext) {
    this.defsearchtext = defsearchtext;
  }

  public void setIsfuzzymatch(boolean isfuzzymatch) {
    this.isfuzzymatch = isfuzzymatch;
  }

  public void setIsallcheckvisible(boolean isallcheckvisible) {
    this.isallcheckvisible = isallcheckvisible;
  }

  public void setIsallowrollback(boolean isallowrollback) {
    this.isallowrollback = isallowrollback;
  }

  public void setSqlid(String sqlid) {
    this.sqlid = sqlid;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public void setIstreeview(boolean istreeview) {
    this.istreeview = istreeview;
  }

  public void setIsboxconddisabled(boolean isboxconddisabled) {
    this.isboxconddisabled = isboxconddisabled;
  }

  public void setBodyText(String paramXml) {
    this.bodyText = paramXml;
  }

  private boolean isIsstrictinput() {
    return isstrictinput;
  }

  private boolean isIsclearinput() {
    return isclearinput;
  }

  private boolean isIsautofilldata() {
    return isautofilldata;
  }

  private String getNoclearfields() {
    return noclearfields;
  }

  private String getSearchedfields() {
    return searchedfields;
  }

  private boolean isIsfuzzymatch() {
    return isfuzzymatch;
  }

  private boolean isIsboxconddisabled() {
    return isboxconddisabled;
  }

  public void setUsercond(String usercond) {
    this.usercond = usercond;
  }
  public boolean isMultiSel() {
    return isMultiSel;
  }

  public void setIsMultiSel(boolean isMultiSel) {
    this.isMultiSel = isMultiSel;
  }

}
