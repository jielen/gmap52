/* $Id */
package com.anyi.gp.taglib;

/**
 * <p>Title: UFGOV A++ Platform Page Tag</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UFGOV</p>
 * @author lijianwei
 * @version 1.0
 */
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

public class BlankRowTag extends TagSupport{
  
  private static final long serialVersionUID = 5884238899817129994L;
  
  private static final Logger log = Logger.getLogger(BlankRowTag.class);

  private int height= 10;

  public BlankRowTag(){
  }

  public int doStartTag() throws JspException{
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException{
    try{
      pageContext.getOut().write(make());
    }catch(IOException e){
      log.error(e);
      throw new JspException(e);
    }
    return EVAL_PAGE;
  }

  private String make(){
    StringBuffer voBuf= new StringBuffer();
    voBuf.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size:1px;width:100%;\">");
    voBuf.append("<tr style=\"height:");
    voBuf.append(getHeight());
    voBuf.append("px;\"><td></td></tr></table>");
    return voBuf.toString();
  }


  private int getHeight() {
  	return height;
  }
  
  public void setHeight(int height) {
  	this.height = height;
  }
  
}
