package com.kingdrive.workflow.model;

public class TemplateModel implements java.io.Serializable {
  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String templateType = null;

  private boolean templateTypeModifyFlag;

  private String version = null;

  private boolean versionModifyFlag;

  private String startTime = null;

  private boolean startTimeModifyFlag;

  private String expireTime = null;

  private boolean expireTimeModifyFlag;

  private String createTime = null;

  private boolean createTimeModifyFlag;

  private String createStaffId = null;

  private boolean createStaffIdModifyFlag;

  private String isActive = null;

  private boolean isActiveModifyFlag;

  private String action = "";

  public TemplateModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.templateIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.templateTypeModifyFlag = false;
    this.versionModifyFlag = false;
    this.startTimeModifyFlag = false;
    this.expireTimeModifyFlag = false;
    this.createTimeModifyFlag = false;
    this.createStaffIdModifyFlag = false;
    this.isActiveModifyFlag = false;
  }

  public void setTemplateId(int templateId) {
    this.templateId = new Integer(templateId);
    this.templateIdModifyFlag = true;
  }

  public void setTemplateId(Integer templateId) {
    this.templateId = templateId;
    this.templateIdModifyFlag = true;
  }

  public Integer getTemplateId() {
    return this.templateId;
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

  public void setTemplateType(String templateType) {
    this.templateType = templateType;
    this.templateTypeModifyFlag = true;
  }

  public String getTemplateType() {
    return this.templateType;
  }

  public void setVersion(String version) {
    this.version = version;
    this.versionModifyFlag = true;
  }

  public String getVersion() {
    return this.version;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
    this.startTimeModifyFlag = true;
  }

  public String getStartTime() {
    return this.startTime;
  }

  public void setExpireTime(String expireTime) {
    this.expireTime = expireTime;
    this.expireTimeModifyFlag = true;
  }

  public String getExpireTime() {
    return this.expireTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
    this.createTimeModifyFlag = true;
  }

  public String getCreateTime() {
    return this.createTime;
  }

  public void setCreateStaffId(String createStaffId) {
    this.createStaffId = createStaffId;
    this.createStaffIdModifyFlag = true;
  }

  public String getCreateStaffId() {
    return this.createStaffId;
  }

  public void setIsActive(String isActive) {
    this.isActive = isActive;
    this.isActiveModifyFlag = true;
  }

  public String getIsActive() {
    return this.isActive;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getTemplateTypeModifyFlag() {
    return this.templateTypeModifyFlag;
  }

  public boolean getVersionModifyFlag() {
    return this.versionModifyFlag;
  }

  public boolean getStartTimeModifyFlag() {
    return this.startTimeModifyFlag;
  }

  public boolean getExpireTimeModifyFlag() {
    return this.expireTimeModifyFlag;
  }

  public boolean getCreateTimeModifyFlag() {
    return this.createTimeModifyFlag;
  }

  public boolean getCreateStaffIdModifyFlag() {
    return this.createStaffIdModifyFlag;
  }

  public boolean getIsActiveModifyFlag() {
    return this.isActiveModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
