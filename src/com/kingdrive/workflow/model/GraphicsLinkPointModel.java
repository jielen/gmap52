package com.kingdrive.workflow.model;

public class GraphicsLinkPointModel implements java.io.Serializable {
  private Integer graphicsLinkPointId = null;

  private boolean graphicsLinkPointIdModifyFlag;

  private Integer nodeLinkId = null;

  private boolean nodeLinkIdModifyFlag;

  private Integer sort = null;

  private boolean sortModifyFlag;

  private Integer x = null;

  private boolean xModifyFlag;

  private Integer y = null;

  private boolean yModifyFlag;

  private Integer templateId = null;

  private boolean templateIdModifyFlag;

  private String action = "";

  public GraphicsLinkPointModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.graphicsLinkPointIdModifyFlag = false;
    this.nodeLinkIdModifyFlag = false;
    this.sortModifyFlag = false;
    this.xModifyFlag = false;
    this.yModifyFlag = false;
    this.templateIdModifyFlag = false;
  }

  public void setGraphicsLinkPointId(int graphicsLinkPointId) {
    this.graphicsLinkPointId = new Integer(graphicsLinkPointId);
    this.graphicsLinkPointIdModifyFlag = true;
  }

  public void setGraphicsLinkPointId(Integer graphicsLinkPointId) {
    this.graphicsLinkPointId = graphicsLinkPointId;
    this.graphicsLinkPointIdModifyFlag = true;
  }

  public Integer getGraphicsLinkPointId() {
    return this.graphicsLinkPointId;
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

  public void setSort(int sort) {
    this.sort = new Integer(sort);
    this.sortModifyFlag = true;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
    this.sortModifyFlag = true;
  }

  public Integer getSort() {
    return this.sort;
  }

  public void setX(int x) {
    this.x = new Integer(x);
    this.xModifyFlag = true;
  }

  public void setX(Integer x) {
    this.x = x;
    this.xModifyFlag = true;
  }

  public Integer getX() {
    return this.x;
  }

  public void setY(int y) {
    this.y = new Integer(y);
    this.yModifyFlag = true;
  }

  public void setY(Integer y) {
    this.y = y;
    this.yModifyFlag = true;
  }

  public Integer getY() {
    return this.y;
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

  public boolean getGraphicsLinkPointIdModifyFlag() {
    return this.graphicsLinkPointIdModifyFlag;
  }

  public boolean getNodeLinkIdModifyFlag() {
    return this.nodeLinkIdModifyFlag;
  }

  public boolean getSortModifyFlag() {
    return this.sortModifyFlag;
  }

  public boolean getXModifyFlag() {
    return this.xModifyFlag;
  }

  public boolean getYModifyFlag() {
    return this.yModifyFlag;
  }

  public boolean getTemplateIdModifyFlag() {
    return this.templateIdModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
