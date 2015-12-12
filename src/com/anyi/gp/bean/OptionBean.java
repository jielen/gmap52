package com.anyi.gp.bean;

public class OptionBean {
  
  private String optId;
  
  private String coCode;
  
  private String compoId;
  
  private String transType;
  
  private String optVal;
  
  private boolean systemOpt;

  public String getCoCode() {
    return coCode;
  }

  public void setCoCode(String coCode) {
    this.coCode = coCode;
  }

  public String getCompoId() {
    return compoId;
  }

  public void setCompoId(String compoId) {
    this.compoId = compoId;
  }

  public String getOptId() {
    return optId;
  }

  public void setOptId(String optId) {
    this.optId = optId;
  }

  public String getOptVal() {
    return optVal;
  }

  public void setOptVal(String optVal) {
    this.optVal = optVal;
  }

  public boolean isSystemOpt() {
    return systemOpt;
  }

  public void setSystemOpt(boolean systemOpt) {
    this.systemOpt = systemOpt;
  }

  public String getTransType() {
    return transType;
  }

  public void setTransType(String transType) {
    this.transType = transType;
  }
  
}
