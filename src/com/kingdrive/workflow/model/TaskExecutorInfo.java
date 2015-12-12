package com.kingdrive.workflow.model;

public class TaskExecutorInfo implements java.io.Serializable {
  private Integer taskExecutorOrderId = null;

  private boolean taskExecutorOrderIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private String instanceName = null;

  private boolean instanceNameModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String nodeName = null;

  private boolean nodeNameModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private String executorName = null;

  private boolean executorNameModifyFlag;

  private Integer executorOrder = null;

  private boolean executorOrderModifyFlag;

  private String executorsMethod = null;

  private boolean executorsMethodModifyFlag;

  private Integer responsibility = null;

  private boolean responsibilityModifyFlag;

  private String action = "";

  public TaskExecutorInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.taskExecutorOrderIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.instanceNameModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.nodeNameModifyFlag = false;
    this.executorModifyFlag = false;
    this.executorNameModifyFlag = false;
    this.executorOrderModifyFlag = false;
    this.executorsMethodModifyFlag = false;
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

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
    this.instanceNameModifyFlag = true;
  }

  public String getInstanceName() {
    return this.instanceName;
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

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
    this.nodeNameModifyFlag = true;
  }

  public String getNodeName() {
    return this.nodeName;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
    this.executorModifyFlag = true;
  }

  public String getExecutor() {
    return this.executor;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
    this.executorNameModifyFlag = true;
  }

  public String getExecutorName() {
    return this.executorName;
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

  public void setExecutorsMethod(String executorsMethod) {
    this.executorsMethod = executorsMethod;
    this.executorsMethodModifyFlag = true;
  }

  public String getExecutorsMethod() {
    return this.executorsMethod;
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

  public boolean getInstanceNameModifyFlag() {
    return this.instanceNameModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getNodeNameModifyFlag() {
    return this.nodeNameModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getExecutorNameModifyFlag() {
    return this.executorNameModifyFlag;
  }

  public boolean getExecutorOrderModifyFlag() {
    return this.executorOrderModifyFlag;
  }

  public boolean getExecutorsMethodModifyFlag() {
    return this.executorsMethodModifyFlag;
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
