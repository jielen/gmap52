package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.anyi.gp.taglib.components.Page;

public class EndPageTag extends TagSupport{

  private static final long serialVersionUID = -7728472040187400075L;

  public EndPageTag() {
  }

  public int doStartTag() throws JspException {
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
    Page page = Page.getPage(pageContext.getRequest());
    try {
      pageContext.getOut().write(makeTextSizeTA());
      page.writePageInitScript(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_PAGE;
  }

  private String makeTextSizeTA(){
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("<table id='TestTextSizeTA' border=\"0\" width=\"1%\" cellpadding=\"0\">");
    voBuf.append("<tbody><tr><td width=\"100%\" nowrap></td></tr></tbody></table>");
    return voBuf.toString();
  }

}
