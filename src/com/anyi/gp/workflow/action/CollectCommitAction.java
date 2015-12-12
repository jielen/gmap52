package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class CollectCommitAction extends AjaxAction implements ServletRequestAware {
  /**
   * @guohui
   */
  private static final long serialVersionUID = 1L;

  private String wfData;

  private String entityName;

  private String detailTaskIds;

  private String svUserID;

  private ServiceFacade sf;

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

  public String getSvUserID() {
    return svUserID;
  }

  public void setSvUserID(String svUserID) {
    this.svUserID = svUserID;
  }

  public String getDetailTaskIds() {
    return detailTaskIds;
  }

  public void setDetailTaskIds(String detailTaskIds) {
    this.detailTaskIds = detailTaskIds;
  }

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public String getWfData() {
    return wfData;
  }

  public void setWfData(String wfData) {
    this.wfData = wfData;
  }

  private HttpServletRequest servletRequest = null;

  public void setServletRequest(HttpServletRequest request) {
    // TCJLODO Auto-generated method stub
    servletRequest = request;
    HttpSession session = servletRequest.getSession();
    svUserID = (String)SessionUtils.getAttribute(session,"svUserID");
  }

  public String doExecute() throws Exception {
    // TCJLODO Auto-generated method stub
    String dataStr = "";
    String flag = "false";
    try {
      //dataStr = sf.collectCommit(wfData, entityName, detailTaskIds, svUserID);
      flag = "true";
    } catch (Exception ex) {
      dataStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(flag, dataStr);
    return null;
  }

}
