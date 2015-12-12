package com.kingdrive.workflow.model;

public class LinkStateModel implements java.io.Serializable {
  private Integer nodeLinkStateId = null;

  private boolean nodeLinkStateIdModifyFlag;

  private Integer nodeLinkId = null;

  private boolean nodeLinkIdModifyFlag;

  private Integer stateId = null;

  private boolean stateIdModifyFlag;

  private String stateValue = null;

  private boolean stateValueModifyFlag;

  private String action = "";

  public LinkStateModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.nodeLinkStateIdModifyFlag = false;
    this.nodeLinkIdModifyFlag = false;
    this.stateIdModifyFlag = false;
    this.stateValueModifyFlag = false;
  }

  public void setNodeLinkStateId(int nodeLinkStateId) {
    this.nodeLinkStateId = new Integer(nodeLinkStateId);
    this.nodeLinkStateIdModifyFlag = true;
  }

  public void setNodeLinkStateId(Integer nodeLinkStateId) {
    this.nodeLinkStateId = nodeLinkStateId;
    this.nodeLinkStateIdModifyFlag = true;
  }

  public Integer getNodeLinkStateId() {
    return this.nodeLinkStateId;
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

  public void setStateId(int stateId) {
    this.stateId = new Integer(stateId);
    this.stateIdModifyFlag = true;
  }

  public void setStateId(Integer stateId) {
    this.stateId = stateId;
    this.stateIdModifyFlag = true;
  }

  public Integer getStateId() {
    return this.stateId;
  }

  public void setStateValue(String stateValue) {
    this.stateValue = stateValue;
    this.stateValueModifyFlag = true;
  }

  public String getStateValue() {
    return this.stateValue;
  }

  public boolean getNodeLinkStateIdModifyFlag() {
    return this.nodeLinkStateIdModifyFlag;
  }

  public boolean getNodeLinkIdModifyFlag() {
    return this.nodeLinkIdModifyFlag;
  }

  public boolean getStateIdModifyFlag() {
    return this.stateIdModifyFlag;
  }

  public boolean getStateValueModifyFlag() {
    return this.stateValueModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
