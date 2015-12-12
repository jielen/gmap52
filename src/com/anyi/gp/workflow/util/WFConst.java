//$Id: WFConst.java,v 1.5 2009/01/03 11:59:16 zhangting Exp $

package com.anyi.gp.workflow.util;

/**
 * �������õ��ĳ���
 */
public interface WFConst {
  /** �״̬ - ������ */
  int ACT_DOING = 2;
  
  /** �״̬ - ��� */
  int ACT_DONE = 3;
  
  /** ҳ���Ϲ��������ݵ� id */
  String WFDATA = "_WFDATA";
  
  /** ҳ���Ϲ�����״̬id */
  String WFSTATE = "_WFSTATE";
  
  /** ҳ���Ϲ���������id */
  String WFVARIABLE = "WF_VARIABLE";
  
  /** ҳ���Ϲ��������ݵ� id */
  String WFDATA4 = "WF_DATA";
  
  /** ҳ���Ϲ�����״̬id */
  String WFSTATE4 = "WF_STATE";
  
  /** ҳ���Ϲ���������id */
  String WFVARIABLE4 = "WF_VARIABLE";
  
  /** ����ģ��ID */
  String WF_TEMPLATE_ID = "WF_TEMPLATE_ID";
  
  /** �������� */
  String WF_TEMPLATE_TYPE = "WF_TEMPLATE_TYPE";
  
  /** ���̻���ID */
  String WF_ACTIVITY_ID = "WF_NODE_ID";
  
  String WF_NODE_ID = "WF_NODE_ID";
  
  String WF_NEXT_NODE_ID = "WF_NEXT_NODE_ID";
  
  
  /** ���̻������� */
  String WF_ACTIVITY_NAME = "WF_NODE_NAME";
  String WF_NODE_NAME = "WF_NODE_NAME";
  
  /** �û��� */
  String USER = "USER";
  
  /** ����ʵ������ */
  String NAME = "NAME";
  
  /** ����ʵ������ */
  String DESCRIPTION = "DESCRIPTION";
  
  /** ����ʵ��ID */
  String PROCESS_INST_ID = "WF_INSTANCE_ID";
  
  String WF_PROCESS_INST_ID = "WF_INSTANCE_ID";
  
  /** ������ID */
  String WORKITEM_ID = "WF_TASK_ID";
  
  String WF_WORKITEM_ID = "WF_TASK_ID";
  
  /** ACTION_NAME */
  String ACTION_NAME = "WF_ACTION";
  
  String WF_ACTION_NAME = "WF_ACTION_NAME";
  
  /** POSITION_ID */
  String POSITION_ID = "WF_POSITION_ID";
  /** ND */
  String ND = "ND";
  
  /** ��һ���Ļ */
  String NEXT_ACTIVITY = "WF_NEXT_NODE";
  
  /** ��һ�����ִ����(������) */
  String NEXT_EXECUTOR = "WF_NEXT_EXECUTOR_ID";
  
  String WF_NEXT_EXECUTOR_ID = "WF_NEXT_EXECUTOR_ID";
  String WF_NEXT_EXECUTOR_ASS_ID = "WF_NEXT_EXECUTOR_ASS_ID";
  
  String WF_CURRENT_EXECUTOR_ID = "WF_CURRENT_EXECUTOR_ID";
  String WF_CURRENT_EXECUTOR_NAME = "WF_CURRENT_EXECUTOR_NAME";
  
  /** ������ */
  String NEXT_EXECUTOR2 = "WF_NEXT_EXECUTOR_ASS_ID";
  
  /** �ύʱ��д����� */
  String COMMENT = "WF_COMMENT";
  String WF_COMMENT = "WF_COMMENT";
  
  /**���̱���*/
  String INSTANCE_NAME="WF_INSTANCE_NAME";
  
  /** �Ƿ���ύ */
  String WF_IS_BIND_COMMIT = "WF_IS_BIND_COMMIT";
  
  /** ����ʵ��ID�����ݿ��е��ֶ��� */
  String PROCESS_INST_ID_FIELD = "PROCESS_INST_ID";
  
  /** ҵ�����ݵ�����ֵ */
  String DATA_PK_VALUE = "DATA_PK_VALUE";
  
  /** �����˱�־ */
  int MASTER_EXECUTOR_FLAG = 1;
  
  /** �����˱�־ */
  int SECOND_EXECUTOR_FLAG = -1;
  
  String WF_TEMPLATE_TYPE_ID = "WF_TEMPLATE_TYPE_ID";//��������
  
  String WF_BUSINESS_TYPE = "WF_BUSINESS_TYPE";//��������
  
  String WF_PROCESS_INSTANCE_ID = "WF_INSTANCE_ID";
  
  String BY_TYPE = "BY_TYPE";
  
  String WF_TEMPLATE_NAME = "WF_TEMPLATE_NAME";
  
  String WF_TEMPLATE_DESCRIPTION = "WF_TEMPLATE_DESCRT";
  
  String WF_INSTANCE_NAME = "WF_INSTANCE_NAME";
  
  String WF_INSTANCE_STATUS = "WF_INSTANCE_STATUS";
  
  // /String WF_ACTIVITY_NAME = "WF_ACTIVITY_NAME";
  
  // /String WF_ACTION_NAME = "WF_ACTION_NAME";
  String WF_ACTION_EXECUTOR_NAME = "WF_ACTION_EXECUTOR_NAME";
  
