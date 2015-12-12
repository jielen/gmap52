package com.kingdrive.workflow.model;

public class PositionModel implements java.io.Serializable {
  private String positionId = null;

  private boolean positionIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String action = "";

  public PositionModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.positionIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
    this.positionIdModifyFlag = true;
  }

  public String getPositionId() {
    return this.positionId;
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

  public boolean getPositionIdModifyFlag() {
    return this.positionIdModifyFlag;
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
