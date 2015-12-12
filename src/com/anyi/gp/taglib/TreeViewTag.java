package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.TreeView;

public class TreeViewTag extends BodyTagSupport {

  private TreeView o = new TreeView();
  
  public TreeViewTag() {
  }

  public int doStartTag() throws JspException{
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException{
    Page.addTreeView(pageContext.getRequest(), o);
    try {
      o.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    o = new TreeView();
    return EVAL_PAGE;
  }

  public void setCheckedvalue(String checkedvalue) {
    o.setCheckedvalue(checkedvalue);
  }

  public void setCheckfield(String checkfield) {
    o.setCheckfield(checkfield);
  }

  public void setCodefield(String codefield) {
    o.setCodefield(codefield);
  }

  public void setCssclass(String cssclass) {
    o.setCssclass(cssclass);
  }

  public void setId(String id) {
    o.setId(id);
  }

  public void setIdsuffix(String idsuffix) {
    o.setIdsuffix(idsuffix);
  }

  public void setInitlevel(int initlevel) {
    o.setInitlevel(initlevel);
  }

  public void setIsautoappear(boolean isautoappear) {
    o.setIsautoappear(isautoappear);
  }

  public void setIscodeandname(boolean iscodeandname) {
    o.setIscodeandname(iscodeandname);
  }

  public void setIscollapseondblclick(boolean iscollapseondblclick) {
    o.setIscollapseondblclick(iscollapseondblclick);
  }

  public void setIsexistcheck(boolean isexistcheck) {
    o.setIsexistcheck(isexistcheck);
  }

  public void setIsfromdb(boolean isfromdb) {
    o.setIsfromdb(isfromdb);
  }

  public void setIsupdatedata(boolean isupdatedata) {
    o.setIsupdatedata(isupdatedata);
  }

  public void setIsreadonly(boolean isreadonly) {
    o.setIsreadonly(isreadonly);
  }

  public void setIsRelachildren(boolean isrelachildren) {
    o.setIsRelachildren(isrelachildren);
  }

  public void setIsrelaparent(boolean isrelaparent) {
    o.setIsrelaparent(isrelaparent);
  }

  public void setIsuseleafimg(boolean isuseleafimg) {
    o.setIsuseleafimg(isuseleafimg);
  }

  public void setIsvisible(boolean isvisible) {
    o.setIsvisible(isvisible);
  }

  public void setMaketype(int maketype) {
    o.setMaketype(maketype);
  }

  public void setNamefield(String namefield) {
    o.setNamefield(namefield);
  }

  public void setNormalimgfield(String normalimgfield) {
    o.setNormalimgfield(normalimgfield);
  }

  public void setOninit(String oninit) {
    o.setOninit(oninit);
  }

  public void setPagesize(int pagesize) {
    o.setPagesize(pagesize);
  }

  public void setPcodefield(String pcodefield) {
    o.setPcodefield(pcodefield);
  }

  public void setRoot(String root) {
    o.setRoot(root);
  }

  public void setRootnormalimg(String rootnormalimg) {
    o.setRootnormalimg(rootnormalimg);
  }

  public void setRootselimg(String rootselimg) {
    o.setRootselimg(rootselimg);
  }

  public void setRoottip(String roottip) {
    o.setRoottip(roottip);
  }

  public void setSelimgfield(String selimgfield) {
    o.setSelimgfield(selimgfield);
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

  public void setTipfield(String tipfield) {
    o.setTipfield(tipfield);
  }

  public void setUncheckedvalue(String uncheckedvalue) {
    o.setUncheckedvalue(uncheckedvalue);
  }

}
