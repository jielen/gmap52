package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class WfNewCommitAction extends AjaxAction implements ServletRequestAware {

  /**
   * 
   * @author guohui
   */
  private static final long serialVersionUID = 1L;

  private String data;
  private String componame;
  private String user;
  private String wfdata;
  private ServiceFacade sf;
  private HttpServletRequest request = null;

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getComponame() {
    return componame;
  }

  public void setComponame(String entityName) {
    this.componame = entityName;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getWfdata() {
    return wfdata;
  }

  public void setWfdata(String wfdata) {
    this.wfdata = wfdata;
  }

  public void setServletRequest(HttpServletRequest request) {
    // TCJLODO Auto-generated method stub
    this.request = request;
    HttpSession session = request.getSession();
    this.user = (String)SessionUtils.getAttribute(session, "svUserID");
  }

  public String doExecute() {
    // TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    try {
      resultStr = sf.wfNewCommit(data, componame, user, wfdata);
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }

}
