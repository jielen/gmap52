package com.kingdrive.workflow.business;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kingdrive.workflow.access.CountQuery;
import com.kingdrive.workflow.access.ExecutorOrderBean;
import com.kingdrive.workflow.access.ExecutorQuery;
import com.kingdrive.workflow.access.ExecutorSourceBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.CountInfo;
import com.kingdrive.workflow.model.ExecutorInfo;
import com.kingdrive.workflow.model.ExecutorOrderModel;
import com.kingdrive.workflow.model.ExecutorSourceModel;

public class Executor implements Serializable, Comparable {

  public static final int EXECUTOR_SOURCE_COMPANY = 1;

  public static final int EXECUTOR_SOURCE_ORGANIZATION = 2;

  public static final int EXECUTOR_SOURCE_ROLE = 3;

  public static final int EXECUTOR_SOURCE_POSITION = 4;

  public static final int EXECUTOR_SOURCE_STAFF = 5;

  private int nodeId;

  private String executor;

  private String executorName;

  private int executorOrder;

  private int responsibility;

  public Executor() {
  }

  public int compareTo(Object obj) {
    return executorOrder - ((Executor) obj).getExecutorOrder();
  }

  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  public String getExecutorName() {
    return executorName;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
  }

  public int getExecutorOrder() {
    return executorOrder;
  }

  public void setExecutorOrder(int executorOrder) {
    this.executorOrder = executorOrder;
  }

  public int getResponsibility() {
    return responsibility;
  }

  public void setResponsibility(int responsibility) {
    this.responsibility = responsibility;
  }

