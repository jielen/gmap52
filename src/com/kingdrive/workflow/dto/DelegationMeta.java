package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class DelegationMeta implements Serializable {
  private int id;

  private String description;

  private int templateId;

  private String templateName;

  private int nodeId;

  private String nodeName;

  private String sender;

  private String senderName;

  private String owner;

  private String ownerName;

  private String receiver;

  private String receiverName;

  private int parentId;

  private String delegationTime;

  private String startTime;

  private String endTime;

  public DelegationMeta() {
  }

  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public int getTemplateId() {
    return templateId;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOwner() {
    return owner;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getSender() {
    return sender;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setDelegationTime(String delegationTime) {
    this.delegationTime = delegationTime;
  }

  public String getDelegationTime() {
    return delegationTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
  }

  public int getParentId() {
    return parentId;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getSenderName() {
    return senderName;
  }
}
