// $Id: ProcessDefBean.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.bean;

import java.util.ArrayList;
import java.util.List;


/**
 * @author   leidaohong
 */
public class ProcessDefBean extends AbstractWFEntity {
  private String templateType;

  private Object templateId;

  private String name;

  /** 绑定的工作流变量信息列表 */
  private List bindVariableInfo;

  /** 绑定的工作流状态列表 */
	private List bindStateInfo;

  /**
   * 
   */
  public ProcessDefBean() {
    super();
    bindVariableInfo = new ArrayList();
    bindStateInfo = new ArrayList();
  }

  /**
 * @return   Returns the bindVariableInfo.
 * @uml.property   name="bindVariableInfo"
 */
  public List getBindVariableInfo() {
    return bindVariableInfo;
  }

  /**
 * @param bindVariableInfo   The bindVariableInfo to set.
 * @uml.property   name="bindVariableInfo"
 */
  public void setBindVariableInfo(List bindVariableInfo) {
    this.bindVariableInfo = bindVariableInfo;
  }

  /**
   * @param bindVariableInfo
   *          The bindVariableInfo to set.
   */
  public void addBindVariableInfo(VariableInfo v) {
    this.bindVariableInfo.add(v);
  }

  /**
 * 流程类型
 * @uml.property   name="templateType"
 */
  public String getTemplateType() {
    return templateType;
  }

  /**
 * @param templateType   The templateType to set.
 * @uml.property   name="templateType"
 */
public void setTemplateType(String templateType) {
	this.templateType = templateType;
}

  /**
 * 流程ID
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
 * 流程名称
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
 * @return
 * @uml.property   name="bindStateInfo"
 */
public List getBindStateInfo() {
    // TCJLODO Auto-generated method stub
    return this.bindStateInfo;
}
/**
 * @param bindStateInfo   The bindStateInfo to set.
 * @uml.property   name="bindStateInfo"
 */
public List setBindStateInfo(List lstBindStateInfo) {
	// TCJLODO Auto-generated method stub
	return this.bindStateInfo = lstBindStateInfo;
}
public void addBindStateInfo(BindStateInfo x) {
    // TCJLODO Auto-generated method stub
    this.bindStateInfo.add(x);
}

}
