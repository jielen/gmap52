package com.kingdrive.workflow.model;

public class InstanceModel implements java.io.Serializable {
  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String owner = null;

  private boolean ownerModifyFlag;

  private String startTime = null;

  private boolean startTimeModifyFlag;

  private String endTime = null;

  private boolean endTimeModifyFlag;

  private Integer status = null;

  private boolean statusModifyFlag;

  private String action = "";
	private Integer parentInstanceId = null;
	private boolean parentInstanceIdModifyFlag;

  public InstanceModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.instanceIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.ownerModifyFlag = false;
    this.startTimeModifyFlag = false;
    this.endTimeModifyFlag = false;
    this.statusModifyFlag = false;
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

  public void setOwner(String owner) {
    this.owner = owner;
    this.ownerModifyFlag = true;
  }

  public String getOwner() {
    return this.owner;
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

  public void setStatus(int status) {
    this.status = new Integer(status);
    this.statusModifyFlag = true;
  }

  public void setStatus(Integer status) {
    this.status = status;
    this.statusModifyFlag = true;
  }

  public Integer getStatus() {
    return this.status;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getOwnerModifyFlag() {
    return this.ownerModifyFlag;
  }

  public boolean getStartTimeModifyFlag() {
    return this.startTimeModifyFlag;
  }

  public boolean getEndTimeModifyFlag() {
    return this.endTimeModifyFlag;
  }

  public boolean getStatusModifyFlag() {
    return this.statusModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
    /**
     * @return Returns the parentInstanceId.
     */
    public Integer getParentInstanceId() {
        return parentInstanceId;
    }
    /**
     * @param parentInstanceId The parentInstanceId to set.
     */
    public void setParentInstanceId(Integer parentInstanceId) {
        this.parentInstanceId = parentInstanceId;
        this.parentInstanceIdModifyFlag=true;
    }
    /**
     * @param parentInstanceId The parentInstanceId to set.
     */
    public void setParentInstanceId(int parentInstanceId) {
        this.parentInstanceId = new Integer(parentInstanceId);
        this.parentInstanceIdModifyFlag=true;
    }
    /**
     * @return Returns the parentInstanceIdModifyFlag.
     */
    public boolean getParentInstanceIdModifyFlag() {
        return parentInstanceIdModifyFlag;
    }
}
