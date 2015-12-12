package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;

public class QueryPreviousNodePageDataByNodeAction extends AjaxAction{
	private String instanceId;
	private String nodeId;
	private String beforeModified;
	
	public String getBeforeModified(){
		return beforeModified;
	}

	public void setBeforeModified(String beforeModified){
		this.beforeModified = beforeModified;
	}

	public String getInstanceId(){
		return instanceId;
	}

	public void setInstanceId(String instanceId){
		this.instanceId = instanceId;
	}

	public String getNodeId(){
		return nodeId;
	}

	public void setNodeId(String nodeId){
		this.nodeId = nodeId;
	}

	public String doExecute() throws Exception{
		// TCJLODO Auto-generated method stub
		//WFHistoryRestore hr = new WFHistoryRestore();
		String dataStr = "";
		String flag = "false";
		try {
			//dataStr = hr.queryPreviousNodePageData(this.instanceId, this.nodeId, this.beforeModified);
			flag = "true";
		} catch (Exception ex) {
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}

}
