package com.kingdrive.workflow.model;

public class NodeStateModel implements java.io.Serializable {
  private Integer nodeStateId = null;

  private boolean nodeStateIdModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private Integer stateId = null;

  private boolean stateIdModifyFlag;

  private String stateValue = null;

  private boolean stateValueModifyFlag;

  private String action = "";

  public NodeStateModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.nodeStateIdModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.stateIdModifyFlag = false;
    this.stateValueModifyFlag = false;
  }

  public void setNodeStateId(int nodeStateId) {
    this.nodeStateId = new Integer(nodeStateId);
    this.nodeStateIdModifyFlag = true;
  }

  public void setNodeStateId(Integer nodeStateId) {
    this.nodeStateId = nodeStateId;
    this.nodeStateIdModifyFlag = true;
  }

  public Integer getNodeStateId() {
    return this.nodeStateId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = new Integer(nodeId);
    this.nodeIdModifyFlag = true;
  }

  public void setNodeId(Integer nodeId) {
    this.nodeId = nodeId;
    this.nodeIdModifyFlag = true;
  }

  public Integer getNodeId() {
    return this.nodeId;
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

  public void setStateValue(String stateValue) {
    this.stateValue = stateValue;
    this.stateValueModifyFlag = true;
  }

  public String getStateValue() {
    return this.stateValue;
  }

  public boolean getNodeStateIdModifyFlag() {
    return this.nodeStateIdModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getStateIdModifyFlag() {
    return this.stateIdModifyFlag;
  }

  public boolean getStateValueModifyFlag() {
    return this.stateValueModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
