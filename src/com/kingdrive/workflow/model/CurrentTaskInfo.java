package com.kingdrive.workflow.model;

public class CurrentTaskInfo implements java.io.Serializable {
  private Integer currentTaskId = null;

  private boolean currentTaskIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private String instanceName = null;

  private boolean instanceNameModifyFlag;

  private String instanceDescription = null;

  private boolean instanceDescriptionModifyFlag;

  private String instanceStartTime = null;

  private boolean instanceStartTimeModifyFlag;

  private String instanceEndTime = null;

  private boolean instanceEndTimeModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String templateName = null;

  private boolean templateNameModifyFlag;

  private String templateType = null;

  private boolean templateTypeModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String nodeName = null;

  private boolean nodeNameModifyFlag;

  private String businessType = null;

  private boolean businessTypeModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private String executorName = null;

  private boolean executorNameModifyFlag;

  private Integer delegationId = null;

  private boolean delegationIdModifyFlag;

  private String owner = null;

  private boolean ownerModifyFlag;

  private String ownerName = null;

  private boolean ownerNameModifyFlag;

  private String creator = null;

  private boolean creatorModifyFlag;

  private String creatorName = null;

  private boolean creatorNameModifyFlag;

  private String createTime = null;

  private boolean createTimeModifyFlag;

  private String limitExecuteTime = null;

  private boolean limitExecuteTimeModifyFlag;

  private Integer responsibility = null;
  private String compoId = null;

  private boolean responsibilityModifyFlag;
	private Integer parentTaskId = null;
	private boolean parentTaskIdModifyFlag;
  private String action = "";

