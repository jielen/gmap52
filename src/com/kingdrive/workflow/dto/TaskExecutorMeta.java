package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class TaskExecutorMeta implements Serializable {
  public static final int TASK_EXECUROR_RESPONSIBILITY_MAIN = 1;

  public static final int TASK_EXECUROR_RESPONSIBILITY_ASSISTANT = -1;

  private int id;

  private int instanceId;

  private String instanceName;

  private int nodeId;

  private String nodeName;

  private String executor;

  private String executorName;

  private int executorOrder;

  private String executorsMethod;

  private int responsibility;

  public TaskExecutorMeta() {
    responsibility = TASK_EXECUROR_RESPONSIBILITY_MAIN;
  }

  public int getId() {
    return id;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public int getNodeId() {
    return nodeId;
  }

  public String getExecutor() {
    return executor;
  }

  public int getExecutorOrder() {
    return executorOrder;
  }

  public String getNodeName() {
    return nodeName;
  }

  public String getExecutorsMethod() {
    return executorsMethod;
  }

  public String getExecutorName() {
    return executorName;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  public void setExecutorOrder(int executorOrder) {
    this.executorOrder = executorOrder;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public void setExecutorsMethod(String executorsMethod) {
    this.executorsMethod = executorsMethod;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public int getResponsibility() {
    return responsibility;
  }

  public void setResponsibility(int responsibility) {
    this.responsibility = responsibility;
  }
}
