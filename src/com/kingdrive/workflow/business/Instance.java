package com.kingdrive.workflow.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.beanutils.MethodUtils;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.access.ActionHistoryQuery;
import com.kingdrive.workflow.access.CurrentTaskBean;
import com.kingdrive.workflow.access.InstanceBean;
import com.kingdrive.workflow.access.InstanceQuery;
import com.kingdrive.workflow.access.LinkBean;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.InstanceMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TaskExecutorMeta;
import com.kingdrive.workflow.dto.TaskMeta;
import com.kingdrive.workflow.dto.TaskResultMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.listener.TaskListener;
import com.kingdrive.workflow.model.ActionHistoryInfo;
import com.kingdrive.workflow.model.CurrentTaskModel;
import com.kingdrive.workflow.model.InstanceInfo;
import com.kingdrive.workflow.model.InstanceModel;
import com.kingdrive.workflow.model.LinkModel;
import com.kingdrive.workflow.util.DateTime;
import com.kingdrive.workflow.util.Sequence;

public class Instance {

  public static final String INITIAL_END_TIME = "00000000000000";

  public static final String MSG_INSTANCE_NAME_TOCOLLECT = "待汇总待办任务";

  public static final String MSG_INSTANCE_NAME_COLLECTED = "已汇总待办任务";

  public static final String MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT = "被退回的汇总待办任务";

  public static final String MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT_DETAIL = "被退回的待办任务";

  public static int VALID_STATUS;

  public static int INVALID_STATUS;

  public static int VALID_ORNOT_STATUS;

  public Instance() {
  }

