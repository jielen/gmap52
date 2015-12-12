package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.kingdrive.framework.util.Debug;
import com.kingdrive.workflow.access.CountQuery;
import com.kingdrive.workflow.access.TaskExecutorBean;
import com.kingdrive.workflow.access.TaskExecutorQuery;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TaskExecutorMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.TaskExecutorInfo;
import com.kingdrive.workflow.model.TaskExecutorModel;
import com.kingdrive.workflow.util.Sequence;

public class TaskExecutor implements Serializable {

  public TaskExecutor() {
  }

  public void create(int instanceId, int nodeId, String[] executor,
      int[] responsibility, String authorizer)
      throws WorkflowException {
    int order = getExecutor(instanceId, nodeId, authorizer)
        .getExecutorOrder();
    for (int i = 0; i < responsibility.length; i++) {
      create(instanceId, nodeId, executor[i], order, responsibility[i]);
    }
  }

  public void create(int instanceId, int nodeId, String executor, int order,
      int responsibility) throws WorkflowException {
    try {
      TaskExecutorBean dbob = new TaskExecutorBean();
      TaskExecutorModel model = new TaskExecutorModel();
      model.setTaskExecutorOrderId(Sequence.fetch(Sequence.SEQ_TASK_EXECUTOR));
      model.setInstanceId(instanceId);
      model.setNodeId(nodeId);
      model.setExecutor(executor);
      model.setExecutorOrder(order);
      model.setResponsibility(responsibility);
      if (dbob.insert(model) != 1)
        throw new WorkflowException(1360);
    } catch (SQLException sqle) {
      throw new WorkflowException(1361, sqle.toString());
    }
  }

  public void create(TaskExecutorMeta order)
      throws WorkflowException {
    try {
      TaskExecutorBean dbob = new TaskExecutorBean();
      order.setId(Sequence.fetch(Sequence.SEQ_TASK_EXECUTOR));
      if (dbob.insert(unwrap(order)) != 1)
        throw new WorkflowException(1360);
    } catch (SQLException sqle) {
      throw new WorkflowException(1361, sqle.toString());
    }
  }

  public void removeByNode(int templateId, int instanceId, int fromNodeId,
      int toNodeId) throws WorkflowException {
    Node nodeHandler = new Node();
    Set betweenNodeSet = nodeHandler.getNodeListBetween(templateId, fromNodeId,
        toNodeId);
    List betweenNodeList = new ArrayList();
    betweenNodeList.addAll(betweenNodeSet);
    for (int j = 0; j < betweenNodeList.size(); j++) {
      NodeMeta tempNode = (NodeMeta) betweenNodeList.get(j);
      removeByNode(instanceId, tempNode.getId());
    }

  }

  public void removeByNode(int instanceId, int nodeId)
      throws WorkflowException {
    try {
      TaskExecutorBean bean = new TaskExecutorBean();
      bean.removeByNode(instanceId, nodeId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1391, sqle.toString());
    }
  }

  public void removeByInstance(int instanceId)
      throws WorkflowException {
    try {
      TaskExecutorBean bean = new TaskExecutorBean();
      bean.removeByInstance(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1391, sqle.toString());
    }
  }

  public TaskExecutorMeta getExecutor(int instanceId, int nodeId,
      String executor) throws WorkflowException {
    TaskExecutorMeta result = new TaskExecutorMeta();
    try {
      TaskExecutorQuery query = new TaskExecutorQuery();
      result = wrap(query.getExecutor(instanceId, nodeId, executor));
    } catch (SQLException sqle) {
      throw new WorkflowException(1396, sqle.toString());
    }

    return result;
  }

  public void removeByExecutor(int instanceId, int nodeId, String executor) throws WorkflowException {
    try {
      TaskExecutorBean bean = new TaskExecutorBean();
      if (getSameOrderNum(instanceId, nodeId, executor) <= 1) {
        reset(instanceId, nodeId, executor);
      }
      bean.removeByExecutor(instanceId, nodeId, executor);
    } catch (SQLException sqle) {
      throw new WorkflowException(1371, sqle.toString());
    }
  }

  private void reset(int instanceId, int nodeId, String executor) throws WorkflowException {
    try {
      TaskExecutorBean bean = new TaskExecutorBean();
      bean.resetOrderByExecutor(instanceId, nodeId, executor);
    } catch (SQLException sqle) {
      throw new WorkflowException(1411, sqle.toString());
    }
  }

  private int getSameOrderNum(int instanceId, int nodeId, String executor) throws WorkflowException {
    int result = 0;
    try {
      CountQuery query = new CountQuery();
      result = query
          .getSameOrderExecutorNum(instanceId, nodeId, executor)
          .getCount().intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(1371, sqle.toString());
    }
    return result;
  }

  public void remove(int taskExecutorOrderId)
      throws WorkflowException {
    TaskExecutorBean bean = new TaskExecutorBean();
    try {
      bean.delete(taskExecutorOrderId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1401, sqle.toString());
    }
  }

  public void reset(int taskExecutorOrderId[], int newOrder[])
      throws WorkflowException {
    try {
      TaskExecutorBean bean = new TaskExecutorBean();
      for (int i = 0; i < taskExecutorOrderId.length; i++) {
        Debug.println((new StringBuffer(String.valueOf(i))).append(".").append(
            taskExecutorOrderId[i]).append(",").append(newOrder[i]));
        bean.updateOrder(newOrder[i], taskExecutorOrderId[i]);
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1406, sqle.toString());
    }
  }

