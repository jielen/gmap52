package com.kingdrive.workflow.model;

public class PositionRoleModel implements java.io.Serializable {
  private String positionId = null;

  private boolean positionIdModifyFlag;

  private String roleId = null;

  private boolean roleIdModifyFlag;

  private String action = "";

  public PositionRoleModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.positionIdModifyFlag = false;
    this.roleIdModifyFlag = false;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
    this.positionIdModifyFlag = true;
  }

  public String getPositionId() {
    return this.positionId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
    this.roleIdModifyFlag = true;
  }

  public String getRoleId() {
    return this.roleId;
  }

  public boolean getPositionIdModifyFlag() {
    return this.positionIdModifyFlag;
  }

  public boolean getRoleIdModifyFlag() {
    return this.roleIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
