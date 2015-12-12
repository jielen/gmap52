package com.kingdrive.workflow.dto;

import java.io.Serializable;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class InstanceMeta implements Serializable {

  public static final int INSTANCE_STATUS_ACTIVE = 1;

  public static final int INSTANCE_STATUS_DEACTIVE = -1;

  public static final int INSTANCE_STATUS_FINISHED = 9;

  public static final int INSTANCE_STATUS_INTERRUPTED = -9;

  private int templateId;

  private String templateName;

  private int instanceId;

  private String name;

  private String description;

  private String owner;

  private String ownerName;

  private String startTime;

  private String endTime;

  private int status;
  
  private int parentInstanceId;

  public InstanceMeta() {
    this.status = INSTANCE_STATUS_ACTIVE;
  }

  public int getTemplateId() {
    return templateId;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

    /**
     * @return Returns the parentInstanceId.
     */
    public int getParentInstanceId() {
        return parentInstanceId;
    }
    /**
     * @param parentInstanceId The parentInstanceId to set.
     */
    public void setParentInstanceId(int parentInstanceId) {
        this.parentInstanceId = parentInstanceId;
    }
}
