/* $Id */
package com.anyi.gp.taglib;

/**
 * <applus:logic>
 *   <logic name="" type="" classname="">
 *     <param name="" value="" />
 *     <param name="" value="" />
 *   </logic>
 * </applus:logic>
 */

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Logic;
import com.anyi.gp.taglib.components.Page;

public class LogicTag extends BodyTagSupport {

  private Logic o = new Logic();

  public LogicTag() {
  }

  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    Page.addLogic(pageContext.getRequest(),o);
    BodyContent bc = this.getBodyContent();
    if (bc != null) {
      o.setBodyText(bc.getString());
    }
    try {
      o.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.o = new Logic();
    return EVAL_PAGE;
  }

  public void setId(String id){
    this.o.setId(id);
  }
}
