package com.anyi.gp.bean;

import java.sql.Date;

public class ValBean {

  private String valSetId;
  
  private String valId;
  
  private String val;
  
  private int ordIndex;
  
  private Date lstDate;
  
  private boolean system;

  public ValBean(){
    
  }
  
  public Date getLstDate() {
    return lstDate;
  }

  public void setLstDate(Date lstDate) {
    this.lstDate = lstDate;
  }

  public int getOrdIndex() {
    return ordIndex;
  }

  public void setOrdIndex(int ordIndex) {
    this.ordIndex = ordIndex;
  }

  public boolean isSystem() {
    return system;
  }

  public void setSystem(boolean system) {
    this.system = system;
  }

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }

  public String getValId() {
    return valId;
  }

  public void setValId(String valId) {
    this.valId = valId;
  }

  public String getValSetId() {
    return valSetId;
  }

  public void setValSetId(String valSetId) {
    this.valSetId = valSetId;
  }
  
}