  public List getActiveInstanceList() throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getActiveInstanceList();
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getActiveInstanceList(int templateId) throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getActiveInstanceListByTemplate(templateId);
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getActiveInstanceList(String templateType) throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getActiveInstanceListByTemplate(templateType);
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getActiveInstanceList(int templateId, String owner)
    throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getActiveInstanceListByTemplate(templateId, owner);
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getInstanceList() throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getInstanceList();
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getInstanceList(int templateId) throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getInstanceListByTemplate(templateId);
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getInstanceList(int templateId, String owner) throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getInstanceListByTemplate(templateId, owner);
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;
  }

  public List getChildInstanceListByParent(int parentInstanceId)
    throws WorkflowException {
    List result = new ArrayList();
    try {
      InstanceQuery query = new InstanceQuery();
      ArrayList list = query.getChildInstanceListByParentInstance(parentInstanceId);
      InstanceMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((InstanceInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1330, sqle.toString());
    }
    return result;

  }

  public boolean isInstanceFinished(int instanceId) throws WorkflowException {
    boolean result = false;
    InstanceMeta instance = getInstance(instanceId);
    // if (!"00000000000000".equals(instance.getEndTime())) {
    if (instance.getEndTime() != null) {
      result = true;
    }
    return result;
  }

  private void create(InstanceMeta meta) throws WorkflowException {
    try {
      InstanceBean bean = new InstanceBean();
      meta.setInstanceId(Sequence.fetch(Sequence.SEQ_INSTANCE));
      meta.setStatus(InstanceMeta.INSTANCE_STATUS_ACTIVE);
      if (bean.insert(unwrap(meta)) != 1) {
        throw new WorkflowException(1105);
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1106, sqle.toString());
    }
  }

  public TaskMeta create(int templateId, String name, String description,
    String creator, String createTime) throws WorkflowException {
    String[] users;
    if (null != creator) {
      users = creator.split(",");// 多个人员以逗号分隔
    } else {
      throw new WorkflowException(1015);
    }
    InstanceMeta meta = new InstanceMeta();
    meta.setName(name);
    // meta.setDescription(description);
    meta.setTemplateId(templateId);
    meta.setOwner(users[0]);// 用第一个用户的名义创建流程
    meta.setStartTime(createTime);
    meta.setEndTime(Instance.INITIAL_END_TIME);
    create(meta);

    // get the instanceId.
    int instanceId = meta.getInstanceId();

    Node nodeHandler = new Node();
    int nodeId = nodeHandler.getStartNode(templateId).getId();

    // 创建状态
    StateValue stateValueHandler = new StateValue();
    // 生成该节点的任务时，即将对应状态值赋给对应状态
    stateValueHandler.setNodeState(instanceId, nodeId);
    CurrentTask taskHandler = new CurrentTask();
    TaskMeta taskMeta = taskHandler.createStartTask(instanceId, nodeId, users[0],
      createTime);
    for (int i = 1; i < users.length; i++) {
      taskHandler.createStartTask(instanceId, nodeId, users[i], createTime);// 给其他人员创建任务
    }
    return taskMeta;// 限于返回值类型，只返回第一个人员的任务
  }

  public void activate(int instanceId, String executor, String executeTime,
    String comment) throws WorkflowException {
    InstanceMeta meta = getInstance(instanceId);
    if (meta.getStatus() == InstanceMeta.INSTANCE_STATUS_ACTIVE) {
      return;
    } else if (meta.getStatus() != InstanceMeta.INSTANCE_STATUS_DEACTIVE) {
      throw new WorkflowException(1107);
    } else {
      // record the action's history
      ActionHistory actionHistoryHandler = new ActionHistory();
      actionHistoryHandler.record(instanceId, ActionMeta.ACTION_NODE_UNKNOWN,
        ActionMeta.ACTION_TYPE_ACTIVATE_INSTANCE, executor, executeTime, comment,
        executor, null);
      // set the instance active
      meta.setStatus(InstanceMeta.INSTANCE_STATUS_ACTIVE);
      update(meta);
    }
  }

  public void deactivate(int instanceId, String executor, String executeTime,
    String comment) throws WorkflowException {
    InstanceMeta meta = getInstance(instanceId);
    if (meta.getStatus() == InstanceMeta.INSTANCE_STATUS_DEACTIVE) {
      return;
    } else if (meta.getStatus() != InstanceMeta.INSTANCE_STATUS_ACTIVE) {
      throw new WorkflowException(1107);
    } else {
      // record the action's history
      ActionHistory actionHistoryHandler = new ActionHistory();
      actionHistoryHandler.record(instanceId, ActionMeta.ACTION_NODE_UNKNOWN,
        ActionMeta.ACTION_TYPE_DEACTIVATE_INSTANCE, executor, executeTime, comment,
        executor, null);
      // set the instance deactive
      meta.setStatus(InstanceMeta.INSTANCE_STATUS_DEACTIVE);
      update(meta);
    }
  }

  /**
   * 跳转实例流程
   * 
   * @param instanceId
   *            int
   * @param nodeId
   *            int
   * @param executor
   *            int
   * @param executeTime
   *            String
   * @param comment
   *            String
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  public void transfer(int instanceId, int nodeId, int nextNodeId, String executor,
    String executeTime, String comment) throws WorkflowException {
    // record the action's history

    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.record(instanceId, nodeId,
      ActionMeta.ACTION_TYPE_TRANSFER_FLOW, executor, executeTime, comment,
      executor, null);

    Action actionHandler = new Action();
    Pass passHandler = new Pass();
    StateValue stateValueHandler = new StateValue();
    TaskTerm termHandler = new TaskTerm();
    TaskExecutor executorHandler = new TaskExecutor();

    Node nodeHandler = new Node();
    NodeMeta node = nodeHandler.getNode(nextNodeId);
    int templateId = node.getTemplateId();
    String executorsMethod = node.getExecutorsMethod();

    CurrentTask taskHandler = new CurrentTask();
    StateValue stateHandler = new StateValue();

    // 清除本节点的action、pass、node state、current task、execute term、executor.
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);
    stateValueHandler.removeNodeState(instanceId, nodeId);
    taskHandler.removeByNode(instanceId, nodeId);
    termHandler.removeByNode(instanceId, nodeId);
    executorHandler.removeByNode(instanceId, nodeId);

    List precedingNodeList = getWholeExecutingPrecedingTaskNodeList(templateId,
      instanceId, nextNodeId);
    for (int i = 0; i < precedingNodeList.size(); i++) {
      NodeMeta precedingNode = (NodeMeta) precedingNodeList.get(i);
      int precedingNodeId = precedingNode.getId();
      taskHandler.removeByNode(instanceId, precedingNodeId);
      stateHandler.removeNodeState(templateId, precedingNodeId);
    }
    taskHandler.createNodeTask(instanceId, nextNodeId, executorsMethod, executor,
      executeTime);

    // List precedingLinkList = linkHandler.getPrecedingLinkList(templateId,
    // nextNodeId, conn);
    // 跳转时只设置节点的状态
    // stateHandler.setLinkState(instanceId, precedingLinkList, conn);
    stateValueHandler.setNodeState(instanceId, nextNodeId);
  }

  /**
   * 回退实例至当前任务节点的原前置任务节点，
   * 
   * @param instanceId
   *            int
   * @param nodeId
   *            int
   * @param executor
   *            int
   * @param executeTime
   *            String
   * @param comment
   *            String
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  public void giveback(int instanceId, int nodeId, String executor,
    String executeTime, String comment) throws WorkflowException {
    Action actionHandler = new Action();
    ActionHistory actionHistoryHandler = new ActionHistory();
    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    Pass passHandler = new Pass();
    StateValue stateValueHandler = new StateValue();
    TaskTerm termHandler = new TaskTerm();
    TaskExecutor executorHandler = new TaskExecutor();

    InstanceMeta instance = getInstance(instanceId);
    int templateId = instance.getTemplateId();
    NodeMeta start = nodeHandler.getStartNode(templateId);
    if (nodeId == start.getId()) {
      throw new WorkflowException(1112, null);
    }

    // record the action's history
    actionHistoryHandler.record(instanceId, nodeId,
      ActionMeta.ACTION_TYPE_GIVEBACK_FLOW, executor, executeTime, comment,
      executor, null);

    // 清除本节点的action、pass、node state、current task、execute term、executor.
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);
    stateValueHandler.removeNodeState(instanceId, nodeId);
    taskHandler.removeByNode(instanceId, nodeId);
    termHandler.removeByNode(instanceId, nodeId);
    executorHandler.removeByNode(instanceId, nodeId);

    List precedingTaskNodeList = getJustExecutedPrecedingTaskNodeList(templateId,
      instanceId, nodeId);

    // 创建原节点的任务
    taskHandler.createPrecedingNodesTask(instanceId, precedingTaskNodeList,
      executor, executeTime);

    // 清除原节点的运行期action、pass，并记录原节点的前置链接以设置其前置链接状态
    // List precedingLinkList = new ArrayList();
    // 清除原节点的运行期action、pass，并记录原节点的后置链接以设置其后置链接状态
    List followingLinkList = new ArrayList();
    for (int i = 0; i < precedingTaskNodeList.size(); i++) {
      int precedingTaskNodeId = ((NodeMeta) precedingTaskNodeList.get(i)).getId();
      // action不要删掉 by zhanggh 050624
      // actionHandler.removeByNode(instanceId, precedingTaskNodeId,
      // conn);
      passHandler.removeByNode(instanceId, precedingTaskNodeId);

      // 记录原节点的前置链接
      // precedingLinkList.addAll(linkHandler.getPrecedingLinkList(templateId,
      // precedingTaskNodeId, conn));
      // 退回时记录原节点的后置链接
      followingLinkList.addAll(linkHandler.getFollowedLinkList(templateId,
        precedingTaskNodeId));
    }

    // 设置原节点的前置链接链接状态
    // stateValueHandler.setLinkState(instanceId, precedingLinkList, conn);
    // 设置原节点的后置链接链接状态,以便设置退回的状态，区别于提交
    stateValueHandler.setLinkState(instanceId, followingLinkList);
  }

  public void untread(int instanceId, int nodeId, int prevNodeId, String executor,
    String executeTime, String comment) throws WorkflowException {
    Object listener = getListener(nodeId);
    ActionMeta am = null;
    ActionHistory history = new ActionHistory();
    Node nodeHandler = new Node();
    InstanceMeta instance = getInstance(instanceId);
    int templateId = instance.getTemplateId();
    Node prevNode = new Node();
    NodeMeta prevNodeMeta = new NodeMeta();
    // 计算-1,-2节点
    if (-1 == prevNodeId) {// 表示尚不知晓、需要通过计算得知的nodeId的前一个节点
      List tempNodeList = getExecutedNodeListBetween(templateId, instanceId, nodeId,
        -1, true);
      if (null != tempNodeList && tempNodeList.size() > 0) {
        prevNodeId = ((NodeMeta) tempNodeList.get(0)).getId();
        prevNodeMeta = (NodeMeta) tempNodeList.get(0);
      }
    } else if (-2 == prevNodeId) {
      prevNodeMeta = nodeHandler.getStartNode(templateId);
      prevNodeId = prevNodeMeta.getId();
    } else {
      prevNodeMeta = prevNode.getNode(prevNodeId);
    }
    if (prevNodeMeta == null || prevNodeMeta.getId() == nodeId) {
      throw new WorkflowException(1112);
    }
    if (null != listener) {
      List doneList = history.getActionListByInstanceAndNode(instanceId, prevNodeId,
        1);
      if (null != doneList && doneList.size() > 0) {
        am = (ActionMeta) doneList.get(doneList.size() - 1);// 只取最后执行的那个
      }
      if (am != null) {
        // listener.beforeUntread(am);
        try {
          MethodUtils.invokeMethod(listener, "beforeUntread", new Object[] { am });
        } catch (NoSuchMethodException ex) {
        } catch (Exception ex) {
          throw new WorkflowException(2208, ex.getMessage());
        }
      }
    }

    try {
      String endTime = DateTime.getSysTime();
      try {
        Class UtilWFTool = Class.forName("com.anyi.mom.tool.UtilWFTool");
        MethodUtils
          .invokeMethod(UtilWFTool.newInstance(), "callBackBusiness", new Object[] {
            Integer.toString(nodeId), am.getNodeId() + "", am.getExecuteTime(),
            endTime, Integer.valueOf(am.getInstanceId() + "") });
      } catch (NoSuchMethodException ex) {
      } catch (Exception ex) {
      }
    } catch (Exception e) {

      throw new WorkflowException(9999);
    }

    untread(instanceId, nodeId, prevNodeId, executor, executeTime, comment, true);
    if (am != null && null != listener) {
      // listener.afterUntread(am);
      try {
        MethodUtils.invokeMethod(listener, "afterUntread", new Object[] { am });
      } catch (NoSuchMethodException ex) {
      } catch (Exception ex) {
        throw new WorkflowException(2208, ex.getMessage());
      }
    }
  }

  /**
   * 回退实例至指定的已执行过的任务节点
   * 
   * @param instanceId
   *            int 实例Id
   * @param nodeId
   *            int 当前节点
   * @param prevNodeId
   *            int 已执行过的某一节点，目标节点。 -1代表表示尚不知晓、需要通过计算得知的nodeId的前一个节点,
   *            -2表示第一个节点
   * @param executor
   *            int 当前执行人
   * @param executeTime
   *            String 执行时间
   * @param comment
   *            String 回退的意见内容
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  /* zhanggh 20050606 */
  public void untread(int instanceId, int nodeId, int prevNodeId, String executor,
    String executeTime, String comment, boolean justFlag) throws WorkflowException {
    // init
    Action actionHandler = new Action();
    ActionHistory actionHistoryHandler = new ActionHistory();
    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    Pass passHandler = new Pass();
    StateValue stateValueHandler = new StateValue();
    TaskTerm termHandler = new TaskTerm();
    TaskExecutor executorHandler = new TaskExecutor();

    InstanceMeta instance = getInstance(instanceId);
    int templateId = instance.getTemplateId();
    NodeMeta start = nodeHandler.getStartNode(templateId);
    if (nodeId == start.getId()) {
      throw new WorkflowException(1112, null);
    }
    // initalization over
    if (!existMyInstNodeTask(instanceId, nodeId, executor)) {
      throw new WorkflowException(1035);
    }
    // record the action's history
    actionHistoryHandler.record(instanceId, nodeId,
      ActionMeta.ACTION_TYPE_UNTREAD_FLOW, executor, executeTime, comment, executor,
      null);

    // 清除本节点的action、pass、node state、current task、execute term、executor.
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);
    stateValueHandler.removeNodeState(instanceId, nodeId);
    taskHandler.removeByNode(instanceId, nodeId);
    termHandler.removeByNode(instanceId, nodeId);
    executorHandler.removeByNode(instanceId, nodeId);

    Node prevNode = new Node();
    NodeMeta prevNodeMeta = prevNode.getNode(prevNodeId);

    /*
     * List taskExecutorList = executorHandler.getExecutorList(instanceId,
     * prevNodeId, conn); for (int index = 0; index <
     * taskExecutorList.size(); index++) { executorMeta = (TaskExecutorMeta)
     * taskExecutorList.get(index); taskHandler.createNodeTask(instanceId,
     * prevNodeId,prevNodeMeta.getExecutorsMethod(),
     * executorMeta.getExecutor(), executeTime, conn); //break;//只取第一个//todo }
     */

    taskHandler.createNodeTask(instanceId, prevNodeId, prevNodeMeta
      .getExecutorsMethod(), executor, executeTime);
    // 得到两节点之间执行过的节点的列表
    List precedingNodeList = getExecutedNodeListBetween(templateId, instanceId,
      nodeId, prevNodeId, false);
    /*
     * getExecutedActionListBetweenTimeOrder( templateId, instanceId,
     * prevNodeId,nodeId,false, conn);
     */
    if (null == precedingNodeList) {
      precedingNodeList = new ArrayList();
    }
    // if (prevNodeId != -1) {
    // precedingNodeList.add(prevNodeMeta);
    // }

    // 清除原节点以及已有已运行的节点的运行期pass，
    // List precedingLinkList = new ArrayList();
    List followingLinkList = new ArrayList();
    for (int i = 0; i < precedingNodeList.size(); i++) {
      int precedingTaskNodeId = ((NodeMeta) precedingNodeList.get(i)).getId();
      actionHandler.removeByNode(instanceId, precedingTaskNodeId);
      passHandler.removeByNode(instanceId, precedingTaskNodeId);
    }
    // 记录原节点的前置链接
    // precedingLinkList.addAll(linkHandler.getPrecedingLinkList(templateId,
    // prevNodeId, conn));
    // 设置原节点的前置链接链接状态
    // stateValueHandler.setLinkState(instanceId, precedingLinkList, conn);
    // 记录原节点的后置链接
    followingLinkList
      .addAll(linkHandler.getFollowedLinkList(templateId, prevNodeId));
    // 设置原节点的后置链接链接状态
    stateValueHandler.setLinkState(instanceId, followingLinkList);
    // 设置实例当前节点进行状态
    // 退回时不再把节点状态值设置给状态变量
    // stateValueHandler.setNodeState(instanceId, prevNodeId, conn);
  }

  /**
   * @param instanceId
   * @param nodeId
   * @param executor
   * @param conn
   * @throws WorkflowException
   */
  public boolean existMyInstNodeTask(int instanceId, int nodeId, String executor)
    throws WorkflowException {
    boolean existMyTask = false;
    CurrentTaskMeta currentTask = null;
    List taskList = (new CurrentTask()).getTodoListByExecutorAndInstance(executor,
      instanceId, 1);
    // 没有它的待办，或者不是主办，都不能做退回
    for (int i = 0; i < taskList.size(); i++) {
      currentTask = (CurrentTaskMeta) taskList.get(i);
      if (currentTask != null
        && currentTask.getNodeId() == nodeId
        && currentTask.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN) {
        existMyTask = true;
        break;
      }
    }
    return existMyTask;
  }

  /**
   * 授权
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
   * @param authorizeTime
   *            String
   * @param comment
   *            String
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  public void authorize(int instanceId, int nodeId, String[] executor,
    int responsibility[], String authorizor, String authorizeTime, String comment)
    throws WorkflowException {

    if (executor == null) {
      throw new WorkflowException(1117, null);
    }
    for (int i = 0; i < executor.length; i++) {
      if (authorizor.equals(executor[i])) {
        throw new WorkflowException(1118, null);
      }
    }

    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.record(instanceId, nodeId,
      ActionMeta.ACTION_TYPE_AUTHORIZE_TASK, authorizor, authorizeTime, comment,
      authorizor, null);

    CurrentTask taskHandler = new CurrentTask();
    taskHandler.createNodeAuthorizeTask(instanceId, nodeId, executor,
      responsibility, authorizor, authorizeTime);

    TaskExecutor executorHandler = new TaskExecutor();
    executorHandler.create(instanceId, nodeId, executor, responsibility, authorizor);
  }

  /**
   * 移交
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
   * @param forwardTime
   *            String
   * @param comment
   *            String
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  public void forward(int instanceId, int nodeId, int currentTaskId,
    String[] executor, int responsibility[], String forwarder, String forwardTime,
    String comment) throws WorkflowException {

    if (executor == null) {
      throw new WorkflowException(1117, null);
    }
    for (int i = 0; i < executor.length; i++) {
      if (forwarder.equals(executor[i])) {
        throw new WorkflowException(1118, null);
      }
    }

    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.record(instanceId, nodeId,
      ActionMeta.ACTION_TYPE_FORWARD_TASK, forwarder, forwardTime, comment,
      forwarder, null);

    CurrentTask taskHandler = new CurrentTask();
    taskHandler.createNodeAuthorizeTask(instanceId, nodeId, executor,
      responsibility, forwarder, forwardTime);
    taskHandler.removeByTask(currentTaskId);

    TaskExecutor executorHandler = new TaskExecutor();
    executorHandler.create(instanceId, nodeId, executor, responsibility, forwarder);
    executorHandler.removeByExecutor(instanceId, nodeId, forwarder);
  }

  /**
   * 把实例流程招回到当前的任务节点 如果指定任务节点的后续任务节点已经进行或已经完成，则不可招回，抛WorkflowException(1113)
   * 
   * @param instanceId
   *            int
   * @param nodeId
   *            int
   * @param executor
   *            int
   * @param executeTime
   *            String
   * @param comment
   *            String
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  public void callback(int instanceId, int nodeId, String executor,
    String executeTime, String comment) throws WorkflowException {
    // record the action's history
    Action actionHandler = new Action();
    ActionHistory actionHistoryHandler = new ActionHistory();
    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    Pass passHandler = new Pass();
    TaskExecutor executorHandler = new TaskExecutor();
    TaskTerm termHandler = new TaskTerm();
    StateValue stateValueHandler = new StateValue();

    Object listener = getListener(nodeId);
    ActionMeta am = null;

    // 取得后续任务节点以清除其action、pass、executor、term、current
    // task，并取得后续任务节点的前置链接，以清除其链接状态
    InstanceMeta instance = getInstance(instanceId);
    int templateId = instance.getTemplateId();
    ActionHistoryQuery ahq = new ActionHistoryQuery();
    // add end
    List followedTaskNodeList = getOnHandFollowedTaskNodeList(templateId,
      instanceId, nodeId);

    if (followedTaskNodeList.size() == 0 || (!ahq.isLastAction(instanceId, nodeId))) {
      throw new WorkflowException(1113);
    }/*
     * else{ for(int l=followedTaskNodeList.size()-1;l>=0;l--){ int
     * oneNodeId = ((NodeMeta) followedTaskNodeList.get(l)).getId();
     * if(!ahq.isLastAction(conn,instanceId,oneNodeId)){
     * followedTaskNodeList.remove(l); } } }
     */

    actionHistoryHandler.record(instanceId, nodeId,
      ActionMeta.ACTION_TYPE_CALLBACK_FLOW, executor, executeTime, comment,
      executor, null);

    // add by liubo : add the new callback to callback
    if (listener != null) {
      am = this.getActionMeta(nodeId, instanceId, executor);
      if (am != null) {
        // listener.beforeCallback(am);
        try {
          MethodUtils.invokeMethod(listener, "beforeCallback", new Object[] { am });
        } catch (NoSuchMethodException ex) {
        } catch (Exception ex) {
          throw new WorkflowException(2208, ex.getMessage());
        }
      }
    }
    List followedLinkList = new ArrayList();
    for (int i = 0; i < followedTaskNodeList.size(); i++) {
      int followedNodeId = ((NodeMeta) followedTaskNodeList.get(i)).getId();
      // 删除已经执行了的各项数据。by zhanggh
      actionHandler.removeByNode(instanceId, followedNodeId);
      passHandler.removeByNode(instanceId, followedNodeId);
      executorHandler.removeByNode(instanceId, followedNodeId);
      termHandler.removeByNode(instanceId, followedNodeId);
      stateValueHandler.removeNodeState(instanceId, followedNodeId);
      // end
      taskHandler.removeByNode(instanceId, followedNodeId);

      followedLinkList.addAll(linkHandler.getPrecedingLinkList(templateId,
        followedNodeId));
    }
    stateValueHandler.removeLinkState(instanceId, followedLinkList);

    // 创建当前节点的任务
    String executorsMethod = nodeHandler.getNode(nodeId).getExecutorsMethod();
    taskHandler.createNodeTask(instanceId, nodeId, executorsMethod, executor,
      executeTime);

    // 清除本节点的action、pass、node state.
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);

    // List precedingLinkList = linkHandler.getPrecedingLinkList(templateId,
    // nodeId, conn);
    // stateValueHandler.setLinkState(instanceId, precedingLinkList, conn);
    // 收回时状态为当前节点的状态
    stateValueHandler.setNodeState(instanceId, nodeId);
    if (am != null && listener != null) {
      // listener.afterCallback(am);
      try {
        MethodUtils.invokeMethod(listener, "afterCallback", new Object[] { am });
      } catch (NoSuchMethodException ex) {
      } catch (Exception ex) {
        throw new WorkflowException(2208, ex.getMessage());
      }
    }
  }

  public void interrupt(int instanceId, String executor, String executeTime,
    String comment) throws WorkflowException {
    InstanceMeta instance = getInstance(instanceId);
    if (instance.getStatus() == InstanceMeta.INSTANCE_STATUS_INTERRUPTED) {
      return;
    } else if (instance.getStatus() == InstanceMeta.INSTANCE_STATUS_FINISHED) {
      throw new WorkflowException(1108);
    }

    // record the action's history
    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.record(instanceId, ActionMeta.ACTION_NODE_UNKNOWN,
      ActionMeta.ACTION_TYPE_INTERRUPT_INSTANCE, executor, executeTime, comment,
      executor, null);

    // modify the instance's status and end time
    // but not clear the instance run time dependents for restart
    instance.setStatus(InstanceMeta.INSTANCE_STATUS_INTERRUPTED);
    instance.setEndTime(executeTime);
    update(instance);
  }

  /**
   * 重启实例流程 如果实例的状态为中止（INSTANCE_STATUS_INTERRUPTED） 且original为true则把实例重启到原执行位置
   * 否始重启到开始任务节点，重启前如不设置运行执行者，则执行者缺省为配置期执行者
   * 
   * @param instanceId
   *            int
   * @param executor
   *            int
   * @param executeTime
   *            String
   * @param comment
   *            String
   * @param original
   *            boolean
   * @param conn
   *            Connection
   * @throws WorkflowException
   */
  public void restart(int instanceId, String executor, String executeTime,
    String comment, boolean original) throws WorkflowException {
    // record the action's history
    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.record(instanceId, ActionMeta.ACTION_NODE_UNKNOWN,
      ActionMeta.ACTION_TYPE_RESTART_INSTANCE, executor, executeTime, comment,
      executor, null);

    InstanceMeta instance = getInstance(instanceId);
    instance.setStatus(InstanceMeta.INSTANCE_STATUS_ACTIVE);
    instance.setEndTime(INITIAL_END_TIME);
    update(instance);

    if (original && instance.getStatus() == InstanceMeta.INSTANCE_STATUS_INTERRUPTED) {
      // 把实例重启到原执行位置
      // 由于在中止实例时没有对run time dependents清理，所以重启时不做任何处理。
    } else {
      // 把实例重启到开始任务节点
      Action actionHandler = new Action();
      CurrentTask taskHandler = new CurrentTask();
      Node nodeHandler = new Node();
      Pass passHandler = new Pass();
      TaskExecutor taskExecutorHandler = new TaskExecutor();
      TaskTerm taskExecuteTermHandler = new TaskTerm();
      StateValue stateValueHandler = new StateValue();
      VariableValue variableValueHandler = new VariableValue();
      int templateId = instance.getTemplateId();
      NodeMeta node = nodeHandler.getStartNode(templateId);
      int nodeId = node.getId();
      String executorsMethod = node.getExecutorsMethod();

      // 取得首个节点的原来的执行者,下面会据此声称待办任务
      // 如果是多是人，可能只有其中一个人实际执行了操作，但仍然会给所有人发待办
      // backup the instance start node's task executor and execute term
      List executorList = taskExecutorHandler.getExecutorList(instanceId, nodeId);
      int limitExecuteTerm = taskExecuteTermHandler.getLimitExecuteTerm(instanceId,
        nodeId);

      // remove all the instance's run time dependents.
      actionHandler.removeByInstance(instanceId);
      passHandler.removeByInstance(instanceId);
      taskExecuteTermHandler.removeByInstance(instanceId);
      taskHandler.removeByInsatnce(instanceId);
      stateValueHandler.removeByInstance(instanceId);
      variableValueHandler.removeByInstance(instanceId);
      taskExecutorHandler.removeByInstance(instanceId);

      // restore the instance start node's task executor and execute term
      if (executorList.size() == 0) {
        taskExecutorHandler.set(instanceId, nodeId, executorsMethod);
      } else {
        for (int i = 0; i < executorList.size(); i++) {
          taskExecutorHandler.create((TaskExecutorMeta) executorList.get(i));
        }
      }
      if (limitExecuteTerm != 0) {
        taskExecuteTermHandler.reset(instanceId, nodeId, limitExecuteTerm);
      }

      // create the instance start node's current task
      taskHandler.createNodeTask(instanceId, nodeId, executorsMethod, executor,
        executeTime);
    }
  }

  /**
   * 重做实例流程 实例结束后(状态为结束(9))，需要重新操作，进行修改。 1.给最后的执行者创建对应节点(一般是最后一个节点)的待办任务，
   * 2.把流程状态设置到该节点， 3.把实例状态置为进行中(1)
   * 
   * zhanggh 20060912
   */
  public void rework(int instanceId, String executor, String executeTime,
    String comment) throws WorkflowException {
    // record the action's history
    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.record(instanceId, ActionMeta.ACTION_NODE_UNKNOWN,
      ActionMeta.ACTION_TYPE_REDO_INSTANCE, executor, executeTime, comment,
      executor, null);

    InstanceMeta instance = getInstance(instanceId);
    instance.setStatus(InstanceMeta.INSTANCE_STATUS_ACTIVE);
    instance.setEndTime(INITIAL_END_TIME);
    update(instance);

    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Pass passHandler = new Pass();
    Action actionHandler = new Action();
    StateValue stateValueHandler = new StateValue();

    // 把实例重启到最后一个action的节点
    List actionList = actionHistoryHandler.getActionListByInstance(instanceId, 1);
    if (actionList == null || actionList.size() == 0) {
      throw new WorkflowException(1175);
    }
    ActionMeta action = (ActionMeta) actionList.get(actionList.size() - 1);
    int nodeId = action.getNodeId();
    NodeMeta node = nodeHandler.getNode(nodeId);
    String executorsMethod = node.getExecutorsMethod();
	TaskListener listener = getListener(nodeId);
	if (listener != null) {
		try {
			MethodUtils.invokeMethod(listener, "beforeRework",
					new Object[] { action });
		} catch (NoSuchMethodException ex) {
		} catch (Exception ex) {
			throw new WorkflowException(2208, ex.getMessage());
		}
	}
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);
    stateValueHandler.setNodeState(instanceId, nodeId);

    // 创建任务
    taskHandler.createNodeTask(instanceId, nodeId, executorsMethod, executor,
      executeTime);
    // 设置返回结果
	if (listener != null) {
		try {
			MethodUtils.invokeMethod(listener, "afterRework",
					new Object[] { action });
		} catch (NoSuchMethodException ex) {
		} catch (Exception ex) {
			throw new WorkflowException(2208, ex.getMessage());
		}
	}
  }

  /**
   * 清除实例
   * 
   * @param instanceId
   *            int
   * @param conn
   *            Connection
   * @throws WorkflowException
   * @todo remove -- if the document's phisical files are maintained by
   *       workflow system, we should delete them also.
   */
  public void remove(int instanceId) throws WorkflowException {
    Action actionHandler = new Action();
    actionHandler.removeByInstance(instanceId);

    ActionHistory actionHistoryHandler = new ActionHistory();
    actionHistoryHandler.removeByInstance(instanceId);

    CurrentTask taskHandler = new CurrentTask();
    taskHandler.removeByInsatnce(instanceId);

    Pass passHandler = new Pass();
    passHandler.removeByInstance(instanceId);

    TaskExecutor taskExecutorHandler = new TaskExecutor();
    taskExecutorHandler.removeByInstance(instanceId);

    TaskTerm taskExecuteTermHandler = new TaskTerm();
    taskExecuteTermHandler.removeByInstance(instanceId);

    StateValue stateValueHandler = new StateValue();
    stateValueHandler.removeByInstance(instanceId);

    VariableValue variableValueHandler = new VariableValue();
    variableValueHandler.removeByInstance(instanceId);

    Document documentHandler = new Document();
    documentHandler.removeByInstance(instanceId);

    // delete the instance
    try {
      InstanceBean bean = new InstanceBean();
      bean.delete(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1111, sqle.toString());
    }
  }

  public void update(int instanceId, String instanceName, String instanceDescription)
    throws WorkflowException {
    InstanceMeta meta = new InstanceMeta();
    meta.setInstanceId(instanceId);
    meta.setName(instanceName);
    // meta.setDescription(instanceDescription);
    update(meta);
  }

  public void update(InstanceMeta meta) throws WorkflowException {
    try {
      InstanceBean bean = new InstanceBean();
      if (bean.update(unwrap(meta)) != 1) {
        throw new WorkflowException(1110);
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1111, sqle.toString());
    }
  }

  private boolean canFinishInstance(List nodeLinkList) throws WorkflowException {
    boolean finished = false;
    // 适应结束节点前是路由节点的情况 by zhanggh 050629
    if (nodeLinkList.size() == 0) {
      finished = true;
    }
    for (int i = 0; i < nodeLinkList.size(); i++) {
      // 如果某一个流向(link)的下一个节点是结束节点
      int followedNodeId = ((Link) nodeLinkList.get(i)).getNextNodeId();
      if (followedNodeId == -2) {// 结束节点
        finished = true;
        break;
      }
    }
    return finished;
  }

  /**
   * 实际执行的是这个方法，但主体是下面的方法，这个是在前后添加before/afterExecution
   */

  public TaskResultMeta execute(int currentTaskId, int instanceId,
    String instanceName, String instanceDescription, int nodeId, String action,
    String comment, List variableValueList, String executor, String positionId,
    String executeTime) throws WorkflowException {
    //TaskListener listener = getListener(nodeId);
    CurrentTask taskHandler = new CurrentTask();
    CurrentTaskMeta currentTask = null;
    //if (null != listener) {
    //  currentTask = taskHandler.getCurrentTask(currentTaskId, 1);
    //  if (currentTask != null) {
    //    listener.beforeExecution(currentTask);
    //    // try {
    //    // MethodUtils.invokeMethod(listener, "beforeExecution",
    //    // new Object[] { currentTask });
    //    // } catch (NoSuchMethodException ex) {
    //    // } catch (Exception ex) {
    //    // throw new WorkflowException(2208, ex.getMessage());
    //    // }
    //  }
    //}
    TaskResultMeta result = execute(currentTaskId, instanceId, instanceName,
      instanceDescription, nodeId, action, comment, variableValueList, executor,
      positionId, executeTime, true);
    //if (null != listener && null != currentTask) {
    //  listener.afterExecution(currentTask);
    //  // try {
    //  // MethodUtils.invokeMethod(listener, "afterExecution",
    //  // new Object[] { currentTask });
    //  // } catch (NoSuchMethodException ex) {
    //  // } catch (Exception ex) {
    //  // throw new WorkflowException(2208, ex.getMessage());
    //  //			}
    //}
    return result;
  }

  /**
   * 执行当前实例
   * 
   * @param templateId
   * @param instanceId
   * @param nodeId
   * @param conn
   * @return TaskResultMeta dto对象，包含
   * @throws WorkflowException
   * @throws
   * @see
   */
  public TaskResultMeta execute(int currentTaskId, int instanceId,
    String instanceName, String instanceDescription, int nodeId, String action,
    String comment, List variableValueList, String executor, String positionId,
    String executeTime, boolean justFlag) throws WorkflowException {
    
    NodeMeta currentNode = new Node().getNode(nodeId);
    int templateId = currentNode.getTemplateId();
    List nodeLinkList = new Link().getFollowedLinkList(templateId, nodeId, action);
    List nextLinkList = getRightFollowedTaskLinkList(instanceId, templateId,
      nodeLinkList, variableValueList);
      
    return doExecute(currentTaskId, instanceId, instanceName, instanceDescription,
      nodeId, action, comment, variableValueList, nextLinkList, executor,
      positionId, executeTime, justFlag);
  }
  //外部已计算下个link
  public TaskResultMeta execute(int currentTaskId, int instanceId,
    String instanceName, String instanceDescription, int nodeId, String action,
    String comment, List variableValueList, List nextLinkList, String executor, String positionId,
    String executeTime, boolean justFlag) throws WorkflowException {
    
    return doExecute(currentTaskId, instanceId, instanceName, instanceDescription,
      nodeId, action, comment, variableValueList, nextLinkList, executor,
      positionId, executeTime, justFlag);
  }

  public TaskResultMeta doExecute(int currentTaskId, int instanceId,
    String instanceName, String instanceDescription, int nodeId, String action,
    String comment, List variableValueList, List nextLinkList, String executor,
    String positionId, String executeTime, boolean justFlag)
    throws WorkflowException {
    
//    TaskListener listener = getListener(nodeId);
    CurrentTask taskHandler = new CurrentTask();
//    CurrentTaskMeta currentTask = null;
//    if (null != listener) {
//      currentTask = taskHandler.getCurrentTask(currentTaskId, 1);
//      if (currentTask != null) {
//        listener.beforeExecution(currentTask);
//      }
//    }
    
    /* define bussiness handler */
    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    Pass passCountHandler = new Pass();
    StateValue stateValueHandler = new StateValue();
    VariableValue variableValueHandler = new VariableValue();

    int templateId;
    int nextNodeId;
    NodeMeta currentNode = null;
    String executorsMethod;
    List nodeLinkList = null;
    String linkType;
    String taskRealOwner;
    String limitExecuteTime = null;

    currentNode = nodeHandler.getNode(nodeId);
    templateId = currentNode.getTemplateId();
    executorsMethod = currentNode.getExecutorsMethod();
    nodeLinkList = linkHandler.getFollowedLinkList(templateId, nodeId, action);
    // 取第一个元素，是因为根据NodeId和action取得的一般只有一个link
    linkType = ((Link) nodeLinkList.get(0)).getType();
    nextNodeId = ((Link) nodeLinkList.get(0)).getNextNodeId();
    action = ((Link) nodeLinkList.get(0)).getActionName();
    // we will modify the instance but create it when normal execution.
    /* 更新instance */
    InstanceMeta instance = new InstanceMeta(); // getInstance(instanceId,
    // conn);
    instance.setInstanceId(instanceId);
    instance.setName(instanceName);
    // instance.setDescription(instanceDescription);
    update(instance);

    /* 记录CurrentTask到历史记录 */
    CurrentTaskMeta task = taskHandler.getCurrentTask(currentTaskId, 1);
    taskRealOwner = task.getOwner();
    limitExecuteTime = task.getLimitExecuteTime();
    if (task.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN) {
      // 主办，记录到action表中.该方法中包含写actionhistory记录
      actionHandler.record(instanceId, nodeId, action, executor, executeTime,
        comment, taskRealOwner, limitExecuteTime);
      // 记录pass.将所有后继link都记录下来
      passCountHandler.record(instanceId, nodeLinkList);
    } else {// 辅办，记录到actionhistory表中
      ActionHistory history = new ActionHistory();
      history.record(instanceId, nodeId, action, executor, executeTime, comment,
        taskRealOwner, limitExecuteTime);
    }

    // 设置变量值
    if (variableValueList != null)
      variableValueHandler.reset(templateId, instanceId, variableValueList);

    // 设置实例当前节点进行状态 //为什么注掉？
    // stateValueHandler.setNodeState(instanceId, nodeId, conn);

    /* 判断当前NodeId任务是否可以完成 */
    boolean canNodeBeFinished = taskHandler.canFinishNode(instanceId, nodeId,
      executorsMethod, nodeLinkList);

    /* 辅办不能结束任务 */
    if (task.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_ASSISTANT)
      canNodeBeFinished = false;

    if (canNodeBeFinished) {// Node可以结束

      if (linkType.equals(Link.TYPE_ORDINARY) || linkType.equals(Link.TYPE_COLLECT)) {
        // taskCanBeFinishedNormalOperation();
        // followedNodeLinkList的元素是Link。nodeLinkList是直接后继Link
        // 根据变量值选择正确的Link，
        // 可能不是本节点的直接后续Link，可能是nodeLinkList中的Link的nextNode的后续Link
        /*
         * taskHandler.finishNode(instanceId, currentTaskId, nodeId,
         * nodeLinkList, followedNodeLinkList, conn);
         */
        // 是否可以结束实例
        boolean canInstanceBeFinished = canFinishInstance(nextLinkList);
        if (canInstanceBeFinished) {
          // cuiliguo 2006.06.09 应处理 followedNodeLinkList 。
          if (nextLinkList.size() > 1)
            nodeLinkList.add(nextLinkList.get(1));
          finishInstance(instanceId, nodeLinkList, executeTime);// 结束实例
        } else {// 不能结束实例
          // 生成后续节点的任务
          taskHandler.createFollowedNodesTask(instanceId, nextLinkList,
            taskRealOwner, positionId, executor, executeTime);
          // 结束当前节点
          taskHandler.finishNode(instanceId, currentTaskId, nodeId, nodeLinkList,
            nextLinkList);
        }
        if (linkType.equals(Link.TYPE_COLLECT)) {// 汇总类型流向
          int toCollectInstance;
          // 取得待汇总的任务id
          toCollectInstance = getToCollectTaskId(templateId, nextNodeId, executor,
            Instance.MSG_INSTANCE_NAME_TOCOLLECT,
            Instance.MSG_INSTANCE_NAME_TOCOLLECT);
          setParentInstanceId(instanceId, toCollectInstance);
          taskHandler.setParentTaskId(instanceId, CurrentTask.TYPE_TOCOLLECT_DETAIL);// 更新parent_task_id
          // 标示
          // 提交任务为待汇总明细任务
        }
      } else if (linkType.equals(Link.TYPE_RETURN)) {// 结束型Link
        boolean canInstanceBeFinished = canFinishInstance(nextLinkList);
        if (canInstanceBeFinished) {
          finishInstance(instanceId, nodeLinkList, executeTime);
        }
      } else {
        throw new WorkflowException(1218);
      }
    }// if (canNodeBeFinished)
    else if ((task.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN)) {// Node不能被结束
      if (linkType.equals(Link.TYPE_ORDINARY)) {
        if (executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO)) {
          taskHandler.removeByTask(currentTaskId);// 删除当前任务
        } else if (executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)) {
          taskHandler.removeByTask(currentTaskId);
        } else if (executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)) {// 顺签
          taskHandler.removeByTask(currentTaskId);
          boolean canFinishOrder = true;
          // 取得该节点未完成的任务

          List orderTodoList = taskHandler.getTodoListByNode(instanceId, nodeId);
          for (int i = 0; i < orderTodoList.size(); i++) {
            CurrentTaskMeta orderTask = (CurrentTaskMeta) orderTodoList.get(i);
            if (TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN == orderTask
              .getResponsibility()) {// 如果还有主办的未完成，就不能结束
              canFinishOrder = false;
            }
          }
          if (canFinishOrder) {
            // 删除当前节点的任务
            taskHandler.removeByNode(instanceId, nodeId);
            // 生成后续顺签人员当前节点的任务
            taskHandler.createNodeSerialTask(instanceId, nodeId, taskRealOwner,
              executor, executeTime);
          }
        } else {
          throw new WorkflowException(1215);
        }
      }
      // 如果节点不能结束,就不能创建汇总任务
      else if (linkType.equals(Link.TYPE_RETURN)) {
        // do nothing
      } else {
        throw new WorkflowException(1218);
      }
    }// end if (canNodeBeFinished) {
    