  public CurrentTaskInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.currentTaskIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.instanceNameModifyFlag = false;
    this.instanceDescriptionModifyFlag = false;
    this.instanceStartTimeModifyFlag = false;
    this.instanceEndTimeModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.templateNameModifyFlag = false;
    this.templateTypeModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.nodeNameModifyFlag = false;
    this.businessTypeModifyFlag = false;
    this.executorModifyFlag = false;
    this.executorNameModifyFlag = false;
    this.delegationIdModifyFlag = false;
    this.ownerModifyFlag = false;
    this.ownerNameModifyFlag = false;
    this.creatorModifyFlag = false;
    this.creatorNameModifyFlag = false;
    this.createTimeModifyFlag = false;
    this.limitExecuteTimeModifyFlag = false;
    this.responsibilityModifyFlag = false;
		this.parentTaskIdModifyFlag= false;
  }

  public void setCurrentTaskId(int currentTaskId) {
    this.currentTaskId = new Integer(currentTaskId);
    this.currentTaskIdModifyFlag = true;
  }

  public void setCurrentTaskId(Integer currentTaskId) {
    this.currentTaskId = currentTaskId;
    this.currentTaskIdModifyFlag = true;
  }

  public Integer getCurrentTaskId() {
    return this.currentTaskId;
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

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
    this.instanceNameModifyFlag = true;
  }

  public String getInstanceName() {
    return this.instanceName;
  }

  public void setInstanceDescription(String instanceDescription) {
    this.instanceDescription = instanceDescription;
    this.instanceDescriptionModifyFlag = true;
  }

  public String getInstanceDescription() {
    return this.instanceDescription;
  }

  public void setInstanceStartTime(String instanceStartTime) {
    this.instanceStartTime = instanceStartTime;
    this.instanceStartTimeModifyFlag = true;
  }

  public String getInstanceStartTime() {
    return this.instanceStartTime;
  }

  public void setInstanceEndTime(String instanceEndTime) {
    this.instanceEndTime = instanceEndTime;
    this.instanceEndTimeModifyFlag = true;
  }

  public String getInstanceEndTime() {
    return this.instanceEndTime;
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

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
    this.templateNameModifyFlag = true;
  }

  public String getTemplateName() {
    return this.templateName;
  }

  public void setTemplateType(String templateType) {
    this.templateType = templateType;
    this.templateTypeModifyFlag = true;
  }

  public String getTemplateType() {
    return this.templateType;
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

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
    this.nodeNameModifyFlag = true;
  }

  public String getNodeName() {
    return this.nodeName;
  }

  public void setBusinessType(String businessType) {
    this.businessType = businessType;
    this.businessTypeModifyFlag = true;
  }

  public String getBusinessType() {
    return this.businessType;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
    this.executorModifyFlag = true;
  }

  public String getExecutor() {
    return this.executor;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
    this.executorNameModifyFlag = true;
  }

  public String getExecutorName() {
    return this.executorName;
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

  public void setOwner(String owner) {
    this.owner = owner;
    this.ownerModifyFlag = true;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
    this.ownerNameModifyFlag = true;
  }

  public String getOwnerName() {
    return this.ownerName;
  }

  public void setCreator(String creator) {
    this.creator = creator;
    this.creatorModifyFlag = true;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
    this.creatorNameModifyFlag = true;
  }

  public String getCreatorName() {
    return this.creatorName;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
    this.createTimeModifyFlag = true;
  }

  public String getCreateTime() {
    return this.createTime;
  }

  public void setLimitExecuteTime(String limitExecuteTime) {
    this.limitExecuteTime = limitExecuteTime;
    this.limitExecuteTimeModifyFlag = true;
  }

  public String getLimitExecuteTime() {
    return this.limitExecuteTime;
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

  public boolean getCurrentTaskIdModifyFlag() {
    return this.currentTaskIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getInstanceNameModifyFlag() {
    return this.instanceNameModifyFlag;
  }

  public boolean getInstanceDescriptionModifyFlag() {
    return this.instanceDescriptionModifyFlag;
  }

  public boolean getInstanceStartTimeModifyFlag() {
    return this.instanceStartTimeModifyFlag;
  }

  public boolean getInstanceEndTimeModifyFlag() {
    return this.instanceEndTimeModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getTemplateNameModifyFlag() {
    return this.templateNameModifyFlag;
  }

  public boolean getTemplateTypeModifyFlag() {
    return this.templateTypeModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getNodeNameModifyFlag() {
    return this.nodeNameModifyFlag;
  }

  public boolean getBusinessTypeModifyFlag() {
    return this.businessTypeModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getExecutorNameModifyFlag() {
    return this.executorNameModifyFlag;
  }

  public boolean getDelegationIdModifyFlag() {
    return this.delegationIdModifyFlag;
  }

  public boolean getOwnerModifyFlag() {
    return this.ownerModifyFlag;
  }

  public boolean getOwnerNameModifyFlag() {
    return this.ownerNameModifyFlag;
  }

  public boolean getCreatorModifyFlag() {
    return this.creatorModifyFlag;
  }

  public boolean getCreatorNameModifyFlag() {
    return this.creatorNameModifyFlag;
  }

  public boolean getCreateTimeModifyFlag() {
    return this.createTimeModifyFlag;
  }

  public boolean getLimitExecuteTimeModifyFlag() {
    return this.limitExecuteTimeModifyFlag;
  }

  public boolean getResponsibilityModifyFlag() {
    return this.responsibilityModifyFlag;
  }

    /**
     * @return Returns the parentTaskId.
     */
    public Integer getParentTaskId() {
        return parentTaskId;
    }
    /**
     * @param parentTaskId The parentTaskId to set.
     */
    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
        this.parentTaskIdModifyFlag=true;
    }
    /**
     * @param parentTaskId The parentTaskId to set.
     */
    public void setParentTaskId(int parentTaskId) {
        this.parentTaskId = new Integer(parentTaskId);
        this.parentTaskIdModifyFlag=true;
    }
    
    /**
     * @return Returns the parentTaskIdModifyFlag.
     */
    public boolean getParentTaskIdModifyFlag() {
        return parentTaskIdModifyFlag;
    }
  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
  public String getCompoId() {
    return this.compoId;
  }

  public void setCompoId(String compoId) {
    this.compoId = compoId;
  }
}
