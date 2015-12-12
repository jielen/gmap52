// $Id: WorkitemBean.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.bean;

import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;


/**
 * @author   leidaohong
 */
public class WorkitemBean extends AbstractWFEntity {

  private Object processInstId;

  private Object workitemId;

  private Object templateId;

  private Object activityId;

  private int activityState;

  private String executor;

  private String executorName;

  private Object executeTime;

  private String comment;

  private Object responsibility;

  private String instanceName;

  private String instanceDescription;

  private String activityName;

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
 * ����ʵ��ID(������ID)
 * @uml.property   name="workitemId"
 */
  public Object getWorkitemId() {
    return workitemId;
  }

  /**
 * @param workitemId   The workitemId to set.
 * @uml.property   name="workitemId"
 */
public void setWorkitemId(Object workitemId) {
	this.workitemId = workitemId;
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
 * ���̻(����)ID
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
 * �״̬(ֻ��)��ȡֵΪ WFConst.ACT_DOING �� WFConst.ACT_DONE
 * @uml.property   name="activityState"
 */
  public int getActivityState() {
    return activityState;
  }

  /**
 * @param activityState   The activityState to set.
 * @uml.property   name="activityState"
 */
public void setActivityState(int activityState) {
	this.activityState = activityState;
}

  /**
 * ִ����(�ύ��)(ֻ��)
 * @uml.property   name="executor"
 */
  public String getExecutor() {
    return executor;
  }

  /**
 * @param executor   The executor to set.
 * @uml.property   name="executor"
 */
public void setExecutor(String executor) {
	this.executor = executor;
}

  /**
 * ִ��ʱ��(�ύʱ��)(ֻ��)
 * @uml.property   name="executeTime"
 */
  public Object getExecuteTime() {
    return executeTime;
  }

  /**
 * @param executeTime   The executeTime to set.
 * @uml.property   name="executeTime"
 */
public void setExecuteTime(Object executeTime) {
	this.executeTime = executeTime;
}

  /**
 * �ύʱ�����(ֻ��)
 * @uml.property   name="comment"
 */
  public String getComment() {
    return comment;
  }

  /**
 * @param comment   The comment to set.
 * @uml.property   name="comment"
 */
public void setComment(String comment) {
	this.comment = comment;
}

  /** ȱʡ���췽�� */
  public WorkitemBean() {
    activityState = WFConst.ACT_DOING;
  }

  /*
   * �� TableData ��ʼ��<p> ע�⣺������ td �ж�ȡ�ID������ʵ��ID��������ID������ȡ����ģ��ID��ִ���ߵ���Ϣ��
   * ��Ϊ���������Ҫ�����ڴӲ����ж�ȡ�����֮��Ӧ���� WFService.findWorkitem �ؽ� @param td ����
   * WFConst.WF_ACTIVITY_ID, PROCESS_INST_ID, WORKITEM_ID�� �ֱ��ʾ�ID������ʵ��ID��������ID /
   * public WorkitemBean(TableData td) { activityState = WFConst.ACT_DOING;
   *
   * activityId = td.getField(WFConst.WF_ACTIVITY_ID); processInstId =
   * td.getField(WFConst.PROCESS_INST_ID); workitemId =
   * td.getField(WFConst.WORKITEM_ID); }
   */

  /**
   * ����������������� TableData ��
   * <p>
   * ֻ��� WFConst.WF_TEMPLATE_ID, WF_ACTIVITY_ID, PROCESS_INST_ID, WORKITEM_ID��
   * �ֱ��ʾ����ģ��ID���ID������ʵ��ID��������ID
   *
   * @param td
   *          [�������] ��Ҫ��ŵ��� TableData
   */

  public void saveToTableData(TableData td) {
    td.setField(WFConst.WF_TEMPLATE_ID, getTemplateId());
    td.setField(WFConst.WF_NODE_ID, getActivityId());
    td.setField(WFConst.WF_INSTANCE_ID, getProcessInstId());
    td.setField(WFConst.WF_TASK_ID, getWorkitemId());
    td.setField(WFConst.WF_EXECUTOR_RESPONSIBILITY, getResponsibility());
    td.setField(WFConst.WF_INSTANCE_NAME, getInstanceName());
    td.setField(WFConst.WF_INSTANCE_DESCRIPTION, getInstanceDescription());

    //collectCommit, commitͳһ��ȥ�����¶����field
    td.setField(WFConst.WF_ACTIVITY_ID, getActivityId());
    td.setField(WFConst.PROCESS_INST_ID, getProcessInstId());
    td.setField(WFConst.WORKITEM_ID, getWorkitemId());
    td.setField(WFConst.WF_TASK_RESPONSIBILITY, getResponsibility());
  }

  /**
 * @return   Returns the responsibility.
 * @uml.property   name="responsibility"
 */
public Object getResponsibility() {
	return responsibility;
}

  /**
 * @return   Returns the instanceName.
 * @uml.property   name="instanceName"
 */
public String getInstanceName() {
	return instanceName;
}

  /**
 * @return   Returns the instanceDescription.
 * @uml.property   name="instanceDescription"
 */
public String getInstanceDescription() {
	return instanceDescription;
}

  /**
 * @param responsibility   The responsibility to set.
 * @uml.property   name="responsibility"
 */
public void setResponsibility(Object responsibility) {
	this.responsibility = responsibility;
}

  /**
 * @param instanceName   The instanceName to set.
 * @uml.property   name="instanceName"
 */
public void setInstanceName(String instanceName) {
	this.instanceName = instanceName;
}

  /**
 * @param instanceDescription   The instanceDescription to set.
 * @uml.property   name="instanceDescription"
 */
public void setInstanceDescription(String instanceDescription) {
	this.instanceDescription = instanceDescription;
}

  /**
 * @return   Returns the activityName.
 * @uml.property   name="activityName"
 */
public String getActivityName() {
	return activityName;
}

  /**
 * @param activityName   The activityName to set.
 * @uml.property   name="activityName"
 */
public void setActivityName(String activityName) {
	this.activityName = activityName;
}

  /**
 * @return   Returns the executorName.
 * @uml.property   name="executorName"
 */
public String getExecutorName() {
	return executorName;
}

  /**
 * @param executorName   The executorName to set.
 * @uml.property   name="executorName"
 */
public void setExecutorName(String executorName) {
	this.executorName = executorName;
}
}
