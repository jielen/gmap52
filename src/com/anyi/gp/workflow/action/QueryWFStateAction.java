package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.workflow.WFGeneral;

public class QueryWFStateAction extends AjaxAction{
	private String nodeId;
	private String wfStateName;
	private String offset;
	private String actionName;
	
	public String getActionName(){
		return actionName;
	}

	public void setActionName(String actionName){
		this.actionName = actionName;
	}

	public String getNodeId(){
		return nodeId;
	}

	public void setNodeId(String nodeId){
		this.nodeId = nodeId;
	}

	public String getOffset(){
		return offset;
	}

	public void setOffset(String offset){
		this.offset = offset;
	}

	public String getWfStateName(){
		return wfStateName;
	}

	public void setWfStateName(String wfStateName){
		this.wfStateName = wfStateName;
	}

	public String doExecute(){
		// TCJLODO Auto-generated method stub
		String resStr = "";
		String resFlag = "false";
		try {
			resStr = WFGeneral.queryWFState(nodeId, wfStateName, offset, actionName);
			resFlag = "true";
		} catch (Exception ex) {
			resStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(resFlag, resStr);
		return this.SUCCESS;
	}

}
