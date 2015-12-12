package com.anyi.gp.core.action;

import com.anyi.gp.license.RegisterTools;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

public class CreateKeyAction extends ActionSupport {

  private static final long serialVersionUID = 3732613038280260690L;

  private String sn;
  
  private String companyName;
  
  private String address;
  
  private String postcode;
  
  private String linkman;
  
  private String linkTel;
  
  private String agentName;
 
  private String coCount;
  
  private String accCount;
  
  private String products;
  
  private String encodeType;
  
  public void setAddress(String address) {
    this.address = address;
  }

  public void setAgentName(String agentName) {
    this.agentName = agentName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public void setLinkman(String linkman) {
    this.linkman = linkman;
  }

  public void setLinkTel(String linkTel) {
    this.linkTel = linkTel;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public void setAccCount(String accCount) {
    this.accCount = accCount;
  }

  public void setCoCount(String coCount) {
    this.coCount = coCount;
  }

  public void setProducts(String products) {
    this.products = products;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public void setEncodeType(String encodeType) {
	this.encodeType = encodeType;
}

public String execute() throws Exception{
    StringBuffer registerString = new StringBuffer();
    registerString.append("sn[" + sn + "];");
    registerString.append("companyName[" + companyName + "];");
    registerString.append("address[" + address + "];");
    registerString.append("postcode[" + postcode + "];");
    registerString.append("linkman[" + linkman + "];");
    registerString.append("linkTel[" + linkTel + "];");
    registerString.append("agentName[" + agentName + "];");
    registerString.append("coCount[" + coCount + "];");
    registerString.append("accCount[" + accCount + "];");
    registerString.append("products[" + products + "]");
    
    String keyString = RegisterTools.getKeyString(this.encodeType);
    saveKeyToDB(RegisterTools.encodeString(keyString));
    
    keyString = RegisterTools.encodeString(registerString.toString() + ";" + keyString);
    ServletActionContext.getRequest().setAttribute("ufgov_key", keyString);
    return SUCCESS;
  }
  
  private void saveKeyToDB(String valueString){
    String sqlDeleteKey = " DELETE FROM as_info WHERE KEY =?";
    if(DAOFactory.getWhichFactory() == DAOFactory.MSSQL){
      sqlDeleteKey = " DELETE FROM as_info WHERE [KEY] =?";
    }
    String sqlInsertKey = " INSERT INTO as_info VALUES(?, ?) ";
    DBHelper.executeSQL(sqlDeleteKey, new Object[]{RegisterTools.LICENSE_KEY});
    DBHelper.executeSQL(sqlInsertKey, new Object[]{RegisterTools.LICENSE_KEY, valueString});
  }
}
