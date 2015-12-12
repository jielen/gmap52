/*
 * history
 * date			author		description
 * 20041025	zhangc		���ӹ��������
 */
package com.anyi.gp.workflow.util;

/**
 * <p>
 * Title: ��������������
 * </p>
 * <p>
 * Description: �������¹������������ͣ����졢�Ѱ졢֧�ֹ������Ĳ���
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author xiary
 * @version 1.0
 */

/*
 * ��v5.0��ʼ��ֻ��7���б�����WF_COMPO, WF_COMPO_OTHER, WF_FILTER_COMPO,
 * WF_FILTER_COMPO_TODO, WF_FILTER_COMPO_DONE, WF_COMPO_DRAFT,
 * WF_FILTER_COMPO_INVALID.
 * ����WF_TODO��WF_DONE�����б�ʱҲ������Ҫ�л�componame.
 * wflisttype�����ȴ�queryParameter��ȡ�����û�У����attribute,
 * �����û�����meta��ȡ�������û�У�
 * ��ȡĬ��ֵWF_COMPO
 */
public interface WFCompoType {

  /** �������� */
  String WF_TODO = "WF_TODO";//��v5.0��ʼ����

  /** �Ѱ����� */
  String WF_DONE = "WF_DONE";//��v5.0��ʼ����
  
  /** ��ѯ�ַ���������*/
  String WF_COMPO_LIST_TYPE="listtype";
  
  /** ֧�ֹ������Ĳ���.����������ʽ��ʾ�б�
   * �������Ǹ��û��Ĵ�����Ѱ�����ļ��� 
   * modified by zhanggh*/
  String WF_FILTER_COMPO = "WF_FILTER_COMPO";
  
  /**��WF_FILTER_COMPO���ƣ���ֻ��ʾ����*/
  String WF_FILTER_COMPO_TODO = "WF_FILTER_COMPO_TODO";
  
  /**��WF_FILTER_COMPO���ƣ���ֻ��ʾ�Ѱ�*/
  String WF_FILTER_COMPO_DONE = "WF_FILTER_COMPO_DONE";
  
  /** ����ֹ(����)�����̡�״ֵ̬Ϊ-9 */
  String WF_FILTER_COMPO_INVALID = "WF_FILTER_COMPO_INVALID";
  
  /**ֻ��ʾ�ݸ�*/
  String WF_COMPO_DRAFT = "WF_COMPO_DRAFT";
  
  /** ֧�ֹ������Ĳ���.��ȫ����������ʽ��ʾ�б�
   * �����¼�Ƿ�ɼ���ҵ���Ŷ��壬��ִ�й�������صĹ��� 
   * modified by zhanggh*/
  String WF_COMPO = "WF_COMPO";
  
  /** ������Ŀǰ��WF_COMPO����*/
  String WF_COMPO_OTHER = "WF_COMPO_OTHER";

  
  /** ������ģ�� */
  String WF_TEMPLATE = "WF_TEMPLATE";

  /** ����������� */
  String WF_ACTION = "WF_ACTION";

  /** ��������ʵ������ */
  String AS_WF_INSTANCE_TRACE = "WF_INSTANCE_TRACE";

  /** ���������� */
  String AS_WF_DEFINE = "WF_DEFINE";

  /** ��������� */
  String WF_WATCH = "WF_WATCH";


}
