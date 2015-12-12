/* $Id: WFService.java,v 1.14 2008/10/09 01:03:41 dingyy Exp $ */
package com.anyi.gp.workflow;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.workflow.bean.ActionBean;
import com.anyi.gp.workflow.bean.ActivityDefBean;
import com.anyi.gp.workflow.bean.ProcessDefBean;
import com.anyi.gp.workflow.bean.ProcessInstBean;
import com.anyi.gp.workflow.bean.WorkitemBean;
import com.anyi.gp.workflow.util.WFException;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.exception.WorkflowException;

/**
 * 工作流所有操作都放在这个接口里
 */

public interface WFService {

	// /取所有活动工作流模板
	List getAllActiveTemplate() throws WFException;

	/**
	 * 根据工作流实例获取部件id
	 * 
	 * @param instanceId
	 *            工作流实例id
	 * @return 工作流对应的部件id，
	 * @throws
	 * @see
	 */
	public List getCompoIdByInstanceId(int instanceId) throws WFException;

	/**
	 * 根据工作流实例以及环节获取部件id
	 * 
	 * @param instanceId
	 *            工作流实例id
	 * @param nodeId
	 *            工作流环节id
	 * @return 工作流对应的部件id，
	 * @throws
	 * @see
	 */
	public String getCompoIdByNodeId(int instanceId, int nodeId)
			throws WFException;

	/**
	 * 取指定部件所能够启动的所有模板
	 * 
	 * @param compoName
	 * @return
	 * @throws WFException
	 */
	List getCompoEnableStartedTemplate(String compoName) throws WFException;

	// /取指定模板和活动的的所有动作
	Delta getActionDeltaByActivity(String compoName, int templateId,
			int activityId) throws WFException;

	// /取指定模板和活动的的所有动作
	Set getActionSetByActivity(int templateId, int activityId)
			throws WFException;

	// /取指定模板和活动的的所有动作
	Delta getActionDeltaByTemplateAndNode(String compoName, int templateId,
			int nodeId) throws WFException;

	// /取指定模板和活动的的所有动作
	Set getActionSetByTemplateAndNode(int templateId, int nodeId)
			throws WFException;

	// /搜索流程定义
	ProcessDefBean findProcessDef(Object processId) throws WFException;

	// /搜索流程包含的活动
	List findProcessActivitys(ProcessDefBean process) throws WFException;

	// /搜索活动定义
	ActivityDefBean findActivityDef(Object activityId) throws WFException;

	ActivityDefBean findActivityDef(NodeMeta node) throws WFException;

	// /指定源活动搜索所有的链接
	List findActivityLinksBySrc(ActivityDefBean activity) throws WFException;

	// /根据 ID 构造流程实例对象
	ProcessInstBean findProcessInst(Object processInstId) throws WFException;

	// /根据工作项ID构造工作项对象<p>
	WorkitemBean findWorkitem(Object workitemId, int isValid)
			throws WFException;

	/**
	 * 根据页面数据计算下一节点执行人
	 * 
	 * @param compoTableData
	 *            部件页面数据
	 * @param WfTableData
	 *            工作流页面数据
	 * @param logonUser
	 *            当前登录人
	 * @param logonUserCoCode
	 *            当前登录人单位
	 * @param logonUserOrgCode
	 *            当前登录人组织机构
	 * @param logonPosiCode
	 *            当前登录人职位
	 * @return 下一节点执行人，如果为多个，以逗号分割
	 * @throws BusinessException
	 * @throws WFException
	 * @throws WorkflowException
	 */
	public Set getDefaultNextExecutor(TableData entityData, TableData wfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, String nd, List wfVarList,List nextLinkList)
			throws WFException;

	public Set getExecutorsByRelation(TableData entityData, TableData wfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, String nd, String action,
			String orgPosiCode) throws WFException, WorkflowException,
			BusinessException;

	// /查找下一步可能的活动<p>
	List getNextActionList(String user, ActivityDefBean activity)
			throws WFException;

