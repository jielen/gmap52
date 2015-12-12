package com.kingdrive.workflow.model;

public class StaffModel implements java.io.Serializable {
  private String staffId = null;

  private boolean staffIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String pwd = null;

  private boolean pwdModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String email = null;

  private boolean emailModifyFlag;

  private String status = null;

  private boolean statusModifyFlag;

  private String action = "";

  public StaffModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.staffIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.pwdModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.emailModifyFlag = false;
    this.statusModifyFlag = false;
  }

  public void setStaffId(String staffId) {
    this.staffId = staffId;
    this.staffIdModifyFlag = true;
  }

  public String getStaffId() {
    return this.staffId;
  }

  public void setName(String name) {
    this.name = name;
    this.nameModifyFlag = true;
  }

  public String getName() {
    return this.name;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
    this.pwdModifyFlag = true;
  }

  public String getPwd() {
    return this.pwd;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
  }

  public void setEmail(String email) {
    this.email = email;
    this.emailModifyFlag = true;
  }

  public String getEmail() {
    return this.email;
  }

  public void setStatus(String status) {
    this.status = status;
    this.statusModifyFlag = true;
  }

  public String getStatus() {
    return this.status;
  }

  public boolean getStaffIdModifyFlag() {
    return this.staffIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getPwdModifyFlag() {
    return this.pwdModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getEmailModifyFlag() {
    return this.emailModifyFlag;
  }

  public boolean getStatusModifyFlag() {
    return this.statusModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
