//$Id: WFConst.java,v 1.5 2009/01/03 11:59:16 zhangting Exp $

package com.anyi.gp.workflow.util;

/**
 * 工作流用到的常量
 */
public interface WFConst {
  /** 活动状态 - 进行中 */
  int ACT_DOING = 2;
  
  /** 活动状态 - 完成 */
  int ACT_DONE = 3;
  
  /** 页面上工作流数据的 id */
  String WFDATA = "_WFDATA";
  
  /** 页面上工作流状态id */
  String WFSTATE = "_WFSTATE";
  
  /** 页面上工作流变量id */
  String WFVARIABLE = "WF_VARIABLE";
  
  /** 页面上工作流数据的 id */
  String WFDATA4 = "WF_DATA";
  
  /** 页面上工作流状态id */
  String WFSTATE4 = "WF_STATE";
  
  /** 页面上工作流变量id */
  String WFVARIABLE4 = "WF_VARIABLE";
  
  /** 流程模板ID */
  String WF_TEMPLATE_ID = "WF_TEMPLATE_ID";
  
  /** 流程类型 */
  String WF_TEMPLATE_TYPE = "WF_TEMPLATE_TYPE";
  
  /** 流程环节ID */
  String WF_ACTIVITY_ID = "WF_NODE_ID";
  
  String WF_NODE_ID = "WF_NODE_ID";
  
  String WF_NEXT_NODE_ID = "WF_NEXT_NODE_ID";
  
  
  /** 流程环节名称 */
  String WF_ACTIVITY_NAME = "WF_NODE_NAME";
  String WF_NODE_NAME = "WF_NODE_NAME";
  
  /** 用户名 */
  String USER = "USER";
  
  /** 流程实例名称 */
  String NAME = "NAME";
  
  /** 流程实例描述 */
  String DESCRIPTION = "DESCRIPTION";
  
  /** 流程实例ID */
  String PROCESS_INST_ID = "WF_INSTANCE_ID";
  
  String WF_PROCESS_INST_ID = "WF_INSTANCE_ID";
  
  /** 工作项ID */
  String WORKITEM_ID = "WF_TASK_ID";
  
  String WF_WORKITEM_ID = "WF_TASK_ID";
  
  /** ACTION_NAME */
  String ACTION_NAME = "WF_ACTION";
  
  String WF_ACTION_NAME = "WF_ACTION_NAME";
  
  /** POSITION_ID */
  String POSITION_ID = "WF_POSITION_ID";
  /** ND */
  String ND = "ND";
  
  /** 下一步的活动 */
  String NEXT_ACTIVITY = "WF_NEXT_NODE";
  
  /** 下一步活动的执行者(主办人) */
  String NEXT_EXECUTOR = "WF_NEXT_EXECUTOR_ID";
  
  String WF_NEXT_EXECUTOR_ID = "WF_NEXT_EXECUTOR_ID";
  String WF_NEXT_EXECUTOR_ASS_ID = "WF_NEXT_EXECUTOR_ASS_ID";
  
  String WF_CURRENT_EXECUTOR_ID = "WF_CURRENT_EXECUTOR_ID";
  String WF_CURRENT_EXECUTOR_NAME = "WF_CURRENT_EXECUTOR_NAME";
  
  /** 辅办人 */
  String NEXT_EXECUTOR2 = "WF_NEXT_EXECUTOR_ASS_ID";
  
  /** 提交时填写的意见 */
  String COMMENT = "WF_COMMENT";
  String WF_COMMENT = "WF_COMMENT";
  
  /**流程标题*/
  String INSTANCE_NAME="WF_INSTANCE_NAME";
  
  /** 是否绑定提交 */
  String WF_IS_BIND_COMMIT = "WF_IS_BIND_COMMIT";
  
  /** 流程实例ID在数据库中的字段名 */
  String PROCESS_INST_ID_FIELD = "PROCESS_INST_ID";
  
  /** 业务数据的主键值 */
  String DATA_PK_VALUE = "DATA_PK_VALUE";
  
  /** 主办人标志 */
  int MASTER_EXECUTOR_FLAG = 1;
  
  /** 辅办人标志 */
  int SECOND_EXECUTOR_FLAG = -1;
  
  String WF_TEMPLATE_TYPE_ID = "WF_TEMPLATE_TYPE_ID";//废弃不用
  
  String WF_BUSINESS_TYPE = "WF_BUSINESS_TYPE";//废弃不用
  
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
  
  String WF_REMIND_TIME = "WF_REMIND_TIME";// 任务到期提醒时间，其值为任务距离要到期的小时数
  
  // /String WF_TEMPLATE_ID = "WF_TEMPLATE_ID";
  String WF_INSTANCE_ID = "WF_INSTANCE_ID";// ？？与PROCESS_INST_ID重复？？
  
  String WF_TASK_ID = "WF_TASK_ID";
  
  String WF_TASK_RESPONSIBILITY = "WF_TASK_RESPONSIBILITY";
  String WF_EXECUTOR_RESPONSIBILITY = "WF_EXECUTOR_RESPONSIBILITY";
  
  String WF_EXCLUDE_WF_CONDITION = "EXCLUDE_WF_CONDITION";
  
  // /String WF_ACTIVITY_ID = "WF_ACTIVITY_ID";
  // execute method /*add by xjh*/
  String WF_SOLO_NODE = "0";
  
  String WF_PARALLEL_NODE = "1";
  
  String WF_SERIAL_NODE = "2";
  
  // int ANYTEMPLATE = 0;//不限制模板id
  
  /* 增加表示工作流任务状态的常量 */
  String WF_WORKLIST_TYPE = "WF_WORKLIST_TYPE";// 取值如下
  
  String WF_WORKLIST_TYPE_IMG_SRC = "WF_WORKLIST_TYPE_IMG_SRC";
  
  String MSG_COLLECTED_TASK = "已汇总待办任务";
  
  Object MSG_TOCOLLECT_TASK = "待汇总待办任务";
  
  //工作流选项对话框中的功能常量
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
