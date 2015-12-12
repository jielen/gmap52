package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Include;
import com.anyi.gp.taglib.components.Page;

public class IncludeTag extends BodyTagSupport {

  private static final long serialVersionUID = -2900805066893712893L;

  private Include include = new Include();

  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    Page.addInclude(pageContext.getRequest(),include);
    BodyContent bc = this.getBodyContent();
    if (bc != null){
      include.setBodyContent(bc.getString());
    }
    try {
      include.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.include = new Include();
    return EVAL_PAGE;
  }

  public void setType(String type) {
    //为保持兼容遗留下来的方法
  }

  public void setLanguage(String language) {
    //为保持兼容遗留下来的方法
  }
}