	// /取待办事宜(根据执行者和流程类型)
	List getTodoListByUser(String userId) throws WFException;

	List getTodoInstListByUser(String userId) throws WFException;

	// /取待办事宜(根据用户和流程实例ID)
	List getTodoListByProcessInst(String user, Object processInstId, int isValid)
			throws WFException;

	// /取待办事宜(根据用户和流程实例ID)
	List getTodoListByProcessInstanceId(int processInstanceId, int isValid)
			throws WFException;

	// 取作废流程id(根据用户)
	List getInvalidInstListByUser(String userId) throws WFException;

	// /取得流程历史
	List getProcessHistory(Object processInstId, int isValid)
			throws WFException;

	// /取待办事宜(根据用户和流程实例ID)
	List getDoneListByProcessInstanceId(int processInstanceId, int isValid)
			throws WFException;

	// /取已办事宜
	List getDoneListByUser(String userId, String startTime, String endTime)
			throws WFException;

	// /取已办事宜
	List getDoneInstListByUser(String userId, String startTime, String endTime)
			throws WFException;

	// 取已办部件列表
	List getDoneCompoListByUser(String userId) throws WFException;

	// 取待办部件列表
	List getTodoCompoListByUser(String userId) throws WFException;

	// 根据已办用户以及流程实例查询已办事宜
	List getDoneListByUserAndInstance(String userId, int nInstanceId,
			int isValid) throws WFException;

	ProcessInstBean createInstance(String userId, ProcessInstBean inst,
			TableData bnData) throws WFException;

	/** 更新流程信息(名称、描述等) */
	ProcessInstBean updateInstance(ProcessInstBean inst) throws WFException;

	/** 删除流程 */
	void deleteInstance(String userId, Object instId) throws WFException;

	// /提交工作项
	WorkitemBean completeWorkitem(String user, WorkitemBean workitem,
			ActionBean action, List parameters, TableData bnData)
			throws WFException;

	String commitSimply(String strInstanceId, String strTemplateId,
			String strCompoId, String strUserId, TableData wfData,
			TableData bnData) throws WFException;

	List getNodeList(int templateId) throws WFException;

	// 授权
	void impower(int processInstanceId, int activityId, String[] svUserIdList,
			int[] responsibility, String authorizor, String comment,
			TableData bnData) throws WFException;

	// /移交
	void handover(int processInstanceId, int activityId, int workitemId,
			String[] svUserIdList, int[] responsibility, String forwarder,
			String comment, TableData bnData) throws WFException;

	// / 回退
	void withdraw(int processInstanceId, int activityId, String svUserId,
			String comment, TableData bnData) throws WFException;

	/**
	 * 回退多步
	 */
	public void untread(int processInstanceId, int activityId,
			int prevActivityId, String svUserId, String comment,
			TableData bnData) throws WFException;

