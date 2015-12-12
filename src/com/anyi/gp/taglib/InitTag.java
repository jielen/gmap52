package com.anyi.gp.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;

public class InitTag extends BodyTagSupport {

  private static final long serialVersionUID = 653460947870682650L;
	// 以上变量时入标记属性;
	public InitTag() {
	}

	public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspException {
    BodyContent bc = this.getBodyContent();
    if (bc != null){
      Page.getPage(pageContext.getRequest()).setInitScript(bc.getString());
    }
		return EVAL_PAGE;
	}

	public void setPagelayout(String pagelayout) {
	}

	public void setPagetype(String pageType) {
	  Page.getPage(pageContext.getRequest()).setPageType(pageType);
	}
}
