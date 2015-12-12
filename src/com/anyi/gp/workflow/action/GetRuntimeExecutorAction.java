package com.anyi.gp.workflow.action;

import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.core.action.AjaxAction;

public class GetRuntimeExecutorAction extends AjaxAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4823074150582776605L;
	
	private String templateId;
	private String instanceId;
	private String nodeId;
	private String action;
	private WorkflowService ws;
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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
			this.resultstring = ws.getRuntimeExecutor(templateId, instanceId, nodeId, action);
		} catch (Exception ex) {
			this.resultstring = ex.getMessage();
		}
		return this.SUCCESS;
	}

}
