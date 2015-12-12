package com.anyi.gp.core.action;

import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.access.FileExportService;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class ExportExcelDataAction extends ActionSupport {

	private static final long serialVersionUID = -5779573990738686561L;

	private String ruleID;

	private String condition;

	private String tableHead;

	private String tableData;
	
	private String tableName;
	
	private String valueSet;
	
	private String compoName;
	
	public String getCompoName() {
		return compoName;
	}

	public void setCompoName(String compoName) {
		this.compoName = compoName;
	}

	public String getValueSet() {
		return valueSet;
	}

	public void setValueSet(String valueSet) {
		this.valueSet = valueSet;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	public String getTableData() {
		return tableData;
	}

	public void setTableData(String tableData) {
		this.tableData = tableData;
	}

	public String getTableHead() {
		return tableHead;
	}

	public void setTableHead(String tableHead) {
		this.tableHead = tableHead;
	}

	public String execute() throws Exception {
    if(1 == 1) return SUCCESS;
    
    //TODO:注意一下代码不执行
		HttpServletResponse response = ServletActionContext.getResponse();
		String userNumLimCondition = this.getUserNumLimCondition();
    
    Runtime rt = Runtime.getRuntime();
    //System.out.println("before export:");
    //System.out.println("Total   Memory   =   " + rt.totalMemory());
    //System.out.println("Free   Memory   =   " + rt.freeMemory());   
		
    FileExportService.createExcel(tableName, userNumLimCondition, tableHead, tableData, null, ruleID, condition, valueSet);
		FileExportService.exportExcel(null, tableName, response);
		
    Runtime rt2 = Runtime.getRuntime();
    //System.out.println("after export:");
    //System.out.println("Total   Memory   =   " + rt2.totalMemory());
    //System.out.println("Free   Memory   =   " + rt2.freeMemory());
    
    System.gc();
    Runtime rt3 = Runtime.getRuntime();
    //System.out.println("after gc:");
    //System.out.println("Total   Memory   =   " + rt3.totalMemory());
    //System.out.println("Free   Memory   =   " + rt3.freeMemory());
    
		return NONE;
	}
	
	public String getUserNumLimCondition(){
		String userNumLimCondition = "";
		String userId = SessionUtils.getAttribute(ServletActionContext.getRequest(), "svUserID");
    if(tableName != null){
      CompoMeta compoMeta = MetaManager.getCompoMeta(compoName);
      if(compoMeta != null && tableName.equals(compoMeta.getMasterTable())){
        userNumLimCondition = RightUtil.getUserNumLimCondition(ServletActionContext.getRequest(), userId,
          "fwatch", compoName, null, null);
      }
    }
		return userNumLimCondition;
	}
}
