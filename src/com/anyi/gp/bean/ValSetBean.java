package com.anyi.gp.bean;

import java.sql.Date;
import java.util.List;

public class ValSetBean {

  private String valSetId;
  
  private String valSetName;
  
  private String valSql;
  
  private Date lstDate;
  
  private boolean system;

  private List valBeans;
  
  public ValSetBean() {
  }

  public Date getLstDate() {
    return lstDate;
  }

  public void setLstDate(Date lstDate) {
    this.lstDate = lstDate;
  }

  public boolean isSystem() {
    return system;
  }

  public void setSystem(boolean system) {
    this.system = system;
  }

  public String getValSetId() {
    return valSetId;
  }

  public void setValSetId(String valSetId) {
    this.valSetId = valSetId;
  }

  public String getValSetName() {
    return valSetName;
  }

  public void setValSetName(String valSetName) {
    this.valSetName = valSetName;
  }

  public String getValSql() {
    return valSql;
  }

  public void setValSql(String valSql) {
    this.valSql = valSql;
  }

  public List getValBeans() {
    return valBeans;
  }

  public void setValBeans(List valBeans) {
    this.valBeans = valBeans;
  }

  public void addValBean(ValBean val){
    this.valBeans.add(val);
  }
}
