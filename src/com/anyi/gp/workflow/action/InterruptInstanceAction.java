package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class InterruptInstanceAction extends AjaxAction implements ServletRequestAware{
  private String instanceId;
  private String userId;
  private String comment;
  private HttpServletRequest servletRequest = null;
  private ServiceFacade sf; 
  
  
	public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceid) {
    this.instanceId = instanceid;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String svUserID) {
    this.userId = svUserID;
  }


  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String doExecute() throws Exception{
		// TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    try {
      sf.interruptInstance(instanceId, userId, comment);
      resultStr = "success";
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
	}

  public void setServletRequest(HttpServletRequest request) {
    // TCJLODO Auto-generated method stub
    this.servletRequest = request;
    this.userId = SessionUtils.getAttribute(request, "svUserID");
  }

}
