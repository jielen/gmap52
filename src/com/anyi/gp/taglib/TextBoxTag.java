package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.TextBox;

public class TextBoxTag extends BodyTagSupport {
  
  private static final long serialVersionUID = -4159871866788115452L;

  private TextBox o = null;

  public TextBoxTag() {
    this.o = this.makeO();
  }

  protected TextBox makeO(){
    return new TextBox();
  }
  
  protected void setO(TextBox o){
    this.o = o;
  }
  
  protected TextBox getO(){
    return o;
  }
  
  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    Page.addEditBox(pageContext.getRequest(), o);
    try {
      o.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.setO(this.makeO());
    return EVAL_PAGE;
  }

  public void setAdditional(String additional) {
    o.setAdditional(additional);
  }

  public void setComponame(String componame) {
    o.setComponame(componame);
  }

  public void setCssclass(String cssclass) {
    o.setCssclass(cssclass);
  }

  public void setDefvalue(String defvalue) {
    o.setDefvalue(defvalue);
  }

  public void setFieldname(String fieldname) {
    o.setFieldname(fieldname);
  }

  public void setFieldtype(String fieldtype) {
    o.setFieldtype(fieldtype);
  }

  public void setGroupid(String groupid) {
    o.setGroupid(groupid);
  }

  public void setId(String id) {
    o.setId(id);
  }

  public void setIdsuffix(String idsuffix) {
    o.setIdsuffix(idsuffix);
  }

  public void setIsallowinput(boolean isallowinput) {
    o.setIsallowinput(isallowinput);
  }

  public void setIsallownull(boolean isallownull) {
    o.setIsallownull(isallownull);
  }

  public void setIsalone(boolean isalone) {
    o.setIsalone(isalone);
  }

  public void setIsexact(boolean isexact) {
    o.setIsexact(isexact);
  }

  public void setIsforcedflt(boolean isforcedflt) {
    o.setIsforcedflt(isforcedflt);
  }

  public void setIsforcereadonly(boolean isforcereadonly) {
    o.setIsforcereadonly(isforcereadonly);
  }

  public void setIsfreemember(boolean isfreemember) {
    o.setIsfreemember(isfreemember);
  }

  public void setIsfromdb(boolean isfromdb) {
    o.setIsfromdb(isfromdb);
  }

  public void setIsvisible(boolean isvisible) {
    o.setIsvisible(isvisible);
  }

  public void setMaxlen(int maxlen) {
    o.setMaxlen(maxlen);
  }

  public void setMinlen(int minlen) {
    o.setMinlen(minlen);
  }

  public void setOninit(String oninit) {
    o.setOninit(oninit);
  }

  public void setOwnertype(String ownertype) {
    o.setOwnertype(ownertype);
  }

  public void setStyle(String style) {
    o.setStyle(style);
  }

  public void setTabindex(int tabindex) {
    o.setTabindex(tabindex);
  }

  public void setTablename(String tablename) {
    o.setTablename(tablename);
  }

  public void setValue(String value) {
    o.setValue(value);
  }

  public void setIsOut(boolean isout) {
    o.setIsout(isout);
  }

  public void setIsreadonly(boolean isreadonly) {
    o.setIsreadonly(isreadonly);
  }

}
