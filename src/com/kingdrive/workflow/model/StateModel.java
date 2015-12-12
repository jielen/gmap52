package com.kingdrive.workflow.model;

public class StateModel implements java.io.Serializable {
  private Integer stateId = null;

  private boolean stateIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String action = "";

  public StateModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.stateIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.templateIdModifyFlag = false;
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

  public boolean getStateIdModifyFlag() {
    return this.stateIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
