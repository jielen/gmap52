package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class QueryDefaultActionNameAction extends AjaxAction {
	private String templateId;

	private String nodeId;

	private ServiceFacade sf;

	public ServiceFacade getSf() {
		return sf;
	}

	public void setSf(ServiceFacade sf) {
		this.sf = sf;
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

	public String doExecute() throws Exception {
		// TCJLODO Auto-generated method stub
		String dataStr = "";
		String flag = "false";
		try {
			dataStr = sf.getDefaultActionName(templateId, nodeId);
			flag = "true";
		} catch (Exception ex) {
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}

}
