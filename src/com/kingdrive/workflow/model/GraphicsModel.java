package com.kingdrive.workflow.model;

public class GraphicsModel implements java.io.Serializable {
  private Integer type = null;

  private boolean typeModifyFlag;

  private Integer elementId = null;

  private boolean elementIdModifyFlag;

  private Integer ownerId = null;

  private boolean ownerIdModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private Integer objectId = null;

  private boolean objectIdModifyFlag;

  private Integer elementX = null;

  private boolean elementXModifyFlag;

  private Integer elementY = null;

  private boolean elementYModifyFlag;

  private Integer elementWidth = null;

  private boolean elementWidthModifyFlag;

  private Integer elementHeight = null;

  private boolean elementHeightModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String action = "";

  public GraphicsModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.typeModifyFlag = false;
    this.elementIdModifyFlag = false;
    this.ownerIdModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.objectIdModifyFlag = false;
    this.elementXModifyFlag = false;
    this.elementYModifyFlag = false;
    this.elementWidthModifyFlag = false;
    this.elementHeightModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
  }

  public void setType(int type) {
    this.type = new Integer(type);
    this.typeModifyFlag = true;
  }

  public void setType(Integer type) {
    this.type = type;
    this.typeModifyFlag = true;
  }

  public Integer getType() {
    return this.type;
  }

  public void setElementId(int elementId) {
    this.elementId = new Integer(elementId);
    this.elementIdModifyFlag = true;
  }

  public void setElementId(Integer elementId) {
    this.elementId = elementId;
    this.elementIdModifyFlag = true;
  }

  public Integer getElementId() {
    return this.elementId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = new Integer(ownerId);
    this.ownerIdModifyFlag = true;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
    this.ownerIdModifyFlag = true;
  }

  public Integer getOwnerId() {
    return this.ownerId;
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

  public void setObjectId(int objectId) {
    this.objectId = new Integer(objectId);
    this.objectIdModifyFlag = true;
  }

  public void setObjectId(Integer objectId) {
    this.objectId = objectId;
    this.objectIdModifyFlag = true;
  }

  public Integer getObjectId() {
    return this.objectId;
  }

  public void setElementX(int elementX) {
    this.elementX = new Integer(elementX);
    this.elementXModifyFlag = true;
  }

  public void setElementX(Integer elementX) {
    this.elementX = elementX;
    this.elementXModifyFlag = true;
  }

  public Integer getElementX() {
    return this.elementX;
  }

  public void setElementY(int elementY) {
    this.elementY = new Integer(elementY);
    this.elementYModifyFlag = true;
  }

  public void setElementY(Integer elementY) {
    this.elementY = elementY;
    this.elementYModifyFlag = true;
  }

  public Integer getElementY() {
    return this.elementY;
  }

  public void setElementWidth(int elementWidth) {
    this.elementWidth = new Integer(elementWidth);
    this.elementWidthModifyFlag = true;
  }

  public void setElementWidth(Integer elementWidth) {
    this.elementWidth = elementWidth;
    this.elementWidthModifyFlag = true;
  }

  public Integer getElementWidth() {
    return this.elementWidth;
  }

  public void setElementHeight(int elementHeight) {
    this.elementHeight = new Integer(elementHeight);
    this.elementHeightModifyFlag = true;
  }

  public void setElementHeight(Integer elementHeight) {
    this.elementHeight = elementHeight;
    this.elementHeightModifyFlag = true;
  }

  public Integer getElementHeight() {
    return this.elementHeight;
  }

  public void setName(String name) {
    this.name = name;
    this.nameModifyFlag = true;
  }

  public String getName() {
    return this.name;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean getTypeModifyFlag() {
    return this.typeModifyFlag;
  }

  public boolean getElementIdModifyFlag() {
    return this.elementIdModifyFlag;
  }

  public boolean getOwnerIdModifyFlag() {
    return this.ownerIdModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getObjectIdModifyFlag() {
    return this.objectIdModifyFlag;
  }

  public boolean getElementXModifyFlag() {
    return this.elementXModifyFlag;
  }

  public boolean getElementYModifyFlag() {
    return this.elementYModifyFlag;
  }

  public boolean getElementWidthModifyFlag() {
    return this.elementWidthModifyFlag;
  }

  public boolean getElementHeightModifyFlag() {
    return this.elementHeightModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
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
