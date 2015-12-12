package com.kingdrive.workflow.model;

public class TaskExecutorModel implements java.io.Serializable {
  private Integer taskExecutorOrderId = null;

  private boolean taskExecutorOrderIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private Integer executorOrder = null;

  private boolean executorOrderModifyFlag;

  private Integer responsibility = null;

  private boolean responsibilityModifyFlag;

  private String action = "";

  public TaskExecutorModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.taskExecutorOrderIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.executorModifyFlag = false;
    this.executorOrderModifyFlag = false;
    this.responsibilityModifyFlag = false;
  }

  public void setTaskExecutorOrderId(int taskExecutorOrderId) {
    this.taskExecutorOrderId = new Integer(taskExecutorOrderId);
    this.taskExecutorOrderIdModifyFlag = true;
  }

  public void setTaskExecutorOrderId(Integer taskExecutorOrderId) {
    this.taskExecutorOrderId = taskExecutorOrderId;
    this.taskExecutorOrderIdModifyFlag = true;
  }

  public Integer getTaskExecutorOrderId() {
    return this.taskExecutorOrderId;
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

  public void setExecutor(String executor) {
    this.executor = executor;
    this.executorModifyFlag = true;
  }

  public String getExecutor() {
    return this.executor;
  }

  public void setExecutorOrder(int executorOrder) {
    this.executorOrder = new Integer(executorOrder);
    this.executorOrderModifyFlag = true;
  }

  public void setExecutorOrder(Integer executorOrder) {
    this.executorOrder = executorOrder;
    this.executorOrderModifyFlag = true;
  }

  public Integer getExecutorOrder() {
    return this.executorOrder;
  }

  public void setResponsibility(int responsibility) {
    this.responsibility = new Integer(responsibility);
    this.responsibilityModifyFlag = true;
  }

  public void setResponsibility(Integer responsibility) {
    this.responsibility = responsibility;
    this.responsibilityModifyFlag = true;
  }

  public Integer getResponsibility() {
    return this.responsibility;
  }

  public boolean getTaskExecutorOrderIdModifyFlag() {
    return this.taskExecutorOrderIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getExecutorOrderModifyFlag() {
    return this.executorOrderModifyFlag;
  }

  public boolean getResponsibilityModifyFlag() {
    return this.responsibilityModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
