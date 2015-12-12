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
 * 工作流运行期接口
 * <p>
 * Title: 工作流系统
 * </p>
 * <p>
 * Description: 工作流运行期接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 上海精驱科技有限公司
 * </p>
 * 
 * @author 上海精驱科技有限公司
 * @version 1.1
 */
public class ExecuteFacade {

	public ExecuteFacade() {
	}

	/**
	 * 授权任务
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
	 * 移交任务
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
	 * 把实例流程招回到当前的任务节点
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
	 * 回退实例流程至当前任务节点的原前置任务节点，
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
	 * 回退实例流程至当前任务节点的经历过的任务节点，
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
	 * 回退汇总流程到汇总节点 ，必须处理汇总流程的子流程的回退
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

		// 获取汇总节点
		int collectNodeId = instanceHandler.getPassedCollectNodeId(instanceId);
		List childTodoList = instanceHandler.getChildTodoListByParentInstance(
				instanceId, executor);
		// 回退所有被汇总的子流程
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
			// 去除流程的父子关系
			instanceHandler.setParentInstanceId(element.getInstanceId(), 0);
		}

		// 删除汇总任务，以及流程
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
	 * 中止实例
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
	 * 指定实例是否执行完成
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
	 * 挂起实例
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
	 * 激活实例
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
	 * 更改实例的名称与描述
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
	 * 跳转实例流程到指定节点
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
	 * 重启实例流程 如果实例的状态为中止（INSTANCE_STATUS_INTERRUPTED）且original为true则把实例重启到原执行位置
	 * 否始重启到开始任务节点，重启前如不设置运行执行者，则执行者缺省为配置期执行者
	 * 
	 * @param instanceId
	 *            int
	 * @param executor
	 *            int
	 * @param comment
	 *            String
	 * @param original
	 *            boolean 如果实例的状态为中止的，是否重启到实例的原执行位置（true），或者重启到实例的初始状态（false）。
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
	 * 创建实例，并返回创建者的当前任务
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
	 * 删除实例
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
	 * 在流程实例运行过程中取得当前任务
	 * 
	 * @param currentTaskId
	 *            int 当前任务id
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 取得待办任务
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
	 * 执行任务，并在实例可结束时正常结束实例 执行任务前后分别检查是否存在任务同步类,如果存在执行之
	 * 
	 * @param currentTaskId
	 *            int 当前任务id
	 * @param instanceId
	 *            int 任务的实例id
	 * @param instanceName
	 *            String 实例名称
	 * @param instanceDescription
	 *            String 实例描述
	 * @param nodeId
	 *            int 任务所在节点id
	 * @param action
	 *            String 节点流向上的动作
	 * @param comment
	 *            String 批注
	 * @param variableValueList
	 *            List a list of VariableValueMeta objects.
	 * @param executor
	 *            int 执行者id
	 * @param positionId
	 *            int 任务拥有者执行当前任务所处的职位id
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
		// 监听器会在Instance.execute(...)方法中执行
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
		if (taskType == CurrentTask.TYPE_COLLECTED) {// 汇总待办任务
			// 把子任务也提交
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
  //直接传入nextLinkList
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
  if (taskType == CurrentTask.TYPE_COLLECTED) {// 汇总待办任务
    // 把子任务也提交
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
          variableValueList,executor, positionId);//TODO:有待进一步确证::
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
	 * 取得指定执行者可执行的流程
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
	 * 取得指定执行者可执行的流程
	 * 
	 * @param executor
	 *            int 执行者id
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
	 * 取得指定执行者可执行的流程
	 * 
	 * @param templateType
	 *            String 流程类型
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
	 * 取得指定执行者可执行的流程
	 * 
	 * @param executor
	 *            int 执行者id
	 * @param templateType
	 *            String 流程类型
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
	 * 根据原拥有者取得待办任务
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
	 * 根据原拥有者取得待办任务
	 * 
	 * @param owner
	 *            int
	 * @param templateType
	 *            String 流程类型
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
	 * 根据原拥有者取得待办任务
	 * 
	 * @param owner
	 *            int
	 * @param templateId
	 *            int 流程id
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
	 * 根据原拥有者取得待办任务
	 * 
	 * @param owner
	 *            int
	 * @param instanceId
	 *            int 实例id
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
	 * 根据原拥有者取得待办任务
	 * 
	 * @param owner
	 *            int
	 * @param businessType
	 *            String 业点节务流程类型
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
	 * 根据原拥有者取得待办任务
	 * 
	 * @param owner
	 *            int
	 * @param nodeId
	 *            int 节点id
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
	 * 根据执行者取得待办任务
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
	 * 根据执行者取得待办任务
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
	 * 取得已办任务(执行动作)列表
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
	 * 根据流程id取得已办任务(执行动作)列表
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
	 * 根据流程类型取得已办任务(执行动作)列表
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
	 * 根据实例id取得已办任务(执行动作)列表
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
	 * 根据节点id取得已办任务(执行动作)列表
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
	 * 根据节点业务类型取得已办任务(执行动作)列表
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
	 * 根据实例拥有者（实例创建者）取得已办任务(执行动作)列表
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
	 * 根据实例拥有者（实例创建者）与流程id取得已办任务(执行动作)列表
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
	 * 根据实例拥有者（实例创建者）与流程类型取得已办任务(执行动作)列表
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
	 * 根据实例拥有者（实例创建者）与实例id取得已办任务(执行动作)列表
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
	 * 根据实例拥有者（实例创建者）与节点id取得已办任务(执行动作)列表
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
	 * 根据实例拥有者（实例创建者）与节点业务类型取得已办任务(执行动作)列表
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
	 * 根据任务执行者取得已办任务(执行动作)列表
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
	 * 根据任务执行者与流程id取得已办任务(执行动作)列表
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
	 * 根据任务执行者与流程类型取得已办任务(执行动作)列表
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
	 * 根据任务执行者与实例id取得已办任务(执行动作)列表
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
	 * 根据任务执行者与节点id取得已办任务(执行动作)列表
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
	 * 根据任务执行者与节点业务类型取得已办任务(执行动作)列表
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
	 * 取得指定实例id的实例
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
	 * 取得活动的实例列表
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
	 * 取得活动的实例列表
	 * 
	 * @param templateId
	 *            int 流程id
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
	 * 取得活动的实例列表
	 * 
	 * @param templateType
	 *            String 流程类型
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
	 * 取得活动的实例列表
	 * 
	 * @param templateId
	 *            int 流程id
	 * @param owner
	 *            int 实列拥有者（创建者）id
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
	 * 取得实例列表(包括活动的与结束的实例)
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
	 * 取得实例列表(包括活动的与结束的实例)
	 * 
	 * @param templateId
	 *            int 流程id
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
	 * 取得实例列表(包括活动的与结束的实例)
	 * 
	 * @param templateId
	 *            int 流程id
	 * @param owner
	 *            int 实列拥有者（创建者）id
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
	 * 获得该实例中在某两个节点之间已经执行的节点列表
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
			result.remove(new NodeMeta());// 去掉多增加的空Node
		}
		return result;
	}

	public static Map getFollowedTaskNodeActionMap(int templateId, int nodeId,
			Connection conn) throws WorkflowException {
		Node handler = new Node();
		return handler.getFollowedTaskNodeActionMap(templateId, nodeId);
	}

	/**
	 * 取得指定任务节点id的所有后续任务节点，以动态增加节点执行者
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
	 * 取得指定任务节点id与动作名称的所有后续任务节点，以动态增加节点执行者
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
	 * 取得指定实例id，节点id的运行期执行者
	 * 
	 * @param instanceId
	 *            int 实例id
	 * @param nodeId
	 *            int 节点id
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
	 * 创建运行期任务执行者
	 * 
	 * @param instanceId
	 *            int 实例id
	 * @param nodeId
	 *            int 节点id
	 * @param executor
	 *            int 执行者id
	 * @param order
	 *            int 执行者执行顺序
	 * @param responsibility
	 *            int 执行者执行职责
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
	 * 删除运行期执行者
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
	 * 重置运行期执行者执行顺序
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
	 * 删除运行期节点任务执行期限
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
	 * 重置运行期节点任务执行期限
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
	 * 根据实例id取得实例的所有文档列表
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
	 * 创建文档
	 * 
	 * @param instanceId
	 *            int 实例id
	 * @param name
	 *            String 文件名称
	 * @param type
	 *            String 文件类型
	 * @param linkName
	 *            String 文件存储的物理相对路径名
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createDocument(int instanceId, String name, String type,
			String linkName) throws WorkflowException {
		createDocument(instanceId, name, type, linkName, null, null);
	}

	/**
	 * 创建文档
	 * 
	 * @param instanceId
	 *            int 实例id
	 * @param name
	 *            String 文件名称
	 * @param type
	 *            String 文件类型
	 * @param linkName
	 *            String 文件存储的物理相对路径名
	 * @param description
	 *            String 说明
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void createDocument(int instanceId, String name, String type,
			String linkName, String description) throws WorkflowException {
		createDocument(instanceId, name, type, linkName, description, null);
	}

	/**
	 * 创建文档
	 * 
	 * @param instanceId
	 *            int 实例id
	 * @param name
	 *            String 文件名称
	 * @param type
	 *            String 文件类型
	 * @param linkName
	 *            String 文件存储的物理相对路径名
	 * @param description
	 *            String 说明
	 * @param uploadTime
	 *            String 文件上传时间
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
	 * 创建文档
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
	 * 删除文档
	 * 
	 * @param docId
	 *            int 文档id
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 */
	public static void removeDocument(int docId) throws WorkflowException {
		Document document = new Document();
		document.delete(docId);
	}

	/**
	 * 取得实例状态
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
	 * 将子任务汇总到父任务当中，建立两个任务的父子关系
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
 * 添加下一结点的执行人
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
 * 删除下一节点的执行人
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
