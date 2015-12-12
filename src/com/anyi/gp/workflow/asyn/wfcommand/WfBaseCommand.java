package com.anyi.gp.workflow.asyn.wfcommand;

import javax.jms.MapMessage;
import javax.jms.Message;

import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.workflow.util.WFException;
import com.anyi.gp.workflow.util.WFUtil;

public abstract class WfBaseCommand implements WfCommand {
	private WorkflowService workflowService;
	private String instanceid;
	private String entityName;
	

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(String instanceid) {
		this.instanceid = instanceid;
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	public abstract void doExecute(MapMessage message) throws Exception;
	
	public void execute(Message mess) throws WFException {
		try {
			MapMessage mes = (MapMessage)mess;
			doExecute(mes);
			WFUtil.setASWfState(instanceid, entityName, "0");
		} catch (Exception ex) {
			throw new WFException(ex.getMessage());
		}
	}
}
