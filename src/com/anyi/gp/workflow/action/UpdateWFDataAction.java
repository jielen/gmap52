package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.workflow.WFGeneral;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class UpdateWFDataAction extends AjaxAction implements ServletRequestAware{
	private String wfdata;
	private String svUserID;
	
	private HttpServletRequest servletRequest = null;

	public String getWfdata(){
		return wfdata;
	}

	public void setWfdata(String wfdata){
		this.wfdata = wfdata;
	}
	
	public String getSvUserID(){
		return svUserID;
	}

	public void setSvUserID(String svUserID){
		this.svUserID = svUserID;
	}

	public void setServletRequest(HttpServletRequest request){
		// TCJLODO Auto-generated method stub
		servletRequest = request;
		HttpSession session = servletRequest.getSession();
		svUserID = (String)SessionUtils.getAttribute(session, "svUserID");
	}


	public String doExecute() throws Exception{
		// TCJLODO Auto-generated method stub
		String flag = "false";
		String dataStr = "";
		try {
			WFGeneral.updateWFData(svUserID, wfdata);
		} catch (Exception ex) {
			dataStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(flag, dataStr);
		return this.SUCCESS;
	}
}
