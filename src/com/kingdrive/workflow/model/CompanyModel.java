package com.kingdrive.workflow.model;

public class CompanyModel implements java.io.Serializable {
  private String companyId = null;

  private boolean companyIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String parentId = null;

  private boolean parentIdModifyFlag;

  private String action = "";

  public CompanyModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.companyIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.parentIdModifyFlag = false;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
    this.companyIdModifyFlag = true;
  }

  public String getCompanyId() {
    return this.companyId;
  }

  public void setName(String name) {
    this.name = name;
    this.nameModifyFlag = true;
  }

  public String getName() {
    return this.name;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
    this.parentIdModifyFlag = true;
  }

  public String getParentId() {
    return this.parentId;
  }

  public boolean getCompanyIdModifyFlag() {
    return this.companyIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
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