  public List getExecutorListByOrder(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      ExecutorQuery query = new ExecutorQuery();
      ArrayList list = query.getExecutorListByOrder(nodeId);
      Executor executor = null;
      for (int i = 0; i < list.size(); i++, result.add(executor)) {
        executor = wrap((ExecutorInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1366, sqle.toString());
    }
    return result;
  }

  public List getExecutorListBySource(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      ExecutorQuery query = new ExecutorQuery();
      ArrayList list = query.getExecutorListBySource(nodeId);
      Executor executor = null;
      for (int i = 0; i < list.size(); i++, result.add(executor)) {
        executor = wrap((ExecutorInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1366, sqle.toString());
    }
    return result;
  }

  public void clone(int oldNodeId, int newNodeId)
      throws WorkflowException {

    ExecutorSourceBean bean = new ExecutorSourceBean();
    try {
      List list = bean.getSourceListByNode(oldNodeId);
      for (int i = 0; i < list.size(); i++) {
        ExecutorSourceModel model = (ExecutorSourceModel) list.get(i);
        model.setNodeId(newNodeId);
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }

    ExecutorOrderBean order = new ExecutorOrderBean();
    try {
      List list = order.getOrderListByNode(oldNodeId);
      for (int i = 0; i < list.size(); i++) {
        ExecutorOrderModel model = (ExecutorOrderModel) list.get(i);
        model.setNodeId(newNodeId);
        order.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void resetSource(int nodeId, String executor[], int source,
      int responsibility) throws WorkflowException {
    ExecutorSourceBean bean = new ExecutorSourceBean();
    try {
      bean.remove(nodeId, source, responsibility);
      if (executor == null)
        return;
      for (int i = 0; i < executor.length; i++) {
        ExecutorSourceModel model = new ExecutorSourceModel();
        model.setNodeId(nodeId);
        model.setExecutor(executor[i]);
        model.setSource(source);
        model.setResponsibility(responsibility);
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void resetOrder(int nodeId, String[] executor, int[] executorOrder,
      int[] responsibility) throws WorkflowException {
    try {
      ExecutorOrderBean bean = new ExecutorOrderBean();
      bean.removeByNode(nodeId);
      if (executor == null)
        return;
      List list = new ArrayList();
      for (int i = 0; i < executor.length; i++) {
        Executor meta = new Executor();
        meta.setNodeId(nodeId);
        meta.setExecutor(executor[i]);
        meta.setExecutorOrder(executorOrder[i]);
        meta.setResponsibility(responsibility[i]);
        list.add(meta);
      }
      Collections.sort(list);
      int order = 1;
      int previous = 0;
      for (int i = 0; i < list.size(); i++) {
        Executor meta = (Executor) list.get(i);
        if (i == 0) {
          previous = meta.getExecutorOrder();
        }
        if (previous == meta.getExecutorOrder()) {
          meta.setExecutorOrder(order);
        } else {
          order += 1;
          meta.setExecutorOrder(order);
          previous = meta.getExecutorOrder();
        }
        ExecutorOrderModel model = new ExecutorOrderModel();
        model.setNodeId(meta.getNodeId());
        model.setExecutor(meta.getExecutor());
        model.setExecutorOrder(meta.getExecutorOrder());
        model.setResponsibility(meta.getResponsibility());
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private Executor wrap(ExecutorInfo model) {
    Executor meta = new Executor();
    if (model.getNodeId() != null)
      meta.nodeId = model.getNodeId().intValue();
    meta.executor = model.getExecutor();
    if (model.getExecutorOrder() != null)
      meta.executorOrder = model.getExecutorOrder().intValue();
    meta.executorName = model.getExecutorName();
    if (model.getResponsibility() != null)
      meta.responsibility = model.getResponsibility().intValue();
    return meta;
  }

  /**
   * 重置顺签执行者表记录(即wf_executor_order)。 运行时重置wf_executor_order的执行人
   * 
   * @param nodeId
   * @param conn
   * @throws WorkflowException
   */
  public void resetOrder(int nodeId) throws WorkflowException {
    List sourceExecutorList = getExecutorListBySource(nodeId);
    List orderExecutorList = getExecutorListByOrder(nodeId);

    List deletedExecutorList = getRemainder(orderExecutorList,
        sourceExecutorList);
    removeOrder(nodeId, deletedExecutorList);

    List addedExecutorList = getRemainder(sourceExecutorList, orderExecutorList);
    //两次筛选，剩下其共同部分，插入order表中
    createOrder(nodeId, addedExecutorList);
  }

  /**
   * 返回main中-sub中的执行者列表
   * 
   * @param main
   * @param sub
   * @return
   * @throws WorkflowException
   */
  private List getRemainder(List main, List sub) throws WorkflowException {
    List result = new ArrayList();

    for (int i = 0; i < main.size(); i++) {
      Executor mainExecutor = (Executor) main.get(i);

      boolean hasMainExecutor = false;
      for (int j = 0; j < sub.size(); j++) {
        Executor subExecutor = (Executor) sub.get(j);
        if (subExecutor.getNodeId() == mainExecutor.getNodeId()
            && subExecutor.getExecutor().equals(mainExecutor.getExecutor())) {
          hasMainExecutor = true;
          break;
        }
      }

      if (!hasMainExecutor) {
        result.add(mainExecutor);
      }
    }
    return result;
  }

  private void createOrder(int nodeId, List list)
      throws WorkflowException {
    if (list.size() == 0)
      return;

    int maxOrder = getMaxOrder(nodeId);

    ExecutorOrderBean bean = new ExecutorOrderBean();
    for (int i = 0; i < list.size(); i++) {
      Executor executor = (Executor) list.get(i);
      ExecutorOrderModel model = new ExecutorOrderModel();
      model.setNodeId(nodeId);
      model.setExecutor(executor.getExecutor());
      model.setExecutorOrder(maxOrder);
      model.setResponsibility(executor.getResponsibility());
      try {
        bean.insert(model);
      } catch (SQLException sqle) {
        throw new WorkflowException(2000, sqle.toString());
      }
    }
  }

  private int getMaxOrder(int nodeId) throws WorkflowException {
    int result = 1;
    try {
      CountQuery query = new CountQuery();
      CountInfo model = query.getMaxExecutorOrder(nodeId);
      if (model.getCount() != null) {
        result = model.getCount().intValue();
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1376, sqle.toString());
    }
    return result;
  }

  private void removeOrder(int nodeId, List list)
      throws WorkflowException {
    for (int i = 0; i < list.size(); i++) {
      String executor = ((Executor) list.get(i)).getExecutor();
      if (getSameOrderNum(nodeId, executor) <= 1)
        resetOrder(nodeId, executor);
      removeOrder(nodeId, executor);
    }

  }

  private void resetOrder(int nodeId, String executor)
      throws WorkflowException {
    try {
      ExecutorOrderBean bean = new ExecutorOrderBean();
      bean.updateOrder(nodeId, executor);
    } catch (SQLException sqle) {
      throw new WorkflowException(1411, sqle.toString());
    }
  }

  private void removeOrder(int nodeId, String executor)
      throws WorkflowException {
    try {
      ExecutorOrderBean bean = new ExecutorOrderBean();
      bean.remove(nodeId, executor);
    } catch (SQLException sqle) {
      throw new WorkflowException(1386, sqle.toString());
    }
  }

  private int getSameOrderNum(int nodeId, String executor)
      throws WorkflowException {
    int count = -1;
    try {
      CountQuery query = new CountQuery();
      count = query.getSameOrderExecutorNum(nodeId, executor).getCount()
          .intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(1416, sqle.toString());
    }
    return count;
  }

  public void resetOrderAsNonSerial(int nodeId)
      throws WorkflowException {
    try {
      ExecutorOrderBean bean = new ExecutorOrderBean();
      bean.setNonSerialOrder(nodeId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByTemplate(int templateId)
      throws WorkflowException {
    try {
      ExecutorSourceBean source = new ExecutorSourceBean();
      source.removeByTemplate(templateId);

      ExecutorOrderBean order = new ExecutorOrderBean();
      order.removeByTemplate(templateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByNode(int nodeId)
      throws WorkflowException {
    try {
      ExecutorSourceBean source = new ExecutorSourceBean();
      source.removeByNode(nodeId);

      ExecutorOrderBean order = new ExecutorOrderBean();
      order.removeByNode(nodeId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public String toString() {
    return "nodeId = " + nodeId + "\texecutor = " + executor + "\texecutorName"
        + executorName + "\texecutorOrder" + executorOrder + "\tresponsibility"
        + responsibility;
  }
}
