package com.kingdrive.workflow;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.kingdrive.workflow.business.ActionHistory;
import com.kingdrive.workflow.business.CurrentTask;
import com.kingdrive.workflow.business.Document;
import com.kingdrive.workflow.business.Instance;
import com.kingdrive.workflow.business.Node;
import com.kingdrive.workflow.business.StateValue;
import com.kingdrive.workflow.business.TaskExecutor;
import com.kingdrive.workflow.business.TaskTerm;
import com.kingdrive.workflow.business.Template;
import com.kingdrive.workflow.business.Variable;
import com.kingdrive.workflow.business.VariableValue;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.DocumentMeta;
import com.kingdrive.workflow.dto.InstanceMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TaskMeta;
import com.kingdrive.workflow.dto.TaskResultMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.DateTime;

/**
 * �����������ڽӿ�
 * <p>
 * Title: ������ϵͳ
 * </p>
 * <p>
 * Description: �����������ڽӿ�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: �Ϻ������Ƽ����޹�˾
 * </p>
 * 
 * @author �Ϻ������Ƽ����޹�˾
 * @version 1.1
 */
public class ExecuteFacade {

	public ExecuteFacade() {
	}

	/**
	 * ��Ȩ����
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param executor
	 *            int[]
	 * @param responsibility
	 *            int[]
	 * @param authorizor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void authorizeTask(int instanceId, int nodeId,
			String[] executor, int responsibility[], String authorizor,
			String comment) throws WorkflowException {
		String authorizeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.authorize(instanceId, nodeId, executor, responsibility,
				authorizor, authorizeTime, comment);
	}

	/**
	 * �ƽ�����
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param currentTaskId
	 *            int
	 * @param executor
	 *            int[]
	 * @param responsibility
	 *            int[]
	 * @param forwarder
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void forwardTask(int instanceId, int nodeId,
			int currentTaskId, String[] executor, int responsibility[],
			String forwarder, String comment) throws WorkflowException {
		String forwardTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.forward(instanceId, nodeId, currentTaskId, executor,
				responsibility, forwarder, forwardTime, comment);
	}

	/**
	 * ��ʵ�������лص���ǰ������ڵ�
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void callbackFlow(int instanceId, int nodeId,
			String executor, String comment) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.callback(instanceId, nodeId, executor, executeTime, comment);
	}

	/**
	 * ����ʵ����������ǰ����ڵ��ԭǰ������ڵ㣬
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void givebackFlow(int instanceId, int nodeId,
			String executor, String comment) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.giveback(instanceId, nodeId, executor, executeTime, comment);
	}

	/**
	 * ����ʵ����������ǰ����ڵ�ľ�����������ڵ㣬
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void untreadFlow(int instanceId, int nodeId, int prevNodeId,
			String executor, String comment) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.untread(instanceId, nodeId, prevNodeId, executor, executeTime,
				comment);
	}

	/**
	 * ���˻������̵����ܽڵ� �����봦��������̵������̵Ļ���
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void givebackCollectedFlow(int instanceId, int nodeId,
			String executor, String comment) throws WorkflowException {
		Instance instanceHandler = new Instance();
		CurrentTask taskHandler = new CurrentTask();
		InstanceMeta instanceMeta = instanceHandler.getInstance(instanceId);
		int templateId = instanceMeta.getTemplateId();

		// ��ȡ���ܽڵ�
		int collectNodeId = instanceHandler.getPassedCollectNodeId(instanceId);
		List childTodoList = instanceHandler.getChildTodoListByParentInstance(
				instanceId, executor);
		// �������б����ܵ�������
		Iterator iter = childTodoList.iterator();
		while (iter.hasNext()) {
			CurrentTaskMeta element = (CurrentTaskMeta) iter.next();

			untreadFlow(element.getInstanceId(), nodeId, collectNodeId,
					executor, comment);
			// givebackFlow(element.getInstanceId(),nodeId,collectNodeId,executor,
			// comment,conn);
			taskHandler.setParentTaskId(element.getInstanceId(),
					CurrentTask.TYPE_TOCOLLECT_DETAIL);

			instanceHandler.update(element.getInstanceId(),
					Instance.MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT_DETAIL,
					Instance.MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT_DETAIL);
			// ȥ�����̵ĸ��ӹ�ϵ
			instanceHandler.setParentInstanceId(element.getInstanceId(), 0);
		}

		// ɾ�����������Լ�����
		removeInstance(instanceId);
		String instanceName = Instance.MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT;
		String instanceDescription = Instance.MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT;
		int toCollectInstanceId = instanceHandler.getToCollectTaskId(
				templateId, collectNodeId, executor, instanceName,
				instanceDescription);
		// instanceHandler.setParentInstanceId(instanceId,toCollectInstance,conn);
		instanceHandler.update(toCollectInstanceId,
				Instance.MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT,
				Instance.MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT);
	}

	/**
	 * ��ֹʵ��
	 * 
	 * @param instanceId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void interruptInstance(int instanceId, String executor,
			String comment) throws WorkflowException {
		String currentTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.interrupt(instanceId, executor, currentTime, comment);
	}

	/**
	 * ָ��ʵ���Ƿ�ִ�����
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return boolean
	 */
	public static boolean isInstanceFinished(int instanceId)
			throws WorkflowException {
		Instance instance = new Instance();
		return instance.isInstanceFinished(instanceId);
	}

