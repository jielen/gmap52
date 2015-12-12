package com.anyi.gp.workflow.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class GetExecutorsByRelationAction extends AjaxAction implements
  ServletRequestAware {
  /**
   * @guohui
   */
  private static final long serialVersionUID = 1L;

  private String data;
  private String sWfData;
  private String entityName;
  private String action;
  private String orgPosiCode;
  private String svUserID;
  private String svCoCode;
  private String svOrgCode;
  private String svPoCode;
  private String svNd;
  private ServiceFacade sf;
  
  private HttpServletRequest servletRequest = null;


  public String getSvCoCode(){
		return svCoCode;
	}

	public void setSvCoCode(String svCoCode){
		this.svCoCode = svCoCode;
	}

	public String getSvNd(){
		return svNd;
	}

	public void setSvNd(String svNd){
		this.svNd = svNd;
	}

	public String getSvOrgCode(){
		return svOrgCode;
	}

	public void setSvOrgCode(String svOrgCode){
		this.svOrgCode = svOrgCode;
	}

	public String getSvPoCode(){
		return svPoCode;
	}

	public void setSvPoCode(String svPoCode){
		this.svPoCode = svPoCode;
	}

	public String getSvUserID(){
		return svUserID;
	}

	public void setSvUserID(String svUserID){
		this.svUserID = svUserID;
	}

	public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
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

  public String getOrgPosiCode() {
    return orgPosiCode;
  }

  public void setOrgPosiCode(String orgPosiCode) {
    this.orgPosiCode = orgPosiCode;
  }

  public String getSWfData() {
    return sWfData;
  }

  public void setSWfData(String wfData) {
    sWfData = wfData;
  }
  
  public void setServletRequest(HttpServletRequest request) {
    // TCJLODO Auto-generated method stub
    this.servletRequest = request;
    HttpSession session = this.servletRequest.getSession();
    svUserID = (String)SessionUtils.getAttribute(session, "svUserID");
    svCoCode = (String)SessionUtils.getAttribute(session, "svCoCode");
    svOrgCode = (String)SessionUtils.getAttribute(session, "svOrgCode");
    svPoCode = (String)SessionUtils.getAttribute(session, "svPoCode");
    svNd = (String)SessionUtils.getAttribute(session, "svNd");
  }

  public String doExecute() {
    // TCJLODO Auto-generated method stub
    String dataStr = "";
    String flag = "false";
    try {
      dataStr = sf.getExecutorsByRelation(data, sWfData, entityName, svUserID,
        svCoCode, svOrgCode, svPoCode, svNd, action, orgPosiCode);
      flag = "true";
    } catch (Exception ex) {
      dataStr = ex.getMessage();
    }
    this.resultstring = this.wrapResultStr(flag, dataStr);
    return SUCCESS;
  }

  public ServiceFacade getSf() {
    return sf;
  }

  public void setSf(ServiceFacade sf) {
    this.sf = sf;
  }

}
