package com.anyi.gp.workflow.bean;

import java.util.List;

/**
 * @author   leidaohong
 * @time  2005-3-25
 */
public class CompoDataRestoreBean {
  private String compo_id;

  private List lsRowManagerData;

  private String mainTab_KeyCondition;/* ��ǰ������������������� */

  private boolean includeInsertAction;/* ��ǰ������ĳ���ڵ��е��й������������Ƿ����������� */

  /**
   * @param compo_id
   * @param lsRowManagerDataPerNode
   * @param mainTab_KeyCondition
   *          �����������ݵ�������������
   */
  public CompoDataRestoreBean(String compo_id, List lsRowManagerDataPerNode,
      String mainTab_KeyCondition, boolean includeInsertAction) {
    this.compo_id = compo_id;
    this.lsRowManagerData = lsRowManagerDataPerNode;
    this.mainTab_KeyCondition = mainTab_KeyCondition;
    this.includeInsertAction = includeInsertAction;
  }

  /**
 * @return   Returns the strRMDInsert.
 * @uml.property   name="mainTab_KeyCondition"
 */
  public String getMainTab_KeyCondition() {
    return mainTab_KeyCondition;
  }

  /**
 * @param strRMDInsert   The strRMDInsert to set.
 * @uml.property   name="mainTab_KeyCondition"
 */
  public void setMainTab_KeyCondition(String mainTab_KeyCondition) {
    this.mainTab_KeyCondition = mainTab_KeyCondition;
  }

  /**
 * @return   Returns the bInsertAction.
 * @uml.property   name="includeInsertAction"
 */
  public boolean isIncludeInsertAction() {
    return includeInsertAction;
  }

  /**
 * @param insertAction   The bInsertAction to set.
 * @uml.property   name="includeInsertAction"
 */
  public void setIncludeInsertAction(boolean insertAction) {
    includeInsertAction = insertAction;
  }

  /**
 * @return   Returns the compo_id.
 * @uml.property   name="compo_id"
 */
  public String getCompo_id() {
    return compo_id;
  }

  /**
 * @param compo_id   The compo_id to set.
 * @uml.property   name="compo_id"
 */
  public void setCompo_id(String compo_id) {
    this.compo_id = compo_id;
  }

  /**
   * @return Returns the lsRowManagerData.
   */
  public List getRowManagerData() {
    return lsRowManagerData;
  }

  /**
   * @param lsRowManagerData
   *          The lsRowManagerData to set.
   */
  public void setRowManagerData(List lsRowManagerData) {
    this.lsRowManagerData = lsRowManagerData;
  }
}
