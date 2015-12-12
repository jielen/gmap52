package com.kingdrive.workflow.model;

public class ExecutorSourceModel implements java.io.Serializable {
  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private Integer source = null;

  private boolean sourceModifyFlag;

  private Integer responsibility = null;

  private boolean responsibilityModifyFlag;

  private String action = "";

  public ExecutorSourceModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.nodeIdModifyFlag = false;
    this.executorModifyFlag = false;
    this.sourceModifyFlag = false;
    this.responsibilityModifyFlag = false;
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

  public void setSource(int source) {
    this.source = new Integer(source);
    this.sourceModifyFlag = true;
  }

  public void setSource(Integer source) {
    this.source = source;
    this.sourceModifyFlag = true;
  }

  public Integer getSource() {
    return this.source;
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

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getSourceModifyFlag() {
    return this.sourceModifyFlag;
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
