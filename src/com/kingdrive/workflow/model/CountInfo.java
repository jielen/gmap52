package com.kingdrive.workflow.model;

public class CountInfo implements java.io.Serializable {
  private Integer count = null;

  private boolean countModifyFlag;

  private String action = "";

  public CountInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.countModifyFlag = false;
  }

  public void setCount(int count) {
    this.count = new Integer(count);
    this.countModifyFlag = true;
  }

  public void setCount(Integer count) {
    this.count = count;
    this.countModifyFlag = true;
  }

  public Integer getCount() {
    return this.count;
  }

  public boolean getCountModifyFlag() {
    return this.countModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
