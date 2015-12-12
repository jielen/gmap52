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
 * ���������в�������������ӿ���
 */

public interface WFService {

	// /ȡ���л������ģ��
	List getAllActiveTemplate() throws WFException;

	/**
	 * ���ݹ�����ʵ����ȡ����id
	 * 
	 * @param instanceId
	 *            ������ʵ��id
	 * @return ��������Ӧ�Ĳ���id��
	 * @throws
	 * @see
	 */
	public List getCompoIdByInstanceId(int instanceId) throws WFException;

	/**
	 * ���ݹ�����ʵ���Լ����ڻ�ȡ����id
	 * 
	 * @param instanceId
	 *            ������ʵ��id
	 * @param nodeId
	 *            ����������id
	 * @return ��������Ӧ�Ĳ���id��
	 * @throws
	 * @see
	 */
	public String getCompoIdByNodeId(int instanceId, int nodeId)
			throws WFException;

	/**
	 * ȡָ���������ܹ�����������ģ��
	 * 
	 * @param compoName
	 * @return
	 * @throws WFException
	 */
	List getCompoEnableStartedTemplate(String compoName) throws WFException;

	// /ȡָ��ģ��ͻ�ĵ����ж���
	Delta getActionDeltaByActivity(String compoName, int templateId,
			int activityId) throws WFException;

	// /ȡָ��ģ��ͻ�ĵ����ж���
	Set getActionSetByActivity(int templateId, int activityId)
			throws WFException;

	// /ȡָ��ģ��ͻ�ĵ����ж���
	Delta getActionDeltaByTemplateAndNode(String compoName, int templateId,
			int nodeId) throws WFException;

	// /ȡָ��ģ��ͻ�ĵ����ж���
	Set getActionSetByTemplateAndNode(int templateId, int nodeId)
			throws WFException;

	// /�������̶���
	ProcessDefBean findProcessDef(Object processId) throws WFException;

	// /�������̰����Ļ
	List findProcessActivitys(ProcessDefBean process) throws WFException;

	// /���������
	ActivityDefBean findActivityDef(Object activityId) throws WFException;

	ActivityDefBean findActivityDef(NodeMeta node) throws WFException;

	// /ָ��Դ��������е�����
	List findActivityLinksBySrc(ActivityDefBean activity) throws WFException;

	// /���� ID ��������ʵ������
	ProcessInstBean findProcessInst(Object processInstId) throws WFException;

	// /���ݹ�����ID���칤�������<p>
	WorkitemBean findWorkitem(Object workitemId, int isValid)
			throws WFException;

	/**
	 * ����ҳ�����ݼ�����һ�ڵ�ִ����
	 * 
	 * @param compoTableData
	 *            ����ҳ������
	 * @param WfTableData
	 *            ������ҳ������
	 * @param logonUser
	 *            ��ǰ��¼��
	 * @param logonUserCoCode
	 *            ��ǰ��¼�˵�λ
	 * @param logonUserOrgCode
	 *            ��ǰ��¼����֯����
	 * @param logonPosiCode
	 *            ��ǰ��¼��ְλ
	 * @return ��һ�ڵ�ִ���ˣ����Ϊ������Զ��ŷָ�
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

	// /������һ�����ܵĻ<p>
	List getNextActionList(String user, ActivityDefBean activity)
			throws WFException;

	// /ȡ��������(����ִ���ߺ���������)
	List getTodoListByUser(String userId) throws WFException;

	List getTodoInstListByUser(String userId) throws WFException;

	// /ȡ��������(�����û�������ʵ��ID)
	List getTodoListByProcessInst(String user, Object processInstId, int isValid)
			throws WFException;

	// /ȡ��������(�����û�������ʵ��ID)
	List getTodoListByProcessInstanceId(int processInstanceId, int isValid)
			throws WFException;

	// ȡ��������id(�����û�)
	List getInvalidInstListByUser(String userId) throws WFException;

	// /ȡ��������ʷ
	List getProcessHistory(Object processInstId, int isValid)
			throws WFException;

	// /ȡ��������(�����û�������ʵ��ID)
	List getDoneListByProcessInstanceId(int processInstanceId, int isValid)
			throws WFException;

	// /ȡ�Ѱ�����
	List getDoneListByUser(String userId, String startTime, String endTime)
			throws WFException;

	// /ȡ�Ѱ�����
	List getDoneInstListByUser(String userId, String startTime, String endTime)
			throws WFException;

	// ȡ�Ѱ첿���б�
	List getDoneCompoListByUser(String userId) throws WFException;

	// ȡ���첿���б�
	List getTodoCompoListByUser(String userId) throws WFException;

	// �����Ѱ��û��Լ�����ʵ����ѯ�Ѱ�����
	List getDoneListByUserAndInstance(String userId, int nInstanceId,
			int isValid) throws WFException;

	ProcessInstBean createInstance(String userId, ProcessInstBean inst,
			TableData bnData) throws WFException;

	/** ����������Ϣ(���ơ�������) */
	ProcessInstBean updateInstance(ProcessInstBean inst) throws WFException;

