package com.anyi.gp.core.action;

import java.io.File;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.util.FileTools;

public class DeletePropAction extends AjaxAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String GRID_PROPERTY_USERS = "gp" + File.separator
        + "gridproperty" + File.separator + "users" + File.separator;
	
	public static final String GRID_PROPERTY_DEFAULT = "gp" + File.separator
        + "gridproperty" + File.separator + "default" + File.separator;
	
	
	private String userId;

	private String pageName;

	private String tableName;

	private String gridId;

	private String propFileId;

	private String prop;

	public void setUserId(String UserId) {
		userId = UserId;
	}

	public void setPageName(String PageName) {
		pageName = PageName;
	}

	public void setTableName(String TableName) {
		tableName = TableName;
	}

	public void setGridId(String GridId) {
		gridId = GridId;
	}

	public void setPropFileId(String PropFileId) {
		propFileId = PropFileId;
	}

	public void setProp(String Prop) {
		prop = Prop;
	}

	public String getUserId() {
		return userId;
	}

	public String getPageName() {
		return pageName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getGridId() {
		return gridId;
	}

	public String getPropFileId() {
		return propFileId;
	}

	public String getProp() {
		return prop;
	}

	public String doExecute() throws Exception {
		if (pageName == null || gridId == null || tableName == null) {
			resultstring = wrapResultStr("false", "");
			return SUCCESS;
		}

		String vsFile = this.getPropFileName(true);
	  resultstring = wrapResultStr("true",makeRetString(FileTools.deleteFile(vsFile), "", vsFile));
		return SUCCESS;
	}

	private String makeRetString(boolean tSuccess, String sDigest, String sText) {
		StringBuffer voSB = new StringBuffer();
		voSB.append("<response id=\"result\" success=\"" + tSuccess + "\">\n");
		voSB.append("<info digest=\"" + sDigest + "\">");
		voSB.append(sText);
		voSB.append("</info>\n");
		voSB.append("</response>\n");
		return voSB.toString();
	}

	private String getPropFileName(boolean tIsUser) {
		if (pageName == null || gridId == null || tableName == null) {
			return null;
		}
		
		String vsPropPath = GRID_PROPERTY_USERS;
		if (!Boolean.valueOf(
				ApplusContext.getEnvironmentConfig().get("isrunningmode"))
				.booleanValue()) {
			tIsUser = false;
		}
		if (!tIsUser) {
			vsPropPath = GRID_PROPERTY_DEFAULT;
			userId = "default";
		}
		String vsFile = (userId + "$" + pageName + "$" + tableName + "$" + propFileId)
				+ ".xml";
		String vsPath = GeneralFunc.getWorkPath() + vsPropPath;
		FileTools.makeDir(vsPath);
		vsFile = vsPath + vsFile;
		return vsFile;
	}

}