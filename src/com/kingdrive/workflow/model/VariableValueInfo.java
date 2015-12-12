package com.kingdrive.workflow.model;

public class VariableValueInfo implements java.io.Serializable {
  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer variableId = null;

  private boolean variableIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String type = null;

  private boolean typeModifyFlag;

  private Integer valueId = null;

  private boolean valueIdModifyFlag;

  private String value = null;

  private boolean valueModifyFlag;

  private String action = "";

  public VariableValueInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.templateIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.variableIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.typeModifyFlag = false;
    this.valueIdModifyFlag = false;
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

  public void setValueId(int valueId) {
    this.valueId = new Integer(valueId);
    this.valueIdModifyFlag = true;
  }

  public void setValueId(Integer valueId) {
    this.valueId = valueId;
    this.valueIdModifyFlag = true;
  }

  public Integer getValueId() {
    return this.valueId;
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

  public boolean getValueIdModifyFlag() {
    return this.valueIdModifyFlag;
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
