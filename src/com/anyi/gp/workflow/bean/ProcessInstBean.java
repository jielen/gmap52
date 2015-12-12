// $Id: ProcessInstBean.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.bean;

import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;

/**
 * @author   leidaohong
 */
public class ProcessInstBean extends AbstractWFEntity {

  private Object processInstId;

  private String templateType;

  private Object templateId;

  private String name;

  private String description;

  /**
 * ����ʵ��ID
 * @uml.property   name="processInstId"
 */
  public Object getProcessInstId() {
    return processInstId;
  }

  /**
 * @param processInstId   The processInstId to set.
 * @uml.property   name="processInstId"
 */
public void setProcessInstId(Object processInstId) {
	this.processInstId = processInstId;
}

  /**
 * ��������
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
 * ����ID
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
 * ����ʵ������
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
 * ����ʵ������
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

  /** ȱʡ���췽�� */
  public ProcessInstBean() {
  }

  /** �� TableData ��ʼ�� */
  public ProcessInstBean(TableData td) {
    templateId = td.getField(WFConst.WF_TEMPLATE_ID);
    processInstId = td.getField(WFConst.WF_INSTANCE_ID);
    name = td.getFieldValue(WFConst.NAME);
    description = td.getFieldValue(WFConst.DESCRIPTION);
  }

}