  public int getExecutorNumByNode(int instanceId, int nodeId)
      throws WorkflowException {
    int result = -1;

    try {
      CountQuery query = new CountQuery();
      result = query.getTaskExecutorNum(instanceId, nodeId).getCount()
          .intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(1371, sqle.toString());
    }
    return result;
  }

  public int getMainExecutorNumByNode(int instanceId, int nodeId) throws WorkflowException {
    int result = -1;

    try {
      CountQuery query = new CountQuery();
      result = query.getMainTaskExecutorNum(instanceId, nodeId)
          .getCount().intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(1371, sqle.toString());
    }
    return result;
  }

  public List getFollowedExecutorList(int instanceId, int nodeId,
      String executor) throws WorkflowException {
    List result = new ArrayList();
    try {
      TaskExecutorQuery query = new TaskExecutorQuery();
      ArrayList list = query.getFollowedExecutorList(instanceId, nodeId,
          executor);
      TaskExecutorMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TaskExecutorInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1161, sqle.toString());
    }
    return result;
  }

  public List getForemostExecutorList(int instanceId, int nodeId) throws WorkflowException {
    List result = new ArrayList();

    try {
      TaskExecutorQuery query = new TaskExecutorQuery();
      ArrayList list = query.getForemostExecutorList(instanceId, nodeId);
      TaskExecutorMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TaskExecutorInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1163, sqle.toString());
    }
    return result;
  }

  public List getExecutorList(int instanceId, int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      TaskExecutorQuery query = new TaskExecutorQuery();
      ArrayList list = query.getExecutorList(instanceId, nodeId);
      TaskExecutorMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TaskExecutorInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1396, sqle.toString());
    }
    return result;
  }

  public List getExecutorList(int instanceId, int nodeId, String realOwner,
      Connection conn) throws WorkflowException {
    List result = new ArrayList();

    try {
      TaskExecutorQuery query = new TaskExecutorQuery();
      ArrayList list = query.getExecutorList(instanceId, nodeId,
          realOwner);
      TaskExecutorMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TaskExecutorInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1396, sqle.toString());
    }
    return result;
  }

  /**
   * 根据流程定义所定义的任务执行者，设置为运行时该节点的执行者
   * 
   * @param instanceId
   * @param nodeId
   * @param executeMethod
   * @param conn
   * @throws WorkflowException
   */
  public void set(int instanceId, int nodeId, String executeMethod) throws WorkflowException {
    Executor handler = new Executor();
    handler.resetOrder(nodeId);
    List executorList = handler.getExecutorListByOrder(nodeId);
    removeByNode(instanceId,nodeId);//copy之前先删除，避免重复
    copy(instanceId, executorList);
  }

  /**
   * 为指定工作流实例创建指定执行者
   * 
   * @param instanceId
   * @param executorList
   * @param conn
   * @throws WorkflowException
   */
  private void copy(int instanceId, List executorList)
      throws WorkflowException {
    for (int i = 0; i < executorList.size(); i++) {
      Executor eo = (Executor) executorList.get(i);
      TaskExecutorMeta teo = new TaskExecutorMeta();
      teo.setInstanceId(instanceId);
      teo.setNodeId(eo.getNodeId());
      teo.setExecutor(eo.getExecutor());
      teo.setExecutorOrder(eo.getExecutorOrder());
      teo.setResponsibility(eo.getResponsibility());
      create(teo);
    }

  }

  private TaskExecutorMeta wrap(TaskExecutorInfo model) {
    TaskExecutorMeta wrapper = new TaskExecutorMeta();
    if (model.getTaskExecutorOrderId() != null)
      wrapper.setId(model.getTaskExecutorOrderId().intValue());
    if (model.getInstanceId() != null)
      wrapper.setInstanceId(model.getInstanceId().intValue());
    wrapper.setInstanceName(model.getInstanceName());
    wrapper.setNodeName(model.getNodeName());
    if (model.getNodeId() != null)
      wrapper.setNodeId(model.getNodeId().intValue());
    wrapper.setExecutor(model.getExecutor());
    wrapper.setExecutorName(model.getExecutorName());
    if (model.getExecutorOrder() != null)
      wrapper.setExecutorOrder(model.getExecutorOrder().intValue());
    wrapper.setExecutorsMethod(model.getExecutorsMethod());
    if (model.getResponsibility() != null)
      wrapper.setResponsibility(model.getResponsibility().intValue());
    return wrapper;
  }

  private TaskExecutorModel unwrap(TaskExecutorMeta meta) {
    TaskExecutorModel unwrapper = new TaskExecutorModel();
    if (meta.getId() != 0)
      unwrapper.setTaskExecutorOrderId(meta.getId());
    if (meta.getInstanceId() != 0)
      unwrapper.setInstanceId(meta.getInstanceId());
    if (meta.getNodeId() != 0)
      unwrapper.setNodeId(meta.getNodeId());
    if (meta.getExecutor() != null)
      unwrapper.setExecutor(meta.getExecutor());
    if (meta.getExecutorOrder() != 0)
      unwrapper.setExecutorOrder(meta.getExecutorOrder());
    if (meta.getResponsibility() != 0)
      unwrapper.setResponsibility(meta.getResponsibility());
    return unwrapper;
  }
}
