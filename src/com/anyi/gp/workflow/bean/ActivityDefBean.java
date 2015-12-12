// $Id: ActivityDefBean.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.bean;

import java.util.List;

import com.anyi.gp.bean.FuncBean;
import com.anyi.gp.debug.Debug;



/**
 * @author   leidaohong
 */
public class ActivityDefBean extends AbstractWFEntity {
  private String compoName;

  private List functions;

  private List executorList;

  private String commentFieldName;

  private String dataFields;
  
  private Object activityId;

  private String name;

  private Object templateId;

  private String description;

  private String businessType;/* 已经停用 */

  /**
   * 流程节点所定义的数据读写权限,key:fieldname, value:读写权限，0-不可读，1-表示可读不可写
   */
  private List nodeDefineFieldAcessList;

  /**
 * @return   Returns the nodeDefineFieldAcessMap.
 * @uml.property   name="nodeDefineFieldAcessList"
 */
  public List getNodeDefineFieldAcessList() {
    return nodeDefineFieldAcessList;
  }

  /**
 * @param nodeDefineFieldAcessMap   The nodeDefineFieldAcessMap to set.
 * @uml.property   name="nodeDefineFieldAcessList"
 */
  public void setNodeDefineFieldAcessList(List nodeDefineFieldAcessList) {
    this.nodeDefineFieldAcessList = nodeDefineFieldAcessList;
  }

  /**
 * 部件名称
 * @uml.property   name="compoName"
 */
  public String getCompoName() {
    return compoName;
  }

  /**
 * @param compoName   The compoName to set.
 * @uml.property   name="compoName"
 */
public void setCompoName(String compoName) {
  this.compoName = compoName;
}

  /**
 * 可用的操作列表
 * @return   元素类型为 com.anyi.erp.bean.FuncBean
 * @uml.property   name="functions"
 */
  public List getFunctions() {
    return functions;
  }

  /**
 * @param functions   The functions to set.
 * @uml.property   name="functions"
 */
public void setFunctions(List functions) {
  Debug.assertTrue(null == functions || 0 == functions.size()
      || functions.get(0) instanceof FuncBean);
  this.functions = functions;
}

  /**
 * 活动的执行人列表 元素类型为 String? 还是需要标明执行人类型，如部门、角色、人员
 * @uml.property   name="executorList"
 */
  public List getExecutorList() {
    return executorList;
  }

  /**
 * @param executorList   The executorList to set.
 * @uml.property   name="executorList"
 */
public void setExecutorList(List executorList) {
  this.executorList = executorList;
}

  /**
 * 提交意见放到业务数据的哪个字段中去
 * @uml.property   name="commentFieldName"
 */
  public String getCommentFieldName() {
    return commentFieldName;
  }

  /**
 * @param commentFieldName   The commentFieldName to set.
 * @uml.property   name="commentFieldName"
 */
public void setCommentFieldName(String commentFieldName) {
  this.commentFieldName = commentFieldName;
}
/**
 * 提交意见需要哪几个字段的历史数据
 * @uml.property   name="commentFieldName"
 */
  public String getDataFields() {
    return dataFields;
  }

  /**
 * @param commentFieldName   The commentFieldName to set.
 * @uml.property   name="commentFieldName"
 */
public void setDataFields(String dataFields) {
    this.dataFields = dataFields;
}
  /**
 * 活动ID
 * @uml.property   name="activityId"
 */
  public Object getActivityId() {
    return activityId;
  }

  /**
 * @param activityId   The activityId to set.
 * @uml.property   name="activityId"
 */
public void setActivityId(Object activityId) {
  this.activityId = activityId;
}

  /**
 * 活动名称
 * @uml.property   name="name"
 */
  public String getName() {
    return name;
  }

  /**
 * @param name   The name to set.
 * @uml.property   name="name"
 */
public void setName(String name) {
  this.name = name;
}

  /**
 * 流程（模板）ID
 * @uml.property   name="templateId"
 */
  public Object getTemplateId() {
    return templateId;
  }

  /**
 * @param templateId   The templateId to set.
 * @uml.property   name="templateId"
 */
public void setTemplateId(Object templateId) {
  this.templateId = templateId;
}

  /**
 * 活动描述
 * @uml.property   name="description"
 */
  public String getDescription() {
    return description;
  }

  /**
 * @param description   The description to set.
 * @uml.property   name="description"
 */
public void setDescription(String description) {
  this.description = description;
}

  /**
 * 活动业务类型，或理解为活动类别
 * @deprecated   已经停用
 * @uml.property   name="businessType"
 */
  public String getBusinessType() {
    return businessType;
  }

  /**
 * @deprecated   已经停用
 * @param businessType
 * @uml.property   name="businessType"
 */
  public void setBusinessType(String businessType) {
    this.businessType = businessType;
  }

/**
 * @return
 */
public boolean isAlwayShowFirstNodeCompo() {
    // TCJLODO Auto-generated method stub
    return false;
}
/**
 * @return
 */
public boolean setAlwayShowFirstNodeCompo() {
    // TCJLODO Auto-generated method stub
    return false;
}

}