	/**
	 * ����ʵ��
	 * 
	 * @param instanceId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void deactivateInstance(int instanceId, String executor,
			String comment) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.deactivate(instanceId, executor, executeTime, comment);
	}

	/**
	 * ����ʵ��
	 * 
	 * @param instanceId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void activateInstance(int instanceId, String executor,
			String comment) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.activate(instanceId, executor, executeTime, comment);
	}

	/**
	 * ����ʵ��������������
	 * 
	 * @param instanceId
	 *            int
	 * @param instanceName
	 *            String
	 * @param instanceDescription
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void updateInstance(int instanceId, String instanceName,
			String instanceDescription) throws WorkflowException {
		Instance instance = new Instance();
		instance.update(instanceId, instanceName, instanceDescription);
	}

	/**
	 * ��תʵ�����̵�ָ���ڵ�
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void transferFlow(int instanceId, int nodeId, int nextNodeId,
			String executor, String comment) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.transfer(instanceId, nodeId, nextNodeId, executor,
				executeTime, comment);
	}

	/**
	 * ����ʵ������ ���ʵ����״̬Ϊ��ֹ��INSTANCE_STATUS_INTERRUPTED����originalΪtrue���ʵ��������ԭִ��λ��
	 * ��ʼ��������ʼ����ڵ㣬����ǰ�粻��������ִ���ߣ���ִ����ȱʡΪ������ִ����
	 * 
	 * @param instanceId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param original
	 *            boolean ���ʵ����״̬Ϊ��ֹ�ģ��Ƿ�������ʵ����ԭִ��λ�ã�true��������������ʵ���ĳ�ʼ״̬��false����
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void restartInstance(int instanceId, String executor,
			String comment, boolean original) throws WorkflowException {
		String executeTime = DateTime.getSysTime();
		Instance instance = new Instance();
		instance.restart(instanceId, executor, executeTime, comment, original);
	}

	/**
	 * ����ʵ���������ش����ߵĵ�ǰ����
	 * 
	 * @param templateId
	 *            int
	 * @param name
	 *            String
	 * @param description
	 *            String
	 * @param creator
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return TaskMeta
	 */
	public static TaskMeta createInstance(int templateId, String name,
			String description, String creator) throws WorkflowException {
		String createTime = DateTime.getSysTime();
		Instance instance = new Instance();
		return instance.create(templateId, name, description, creator,
				createTime);
	}

