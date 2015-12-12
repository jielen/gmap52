package com.kingdrive.workflow.model;

public class VariableValueModel implements java.io.Serializable {
  private Integer valueId = null;

  private boolean valueIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer variableId = null;

  private boolean variableIdModifyFlag;

  private String value = null;

  private boolean valueModifyFlag;

  private String action = "";

  public VariableValueModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.valueIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.variableIdModifyFlag = false;
    this.valueModifyFlag = false;
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

  public void setValue(String value) {
    this.value = value;
    this.valueModifyFlag = true;
  }

  public String getValue() {
    return this.value;
  }

  public boolean getValueIdModifyFlag() {
    return this.valueIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getVariableIdModifyFlag() {
    return this.variableIdModifyFlag;
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
