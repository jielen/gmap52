package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.DelegationBean;
import com.kingdrive.workflow.access.DelegationHistoryBean;
import com.kingdrive.workflow.access.DelegationQuery;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.DelegationMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.DelegationHistoryModel;
import com.kingdrive.workflow.model.DelegationInfo;
import com.kingdrive.workflow.model.DelegationModel;
import com.kingdrive.workflow.util.DateTime;
import com.kingdrive.workflow.util.Sequence;

public class Delegation implements Serializable {

  public Delegation() {
  }

  private void create(DelegationMeta meta)
      throws WorkflowException {
    try {
      DelegationBean bean = new DelegationBean();
      meta.setId(Sequence.fetch(Sequence.SEQ_DELEGATION));
      meta.setDelegationTime(DateTime.getSysTime());
      if (bean.insert(unwrap(meta)) != 1)
        throw new WorkflowException(2051);
      addHistory(meta);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void delegate(DelegationMeta meta)
      throws WorkflowException {
    if (meta.getSender().equalsIgnoreCase(meta.getReceiver()))
      throw new WorkflowException(2056);
    if (meta.getStartTime().compareTo(meta.getEndTime()) > 0)
      throw new WorkflowException(2057);
    if (meta.getOwner().equalsIgnoreCase(meta.getReceiver()))
      throw new WorkflowException(2062);
    if (meta.getOwner().equalsIgnoreCase(meta.getSender())) {
      create(meta);
    } else {
      DelegationMeta last = getDelegation(meta.getParentId());
      if (last == null
          || last.getStartTime().compareTo(meta.getStartTime()) <= 0
          && last.getEndTime().compareTo(meta.getEndTime()) >= 0) {
        create(meta);
      } else {
        throw new WorkflowException(2059);
      }
    }
  }

  private void delete(int delegationId)
      throws WorkflowException {
    try {
      DelegationBean bean = new DelegationBean();
      bean.delete(delegationId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public List getDelegationListByReceiver(String receiver)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findByReceiver(receiver, DateTime
          .getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getDelegationListByReceiver(String receiver, String templateType) throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findByReceiver(receiver, templateType,
          DateTime.getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getDelegationListByReceiver(int nodeId, String receiver) throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findByReceiver(nodeId, receiver, DateTime
          .getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public DelegationMeta getDelegation(int id)
      throws WorkflowException {
    DelegationMeta result = null;
    try {
      DelegationQuery query = new DelegationQuery();
      result = wrap(query.getDelegation(id));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getDelegationListBySender(String sender)
      throws WorkflowException {
    List result = new ArrayList();

    DelegationQuery query = new DelegationQuery();
    try {
      ArrayList list = query.findBySender(sender, DateTime.getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getDelegationListBySender(int nodeId, String sender) throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findBySender(nodeId, sender, DateTime
          .getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  private List getDelegationListByParent(int parentId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findNext(parentId, DateTime.getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void cancel(int delegationId)
      throws WorkflowException {
    DelegationMeta dg = getDelegation(delegationId);
    if (dg == null)
      return;
    delete(dg.getId());
    List next = getDelegationListByParent(dg.getId());
    for (int i = 0; i < next.size(); i++) {
      DelegationMeta nextDg = (DelegationMeta) next.get(i);
      cancel(nextDg.getId());
    }
  }

  public List getDelegationListByOwner(int nodeId, String owner)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findByOwner(nodeId, owner, DateTime
          .getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getDelegationListByOwner(String owner)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      DelegationQuery query = new DelegationQuery();
      ArrayList list = query.findByOwner(owner, DateTime.getSysTime());
      DelegationMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((DelegationInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  /**
   * 取得可委托给他人的代理与他人委托过来的代理
   * 
   * @param staffId
   *          int
   * @throws WorkflowException
   * @return List
   */
  public List getDelegationList(String executor)
      throws WorkflowException {
    List result = new ArrayList();

    // 取得可执行任务的节点，可可委托此节点上的任务给其他人。
    Node nodeHandler = new Node();
    List nodeList = nodeHandler.getTaskNodeListByExecutor(executor);
    for (int i = 0; i < nodeList.size(); i++) {
      NodeMeta node = (NodeMeta) nodeList.get(i);
      DelegationMeta meta = new DelegationMeta();
      meta.setNodeId(node.getId());
      meta.setTemplateId(node.getTemplateId());
      meta.setTemplateName(node.getTemplateName());
      meta.setNodeName(node.getName());
      meta.setOwner(executor);
      result.add(meta);
    }

    // 取得其他人委托过来的节点。
    List delegations = getDelegationListByReceiver(executor);
    result.addAll(delegations);
    return result;
  }

  public List getDelegationList(String executor, String templateType) throws WorkflowException {
    List result = new ArrayList();

    // 取得可执行任务的节点，可可委托此节点上的任务给其他人。
    Node nodeHandler = new Node();
    List nodeList = nodeHandler.getTaskNodeListByExecutor(executor,
        templateType);
    for (int i = 0; i < nodeList.size(); i++) {
      NodeMeta node = (NodeMeta) nodeList.get(i);
      DelegationMeta meta = new DelegationMeta();
      meta.setNodeId(node.getId());
      meta.setTemplateId(node.getTemplateId());
      meta.setTemplateName(node.getTemplateName());
      meta.setNodeName(node.getName());
      meta.setOwner(executor);
      result.add(meta);
    }

    // 取得其他人委托过来的节点。
    List delegations = getDelegationListByReceiver(executor, templateType);
    result.addAll(delegations);
    return result;
  }

  public List getCurrentTask(List tasks)
      throws WorkflowException {
    List list = new ArrayList();
    if (tasks == null || tasks.size() == 0)
      return list;
    for (int i = 0; i < tasks.size(); i++) {
      CurrentTaskMeta task = (CurrentTaskMeta) tasks.get(i);
      if (task.getIdentity() != CurrentTaskMeta.TASK_IDENTITY_NORMAL) {
        list.add(task);
        continue;
      }
      List dgs = getDelegationListByOwner(task.getNodeId(), task.getOwner());
      if (dgs.size() == 0) {
        list.add(task);
        continue;
      }
      task.setIdentity(CurrentTaskMeta.TASK_IDENTITY_DELEGATED);
      list.add(task);
      for (int j = 0; j < dgs.size(); j++) {
        CurrentTaskMeta temp = new CurrentTaskMeta();
        DelegationMeta delegation = (DelegationMeta) dgs.get(j);
        temp.setInstanceId(task.getInstanceId());
        temp.setNodeId(task.getNodeId());
        temp.setExecutor(delegation.getReceiver());
        temp.setIdentity(CurrentTaskMeta.TASK_IDENTITY_DELEGATING);
        temp.setOwner(task.getOwner());
        temp.setCreator(task.getCreator());
        temp.setCreateTime(task.getCreateTime());
        temp.setLimitExecuteTime(task.getLimitExecuteTime());
        temp.setResponsibility(task.getResponsibility());
        list.add(temp);
      }
    }

    return list;
  }

  public void removeOutOfDate() throws WorkflowException {
    try {
      DelegationBean bean = new DelegationBean();
      bean.removeOutOfDate(DateTime.getSysTime());
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private void addHistory(DelegationMeta meta)
      throws WorkflowException {
    DelegationHistoryBean bean = new DelegationHistoryBean();
    meta.setId(Sequence.fetch(Sequence.SEQ_DELEGATION_HISTORY));
    try {
      if (bean.insert(unwrapHistory(meta)) != 1)
        throw new WorkflowException(2050);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private DelegationMeta wrap(DelegationInfo model) {
    DelegationMeta wrapper = new DelegationMeta();
    if (model.getDelegationId() != null)
      wrapper.setId(model.getDelegationId().intValue());
    wrapper.setDescription(model.getDescription());
    if (model.getTemplateId() != null)
      wrapper.setTemplateId(model.getTemplateId().intValue());
    wrapper.setTemplateName(model.getTemplateName());
    if (model.getNodeId() != null)
      wrapper.setNodeId(model.getNodeId().intValue());
    wrapper.setNodeName(model.getNodeName());
    wrapper.setSender(model.getSender());
    wrapper.setSenderName(model.getSenderName());
    wrapper.setOwner(model.getOwner());
    wrapper.setOwnerName(model.getOwnerName());
    wrapper.setReceiver(model.getReceiver());
    wrapper.setReceiverName(model.getReceiverName());
    if (model.getParentId() != null)
      wrapper.setParentId(model.getParentId().intValue());
    wrapper.setDelegationTime(model.getDelegationTime());
    wrapper.setStartTime(model.getStartTime());
    wrapper.setEndTime(model.getEndTime());
    return wrapper;
  }

  private DelegationModel unwrap(DelegationMeta meta) {
    DelegationModel model = new DelegationModel();
    if (meta.getId() != 0)
      model.setDelegationId(meta.getId());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    if (meta.getTemplateId() != 0)
      model.setTemplateId(meta.getTemplateId());
    if (meta.getNodeId() != 0)
      model.setNodeId(meta.getNodeId());
    if (meta.getSender() != null)
      model.setSender(meta.getSender());
    if (meta.getOwner() != null)
      model.setOwner(meta.getOwner());
    if (meta.getReceiver() != null)
      model.setReceiver(meta.getReceiver());
    if (meta.getParentId() != 0)
      model.setParentId(meta.getParentId());
    if (meta.getDelegationTime() != null)
      model.setDelegationTime(meta.getDelegationTime());
    if (meta.getStartTime() != null)
      model.setStartTime(meta.getStartTime());
    if (meta.getEndTime() != null)
      model.setEndTime(meta.getEndTime());
    return model;
  }

  private DelegationHistoryModel unwrapHistory(DelegationMeta meta) {
    DelegationHistoryModel model = new DelegationHistoryModel();
    if (meta.getId() != 0)
      model.setDelegationHistoryId(meta.getId());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    if (meta.getTemplateId() != 0)
      model.setTemplateId(meta.getTemplateId());
    if (meta.getNodeId() != 0)
      model.setNodeId(meta.getNodeId());
    if (meta.getSender() != null)
      model.setSender(meta.getSender());
    if (meta.getOwner() != null)
      model.setOwner(meta.getOwner());
    if (meta.getReceiver() != null)
      model.setReceiver(meta.getReceiver());
    if (meta.getParentId() != 0)
      model.setParentId(meta.getParentId());
    if (meta.getDelegationTime() != null)
      model.setDelegationTime(meta.getDelegationTime());
    if (meta.getStartTime() != null)
      model.setStartTime(meta.getStartTime());
    if (meta.getEndTime() != null)
      model.setEndTime(meta.getEndTime());
    return model;
  }

}
