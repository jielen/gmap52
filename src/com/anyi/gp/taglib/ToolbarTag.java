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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.Toolbar;

/**
 * @author   leidaohong
 */
public class ToolbarTag extends BodyTagSupport {

  /**
   * 
   */
  private static final long serialVersionUID = 8135732422832811518L;
 public final static String TAG_INTERFACE_FILTER_FUNC_MAP = "toolbartag_interface_filter_func_map";

  private Toolbar toolbar = new Toolbar();

  public ToolbarTag() {
  }

  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    BodyContent bc = this.getBodyContent();
    Map funcFiltermap = getFilterFuncMap();
    toolbar.setFilterFuncMap(funcFiltermap);
    if (bc != null) {
      toolbar.setBodytext(bc.getString());
    }
    Page.addToolbar(pageContext.getRequest(), toolbar);
    try {
      toolbar.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new RuntimeException();
    }
    this.toolbar = new Toolbar();
    return EVAL_PAGE;
  }
  
	/**
	 * 接口参数处理; leidh; 20050902;
	 * 
	 * @return
	 */
	private Map getFilterFuncMap() {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		if (request == null)
			return null;
		return (Map) request.getAttribute(TAG_INTERFACE_FILTER_FUNC_MAP);
	}

  public void setComponame(String componame) {
    toolbar.setComponame(componame);
  }

  public void setCssclass(String cssclass) {
    toolbar.setCssclass(cssclass);
  }

  public void setId(String id) {
    toolbar.setId(id);
  }

  public void setIdsuffix(String idsuffix) {
    toolbar.setIdsuffix(idsuffix);
  }

  public void setIsfromdb(boolean isfromdb) {
    toolbar.setIsfromdb(isfromdb);
  }

  public void setIsvisible(boolean isvisible) {
    toolbar.setIsvisible(isvisible);
  }

  public void setOninit(String oninit) {
    toolbar.setOninit(oninit);
  }

  public void setStyle(String style) {
    toolbar.setStyle(style);
  }

  public void setTabindex(int tabindex) {
    toolbar.setTabindex(tabindex);
  }
}
