package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class CommitAction extends AjaxAction implements ServletRequestAware{

	/**
	 * @author guohui
	 */
	private static final long serialVersionUID = 1L;
	private String data;
	private String componame;
	private String wfdata;
	private String user;
	private HttpServletRequest servletRequest = null;
	private ServiceFacade sf;

	public ServiceFacade getSf(){
		return sf;
	}

	public void setSf(ServiceFacade sf){
		this.sf = sf;
	}

	public String getUser(){
		return user;
	}

	public void setUser(String user){
		this.user = user;
	}

	public String getComponame(){
		return componame;
	}

	public void setComponame(String componame){
		this.componame = componame;
	}

	public String getData(){
		return data;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getWfdata(){
		return wfdata;
	}

	public void setWfdata(String wfdata){
		this.wfdata = wfdata;
	}

	public String doExecute(){
		// TCJLODO Auto-generated method stub
		String result = "true";
		String resultStr = "";
		try{
			sf.commit(data, componame, wfdata);
			resultStr = "success";
		}catch(Exception ex){
			result = "false";
			resultStr = ex.getMessage();
		}
		this.resultstring = this.wrapResultStr(result, resultStr);
		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest request){
		// TCJLODO Auto-generated method stub
		this.servletRequest = request;
		HttpSession session = this.servletRequest.getSession();
		String user = (String)SessionUtils.getAttribute(session, "svUserID");
		this.setUser(user);
	}

}