	/**
	 * ɾ��ʵ��
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void removeInstance(int instanceId) throws WorkflowException {
		Instance instance = new Instance();
		instance.remove(instanceId);
	}

	/**
	 * ������ʵ�����й�����ȡ�õ�ǰ����
	 * 
	 * @param currentTaskId
	 *            int ��ǰ����id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return TaskMeta
	 */
	public static TaskMeta getTask(int currentTaskId, int isValid)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTask(currentTaskId, isValid);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoList() throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoList();
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param templateType
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByTemplate(String templateType)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByTemplate(templateType);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param templateId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByTemplate(int templateId)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByTemplate(templateId);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param templateType
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByExecutorAndTemplate(String executor,
			String templateType) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByExecutorAndTemplate(executor, templateType);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param templateId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByExecutorAndTemplate(String executor,
			int templateId) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByExecutorAndTemplate(executor, templateId);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByInstance(int instanceId, int isValid)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByInstance(instanceId, isValid);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByExecutorAndInstance(String executor,
			int instanceId, int isValid) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByExecutorAndInstance(executor, instanceId,
				isValid);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param businessType
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByNode(String businessType)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByNode(businessType);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param nodeId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByNode(int instanceId, int nodeId)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByNode(instanceId, nodeId);
	}

	public static List getTodoListByUserCompo(String userId, String compoId)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByUserCompo(userId, compoId);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param businessType
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByExecutorAndNode(String executor,
			String businessType) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByExecutorAndNode(executor, businessType);
	}

	/**
	 * ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param nodeId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects.
	 */
	public static List getTodoListByExecutorAndNode(String executor, int nodeId)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByExecutorAndNode(executor, nodeId);
	}

	/**
	 * ִ�����񣬲���ʵ���ɽ���ʱ��������ʵ�� ִ������ǰ��ֱ����Ƿ��������ͬ����,�������ִ��֮
	 * 
	 * @param currentTaskId
	 *            int ��ǰ����id
	 * @param instanceId
	 *            int �����ʵ��id
	 * @param instanceName
	 *            String ʵ������
	 * @param instanceDescription
	 *            String ʵ������
	 * @param nodeId
	 *            int �������ڽڵ�id
	 * @param action
	 *            String �ڵ������ϵĶ���
	 * @param comment
	 *            String ��ע
	 * @param variableValueList
	 *            List a list of VariableValueMeta objects.
	 * @param executor
	 *            int ִ����id
	 * @param positionId
	 *            int ����ӵ����ִ�е�ǰ����������ְλid
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return TaskResultMeta
	 */
	public static TaskResultMeta executeTask(int currentTaskId, int instanceId,
			String instanceName, String instanceDescription, int nodeId,
			String action, String comment, List variableValueList,
			String executor, String positionId) throws WorkflowException {
		TaskResultMeta result = new TaskResultMeta();

		Instance instanceHandler = new Instance();
		String executeTime = DateTime.getSysTime();
		// ����������Instance.execute(...)������ִ��
		/*
		 * TaskListener taskListener = null; String taskListenerName =
		 * nodeHandler.getNode(nodeId) .getTaskListener(); if (taskListenerName !=
		 * null && !taskListenerName.trim().equals("")) { try { taskListener =
		 * (TaskListener) Class.forName(taskListenerName) .newInstance(); }
		 * catch (Exception e) { throw new WorkflowException(1210,
		 * e.toString()); } }
		 */

		CurrentTask taskHandler = new CurrentTask();
		int taskType = taskHandler.getTaskType(currentTaskId);

		result = instanceHandler.execute(currentTaskId, instanceId,
				instanceName, instanceDescription, nodeId, action, comment,
				variableValueList, executor, positionId, executeTime);
		if (taskType == CurrentTask.TYPE_COLLECTED) {// ���ܴ�������
			// ��������Ҳ�ύ
			List childTodoList = instanceHandler
					.getChildTodoListByParentInstance(instanceId, executor);
			if (childTodoList.size() == 0) {
				if (isInstanceFinished(instanceId)) {
					childTodoList = instanceHandler
							.getChildToListByParentTaskId(currentTaskId,
									executor);
				}
			}
			Iterator iter = childTodoList.iterator();
			while (iter.hasNext()) {
				CurrentTaskMeta elem = (CurrentTaskMeta) iter.next();

				int c_currentTaskId = elem.getCurrentTaskId();
				int c_instanceId = elem.getInstanceId();
				String c_instanceName = elem.getInstanceName();
				String c_instanceDescription = elem.getInstanceName();
				/*
				 * instanceHandler.execute(c_currentTaskId, c_instanceId,
				 * c_instanceName, c_instanceDescription, nodeId, action,
				 * comment, variableValueList, executor, positionId,
				 * executeTime);
				 */
				executeTask(c_currentTaskId, c_instanceId, c_instanceName,
						c_instanceDescription, nodeId, action, comment,
						variableValueList, executor, positionId);
			}
		}
		/*
		 * if (taskListener != null) { taskListener.afterExecution(currentTask); }
		 */
		return result;
	}
  //ֱ�Ӵ���nextLinkList
  public static TaskResultMeta executeTask(int currentTaskId, int instanceId,
    String instanceName, String instanceDescription, int nodeId,
    String action, String comment, List variableValueList,List nextLinkList,
    String executor, String positionId) throws WorkflowException {
  TaskResultMeta result = new TaskResultMeta();

  Instance instanceHandler = new Instance();
  String executeTime = DateTime.getSysTime();

  CurrentTask taskHandler = new CurrentTask();
  int taskType = taskHandler.getTaskType(currentTaskId);

  result = instanceHandler.execute(currentTaskId, instanceId,
      instanceName, instanceDescription, nodeId, action, comment,
      variableValueList,nextLinkList, executor, positionId, executeTime,true);
  if (taskType == CurrentTask.TYPE_COLLECTED) {// ���ܴ�������
    // ��������Ҳ�ύ
    List childTodoList = instanceHandler
        .getChildTodoListByParentInstance(instanceId, executor);
    if (childTodoList.size() == 0) {
      if (isInstanceFinished(instanceId)) {
        childTodoList = instanceHandler
            .getChildToListByParentTaskId(currentTaskId,
                executor);
      }
    }
    Iterator iter = childTodoList.iterator();
    while (iter.hasNext()) {
      CurrentTaskMeta elem = (CurrentTaskMeta) iter.next();

      int c_currentTaskId = elem.getCurrentTaskId();
      int c_instanceId = elem.getInstanceId();
      String c_instanceName = elem.getInstanceName();
      String c_instanceDescription = elem.getInstanceName();
      /*
       * instanceHandler.execute(c_currentTaskId, c_instanceId,
       * c_instanceName, c_instanceDescription, nodeId, action,
       * comment, variableValueList, executor, positionId,
       * executeTime);
       */
      executeTask(c_currentTaskId, c_instanceId, c_instanceName,
          c_instanceDescription, nodeId, action, comment,
          variableValueList,executor, positionId);//TODO:�д���һ��ȷ֤::
    }
  }
  /*
   * if (taskListener != null) { taskListener.afterExecution(currentTask); }
   */
  return result;
}

	public static void rework(int instanceId, String executor, String comment)
			throws WorkflowException {
		Instance instanceHandler = new Instance();
		String executeTime = DateTime.getSysTime();
		instanceHandler.rework(instanceId, executor, executeTime, comment);
	}

	/**
	 * ȡ��ָ��ִ���߿�ִ�е�����
	 * 
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of TemplateMeta objects
	 */
	public static List getActiveTemplateList() throws WorkflowException {
		Template templateHandler = new Template();
		return templateHandler.getActiveTemplateList();
	}

	/**
	 * ȡ��ָ��ִ���߿�ִ�е�����
	 * 
	 * @param executor
	 *            int ִ����id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of TemplateMeta objects
	 */
	public static List getActiveTemplateListByExecutor(String executor)
			throws WorkflowException {
		Template templateHandler = new Template();
		return templateHandler.getActiveTemplateListByExecutor(executor);
	}

	/**
	 * ȡ��ָ��ִ���߿�ִ�е�����
	 * 
	 * @param templateType
	 *            String ��������
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of TemplateMeta objects
	 */
	public static List getActiveTemplateList(String templateType)
			throws WorkflowException {
		Template templateHandler = new Template();
		return templateHandler.getActiveTemplateList(templateType);
	}

	/**
	 * ȡ��ָ��ִ���߿�ִ�е�����
	 * 
	 * @param executor
	 *            int ִ����id
	 * @param templateType
	 *            String ��������
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of TemplateMeta objects
	 */
	public static List getActiveTemplateListByExecutor(String executor,
			String templateType) throws WorkflowException {
		Template templateHandler = new Template();
		return templateHandler.getActiveTemplateListByExecutor(executor,
				templateType);
	}

	/**
	 * ����ԭӵ����ȡ�ô�������
	 * 
	 * @param owner
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByOwner(String owner)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByOwner(owner);
	}

	/**
	 * ����ԭӵ����ȡ�ô�������
	 * 
	 * @param owner
	 *            int
	 * @param templateType
	 *            String ��������
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByOwnerAndTemplate(String owner,
			String templateType) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByOwnerAndTemplate(owner, templateType);
	}

	/**
	 * ����ԭӵ����ȡ�ô�������
	 * 
	 * @param owner
	 *            int
	 * @param templateId
	 *            int ����id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByOwnerAndTemplate(String owner,
			int templateId, Connection conn) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByOwnerAndTemplate(owner, templateId);
	}

	/**
	 * ����ԭӵ����ȡ�ô�������
	 * 
	 * @param owner
	 *            int
	 * @param instanceId
	 *            int ʵ��id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByOwnerAndInstance(String owner,
			int instanceId, Connection conn) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByOwnerAndInstance(owner, instanceId);
	}

	/**
	 * ����ԭӵ����ȡ�ô�������
	 * 
	 * @param owner
	 *            int
	 * @param businessType
	 *            String ҵ�������������
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByOwnerAndNode(String owner,
			String businessType, Connection conn) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByOwnerAndNode(owner, businessType);
	}

	/**
	 * ����ԭӵ����ȡ�ô�������
	 * 
	 * @param owner
	 *            int
	 * @param nodeId
	 *            int �ڵ�id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByOwnerAndNode(String owner, int nodeId,
			Connection conn) throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByOwnerAndNode(owner, nodeId);
	}

	/**
	 * ����ִ����ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoListByExecutor(String executor)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoListByExecutor(executor);
	}

	public static List getTodoCompoListByExecutor(String executor)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoCompoListByExecutor(executor);
	}

	/**
	 * ����ִ����ȡ�ô�������
	 * 
	 * @param executor
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of CurrentTaskMeta objects
	 */
	public static List getTodoInstListByExecutor(String executor)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getTodoInstListByExecutor(executor);
	}

	public static List getInvalidTodoInstListByExecutor(String executor)
			throws WorkflowException {
		CurrentTask task = new CurrentTask();
		return task.getInvalidTodoInstListByExecutor(executor);
	}

	public static List getInvalidDoneInstListByExecutor(String executor)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getInvalidDoneInstListByExecutor(executor);
	}

	/**
	 * ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionList(String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionList(startTime, endTime);
	}

	/**
	 * ��������idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param templateId
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByTemplate(int templateId,
			String startTime, String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByTemplate(templateId, startTime, endTime);
	}

	/**
	 * ������������ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param templateType
	 *            String
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByTemplate(String templateType,
			String startTime, String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history
				.getActionListByTemplate(templateType, startTime, endTime);
	}

	/**
	 * ����ʵ��idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByInstance(int instanceId, int isValid)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByInstance(instanceId, isValid);
	}

	/**
	 * ���ݽڵ�idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param nodeId
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByNode(int nodeId, String startTime,
			String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByNode(nodeId, startTime, endTime);
	}

	/**
	 * ���ݽڵ�ҵ������ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param businessType
	 *            String
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByNode(String businessType,
			String startTime, String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByNode(businessType, startTime, endTime);
	}

	/**
	 * ����ʵ��ӵ���ߣ�ʵ�������ߣ�ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param owner
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByOwner(String owner, String startTime,
			String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByOwner(owner, startTime, endTime);
	}

	/**
	 * ����ʵ��ӵ���ߣ�ʵ�������ߣ�������idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param owner
	 *            int
	 * @param templateId
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByOwnerAndTemplate(String owner,
			int templateId, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByOwnerAndTemplate(owner, templateId,
				startTime, endTime);
	}

	/**
	 * ����ʵ��ӵ���ߣ�ʵ�������ߣ�����������ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param owner
	 *            int
	 * @param templateType
	 *            String
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByOwnerAndTemplate(String owner,
			String templateType, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByOwnerAndTemplate(owner, templateType,
				startTime, endTime);
	}

	/**
	 * ����ʵ��ӵ���ߣ�ʵ�������ߣ���ʵ��idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param owner
	 *            int
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByOwnerAndInstance(String owner,
			int instanceId, int isValid) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByOwnerAndInstance(owner, instanceId,
				isValid);
	}

	/**
	 * ����ʵ��ӵ���ߣ�ʵ�������ߣ���ڵ�idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param owner
	 *            int
	 * @param nodeId
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByOwnerAndNode(String owner, int nodeId,
			String startTime, String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByOwnerAndNode(owner, nodeId, startTime,
				endTime);
	}

	/**
	 * ����ʵ��ӵ���ߣ�ʵ�������ߣ���ڵ�ҵ������ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param owner
	 *            int
	 * @param businessType
	 *            String
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByOwnerAndNode(String owner,
			String businessType, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByOwnerAndNode(owner, businessType,
				startTime, endTime);
	}

	/**
	 * ��������ִ����ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param executor
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByExecutor(String executor,
			String startTime, String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByExecutor(executor, startTime, endTime);
	}

	public static List getActionInstListByExecutor(String executor,
			String startTime, String endTime) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history
				.getActionInstListByExecutor(executor, startTime, endTime);
	}

	public static List getActionCompoListByExecutor(String executor)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionCompoListByExecutor(executor);
	}

	/**
	 * ��������ִ����������idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param executor
	 *            int
	 * @param templateId
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByExecutorAndTemplate(String executor,
			int templateId, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByExecutorAndTemplate(executor, templateId,
				startTime, endTime);
	}

	/**
	 * ��������ִ��������������ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param executor
	 *            int
	 * @param templateType
	 *            String
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByExecutorAndTemplate(String executor,
			String templateType, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByExecutorAndTemplate(executor,
				templateType, startTime, endTime);
	}

	/**
	 * ��������ִ������ʵ��idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param executor
	 *            int
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByExecutorAndInstance(String executor,
			int instanceId, int isValid) throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByExecutorAndInstance(executor, instanceId,
				isValid);
	}

	/**
	 * ��������ִ������ڵ�idȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param executor
	 *            int
	 * @param nodeId
	 *            int
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByExecutorAndNode(String executor,
			int nodeId, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByExecutorAndNode(executor, nodeId,
				startTime, endTime);
	}

	/**
	 * ��������ִ������ڵ�ҵ������ȡ���Ѱ�����(ִ�ж���)�б�
	 * 
	 * @param executor
	 *            int
	 * @param businessType
	 *            String
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of ActionMeta objects
	 */
	public static List getActionListByExecutorAndNode(String executor,
			String businessType, String startTime, String endTime)
			throws WorkflowException {
		ActionHistory history = new ActionHistory();
		return history.getActionListByExecutorAndNode(executor, businessType,
				startTime, endTime);
	}

	/**
	 * ȡ��ָ��ʵ��id��ʵ��
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return InstanceMeta
	 */
	public static InstanceMeta getInstance(int instanceId)
			throws WorkflowException {
		Instance instance = new Instance();
		return instance.getInstance(instanceId);
	}

	/**
	 * ȡ�û��ʵ���б�
	 * 
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getActiveInstanceList() throws WorkflowException {
		Instance instance = new Instance();
		return instance.getActiveInstanceList();
	}

	/**
	 * ȡ�û��ʵ���б�
	 * 
	 * @param templateId
	 *            int ����id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getActiveInstanceList(int templateId)
			throws WorkflowException {
		Instance instance = new Instance();
		return instance.getActiveInstanceList(templateId);
	}

	/**
	 * ȡ�û��ʵ���б�
	 * 
	 * @param templateType
	 *            String ��������
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getActiveInstanceList(String templateType)
			throws WorkflowException {
		Instance instance = new Instance();
		return instance.getActiveInstanceList(templateType);
	}

	/**
	 * ȡ�û��ʵ���б�
	 * 
	 * @param templateId
	 *            int ����id
	 * @param owner
	 *            int ʵ��ӵ���ߣ������ߣ�id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getActiveInstanceList(int templateId, String owner,
			Connection conn) throws WorkflowException {
		Instance instance = new Instance();
		return instance.getActiveInstanceList(templateId, owner);
	}

	/**
	 * ȡ��ʵ���б�(��������������ʵ��)
	 * 
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getInstanceList() throws WorkflowException {
		Instance instance = new Instance();
		return instance.getInstanceList();
	}

	/**
	 * ȡ��ʵ���б�(��������������ʵ��)
	 * 
	 * @param templateId
	 *            int ����id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getInstanceList(int templateId) throws WorkflowException {
		Instance instance = new Instance();
		return instance.getInstanceList(templateId);
	}

	/**
	 * ȡ��ʵ���б�(��������������ʵ��)
	 * 
	 * @param templateId
	 *            int ����id
	 * @param owner
	 *            int ʵ��ӵ���ߣ������ߣ�id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of InstanceMeta objects
	 */
	public static List getInstanceList(int templateId, String owner)
			throws WorkflowException {
		Instance instance = new Instance();
		return instance.getInstanceList(templateId, owner);
	}

	/**
	 * ��ø�ʵ������ĳ�����ڵ�֮���Ѿ�ִ�еĽڵ��б�
	 * 
	 * @param templateId
	 * @param toNodeId
	 * @param preNodeId
	 * @return
	 * @throws WorkflowException
	 */
	public static List getExecutedActionListBetweenTimeOrder(int templateId,
			int instanceId, int fromNodeId, int toNodeId,
			boolean onlyNormalAction, Connection conn) throws WorkflowException {
		return new Instance().getExecutedActionListBetweenTimeOrder(templateId,
				instanceId, fromNodeId, toNodeId, onlyNormalAction);
	}

	public static List getExecutedNodeListBetween(int templateId,
			int instanceId, int nodeId, int prevNodeId, boolean onlyGetLastest)
			throws WorkflowException {
		List result = new Instance().getExecutedNodeListBetween(templateId,
				instanceId, nodeId, prevNodeId, onlyGetLastest);
		if (result != null) {
			result.remove(new NodeMeta());// ȥ�������ӵĿ�Node
		}
		return result;
	}

	public static Map getFollowedTaskNodeActionMap(int templateId, int nodeId,
			Connection conn) throws WorkflowException {
		Node handler = new Node();
		return handler.getFollowedTaskNodeActionMap(templateId, nodeId);
	}

	/**
	 * ȡ��ָ������ڵ�id�����к�������ڵ㣬�Զ�̬���ӽڵ�ִ����
	 * 
	 * @param templateId
	 *            int
	 * @param nodeId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of node objects
	 */
	public static List getFollowedTaskNodeList(int templateId, int nodeId,
			Connection conn) throws WorkflowException {
		Node nodeHandler = new Node();
		return nodeHandler.getFollowedTaskNodeList(templateId, nodeId);
	}

	/**
	 * ȡ��ָ������ڵ�id�붯�����Ƶ����к�������ڵ㣬�Զ�̬���ӽڵ�ִ����
	 * 
	 * @param templateId
	 *            int
	 * @param nodeId
	 *            int
	 * @param action
	 *            String
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of node objects
	 */
	public static List getFollowedTaskNodeList(int templateId, int nodeId,
			String action) throws WorkflowException {
		Node nodeHandler = new Node();
		return nodeHandler.getFollowedTaskNodeList(templateId, nodeId, action);
	}

	/**
	 * ȡ��ָ��ʵ��id���ڵ�id��������ִ����
	 * 
	 * @param instanceId
	 *            int ʵ��id
	 * @param nodeId
	 *            int �ڵ�id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of TaskExecutorMeta objects
	 */
	public static List getTaskExecutorList(int instanceId, int nodeId)
			throws WorkflowException {
		TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
		return taskExecutorOrderHandler.getExecutorList(instanceId, nodeId);
	}

	/**
	 * ��������������ִ����
	 * 
	 * @param instanceId
	 *            int ʵ��id
	 * @param nodeId
	 *            int �ڵ�id
	 * @param executor
	 *            int ִ����id
	 * @param order
	 *            int ִ����ִ��˳��
	 * @param responsibility
	 *            int ִ����ִ��ְ��
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createTaskExecutor(int instanceId, int nodeId,
			String executor, int order, int responsibility)
			throws WorkflowException {
		TaskExecutor taskExecutorHandler = new TaskExecutor();
		taskExecutorHandler.create(instanceId, nodeId, executor, order,
				responsibility);
	}

	/**
	 * ɾ��������ִ����
	 * 
	 * @param taskExecutorOrderId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void removeTaskExecutor(int taskExecutorOrderId)
			throws WorkflowException {
		TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
		taskExecutorOrderHandler.remove(taskExecutorOrderId);
	}

	/**
	 * ����������ִ����ִ��˳��
	 * 
	 * @param taskExecutorOrderId
	 *            int[]
	 * @param order
	 *            int[]
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void resetTaskExecutor(int taskExecutorOrderId[], int order[])
			throws WorkflowException {
		TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
		taskExecutorOrderHandler.reset(taskExecutorOrderId, order);
	}

	/**
	 * ɾ�������ڽڵ�����ִ������
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void removeTaskTerm(int instanceId, int nodeId)
			throws WorkflowException {
		TaskTerm taskExecuteTermHandler = new TaskTerm();
		taskExecuteTermHandler.removeByNode(instanceId, nodeId);
	}

	/**
	 * ���������ڽڵ�����ִ������
	 * 
	 * @param instanceId
	 *            int
	 * @param nodeId
	 *            int
	 * @param limitExecuteTerm
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void resetTaskTerm(int instanceId, int nodeId,
			int limitExecuteTerm, Connection conn) throws WorkflowException {
		TaskTerm taskExecuteTermHandler = new TaskTerm();
		taskExecuteTermHandler.reset(instanceId, nodeId, limitExecuteTerm);
	}

	/**
	 * ����ʵ��idȡ��ʵ���������ĵ��б�
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List a list of DocumentMeta objects
	 */
	public static List getDocumentListByInstance(int instanceId)
			throws WorkflowException {
		Document document = new Document();
		return document.getDocumentListByInstance(instanceId);
	}

	/**
	 * �����ĵ�
	 * 
	 * @param instanceId
	 *            int ʵ��id
	 * @param name
	 *            String �ļ�����
	 * @param type
	 *            String �ļ�����
	 * @param linkName
	 *            String �ļ��洢���������·����
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createDocument(int instanceId, String name, String type,
			String linkName) throws WorkflowException {
		createDocument(instanceId, name, type, linkName, null, null);
	}

	/**
	 * �����ĵ�
	 * 
	 * @param instanceId
	 *            int ʵ��id
	 * @param name
	 *            String �ļ�����
	 * @param type
	 *            String �ļ�����
	 * @param linkName
	 *            String �ļ��洢���������·����
	 * @param description
	 *            String ˵��
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createDocument(int instanceId, String name, String type,
			String linkName, String description) throws WorkflowException {
		createDocument(instanceId, name, type, linkName, description, null);
	}

	/**
	 * �����ĵ�
	 * 
	 * @param instanceId
	 *            int ʵ��id
	 * @param name
	 *            String �ļ�����
	 * @param type
	 *            String �ļ�����
	 * @param linkName
	 *            String �ļ��洢���������·����
	 * @param description
	 *            String ˵��
	 * @param uploadTime
	 *            String �ļ��ϴ�ʱ��
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createDocument(int instanceId, String name, String type,
			String linkName, String description, String uploadTime)
			throws WorkflowException {
		Document document = new Document();
		document.create(instanceId, name, type, linkName, description,
				uploadTime);
	}

	/**
	 * �����ĵ�
	 * 
	 * @param meta
	 *            DocumentMeta
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createDocument(DocumentMeta meta)
			throws WorkflowException {
		Document document = new Document();
		document.create(meta);
	}

	/**
	 * ɾ���ĵ�
	 * 
	 * @param docId
	 *            int �ĵ�id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void removeDocument(int docId) throws WorkflowException {
		Document document = new Document();
		document.delete(docId);
	}

	/**
	 * ȡ��ʵ��״̬
	 * 
	 * @param instanceId
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List
	 */
	public static List getStateListByInstance(int instanceId)
			throws WorkflowException {
		StateValue stateValue = new StateValue();
		return stateValue.getStateListByInstance(instanceId);
	}

	public static List getValueListByInstnace(int templateId, int instanceId)
			throws WorkflowException {
		VariableValue variableValue = new VariableValue();
		return variableValue.getValueListByInstnace(templateId, instanceId);
	}

	public static List getVariableListByTemplate(int templateId)
			throws WorkflowException {
		Variable variable = new Variable();
		return variable.getVariableListByTemplate(templateId);
	}

	/**
	 * ����������ܵ��������У�������������ĸ��ӹ�ϵ
	 * 
	 * @param childTaskId
	 * @param parentTaskId
	 * @param conn
	 * @throws WorkflowException
	 */
	public static void collectCurrentTask(int childTaskId, int parentTaskId)
			throws WorkflowException {
		CurrentTask taskHandler = new CurrentTask();
		// CurrentTaskBean taskBean=new CurrentTaskBean();
		// CurrentTaskMeta parentTaskMeta=taskBean.
		taskHandler.collectChildTaskToParentTask(childTaskId, parentTaskId);

	}

	public static List getChildInstanceByParentInstance(int parentInstanceId)
			throws WorkflowException {
		return new Instance().getChildInstanceListByParent(parentInstanceId);
	}
/**
 * �����һ����ִ����
 * @param instanceId
 * @param nodeId
 * @param creator
 * @param appendExecutors
 * @throws WorkflowException
 */
	public static void appendExecutor(int instanceId, int nodeId,
			String creator, String appendExecutors) throws WorkflowException {
		Instance instance = new Instance();
		instance.appendExecutor(instanceId, nodeId, creator, appendExecutors);
	}
/**
 * ɾ����һ�ڵ��ִ����
 * @param instanceId
 * @param nodeId
 * @param strUserId
 * @throws WorkflowException
 */
	public static void removeExecutor(int instanceId,
			int nodeId, String strUserId) throws WorkflowException {
		Instance instance = new Instance();
		instance.removeExecutor(instanceId, nodeId, strUserId);
	}
	
	public static void removeExecutors(String userId, int instanceId, int nodeId, String[] exes) throws WorkflowException {
		Instance instance = new Instance();
		instance.removeExecutors(userId,instanceId,nodeId,exes);	
	}
}