//    if (null != listener && null != currentTask) {
//      listener.afterExecution(currentTask);
//    }
    
    TaskResultMeta result = new TaskResultMeta();
    result.setInstanceId(instanceId);
    result.setStateValueList(stateValueHandler.getStateListByInstance(instanceId));
    result.setVariableValueList(variableValueHandler
      .getValueListByInstance(instanceId));
    return result;
  }

  public static TaskListener getListener(int nodeId) throws WorkflowException {
    TaskListener taskListener = null;
    try {
      String taskListenerName = new Node().getNode(nodeId).getTaskListener();
      if (taskListenerName != null && !taskListenerName.trim().equals("")) {
        taskListener = (TaskListener) Class.forName(taskListenerName).newInstance();
      }
    } catch (Exception e) {
      throw new WorkflowException(1210, e.toString());
    }
    return taskListener;
  }

  /**
   * 获取指定环节待汇总任务id 如果指定环节没有待汇总任务则,需要创建它,如果已经存在一个待汇总任务则查询之
   * 
   * @param templateId
   * @param nextNodeId
   * @param executor
   * @param instanceName
   *            TODO
   * @param instanceDescription
   *            TODO
   * @param conn
   * @return
   * @throws WorkflowException
   */
  public int getToCollectTaskId(int templateId, int nextNodeId, String executor,
    String instanceName, String instanceDescription) throws WorkflowException {
    CurrentTask taskHandler = new CurrentTask();
    int toCollectInstance;
    if (!taskHandler.isExistToCollectTask(executor, templateId)) {
      String timeNow = DateTime.getSysTime();
      toCollectInstance = createToCollectInstanceAndTask(executor, templateId,
        nextNodeId, timeNow, instanceName, instanceDescription);// 创建一条待汇总任务

    } else {
      CurrentTaskMeta toCollectTask = taskHandler.findToCollectTask(executor,
        templateId);
      toCollectInstance = toCollectTask.getInstanceId();
    }
    return toCollectInstance;
  }

  /**
   * @param instanceId
   * @param instanceId2
   * @param conn
   */
  public void setParentInstanceId(int instanceId, int parentInstanceId) {
    InstanceBean instanceBean = new InstanceBean();
    try {
      InstanceModel instanceModel = instanceBean.findByKey(instanceId);
      instanceModel.setParentInstanceId(parentInstanceId);
      instanceBean.update(instanceModel);

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  /**
   * 根据给定参数创建汇总流程，以及汇总任务。
   * 
   * @param executor
   * @param templateId
   * @param nodeId
   * @param instanceName
   *            TODO
   * @param instanceDescription
   *            TODO
   * @param conn
   * @return 创建的汇总待办任务
   * @throws WorkflowException
   */
  private int createToCollectInstanceAndTask(String executor, int templateId,
    int nodeId, String createTime, String instanceName, String instanceDescription)
    throws WorkflowException {
    /*
     * String instanceName = MSG_INSTANCE_NAME_TOCOLLECT; String
     * instanceDescription =MSG_INSTANCE_NAME_TOCOLLECT;
     */
    // a,创建流程
    TaskMeta task = create(templateId, instanceName, instanceDescription, executor,
      createTime);

    CurrentTask taskHandler = new CurrentTask();
    TaskExecutor taskExecutorHandler = new TaskExecutor();
    // b,将刚刚生成的起始待办任务,变成汇总节点的代表任务
    taskHandler.removeByInsatnce(task.getInstanceId());

    int[] order = new int[1];
    order[0] = 1;
    String responsibility = "1";
    String[] executorArray = new String[1];
    executorArray[0] = executor;
    // 给该汇总节点的执行者生成特殊的待办任务
    taskExecutorHandler.create(task.getInstanceId(), nodeId, executorArray, order,
      responsibility);

    taskHandler.createNodeTask(task.getInstanceId(), nodeId,
      Node.EXECUTORS_METHOD_SOLO, executor, createTime);
    // c,设置parent_task_id为待汇总类型
    taskHandler.setParentTaskId(task.getInstanceId(), CurrentTask.TYPE_TOCOLLECT);
    return task.getInstanceId();
  }

  private void finishInstance(int instanceId, List followedNodeLinkList,
    String endTime) throws WorkflowException {
    try {
      // remove all of the instnace run time dependences.
      CurrentTask currentTask = new CurrentTask();
      currentTask.removeByInsatnce(instanceId);
      Pass passHandler = new Pass();
      passHandler.removeByInstance(instanceId);
      // 由于结束的流程还可能会重做(redo),所以要保存action和执行者
      // 20060912 zhanggh
      // Action actionHandler = new Action();
      // actionHandler.removeByInstance(instanceId, conn);
      // TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
      // 删除该实例所有的任务执行者
      // taskExecutorOrderHandler.removeByInstance(instanceId, conn);

      // 重置实例当前节点完成状态
      StateValue stateValueHandler = new StateValue();
      stateValueHandler.setLinkState(instanceId, followedNodeLinkList);

      // set the instance's end time and not active.
      InstanceBean bean = new InstanceBean();
      InstanceModel model = new InstanceModel();
      model.setInstanceId(instanceId);
      model.setEndTime(endTime);
      model.setStatus(InstanceMeta.INSTANCE_STATUS_FINISHED);
      if (bean.update(model) != 1) {
        throw new WorkflowException(1110);
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1111, sqle.toString());
    }
  }

  private List getJustExecutedPrecedingTaskNodeList(int templateId, int instanceId,
    int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; i < precedingLinkList.size(); i++) {
      Link link = (Link) precedingLinkList.get(i);
      int precedingNodeId = link.getCurrentNodeId();
      if (precedingNodeId == -1)
        continue;
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'分支节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByNavigation(templateId,
          instanceId, precedingNodeId));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      case 6: // '\006'或节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;

      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  private List getJustExecutedPrecedingTaskNodeListByNavigation(int templateId,
    int instanceId, int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; i < precedingLinkList.size(); i++) {
      Link link = (Link) precedingLinkList.get(i);
      int precedingNodeId = link.getCurrentNodeId();
      if (precedingNodeId == -1)
        continue;
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'分支节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByNavigation(templateId,
          instanceId, precedingNodeId));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        break;
      case 6: // '\006'或节点
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  private List getJustExecutedPrecedingTaskNodeListByAO(int templateId,
    int instanceId, int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; i < precedingLinkList.size(); i++) {
      Link link = (Link) precedingLinkList.get(i);
      int precedingNodeId = link.getCurrentNodeId();
      if (precedingNodeId == -1)
        continue;
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'分支节点
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      case 6: // '\006'或节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  /**
   * 得到所有的已经执行过的节点
   * 
   * @param templateId
   * @param instanceId
   * @param nodeId
   * @param conn
   * @return
   * @throws WorkflowException
   */
  public List getExecutedPrecedingTaskNodeListByAO(int templateId, int instanceId,
    int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; i < precedingLinkList.size(); i++) {
      Link link = (Link) precedingLinkList.get(i);
      int precedingNodeId = link.getCurrentNodeId();
      if (precedingNodeId == -1)
        continue;
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'分支节点
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      case 6: // '\006'或节点
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  /**
   * 获取指定节点的后续节点List
   * 
   * @param templateId
   * @param instanceId
   * @param nodeId
   *            int
   * @param conn
   * @return
   * @throws WorkflowException
   * @throws
   * @see
   */
  private List getOnHandFollowedTaskNodeList(int templateId, int instanceId,
    int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();

    /* 先获取所有后续流向 */
    List followedLinkList = linkHandler.getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < followedLinkList.size(); i++) {
      Link link = (Link) followedLinkList.get(i);
      int followedNodeId = link.getNextNodeId();
      if (followedNodeId == -2)
        continue;// ????
      NodeMeta followedNode = nodeHandler.getNode(followedNodeId);
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, followedNodeId) == 0
          && taskHandler.getTaskNumByNode(instanceId, followedNodeId) != 0) {
          result.add(followedNode);
        }
        break;
      case 3: // '\003'分支节点
        result.addAll(getOnHandFollowedTaskNodeListByNavigation(templateId,
          instanceId, followedNodeId));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getOnHandFollowedTaskNodeListByAO(templateId, instanceId,
          followedNodeId));
        break;
      case 6: // '\006'或节点
        result.addAll(getOnHandFollowedTaskNodeListByAO(templateId, instanceId,
          followedNodeId));
        break;

      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  /**
   * 获取分支节点的所有后续节点List
   * 
   * @param templateId
   * @param instanceId
   * @param nodeId
   * @param conn
   * @return
   * @throws WorkflowException
   * @throws
   * @see
   */
  private List getOnHandFollowedTaskNodeListByNavigation(int templateId,
    int instanceId, int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List followedLinkList = linkHandler.getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < followedLinkList.size(); i++) {
      Link link = (Link) followedLinkList.get(i);
      int followedNodeId = link.getNextNodeId();
      if (followedNodeId == -2)
        continue;
      NodeMeta followedNode = nodeHandler.getNode(followedNodeId);
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, followedNodeId) == 0
          && taskHandler.getTaskNumByNode(instanceId, followedNodeId) != 0) {
          result.add(followedNode);
        }
        break;
      case 3: // '\003'分支节点
        result.addAll(getOnHandFollowedTaskNodeListByNavigation(templateId,
          instanceId, followedNodeId));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        break;
      case 6: // '\006'或节点
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  private List getOnHandFollowedTaskNodeListByAO(int templateId, int instanceId,
    int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Action actionHandler = new Action();
    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List followedLinkList = linkHandler.getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < followedLinkList.size(); i++) {
      Link link = (Link) followedLinkList.get(i);
      int followedNodeId = link.getNextNodeId();
      if (followedNodeId == -2)
        continue;
      NodeMeta followedNode = nodeHandler.getNode(followedNodeId);
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '\002'任务节点
        if (actionHandler.getActionNumByNode(instanceId, followedNodeId) == 0
          && taskHandler.getTaskNumByNode(instanceId, followedNodeId) != 0) {
          result.add(followedNode);
        }
        break;
      case 3: // '\003'分支节点
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getOnHandFollowedTaskNodeListByAO(templateId, instanceId,
          followedNodeId));
        break;
      case 6: // '\006'或节点
        result.addAll(getOnHandFollowedTaskNodeListByAO(templateId, instanceId,
          followedNodeId));
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  /**
   * 取得指定任务节点id的所有存在任当前任务的前置任务节点，供流程跳转时使用。
   * 
   * @param instanceId
   *            int
   * @param templateId
   *            int
   * @param nodeId
   *            int
   * @param conn
   *            Connection
   * @throws WorkflowException
   * @return List
   */
  private List getWholeExecutingPrecedingTaskNodeList(int templateId,
    int instanceId, int nodeId) throws WorkflowException {
    List result = new ArrayList();

    CurrentTask taskHandler = new CurrentTask();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; i < precedingLinkList.size(); i++) {
      int precedingNodeId = ((Link) precedingLinkList.get(i)).getCurrentNodeId();
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      if (precedingNodeId == -1) { // 开始节点
        continue;
      }
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'任务节点
        if (taskHandler.getTaskNumByNode(instanceId, precedingNodeId) != 0) {
          // 任务节点上存在当前任务，则其前置任务节点必定已完成，不继续搜索。
          result.add(precedingNode);
        } else {
          result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId,
            instanceId, precedingNodeId));
        }
        break;
      case 3: // '\003'分支节点
        result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId, instanceId,
          precedingNodeId));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId, instanceId,
          precedingNodeId));
        break;
      case 6: // '\006'或节点
        result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId, instanceId,
          precedingNodeId));
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    return result;
  }

  /**
   * 
   * 根据模板取得指定两个任务节点所在的流程实例中已经执行过的中间的任务节点， 包含目标节点。供多步回退时使用。先后顺序可靠。
   * 如果需要取得所有已执行的前置节点，设置prevNodeId为-1
   * 
   * @param instanceId
   *            int
   * @param templateId
   *            int
   * @param toNodeId
   *            int
   * @param onlyGetLastest
   *            boolean ,只取刚刚被执行的那个
   * @param conn
   *            Connection
   * @throws WorkflowException
   * @return List
   */
  /*
   * 根据getWholeExecutingPrecedingTaskNodeList修改，
   * 又与getJustExecutedPrecedingTaskNodeList类似。 by zhanggh
   */
  public List getExecutedNodeListBetween(int templateId, int instanceId, int nodeId,
    int prevNodeId, boolean onlyGetLastest) throws WorkflowException {
    List result = new ArrayList();
    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    // 得到nodeId所有前置link
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; precedingLinkList != null && i < precedingLinkList.size(); i++) {
      List tmp = new ArrayList();
      // 取其中一个前置节点的Id
      int precedingNodeId = ((Link) precedingLinkList.get(i)).getCurrentNodeId();
      if (precedingNodeId == -1) { // 开始节点
        if (prevNodeId == -1) {
          result.add(new NodeMeta()); // 增加一个空的Node，保证返回值不为空
        }
        continue;
      }
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      if (precedingNodeId == prevNodeId) {// 目标节点
        result.add(precedingNode);
        continue;
      }
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'任务节点
        // 重要不同。是action 而不是task，也就是说已经执行过
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) == 0)
          break;
        // } else {//重要不同。仍然继续搜索
        tmp = getExecutedNodeListBetween(templateId, instanceId, precedingNodeId,
          prevNodeId, onlyGetLastest);
        if (!onlyGetLastest) {
          result.addAll(tmp);
          // 只有当其找到目标节点后，才能size>0，才能添加中途经过的节点
          if (tmp.size() > 0) {
            // 任务节点上存在当前任务，则其前置任务节点必定已完成。
            result.add(precedingNode);
          }
        } else {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'分支节点
        result.addAll(getExecutedNodeListBetween(templateId, instanceId,
          precedingNodeId, prevNodeId, onlyGetLastest));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        result.addAll(getExecutedNodeListBetween(templateId, instanceId,
          precedingNodeId, prevNodeId, onlyGetLastest));
        break;
      case 6: // '\006'或节点
        result.addAll(getExecutedNodeListBetween(templateId, instanceId,
          precedingNodeId, prevNodeId, onlyGetLastest));
        break;
      default:
        throw new WorkflowException(1220);
      }
    }
    result = getExecutedNodesOrderByTime(result, instanceId, onlyGetLastest);
    return result;
  }

  /**
   * 根据ActionHistory取得从某个节点至另一个节点之间的正常提交的节点列表， 并按执行时间先后顺序。注意不考虑边缘环节
   * 
   * @param templateId
   * @param instanceId
   * @param preNodeId
   *            -1表示第一个环节
   * @param nodeId
   *            前一个环节 -2表示最后一个环节
   * @param onlyNormalAction
   *            只取正常提交的，不包含回收、退回等操作
   * @param conn
   * @return 返回NodeMeta列表
   * @throws WorkflowException
   */
  public List getExecutedActionListBetweenTimeOrder(int templateId, int instanceId,
    int preNodeId, int nodeId, boolean onlyNormalAction) throws WorkflowException {
    List result = new ArrayList();
    List tempList = new ArrayList();
    List resultList = new ArrayList();
    // NodeMeta nodeMeta = null;
    ActionMeta actionMeta;
    boolean isFound = false;

    boolean isValidePreNodeId = false;
    boolean isValideNodeId = false;
    if (preNodeId == -1)
      isValidePreNodeId = true;
    if (nodeId == -2)
      isValideNodeId = true;
    List nodeList = ConfigureFacade.getNodeList(templateId);
    Iterator iter = nodeList.iterator();
    while (iter.hasNext()) {
      NodeMeta element = (NodeMeta) iter.next();
      if (!isValideNodeId) {
        if (element.getId() == nodeId)
          isValideNodeId = true;
      }
      if (!isValidePreNodeId) {
        if (element.getId() == preNodeId)
          isValidePreNodeId = true;
      }
      if (isValideNodeId && isValidePreNodeId)
        break;
    }
    if (!isValideNodeId || !isValidePreNodeId)
      throw new WorkflowException(2206);

    result = new ActionHistory().getActionListByInstance(instanceId, 1);
    if (result != null) {
      // 把ActionMeta列表转成NodeMeta列表
      for (int i = 0; i < result.size(); i++) {
        actionMeta = (ActionMeta) result.get(i);
        int tempNodeId = actionMeta.getNodeId();
        if (tempNodeId == nodeId)
          break;// nodeId以后的都不考虑，如果是-2，表示所有的已办都要
        // nodeMeta = new Node().getNode(tempNodeId,conn);
        if (onlyNormalAction) {
          if (isNormalCommit(actionMeta.getActionName())) {
            tempList.add(actionMeta);
          }
        } else {
          tempList.add(actionMeta);
        }
      }
      // 去掉起始节点之前的节点
      for (int j = 0; j < tempList.size(); j++) {
        if (preNodeId == -1
          || preNodeId == ((ActionMeta) tempList.get(j)).getNodeId())
          isFound = true;// preNodeId以前的都不考虑
        if (isFound) {
          resultList.add(tempList.get(j));
        }
      }
    }
    return resultList;
  }

  /**
   * 把List中的节点按执行时间顺序逆序排序
   * 
   * @param lstSource
   *            其内的元素是NodeMeta
   * @param 返回的List的元素是NodeMeta，至多有一个元素
   */
  private List getExecutedNodesOrderByTime(List lstSource, int instanceId,
    boolean onlyGetLastest) throws WorkflowException {
    List lstResult = new ArrayList();
    ActionMeta actionMeta = null;
    NodeMeta nodeMeta = null;
    if (null != lstSource) {
      // 取得该实例所有的action记录,取得的是按时间升序
      List actions = new ActionHistory().getActionListByInstance(instanceId, 1);
      if (actions != null) {
        for (int i = actions.size() - 1; i > -1; i--) {
          // 把ActionMeta列表转成NodeMeta列表
          actionMeta = (ActionMeta) actions.get(i);
          int tempNodeId = actionMeta.getNodeId();
          nodeMeta = new Node().getNode(tempNodeId);
          if (lstSource.indexOf(nodeMeta) > -1 && // 在源中存在，并且是正常提交
            isNormalCommit(actionMeta.getActionName())) {
            if (-1 == lstResult.indexOf(nodeMeta)) {// 在lstResult中尚没有，如果有就不添加
              lstResult.add(nodeMeta);
              // 如果只希望得到刚刚执行的一个节点，并且已经有，就删除
              if (onlyGetLastest) {
                break;
              }
            }
          }
        }
        if (lstSource.indexOf(new NodeMeta()) > -1) {// 特殊处理空NodeMeta
          lstResult.add(new NodeMeta());
        }
      }
    }
    return lstResult;
  }

  private boolean isNormalCommit(String strActionName) {

    /** ActionMeta/ACTION_TYPE_FORWARD_TASK = //"forward_task";//提交 */
    boolean isNormalCommit = !(ActionMeta.ACTION_TYPE_AUTHORIZE_TASK
      .equals(strActionName)) // "authorize_task";//授权
      && !(ActionMeta.ACTION_TYPE_CALLBACK_FLOW.equals(strActionName)) // "callback_flow";//回收
      && !(ActionMeta.ACTION_TYPE_GIVEBACK_FLOW.equals(strActionName)) // "giveback_flow";//回退一步
      && !(ActionMeta.ACTION_TYPE_TRANSFER_FLOW.equals(strActionName)) // "transfer_flow";//流程跳转
      && !(ActionMeta.ACTION_TYPE_UNTREAD_FLOW.equals(strActionName)) // "untread_flow";//回退多步.zhanggh
      && !(ActionMeta.ACTION_TYPE_ACTIVATE_INSTANCE.equals(strActionName)) // "activate_instance";//激活
      && !(ActionMeta.ACTION_TYPE_DEACTIVATE_INSTANCE.equals(strActionName)) // "deactivate_instance";//冻结
      && !(ActionMeta.ACTION_TYPE_INTERRUPT_INSTANCE.equals(strActionName)) // "interrupt_instance";//中止
      && !(ActionMeta.ACTION_TYPE_RESTART_INSTANCE.equals(strActionName)); // "restart_instance";//重启
    return isNormalCommit;
  }

  /**
   * 返回的是该节点后的Link的列表，会根据变量值进行匹配，只有那些最终会被执行的任务
   * 节点前的Link以及结束前的Link会被添加进去，分支节点前的Link不会被添加
   */
  /* 更改参数列表。by zhanggh */
  /*
   * public List getRightFollowedTaskLinkList(int instanceId, int templateId,
   * int activityId, List valueList, Connection conn) throws WorkflowException {
   * List result = new ArrayList(); List nodeLinkList = new
   * Link().getFollowedLinkList(templateId, activityId, null,conn); //
   */
  public List getRightFollowedTaskLinkList(int instanceId, int templateId,
    List nodeLinkList, List valueList) throws WorkflowException {
    List result = new ArrayList();

    Node nodeHandler = new Node();
    for (int i = 0; i < nodeLinkList.size(); i++) {
      // 如果下一节点是结束节点，添加该Link，break
      int followedNodeId = ((Link) nodeLinkList.get(i)).getNextNodeId();
      if (followedNodeId == -2) {
        // 元素是Link
        result.add(nodeLinkList.get(i));
        break;
      }
      // 该Link的下一个节点
      NodeMeta followedNode = nodeHandler.getNode(followedNodeId);
      // 下一节点的类型
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '\002'任务节点
        result.add(nodeLinkList.get(i));
        break;
      case 3: // '\003'分支节点。在分支节点上不停留
        result.addAll(getRightFollowedTaskLinkListByNavigation(templateId,
          followedNodeId, valueList));
        break;
      case 4: // '\004'事件触发节点
        break;
      case 5: // '\005'与节点
        if (canPassAndNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
        }
        break;

      case 6: // '\006'或节点
        CurrentTask taskHandler = new CurrentTask();
        TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
        // TCJLODO:canPassOrNode方法有问题
        if (canPassOrNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
          // 已经可以通过了，其他的就不必执行了。删除taskExecutorOrder 和currentTask
          taskExecutorOrderHandler.removeByNode(templateId, instanceId, -1,
            followedNodeId);
          taskHandler.removeByNode(templateId, instanceId, -1, followedNodeId);
        }
        break;

      default:
        throw new WorkflowException(1220);
      }
    }

    return result;
  }

  /**
   * get the navigation node's right immediate followed task node's link list
   * 根据变量值，取得符合条件的Link
   * 
   * @param templateId
   *            int
   * @param nodeId
   *            int
   * @param valueList
   *            List
   * @param conn
   *            Connection
   * @throws WorkflowException
   * @return List
   */
  private List getRightFollowedTaskLinkListByNavigation(int templateId, int nodeId,
    List valueList) throws WorkflowException {
    List result = new ArrayList();

    int followedNodeId = nodeId;
    Link followedNodeLink = null;
    // cuiliguo 2006.06.09 返回的列表中增加结束节点前的流向 followedNodeLink2 。
    Link followedNodeLink2 = null;
    String followedNodeType = null;
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    do {
      // 根据变量值计算出合适的Link
      followedNodeLink2 = followedNodeLink;
      followedNodeLink = linkHandler.getRIFNavigationLink(templateId,
        followedNodeId, valueList);
      if (followedNodeLink == null)
        throw new WorkflowException(1225);
      // 递归
      followedNodeId = followedNodeLink.getNextNodeId();
      if (followedNodeId == -2)
        break;
      else
        followedNodeLink2 = null;
      followedNodeType = nodeHandler.getNode(followedNodeId).getType();

      // 如果下一节点仍然是分支(路由)节点(Nav)，继续计算Link
      // 直到下一节点是分支节点以外的类型，或者是结束节点
    } while (followedNodeType.equals(Node.TYPE_NAVIGATION));
    result.add(followedNodeLink);
    if (followedNodeLink2 != null) {
      result.add(followedNodeLink2);
    }
    return result;
  }

  private List getRightFollowedTaskLinkListByAO(int templateId, int instanceId,
    int nodeId) throws WorkflowException {
    List result = new ArrayList();

    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    // 取得所有后继Link
    List list = linkHandler.getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < list.size(); i++) {
      Link followedLink = (Link) list.get(i);
      NodeMeta followedNode = nodeHandler.getNode(followedLink.getNextNodeId());
      int followedNodeId = followedNode.getId();
      if (followedNodeId == -2) {
        result.add(followedLink);
        break;
      }
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '2'任务节点
        result.add(followedLink);
        break;
      case 3: // '3'分支节点
      case 4: // '4'事件触发节点
      case 5: // '5'与结点
        // 递归
        if (canPassAndNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
        }
        break;
      case 6: // '6'或节点
        if (canPassOrNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
        }
        break;
      default:
        throw new WorkflowException(2000, "配置错误");
      }
    }

    return result;
  }

  /*
   * 判断And节点条件是否满足 TODO：有问题
   */
  private boolean canPassAndNode(int templateId, int instanceId, int nodeId)
    throws WorkflowException {
    boolean result = true;

    Node nodeHandler = new Node();
    // 取得所有前驱节点
    List precedingNodeList = nodeHandler.getPrecedingNodeList(templateId, nodeId);
    for (int i = 0; i < precedingNodeList.size(); i++) {
      NodeMeta precedingNode = (NodeMeta) precedingNodeList.get(i);
      int precedingNodeId = precedingNode.getId();
      if (precedingNodeId == -1)
        continue;
      // 前驱节点的类型
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '2'任务节点
        Action action = new Action();
        CurrentTask currentTask = new CurrentTask();
        // //与Or节点区别的关键。Action数目为0 或者还有Task未执行完成，则不能通过
        // N个前驱节点，只要有一个未完成，则就不能通过
        // 修改。因为节点完成辅办任务仍存在
        // TCJLODO：And节点概念理解也有问题，应该是所有前驱都完成
        if (action.getActionNumByNode(instanceId, precedingNodeId) <= 0
          || currentTask.getMainTaskNumByNode(instanceId, precedingNodeId) != 0)
          result = false;
        break;
      case 3: // '3'分支节点
      case 4: // '4'事件触发节点
      case 5: // '5'与结点
        result = canPassAndNode(templateId, instanceId, precedingNodeId);
        break;
      case 6: // '6'或节点
        result = canPassOrNode(templateId, instanceId, precedingNodeId);
        break;
      default:
        throw new WorkflowException(2000, "配置错误");
      }
      // N个前驱节点，只要有一个未完成，则就不能通过，不用继续判断了
      if (!result)
        break;
    }
    return result;
  }

  /*
   * 判断Or节点条件是否满足 TODO:有问题
   */
  private boolean canPassOrNode(int templateId, int instanceId, int nodeId)
    throws WorkflowException {
    boolean result = false;

    Node nodeHandler = new Node();
    List precedingNodeList = nodeHandler.getPrecedingNodeList(templateId, nodeId);
    for (int i = 0; i < precedingNodeList.size(); i++) {
      NodeMeta precedingNode = (NodeMeta) precedingNodeList.get(i);
      int precedingNodeId = precedingNode.getId();
      if (precedingNodeId == -1)
        continue;
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '2'任务节点
        Action action = new Action();
        CurrentTask currentTask = new CurrentTask();
        // //与AND节点区别的关键。已执行的大于0，而且所有任务都执行完成
        // 修改。因为节点完成辅办任务仍存在
        // TCJLODO：Or节点概念理解也有问题，既不是XOR，也不是OR。和And相差不大
        if (action.getActionNumByNode(instanceId, precedingNodeId) > 0
          && currentTask.getMainTaskNumByNode(instanceId, precedingNodeId) == 0)
          result = true;
        break;
      case 3: // '3'分支节点
      case 4: // '4'事件触发节点
      case 5: // '5'与节点
        result = canPassAndNode(templateId, instanceId, precedingNodeId);
        break;
      case 6: // '6'或节点
        result = canPassOrNode(templateId, instanceId, precedingNodeId);
        break;
      default:
        throw new WorkflowException(2000, "配置错误");
      }
      if (result)
        break;
    }
    return result;
  }

  public InstanceMeta getInstance(int instanceId) throws WorkflowException {
    InstanceMeta result = new InstanceMeta();

    try {
      InstanceQuery query = new InstanceQuery();
      result = wrap(query.getInstance(instanceId));
    } catch (SQLException e) {
      throw new WorkflowException(1195, e.toString());
    }
    return result;
  }

  private InstanceMeta wrap(InstanceInfo model) {
    InstanceMeta meta = new InstanceMeta();
    if (model.getTemplateId() != null) {
      meta.setTemplateId(model.getTemplateId().intValue());
    }
    meta.setTemplateName(model.getTemplateName());
    if (model.getInstanceId() != null) {
      meta.setInstanceId(model.getInstanceId().intValue());
    }
    meta.setName(model.getName());
    meta.setDescription(model.getDescription());
    if (model.getOwner() != null) {
      meta.setOwner(model.getOwner());
    }
    meta.setOwnerName(model.getOwnerName());
    meta.setStartTime(model.getStartTime());
    if (model.getEndTime() != null && !model.getEndTime().equals(INITIAL_END_TIME))
      meta.setEndTime(model.getEndTime());
    if (model.getStatus() != null)
      meta.setStatus(model.getStatus().intValue());
    return meta;
  }

  private InstanceModel unwrap(InstanceMeta meta) {
    InstanceModel unwrapper = new InstanceModel();
    if (meta.getInstanceId() != 0)
      unwrapper.setInstanceId(meta.getInstanceId());
    if (meta.getName() != null)
      unwrapper.setName(meta.getName());
    if (meta.getDescription() != null)
      unwrapper.setDescription(meta.getDescription());
    if (meta.getTemplateId() != 0)
      unwrapper.setTemplateId(meta.getTemplateId());
    if (meta.getOwner() != null)
      unwrapper.setOwner(meta.getOwner());
    if (meta.getStartTime() != null)
      unwrapper.setStartTime(meta.getStartTime());
    if (meta.getEndTime() != null)
      unwrapper.setEndTime(meta.getEndTime());
    if (meta.getStatus() != 0)
      unwrapper.setStatus(meta.getStatus());
    return unwrapper;
  }

  /**
   * 根据汇总流程 获取子任务列表
   * 
   * @param instanceId
   * @param executor
   * @param conn
   * @return
   * @throws WorkflowException
   */
  public List getChildTodoListByParentInstance(int instanceId, String executor)
    throws WorkflowException {
    CurrentTask taskHandler = new CurrentTask();
    int parentTaskId;
    // TCJLODO attention
    List parentTaskMetaList = taskHandler.getTodoListByInstance(instanceId, 1);
    if (parentTaskMetaList.size() == 0)
      return new ArrayList();

    CurrentTaskMeta parentTaskMeta = (CurrentTaskMeta) parentTaskMetaList.get(0);
    parentTaskId = parentTaskMeta.getCurrentTaskId();
    List childTodoList = taskHandler.getChildToDoListByParentId(parentTaskId);
    return childTodoList;
  }

  /**
   * 根据汇总任务 获取子任务列表
   * 
   * @param taskId
   * @param executor
   * @param conn
   * @return
   * @throws WorkflowException
   */
  public List getChildToListByParentTaskId(int taskId, String executor)
    throws WorkflowException {
    CurrentTask taskHandler = new CurrentTask();
    return taskHandler.getChildToDoListByParentId(taskId);
  }

  /**
   * 获取流程经过的汇总节点id
   * 
   * @param instanceId
   * @param conn
   * @return
   * @throws WorkflowException
   */
  public int getPassedCollectNodeId(int instanceId) throws WorkflowException {
    int result = 0;
    InstanceQuery instanceQuery = new InstanceQuery();
    LinkBean linkBean = new LinkBean();
    ActionHistory historyHandler = new ActionHistory();
    try {
      // 根据流程定义获取所有需汇总流向
      InstanceMeta instanceMeta = wrap(instanceQuery.getInstance(instanceId));
      List lstAllCollectNode = new ArrayList();
      List lstAllLink = linkBean.getLinkList(instanceMeta.getTemplateId());
      Iterator iter = lstAllLink.iterator();
      while (iter.hasNext()) {
        LinkModel element = (LinkModel) iter.next();
        if (element.getLinkType().equalsIgnoreCase(Link.TYPE_COLLECT)) {
          lstAllCollectNode.add(element.getNextNodeId());
        }
      }
      if (lstAllCollectNode.size() == 0)
        throw new WorkflowException(2205);
      else if (lstAllCollectNode.size() == 1) {
        result = ((Integer) lstAllCollectNode.get(0)).intValue();
      } else {
        // 存在多个汇总节点，需要根据运行实例过滤出真正经过的汇总节点
        List lstActionHistory = historyHandler
          .getActionListByInstance(instanceId, 1);
        Iterator iterActionHistory = lstActionHistory.iterator();
        while (iterActionHistory.hasNext()) {
          ActionMeta element = (ActionMeta) iterActionHistory.next();
          if (lstAllCollectNode.contains(new Integer(element.getNodeId()))) {
            result = element.getNodeId();
            break;
          }
        }
      }
      if (result == 0)
        throw new WorkflowException(2205);
    } catch (SQLException e) {
      throw new WorkflowException(2205);
    }

    return result;
  }

  private ActionMeta getActionMeta(int nodeId, int instanceId, String executor) {
    ActionHistoryQuery ahq = new ActionHistoryQuery();
    ActionMeta am = null;
    if (nodeId == -1) {
      try {
        List ahList = ahq.getActionListByOwnerAndInstance(-1, -1, executor,
          instanceId, 1);
        if (ahList.size() > 0) {
          // get the last one
          ActionHistoryInfo ahi = (ActionHistoryInfo) ahList.get(ahList.size() - 1);
          nodeId = ahi.getNodeId().intValue();
          am = ActionHistory.wrap(ahi);
        }
      } catch (SQLException se) {
      }
    }
    // add by liubo 20071024
    if (am == null) {
      try {
        List historyList = ahq.getActionListByInstanceAndNode(instanceId, nodeId, 1);
        if (historyList.size() > 0) {
          ActionHistoryInfo ahi = (ActionHistoryInfo) historyList.get(historyList
            .size() - 1);
          am = ActionHistory.wrap(ahi);
        }
      } catch (SQLException ex) {
        // TCJLODO:
      }
    }
    return am;
  }

  public void appendExecutor(int instanceId, int nodeId, String creator,
    String appendExecutors) throws WorkflowException {
    String executorDef = appendExecutors;
    String[] defs = executorDef.split(",");
    List newExecutors = new ArrayList();
    List newExecutors1 = new ArrayList();
    List executors = ExecuteFacade.getTaskExecutorList(instanceId, nodeId);
    // 删除旧的执行人
    for (int i = 0; i < executors.size(); i++) {
      TaskExecutorMeta e = (TaskExecutorMeta) executors.get(i);
      newExecutors.add(e.getExecutor());
      ExecuteFacade.removeTaskExecutor(e.getId());
    }
    // 添加进新的执行人
    for (int i = 0; i < defs.length; i++) {
      if (!newExecutors.contains(defs[i])) {
        newExecutors.add(defs[i]);
        newExecutors1.add(defs[i]);
      }
    }
    // 创建task_executor
    for (int i = 0; i < newExecutors.size(); i++) {
      ExecuteFacade.createTaskExecutor(instanceId, nodeId, (String) newExecutors
        .get(i), i + 1, 1);
    }
    // 为追加的执行人创建待办任务,现在只考虑独签和并签
    CurrentTaskBean bean = new CurrentTaskBean();
    String executeTime = DateTime.getSysTime();
    try {
      for (int i = 0; i < newExecutors1.size(); i++) {
        int newId = Sequence.fetch(Sequence.SEQ_CURRENT_TASK);
        CurrentTaskModel model = new CurrentTaskModel();
        model.setCurrentTaskId(newId);
        model.setInstanceId(instanceId);
        model.setNodeId(nodeId);
        String executor = (String) newExecutors1.get(i);
        model.setExecutor(executor);
        model.setResponsibility(1);
        model.setDelegationId(1);
        model.setOwner(executor);
        model.setCreator(creator);
        model.setCreateTime(executeTime);
        bean.insert(model);
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1121, sqle.toString());
    }
  }

  public void removeExecutor(int instanceId, int nodeId, String strUserId)
    throws WorkflowException {
    // 删除执行人
    TaskExecutor te = new TaskExecutor();
    te.removeByExecutor(instanceId, nodeId, strUserId);
    // 删除待办任务
    CurrentTask task = new CurrentTask();
    task.removeByExecutor(instanceId, nodeId, strUserId);
  }

  public void removeExecutors(String userId, int instanceId, int nodeId,
    String[] exes) throws WorkflowException {
    for (int i = 0; i < exes.length; i++) {
      String exeId = exes[i];
      this.removeExecutor(instanceId, nodeId, exeId);
    }
  }

}
