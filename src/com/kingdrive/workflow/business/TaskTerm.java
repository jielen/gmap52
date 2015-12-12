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
import java.util.Calendar;

import com.kingdrive.workflow.access.TaskTermBean;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.TaskTermModel;
import com.kingdrive.workflow.util.DateTime;

public class TaskTerm implements Serializable {

  public TaskTerm() {
  }

  public void removeByInstance(int instanceId)
      throws WorkflowException {
    try {
      TaskTermBean bean = new TaskTermBean();
      bean.remove(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(2000, sqle.toString());
    }
  }

  public void removeByNode(int instanceId, int nodeId)
      throws WorkflowException {
    try {
      TaskTermBean bean = new TaskTermBean();
      bean.delete(instanceId, nodeId);
    } catch (SQLException sqle) {
      throw new WorkflowException(2000, sqle.toString());
    }
  }

  public void reset(int instanceId, int nodeId, int limitExecuteTerm) throws WorkflowException {
    try {
      TaskTermBean bean = new TaskTermBean();
      bean.delete(instanceId, nodeId);
      TaskTermModel model = new TaskTermModel();
      model.setInstanceId(instanceId);
      model.setNodeId(nodeId);
      model.setLimitExecuteTerm(limitExecuteTerm);
      bean.insert(model);
    } catch (SQLException sqle) {
      throw new WorkflowException(2000, sqle.toString());
    }
  }

  public int getLimitExecuteTerm(int instanceId, int nodeId)
      throws WorkflowException {
    int result = 0;
    try {
      TaskTermBean bean = new TaskTermBean();
      TaskTermModel model = bean.findByKey(instanceId, nodeId);
      if (model.getLimitExecuteTerm() != null)
        result = model.getLimitExecuteTerm().intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(2000, sqle.toString());
    }
    return result;
  }

  public String getLimitExecuteTime(int instanceId, int nodeId, String time) throws WorkflowException {
    String result = null;

    int limitExecuteTerm = 0;
    try {
      TaskTermBean bean = new TaskTermBean();
      TaskTermModel model = bean.findByKey(instanceId, nodeId);
      if (model.getLimitExecuteTerm() != null)
        limitExecuteTerm = model.getLimitExecuteTerm().intValue();
    } catch (SQLException sqle) {
      throw new WorkflowException(2000, sqle.toString());
    }
    if (limitExecuteTerm == 0) {
      Node nodeHandler = new Node();
      NodeMeta node = nodeHandler.getNode(nodeId);
      if (node.getLimitExecuteTerm() != 0) {
        limitExecuteTerm = node.getLimitExecuteTerm();
      }
    }
    if (limitExecuteTerm != 0) {
      result = DateTime.getTime(time, Calendar.HOUR, limitExecuteTerm);
    }

    return result;
  }
}
