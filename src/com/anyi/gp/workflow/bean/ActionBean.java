// $Id: ActionBean.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.bean;

import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;



/**
 * @author   leidaohong
 */
public class ActionBean extends AbstractWFEntity {

  private Object nextActivity;

  private String nextExecutor;

  private String nextExecutor2;

  private String comment;

  private String actionId;

  private String positionId;

  /**
 * ��һ���
 * @uml.property   name="nextActivity"
 */
  public Object getNextActivity() {
    return nextActivity;
  }

  /**
 * @param nextActivity   The nextActivity to set.
 * @uml.property   name="nextActivity"
 */
public void setNextActivity(Object nextActivity) {
	this.nextActivity = nextActivity;
}

  /**
   * ��һ�����ִ�����б� Ԫ������Ϊ String? ������Ҫ����ִ�������ͣ��粿�š���ɫ����Ա
   */
  /*
   * public List getNextExecutorList() { return nextExecutorList; }
   * 
   * public void setNextExecutorList(List nextExecutorList) {
   * this.nextExecutorList = nextExecutorList; }
   */
  /**
 * ��һ�����ִ�����б� Ԫ������Ϊ String? ������Ҫ����ִ�������ͣ��粿�š���ɫ����Ա
 * @uml.property   name="nextExecutor"
 */
  public String getNextExecutor() {
    return nextExecutor;
  }

  /**
 * @param nextExecutor   The nextExecutor to set.
 * @uml.property   name="nextExecutor"
 */
public void setNextExecutor(String nextExecutor) {
	this.nextExecutor = nextExecutor;
}

  /**
 * ��һ����ĸ�����
 * @uml.property   name="nextExecutor2"
 */
  public String getNextExecutor2() {
    return nextExecutor2;
  }

  /**
 * @param nextExecutor2   The nextExecutor2 to set.
 * @uml.property   name="nextExecutor2"
 */
public void setNextExecutor2(String nextExecutor2) {
	this.nextExecutor2 = nextExecutor2;
}

  /**
 * �ύʱ�����
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

  /**
 * ActionName
 * @uml.property   name="actionId"
 */
  public String getActionId() {
    return actionId;
  }

  /**
 * @param actionId   The actionId to set.
 * @uml.property   name="actionId"
 */
public void setActionId(String actionId) {
	this.actionId = actionId;
}

  /**
 * PositionId
 * @uml.property   name="positionId"
 */
  public String getPositionId() {
    return positionId;
  }

  /**
 * @param positionId   The positionId to set.
 * @uml.property   name="positionId"
 */
public void setPositionId(String positionId) {
	this.positionId = positionId;
}

  /** ȱʡ���췽�� */
  public ActionBean() {
  }

  /** �� TableData ��ʼ�� */
  public ActionBean(TableData td) {
    comment = td.getFieldValue(WFConst.COMMENT);
    nextActivity = td.getFieldValue(WFConst.NEXT_ACTIVITY);
    nextExecutor = td.getFieldValue(WFConst.NEXT_EXECUTOR);
    nextExecutor2 = td.getFieldValue(WFConst.NEXT_EXECUTOR2);
    actionId = td.getFieldValue(WFConst.ACTION_NAME);
    positionId = td.getFieldValue(WFConst.POSITION_ID);
  }
}
