package com.kingdrive.workflow.model;

public class VariableModel implements java.io.Serializable {
  private Integer variableId = null;

  private boolean variableIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String type = null;

  private boolean typeModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String action = "";

  public VariableModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.variableIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.typeModifyFlag = false;
    this.templateIdModifyFlag = false;
  }

  public void setVariableId(int variableId) {
    this.variableId = new Integer(variableId);
    this.variableIdModifyFlag = true;
  }

  public void setVariableId(Integer variableId) {
    this.variableId = variableId;
    this.variableIdModifyFlag = true;
  }

  public Integer getVariableId() {
    return this.variableId;
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

  public void setType(String type) {
    this.type = type;
    this.typeModifyFlag = true;
  }

  public String getType() {
    return this.type;
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

  public boolean getVariableIdModifyFlag() {
    return this.variableIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getTypeModifyFlag() {
    return this.typeModifyFlag;
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
