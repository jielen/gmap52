package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class InsertWithWorkflowAction extends AjaxAction {

  /**
   * @author guohui
   */
  private static final long serialVersionUID = 1L;

  private String data;

  private String entityName;

  private String user;

  private String sWfData;

  private ServiceFacade sf;

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

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
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
      //resultStr = sf.insertWithWorkflow(data, entityName, user, sWfData);
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }

}
