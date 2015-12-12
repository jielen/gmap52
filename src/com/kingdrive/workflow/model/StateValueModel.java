package com.kingdrive.workflow.model;

public class StateValueModel implements java.io.Serializable {
  private Integer stateValueId = null;

  private boolean stateValueIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer stateId = null;

  private boolean stateIdModifyFlag;

  private String value = null;

  private boolean valueModifyFlag;

  private String action = "";

  public StateValueModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.stateValueIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.stateIdModifyFlag = false;
    this.valueModifyFlag = false;
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

  public void setValue(String value) {
    this.value = value;
    this.valueModifyFlag = true;
  }

  public String getValue() {
    return this.value;
  }

  public boolean getStateValueIdModifyFlag() {
    return this.stateValueIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getStateIdModifyFlag() {
    return this.stateIdModifyFlag;
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
