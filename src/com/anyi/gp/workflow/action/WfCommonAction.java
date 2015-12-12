package com.anyi.gp.workflow.action;

import com.anyi.gp.Delta;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.workflow.WFGeneral;

class WfCommonAction extends AjaxAction {

	private String funcname;
	private String indata;
	private String strBnData;
	
	private ServiceFacade sf;
	
	public String getFuncname(){
		return funcname;
	}

	public void setFuncname(String funcname){
		this.funcname = funcname;
	}

	public String getIndata(){
		return indata;
	}

	public void setIndata(String indata){
		this.indata = indata;
	}


	public String getStrBnData(){
		return strBnData;
	}

	public void setStrBnData(String strBnData){
		this.strBnData = strBnData;
	}

	public ServiceFacade getSf() {
		return sf;
	}

	public void setSf(ServiceFacade sf) {
		this.sf = sf;
	}

	public String doExecute() throws Exception{
		// TCJLODO Auto-generated method stub
		String resStr = "";
		String resFlag = "false";
		try {
			sf.wfCommon(funcname, indata, strBnData);
			resStr = "success";
			resFlag = "true";
		} catch (Exception ex) {
			resStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(resFlag, resStr);
		return this.SUCCESS;
	}

}
