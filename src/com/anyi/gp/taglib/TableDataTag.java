package com.anyi.gp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.taglib.components.TableDataPart;

public class TableDataTag extends BodyTagSupport {

	private static final long serialVersionUID = 6442187238480315198L;

	private TableDataPart tdp = new TableDataPart();

	public TableDataTag() {
	}

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspException {
		Page.addTableDataPart(pageContext.getRequest(), tdp);
		try {
			tdp.setRequest(pageContext.getRequest());
			tdp.writeHTML(pageContext.getOut());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.tdp = new TableDataPart();
		return EVAL_PAGE;
	}

	public void setComponame(String componame) {
		tdp.setComponame(componame);
	}

	public void setCondition(String condition) {
		tdp.setCondition(condition);
	}

	public void setFromrow(int fromrow) {
		tdp.setFromrow(fromrow);
	}

	public void setIsdigest(boolean isdigest) {
		tdp.setIsdigest(isdigest);
	}

	public void setIssave(boolean issave) {
		tdp.setIssave(issave);
	}

	public void setIstablemeta(boolean istablemeta) {
		tdp.setIstablemeta(istablemeta);
	}

	public void setOnceautonumfields(String onceautonumfields) {
		tdp.setOnceautonumfields(onceautonumfields);
	}

	public void setOwnerPage(Page ownerPage) {
		tdp.setOwnerPage(ownerPage);
	}

	public void setPagesize(int pagesize) {
		tdp.setPagesize(pagesize);
	}

	public void setPhysicaltable(String physicaltable) {
		tdp.setPhysicaltable(physicaltable);
	}

	public void setSqlid(String sqlid) {
		tdp.setSqlid(sqlid);
	}

	public void setTablename(String tablename) {
		tdp.setTablename(tablename);
	}

	public void setTorow(int torow) {
		tdp.setTorow(torow);
	}

	public void setTotalfields(String totalfields) {
		tdp.setTotalfields(totalfields);
	}
  

  public void setSqlCountid(String sqlCountid) {
    tdp.setSqlCountid(sqlCountid);
  }


  public void setSqlSumid(String sqlSumid) {
    tdp.setSqlSumid(sqlSumid);
  }

}
