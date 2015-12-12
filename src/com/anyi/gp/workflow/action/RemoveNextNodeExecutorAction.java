package com.anyi.gp.workflow.action;

import com.anyi.gp.TableData;
import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.DataTools;

public class RemoveNextNodeExecutorAction extends AjaxAction {
	private String instanceId;
	private String userId;
	private String strWfData;
	private String comment;

	private WorkflowService ws;

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStrWfData() {
		return strWfData;
	}

	public void setStrWfData(String strWfData) {
		this.strWfData = strWfData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public WorkflowService getWs() {
		return ws;
	}

	public void setWs(WorkflowService ws) {
		this.ws = ws;
	}

	public String doExecute() throws Exception {
		// TCJLODO Auto-generated method stub
		String dataStr = "";
		String flag = "true";
		try {
			TableData wfData = DataTools.parseData(strWfData);
			dataStr = ws.removeNextNodeExecutor(instanceId, userId, wfData, comment);
		} catch (Exception ex) {
			flag = "false";
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}

}
