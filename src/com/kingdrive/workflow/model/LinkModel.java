package com.kingdrive.workflow.model;

public class LinkModel implements java.io.Serializable {
  private Integer nodeLinkId = null;

  private boolean nodeLinkIdModifyFlag;

  private String name = null;

  private boolean nameModifyFlag;

  private String description = null;

  private boolean descriptionModifyFlag;

  private String linkType = null;

  private boolean linkTypeModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private Integer currentNodeId = null;

  private boolean currentNodeIdModifyFlag;

  private Integer nextNodeId = null;

  private boolean nextNodeIdModifyFlag;

  private String executorRelation = null;

  private boolean executorRelationModifyFlag;

  private String executorsMethod = null;

  private boolean executorsMethodModifyFlag;

  private String numberOrPercent = null;

  private boolean numberOrPercentModifyFlag;

  private Double passValue = null;

  private boolean passValueModifyFlag;

  private String expression = null;

  private boolean expressionModifyFlag;

  private String defaultPath = null;

  private boolean defaultPathModifyFlag;

  private String actionName = null;

  private boolean actionNameModifyFlag;

  private String action = "";

  public LinkModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.nodeLinkIdModifyFlag = false;
    this.nameModifyFlag = false;
    this.descriptionModifyFlag = false;
    this.linkTypeModifyFlag = false;
    this.templateIdModifyFlag = false;
    this.currentNodeIdModifyFlag = false;
    this.nextNodeIdModifyFlag = false;
    this.executorRelationModifyFlag = false;
    this.executorsMethodModifyFlag = false;
    this.numberOrPercentModifyFlag = false;
    this.passValueModifyFlag = false;
    this.expressionModifyFlag = false;
    this.defaultPathModifyFlag = false;
    this.actionNameModifyFlag = false;
  }

  public void setNodeLinkId(int nodeLinkId) {
    this.nodeLinkId = new Integer(nodeLinkId);
    this.nodeLinkIdModifyFlag = true;
  }

  public void setNodeLinkId(Integer nodeLinkId) {
    this.nodeLinkId = nodeLinkId;
    this.nodeLinkIdModifyFlag = true;
  }

  public Integer getNodeLinkId() {
    return this.nodeLinkId;
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

  public void setLinkType(String linkType) {
    this.linkType = linkType;
    this.linkTypeModifyFlag = true;
  }

  public String getLinkType() {
    return this.linkType;
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

  public void setCurrentNodeId(int currentNodeId) {
    this.currentNodeId = new Integer(currentNodeId);
    this.currentNodeIdModifyFlag = true;
  }

  public void setCurrentNodeId(Integer currentNodeId) {
    this.currentNodeId = currentNodeId;
    this.currentNodeIdModifyFlag = true;
  }

  public Integer getCurrentNodeId() {
    return this.currentNodeId;
  }

  public void setNextNodeId(int nextNodeId) {
    this.nextNodeId = new Integer(nextNodeId);
    this.nextNodeIdModifyFlag = true;
  }

  public void setNextNodeId(Integer nextNodeId) {
    this.nextNodeId = nextNodeId;
    this.nextNodeIdModifyFlag = true;
  }

  public Integer getNextNodeId() {
    return this.nextNodeId;
  }

  public void setExecutorRelation(String executorRelation) {
    this.executorRelation = executorRelation;
    this.executorRelationModifyFlag = true;
  }

  public String getExecutorRelation() {
    return this.executorRelation;
  }

  public void setExecutorsMethod(String executorsMethod) {
    this.executorsMethod = executorsMethod;
    this.executorsMethodModifyFlag = true;
  }

  public String getExecutorsMethod() {
    return this.executorsMethod;
  }

  public void setNumberOrPercent(String numberOrPercent) {
    this.numberOrPercent = numberOrPercent;
    this.numberOrPercentModifyFlag = true;
  }

  public String getNumberOrPercent() {
    return this.numberOrPercent;
  }

  public void setPassValue(double passValue) {
    this.passValue = new Double(passValue);
    passValueModifyFlag = true;
  }

  public void setPassValue(Double passValue) {
    this.passValue = passValue;
    this.passValueModifyFlag = true;
  }

  public Double getPassValue() {
    return this.passValue;
  }

  public void setExpression(String expression) {
    this.expression = expression;
    this.expressionModifyFlag = true;
  }

  public String getExpression() {
    return this.expression;
  }

  public void setDefaultPath(String defaultPath) {
    this.defaultPath = defaultPath;
    this.defaultPathModifyFlag = true;
  }

  public String getDefaultPath() {
    return this.defaultPath;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
    this.actionNameModifyFlag = true;
  }

  public String getActionName() {
    return this.actionName;
  }

  public boolean getNodeLinkIdModifyFlag() {
    return this.nodeLinkIdModifyFlag;
  }

  public boolean getNameModifyFlag() {
    return this.nameModifyFlag;
  }

  public boolean getDescriptionModifyFlag() {
    return this.descriptionModifyFlag;
  }

  public boolean getLinkTypeModifyFlag() {
    return this.linkTypeModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public boolean getCurrentNodeIdModifyFlag() {
    return this.currentNodeIdModifyFlag;
  }

  public boolean getNextNodeIdModifyFlag() {
    return this.nextNodeIdModifyFlag;
  }

  public boolean getExecutorRelationModifyFlag() {
    return this.executorRelationModifyFlag;
  }

  public boolean getExecutorsMethodModifyFlag() {
    return this.executorsMethodModifyFlag;
  }

  public boolean getNumberOrPercentModifyFlag() {
    return this.numberOrPercentModifyFlag;
  }

  public boolean getPassValueModifyFlag() {
    return this.passValueModifyFlag;
  }

  public boolean getExpressionModifyFlag() {
    return this.expressionModifyFlag;
  }

  public boolean getDefaultPathModifyFlag() {
    return this.defaultPathModifyFlag;
  }

  public boolean getActionNameModifyFlag() {
    return this.actionNameModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
