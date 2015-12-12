package com.kingdrive.workflow.model;

public class TaskTermModel implements java.io.Serializable {
  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private Integer limitExecuteTerm = null;

  private boolean limitExecuteTermModifyFlag;

  private String action = "";

  public TaskTermModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.instanceIdModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.limitExecuteTermModifyFlag = false;
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

  public void setLimitExecuteTerm(int limitExecuteTerm) {
    this.limitExecuteTerm = new Integer(limitExecuteTerm);
    this.limitExecuteTermModifyFlag = true;
  }

  public void setLimitExecuteTerm(Integer limitExecuteTerm) {
    this.limitExecuteTerm = limitExecuteTerm;
    this.limitExecuteTermModifyFlag = true;
  }

  public Integer getLimitExecuteTerm() {
    return this.limitExecuteTerm;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getLimitExecuteTermModifyFlag() {
    return this.limitExecuteTermModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
