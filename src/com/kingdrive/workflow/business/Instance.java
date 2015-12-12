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

  public static final String MSG_INSTANCE_NAME_TOCOLLECT = "�����ܴ�������";

  public static final String MSG_INSTANCE_NAME_COLLECTED = "�ѻ��ܴ�������";

  public static final String MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT = "���˻صĻ��ܴ�������";

  public static final String MSG_INSTANCE_NAME_WITHDRAWEDCOLLECT_DETAIL = "���˻صĴ�������";

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
      users = creator.split(",");// �����Ա�Զ��ŷָ�
    } else {
      throw new WorkflowException(1015);
    }
    InstanceMeta meta = new InstanceMeta();
    meta.setName(name);
    // meta.setDescription(description);
    meta.setTemplateId(templateId);
    meta.setOwner(users[0]);// �õ�һ���û������崴������
    meta.setStartTime(createTime);
    meta.setEndTime(Instance.INITIAL_END_TIME);
    create(meta);

    // get the instanceId.
    int instanceId = meta.getInstanceId();

    Node nodeHandler = new Node();
    int nodeId = nodeHandler.getStartNode(templateId).getId();

    // ����״̬
    StateValue stateValueHandler = new StateValue();
    // ���ɸýڵ������ʱ��������Ӧ״ֵ̬������Ӧ״̬
    stateValueHandler.setNodeState(instanceId, nodeId);
    CurrentTask taskHandler = new CurrentTask();
    TaskMeta taskMeta = taskHandler.createStartTask(instanceId, nodeId, users[0],
      createTime);
    for (int i = 1; i < users.length; i++) {
      taskHandler.createStartTask(instanceId, nodeId, users[i], createTime);// ��������Ա��������
    }
    return taskMeta;// ���ڷ���ֵ���ͣ�ֻ���ص�һ����Ա������
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
   * ��תʵ������
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

    // ������ڵ��action��pass��node state��current task��execute term��executor.
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
    // ��תʱֻ���ýڵ��״̬
    // stateHandler.setLinkState(instanceId, precedingLinkList, conn);
    stateValueHandler.setNodeState(instanceId, nextNodeId);
  }

  /**
   * ����ʵ������ǰ����ڵ��ԭǰ������ڵ㣬
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

    // ������ڵ��action��pass��node state��current task��execute term��executor.
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);
    stateValueHandler.removeNodeState(instanceId, nodeId);
    taskHandler.removeByNode(instanceId, nodeId);
    termHandler.removeByNode(instanceId, nodeId);
    executorHandler.removeByNode(instanceId, nodeId);

    List precedingTaskNodeList = getJustExecutedPrecedingTaskNodeList(templateId,
      instanceId, nodeId);

    // ����ԭ�ڵ������
    taskHandler.createPrecedingNodesTask(instanceId, precedingTaskNodeList,
      executor, executeTime);

    // ���ԭ�ڵ��������action��pass������¼ԭ�ڵ��ǰ��������������ǰ������״̬
    // List precedingLinkList = new ArrayList();
    // ���ԭ�ڵ��������action��pass������¼ԭ�ڵ�ĺ����������������������״̬
    List followingLinkList = new ArrayList();
    for (int i = 0; i < precedingTaskNodeList.size(); i++) {
      int precedingTaskNodeId = ((NodeMeta) precedingTaskNodeList.get(i)).getId();
      // action��Ҫɾ�� by zhanggh 050624
      // actionHandler.removeByNode(instanceId, precedingTaskNodeId,
      // conn);
      passHandler.removeByNode(instanceId, precedingTaskNodeId);

      // ��¼ԭ�ڵ��ǰ������
      // precedingLinkList.addAll(linkHandler.getPrecedingLinkList(templateId,
      // precedingTaskNodeId, conn));
      // �˻�ʱ��¼ԭ�ڵ�ĺ�������
      followingLinkList.addAll(linkHandler.getFollowedLinkList(templateId,
        precedingTaskNodeId));
    }

    // ����ԭ�ڵ��ǰ����������״̬
    // stateValueHandler.setLinkState(instanceId, precedingLinkList, conn);
    // ����ԭ�ڵ�ĺ�����������״̬,�Ա������˻ص�״̬���������ύ
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
    // ����-1,-2�ڵ�
    if (-1 == prevNodeId) {// ��ʾ�в�֪������Ҫͨ�������֪��nodeId��ǰһ���ڵ�
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
        am = (ActionMeta) doneList.get(doneList.size() - 1);// ֻȡ���ִ�е��Ǹ�
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
   * ����ʵ����ָ������ִ�й�������ڵ�
   * 
   * @param instanceId
   *            int ʵ��Id
   * @param nodeId
   *            int ��ǰ�ڵ�
   * @param prevNodeId
   *            int ��ִ�й���ĳһ�ڵ㣬Ŀ��ڵ㡣 -1�����ʾ�в�֪������Ҫͨ�������֪��nodeId��ǰһ���ڵ�,
   *            -2��ʾ��һ���ڵ�
   * @param executor
   *            int ��ǰִ����
   * @param executeTime
   *            String ִ��ʱ��
   * @param comment
   *            String ���˵��������
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

    // ������ڵ��action��pass��node state��current task��execute term��executor.
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
     * executorMeta.getExecutor(), executeTime, conn); //break;//ֻȡ��һ��//todo }
     */

    taskHandler.createNodeTask(instanceId, prevNodeId, prevNodeMeta
      .getExecutorsMethod(), executor, executeTime);
    // �õ����ڵ�֮��ִ�й��Ľڵ���б�
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

    // ���ԭ�ڵ��Լ����������еĽڵ��������pass��
    // List precedingLinkList = new ArrayList();
    List followingLinkList = new ArrayList();
    for (int i = 0; i < precedingNodeList.size(); i++) {
      int precedingTaskNodeId = ((NodeMeta) precedingNodeList.get(i)).getId();
      actionHandler.removeByNode(instanceId, precedingTaskNodeId);
      passHandler.removeByNode(instanceId, precedingTaskNodeId);
    }
    // ��¼ԭ�ڵ��ǰ������
    // precedingLinkList.addAll(linkHandler.getPrecedingLinkList(templateId,
    // prevNodeId, conn));
    // ����ԭ�ڵ��ǰ����������״̬
    // stateValueHandler.setLinkState(instanceId, precedingLinkList, conn);
    // ��¼ԭ�ڵ�ĺ�������
    followingLinkList
      .addAll(linkHandler.getFollowedLinkList(templateId, prevNodeId));
    // ����ԭ�ڵ�ĺ�����������״̬
    stateValueHandler.setLinkState(instanceId, followingLinkList);
    // ����ʵ����ǰ�ڵ����״̬
    // �˻�ʱ���ٰѽڵ�״ֵ̬���ø�״̬����
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
    // û�����Ĵ��죬���߲������죬���������˻�
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
   * ��Ȩ
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
   * �ƽ�
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
   * ��ʵ�������лص���ǰ������ڵ� ���ָ������ڵ�ĺ�������ڵ��Ѿ����л��Ѿ���ɣ��򲻿��лأ���WorkflowException(1113)
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

    // ȡ�ú�������ڵ��������action��pass��executor��term��current
    // task����ȡ�ú�������ڵ��ǰ�����ӣ������������״̬
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
      // ɾ���Ѿ�ִ���˵ĸ������ݡ�by zhanggh
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

    // ������ǰ�ڵ������
    String executorsMethod = nodeHandler.getNode(nodeId).getExecutorsMethod();
    taskHandler.createNodeTask(instanceId, nodeId, executorsMethod, executor,
      executeTime);

    // ������ڵ��action��pass��node state.
    actionHandler.removeByNode(instanceId, nodeId);
    passHandler.removeByNode(instanceId, nodeId);

    // List precedingLinkList = linkHandler.getPrecedingLinkList(templateId,
    // nodeId, conn);
    // stateValueHandler.setLinkState(instanceId, precedingLinkList, conn);
    // �ջ�ʱ״̬Ϊ��ǰ�ڵ��״̬
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
   * ����ʵ������ ���ʵ����״̬Ϊ��ֹ��INSTANCE_STATUS_INTERRUPTED�� ��originalΪtrue���ʵ��������ԭִ��λ��
   * ��ʼ��������ʼ����ڵ㣬����ǰ�粻��������ִ���ߣ���ִ����ȱʡΪ������ִ����
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
      // ��ʵ��������ԭִ��λ��
      // ��������ֹʵ��ʱû�ж�run time dependents������������ʱ�����κδ���
    } else {
      // ��ʵ����������ʼ����ڵ�
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

      // ȡ���׸��ڵ��ԭ����ִ����,�����ݴ����ƴ�������
      // ����Ƕ����ˣ�����ֻ������һ����ʵ��ִ���˲���������Ȼ��������˷�����
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
   * ����ʵ������ ʵ��������(״̬Ϊ����(9))����Ҫ���²����������޸ġ� 1.������ִ���ߴ�����Ӧ�ڵ�(һ�������һ���ڵ�)�Ĵ�������
   * 2.������״̬���õ��ýڵ㣬 3.��ʵ��״̬��Ϊ������(1)
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

    // ��ʵ�����������һ��action�Ľڵ�
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

    // ��������
    taskHandler.createNodeTask(instanceId, nodeId, executorsMethod, executor,
      executeTime);
    // ���÷��ؽ��
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
   * ���ʵ��
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
    // ��Ӧ�����ڵ�ǰ��·�ɽڵ����� by zhanggh 050629
    if (nodeLinkList.size() == 0) {
      finished = true;
    }
    for (int i = 0; i < nodeLinkList.size(); i++) {
      // ���ĳһ������(link)����һ���ڵ��ǽ����ڵ�
      int followedNodeId = ((Link) nodeLinkList.get(i)).getNextNodeId();
      if (followedNodeId == -2) {// �����ڵ�
        finished = true;
        break;
      }
    }
    return finished;
  }

  /**
   * ʵ��ִ�е������������������������ķ������������ǰ�����before/afterExecution
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
   * ִ�е�ǰʵ��
   * 
   * @param templateId
   * @param instanceId
   * @param nodeId
   * @param conn
   * @return TaskResultMeta dto���󣬰���
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
  //�ⲿ�Ѽ����¸�link
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
    // ȡ��һ��Ԫ�أ�����Ϊ����NodeId��actionȡ�õ�һ��ֻ��һ��link
    linkType = ((Link) nodeLinkList.get(0)).getType();
    nextNodeId = ((Link) nodeLinkList.get(0)).getNextNodeId();
    action = ((Link) nodeLinkList.get(0)).getActionName();
    // we will modify the instance but create it when normal execution.
    /* ����instance */
    InstanceMeta instance = new InstanceMeta(); // getInstance(instanceId,
    // conn);
    instance.setInstanceId(instanceId);
    instance.setName(instanceName);
    // instance.setDescription(instanceDescription);
    update(instance);

    /* ��¼CurrentTask����ʷ��¼ */
    CurrentTaskMeta task = taskHandler.getCurrentTask(currentTaskId, 1);
    taskRealOwner = task.getOwner();
    limitExecuteTime = task.getLimitExecuteTime();
    if (task.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN) {
      // ���죬��¼��action����.�÷����а���дactionhistory��¼
      actionHandler.record(instanceId, nodeId, action, executor, executeTime,
        comment, taskRealOwner, limitExecuteTime);
      // ��¼pass.�����к��link����¼����
      passCountHandler.record(instanceId, nodeLinkList);
    } else {// ���죬��¼��actionhistory����
      ActionHistory history = new ActionHistory();
      history.record(instanceId, nodeId, action, executor, executeTime, comment,
        taskRealOwner, limitExecuteTime);
    }

    // ���ñ���ֵ
    if (variableValueList != null)
      variableValueHandler.reset(templateId, instanceId, variableValueList);

    // ����ʵ����ǰ�ڵ����״̬ //Ϊʲôע����
    // stateValueHandler.setNodeState(instanceId, nodeId, conn);

    /* �жϵ�ǰNodeId�����Ƿ������� */
    boolean canNodeBeFinished = taskHandler.canFinishNode(instanceId, nodeId,
      executorsMethod, nodeLinkList);

    /* ���첻�ܽ������� */
    if (task.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_ASSISTANT)
      canNodeBeFinished = false;

    if (canNodeBeFinished) {// Node���Խ���

      if (linkType.equals(Link.TYPE_ORDINARY) || linkType.equals(Link.TYPE_COLLECT)) {
        // taskCanBeFinishedNormalOperation();
        // followedNodeLinkList��Ԫ����Link��nodeLinkList��ֱ�Ӻ��Link
        // ���ݱ���ֵѡ����ȷ��Link��
        // ���ܲ��Ǳ��ڵ��ֱ�Ӻ���Link��������nodeLinkList�е�Link��nextNode�ĺ���Link
        /*
         * taskHandler.finishNode(instanceId, currentTaskId, nodeId,
         * nodeLinkList, followedNodeLinkList, conn);
         */
        // �Ƿ���Խ���ʵ��
        boolean canInstanceBeFinished = canFinishInstance(nextLinkList);
        if (canInstanceBeFinished) {
          // cuiliguo 2006.06.09 Ӧ���� followedNodeLinkList ��
          if (nextLinkList.size() > 1)
            nodeLinkList.add(nextLinkList.get(1));
          finishInstance(instanceId, nodeLinkList, executeTime);// ����ʵ��
        } else {// ���ܽ���ʵ��
          // ���ɺ����ڵ������
          taskHandler.createFollowedNodesTask(instanceId, nextLinkList,
            taskRealOwner, positionId, executor, executeTime);
          // ������ǰ�ڵ�
          taskHandler.finishNode(instanceId, currentTaskId, nodeId, nodeLinkList,
            nextLinkList);
        }
        if (linkType.equals(Link.TYPE_COLLECT)) {// ������������
          int toCollectInstance;
          // ȡ�ô����ܵ�����id
          toCollectInstance = getToCollectTaskId(templateId, nextNodeId, executor,
            Instance.MSG_INSTANCE_NAME_TOCOLLECT,
            Instance.MSG_INSTANCE_NAME_TOCOLLECT);
          setParentInstanceId(instanceId, toCollectInstance);
          taskHandler.setParentTaskId(instanceId, CurrentTask.TYPE_TOCOLLECT_DETAIL);// ����parent_task_id
          // ��ʾ
          // �ύ����Ϊ��������ϸ����
        }
      } else if (linkType.equals(Link.TYPE_RETURN)) {// ������Link
        boolean canInstanceBeFinished = canFinishInstance(nextLinkList);
        if (canInstanceBeFinished) {
          finishInstance(instanceId, nodeLinkList, executeTime);
        }
      } else {
        throw new WorkflowException(1218);
      }
    }// if (canNodeBeFinished)
    else if ((task.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN)) {// Node���ܱ�����
      if (linkType.equals(Link.TYPE_ORDINARY)) {
        if (executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO)) {
          taskHandler.removeByTask(currentTaskId);// ɾ����ǰ����
        } else if (executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)) {
          taskHandler.removeByTask(currentTaskId);
        } else if (executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)) {// ˳ǩ
          taskHandler.removeByTask(currentTaskId);
          boolean canFinishOrder = true;
          // ȡ�øýڵ�δ��ɵ�����

          List orderTodoList = taskHandler.getTodoListByNode(instanceId, nodeId);
          for (int i = 0; i < orderTodoList.size(); i++) {
            CurrentTaskMeta orderTask = (CurrentTaskMeta) orderTodoList.get(i);
            if (TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN == orderTask
              .getResponsibility()) {// ������������δ��ɣ��Ͳ��ܽ���
              canFinishOrder = false;
            }
          }
          if (canFinishOrder) {
            // ɾ����ǰ�ڵ������
            taskHandler.removeByNode(instanceId, nodeId);
            // ���ɺ���˳ǩ��Ա��ǰ�ڵ������
            taskHandler.createNodeSerialTask(instanceId, nodeId, taskRealOwner,
              executor, executeTime);
          }
        } else {
          throw new WorkflowException(1215);
        }
      }
      // ����ڵ㲻�ܽ���,�Ͳ��ܴ�����������
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
   * ��ȡָ�����ڴ���������id ���ָ������û�д�����������,��Ҫ������,����Ѿ�����һ���������������ѯ֮
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
        nextNodeId, timeNow, instanceName, instanceDescription);// ����һ������������

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
   * ���ݸ������������������̣��Լ���������
   * 
   * @param executor
   * @param templateId
   * @param nodeId
   * @param instanceName
   *            TODO
   * @param instanceDescription
   *            TODO
   * @param conn
   * @return �����Ļ��ܴ�������
   * @throws WorkflowException
   */
  private int createToCollectInstanceAndTask(String executor, int templateId,
    int nodeId, String createTime, String instanceName, String instanceDescription)
    throws WorkflowException {
    /*
     * String instanceName = MSG_INSTANCE_NAME_TOCOLLECT; String
     * instanceDescription =MSG_INSTANCE_NAME_TOCOLLECT;
     */
    // a,��������
    TaskMeta task = create(templateId, instanceName, instanceDescription, executor,
      createTime);

    CurrentTask taskHandler = new CurrentTask();
    TaskExecutor taskExecutorHandler = new TaskExecutor();
    // b,���ո����ɵ���ʼ��������,��ɻ��ܽڵ�Ĵ�������
    taskHandler.removeByInsatnce(task.getInstanceId());

    int[] order = new int[1];
    order[0] = 1;
    String responsibility = "1";
    String[] executorArray = new String[1];
    executorArray[0] = executor;
    // ���û��ܽڵ��ִ������������Ĵ�������
    taskExecutorHandler.create(task.getInstanceId(), nodeId, executorArray, order,
      responsibility);

    taskHandler.createNodeTask(task.getInstanceId(), nodeId,
      Node.EXECUTORS_METHOD_SOLO, executor, createTime);
    // c,����parent_task_idΪ����������
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
      // ���ڽ��������̻����ܻ�����(redo),����Ҫ����action��ִ����
      // 20060912 zhanggh
      // Action actionHandler = new Action();
      // actionHandler.removeByInstance(instanceId, conn);
      // TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
      // ɾ����ʵ�����е�����ִ����
      // taskExecutorOrderHandler.removeByInstance(instanceId, conn);

      // ����ʵ����ǰ�ڵ����״̬
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
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        result.addAll(getJustExecutedPrecedingTaskNodeListByNavigation(templateId,
          instanceId, precedingNodeId));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      case 6: // '\006'��ڵ�
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
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        result.addAll(getJustExecutedPrecedingTaskNodeListByNavigation(templateId,
          instanceId, precedingNodeId));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        break;
      case 6: // '\006'��ڵ�
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
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      case 6: // '\006'��ڵ�
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
   * �õ����е��Ѿ�ִ�й��Ľڵ�
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
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) != 0) {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getJustExecutedPrecedingTaskNodeListByAO(templateId,
          instanceId, precedingNodeId));
        break;
      case 6: // '\006'��ڵ�
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
   * ��ȡָ���ڵ�ĺ����ڵ�List
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

    /* �Ȼ�ȡ���к������� */
    List followedLinkList = linkHandler.getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < followedLinkList.size(); i++) {
      Link link = (Link) followedLinkList.get(i);
      int followedNodeId = link.getNextNodeId();
      if (followedNodeId == -2)
        continue;// ????
      NodeMeta followedNode = nodeHandler.getNode(followedNodeId);
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, followedNodeId) == 0
          && taskHandler.getTaskNumByNode(instanceId, followedNodeId) != 0) {
          result.add(followedNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        result.addAll(getOnHandFollowedTaskNodeListByNavigation(templateId,
          instanceId, followedNodeId));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getOnHandFollowedTaskNodeListByAO(templateId, instanceId,
          followedNodeId));
        break;
      case 6: // '\006'��ڵ�
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
   * ��ȡ��֧�ڵ�����к����ڵ�List
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
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, followedNodeId) == 0
          && taskHandler.getTaskNumByNode(instanceId, followedNodeId) != 0) {
          result.add(followedNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        result.addAll(getOnHandFollowedTaskNodeListByNavigation(templateId,
          instanceId, followedNodeId));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        break;
      case 6: // '\006'��ڵ�
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
      case 2: // '\002'����ڵ�
        if (actionHandler.getActionNumByNode(instanceId, followedNodeId) == 0
          && taskHandler.getTaskNumByNode(instanceId, followedNodeId) != 0) {
          result.add(followedNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getOnHandFollowedTaskNodeListByAO(templateId, instanceId,
          followedNodeId));
        break;
      case 6: // '\006'��ڵ�
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
   * ȡ��ָ������ڵ�id�����д����ε�ǰ�����ǰ������ڵ㣬��������תʱʹ�á�
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
      if (precedingNodeId == -1) { // ��ʼ�ڵ�
        continue;
      }
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'����ڵ�
        if (taskHandler.getTaskNumByNode(instanceId, precedingNodeId) != 0) {
          // ����ڵ��ϴ��ڵ�ǰ��������ǰ������ڵ�ض�����ɣ�������������
          result.add(precedingNode);
        } else {
          result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId,
            instanceId, precedingNodeId));
        }
        break;
      case 3: // '\003'��֧�ڵ�
        result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId, instanceId,
          precedingNodeId));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getWholeExecutingPrecedingTaskNodeList(templateId, instanceId,
          precedingNodeId));
        break;
      case 6: // '\006'��ڵ�
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
   * ����ģ��ȡ��ָ����������ڵ����ڵ�����ʵ�����Ѿ�ִ�й����м������ڵ㣬 ����Ŀ��ڵ㡣���ಽ����ʱʹ�á��Ⱥ�˳��ɿ���
   * �����Ҫȡ��������ִ�е�ǰ�ýڵ㣬����prevNodeIdΪ-1
   * 
   * @param instanceId
   *            int
   * @param templateId
   *            int
   * @param toNodeId
   *            int
   * @param onlyGetLastest
   *            boolean ,ֻȡ�ոձ�ִ�е��Ǹ�
   * @param conn
   *            Connection
   * @throws WorkflowException
   * @return List
   */
  /*
   * ����getWholeExecutingPrecedingTaskNodeList�޸ģ�
   * ����getJustExecutedPrecedingTaskNodeList���ơ� by zhanggh
   */
  public List getExecutedNodeListBetween(int templateId, int instanceId, int nodeId,
    int prevNodeId, boolean onlyGetLastest) throws WorkflowException {
    List result = new ArrayList();
    Action actionHandler = new Action();
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    // �õ�nodeId����ǰ��link
    List precedingLinkList = linkHandler.getPrecedingLinkList(templateId, nodeId);
    for (int i = 0; precedingLinkList != null && i < precedingLinkList.size(); i++) {
      List tmp = new ArrayList();
      // ȡ����һ��ǰ�ýڵ��Id
      int precedingNodeId = ((Link) precedingLinkList.get(i)).getCurrentNodeId();
      if (precedingNodeId == -1) { // ��ʼ�ڵ�
        if (prevNodeId == -1) {
          result.add(new NodeMeta()); // ����һ���յ�Node����֤����ֵ��Ϊ��
        }
        continue;
      }
      NodeMeta precedingNode = nodeHandler.getNode(precedingNodeId);
      if (precedingNodeId == prevNodeId) {// Ŀ��ڵ�
        result.add(precedingNode);
        continue;
      }
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '\002'����ڵ�
        // ��Ҫ��ͬ����action ������task��Ҳ����˵�Ѿ�ִ�й�
        if (actionHandler.getActionNumByNode(instanceId, precedingNodeId) == 0)
          break;
        // } else {//��Ҫ��ͬ����Ȼ��������
        tmp = getExecutedNodeListBetween(templateId, instanceId, precedingNodeId,
          prevNodeId, onlyGetLastest);
        if (!onlyGetLastest) {
          result.addAll(tmp);
          // ֻ�е����ҵ�Ŀ��ڵ�󣬲���size>0�����������;�����Ľڵ�
          if (tmp.size() > 0) {
            // ����ڵ��ϴ��ڵ�ǰ��������ǰ������ڵ�ض�����ɡ�
            result.add(precedingNode);
          }
        } else {
          result.add(precedingNode);
        }
        break;
      case 3: // '\003'��֧�ڵ�
        result.addAll(getExecutedNodeListBetween(templateId, instanceId,
          precedingNodeId, prevNodeId, onlyGetLastest));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        result.addAll(getExecutedNodeListBetween(templateId, instanceId,
          precedingNodeId, prevNodeId, onlyGetLastest));
        break;
      case 6: // '\006'��ڵ�
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
   * ����ActionHistoryȡ�ô�ĳ���ڵ�����һ���ڵ�֮��������ύ�Ľڵ��б� ����ִ��ʱ���Ⱥ�˳��ע�ⲻ���Ǳ�Ե����
   * 
   * @param templateId
   * @param instanceId
   * @param preNodeId
   *            -1��ʾ��һ������
   * @param nodeId
   *            ǰһ������ -2��ʾ���һ������
   * @param onlyNormalAction
   *            ֻȡ�����ύ�ģ����������ա��˻صȲ���
   * @param conn
   * @return ����NodeMeta�б�
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
      // ��ActionMeta�б�ת��NodeMeta�б�
      for (int i = 0; i < result.size(); i++) {
        actionMeta = (ActionMeta) result.get(i);
        int tempNodeId = actionMeta.getNodeId();
        if (tempNodeId == nodeId)
          break;// nodeId�Ժ�Ķ������ǣ������-2����ʾ���е��Ѱ춼Ҫ
        // nodeMeta = new Node().getNode(tempNodeId,conn);
        if (onlyNormalAction) {
          if (isNormalCommit(actionMeta.getActionName())) {
            tempList.add(actionMeta);
          }
        } else {
          tempList.add(actionMeta);
        }
      }
      // ȥ����ʼ�ڵ�֮ǰ�Ľڵ�
      for (int j = 0; j < tempList.size(); j++) {
        if (preNodeId == -1
          || preNodeId == ((ActionMeta) tempList.get(j)).getNodeId())
          isFound = true;// preNodeId��ǰ�Ķ�������
        if (isFound) {
          resultList.add(tempList.get(j));
        }
      }
    }
    return resultList;
  }

  /**
   * ��List�еĽڵ㰴ִ��ʱ��˳����������
   * 
   * @param lstSource
   *            ���ڵ�Ԫ����NodeMeta
   * @param ���ص�List��Ԫ����NodeMeta��������һ��Ԫ��
   */
  private List getExecutedNodesOrderByTime(List lstSource, int instanceId,
    boolean onlyGetLastest) throws WorkflowException {
    List lstResult = new ArrayList();
    ActionMeta actionMeta = null;
    NodeMeta nodeMeta = null;
    if (null != lstSource) {
      // ȡ�ø�ʵ�����е�action��¼,ȡ�õ��ǰ�ʱ������
      List actions = new ActionHistory().getActionListByInstance(instanceId, 1);
      if (actions != null) {
        for (int i = actions.size() - 1; i > -1; i--) {
          // ��ActionMeta�б�ת��NodeMeta�б�
          actionMeta = (ActionMeta) actions.get(i);
          int tempNodeId = actionMeta.getNodeId();
          nodeMeta = new Node().getNode(tempNodeId);
          if (lstSource.indexOf(nodeMeta) > -1 && // ��Դ�д��ڣ������������ύ
            isNormalCommit(actionMeta.getActionName())) {
            if (-1 == lstResult.indexOf(nodeMeta)) {// ��lstResult����û�У�����оͲ����
              lstResult.add(nodeMeta);
              // ���ֻϣ���õ��ո�ִ�е�һ���ڵ㣬�����Ѿ��У���ɾ��
              if (onlyGetLastest) {
                break;
              }
            }
          }
        }
        if (lstSource.indexOf(new NodeMeta()) > -1) {// ���⴦���NodeMeta
          lstResult.add(new NodeMeta());
        }
      }
    }
    return lstResult;
  }

  private boolean isNormalCommit(String strActionName) {

    /** ActionMeta/ACTION_TYPE_FORWARD_TASK = //"forward_task";//�ύ */
    boolean isNormalCommit = !(ActionMeta.ACTION_TYPE_AUTHORIZE_TASK
      .equals(strActionName)) // "authorize_task";//��Ȩ
      && !(ActionMeta.ACTION_TYPE_CALLBACK_FLOW.equals(strActionName)) // "callback_flow";//����
      && !(ActionMeta.ACTION_TYPE_GIVEBACK_FLOW.equals(strActionName)) // "giveback_flow";//����һ��
      && !(ActionMeta.ACTION_TYPE_TRANSFER_FLOW.equals(strActionName)) // "transfer_flow";//������ת
      && !(ActionMeta.ACTION_TYPE_UNTREAD_FLOW.equals(strActionName)) // "untread_flow";//���˶ಽ.zhanggh
      && !(ActionMeta.ACTION_TYPE_ACTIVATE_INSTANCE.equals(strActionName)) // "activate_instance";//����
      && !(ActionMeta.ACTION_TYPE_DEACTIVATE_INSTANCE.equals(strActionName)) // "deactivate_instance";//����
      && !(ActionMeta.ACTION_TYPE_INTERRUPT_INSTANCE.equals(strActionName)) // "interrupt_instance";//��ֹ
      && !(ActionMeta.ACTION_TYPE_RESTART_INSTANCE.equals(strActionName)); // "restart_instance";//����
    return isNormalCommit;
  }

  /**
   * ���ص��Ǹýڵ���Link���б�����ݱ���ֵ����ƥ�䣬ֻ����Щ���ջᱻִ�е�����
   * �ڵ�ǰ��Link�Լ�����ǰ��Link�ᱻ��ӽ�ȥ����֧�ڵ�ǰ��Link���ᱻ���
   */
  /* ���Ĳ����б�by zhanggh */
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
      // �����һ�ڵ��ǽ����ڵ㣬��Ӹ�Link��break
      int followedNodeId = ((Link) nodeLinkList.get(i)).getNextNodeId();
      if (followedNodeId == -2) {
        // Ԫ����Link
        result.add(nodeLinkList.get(i));
        break;
      }
      // ��Link����һ���ڵ�
      NodeMeta followedNode = nodeHandler.getNode(followedNodeId);
      // ��һ�ڵ������
      switch (Integer.parseInt(followedNode.getType())) {
      case 2: // '\002'����ڵ�
        result.add(nodeLinkList.get(i));
        break;
      case 3: // '\003'��֧�ڵ㡣�ڷ�֧�ڵ��ϲ�ͣ��
        result.addAll(getRightFollowedTaskLinkListByNavigation(templateId,
          followedNodeId, valueList));
        break;
      case 4: // '\004'�¼������ڵ�
        break;
      case 5: // '\005'��ڵ�
        if (canPassAndNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
        }
        break;

      case 6: // '\006'��ڵ�
        CurrentTask taskHandler = new CurrentTask();
        TaskExecutor taskExecutorOrderHandler = new TaskExecutor();
        // TCJLODO:canPassOrNode����������
        if (canPassOrNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
          // �Ѿ�����ͨ���ˣ������ľͲ���ִ���ˡ�ɾ��taskExecutorOrder ��currentTask
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
   * ���ݱ���ֵ��ȡ�÷���������Link
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
    // cuiliguo 2006.06.09 ���ص��б������ӽ����ڵ�ǰ������ followedNodeLink2 ��
    Link followedNodeLink2 = null;
    String followedNodeType = null;
    Node nodeHandler = new Node();
    Link linkHandler = new Link();
    do {
      // ���ݱ���ֵ��������ʵ�Link
      followedNodeLink2 = followedNodeLink;
      followedNodeLink = linkHandler.getRIFNavigationLink(templateId,
        followedNodeId, valueList);
      if (followedNodeLink == null)
        throw new WorkflowException(1225);
      // �ݹ�
      followedNodeId = followedNodeLink.getNextNodeId();
      if (followedNodeId == -2)
        break;
      else
        followedNodeLink2 = null;
      followedNodeType = nodeHandler.getNode(followedNodeId).getType();

      // �����һ�ڵ���Ȼ�Ƿ�֧(·��)�ڵ�(Nav)����������Link
      // ֱ����һ�ڵ��Ƿ�֧�ڵ���������ͣ������ǽ����ڵ�
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
    // ȡ�����к��Link
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
      case 2: // '2'����ڵ�
        result.add(followedLink);
        break;
      case 3: // '3'��֧�ڵ�
      case 4: // '4'�¼������ڵ�
      case 5: // '5'����
        // �ݹ�
        if (canPassAndNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
        }
        break;
      case 6: // '6'��ڵ�
        if (canPassOrNode(templateId, instanceId, followedNodeId)) {
          result.addAll(getRightFollowedTaskLinkListByAO(templateId, instanceId,
            followedNodeId));
        }
        break;
      default:
        throw new WorkflowException(2000, "���ô���");
      }
    }

    return result;
  }

  /*
   * �ж�And�ڵ������Ƿ����� TODO��������
   */
  private boolean canPassAndNode(int templateId, int instanceId, int nodeId)
    throws WorkflowException {
    boolean result = true;

    Node nodeHandler = new Node();
    // ȡ������ǰ���ڵ�
    List precedingNodeList = nodeHandler.getPrecedingNodeList(templateId, nodeId);
    for (int i = 0; i < precedingNodeList.size(); i++) {
      NodeMeta precedingNode = (NodeMeta) precedingNodeList.get(i);
      int precedingNodeId = precedingNode.getId();
      if (precedingNodeId == -1)
        continue;
      // ǰ���ڵ������
      switch (Integer.parseInt(precedingNode.getType())) {
      case 2: // '2'����ڵ�
        Action action = new Action();
        CurrentTask currentTask = new CurrentTask();
        // //��Or�ڵ�����Ĺؼ���Action��ĿΪ0 ���߻���Taskδִ����ɣ�����ͨ��
        // N��ǰ���ڵ㣬ֻҪ��һ��δ��ɣ���Ͳ���ͨ��
        // �޸ġ���Ϊ�ڵ���ɸ��������Դ���
        // TCJLODO��And�ڵ�������Ҳ�����⣬Ӧ��������ǰ�������
        if (action.getActionNumByNode(instanceId, precedingNodeId) <= 0
          || currentTask.getMainTaskNumByNode(instanceId, precedingNodeId) != 0)
          result = false;
        break;
      case 3: // '3'��֧�ڵ�
      case 4: // '4'�¼������ڵ�
      case 5: // '5'����
        result = canPassAndNode(templateId, instanceId, precedingNodeId);
        break;
      case 6: // '6'��ڵ�
        result = canPassOrNode(templateId, instanceId, precedingNodeId);
        break;
      default:
        throw new WorkflowException(2000, "���ô���");
      }
      // N��ǰ���ڵ㣬ֻҪ��һ��δ��ɣ���Ͳ���ͨ�������ü����ж���
      if (!result)
        break;
    }
    return result;
  }

  /*
   * �ж�Or�ڵ������Ƿ����� TODO:������
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
      case 2: // '2'����ڵ�
        Action action = new Action();
        CurrentTask currentTask = new CurrentTask();
        // //��AND�ڵ�����Ĺؼ�����ִ�еĴ���0��������������ִ�����
        // �޸ġ���Ϊ�ڵ���ɸ��������Դ���
        // TCJLODO��Or�ڵ�������Ҳ�����⣬�Ȳ���XOR��Ҳ����OR����And����
        if (action.getActionNumByNode(instanceId, precedingNodeId) > 0
          && currentTask.getMainTaskNumByNode(instanceId, precedingNodeId) == 0)
          result = true;
        break;
      case 3: // '3'��֧�ڵ�
      case 4: // '4'�¼������ڵ�
      case 5: // '5'��ڵ�
        result = canPassAndNode(templateId, instanceId, precedingNodeId);
        break;
      case 6: // '6'��ڵ�
        result = canPassOrNode(templateId, instanceId, precedingNodeId);
        break;
      default:
        throw new WorkflowException(2000, "���ô���");
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
   * ���ݻ������� ��ȡ�������б�
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
   * ���ݻ������� ��ȡ�������б�
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
   * ��ȡ���̾����Ļ��ܽڵ�id
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
      // �������̶����ȡ�������������
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
        // ���ڶ�����ܽڵ㣬��Ҫ��������ʵ�����˳����������Ļ��ܽڵ�
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
    // ɾ���ɵ�ִ����
    for (int i = 0; i < executors.size(); i++) {
      TaskExecutorMeta e = (TaskExecutorMeta) executors.get(i);
      newExecutors.add(e.getExecutor());
      ExecuteFacade.removeTaskExecutor(e.getId());
    }
    // ��ӽ��µ�ִ����
    for (int i = 0; i < defs.length; i++) {
      if (!newExecutors.contains(defs[i])) {
        newExecutors.add(defs[i]);
        newExecutors1.add(defs[i]);
      }
    }
    // ����task_executor
    for (int i = 0; i < newExecutors.size(); i++) {
      ExecuteFacade.createTaskExecutor(instanceId, nodeId, (String) newExecutors
        .get(i), i + 1, 1);
    }
    // Ϊ׷�ӵ�ִ���˴�����������,����ֻ���Ƕ�ǩ�Ͳ�ǩ
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
    // ɾ��ִ����
    TaskExecutor te = new TaskExecutor();
    te.removeByExecutor(instanceId, nodeId, strUserId);
    // ɾ����������
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
