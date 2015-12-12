package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class ReworkAction extends AjaxAction {

  /**
   * 
   * @author guohui
   */
  private static final long serialVersionUID = 1L;

  private String instanceId;
  private String userId;
  private String comment;
  private String strBnData;
  private ServiceFacade sf;

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getStrBnData(){
		return strBnData;
	}

	public void setStrBnData(String strBnData){
		this.strBnData = strBnData;
	}

	public String doExecute() {
    // TCJLODO Auto-generated method stub
    String result = "true";
    String resultStr = "";
    try {
      resultStr = sf.rework(instanceId, userId, comment, strBnData);
    } catch (Exception ex) {
      result = "false";
      resultStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(result, resultStr);
    return SUCCESS;
  }

}
