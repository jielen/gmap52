package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import com.kingdrive.workflow.access.CountQuery;
import com.kingdrive.workflow.access.PassBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.PassModel;
import com.kingdrive.workflow.util.Sequence;

public class Pass implements Serializable {

  private int passCountId;

  private int instanceId;

  private int nodeLinkId;

  private int currentNodeId;

  private int nextNodeId;

  public Pass() {
  }

  public void record(int instanceId, List nodeLinkList)
      throws WorkflowException {
    for (int i = 0; i < nodeLinkList.size(); i++) {
      Pass pc = new Pass();
      Link nodeLink = (Link) nodeLinkList.get(i);
      pc.setInstanceId(instanceId);
      pc.setNodeLinkId(nodeLink.getId());
      pc.setCurrentNodeId(nodeLink.getCurrentNodeId());
      pc.setNextNodeId(nodeLink.getNextNodeId());
      create(pc);
    }
  }

  private void create(Pass count) throws WorkflowException {
    try {
      PassBean bean = new PassBean();
      count.setPassCountId(Sequence.fetch(Sequence.SEQ_PASS));
      if (bean.insert(unwrap(count)) != 1)
        throw new WorkflowException(1345);
    } catch (SQLException sqle) {
      throw new WorkflowException(1346, sqle.toString());
    }
  }

  public int getPassNum(int instanceId, int nodeId)
      throws WorkflowException {
    int count = -1;
    CountQuery query = new CountQuery();
    try {
      count = query.getPassNum(instanceId, nodeId).getCount().intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(1356, sqle.toString());
    }
    return count;
  }

  public int getPassNum(int instanceId, int nodeId, int nextNodeId) throws WorkflowException {
    int count = -1;
    CountQuery query = new CountQuery();
    try {
      count = query.getPassNum(instanceId, nodeId, nextNodeId).getCount()
          .intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(1356, sqle.toString());
    }
    return count;
  }

  public void removeByInstance(int instanceId)
      throws WorkflowException {
    try {
      PassBean bean = new PassBean();
      bean.removeByInstance(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1351, sqle.toString());
    }
  }

  public void removeByNode(int instanceId, int nodeId)
      throws WorkflowException {
    try {
      PassBean bean = new PassBean();
      bean.removeByNode(instanceId, nodeId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1351, sqle.toString());
    }
  }

  private PassModel unwrap(Pass count) {
    PassModel unwrapper = new PassModel();
    if (count.passCountId != 0)
      unwrapper.setPassCountId(count.passCountId);
    if (count.instanceId != 0)
      unwrapper.setInstanceId(count.instanceId);
    if (count.nodeLinkId != 0)
      unwrapper.setNodeLinkId(count.nodeLinkId);
    if (count.currentNodeId != 0)
      unwrapper.setCurrentNodeId(count.currentNodeId);
    if (count.nextNodeId != 0)
      unwrapper.setNextNodeId(count.nextNodeId);
    return unwrapper;
  }

  public int getPassCountId() {
    return passCountId;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public int getNodeLinkId() {
    return nodeLinkId;
  }

  public int getCurrentNodeId() {
    return currentNodeId;
  }

  public int getNextNodeId() {
    return nextNodeId;
  }

  public void setPassCountId(int passCountId) {
    this.passCountId = passCountId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setNodeLinkId(int nodeLinkId) {
    this.nodeLinkId = nodeLinkId;
  }

  public void setCurrentNodeId(int currentNodeId) {
    this.currentNodeId = currentNodeId;
  }

  public void setNextNodeId(int nextNodeId) {
    this.nextNodeId = nextNodeId;
  }
}