	/**
	 * 最简洁的退回方法。所有缺失的参数和数据会从数据库中提取。
	 */
	public String untreadSimply(String strInstanceId, String strUserId,
			TableData strBnData) throws WFException;

	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData) throws WFException;

	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData, int toWhere) throws WFException;

	public String rework(String strInstanceId, String strUserId,
			String strComment, TableData bnData) throws WFException;

	// /回收

	// /回收
	void callBack(int processInstanceId, int activityId, String svUserId,
			String comment, TableData bnData) throws WFException;

	public boolean isInstanceFinished(int instanceId) throws WFException;

	public List getInstaceListByTemplate(String templateId) throws WFException;

	/**
	 * 根据模板id获取TemplateMeta对象
	 * 
	 * @param templateId
	 *            TODO
	 * @return
	 */
	TemplateMeta getTemplateMetaByTemplateId(int templateId);

	/**
	 * 根据节点以及模板找到所挂接到的部件名
	 * 
	 * @param templateId
	 * @param nodeId
	 * @return
	 */
	String findCompoByTask(int templateId, int nodeId);

	TableData getCompoDataByCurTask(int processInstId,
			ActivityDefBean activityDefBean) throws WFException;

	TableData collectCommit(TableData wfData, String entityName,
			List lstDetailTaskId, TableData bnData) throws WFException;

	/**
	 * 根据当前用户,流程实例id,获取当前工作项 如果没有当前工作项(流程已经结束)返回空(null).
	 * 
	 * @param user
	 * @param processId
	 * @return
	 * @throws WFException
	 */
	WorkitemBean getWorkItemByInstance(String user, Object processId,
			int isValid) throws WFException;

	/**
	 * 根据当前用户,流程实例id,获取当前工作项 如果没有当前工作项(流程已经结束)返回空(null).
	 * 
	 * @param user
	 * @param processId
	 * @return
	 * @throws WFException
	 */
	CurrentTaskMeta getWorkItemByInstance(int nProcessId, int isValid)
			throws WFException;

	/**
	 * 设置绑定了工作流状态变量的业务字段值。即把工作流状态值同步到绑定的业务字段上面 目前只考虑把状态设置到主表字段的情况
	 * 
	 * @param i
	 * @param bindStateInfo
	 * @param conn
	 */
	void syncDataByBindedWFSate(int nInstanceId, int nTemplateId)
			throws WFException;

	void syncDataByBindedWFSate(int instanceId) throws WFException;

	/**
	 * 从工作流直接读取意见内容，并设置到db中 并且内容需要累加所有完成步骤地每个意见
	 */
	public void rewriteComment(String instanceId, String nodeId,
			String comment, TableData bnData, ActivityDefBean activityDef)
			throws WFException;

	/**
	 * 从工作流直接读取意见内容，并设置到db中 并且内容需要累加所有完成步骤地每个意见
	 */
	public void refreshBrief(String instanceId, String nodeId,
			TableData bnData, ActivityDefBean activityDef) throws WFException;

	/**
	 * 根据父流程id求子流程
	 * 
	 * @param parentInstanceId
	 * @param conn
	 * @return ArrayList 元素为InstanceInfo类型
	 * @throws WFException
	 */
	public List getChildTodoListByParentInstance(int parentInstanceId)
			throws WFException;

	/**
	 * 同步汇总流程的业务数据
	 * 
	 * @param fieldInt
	 * @param bindStateInfo
	 * @param conn
	 * @throws WFException
	 */
	public void syncCollectedFlowDataByBindedWFSate(int parentInstanceId,
			int parentTemplateId) throws WFException;

	public List getWFVariableValueList(String entityName, TableData entity,
			TableData wfData) throws WFException;

	public int getInstanceStatus(String instanceId);

	public TableData commit(TableData entity, String entityName,
			TableData wfData) throws BusinessException;

	// 中止实例
	public void interruptInstance(String instanceId, String svUserId,
			String comment) throws BusinessException;

	public ActivityDefBean findActivityDefBean(int instanceId, int nNodeId)
			throws NumberFormatException, WFException;
	
	public String getWfdataByProcessInstId(String user, String templateId,
			String instanceId, HttpServletRequest request)
			throws BusinessException ;

	// 以下主要是为OA提供的方法
	public String appendExecutor(String strInstanceId, String strTemplateId,
			String strNodeId, String strCompoId, String strUserId,
			TableData wfData, String direction) throws WFException;

	public String removeExecutor(String strInstanceId, String strNodeId,
			String strUserId, String comment) throws WFException;

	public String getNodeExecutorBySource(String templateId, String nodeId,
			String action, String nd) throws WFException;

	public String getRuntimeExecutor(String strTemplateId,
			String strInstanceId, String strNodeId, String action)
			throws WFException;

	public String removeNextNodeExecutor(String instanceId, String userId,
			TableData wfData, String comment) throws WFException;

	public String removeNodeExecutor(String instanceId, String userId,
			TableData wfData, String comment) throws WFException;

}
