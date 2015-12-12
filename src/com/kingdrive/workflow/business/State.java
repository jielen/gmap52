package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.StateBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.StateModel;
import com.kingdrive.workflow.util.Sequence;

public class State implements Serializable, Comparable {

  private int id;

  private String name;

  private String description;

  private int templateId;

  public State() {
  }

  public int compareTo(Object obj) {
    return id - ((State) obj).id;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof State))
      return false;
    State o = (State) obj;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getTemplateId() {
    return templateId;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public List getStateListByTemplate(int templateId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      StateBean bean = new StateBean();
      ArrayList list = bean.getStateListByTemplate(templateId);
      State state = null;
      for (int i = 0; i < list.size(); i++, result.add(state)) {
        state = wrap((StateModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void create(State state) throws WorkflowException {
    int templateId = state.getTemplateId();
    String name = state.getName();
    State temp = getState(templateId, name);
    if (temp != null && templateId == temp.getTemplateId() && name != null
        && name.equals(temp.getName())) {
      throw new WorkflowException(2030);
    }

    try {
      StateBean bean = new StateBean();
      state.setId(Sequence.fetch(Sequence.SEQ_STATE));
      if (bean.insert(unwrap(state)) != 1)
        throw new WorkflowException(2002);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public State getState(int stateId) throws WorkflowException {
    State result = new State();

    try {
      StateBean bean = new StateBean();
      result = wrap(bean.findByKey(stateId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void remove(int stateId) throws WorkflowException {
    LinkState linkStateHandler = new LinkState();
    linkStateHandler.removeByState(stateId);

    NodeState nodeStateHandler = new NodeState();
    nodeStateHandler.removeByState(stateId);

    try {
      StateBean bean = new StateBean();
      bean.delete(stateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update(State state) throws WorkflowException {
    State temp = getState(state.getTemplateId(), state.getName());
    if (temp.getName() != null
        && (temp.getId() != state.getId() && temp.getName().equals(
            state.getName())))
      throw new WorkflowException(2030);

    try {
      StateBean bean = new StateBean();
      if (bean.update(unwrap(state)) != 1)
        throw new WorkflowException(2004);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByTemplate(int templateId)
      throws WorkflowException {
    LinkState linkStateHandler = new LinkState();
    linkStateHandler.removeByTemplate(templateId);

    NodeState nodeStateHandler = new NodeState();
    nodeStateHandler.removeByTemplate(templateId);

    try {
      StateBean bean = new StateBean();
      bean.removeByTemplate(templateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private State getState(int templateId, String name)
      throws WorkflowException {
    State meta;

    try {
      StateBean bean = new StateBean();
      meta = wrap(bean.getState(templateId, name));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return meta;
  }

  private State wrap(StateModel model) {
    State state = new State();
    if (model.getStateId() != null)
      state.setId(model.getStateId().intValue());
    state.setName(model.getName());
    state.setDescription(model.getDescription());
    if (model.getTemplateId() != null)
      state.setTemplateId(model.getTemplateId().intValue());
    return state;
  }

  private StateModel unwrap(State state) {
    StateModel model = new StateModel();
    if (state.getId() != 0)
      model.setStateId(state.getId());
    if (state.getTemplateId() != 0)
      model.setTemplateId(state.getTemplateId());
    if (state.getName() != null)
      model.setName(state.getName());
    if (state.getDescription() != null)
      model.setDescription(state.getDescription());
    return model;
  }
}
