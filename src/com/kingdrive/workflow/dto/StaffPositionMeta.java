package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class StaffPositionMeta implements Serializable {
  private String staffId;

  private String staffName;

  private String orgPositionId;

  private String organizationId;

  private String companyId;

  private String companyName;

  private String organizationName;

  private String positionId;

  private String positionName;

  public StaffPositionMeta() {
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof StaffPositionMeta)) {
      return false;
    }
    StaffPositionMeta o = (StaffPositionMeta) obj;
    return o.staffId == staffId && o.positionId == positionId
        && o.organizationId == organizationId;
  }

  public String getOrganizationId() {
    return organizationId;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public String getPositionId() {
    return positionId;
  }

  public String getPositionName() {
    return positionName;
  }

  public String getStaffId() {
    return staffId;
  }

  public void setOrganizationId(String organizationId) {
    this.organizationId = organizationId;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
  }

  public void setPositionName(String positionName) {
    this.positionName = positionName;
  }

  public void setStaffId(String staffId) {
    this.staffId = staffId;
  }

  public String getStaffName() {
    return staffName;
  }

  public void setStaffName(String staffName) {
    this.staffName = staffName;
  }

  public String getOrgPositionId() {
    return orgPositionId;
  }

  public void setOrgPositionId(String orgPositionId) {
    this.orgPositionId = orgPositionId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }
}
