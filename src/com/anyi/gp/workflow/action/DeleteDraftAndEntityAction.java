package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class DeleteDraftAndEntityAction extends AjaxAction {
  /**
   * @author guohui
   */
  private static final long serialVersionUID = 8510613012062105736L;


  private String compoId;

  private String instanceId;

  private ServiceFacade sf;

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getCompoId() {
    return compoId;
  }

  public void setCompoId(String compoId) {
    this.compoId = compoId;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String doExecute() {
    String result = "true";
    String resultStr = "";
    try {
      sf.deleteDraft(compoId,instanceId);
      resultStr = "É¾³ý³É¹¦";
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }

}
