package com.kingdrive.workflow.model;

public class StaffPositionInfo implements java.io.Serializable {
  private String orgPositionId = null;

  private boolean orgPositionIdModifyFlag;

  private String companyId = null;

  private boolean companyIdModifyFlag;

  private String companyName = null;

  private boolean companyNameModifyFlag;

  private String organizationId = null;

  private boolean organizationIdModifyFlag;

  private String organizationName = null;

  private boolean organizationNameModifyFlag;

  private String positionId = null;

  private boolean positionIdModifyFlag;

  private String positionName = null;

  private boolean positionNameModifyFlag;

  private String staffId = null;

  private boolean staffIdModifyFlag;

  private String staffName = null;

  private boolean staffNameModifyFlag;

  private String action = "";

  public StaffPositionInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.orgPositionIdModifyFlag = false;
    this.companyIdModifyFlag = false;
    this.companyNameModifyFlag = false;
    this.organizationIdModifyFlag = false;
    this.organizationNameModifyFlag = false;
    this.positionIdModifyFlag = false;
    this.positionNameModifyFlag = false;
    this.staffIdModifyFlag = false;
    this.staffNameModifyFlag = false;
  }

  public void setOrgPositionId(String orgPositionId) {
    this.orgPositionId = orgPositionId;
    this.orgPositionIdModifyFlag = true;
  }

  public String getOrgPositionId() {
    return this.orgPositionId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
    this.companyIdModifyFlag = true;
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
    this.companyNameModifyFlag = true;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
    this.organizationIdModifyFlag = true;
  }

  public String getOrganizationId() {
    return this.organizationId;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
    this.organizationNameModifyFlag = true;
  }

  public String getOrganizationName() {
    return this.organizationName;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
    this.positionIdModifyFlag = true;
  }

  public String getPositionId() {
    return this.positionId;
  }

  public void setPositionName(String positionName) {
    this.positionName = positionName;
    this.positionNameModifyFlag = true;
  }

  public String getPositionName() {
    return this.positionName;
  }

  public void setStaffId(String staffId) {
    this.staffId = staffId;
    this.staffIdModifyFlag = true;
  }

  public String getStaffId() {
    return this.staffId;
  }

  public void setStaffName(String staffName) {
    this.staffName = staffName;
    this.staffNameModifyFlag = true;
  }

  public String getStaffName() {
    return this.staffName;
  }

  public boolean getOrgPositionIdModifyFlag() {
    return this.orgPositionIdModifyFlag;
  }

  public boolean getCompanyIdModifyFlag() {
    return this.companyIdModifyFlag;
  }

  public boolean getCompanyNameModifyFlag() {
    return this.companyNameModifyFlag;
  }

  public boolean getOrganizationIdModifyFlag() {
    return this.organizationIdModifyFlag;
  }

  public boolean getOrganizationNameModifyFlag() {
    return this.organizationNameModifyFlag;
  }

  public boolean getPositionIdModifyFlag() {
    return this.positionIdModifyFlag;
  }

  public boolean getPositionNameModifyFlag() {
    return this.positionNameModifyFlag;
  }

  public boolean getStaffIdModifyFlag() {
    return this.staffIdModifyFlag;
  }

  public boolean getStaffNameModifyFlag() {
    return this.staffNameModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
