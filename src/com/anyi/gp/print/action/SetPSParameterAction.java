package com.anyi.gp.print.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.print.service.PrintSetService;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class SetPSParameterAction extends AjaxAction implements ServletRequestAware {

  private static final long serialVersionUID = 1L;
  private PrintSetService printParameter;
  private String parameter;
  public String getParameter() {
    return parameter;
  }
  public void setParameter(String parameter) {
    this.parameter = parameter;
  }
  public PrintSetService getPrintParameter() {
    return printParameter;
  }
  public void setPrintParameter(PrintSetService printParameter) {
    this.printParameter = printParameter;
  }
  public String doExecute() throws Exception {
    this.getPrintParameter().setPrintSetInfo(parameter);
    return SUCCESS;
  }
  public void setServletRequest(HttpServletRequest request) {
    /**
     * 从页面获取参数并初始化
     */
    parameter = request.getParameter("parameter");
  }
}
