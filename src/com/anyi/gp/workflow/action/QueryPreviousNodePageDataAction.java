package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;

public class QueryPreviousNodePageDataAction extends AjaxAction{
	private String instanceId;
	private String nodeId;
	private String step;
	
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

	public String getStep(){
		return step;
	}

	public void setStep(String step){
		this.step = step;
	}

	public String doExecute() {
		// TCJLODO Auto-generated method stub
		String dataStr = "";
		String flag = "false";
		//WFHistoryRestore hr = new WFHistoryRestore();
		try {
			//dataStr = hr.queryPreviousNodePageData(this.instanceId, this.nodeId, this.step);
			flag = "true";
		} catch (Exception ex) {
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}

}
