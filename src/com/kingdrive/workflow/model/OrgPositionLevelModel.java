package com.kingdrive.workflow.model;

public class OrgPositionLevelModel implements java.io.Serializable {
  private String orgPositionId = null;

  private boolean orgPositionIdModifyFlag;

  private String parentId = null;

  private boolean parentIdModifyFlag;

  private String action = "";

  public OrgPositionLevelModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.orgPositionIdModifyFlag = false;
    this.parentIdModifyFlag = false;
  }

  public void setOrgPositionId(String orgPositionId) {
    this.orgPositionId = orgPositionId;
    this.orgPositionIdModifyFlag = true;
  }

  public String getOrgPositionId() {
    return this.orgPositionId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
    this.parentIdModifyFlag = true;
  }

  public String getParentId() {
    return this.parentId;
  }

  public boolean getOrgPositionIdModifyFlag() {
    return this.orgPositionIdModifyFlag;
  }

  public boolean getParentIdModifyFlag() {
    return this.parentIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
