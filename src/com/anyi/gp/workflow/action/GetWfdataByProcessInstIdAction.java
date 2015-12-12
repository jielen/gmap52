package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.workflow.CZWFService;

import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class GetWfdataByProcessInstIdAction extends AjaxAction implements ServletRequestAware{

  /**
   * @author guohui
   */
  private static final long serialVersionUID = 1L;

  private String user;
  private String templateId;
  private String instanceId;
  private HttpServletRequest request = null;
  
  
  public void setServletRequest(HttpServletRequest servletRequest) {
    // TCJLODO Auto-generated method stub
    this.request = servletRequest;
  }
  
  
  public String getInstanceId() {
    return instanceId;
  }


  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }


  public String getTemplateId() {
    return templateId;
  }


  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }


  public String getUser() {
    return user;
  }


  public void setUser(String user) {
    this.user = user;
  }


  public String doExecute(){
    // TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    CZWFService czwfs = new CZWFService();
    try {
      resultStr = czwfs.getWfdataByProcessInstId(user, templateId, instanceId, request);
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }


}
