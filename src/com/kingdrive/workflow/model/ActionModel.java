package com.kingdrive.workflow.model;

public class ActionModel implements java.io.Serializable {
  private Integer actionId = null;

  private boolean actionIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String actionName = null;

  private boolean actionNameModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private String executeTime = null;

  private boolean executeTimeModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String owner = null;

  private boolean ownerModifyFlag;

  private String limitExecuteTime = null;

  private boolean limitExecuteTimeModifyFlag;

  private String action = "";

  public ActionModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.actionIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.actionNameModifyFlag = false;
    this.executorModifyFlag = false;
    this.executeTimeModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.ownerModifyFlag = false;
    this.limitExecuteTimeModifyFlag = false;
  }

  public void setActionId(int actionId) {
    this.actionId = new Integer(actionId);
    this.actionIdModifyFlag = true;
  }

  public void setActionId(Integer actionId) {
    this.actionId = actionId;
    this.actionIdModifyFlag = true;
  }

  public Integer getActionId() {
    return this.actionId;
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

  public void setActionName(String actionName) {
    this.actionName = actionName;
    this.actionNameModifyFlag = true;
  }

  public String getActionName() {
    return this.actionName;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
    this.executorModifyFlag = true;
  }

  public String getExecutor() {
    return this.executor;
  }

  public void setExecuteTime(String executeTime) {
    this.executeTime = executeTime;
    this.executeTimeModifyFlag = true;
  }

  public String getExecuteTime() {
    return this.executeTime;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
  }

  public void setOwner(String owner) {
    this.owner = owner;
    this.ownerModifyFlag = true;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setLimitExecuteTime(String limitExecuteTime) {
    this.limitExecuteTime = limitExecuteTime;
    this.limitExecuteTimeModifyFlag = true;
  }

  public String getLimitExecuteTime() {
    return this.limitExecuteTime;
  }

  public boolean getActionIdModifyFlag() {
    return this.actionIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getActionNameModifyFlag() {
    return this.actionNameModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getExecuteTimeModifyFlag() {
    return this.executeTimeModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getOwnerModifyFlag() {
    return this.ownerModifyFlag;
  }

  public boolean getLimitExecuteTimeModifyFlag() {
    return this.limitExecuteTimeModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
