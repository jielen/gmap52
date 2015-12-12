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
import java.util.List;

import com.kingdrive.workflow.access.NodeStateBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.NodeStateModel;
import com.kingdrive.workflow.util.Sequence;

public class NodeState implements Serializable, Comparable {

  private int id;

  private int nodeId;

  private int stateId;

  private String stateName;

  private String stateValue;

  public NodeState() {
  }

  public int compareTo(Object obj) {
    return id - ((NodeState) obj).id;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof NodeState)) {
      return false;
    }
    NodeState o = (NodeState) obj;
    return id == o.id;
  }

  public int hashCode() {
    return id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public int getStateId() {
    return stateId;
  }

  public void setStateId(int stateId) {
    this.stateId = stateId;
  }

  public String getStateName() {
    return stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }

  public String getStateValue() {
    return stateValue;
  }

  public void setStateValue(String stateValue) {
    this.stateValue = stateValue;
  }

  public List getStateListByNode(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      NodeStateBean bean = new NodeStateBean();
      ArrayList list = bean.getStateListByNode(nodeId);
      NodeState state = null;
      for (int i = 0; i < list.size(); i++, result.add(state)) {
        state = wrap((NodeStateModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void create(NodeState state) throws WorkflowException {
    try {
      NodeStateBean bean = new NodeStateBean();
      state.setId(Sequence.fetch(Sequence.SEQ_NODE_STATE));
      if (bean.insert(unwrap(state)) != 1) {
        throw new WorkflowException(2002);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void reset(int nodeId, int[] stateId, String[] stateValue) throws WorkflowException {
    try {
      NodeStateBean bean = new NodeStateBean();
      bean.removeByNode(nodeId);
      if (stateId == null)
        return;
      for (int i = 0; i < stateId.length; i++) {
        if (stateValue[i] == null || stateValue[i].equals(""))
          continue;
        NodeStateModel model = new NodeStateModel();
        model.setNodeStateId(Sequence.fetch(Sequence.SEQ_NODE_STATE));
        model.setNodeId(nodeId);
        model.setStateId(stateId[i]);
        model.setStateValue(stateValue[i]);
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByTemplate(int templateId)
      throws WorkflowException {
    try {
      NodeStateBean bean = new NodeStateBean();
      bean.removeByTemplate(templateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByNode(int nodeId)
      throws WorkflowException {
    try {
      NodeStateBean bean = new NodeStateBean();
      bean.removeByNode(nodeId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByState(int stateId)
      throws WorkflowException {
    try {
      NodeStateBean bean = new NodeStateBean();
      bean.removeByState(stateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private NodeState wrap(NodeStateModel model) {
    NodeState state = new NodeState();
    if (model.getNodeStateId() != null)
      state.setId(model.getNodeStateId().intValue());
    state.setStateValue(model.getStateValue());
    if (model.getStateId() != null)
      state.setStateId(model.getStateId().intValue());
    if (model.getNodeId() != null)
      state.setNodeId(model.getNodeId().intValue());
    return state;
  }

  private NodeStateModel unwrap(NodeState state) {
    NodeStateModel model = new NodeStateModel();
    if (state.getId() != 0)
      model.setNodeStateId(state.getId());
    if (state.getStateValue() != null)
      model.setStateValue(state.getStateValue());
    if (state.getStateId() != 0)
      model.setStateId(state.getStateId());
    if (state.getNodeId() != 0)
      model.setNodeId(state.getNodeId());
    return model;
  }
}
