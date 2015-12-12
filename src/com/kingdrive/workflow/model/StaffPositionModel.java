package com.kingdrive.workflow.model;

public class StaffPositionModel implements java.io.Serializable {
  private String orgPositionId = null;

  private boolean orgPositionIdModifyFlag;

  private String staffId = null;

  private boolean staffIdModifyFlag;

  private String action = "";

  public StaffPositionModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.orgPositionIdModifyFlag = false;
    this.staffIdModifyFlag = false;
  }

  public void setOrgPositionId(String orgPositionId) {
    this.orgPositionId = orgPositionId;
    this.orgPositionIdModifyFlag = true;
  }

  public String getOrgPositionId() {
    return this.orgPositionId;
  }

  public void setStaffId(String staffId) {
    this.staffId = staffId;
    this.staffIdModifyFlag = true;
  }

  public String getStaffId() {
    return this.staffId;
  }

  public boolean getOrgPositionIdModifyFlag() {
    return this.orgPositionIdModifyFlag;
  }

  public boolean getStaffIdModifyFlag() {
    return this.staffIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
