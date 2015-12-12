package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;

public class QueryPreviousNodeFieldListValueAction extends AjaxAction{
	private String instanceId;
	private String nodeId;
	private String fieldNameList;
	private String step;
	
	public String getFieldNameList(){
		return fieldNameList;
	}

	public void setFieldNameList(String fieldNameList){
		this.fieldNameList = fieldNameList;
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

	public String getStep(){
		return step;
	}

	public void setStep(String step){
		this.step = step;
	}

	public String doExecute() throws Exception{
		// TCJLODO Auto-generated method stub
		//WFHistoryRestore hr = new WFHistoryRestore();
		String dataStr = "";
		String flag = "false";
		try {
			//dataStr = hr.queryPreviousNodeFieldListValue(this.instanceId, this.nodeId, this.fieldNameList, this.step);
			flag = "true";
		} catch (Exception ex) {
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}

}
