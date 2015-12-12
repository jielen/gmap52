package com.kingdrive.workflow.model;

public class NodeModel implements java.io.Serializable {
  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String type = null;

  private boolean typeModifyFlag;

  private String businessType = null;

  private boolean businessTypeModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String executorsMethod = null;

  private boolean executorsMethodModifyFlag;

  private String taskListener = null;

  private boolean taskListenerModifyFlag;

  private Integer limitExecuteTerm = null;

  private boolean limitExecuteTermModifyFlag;

  private Integer remindExecuteTerm = null;

  private boolean remindExecuteTermModifyFlag;

  private String action = "";

  public NodeModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.nodeIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.typeModifyFlag = false;
    this.businessTypeModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.executorsMethodModifyFlag = false;
    this.taskListenerModifyFlag = false;
    this.limitExecuteTermModifyFlag = false;
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

  public void setType(String type) {
    this.type = type;
    this.typeModifyFlag = true;
  }

  public String getType() {
    return this.type;
  }

  public void setBusinessType(String businessType) {
    this.businessType = businessType;
    this.businessTypeModifyFlag = true;
  }

  public String getBusinessType() {
    return this.businessType;
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

  public void setExecutorsMethod(String executorsMethod) {
    this.executorsMethod = executorsMethod;
    this.executorsMethodModifyFlag = true;
  }

  public String getExecutorsMethod() {
    return this.executorsMethod;
  }

  public void setTaskListener(String taskListener) {
    this.taskListener = taskListener;
    this.taskListenerModifyFlag = true;
  }

  public String getTaskListener() {
    return this.taskListener;
  }

  public void setLimitExecuteTerm(int limitExecuteTerm) {
    this.limitExecuteTerm = new Integer(limitExecuteTerm);
    this.limitExecuteTermModifyFlag = true;
  }

  public void setLimitExecuteTerm(Integer limitExecuteTerm) {
    this.limitExecuteTerm = limitExecuteTerm;
    this.limitExecuteTermModifyFlag = true;
  }

  public Integer getLimitExecuteTerm() {
    return this.limitExecuteTerm;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getTypeModifyFlag() {
    return this.typeModifyFlag;
  }

  public boolean getBusinessTypeModifyFlag() {
    return this.businessTypeModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getExecutorsMethodModifyFlag() {
    return this.executorsMethodModifyFlag;
  }

  public boolean getTaskListenerModifyFlag() {
    return this.taskListenerModifyFlag;
  }

  public boolean getLimitExecuteTermModifyFlag() {
    return this.limitExecuteTermModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  /**
   * @return Returns the remindExecuteTerm.
   */
  public Integer getRemindExecuteTerm() {
    return remindExecuteTerm;
  }

  /**
   * @param remindExecuteTerm
   *          The remindExecuteTerm to set.
   */
  public void setRemindExecuteTerm(int remindExecuteTerm) {
    this.remindExecuteTerm = new Integer(remindExecuteTerm);
    this.remindExecuteTermModifyFlag = true;
  }

  public void setRemindExecuteTerm(Integer remindExecuteTerm) {
    this.remindExecuteTerm = remindExecuteTerm;
    this.remindExecuteTermModifyFlag = true;
  }

  public boolean getRemindExecuteTermModifyFlag() {
    return this.remindExecuteTermModifyFlag;
  }
}
