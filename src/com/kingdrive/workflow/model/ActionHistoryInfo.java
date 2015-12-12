package com.kingdrive.workflow.model;

public class ActionHistoryInfo implements java.io.Serializable {
  private Integer actionHistoryId = null;

  private boolean actionHistoryIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private String instanceName = null;

  private boolean instanceNameModifyFlag;

  private String instanceDescription = null;

  private boolean instanceDescriptionModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;
	private Integer parentInstanceId = null;
	private boolean parentInstanceIdModifyFlag;
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

  private String actionName = null;

  private boolean actionNameModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private String executorName = null;

  private boolean executorNameModifyFlag;

  private String executeTime = null;

  private boolean executeTimeModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String owner = null;

  private boolean ownerModifyFlag;

  private String ownerName = null;

  private boolean ownerNameModifyFlag;

  private String limitExecuteTime = null;

  private boolean limitExecuteTimeModifyFlag;

  private String action = "";

  public ActionHistoryInfo() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.actionHistoryIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.instanceNameModifyFlag = false;
    this.instanceDescriptionModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.templateNameModifyFlag = false;
    this.templateTypeModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.nodeNameModifyFlag = false;
    this.businessTypeModifyFlag = false;
    this.actionNameModifyFlag = false;
    this.executorModifyFlag = false;
    this.executorNameModifyFlag = false;
    this.executeTimeModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.ownerModifyFlag = false;
    this.ownerNameModifyFlag = false;
    this.limitExecuteTimeModifyFlag = false;
  }

  public void setActionHistoryId(int actionHistoryId) {
    this.actionHistoryId = new Integer(actionHistoryId);
    this.actionHistoryIdModifyFlag = true;
  }

  public void setActionHistoryId(Integer actionHistoryId) {
    this.actionHistoryId = actionHistoryId;
    this.actionHistoryIdModifyFlag = true;
  }

  public Integer getActionHistoryId() {
    return this.actionHistoryId;
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

    public Integer getParentInstanceId() {
        return parentInstanceId;
    }
    public void setParentInstanceId(Integer parentInstanceId) {
        this.parentInstanceId = parentInstanceId;
        this.parentInstanceIdModifyFlag=true;
    }
    public void setParentInstanceId(int parentInstanceId) {
        this.parentInstanceId = new Integer(parentInstanceId);
        this.parentInstanceIdModifyFlag=true;
    }
    public boolean getParentInstanceIdModifyFlag() {
        return parentInstanceIdModifyFlag;
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

  public void setActionName(String actionName) {
    this.actionName = actionName;
    this.actionNameModifyFlag = true;
  }

  public String getActionName() {
    return this.actionName;
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

  public void setExecuteTime(String executeTime) {
    this.executeTime = executeTime;
    this.executeTimeModifyFlag = true;
  }

  public String getExecuteTime() {
    return this.executeTime;
  }

  public void setDescription(String description) {
    this.description = description;
    this.descriptionModifyFlag = true;
  }

  public String getDescription() {
    return this.description;
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

  public void setLimitExecuteTime(String limitExecuteTime) {
    this.limitExecuteTime = limitExecuteTime;
    this.limitExecuteTimeModifyFlag = true;
  }

  public String getLimitExecuteTime() {
    return this.limitExecuteTime;
  }

  public boolean getActionHistoryIdModifyFlag() {
    return this.actionHistoryIdModifyFlag;
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

  public boolean getActionNameModifyFlag() {
    return this.actionNameModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getExecutorNameModifyFlag() {
    return this.executorNameModifyFlag;
  }

  public boolean getExecuteTimeModifyFlag() {
    return this.executeTimeModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getOwnerModifyFlag() {
    return this.ownerModifyFlag;
  }

  public boolean getOwnerNameModifyFlag() {
    return this.ownerNameModifyFlag;
  }

  public boolean getLimitExecuteTimeModifyFlag() {
    return this.limitExecuteTimeModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
