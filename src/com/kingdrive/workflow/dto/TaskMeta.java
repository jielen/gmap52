package com.kingdrive.workflow.dto;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import java.io.Serializable;
import java.util.List;

import com.kingdrive.workflow.model.CurrentTaskModel;

public class TaskMeta extends CurrentTaskMeta implements Serializable {
  private List positionList;

  private List variableValueList;

  private java.util.Set actionSet;

  public TaskMeta() {
  }

  public TaskMeta(CurrentTaskMeta meta) {
    this.setCurrentTaskId(meta.getCurrentTaskId());
    this.setInstanceId(meta.getInstanceId());
    this.setInstanceName(meta.getInstanceName());
    this.setInstanceDescription(meta.getInstanceDescription());
    this.setInstanceStartTime(meta.getInstanceStartTime());
    this.setInstanceEndTime(meta.getInstanceEndTime());
    this.setTemplateId(meta.getTemplateId());
    this.setTemplateName(meta.getTemplateName());
    this.setTemplateType(meta.getTemplateType());
    this.setNodeId(meta.getNodeId());
    this.setNodeName(meta.getNodeName());
    this.setBusinessType(meta.getBusinessType());
    this.setExecutor(meta.getExecutor());
    this.setExecutorName(meta.getExecutorName());
    this.setIdentity(meta.getIdentity());
    this.setOwner(meta.getOwner());
    this.setOwnerName(meta.getOwnerName());
    this.setCreator(meta.getCreator());
    this.setCreatorName(meta.getCreatorName());
    this.setCreateTime(meta.getCreateTime());
    this.setLimitExecuteTime(meta.getLimitExecuteTime());
    this.setResponsibility(meta.getResponsibility());
  }

  public java.util.Set getActionSet() {
    return actionSet;
  }

  public void setActionSet(java.util.Set actionSet) {
    this.actionSet = actionSet;
  }

  public List getPositionList() {
    return positionList;
  }

  public void setPositionList(List positionList) {
    this.positionList = positionList;
  }

  public List getVariableValueList() {
    return variableValueList;
  }

  public void setVariableValueList(List variableValueList) {
    this.variableValueList = variableValueList;
  }
}
