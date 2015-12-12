package com.kingdrive.workflow.model;

public class DelegationModel implements java.io.Serializable {
  private Integer delegationId = null;

  private boolean delegationIdModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String sender = null;

  private boolean senderModifyFlag;

  private String owner = null;

  private boolean ownerModifyFlag;

  private String receiver = null;

  private boolean receiverModifyFlag;

  private Integer parentId = null;

  private boolean parentIdModifyFlag;

  private String delegationTime = null;

  private boolean delegationTimeModifyFlag;

  private String startTime = null;

  private boolean startTimeModifyFlag;

  private String endTime = null;

  private boolean endTimeModifyFlag;

  private String action = "";

  public DelegationModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.delegationIdModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.senderModifyFlag = false;
    this.ownerModifyFlag = false;
    this.receiverModifyFlag = false;
    this.parentIdModifyFlag = false;
    this.delegationTimeModifyFlag = false;
    this.startTimeModifyFlag = false;
    this.endTimeModifyFlag = false;
  }

  public void setDelegationId(int delegationId) {
    this.delegationId = new Integer(delegationId);
    this.delegationIdModifyFlag = true;
  }

  public void setDelegationId(Integer delegationId) {
    this.delegationId = delegationId;
    this.delegationIdModifyFlag = true;
  }

  public Integer getDelegationId() {
    return this.delegationId;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
  }

  public void setTemplateId(int templateId) {
    this.templateId = new Integer(templateId);
    this.templateIdModifyFlag = true;
  }

  public void setTemplateId(Integer templateId) {
    this.templateId = templateId;
    this.templateIdModifyFlag = true;
  }

  public Integer getTemplateId() {
    return this.templateId;
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

  public void setSender(String sender) {
    this.sender = sender;
    this.senderModifyFlag = true;
  }

  public String getSender() {
    return this.sender;
  }

  public void setOwner(String owner) {
    this.owner = owner;
    this.ownerModifyFlag = true;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
    this.receiverModifyFlag = true;
  }

  public String getReceiver() {
    return this.receiver;
  }

  public void setParentId(int parentId) {
    this.parentId = new Integer(parentId);
    this.parentIdModifyFlag = true;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
    this.parentIdModifyFlag = true;
  }

  public Integer getParentId() {
    return this.parentId;
  }

  public void setDelegationTime(String delegationTime) {
    this.delegationTime = delegationTime;
    this.delegationTimeModifyFlag = true;
  }

  public String getDelegationTime() {
    return this.delegationTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
    this.startTimeModifyFlag = true;
  }

  public String getStartTime() {
    return this.startTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
    this.endTimeModifyFlag = true;
  }

  public String getEndTime() {
    return this.endTime;
  }

  public boolean getDelegationIdModifyFlag() {
    return this.delegationIdModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getSenderModifyFlag() {
    return this.senderModifyFlag;
  }

  public boolean getOwnerModifyFlag() {
    return this.ownerModifyFlag;
  }

  public boolean getReceiverModifyFlag() {
    return this.receiverModifyFlag;
  }

  public boolean getParentIdModifyFlag() {
    return this.parentIdModifyFlag;
  }

  public boolean getDelegationTimeModifyFlag() {
    return this.delegationTimeModifyFlag;
  }

  public boolean getStartTimeModifyFlag() {
    return this.startTimeModifyFlag;
  }

  public boolean getEndTimeModifyFlag() {
    return this.endTimeModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
