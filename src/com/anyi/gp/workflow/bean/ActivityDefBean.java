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

  private String businessType;/* �Ѿ�ͣ�� */

  /**
   * ���̽ڵ�����������ݶ�дȨ��,key:fieldname, value:��дȨ�ޣ�0-���ɶ���1-��ʾ�ɶ�����д
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
 * ��������
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
 * ���õĲ����б�
 * @return   Ԫ������Ϊ com.anyi.erp.bean.FuncBean
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
 * ���ִ�����б� Ԫ������Ϊ String? ������Ҫ����ִ�������ͣ��粿�š���ɫ����Ա
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
 * �ύ����ŵ�ҵ�����ݵ��ĸ��ֶ���ȥ
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
 * �ύ�����Ҫ�ļ����ֶε���ʷ����
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
 * �ID
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
 * �����
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
 * ���̣�ģ�壩ID
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
 * �����
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
 * �ҵ�����ͣ������Ϊ����
 * @deprecated   �Ѿ�ͣ��
 * @uml.property   name="businessType"
 */
  public String getBusinessType() {
    return businessType;
  }

  /**
 * @deprecated   �Ѿ�ͣ��
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
