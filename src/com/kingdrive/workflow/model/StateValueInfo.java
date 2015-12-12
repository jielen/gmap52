package com.kingdrive.workflow.model;

public class StateValueInfo implements java.io.Serializable {
  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer stateId = null;

  private boolean stateIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private Integer stateValueId = null;

  private boolean stateValueIdModifyFlag;

  private String value = null;

  private boolean valueModifyFlag;

  private String action = "";

  public StateValueInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.templateIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.stateIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.stateValueIdModifyFlag = false;
    this.valueModifyFlag = false;
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

  public void setInstanceId(int instanceId) {
    this.instanceId = new Integer(instanceId);
    this.instanceIdModifyFlag = true;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
    this.instanceIdModifyFlag = true;
  }

  public Integer getInstanceId() {
    return this.instanceId;
  }

  public void setStateId(int stateId) {
    this.stateId = new Integer(stateId);
    this.stateIdModifyFlag = true;
  }

  public void setStateId(Integer stateId) {
    this.stateId = stateId;
    this.stateIdModifyFlag = true;
  }

  public Integer getStateId() {
    return this.stateId;
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

  public void setStateValueId(int stateValueId) {
    this.stateValueId = new Integer(stateValueId);
    this.stateValueIdModifyFlag = true;
  }

  public void setStateValueId(Integer stateValueId) {
    this.stateValueId = stateValueId;
    this.stateValueIdModifyFlag = true;
  }

  public Integer getStateValueId() {
    return this.stateValueId;
  }

  public void setValue(String value) {
    this.value = value;
    this.valueModifyFlag = true;
  }

  public String getValue() {
    return this.value;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getStateIdModifyFlag() {
    return this.stateIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getStateValueIdModifyFlag() {
    return this.stateValueIdModifyFlag;
  }

  public boolean getValueModifyFlag() {
    return this.valueModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
