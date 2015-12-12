package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.CompoMetaPart;
import com.anyi.gp.taglib.components.Page;

public class CompoMetaTag extends BodyTagSupport {
  
  private static final long serialVersionUID = 4809657702927803522L;

  private CompoMetaPart meta = new CompoMetaPart();

  public CompoMetaTag() {
  }

  /**
   * 解析标记开始
   * 
   * @throws JspException
   * @return int
   */
  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  /**
   * 解析标记结束
   * 
   * @throws JspException
   * @return int
   */
  public int doEndTag() throws JspException {
    JspWriter out = this.pageContext.getOut();
    Page.addCompoMetaPart(pageContext.getRequest(),meta);
    try {
      meta.writeHTML(out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    meta = new CompoMetaPart();
    return EVAL_PAGE;
  }

  public void setType(String type) {
    meta.setType(type);
  }

  public void setName(String name) {
    meta.setName(name);
  }

  public void setIsmain(boolean ismain) {
    this.meta.setIsmain(ismain);
  }

}
