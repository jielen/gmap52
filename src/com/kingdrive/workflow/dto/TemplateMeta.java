package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class TemplateMeta implements Serializable {

  private int templateId;

  private String name;

  private String description;

  private String version;

  private String startTime;

  private String expireTime;

  private String createTime;

  private String createStaffId;

  private String isActive;

  private String createStaffName;

  private String templateType;

  public TemplateMeta() {
  }

  public int getTemplateId() {
    return templateId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getExpireTime() {
    return expireTime;
  }

  public String getCreateTime() {
    return createTime;
  }

  public String getCreateStaffId() {
    return createStaffId;
  }

  public String getIsActive() {
    return isActive;
  }

  public String getCreateStaffName() {
    return createStaffName;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public void setExpireTime(String expireTime) {
    this.expireTime = expireTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public void setCreateStaffId(String createStaffId) {
    this.createStaffId = createStaffId;
  }

  public void setIsActive(String isActive) {
    this.isActive = isActive;
  }

  public void setCreateStaffName(String createStaffName) {
    this.createStaffName = createStaffName;
  }

  public void setTemplateType(String templateType) {
    this.templateType = templateType;
  }

  public String getTemplateType() {
    return templateType;
  }
}
