package com.anyi.gp.domain.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.domain.RightService;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;


public class CopyEmpPrivAction extends AjaxAction implements ServletRequestAware {

  private static final long serialVersionUID = 1L;
  
  private String userId;
  
  private String empCodeS;
  
  private String empCodeD;
  
  private String nd;
  
  private RightService service;
  
  private HttpServletRequest servletRequest = null;

  public void setServletRequest(HttpServletRequest request) {
    // TCJLODO Auto-generated method stub
    servletRequest = request;
    HttpSession session = servletRequest.getSession();
    nd = (String)SessionUtils.getAttribute(session,"svNd");
  }

  
  public void setEmpCodeD(String empCodeD) {
    this.empCodeD = empCodeD;
  }

  public void setEmpCodeS(String empCodeS) {
    this.empCodeS = empCodeS;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setNd(String nd) {
    this.nd = nd;
  }
  
  public void setService(RightService service) {
    this.service = service;
  }
  
  public String doExecute() throws Exception {
    boolean result = service.empCopy(empCodeS, empCodeD, userId, nd);
    if(result){
      resultstring = this.wrapResultStr("true", "权限复制成功！");
    }
    else{
      resultstring = this.wrapResultStr("false", "权限复制失败！");
    }
    return SUCCESS;
  }

}
