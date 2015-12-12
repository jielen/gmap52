package com.anyi.gp.workflow.action;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;

public class CommitSimplyAction extends AjaxAction {
  private String strInstanceId;
  private String strTemplateId;
  private String strCompoId;
  private String strUserId;
  private String strWfData;
  private String strBuData;
  private ServiceFacade sf;

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getStrCompoId() {
    return strCompoId;
  }

  public void setStrCompoId(String strCompoId) {
    this.strCompoId = strCompoId;
  }

  public String getStrInstanceId() {
    return strInstanceId;
  }

  public void setStrInstanceId(String strInstanceId) {
    this.strInstanceId = strInstanceId;
  }

  public String getStrTemplateId() {
    return strTemplateId;
  }

  public void setStrTemplateId(String strTemplateId) {
    this.strTemplateId = strTemplateId;
  }

  public String getStrUserId() {
    return strUserId;
  }

  public void setStrUserId(String strUserId) {
    this.strUserId = strUserId;
  }

  public String getStrWfData() {
    return strWfData;
  }

  public void setStrWfData(String strWfData) {
    this.strWfData = strWfData;
  }

  public String getStrBuData(){
		return strBuData;
	}

	public void setStrBuData(String strBuData){
		this.strBuData = strBuData;
	}

	public String doExecute() throws Exception {
    // TCJLODO Auto-generated method stub
    String resStr = "";
    String resFlag = "false";
    try {
      resStr = sf.commitSimply(strInstanceId, strTemplateId, strCompoId, strUserId,
        strWfData,strBuData);
      if (resStr.equals("success")) {
        resFlag = "true";
      }
    } catch (Exception ex) {
      resStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(resFlag, resStr);
    return this.SUCCESS;
  }

}
