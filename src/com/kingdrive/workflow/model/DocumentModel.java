package com.kingdrive.workflow.model;

public class DocumentModel implements java.io.Serializable {
  private Integer documentId = null;

  private boolean documentIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private String type = null;

  private boolean typeModifyFlag;

  private String linkName = null;

  private boolean linkNameModifyFlag;

  private String uploadTime = null;

  private boolean uploadTimeModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String action = "";

  public DocumentModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.documentIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.typeModifyFlag = false;
    this.linkNameModifyFlag = false;
    this.uploadTimeModifyFlag = false;
    this.descriptionModifyFlag = false;
  }

  public void setDocumentId(int documentId) {
    this.documentId = new Integer(documentId);
    this.documentIdModifyFlag = true;
  }

  public void setDocumentId(Integer documentId) {
    this.documentId = documentId;
    this.documentIdModifyFlag = true;
  }

  public Integer getDocumentId() {
    return this.documentId;
  }

  public void setName(String name) {
    this.name = name;
    this.nameModifyFlag = true;
  }

  public String getName() {
    return this.name;
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

  public void setType(String type) {
    this.type = type;
    this.typeModifyFlag = true;
  }

  public String getType() {
    return this.type;
  }

  public void setLinkName(String linkName) {
    this.linkName = linkName;
    this.linkNameModifyFlag = true;
  }

  public String getLinkName() {
    return this.linkName;
  }

  public void setUploadTime(String uploadTime) {
    this.uploadTime = uploadTime;
    this.uploadTimeModifyFlag = true;
  }

  public String getUploadTime() {
    return this.uploadTime;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean getDocumentIdModifyFlag() {
    return this.documentIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getTypeModifyFlag() {
    return this.typeModifyFlag;
  }

  public boolean getLinkNameModifyFlag() {
    return this.linkNameModifyFlag;
  }

  public boolean getUploadTimeModifyFlag() {
    return this.uploadTimeModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
