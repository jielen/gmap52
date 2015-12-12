package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.SessionData;

/**
 * format:
 * <applus:sessiondata componame="" />
 *
 */
public class SessionDataTag extends BodyTagSupport {
  private static final long serialVersionUID = 3568350486502463488L;
  
  private SessionData sessionData = new SessionData();
  
  public SessionDataTag() {
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
    Page.addSessionData(pageContext.getRequest(),sessionData);
    try {
      sessionData.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.sessionData = new SessionData();
    return EVAL_PAGE;
  }

  public void setComponame(String componame) {
    this.sessionData.setComponame(componame);
  }
  

}
