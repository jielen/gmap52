package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.SearchBox;

public class SearchTag extends BodyTagSupport {

  private static final long serialVersionUID = -7373587437630489056L;

  private SearchBox o = new SearchBox();
  
	public SearchTag() {
	}

	public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspException {
    Page.addSearchBox(pageContext.getRequest(), o);
    try {
      o.writeHTML(pageContext.getOut());
    } catch (IOException e) {
      throw new JspException(e);
    }
    this.o = new SearchBox();
		return EVAL_PAGE;
	}

  public void setCssclass(String cssclass) {
    o.setCssclass(cssclass);
  }

  public void setFunction(String function) {
    o.setFunction(function);
  }

  public void setGroupid(String groupid) {
    o.setGroupid(groupid);
  }

  public void setId(String id) {
    o.setId(id);
  }

  public void setIdsuffix(String idsuffix) {
    o.setIdsuffix(idsuffix);
  }

  public void setIsadvancebtnvisible(boolean isadvancebtnvisible) {
    o.setIsadvancebtnvisible(isadvancebtnvisible);
  }

  public void setIsgroupbtnvisible(boolean isgroupbtnvisible) {
    o.setIsgroupbtnvisible(isgroupbtnvisible);
  }

  public void setIsmatchinputvisible(boolean ismatchinputvisible) {
    o.setIsmatchinputvisible(ismatchinputvisible);
  }

  public void setIssearchbtnvisible(boolean issearchbtnvisible) {
    o.setIssearchbtnvisible(issearchbtnvisible);
  }

  public void setIsvisible(boolean isvisible) {
    o.setIsvisible(isvisible);
  }

  public void setKilofields(String kilofields) {
    o.setKilofields(kilofields);
  }

  public void setOninit(String oninit) {
    o.setOninit(oninit);
  }

  public void setPattern(String pattern) {
    o.setPattern(pattern);
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

}
