package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class DeleteWithWorkflowAction extends AjaxAction {

  /**
   * @author guohui
   */
  private static final long serialVersionUID = 1L;

  private String user;

  private String sWfData;

  private ServiceFacade sf;

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getSWfData() {
    return sWfData;
  }

  public void setSWfData(String wfData) {
    sWfData = wfData;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String doExecute() {
    // TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    try {
      //sf.deleteWithWorkflow(user, sWfData);
      resultStr = "É¾³ý³É¹¦";
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }

}
