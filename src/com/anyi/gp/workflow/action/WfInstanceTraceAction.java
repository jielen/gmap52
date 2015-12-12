package com.anyi.gp.workflow.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Delta;
import com.anyi.gp.workflow.WFWorkList;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class WfInstanceTraceAction extends ActionSupport implements ServletRequestAware {
	private String componame;
	private String instanceid;
	private HttpServletRequest servletRequest = null;
	
	public String getComponame(){
		return componame;
	}

	public void setComponame(String componame){
		this.componame = componame;
	}

	public String getInstanceid(){
		return instanceid;
	}

	public void setInstanceid(String instanceid){
		this.instanceid = instanceid;
	}
	
	public void setServletRequest(HttpServletRequest request){
		// TCJLODO Auto-generated method stub
		servletRequest = request;
	}

	public String execute() {
		String result = this.SUCCESS;
		try {
      WFWorkList worklist = new WFWorkList();
      Delta delta = worklist.getProcessInstanceDeltaById(componame,Integer.parseInt(instanceid));
      servletRequest.setAttribute("model", delta);
		} catch (Exception ex) {
			result = this.ERROR;
		}
		return result;
	}

}
