package com.anyi.gp.core.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.meta.Field;
import com.anyi.gp.taglib.components.Grid;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class GetHeadTableAction extends AjaxAction implements ServletRequestAware{
	
	private static final long serialVersionUID = -2816639370259786580L;

	private String compoName;

	private String tableName;

	private String paramMeta;

	private int rowHeight;

	private String pkFields;

	private HttpServletRequest request;
	
	public void setCompoName(String compoName) {
		this.compoName = compoName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setParamMeta(String paramMeta) {
		this.paramMeta = paramMeta;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public void setPkFields(String pkFields) {
		this.pkFields = pkFields;
	}

	public String getCompoName() {
		return compoName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getParamMeta() {
		return paramMeta;
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public String getPkFields() {
		return pkFields;
	}

	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String doExecute() {
		try {
			Grid grid = new Grid();
	    grid.setRequest(request);
	    grid.setCompoName(compoName);
	    grid.setTableName(tableName);
	    grid.setBodyText(paramMeta);
	    grid.setRowheight(rowHeight);
	    grid.init(false);
	    if (pkFields != null) {
	      pkFields = pkFields.trim();
	      if (pkFields.length() > 0) {
	        List voPKFieldList = StringTools.split(pkFields, ",");
	        String vsField = "";
	        Field voField = null;
	        
	        Map tablefieldmap = grid.getTableFields();
	        for (Iterator iter = voPKFieldList.iterator(); iter.hasNext();) {
	          vsField = (String) iter.next();
	          if (vsField == null || vsField.length() == 0) {
	            continue;
	          }
	          
	          voField = (Field) tablefieldmap.get(vsField);
	          if (voField == null) {
	            continue;
	          }
	          //XMLTools.setNodeAttr(voField, "ispk", "true");
	          voField.setPk(true);
	        }
	      }
	    }
	    
	    resultstring = this.wrapResultStr("true",grid.makeHeadBody());
		} catch (Exception ex) {
			resultstring = this.wrapResultStr("false", ex.getMessage());
		}
		return SUCCESS;
	}

}
