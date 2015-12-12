package com.anyi.gp.workflow.action;

import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.core.action.AjaxAction;

public class GetNodeExecutorBySourceAction extends AjaxAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7864955092186098306L;
	
	private String templateId;
	private String nodeId;
	private String action;
	private String nd;
	private WorkflowService ws;
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getNd() {
		return nd;
	}
	
	public void setNd(String nd) {
		this.nd = nd;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getTemplateId() {
		return templateId;
	}
	
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public WorkflowService getWs() {
		return ws;
	}
	
	public void setWs(WorkflowService ws) {
		this.ws = ws;
	}

	public String doExecute() throws Exception {
		// TCJLODO Auto-generated method stub
		try {
			this.resultstring = ws.getNodeExecutorBySource(templateId, nodeId, action, nd);
		} catch (Exception ex) {
			this.resultstring = ex.getMessage();
		}
		return this.SUCCESS;
	}
	

}
