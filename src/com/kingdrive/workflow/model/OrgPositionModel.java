package com.kingdrive.workflow.model;

public class OrgPositionModel implements java.io.Serializable {
  private String orgPositionId = null;

  private boolean orgPositionIdModifyFlag;

  private String organizationId = null;

  private boolean organizationIdModifyFlag;

  private String positionId = null;

  private boolean positionIdModifyFlag;

  private String action = "";

  public OrgPositionModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.orgPositionIdModifyFlag = false;
    this.organizationIdModifyFlag = false;
    this.positionIdModifyFlag = false;
  }

  public void setOrgPositionId(String orgPositionId) {
    this.orgPositionId = orgPositionId;
    this.orgPositionIdModifyFlag = true;
  }

  public String getOrgPositionId() {
    return this.orgPositionId;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
    this.organizationIdModifyFlag = true;
  }

  public String getOrganizationId() {
    return this.organizationId;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
    this.positionIdModifyFlag = true;
  }

  public String getPositionId() {
    return this.positionId;
  }

  public boolean getOrgPositionIdModifyFlag() {
    return this.orgPositionIdModifyFlag;
  }

  public boolean getOrganizationIdModifyFlag() {
    return this.organizationIdModifyFlag;
  }

  public boolean getPositionIdModifyFlag() {
    return this.positionIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