	/** ɾ������ */
	void deleteInstance(String userId, Object instId) throws WFException;

	// /�ύ������
	WorkitemBean completeWorkitem(String user, WorkitemBean workitem,
			ActionBean action, List parameters, TableData bnData)
			throws WFException;

	String commitSimply(String strInstanceId, String strTemplateId,
			String strCompoId, String strUserId, TableData wfData,
			TableData bnData) throws WFException;

	List getNodeList(int templateId) throws WFException;

	// ��Ȩ
	void impower(int processInstanceId, int activityId, String[] svUserIdList,
			int[] responsibility, String authorizor, String comment,
			TableData bnData) throws WFException;

	// /�ƽ�
	void handover(int processInstanceId, int activityId, int workitemId,
			String[] svUserIdList, int[] responsibility, String forwarder,
			String comment, TableData bnData) throws WFException;

	// / ����
	void withdraw(int processInstanceId, int activityId, String svUserId,
			String comment, TableData bnData) throws WFException;

	/**
	 * ���˶ಽ
	 */
	public void untread(int processInstanceId, int activityId,
			int prevActivityId, String svUserId, String comment,
			TableData bnData) throws WFException;

	/**
	 * ������˻ط���������ȱʧ�Ĳ��������ݻ�����ݿ�����ȡ��
	 */
	public String untreadSimply(String strInstanceId, String strUserId,
			TableData strBnData) throws WFException;

	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData) throws WFException;

	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData, int toWhere) throws WFException;

	public String rework(String strInstanceId, String strUserId,
			String strComment, TableData bnData) throws WFException;

	// /����

	// /����
	void callBack(int processInstanceId, int activityId, String svUserId,
			String comment, TableData bnData) throws WFException;

	public boolean isInstanceFinished(int instanceId) throws WFException;

	public List getInstaceListByTemplate(String templateId) throws WFException;

	/**
	 * ����ģ��id��ȡTemplateMeta����
	 * 
	 * @param templateId
	 *            TODO
	 * @return
	 */
	TemplateMeta getTemplateMetaByTemplateId(int templateId);

	/**
	 * ���ݽڵ��Լ�ģ���ҵ����ҽӵ��Ĳ�����
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
	 * ���ݵ�ǰ�û�,����ʵ��id,��ȡ��ǰ������ ���û�е�ǰ������(�����Ѿ�����)���ؿ�(null).
	 * 
	 * @param user
	 * @param processId
	 * @return
	 * @throws WFException
	 */
	WorkitemBean getWorkItemByInstance(String user, Object processId,
			int isValid) throws WFException;

	/**
	 * ���ݵ�ǰ�û�,����ʵ��id,��ȡ��ǰ������ ���û�е�ǰ������(�����Ѿ�����)���ؿ�(null).
	 * 
	 * @param user
	 * @param processId
	 * @return
	 * @throws WFException
	 */
	CurrentTaskMeta getWorkItemByInstance(int nProcessId, int isValid)
			throws WFException;

	/**
	 * ���ð��˹�����״̬������ҵ���ֶ�ֵ�����ѹ�����״ֵ̬ͬ�����󶨵�ҵ���ֶ����� Ŀǰֻ���ǰ�״̬���õ������ֶε����
	 * 
	 * @param i
	 * @param bindStateInfo
	 * @param conn
	 */
	void syncDataByBindedWFSate(int nInstanceId, int nTemplateId)
			throws WFException;

	void syncDataByBindedWFSate(int instanceId) throws WFException;

	/**
	 * �ӹ�����ֱ�Ӷ�ȡ������ݣ������õ�db�� ����������Ҫ�ۼ�������ɲ����ÿ�����
	 */
	public void rewriteComment(String instanceId, String nodeId,
			String comment, TableData bnData, ActivityDefBean activityDef)
			throws WFException;

	/**
	 * �ӹ�����ֱ�Ӷ�ȡ������ݣ������õ�db�� ����������Ҫ�ۼ�������ɲ����ÿ�����
	 */
	public void refreshBrief(String instanceId, String nodeId,
			TableData bnData, ActivityDefBean activityDef) throws WFException;

	/**
	 * ���ݸ�����id��������
	 * 
	 * @param parentInstanceId
	 * @param conn
	 * @return ArrayList Ԫ��ΪInstanceInfo����
	 * @throws WFException
	 */
	public List getChildTodoListByParentInstance(int parentInstanceId)
			throws WFException;

	/**
	 * ͬ���������̵�ҵ������
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

	// ��ֹʵ��
	public void interruptInstance(String instanceId, String svUserId,
			String comment) throws BusinessException;

	public ActivityDefBean findActivityDefBean(int instanceId, int nNodeId)
			throws NumberFormatException, WFException;
	
	public String getWfdataByProcessInstId(String user, String templateId,
			String instanceId, HttpServletRequest request)
			throws BusinessException ;

	// ������Ҫ��ΪOA�ṩ�ķ���
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
