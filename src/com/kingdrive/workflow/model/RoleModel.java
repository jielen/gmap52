package com.kingdrive.workflow.model;

public class RoleModel implements java.io.Serializable {
  private String roleId = null;

  private boolean roleIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String action = "";

  public RoleModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.roleIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
    this.roleIdModifyFlag = true;
  }

  public String getRoleId() {
    return this.roleId;
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

  public boolean getRoleIdModifyFlag() {
    return this.roleIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
