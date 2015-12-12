/* $Id */
package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.Tabstrip;

public class TabstripTag extends BodyTagSupport {
  private static final long serialVersionUID = -5666504291757007306L;

  private Tabstrip o = new Tabstrip();

  public boolean addTab(TabTag tab) {
    return o.addTab(tab);
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

  public void setIsvisible(boolean isvisible) {
    o.setIsvisible(isvisible);
  }

  public void setOninit(String oninit) {
    o.setOninit(oninit);
  }

  public void setOrientation(String orientation) {
    o.setOrientation(orientation);
  }

  public void setStyle(String style) {
    o.setStyle(style);
  }

  public void setTabindex(int tabindex) {
    o.setTabindex(tabindex);
  }

  public TabstripTag() {
  }

  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    Page.addTabstrip(pageContext.getRequest(),o);
    try {
      o.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.o = new Tabstrip();
    return EVAL_PAGE;
  }

}
