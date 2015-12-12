package com.anyi.gp.print.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.template.TemplateDistribution;

public class PrnTempDistAction extends AjaxAction {

	private static final long serialVersionUID = 1L;
	private String tplcode;
	private String isredo;
	private String dbtpl;
	private String userId;
	private String cocode;

	public String getCocode() {
		return cocode;
	}
	public void setCocode(String cocode) {
		this.cocode = cocode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDbtpl() {
		return dbtpl;
	}
	public void setDbtpl(String dbtpl) {
		this.dbtpl = dbtpl;
	}
	public String getIsredo() {
		return isredo;
	}
	public void setIsredo(String isredo) {
		this.isredo = isredo;
	}
	public String getTplcode() {
		return tplcode;
	}
	public void setTplcode(String tplcode) {
		this.tplcode = tplcode;
	}
	public String doExecute() throws Exception {
		boolean result = TemplateDistribution.distributeTemplate(userId, cocode, tplcode, isredo, dbtpl);
		this.resultstring = "<info success='";
		if(result){
			this.resultstring += "true";
		}else{
			this.resultstring += "false";
		}
		this.resultstring += "'></info>";
		return SUCCESS;
	}

}
