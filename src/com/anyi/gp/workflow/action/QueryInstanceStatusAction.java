package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class QueryInstanceStatusAction extends AjaxAction {
  /**
   * @author guohui
   */
  private static final long serialVersionUID = 1L;
  private String sInstanceId;
  private ServiceFacade sf;
  
  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getSInstanceId() {
    return sInstanceId;
  }

  public void setSInstanceId(String instanceId) {
    sInstanceId = instanceId;
  }

  public String doExecute() throws Exception{
    // TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    try {
      resultStr = sf.getInstanceStatus(sInstanceId);
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();;
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }

}
