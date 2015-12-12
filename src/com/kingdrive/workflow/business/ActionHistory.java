package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.ActionHistoryBean;
import com.kingdrive.workflow.access.ActionHistoryQuery;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.ActionHistoryInfo;
import com.kingdrive.workflow.model.ActionHistoryModel;
import com.kingdrive.workflow.util.Sequence;

public class ActionHistory implements Serializable {

  public ActionHistory() {
  }

  public void record(int instanceId, int currentNodeId, String action,
    String executor, String executeTime, String comment, String owner,
    String limitExecuteTime) throws WorkflowException {
    ActionMeta actionHistory = new ActionMeta();
    actionHistory.setInstanceId(instanceId);
    actionHistory.setNodeId(currentNodeId);
    actionHistory.setActionName(action);
    actionHistory.setExecutor(executor);
    actionHistory.setExecuteTime(executeTime);
    actionHistory.setDescription(comment);
    actionHistory.setOwner(owner);
    actionHistory.setLimitExecuteTime(limitExecuteTime);
    create(actionHistory);
  }

  private void create(ActionMeta h) throws WorkflowException {
    ActionHistoryBean bean = new ActionHistoryBean();
    h.setId(Sequence.fetch(Sequence.SEQ_ACTION_HISTORY));
    ActionHistoryModel model = unwrap(h);
    try {
      if (bean.insert(model) != 1)
        throw new WorkflowException(1150);
    } catch (SQLException sqle) {
      throw new WorkflowException(1151, sqle.toString());
    }
  }

  public List getActionList(String startTime, String endTime)
    throws WorkflowException {
    List result = new ArrayList();

    try {
      ActionHistoryQuery query = new ActionHistoryQuery();
      ArrayList list = query.getActionList(startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1190, sqle.toString());
    }
    return result;
  }

  public List getActionListByTemplate(int templateId, String startTime,
    String endTime) throws WorkflowException {
    List result = new ArrayList();

    try {
      ActionHistoryQuery query = new ActionHistoryQuery();
      ArrayList list = query.getActionListByTemplate(templateId, startTime,
        endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1190, sqle.toString());
    }
    return result;
  }

  public List getActionListByTemplate(String templateType, String startTime,
    String endTime) throws WorkflowException {
    List result = new ArrayList();

    try {
      ActionHistoryQuery query = new ActionHistoryQuery();
      ArrayList list = query.getActionListByTemplate(templateType, startTime,
        endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1190, sqle.toString());
    }
    return result;
  }

  public List getActionListByInstance(int instanceId, int isValid)throws WorkflowException {
    List result = new ArrayList();

    try {
      ActionHistoryQuery query = new ActionHistoryQuery();
      ArrayList list = query.getActionListByInstance(instanceId,isValid);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1190, sqle.toString());
    }
    return result;
  }

  public List getActionListByNode(int nodeId, String startTime, String endTime) throws WorkflowException {
    List result = new ArrayList();

    try {
      ActionHistoryQuery query = new ActionHistoryQuery();
      ArrayList list = query.getActionListByNode(nodeId, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1190, sqle.toString());
    }
    return result;
  }

  public List getActionListByNode(String businessType, String startTime,
    String endTime) throws WorkflowException {
    List result = new ArrayList();

    try {
      ActionHistoryQuery query = new ActionHistoryQuery();
      ArrayList list = query.getActionListByNode(businessType, startTime,
        endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1190, sqle.toString());
    }
    return result;
  }

  public List getActionListByOwner(String owner, String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwner(owner, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByOwnerAndTemplate(String owner, int templateId,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndTemplate(owner,
        templateId, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByOwnerAndTemplate(String owner, String templateType,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndTemplate(owner,
        templateType, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByOwnerAndInstance(String owner, int instanceId,
    int isValid) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query
        .getActionListByOwnerAndInstance(owner, instanceId, isValid);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByOwnerAndNode(String owner, int nodeId,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndNode(owner, nodeId,
        startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByOwnerAndNode(String owner, String businessType,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndNode(owner, businessType,
        startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByExecutor(String executor, String startTime,
    String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByExecutor(executor, startTime,
        endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionInstListByExecutor(String executor, String startTime,
    String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      result = query.getActionInstListByExecutor(executor, startTime, endTime);
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }
  public List getInvalidDoneInstListByExecutor(String executor)
     throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      result = query.getInvalidDoneInstListByExecutor(executor);
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }
  public List getActionCompoListByExecutor(String executor) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      result = query.getActionCompoListByExecutor(executor);
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByExecutorAndTemplate(String executor, int templateId,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByExecutorAndTemplate(executor,
        templateId, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByExecutorAndTemplate(String executor,
    String templateType, String startTime, String endTime)
    throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByExecutorAndTemplate(executor,
        templateType, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByExecutorAndInstance(String executor, 
    int instanceId,int isValid) throws WorkflowException{
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndInstance(executor,
        instanceId, isValid);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }
  public List getActionListByInstanceAndNode(int instanceId,int nodeId, 
    int isValid) throws WorkflowException{
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByInstanceAndNode(
        instanceId,nodeId, isValid);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }
  public List getActionListByExecutorAndNode(String executor, int nodeId,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndNode(executor, nodeId,
        startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public List getActionListByExecutorAndNode(String executor, String businessType,
    String startTime, String endTime) throws WorkflowException {
    ActionHistoryQuery query = new ActionHistoryQuery();
    List result = new ArrayList();
    try {
      ArrayList list = query.getActionListByOwnerAndNode(executor,
        businessType, startTime, endTime);
      ActionMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((ActionHistoryInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1140, sqle.toString());
    }
    return result;
  }

  public void removeByInstance(int instanceId)
    throws WorkflowException {
    ActionHistoryBean bean = new ActionHistoryBean();
    try {
      bean.removeByInstance(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1181, sqle.toString());
    }
  }

  private ActionHistoryModel unwrap(ActionMeta meta) {
    ActionHistoryModel unwrapper = new ActionHistoryModel();
    if (meta.getId() != 0)
      unwrapper.setActionHistoryId(meta.getId());
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

  static ActionMeta wrap(ActionHistoryInfo model) {
    ActionMeta wrapper = new ActionMeta();
    if (model.getActionHistoryId() != null)
      wrapper.setId(model.getActionHistoryId().intValue());
    if (model.getInstanceId() != null)
      wrapper.setInstanceId(model.getInstanceId().intValue());
    wrapper.setInstanceName(model.getInstanceName());
    wrapper.setInstanceDescription(model.getInstanceDescription());
    if (model.getTemplateId() != null)
      wrapper.setTemplateId(model.getTemplateId().intValue());
    if (model.getParentInstanceId() != null)
      wrapper.setParentInstanceId(model.getParentInstanceId().intValue());
    wrapper.setTemplateName(model.getTemplateName());
    wrapper.setTemplateType(model.getTemplateType());
    if (model.getNodeId() != null)
      wrapper.setNodeId(model.getNodeId().intValue());
    wrapper.setNodeName(model.getNodeName());
    wrapper.setBusinessType(model.getBusinessType());
    wrapper.setActionName(model.getActionName());
    wrapper.setExecutor(model.getExecutor());
    wrapper.setExecutorName(model.getExecutorName());
    wrapper.setExecuteTime(model.getExecuteTime());
    wrapper.setDescription(model.getDescription());
    wrapper.setOwner(model.getOwner());
    wrapper.setOwnerName(model.getOwnerName());
    wrapper.setLimitExecuteTime(model.getLimitExecuteTime());
    return wrapper;
  }
}
