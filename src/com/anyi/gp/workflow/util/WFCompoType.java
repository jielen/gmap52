/*
 * history
 * date			author		description
 * 20041025	zhangc		增加工作流监控
 */
package com.anyi.gp.workflow.util;

/**
 * <p>
 * Title: 工作流部件类型
 * </p>
 * <p>
 * Description: 描述以下工作流部件类型：待办、已办、支持工作流的部件
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
 * 从v5.0开始，只有7种列表类型WF_COMPO, WF_COMPO_OTHER, WF_FILTER_COMPO,
 * WF_FILTER_COMPO_TODO, WF_FILTER_COMPO_DONE, WF_COMPO_DRAFT,
 * WF_FILTER_COMPO_INVALID.
 * 弃用WF_TODO和WF_DONE。在列表时也不再需要切换componame.
 * wflisttype将首先从queryParameter中取，如果没有，则从attribute,
 * 如果仍没有则从meta中取，如果仍没有，
 * 则取默认值WF_COMPO
 */
public interface WFCompoType {

  /** 待办事宜 */
  String WF_TODO = "WF_TODO";//从v5.0开始弃用

  /** 已办事宜 */
  String WF_DONE = "WF_DONE";//从v5.0开始弃用
  
  /** 查询字符串的名字*/
  String WF_COMPO_LIST_TYPE="listtype";
  
  /** 支持工作流的部件.按部件的形式显示列表，
   * 但内容是该用户的待办和已办任务的集合 
   * modified by zhanggh*/
  String WF_FILTER_COMPO = "WF_FILTER_COMPO";
  
  /**与WF_FILTER_COMPO类似，但只显示代办*/
  String WF_FILTER_COMPO_TODO = "WF_FILTER_COMPO_TODO";
  
  /**与WF_FILTER_COMPO类似，但只显示已办*/
  String WF_FILTER_COMPO_DONE = "WF_FILTER_COMPO_DONE";
  
  /** 被中止(作废)的流程。状态值为-9 */
  String WF_FILTER_COMPO_INVALID = "WF_FILTER_COMPO_INVALID";
  
  /**只显示草稿*/
  String WF_COMPO_DRAFT = "WF_COMPO_DRAFT";
  
  /** 支持工作流的部件.完全按部件的形式显示列表，
   * 具体记录是否可见由业务部门定义，不执行工作流相关的过滤 
   * modified by zhanggh*/
  String WF_COMPO = "WF_COMPO";
  
  /** 其他，目前按WF_COMPO处理*/
  String WF_COMPO_OTHER = "WF_COMPO_OTHER";

  
  /** 工作流模板 */
  String WF_TEMPLATE = "WF_TEMPLATE";

  /** 工作流活动动作 */
  String WF_ACTION = "WF_ACTION";

  /** 工作流程实例跟踪 */
  String AS_WF_INSTANCE_TRACE = "WF_INSTANCE_TRACE";

  /** 工作流定义 */
  String AS_WF_DEFINE = "WF_DEFINE";

  /** 工作流监控 */
  String WF_WATCH = "WF_WATCH";


}
