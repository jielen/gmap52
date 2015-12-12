package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.access.CountQuery;
import com.kingdrive.workflow.access.CurrentTaskBean;
import com.kingdrive.workflow.access.CurrentTaskQuery;
import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TaskExecutorMeta;
import com.kingdrive.workflow.dto.TaskMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.CurrentTaskInfo;
import com.kingdrive.workflow.model.CurrentTaskModel;
import com.kingdrive.workflow.util.Sequence;

/**
 * ��ǰ����
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: ��������
 * </p>
 * 
 * @author unauthorized
 * @time unknown
 * 
 */
public class CurrentTask implements Serializable{

	public static final int TYPE_NORMAL = 0;// ��ͬ���ʹ���

	public static final int TYPE_TOCOLLECT = 1;// �ȴ��������ʹ�������

	public final static int TYPE_COLLECTED = 2;// �Ѿ��������ʹ�������

	public static final int TYPE_TOCOLLECT_DETAIL = 3;// �������ʹ�������

	public static final int TYPE_COLLECTED_DETAIL = 4;// �ѻ������ʹ�������

	// log4j for log
	final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CurrentTask.class);

	public CurrentTask(){
	}

	/**
	 * ��������
	 * 
	 * @param instanceId
	 * @param nodeIdArray
	 * @param executorListArray
	 * @param responsibilityListArray
	 * @param taskSender
	 *          TODO
	 * @param creator
	 * @param createTime
	 * @param conn
	 * 
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	private void create(int instanceId, int nodeIdArray[], List executorListArray[], List responsibilityListArray[], String taskSender, String creator,
			String createTime) throws WorkflowException{
		List currentTaskList = new ArrayList();

		TaskTerm taskExecuteTermHandler = new TaskTerm();
		// ��ȡ��ǰ���������Parent_Task_Id
		int parentTaskId = getCurrentTaskParentTaskId(instanceId, taskSender);
		/* Ϊÿ��ִ���ߴ������� */
		for(int i = 0; i < executorListArray.length; i++){
			int nodeId = nodeIdArray[i];
			String limitExecuteTime = taskExecuteTermHandler.getLimitExecuteTime(instanceId, nodeId, createTime);
			List executorList = executorListArray[i];
			List responsibilityList = responsibilityListArray[i];
			for(int j = 0; j < executorList.size(); j++){
				String executor = (String) executorList.get(j);
				int responsibility = ((Integer) responsibilityList.get(j)).intValue();
				CurrentTaskMeta ct = new CurrentTaskMeta();
				ct.setInstanceId(instanceId);
				ct.setNodeId(nodeId);
				ct.setExecutor(executor);
				ct.setIdentity(CurrentTaskMeta.TASK_IDENTITY_NORMAL);
				// //20050322 by zhangcheng,ownerָ����ԭӵ���ߣ�����һ�����ִ���ߣ�����˵����
				// ct.setOwner(taskSender);
				// 050622 by zhanggh .owner ָ��������ִ���ߣ�������Instance��owner(������)
				ct.setOwner(executor);
				ct.setCreator(creator);
				ct.setCreateTime(createTime);
				ct.setLimitExecuteTime(limitExecuteTime);
				ct.setResponsibility(responsibility);
				ct.setParentTaskId(parentTaskId);
				currentTaskList.add(ct);
			}
		}

		Delegation delegation = new Delegation();
		List currentTaskList2 = delegation.getCurrentTask(currentTaskList);

		CurrentTaskBean bean = new CurrentTaskBean();
		try{
			for(int i = 0; i < currentTaskList2.size(); i++){
				CurrentTaskMeta meta = (CurrentTaskMeta) currentTaskList2.get(i);
				meta.setCurrentTaskId(Sequence.fetch(Sequence.SEQ_CURRENT_TASK));
				bean.insert(unwrap(meta));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1121, sqle.toString());
		}
	}

	/**
	 * ��ȡָ������ִ���˺�ָ������ʵ���ĵ�ǰ�����parent_task_id
	 * 
	 * @param instanceId
	 * @param executor
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 */
	public int getCurrentTaskParentTaskId(int instanceId, String executor) throws WorkflowException{
		int result = 0;
		CurrentTaskQuery ctq = new CurrentTaskQuery();
		try{
			ArrayList list = ctq.getTodoListByExecutorAndInstance(executor, instanceId, 1);
			if(list.size() == 1){
				CurrentTaskInfo ctInfo = (CurrentTaskInfo) list.get(0);
				if(ctInfo.getParentTaskId() != null)
					result = ctInfo.getParentTaskId().intValue();
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new WorkflowException(2000, e.getMessage());
		}
		return result;
	}

	/**
	 * ��ȡ���д���Task List
	 * 
	 * @param instanceId
	 * @param nodeId
	 * @param executorsMethod
	 * @param nodeLinkList
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public List getTodoList() throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoList();
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ����templateType��ȡ����Task List
	 * 
	 * @param templateType
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public List getTodoListByTemplate(String templateType) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByTemplate(templateType);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ����templateId��ȡ����Task List
	 * ˵��:TemplateType��ʾģ�����ͣ�TemplateId��ʾ����ĳ������ģ�壬һ��ģ�����Ϳ��ܶ�Ӧ���ģ��
	 * 
	 * @param templateId
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public List getTodoListByTemplate(int templateId) throws WorkflowException{
		List result = new ArrayList();
		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByTemplate(templateId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ��ȡ����Task List
	 * 
	 * @throws
	 * @see
	 */
	public List getTodoListByExecutorAndTemplate(String executor, String templateType) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByExecutorAndTemplate(executor, templateType);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ��ȡ����Task List
	 * 
	 * @throws
	 * @see
	 */
	public List getTodoListByExecutorAndTemplate(String executor, int templateId) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByExecutorAndTemplate(executor, templateId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ��ȡ����Task List
	 * 
	 * @throws
	 * @see
	 */
	public List getTodoListByInstance(int instanceId, int isValid) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByInstance(instanceId, isValid);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ��ȡ����Task List
	 * 
	 * @throws
	 * @see
	 */
	public List getTodoListByExecutorAndInstance(String executor, int instanceId, int isValid) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByExecutorAndInstance(executor, instanceId, isValid);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByNode(String businessType) throws WorkflowException{
		if(businessType == null || businessType.length() == 0)
			throw new WorkflowException(1145);

		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByNode(businessType);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByNode(int instanceId, int nodeId) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByNode(instanceId, nodeId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByUserCompo(String userId, String compoId) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			List list = query.getTodoListByUserCompo(userId, compoId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByExecutorAndNode(String executor, String businessType) throws WorkflowException{
		if(businessType == null || businessType.length() == 0)
			throw new WorkflowException(1145);

		List result = new ArrayList();
		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByExecutorAndNode(executor, businessType);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByExecutorAndNode(String executor, int nodeId) throws WorkflowException{
		List result = new ArrayList();
		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByExecutorAndNode(executor, nodeId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	/**
	 * ��ȡ�ƶ�TaskId��Task��Ҳ���ǵ�ǰ�ڵ��һ������
	 * 
	 * @param currentTaskId
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 */
	public TaskMeta getTask(int currentTaskId, int isValid) throws WorkflowException{
		TaskMeta result = null;

		CurrentTaskMeta currentTaskMeta = getCurrentTask(currentTaskId, isValid);

		int templateId = currentTaskMeta.getTemplateId();
		int instanceId = currentTaskMeta.getInstanceId();
		int nodeId = currentTaskMeta.getNodeId();
		String owner = currentTaskMeta.getOwner();

		Node nodeHandler = new Node();
		Set actionSet = nodeHandler.getActionSet(templateId, nodeId);

		VariableValue variableValueHandler = new VariableValue();
		List valueList = variableValueHandler.getValueListByInstnace(templateId, instanceId);

		Staff staffHandler = new Staff();
		// List orgPositionList = staffHandler.getStaffPositionList(owner, conn);

		result = new TaskMeta(currentTaskMeta);
		result.setActionSet(actionSet);
		result.setVariableValueList(valueList);
		// result.setPositionList(orgPositionList);

		return result;
	}

	public TaskMeta getTask(CurrentTaskModel taskModel) throws WorkflowException{
		TaskMeta result = null;
		int instanceId = taskModel.getInstanceId().intValue();
		int templateId = ExecuteFacade.getInstance(instanceId).getInstanceId();

		int nodeId = taskModel.getNodeId().intValue();
		String owner = taskModel.getOwner();

		Node nodeHandler = new Node();
		Set actionSet = nodeHandler.getActionSet(templateId, nodeId);

		VariableValue variableValueHandler = new VariableValue();
		List valueList = variableValueHandler.getValueListByInstnace(templateId, instanceId);

		Staff staffHandler = new Staff();
		// List orgPositionList = staffHandler.getStaffPositionList(owner, conn);

		result = new TaskMeta();
		result.setTemplateId(templateId);
		result.setInstanceId(instanceId);
		result.setNodeId(nodeId);
		result.setCurrentTaskId(taskModel.getCurrentTaskId().intValue());
		result.setActionSet(actionSet);
		result.setVariableValueList(valueList);
		// result.setPositionList(orgPositionList);
		return result;
	}

	/**
	 * ��ȡ��ǰ���񣬷��ص���dto CurrentTaskMeta����
	 * 
	 * @param currentTaskId
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public CurrentTaskMeta getCurrentTask(int currentTaskId, int isValid) throws WorkflowException{
		CurrentTaskMeta meta = new CurrentTaskMeta();

		CurrentTaskQuery query = new CurrentTaskQuery();
		try{
			meta = wrap(query.getCurrentTask(currentTaskId, isValid));
		}catch(SQLException sqle){
			throw new WorkflowException(1205, sqle.toString());
		}
		return meta;
	}

	/**
	 * ������ǰ����
	 * 
	 * @param instanceId
	 * @param currentNodeId
	 *          TODO
	 * @param nodeId
	 * @param nodeLinkList
	 * @param followedNodeLinkList
	 * @param conn
	 * 
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public void finishNode(int instanceId, int currentTaskId, int nodeId, List nodeLinkList, List followedNodeLinkList) throws WorkflowException{
		// ɾ�� pass count
		Pass passCountHandler = new Pass();
		// ɾ��pass
		passCountHandler.removeByNode(instanceId, nodeId);
		try{
			// ������ѻ������񣬱�����»�����ϸ�����Parent_task_id
			// ɾ����ǰ�ڵ����л�û��ִ�еĴ�������
			// ��ɾ�����нڵ��������,��Ϊ��ǰ����

			// ֻɾ���ýڵ���������񣬱�����������
			// �����ǰ������ɺ󸨰첻�ɼ������⣬ͬʱ����ڵ���ɺ���Ȼ�д��������
			removeMainTaskByNode(instanceId, nodeId);
			// removeByNode(instanceId, nodeId, conn);
			int currentTaskType = getTaskType(currentTaskId);
			// Ϊʲô��removeByNode�ĳ�removeByTask?��ԭ
			// removeByTask(currentTaskId,conn);
			if(currentTaskType == TYPE_COLLECTED){
				CurrentTaskQuery ctq = new CurrentTaskQuery();
				ArrayList list = ctq.getTodoListByInstance(instanceId, 1);
				// Ϊʲֻȡ��һ����
				CurrentTaskInfo todoTaskInfo = (CurrentTaskInfo) list.get(0);
				int newParentId = wrap(todoTaskInfo).getCurrentTaskId();
				updateChildParentId(currentTaskId, newParentId);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		// TCJLODO �Ƿ��׵�,ɾ����ǰ���̵�task_executor
		// ȥ��ע�Ͷ�WFGeneral.getTaskExecutorInfo��Ӱ�죬��ע���϶Ի����ύ��Ӱ�졣
		// taskExecutorHandler.removeByInstance(instanceId,conn);

		// ����ʵ����ǰ�ڵ����״̬
		StateValue stateValueHandler = new StateValue();

		// stateValueHandler.removeLinkState(instanceId, nodeLinkList, conn);
		// stateValueHandler.setLinkState(instanceId, followedNodeLinkList, conn);

		// stateValueHandler.removeNodeState(instanceId, nodeId, conn);
		stateValueHandler.setNodeState(instanceId, followedNodeLinkList);
	}

	/**
	 * ��������ִ��֮��,���»�����ϸ�����parent_task_id.
	 * 
	 * @param currentTaskId
	 * @param newParentId
	 * @param conn
	 * @throws WorkflowException
	 */
	private void updateChildParentId(int currentTaskId, int newParentId) throws WorkflowException{
		// TCJLODO Auto-generated method stub
		CurrentTaskQuery ctq = new CurrentTaskQuery();
		CurrentTaskBean bean = new CurrentTaskBean();
		try{
			ArrayList list = ctq.findTaskByParentId(currentTaskId);
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				CurrentTaskInfo element = (CurrentTaskInfo) iter.next();
				CurrentTaskModel model = unwrap(wrap(element));
				model.setParentTaskId(newParentId);
				bean.update(model);
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new WorkflowException(2000, e.getMessage());
		}
	}

	/**
	 * 
	 * 
	 * @param instanceId
	 * @param precedingTaskNodeList
	 * @param creator
	 * @param createTime
	 * @param conn
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public void createPrecedingNodesTask(int instanceId, List precedingTaskNodeList, String creator, String createTime) throws WorkflowException{
		Node nodeHandler = new Node();
		TaskExecutor executorHandler = new TaskExecutor();

		int[] nodeIdArray = new int[precedingTaskNodeList.size()];
		List[] executorListArray = new ArrayList[precedingTaskNodeList.size()];
		List[] responsibilityArray = new ArrayList[precedingTaskNodeList.size()];
		for(int i = 0; i < precedingTaskNodeList.size(); i++){
			int nodeId = ((NodeMeta) precedingTaskNodeList.get(i)).getId();
			nodeIdArray[i] = nodeId;
			executorListArray[i] = new ArrayList();
			responsibilityArray[i] = new ArrayList();
			String executorsMethod = nodeHandler.getNode(nodeId).getExecutorsMethod();
			if(executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO) || executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)){
				List taskExecutorList = executorHandler.getExecutorList(instanceId, nodeId);
				for(int index = 0; index < taskExecutorList.size(); index++){
					TaskExecutorMeta executor = (TaskExecutorMeta) taskExecutorList.get(index);
					executorListArray[i].add(executor.getExecutor());
					responsibilityArray[i].add(new Integer(executor.getResponsibility()));
				}
			}else if(executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)){
				// ���˸��ýڵ����λ������ִ����
				List taskExecutorList = executorHandler.getForemostExecutorList(instanceId, nodeId);
				for(int index = 0; i < taskExecutorList.size(); index++){
					TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList.get(index);
					executorListArray[i].add(taskExecutor.getExecutor());
					responsibilityArray[i].add(new Integer(taskExecutor.getResponsibility()));
				}
			}else{
				throw new WorkflowException(1215);
			}
		}
		create(instanceId, nodeIdArray, executorListArray, responsibilityArray, creator, creator, createTime);
	}

	public void createNodeAuthorizeTask(int instanceId, int nodeId, String[] executor, int[] responsibility, String creator, String createTime)
			throws WorkflowException{

		if(executor == null || responsibility == null || executor.length != responsibility.length){
			throw new WorkflowException(0, "�����ִ���߻���ְ��ָ������!");
		}

		int[] nodeIdArray = new int[1];
		nodeIdArray[0] = nodeId;
		List[] executorListArray = new ArrayList[1];
		executorListArray[0] = new ArrayList();
		for(int i = 0; i < executor.length; i++){
			executorListArray[0].add(executor[i]);
		}
		List[] responsibilityListArray = new ArrayList[1];
		responsibilityListArray[0] = new ArrayList();
		for(int i = 0; i < responsibility.length; i++){
			responsibilityListArray[0].add(new Integer(responsibility[i]));
		}

		responsibilityListArray[0].add(responsibility);
		create(instanceId, nodeIdArray, executorListArray, responsibilityListArray, creator, creator, createTime);
	}

	public void createNodeSerialTask(int instanceId, int nodeId, String taskRealOwner, String creator, String createTime)
			throws WorkflowException{
		TaskExecutor taskExecutorHandler = new TaskExecutor();
		int[] nodeIdArray = new int[1];
		nodeIdArray[0] = nodeId;

		List[] executorListArray = new ArrayList[1];
		executorListArray[0] = new ArrayList();

		List[] responsibilityArray = new ArrayList[1];
		responsibilityArray[0] = new ArrayList();

		List taskExecutorList = taskExecutorHandler.getFollowedExecutorList(instanceId, nodeId, taskRealOwner);
		for(int i = 0; i < taskExecutorList.size(); i++){
			TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList.get(i);
			executorListArray[0].add(taskExecutor.getExecutor());
			responsibilityArray[0].add(new Integer(taskExecutor.getResponsibility()));
		}

		// ����Ƿ�����ȷ�ĺ���˳ǩ����ִ����
		if(executorListArray[0].size() == 0){
			throw new WorkflowException(1123);
		}
		create(instanceId, nodeIdArray, executorListArray, responsibilityArray, taskRealOwner, creator, createTime);
	}

	public void createNodeTask(int instanceId, int nodeId, String executorsMethod, String creator, String createTime) throws WorkflowException{
		CurrentTask taskHandler = new CurrentTask();
		TaskExecutor executorHandler = new TaskExecutor();

		int[] nodeIdArray = new int[1];
		nodeIdArray[0] = nodeId;
		List[] executorListArray = new ArrayList[1];
		executorListArray[0] = new ArrayList();
		List[] responsibilityArray = new ArrayList[1];
		responsibilityArray[0] = new ArrayList();
		if(executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO) || executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)){
			List taskExecutorList = executorHandler.getExecutorList(instanceId, nodeId);
			for(int i = 0; i < taskExecutorList.size(); i++){
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList.get(i);
				executorListArray[0].add(taskExecutor.getExecutor());
				responsibilityArray[0].add(new Integer(taskExecutor.getResponsibility()));
			}
		}else if(executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)){
			List taskExecutorList = executorHandler.getForemostExecutorList(instanceId, nodeId);
			for(int i = 0; i < taskExecutorList.size(); i++){
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList.get(i);
				executorListArray[0].add(taskExecutor.getExecutor());
				responsibilityArray[0].add(new Integer(taskExecutor.getResponsibility()));
			}
		}else{
			throw new WorkflowException(1215);
		}
		// cuiliguo 2006.06.12 ���û����һ��ִ�����򱨴�
		if(executorListArray[0].size() > 0)
			taskHandler.create(instanceId, nodeIdArray, executorListArray, responsibilityArray, creator, creator, createTime);
		else
			throw new WorkflowException(1035);
	}

	public TaskMeta createStartTask(int instanceId, int nodeId, String creator, String createTime) throws WorkflowException{
		TaskExecutor executorHandler = new TaskExecutor();
		executorHandler.create(instanceId, nodeId, creator, 1, TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN);

		int currentTaskId = Sequence.fetch(Sequence.SEQ_CURRENT_TASK);
		CurrentTaskModel model = new CurrentTaskModel();
		model.setCurrentTaskId(currentTaskId);
		model.setInstanceId(instanceId);
		model.setNodeId(nodeId);
		model.setExecutor(creator);
		model.setDelegationId(-1);
		model.setOwner(creator);
		model.setCreator(creator);
		model.setCreateTime(createTime);
		model.setLimitExecuteTime(null); // �������������Լ���ʱ������
		model.setResponsibility(TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN);
		CurrentTaskBean bean = new CurrentTaskBean();
		Connection conn = null;
		try{
			bean.insert(model);
		}catch(SQLException sqle){
			throw new WorkflowException(1121, sqle.toString());
		}
		return getTask(model);
	}

	/**
	 * ������һ���ڵ�����
	 * 
	 * @param instanceId
	 * @param nodeLinkList
	 * @param taskRealOwner
	 * @param positionId
	 * @param creator
	 * @param createTime
	 * @param conn
	 * @throws WorkflowException
	 */
	public void createFollowedNodesTask(int instanceId, List nodeLinkList, String taskRealOwner, String positionId, String creator, String createTime) throws WorkflowException{

		/* ��ȡ����ִ���� */
		int[] nodeIdArray = new int[nodeLinkList.size()];
		List[] executorListArray = new ArrayList[nodeLinkList.size()];
		List[] responsibilityArray = new ArrayList[nodeLinkList.size()];
		for(int i = 0; i < nodeLinkList.size(); i++){
			Link nodeLink = (Link) nodeLinkList.get(i);
			int nodeId = nodeLink.getNextNodeId();
			nodeIdArray[i] = nodeId;

			Staff staffHandler = new Staff();
			Node nodeHandler = new Node();
			TaskExecutor taskExecutorHandler = new TaskExecutor();

			executorListArray[i] = new ArrayList();
			responsibilityArray[i] = new ArrayList();
			// majian 2005��1��12ȥ������
			// if (actionHandler.getActionNumByNode(instanceId, nodeId, conn) != 0) {
			// �ڵ��ǰ������ڵ��ǻ��˹��Ľڵ㣬��ǰ�ڵ��Ѿ����л�����ɣ�����Ҫ���´�������
			// continue;
			// }
			String executorRelation = nodeLink.getExecutorRelation();
			NodeMeta node = nodeHandler.getNode(nodeId);
			String executorsMethod = node.getExecutorsMethod();

			// �����ж��Ƿ�����ʱָ����ִ���ˣ�����У���Ϊ�������ɴ�������
			// ������ýڵ��һ�����У�����taskExecutor��Ӧ�ò����а�
			// �������ǰִ�й�����Ϊ��ЩtaskExecutor���ɴ�������
			// �������ǰִ�й����Ƿ���Ҫ��ɾ������TaskExecutor�أ�
			// �����Ǹ�����ʵ��ִ����(��action��ȡ��)����������û��ִ��˳�����Ϣ
			// ���Ƿ���Ҫ����TaskExecutor��action�Ľ������ִ������Ϣ��
			if(taskExecutorHandler.getExecutorNumByNode(instanceId, nodeId) > 0){
				setRuntimeExecutorAndResponsiblity(executorListArray[i], responsibilityArray[i], instanceId, nodeId, executorsMethod);
			}else{
				// ��鶨��ʱ����ĵ�ǰִ��������һ��ִ���߹�ϵ
				if(executorRelation.equals(Link.EXECUTOR_RELATION_SELF)){
					taskExecutorHandler.create(instanceId, nodeId, taskRealOwner, 1, TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN);
					executorListArray[i].add(taskRealOwner);
					responsibilityArray[i].add(new Integer(TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN));
					continue;
				}// end if �Լ�
				else if(executorRelation.equals(Link.EXECUTOR_RELATION_MANAGER)){
					Set managerSet = staffHandler.getSuperStaffSet(taskRealOwner, positionId);
					Object[] managers = managerSet.toArray();
					for(int m = 0; m < managers.length; m++){
						String manager = (String) managers[m];
						taskExecutorHandler.create(instanceId, nodeId, manager, 1, TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN);
						executorListArray[i].add(manager);
						// /responsibilityArray[i].add(manager);
						responsibilityArray[i].add(new Integer(TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN));
					}
					continue;
				}// end if �ϼ�
				else if(executorRelation.equals(Link.EXECUTOR_RELATION_NONE)){
					// if (taskExecutorHandler.getExecutorNumByNode(instanceId, nodeId,
					// conn) == 0) {
					// ���û����������ָ������ִ���ߣ������Ԥ��ִ���ߴ�������ִ����

					// �������̶��������������ִ���ߣ�����Ϊ����ʱ�ýڵ��ִ����
					taskExecutorHandler.set(instanceId, nodeId, executorsMethod);
					// }
					setRuntimeExecutorAndResponsiblity(executorListArray[i], responsibilityArray[i], instanceId, nodeId, executorsMethod);
				}// end if executorRelation.equals(Link.EXECUTOR_RELATION_NONE)
				else{
					throw new WorkflowException(1217);
				}
			}
		}// end for
		// cuiliguo 2006.06.12 ���û����һ��ִ�����򱨴�
		boolean hasNextExecutor = false;
		for(int i = 0; i < nodeLinkList.size(); i++){
			if(executorListArray[i].size() > 0){
				hasNextExecutor = true;
				break;
			}
		}
		if(hasNextExecutor)
			create(instanceId, nodeIdArray, executorListArray, responsibilityArray, taskRealOwner, creator, createTime);
		else
			throw new WorkflowException(1035);
	}

	/**
	 * ��������ʱ���������ִ�����Լ�ִ���������컹�Ǹ��� ����ֵΪ executorListArray��responsibilityArray
	 * 
	 * @param executorListArray
	 * @param responsibilityArray
	 * @param instanceId
	 * @param nodeId
	 * @param executorsMethod
	 * @param conn
	 * @throws WorkflowException
	 */
	private void setRuntimeExecutorAndResponsiblity(List executorListArray, List responsibilityArray, int instanceId, int nodeId, String executorsMethod) throws WorkflowException{
		TaskExecutor taskExecutorHandler = new TaskExecutor();
		if(executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO) || executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)){
			// ��ǩ�Ͳ�ǩ��������taskExecutor�����ɴ�������
			List taskExecutorList = taskExecutorHandler.getExecutorList(instanceId, nodeId);
			for(int index = 0; index < taskExecutorList.size(); index++){
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList.get(index);
				executorListArray.add(taskExecutor.getExecutor());
				responsibilityArray.add(new Integer(taskExecutor.getResponsibility()));
			}
		}// end if ��ǩ �� ��ǩ
		else if(executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)){
			// �������������ȡ�ڵ���task_executor_orderΪ1��ִ����
			List taskExecutorList = taskExecutorHandler.getForemostExecutorList(instanceId, nodeId);
			for(int index = 0; index < taskExecutorList.size(); index++){
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList.get(index);
				executorListArray.add(taskExecutor.getExecutor());
				responsibilityArray.add(new Integer(taskExecutor.getResponsibility()));
			}
		}// end if ˳��ǩ
		else{
			throw new WorkflowException(1215);
		}
		// ����Ƿ�����ȷ�ĺ�������ڵ�ִ����
		if(executorListArray.size() == 0){
			throw new WorkflowException(1122);
		}
	}

	/**
	 * @param responsibilityArray
	 * @param executorListArray
	 * 
	 */
	/*
	 * private void setExecutorAndResponsibility(List executorListArray, List
	 * responsibilityArray,Link nodeLink,Connection conn) { // TCJLODO Auto-generated
	 * method stub Staff staffHandler = new Staff(); Node nodeHandler = new
	 * Node(); TaskExecutor taskExecutorHandler = new TaskExecutor();
	 * 
	 * executorListArray = new ArrayList(); responsibilityArray = new ArrayList(); //
	 * majian 2005��1��12ȥ������ // if (actionHandler.getActionNumByNode(instanceId,
	 * nodeId, conn) != 0) { // �ڵ��ǰ������ڵ��ǻ��˹��Ľڵ㣬��ǰ�ڵ��Ѿ����л�����ɣ�����Ҫ���´������� //
	 * continue; // } int nodeId=nodeLink.getNextNodeId(); String executorRelation =
	 * nodeLink.getExecutorRelation(); NodeMeta node = nodeHandler.getNode(nodeId,
	 * conn); String executorsMethod = node.getExecutorsMethod();
	 * 
	 * if (executorRelation.equals(Link.EXECUTOR_RELATION_SELF)) {
	 * taskExecutorHandler.create(instanceId, nodeId, taskRealOwner, 1,
	 * TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN, conn);
	 * executorListArray[i].add(taskRealOwner); responsibilityArray[i].add(new
	 * Integer( TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN)); continue;
	 * }// end if �Լ� else if
	 * (executorRelation.equals(Link.EXECUTOR_RELATION_MANAGER)) { Set managerSet =
	 * staffHandler.getSuperStaffSet(taskRealOwner, positionId, conn); Object[]
	 * managers = managerSet.toArray(); for (int m = 0; m < managers.length; m++) {
	 * String manager = (String) managers[m];
	 * taskExecutorHandler.create(instanceId, nodeId, manager, 1,
	 * TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN, conn);
	 * executorListArray[i].add(manager); // /responsibilityArray[i].add(manager);
	 * responsibilityArray[i].add(new Integer(
	 * TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN)); } continue; }// end
	 * if �ϼ� else if (executorRelation.equals(Link.EXECUTOR_RELATION_NONE)) { if
	 * (taskExecutorHandler.getExecutorNumByNode(instanceId, nodeId, conn) == 0) { //
	 * ���û����������ָ������ִ���ߣ������Ԥ��ִ���ߴ�������ִ���� taskExecutorHandler.set(instanceId, nodeId,
	 * executorsMethod, conn); } if
	 * (executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO) ||
	 * executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)) { List
	 * taskExecutorList = taskExecutorHandler.getExecutorList( instanceId, nodeId,
	 * conn); for (int index = 0; index < taskExecutorList.size(); index++) {
	 * TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList
	 * .get(index); executorListArray[i].add(taskExecutor.getExecutor());
	 * responsibilityArray[i].add(new Integer(taskExecutor .getResponsibility())); }
	 * }// end if ��ǩ �� ��ǩ else if
	 * (executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)) { List
	 * taskExecutorList = taskExecutorHandler.getForemostExecutorList( instanceId,
	 * nodeId, conn); for (int index = 0; index < taskExecutorList.size();
	 * index++) { TaskExecutorMeta taskExecutor = (TaskExecutorMeta)
	 * taskExecutorList .get(index);
	 * executorListArray[i].add(taskExecutor.getExecutor());
	 * responsibilityArray[i].add(new Integer(taskExecutor .getResponsibility())); }
	 * }// end if ˳��ǩ else { throw new WorkflowException(1215); } //
	 * ����Ƿ�����ȷ�ĺ�������ڵ�ִ���� if (executorListArray[i].size() == 0) { throw new
	 * WorkflowException(1122); } }// end if
	 * executorRelation.equals(Link.EXECUTOR_RELATION_NONE) else { throw new
	 * WorkflowException(1217); } }
	 */
	/**
	 * �ж�ָ��ʵ���ڵ�ǰ�ڵ�����������Ƿ����
	 * 
	 * @param instanceId
	 *          int
	 * @param nodeId
	 *          int
	 * @param executorsMethod
	 *          String ����������
	 * @param nodeLinkList
	 *          List
	 * @param conn
	 *          Connection
	 * @throws WorkflowException
	 * @return boolean true ָ��ʵ���ڵ�ǰ�ڵ������������ȫ����� false δȫ�����
	 */
	public boolean canFinishNode(int instanceId, int nodeId, String executorsMethod, List nodeLinkList) throws WorkflowException{

		Action actionHandler = new Action();
		// ��ǩ�ڵ㣬ֻҪִ������������0���Ϳ��Խ���
		if(executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO)){// ��ǩ
			// if(actionHandler.getActionNumByNode(instanceId, nodeId, conn) == 1){
			if(actionHandler.getActionNumByNode(instanceId, nodeId) >= 1){ // majian
				// 2005��1��12��
				return true;
			}
			return false;
		}
		boolean finished = true; // ʵ���ڵ�ǰ�ڵ�������Ƿ�ȫ�����

		Pass passCountHandler = new Pass();
		TaskExecutor executorHandler = new TaskExecutor();
		// ȡ��Ԥ����ִ����(wf_task_executor)��������������ʱ������ʱָ��
		int totalExecutorCount = executorHandler.getMainExecutorNumByNode(instanceId, nodeId);
		if(totalExecutorCount == 0)
			throw new WorkflowException(1235);

		// ������и����ӵ���������Ƿ�����
		Link link = null;
		for(int i = 0; i < nodeLinkList.size(); i++){
			link = (Link) nodeLinkList.get(i);

			if(executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL) || executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)){// ��ǩ��˳ǩ
				String numberOrPercent = link.getNumberOrPercent();
				int linkPassValue = link.getPassValue();
				int followedNodeId = link.getNextNodeId();
				int linkPassCount = passCountHandler.getPassNum(instanceId, nodeId, followedNodeId);
				if(numberOrPercent.equals(Link.NUMBERORPERCENT_NUMBER)){
					if(linkPassCount >= linkPassValue){ // ��ǰ���������
						continue;
					}
					finished = false;
					break;
				}else if(numberOrPercent.equals(Link.NUMBERORPERCENT_PERCENT)){
					double passPercent = (double) linkPassCount / (double) totalExecutorCount;
					double needPercent = linkPassValue / 100D;
					if(passPercent >= needPercent){ // ��ǰ���������
						continue;
					}
					finished = false;
					break;
				}else{
					throw new WorkflowException(1216);
				}
			}
			throw new WorkflowException(1215);
		}
		if(!finished){
			int totalPassCount = passCountHandler.getPassNum(instanceId, nodeId);
			if(totalPassCount >= totalExecutorCount){
				throw new WorkflowException(0, "��ǰ�ڵ�����������Ѿ�ִ����ɣ�����Ҫ���������������������Բ�������������\n��ִ�л��˹��ܣ��Ա�Ե�ǰ�ڵ����´���");
			}
		}
		return finished;
	}

	/**
	 * remove the current task.
	 * 
	 * @param currentTaskId
	 *          int
	 * @param conn
	 *          Connection
	 * @throws WorkflowException
	 */
	public void removeByTask(int currentTaskId) throws WorkflowException{
		CurrentTaskBean bean = new CurrentTaskBean();
		try{
			bean.delete(currentTaskId);
		}catch(SQLException sqle){
			throw new WorkflowException(1126, sqle.toString());
		}
	}

	public void removeByNode(int instanceId, int currentNodeId) throws WorkflowException{
		CurrentTaskBean bean = new CurrentTaskBean();
		try{
			bean.removeByNodeId(instanceId, currentNodeId);
		}catch(SQLException sqle){
			throw new WorkflowException(1126, sqle.toString());
		}
	}

	public void removeMainTaskByNode(int instanceId, int currentNodeId) throws WorkflowException{
		CurrentTaskBean bean = new CurrentTaskBean();
		try{
			bean.removeMainTaskByNodeId(instanceId, currentNodeId);
		}catch(SQLException sqle){
			throw new WorkflowException(1126, sqle.toString());
		}
	}

	public void removeByNode(int templateId, int instanceId, int startNodeId, int endNodeId) throws WorkflowException{
		Node nodeHandler = new Node();
		Set betweenNodeSet = nodeHandler.getNodeListBetween(templateId, startNodeId, endNodeId);
		List betweenNodeList = new ArrayList();
		betweenNodeList.addAll(betweenNodeSet);
		for(int j = 0; j < betweenNodeList.size(); j++){
			NodeMeta tempNode = (NodeMeta) betweenNodeList.get(j);
			removeByNode(instanceId, tempNode.getId());
		}

	}

	public void removeByInsatnce(int instanceId) throws WorkflowException{
		try{
			CurrentTaskBean bean = new CurrentTaskBean();
			bean.removeByInstance(instanceId);
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
	}

	/**
	 * 
	 * 
	 * @param instanceId
	 * @param nodeId
	 * @param conn
	 * @return
	 * @throws WorkflowException
	 * @throws
	 * @see
	 */
	public int getTaskNumByNode(int instanceId, int nodeId) throws WorkflowException{
		int result = 0;

		try{
			CountQuery query = new CountQuery();
			result = query.getTaskNumByNode(instanceId, nodeId).getCount().intValue();
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
		return result;
	}

	public int getMainTaskNumByNode(int instanceId, int nodeId) throws WorkflowException{
		int result = 0;

		try{
			CountQuery query = new CountQuery();
			result = query.getMainTaskNumByNode(instanceId, nodeId).getCount().intValue();
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
		return result;
	}

	public List getTodoListByOwner(String owner) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByOwner(owner);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByOwnerAndTemplate(String owner, String templateType) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByOwnerAndTemplate(owner, templateType);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByOwnerAndTemplate(String owner, int templateId) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByOwnerAndTemplate(owner, templateId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByOwnerAndInstance(String owner, int instanceId) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByOwnerAndInstance(owner, instanceId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByOwnerAndNode(String owner, String businessType) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByOwnerAndNode(owner, businessType);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByOwnerAndNode(String owner, int nodeId) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByOwnerAndNode(owner, nodeId);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoListByExecutor(String executor) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			ArrayList list = query.getTodoListByExecutor(executor);
			CurrentTaskMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((CurrentTaskInfo) list.get(i));
			}
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoCompoListByExecutor(String executor) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			result = query.getTodoCompoListByExecutor(executor);
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getTodoInstListByExecutor(String executor) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			result = query.getTodoInstListByExecutor(executor);
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	public List getInvalidTodoInstListByExecutor(String executor) throws WorkflowException{
		List result = new ArrayList();

		try{
			CurrentTaskQuery query = new CurrentTaskQuery();
			result = query.getInvalidTodoInstListByExecutor(executor);
		}catch(SQLException sqle){
			throw new WorkflowException(1145, sqle.toString());
		}
		return result;
	}

	private CurrentTaskMeta wrap(CurrentTaskInfo model){
		CurrentTaskMeta meta = new CurrentTaskMeta();
		if(model.getCurrentTaskId() != null)
			meta.setCurrentTaskId(model.getCurrentTaskId().intValue());
		if(model.getInstanceId() != null)
			meta.setInstanceId(model.getInstanceId().intValue());
		meta.setInstanceName(model.getInstanceName());
		meta.setInstanceDescription(model.getInstanceDescription());
		meta.setInstanceStartTime(model.getInstanceStartTime());
		meta.setInstanceEndTime(model.getInstanceEndTime());
		if(model.getTemplateId() != null)
			meta.setTemplateId(model.getTemplateId().intValue());
		meta.setTemplateName(model.getTemplateName());
		meta.setTemplateType(model.getTemplateType());
		if(model.getNodeId() != null)
			meta.setNodeId(model.getNodeId().intValue());
		meta.setNodeName(model.getNodeName());
		meta.setBusinessType(model.getBusinessType());
		meta.setExecutor(model.getExecutor());
		meta.setExecutorName(model.getExecutorName());
		if(model.getDelegationId() != null)
			meta.setIdentity(model.getDelegationId().intValue());
		meta.setOwner(model.getOwner());
		meta.setOwnerName(model.getOwnerName());
		meta.setCreator(model.getCreator());
		meta.setCreatorName(model.getCreatorName());
		meta.setCreateTime(model.getCreateTime());
		meta.setLimitExecuteTime(model.getLimitExecuteTime());
		if(model.getResponsibility() != null)
			meta.setResponsibility(model.getResponsibility().intValue());
		if(model.getParentTaskId() != null)
			meta.setParentTaskId(model.getParentTaskId().intValue());
		if(model.getCompoId() != null)
			meta.setCompoId(model.getCompoId());

		return meta;
	}

	private CurrentTaskMeta wrap(CurrentTaskModel model){
		CurrentTaskMeta meta = new CurrentTaskMeta();
		if(model.getCurrentTaskId() != null)
			meta.setCurrentTaskId(model.getCurrentTaskId().intValue());
		if(model.getInstanceId() != null)
			meta.setInstanceId(model.getInstanceId().intValue());
		if(model.getNodeId() != null)
			meta.setNodeId(model.getNodeId().intValue());
		meta.setExecutor(model.getExecutor());
		if(model.getDelegationId() != null)
			meta.setIdentity(model.getDelegationId().intValue());
		meta.setOwner(model.getOwner());
		meta.setCreator(model.getCreator());
		meta.setCreateTime(model.getCreateTime());
		meta.setLimitExecuteTime(model.getLimitExecuteTime());
		if(model.getResponsibility() != null)
			meta.setResponsibility(model.getResponsibility().intValue());
		if(model.getParentTaskId() != null)
			meta.setParentTaskId(model.getParentTaskId().intValue());

		return meta;
	}

	private CurrentTaskModel unwrap(CurrentTaskMeta meta){
		CurrentTaskModel unwrapper = new CurrentTaskModel();
		if(meta.getCurrentTaskId() != 0)
			unwrapper.setCurrentTaskId(meta.getCurrentTaskId());
		if(meta.getInstanceId() != 0)
			unwrapper.setInstanceId(meta.getInstanceId());
		if(meta.getNodeId() != 0)
			unwrapper.setNodeId(meta.getNodeId());
		if(meta.getExecutor() != null)
			unwrapper.setExecutor(meta.getExecutor());
		if(meta.getIdentity() != 0)
			unwrapper.setDelegationId(meta.getIdentity());
		if(meta.getOwner() != null)
			unwrapper.setOwner(meta.getOwner());
		if(meta.getCreator() != null)
			unwrapper.setCreator(meta.getCreator());
		if(meta.getCreateTime() != null)
			unwrapper.setCreateTime(meta.getCreateTime());
		if(meta.getLimitExecuteTime() != null)
			unwrapper.setLimitExecuteTime(meta.getLimitExecuteTime());
		if(meta.getResponsibility() != 0)
			unwrapper.setResponsibility(meta.getResponsibility());
		if(meta.getParentTaskId() != 0)
			unwrapper.setParentTaskId(meta.getParentTaskId());
		return unwrapper;
	}

	/**
	 * @param currentTaskId
	 * @return
	 * @throws WorkflowException
	 * @throws SQLException
	 */
	public int getTaskType(int currentTaskId) throws WorkflowException{
		int result;
		try{
			CurrentTaskBean ctb = new CurrentTaskBean();
			CurrentTaskModel ctm = ctb.findByKey(currentTaskId);
			CurrentTaskMeta currentTaskMeta = wrap(ctm);
			int parentTaskId = currentTaskMeta.getParentTaskId();
			switch(parentTaskId){
			case CurrentTaskMeta.TASK_TYPE_COLLECTED:
				result = TYPE_COLLECTED;
				break;
			case CurrentTaskMeta.TASK_TYPE_NORMAL:
				result = TYPE_NORMAL;
				break;
			case CurrentTaskMeta.TASK_TYPE_TOCOLLECT:
				result = TYPE_TOCOLLECT;
				break;
			case CurrentTaskMeta.TASK_TYPE_TOCOLLECT_DETAIL:
				result = TYPE_TOCOLLECT_DETAIL;
				break;
			default:
				result = TYPE_COLLECTED_DETAIL;
				break;
			}
		}catch(SQLException e){
			// TCJLODO: handle exception
			throw new WorkflowException(2000, e.getMessage());
		}
		return result;
	}

	/**
	 * Bussness Lay only supply DTO Object
	 * 
	 * @param conn
	 * @param currentTaskId
	 * @return
	 * @throws WorkflowException
	 */
	public List getChildToDoListByParentId(int currentTaskId) throws WorkflowException{
		// TCJLODO Auto-generated method stub
		List result = new ArrayList();
		try{
			CurrentTaskQuery ctb = new CurrentTaskQuery();
			List queryResult = ctb.getChildToDoListByParentId(currentTaskId);
			Iterator iter = queryResult.iterator();
			while(iter.hasNext()){
				CurrentTaskInfo element = (CurrentTaskInfo) iter.next();
				result.add(wrap(element));
			}
		}catch(SQLException e){
			// TCJLODO: handle exception
			throw new WorkflowException(2000, e.getMessage());
		}
		return result;
	}

	/**
	 * ���ָ���û���ָ������ģ���ָ�������Ƿ��Լ��Ƿ��Ѿ��д���������
	 * 
	 * @return
	 * @throws WorkflowException
	 */
	public boolean isExistToCollectTask(String executor, int templateId) throws WorkflowException{
		CurrentTaskQuery ctq = new CurrentTaskQuery();
		ArrayList list;
		try{
			list = ctq.findTaskByTemplateIdAndExecutorAndParentId(-2, templateId, executor);
		}catch(SQLException e){
			e.printStackTrace();
			throw new WorkflowException(2000, e.getMessage());
		}
		return (list.size() == 0) ? false : true;
	}

	/**
	 * ���ָ���û���ָ������ģ���ָ�������Ƿ��Լ��Ƿ��Ѿ��д���������
	 * 
	 * @return
	 * @throws WorkflowException
	 */
	CurrentTaskMeta findToCollectTask(String executor, int templateId) throws WorkflowException{
		CurrentTaskQuery ctq = new CurrentTaskQuery();
		ArrayList list;
		try{
			list = ctq.findTaskByTemplateIdAndExecutorAndParentId(-2, templateId, executor);
		}catch(SQLException e){
			e.printStackTrace();
			throw new WorkflowException(2000, e.getMessage());
		}
		if(list.size() != 1)
			throw new WorkflowException(2204, "ϵͳ�쳣���޷���ȡ��������");

		return wrap((CurrentTaskInfo) list.get(0));
	}

	/**
	 * ����ParentTaskId,ParentTaskId��ʾ���������ͣ���ϸ��ο�CurrentTaskMeta����ParentTaskId �Ľ��͡�
	 * 
	 * @param conn
	 * @param currentTaskId
	 * @param i
	 * @throws WorkflowException
	 */
	public void setParentTaskId(int instanceId, int TASK_TYPE) throws WorkflowException{
		// TCJLODO Auto-generated method stub
		CurrentTaskQuery ctq = new CurrentTaskQuery();
		CurrentTaskBean ctb = new CurrentTaskBean();
		try{
			List modelList = ctq.getTodoListByInstance(instanceId, 1);
			Iterator iter = modelList.iterator();
			while(iter.hasNext()){
				CurrentTaskInfo model = (CurrentTaskInfo) iter.next();
				int parent_task_id;
				switch(TASK_TYPE){
				case TYPE_COLLECTED:
					parent_task_id = CurrentTaskMeta.TASK_TYPE_COLLECTED;
					break;
				case TYPE_TOCOLLECT:
					parent_task_id = CurrentTaskMeta.TASK_TYPE_TOCOLLECT;
					break;
				case TYPE_TOCOLLECT_DETAIL:
					parent_task_id = CurrentTaskMeta.TASK_TYPE_TOCOLLECT_DETAIL;
					break;
				case TYPE_NORMAL:
					parent_task_id = CurrentTaskMeta.TASK_TYPE_NORMAL;
					break;
				default:
					parent_task_id = TASK_TYPE;
				}
				model.setParentTaskId(parent_task_id);
				ctb.update(unwrap(wrap(model)));
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new WorkflowException(2000, e.getMessage());
		}

	}

	/**
	 * �����ƶ�����id���������߸��ӹ�ϵ����������������л�ʹ�õ�������ĸ��
	 * 
	 * @param childTaskId
	 * @param parentTaskId
	 * @param conn
	 * @throws WorkflowException
	 */
	public void collectChildTaskToParentTask(int childTaskId, int parentTaskId) throws WorkflowException{
		CurrentTaskBean taskBean = new CurrentTaskBean();
		try{
			CurrentTaskMeta childTaskMeta = wrap(taskBean.findByKey(childTaskId));
			childTaskMeta.setParentTaskId(parentTaskId);
			taskBean.update(unwrap(childTaskMeta));
			if(getTaskType(parentTaskId) == TYPE_TOCOLLECT){
				CurrentTaskMeta parentTaskMeta = wrap(taskBean.findByKey(parentTaskId));
				parentTaskMeta.setParentTaskId(CurrentTaskMeta.TASK_TYPE_COLLECTED);
				taskBean.update(unwrap(parentTaskMeta));
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new WorkflowException(2000);
		}
	}
	
	public void removeByExecutor(int instanceId, int nodeId,
			String executor) throws WorkflowException {
		CurrentTaskBean bean = new CurrentTaskBean();
		try {
			bean.removeByExecutor(instanceId, nodeId, executor);
		} catch (SQLException sqle) {
			throw new WorkflowException(1126, sqle.toString());
		}
	}
}
