/* $Id: RightBean.java,v 1.1 2008/04/01 11:19:15 liuxiaoyong Exp $ */
package com.anyi.gp.bean;


public class RightBean {
  private String compoId;

  private String funcId;

  private String roleId;

  private String userId;

  private String ctrlField;

  private String granRange;

  private String revoRange;

  private String isGrant;

  /**
   * @param compoId   The compoId to set.
   * @uml.property   name="compoId"
   */
  public void setCompoId(String compoId) {
    this.compoId = compoId;
  }

  /**
   * @return   Returns the compoId.
   * @uml.property   name="compoId"
   */
  public String getCompoId() {
    return compoId;
  }

  /**
   * @param funcId   The funcId to set.
   * @uml.property   name="funcId"
   */
  public void setFuncId(String funcId) {
    this.funcId = funcId;
  }

  /**
   * @return   Returns the funcId.
   * @uml.property   name="funcId"
   */
  public String getFuncId() {
    return funcId;
  }

  /**
   * @param roleId   The roleId to set.
   * @uml.property   name="roleId"
   */
  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  /**
   * @return   Returns the roleId.
   * @uml.property   name="roleId"
   */
  public String getRoleId() {
    return roleId;
  }

  /**
   * @param ctrlField   The ctrlField to set.
   * @uml.property   name="ctrlField"
   */
  public void setCtrlField(String ctrlField) {
    this.ctrlField = ctrlField;
  }

  /**
   * @return   Returns the ctrlField.
   * @uml.property   name="ctrlField"
   */
  public String getCtrlField() {
    return ctrlField;
  }

  /**
   * @param granRange   The granRange to set.
   * @uml.property   name="granRange"
   */
  public void setGranRange(String granRange) {
    this.granRange = granRange;
  }

  /**
   * @return   Returns the granRange.
   * @uml.property   name="granRange"
   */
  public String getGranRange() {
    return granRange;
  }

  /**
   * @param revoRange   The revoRange to set.
   * @uml.property   name="revoRange"
   */
  public void setRevoRange(String revoRange) {
    this.revoRange = revoRange;
  }

  /**
   * @return   Returns the revoRange.
   * @uml.property   name="revoRange"
   */
  public String getRevoRange() {
    return revoRange;
  }

  /**
   * @param isGrant   The isGrant to set.
   * @uml.property   name="isGrant"
   */
  public void setIsGrant(String isGrant) {
    this.isGrant = isGrant;
  }

  /**
   * @return   Returns the isGrant.
   * @uml.property   name="isGrant"
   */
  public String getIsGrant() {
    return isGrant;
  }

  /**
   * @return   Returns the userId.
   * @uml.property   name="userId"
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId   The userId to set.
   * @uml.property   name="userId"
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public RightBean() {
  }
  

}
