package com.anyi.gp.core.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.components.Page;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.xwork.ActionSupport;

public class PageDispatcherAction extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
	
	private static String GET_LIST_PAGE = "getlistpage";
	
	private static String GET_EDIT_PAGE = "geteditpage";
	
	private String function;
	
	private String condition = "";
	
	private HttpServletRequest servletRequest = null;

	public String getFunction(){
		return function;
	}

	public void setFunction(String function){
		this.function = function;
	}
	
	public String getCondition(){
		return condition;
	}

	public void setCondition(String condition){
		this.condition = condition;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		servletRequest = request;
	}

	public String execute() {
		String result = "";
		if (function != null) {
			if(GET_LIST_PAGE.equals(function))
				result = "list";
			else if(GET_EDIT_PAGE.equals(function))
				result = "edit";
			else
				result = function;
		} 
		else {
			result = ERROR;
		}
		if (condition.indexOf("userid") < 0) {
			if (condition.length() == 0) {
				condition = "userid=" + SessionUtils.getAttribute(servletRequest, "svUserID");
			} else {
				condition += ";userid=" + SessionUtils.getAttribute(servletRequest, "svUserID");
			}
		}//加入用户参数
		String extCondition = (String)servletRequest.getAttribute(Page.TAG_INTERFACE_CONDITION);
		if (extCondition == null || extCondition.length() == 0) {
			extCondition = condition;
		} else {
			extCondition += ";" + condition;
		}//和其他接口传入的condition条件拼接
		this.servletRequest.setAttribute(Page.TAG_INTERFACE_CONDITION, extCondition);
		return result;
	}
}
