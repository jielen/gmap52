package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.ActionBean;
import com.kingdrive.workflow.access.CountQuery;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.ActionModel;
import com.kingdrive.workflow.util.Sequence;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: 动作,处理类,包括授权,流程跳转,回收等等各种处理的执行功能
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: 用友政务
 * </p>
 * 
 * @author unauthorized
 * @time unknown
 * 
 */
public class Action implements Serializable {
  public Action() {
  }

  /**
   * 记录当前Action到历史记录
   * 
   * @param instanceId
   * @param currentNodeId
   * @param actionName
   * @param executor
   * @param executeTime
   * @param comment
   * @param owner
   * @param limitExecuteTime
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  public void record(int instanceId, int currentNodeId, String actionName,
      String executor, String executeTime, String comment, String owner,
      String limitExecuteTime) throws WorkflowException {
    ActionMeta action = new ActionMeta();
    action.setInstanceId(instanceId);
    action.setNodeId(currentNodeId);
    action.setActionName(actionName);
    action.setExecutor(executor);
    action.setExecuteTime(executeTime);
    action.setDescription(comment);
    action.setOwner(owner);
    action.setLimitExecuteTime(limitExecuteTime);
    create(action);

    ActionHistory history = new ActionHistory();
    history.record(instanceId, currentNodeId, actionName, executor,
        executeTime, comment, owner, limitExecuteTime);
  }

  /**
   * 创建Action
   * 
   * @param action
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  private void create(ActionMeta action)
      throws WorkflowException {
    try {
      ActionBean bean = new ActionBean();
      action.setId(Sequence.fetch(Sequence.SEQ_ACTION));
      if (bean.insert(unwrap(action)) != 1)
        throw new WorkflowException(1115);
    } catch (SQLException sqle) {
      throw new WorkflowException(1116, sqle.toString());
    }
  }

  /**
   * 根据instanceId删除Action
   * 
   * @param instanceId
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  public void removeByInstance(int instanceId)
      throws WorkflowException {
    ActionBean bean = new ActionBean();
    try {
      bean.removeByInstance(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1181, sqle.toString());
    }
  }

  /**
   * 根据Nodeid删除Action
   * 
   * @param instanceId
   * @param nodeId
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  public void removeByNode(int instanceId, int nodeId)
      throws WorkflowException {
    ActionBean bean = new ActionBean();
    try {
      bean.removeByNode(instanceId, nodeId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1181, sqle.toString());
    }
  }

  /**
   * 获取指定节点处Action数量
   * 
   * @param instanceId
   * @param nodeId
   * @param conn
   * @return
   * @throws WorkflowException
   * @throws
   * @see
   */
  // TCJLODO javadoc
  public int getActionNumByNode(int instanceId, int nodeId)
      throws WorkflowException {
    int result = 0;

    try {
      CountQuery query = new CountQuery();
      result = query.getActionNumByNode(instanceId, nodeId).getCount()
          .intValue();
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  /**
   * 根据instanceId，取得指定nodeId的运行期Action List
   * 
   * @param instanceId
   *          int
   * @param nodeId
   *          int
   * @param conn
   *          Connection
   * @throws WorkflowException
   * @return List
   */
  public List getActionListByNode(int instanceId, int nodeId, Connection conn)
      throws WorkflowException {
    List actionList = new ArrayList();
    ActionBean bean = new ActionBean();
    ActionMeta action = new ActionMeta();
    try {
      ArrayList list = bean.getActionByNode(instanceId, nodeId);
      for (int i = 0; i < list.size(); i++, actionList.add(action))
        action = wrap((ActionModel) list.get(i));
    } catch (SQLException sqle) {
      throw new WorkflowException(1176, sqle.toString());
    }
    return actionList;
  }

  /**
   * 
   * 
   * @param model
   * @return
   * @throws
   * @see
   */
  private ActionMeta wrap(ActionModel model) {
    ActionMeta wrapper = new ActionMeta();
    if (model.getActionId() != null)
      wrapper.setId(model.getActionId().intValue());
    if (model.getInstanceId() != null)
      wrapper.setInstanceId(model.getInstanceId().intValue());
    if (model.getNodeId() != null)
      wrapper.setNodeId(model.getNodeId().intValue());
    wrapper.setActionName(model.getActionName());
    if (model.getExecutor() != null)
      wrapper.setExecutor(model.getExecutor());
    wrapper.setExecuteTime(model.getExecuteTime());
    wrapper.setDescription(model.getDescription());
    wrapper.setOwner(model.getOwner());
    wrapper.setLimitExecuteTime(model.getLimitExecuteTime());

    return wrapper;
  }

  private ActionModel unwrap(ActionMeta meta) {
    ActionModel unwrapper = new ActionModel();
    if (meta.getId() != 0)
      unwrapper.setActionId(meta.getId());
    if (meta.getInstanceId() != 0)
      unwrapper.setInstanceId(meta.getInstanceId());
    if (meta.getNodeId() != 0)
      unwrapper.setNodeId(meta.getNodeId());
    if (meta.getActionName() != null)
      unwrapper.setActionName(meta.getActionName());
    if (meta.getExecutor() != null)
      unwrapper.setExecutor(meta.getExecutor());
    if (meta.getExecuteTime() != null)
      unwrapper.setExecuteTime(meta.getExecuteTime());
    if (meta.getDescription() != null)
      unwrapper.setDescription(meta.getDescription());
    if (meta.getOwner() != null)
      unwrapper.setOwner(meta.getOwner());
    if (meta.getLimitExecuteTime() != null)
      unwrapper.setLimitExecuteTime(meta.getLimitExecuteTime());
    return unwrapper;
  }
}
