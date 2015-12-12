package com.kingdrive.workflow.model;

public class PassModel implements java.io.Serializable {
  private Integer passCountId = null;

  private boolean passCountIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer nodeLinkId = null;

  private boolean nodeLinkIdModifyFlag;

  private Integer currentNodeId = null;

  private boolean currentNodeIdModifyFlag;

  private Integer nextNodeId = null;

  private boolean nextNodeIdModifyFlag;

  private String action = "";

  public PassModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.passCountIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.nodeLinkIdModifyFlag = false;
    this.currentNodeIdModifyFlag = false;
    this.nextNodeIdModifyFlag = false;
  }

  public void setPassCountId(int passCountId) {
    this.passCountId = new Integer(passCountId);
    this.passCountIdModifyFlag = true;
  }

  public void setPassCountId(Integer passCountId) {
    this.passCountId = passCountId;
    this.passCountIdModifyFlag = true;
  }

  public Integer getPassCountId() {
    return this.passCountId;
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

  public void setNodeLinkId(int nodeLinkId) {
    this.nodeLinkId = new Integer(nodeLinkId);
    this.nodeLinkIdModifyFlag = true;
  }

  public void setNodeLinkId(Integer nodeLinkId) {
    this.nodeLinkId = nodeLinkId;
    this.nodeLinkIdModifyFlag = true;
  }

  public Integer getNodeLinkId() {
    return this.nodeLinkId;
  }

  public void setCurrentNodeId(int currentNodeId) {
    this.currentNodeId = new Integer(currentNodeId);
    this.currentNodeIdModifyFlag = true;
  }

  public void setCurrentNodeId(Integer currentNodeId) {
    this.currentNodeId = currentNodeId;
    this.currentNodeIdModifyFlag = true;
  }

  public Integer getCurrentNodeId() {
    return this.currentNodeId;
  }

  public void setNextNodeId(int nextNodeId) {
    this.nextNodeId = new Integer(nextNodeId);
    this.nextNodeIdModifyFlag = true;
  }

  public void setNextNodeId(Integer nextNodeId) {
    this.nextNodeId = nextNodeId;
    this.nextNodeIdModifyFlag = true;
  }

  public Integer getNextNodeId() {
    return this.nextNodeId;
  }

  public boolean getPassCountIdModifyFlag() {
    return this.passCountIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getNodeLinkIdModifyFlag() {
    return this.nodeLinkIdModifyFlag;
  }

  public boolean getCurrentNodeIdModifyFlag() {
    return this.currentNodeIdModifyFlag;
  }

  public boolean getNextNodeIdModifyFlag() {
    return this.nextNodeIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
