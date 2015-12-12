package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.LinkStateBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.LinkStateModel;
import com.kingdrive.workflow.util.Sequence;

public class LinkState implements Serializable, Comparable {

  private int id;

  private int nodeLinkId;

  private int stateId;

  private String stateName;

  private String stateValue;

  public LinkState() {
  }

  public int compareTo(Object obj) {
    return id - ((LinkState) obj).id;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof LinkState))
      return false;
    LinkState o = (LinkState) obj;
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

  public int getNodeLinkId() {
    return nodeLinkId;
  }

  public void setNodeLinkId(int nodeLinkId) {
    this.nodeLinkId = nodeLinkId;
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

  public List getStateListByLink(int nodeLinkId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      LinkStateBean query = new LinkStateBean();
      ArrayList list = query.getStateListByLink(nodeLinkId);
      LinkState state = null;
      for (int i = 0; i < list.size(); i++, result.add(state)) {
        state = wrap((LinkStateModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void create(LinkState state) throws WorkflowException {
    try {
      LinkStateBean bean = new LinkStateBean();
      state.setId(Sequence.fetch(Sequence.SEQ_LINK_STATE));
      if (bean.insert(unwrap(state)) != 1)
        throw new WorkflowException(2002);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void reset(int linkId, int[] stateId, String[] stateValue) throws WorkflowException {
    try {
      LinkStateBean bean = new LinkStateBean();
      bean.removeByLink(linkId);
      if (stateId == null)
        return;
      for (int i = 0; i < stateId.length; i++) {
        if (stateValue[i] == null || stateValue[i].equals(""))
          continue;
        LinkStateModel model = new LinkStateModel();
        model.setNodeLinkStateId(Sequence.fetch(Sequence.SEQ_LINK_STATE));
        model.setNodeLinkId(linkId);
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
      LinkStateBean bean = new LinkStateBean();
      bean.removeByTemplate(templateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByNode(int nodeId)
      throws WorkflowException {
    try {
      LinkStateBean bean = new LinkStateBean();
      bean.removeByNode(nodeId, nodeId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByLink(int linkId)
      throws WorkflowException {
    try {
      LinkStateBean bean = new LinkStateBean();
      bean.removeByLink(linkId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByState(int stateId)
      throws WorkflowException {
    try {
      LinkStateBean bean = new LinkStateBean();
      bean.removeByState(stateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private LinkState wrap(LinkStateModel model) {
    LinkState state = new LinkState();
    if (model.getNodeLinkStateId() != null)
      state.setId(model.getNodeLinkStateId().intValue());
    state.setStateValue(model.getStateValue());
    if (model.getStateId() != null)
      state.setStateId(model.getStateId().intValue());
    if (model.getNodeLinkId() != null)
      state.setNodeLinkId(model.getNodeLinkId().intValue());
    return state;
  }

  private LinkStateModel unwrap(LinkState state) {
    LinkStateModel model = new LinkStateModel();
    if (state.getId() != 0)
      model.setNodeLinkStateId(state.getId());
    if (state.getStateValue() != null)
      model.setStateValue(state.getStateValue());
    if (state.getStateId() != 0)
      model.setStateId(state.getStateId());
    if (state.getNodeLinkId() != 0)
      model.setNodeLinkId(state.getNodeLinkId());
    return model;
  }
}