  String WF_ACTION_EXECUTE_TIME = "WF_ACTION_EXECUTE_TIME";
  
  String WF_ACTION_DESCRIPTION = "WF_ACTION_DESCRIPTION";
  
  String WF_INSTANCE_DESCRIPTION = "WF_INSTANCE_DESCRIPTION";
  
  String WF_INSTANCE_OWNER = "WF_INSTANCE_OWNER";
  
  String WF_INSTANCE_OWNER_NAME = "WF_INSTANCE_OWNER_NAME";
  
  String WF_INSTANCE_START_TIME = "WF_INSTANCE_START_TIME";
  
  String WF_INSTANCE_END_TIME = "WF_INSTANCE_END_TIME";
  
  String WF_TASK_OWNER_NAME = "WF_TASK_OWNER_NAME";
  
  String WF_TASK_CREATOR_NAME = "WF_TASK_CREATOR_NAME";
  
  String WF_TASK_CREATE_TIME = "WF_TASK_CREATE_TIME";
  
  String WF_LIMIT_TIME = "WF_LIMIT_TIME";
  
  String WF_REMIND_TIME = "WF_REMIND_TIME";// ����������ʱ�䣬��ֵΪ�������Ҫ���ڵ�Сʱ��
  
  // /String WF_TEMPLATE_ID = "WF_TEMPLATE_ID";
  String WF_INSTANCE_ID = "WF_INSTANCE_ID";// ������PROCESS_INST_ID�ظ�����
  
  String WF_TASK_ID = "WF_TASK_ID";
  
  String WF_TASK_RESPONSIBILITY = "WF_TASK_RESPONSIBILITY";
  String WF_EXECUTOR_RESPONSIBILITY = "WF_EXECUTOR_RESPONSIBILITY";
  
  String WF_EXCLUDE_WF_CONDITION = "EXCLUDE_WF_CONDITION";
  
  // /String WF_ACTIVITY_ID = "WF_ACTIVITY_ID";
  // execute method /*add by xjh*/
  String WF_SOLO_NODE = "0";
  
  String WF_PARALLEL_NODE = "1";
  
  String WF_SERIAL_NODE = "2";
  
  // int ANYTEMPLATE = 0;//������ģ��id
  
  /* ���ӱ�ʾ����������״̬�ĳ��� */
  String WF_WORKLIST_TYPE = "WF_WORKLIST_TYPE";// ȡֵ����
  
  String WF_WORKLIST_TYPE_IMG_SRC = "WF_WORKLIST_TYPE_IMG_SRC";
  
  String MSG_COLLECTED_TASK = "�ѻ��ܴ�������";
  
  Object MSG_TOCOLLECT_TASK = "�����ܴ�������";
  
  //������ѡ��Ի����еĹ��ܳ���
  String WF_FUNC_MANUALCOMMIT="0";
  String WF_FUNC_SHOWOPTION="1";
  String WF_FUNC_GIVEBACK="2";
  String WF_FUNC_CALLBACK="3";
  String WF_FUNC_TRANSFER="4"; 
  String WF_FUNC_HANDOVER="5"; 
  String WF_FUNC_IMPOWER="6";
  
  String WF_FUNC_ACTIVE="8";
  String WF_FUNC_DEACTIVE="9";
  String WF_FUNC_INTERRUPT="10";
  String WF_FUNC_DELETEFLOW="11";
  String WF_FUNC_SHOWINSTANCETRACE="12";
  
  
  String WF_SCRIPT_FILE= "gp.workflow.WFPage4;"+
  "gp.workflow.WFData;gp.workflow.WFInterface;" +
  "gp.workflow.WFConst;gp.workflow.WFDataTools4";
  String WF_COMPONAME_TODO= "WF_TODO";
  
  String WF_FUNCNAME_BUSINESS_RESTORE = "fbusinessrestore";
  
  
  String WF_FUNCNAME_FAUTOCOMMIT = "fautocommit";
  
  String WF_FUNCNAME_FAUTOCOMMIT_COMMENT = "fautocommit_comment";
  
  String WF_FUNCNAME_FMANUALCOMMIT = "fmanualcommit";
  
  String WF_FUNCNAME_COMMIT = "fautocommit";
  
  static int INSTANCE_VALID_STATUS = 1;
  
  static int INSTANCE_INVALID_STATUS = -1;
  
  static int INSTANCE_VALID_ORNOT_STATUS = 0;
  
  static String WF_DRAFT_TYPE = "WF_COMPO_DRAFT";
  static String WF_TODO_TYPE = "WF_FILTER_COMPO_TODO";
  static String WF_DONE_TYPE = "WF_FILTER_COMPO_DONE";
  
  static String WF_BRIEF = "WF_BRIEF";
  static String WF_CONDITION = "WF_CONDITION";
  static String WF_PAGE_URL = "WF_PAGE_URL";
  static String WF_PAGE_TITLE = "WF_PAGE_TITLE";
  
  static String WF_CREATE_TIME          = "WF_CREATE_TIME";
  static String WF_REMIND_EXECUTE_TERM  = "WF_REMIND_EXECUTE_TERM";
  static String WF_EXECUTE_TIME         = "WF_EXECUTE_TIME";
  static String WF_LIMIT_EXECUTE_TIME   = "WF_LIMIT_EXECUTE_TIME";  
  static String WF_CREATOR_NAME         = "WF_CREATOR_NAME";
  
}
