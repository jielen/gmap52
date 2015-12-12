package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;


public class QueryCompoEnableStartedTempateAction extends AjaxAction{
	private String compoId;

	public String getCompoId(){
		return compoId;
	}

	public void setCompoId(String compoId){
		this.compoId = compoId;
	}

	public String doExecute() throws Exception{
		// TCJLODO Auto-generated method stub
		//WFClientCallFacade facade = new WFClientCallFacade();
		String dataStr = "";
		String flag = "false";
		try{
			//dataStr = facade.getCompoEnableStartedTemplate(compoId);
			flag = "true";
		}catch(Exception ex){
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}

}
