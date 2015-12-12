package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.SessionUtils;

import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class GetListPageAction extends AjaxAction implements ServletRequestAware{

  /**
   * @author guohui
   */
  private static final long serialVersionUID = 1L;

  private String componame;

  private String condition;
  
  private String svCoCode;
  
  private HttpServletRequest servletRequest = null;
  
  
  public String getSvCoCode(){
		return svCoCode;
	}

	public void setSvCoCode(String svCoCode){
		this.svCoCode = svCoCode;
	}

	public String getComponame() {
    return componame;
  }

  public void setComponame(String componame) {
    this.componame = componame;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public void setServletRequest(HttpServletRequest request) {
    // TCJLODO Auto-generated method stub
    this.servletRequest = request;
    HttpSession session = this.servletRequest.getSession();
    svCoCode = (String)SessionUtils.getAttribute(session, "svCoCode");
  }
  

  public String doExecute() {
    // TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    try {
     // DML dml = (DML) ApplusContext.getBean("dml");
      //dml.getEntities(componame, condition, svCoCode);
      resultStr = "获取成功";
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }



}
