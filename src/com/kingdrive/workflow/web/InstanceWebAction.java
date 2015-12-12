package com.kingdrive.workflow.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.TaskMeta;
import com.kingdrive.workflow.dto.TaskResultMeta;
import com.kingdrive.workflow.dto.VariableValueMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.DateTime;
import com.kingdrive.workflow.util.StringUtil;

public class InstanceWebAction extends WebAction {

  public InstanceWebAction() {
  }

  private String executeTask() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "NODE_ID"));
    String nodeLinkGroupId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "NODE_LINK_GROUP_ID"));
    int instanceId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "INSTANCE_ID"));
    String instanceName = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "INSTANCE_NAME"));
    String instanceDescription = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "INSTANCE_DESCRIPTION"));
    List valueList = new ArrayList();
    String comment = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "COMMENT"));
    int currentTaskId = StringUtil.string2int((String) tps.get(
        SessionManager.PDS, "CURRENT_TASK_ID"));
    String executor = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "STAFF_ID"));
    String positionId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "POSITION_ID"));

    String variableId[] = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "VARIABLE_ID"));
    String value[] = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "VALUE"));
    if (variableId != null && value != null) {
      for (int i = 0; i < variableId.length; i++) {
        if (value[i] != null && !value[i].trim().equals("")) {
          VariableValueMeta meta = new VariableValueMeta();
          meta.setVariableId(Integer.parseInt(variableId[i]));
          meta.setValue(value[i]);
          valueList.add(meta);
        }
      }
    }

    TaskResultMeta result = null;
    try {
      result = ExecuteFacade.executeTask(currentTaskId, instanceId,
          instanceName, instanceDescription, nodeId, nodeLinkGroupId, comment,
          valueList, executor, positionId);

    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.PDS, "INSTANCE_ID", String.valueOf(result
        .getInstanceId()));
    if (instanceId == -1)
      return "FIRST";
    return null;
  }

  private String getMonitorList() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    List monitorList = null;
    List staffList = null;
    try {
      monitorList = ExecuteFacade.getTodoListByOwner(staffId);
      staffList = ConfigureFacade.getStaffList();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "MONITOR_LIST", monitorList);
    tps.put(SessionManager.TDS, "STAFF_LIST", staffList);
    return null;
  }

  private String getHistoryList() throws GeneralException {
    List historyList = null;
    List nodeList = null;
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    nodeList = new ArrayList();
    try {
      historyList = ExecuteFacade.getActionListByOwner(staffId, DateTime
          .getTime(Calendar.YEAR, -1), DateTime.getSysTime());
      for (int i = 0; i < historyList.size(); i++) {
        int instanceId = ((ActionMeta) historyList.get(i)).getInstanceId();
        int templateId = ExecuteFacade.getInstance(instanceId).getTemplateId();
        nodeList.addAll(ConfigureFacade.getNodeList(templateId));
      }

    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "HISTORY_LIST", historyList);
    tps.put(SessionManager.TDS, "NODE_LIST", nodeList);
    return null;
  }

  private String getTodoList() throws GeneralException {
    List todoList = null;
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    todoList = new ArrayList();
    try {
      todoList = ExecuteFacade.getTodoListByExecutor(staffId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "TODO_LIST", todoList);
    return null;
  }

  private String getCurrentTask() throws GeneralException {
    int currentTaskId = StringUtil.string2int((String) tps.get(
        SessionManager.PDS, "CURRENT_TASK_ID"));
    ;
    TaskMeta meta = null;
    try {
      meta = ExecuteFacade.getTask(currentTaskId,1);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "TASK", meta);
    return null;
  }

  private String listNextTaskNode() {
    return null;
  }

  private String getTaskExecutorOrderList() throws GeneralException {
    String sInstanceId = (String) tps.get(SessionManager.TDS, "INSTANCE_ID");
    String sNodeId = (String) tps.get(SessionManager.PDS, "NODE_ID");
    List taskExecutorOrderList = new ArrayList();
    List staffList = new ArrayList();
    int instanceId = Integer.parseInt(sInstanceId);
    int nodeId = Integer.parseInt(sNodeId);
    try {
      taskExecutorOrderList = ExecuteFacade.getTaskExecutorList(instanceId,
          nodeId);
      staffList = ConfigureFacade.getStaffList();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "TASK_EXECUTOR_ORDER_LIST",
        taskExecutorOrderList);
    tps.put(SessionManager.TDS, "STAFF_LIST", staffList);
    return null;
  }

  private String addTaskExecutorOrder() throws GeneralException {
    String sNewStaffId = (String) tps.get(SessionManager.PDS, "NEW_STAFF_ID");
    int instanceId = Integer.parseInt((String) tps.get(SessionManager.PDS,
        "INSTANCE_ID"));
    int nodeId = Integer.parseInt((String) tps.get(SessionManager.PDS,
        "NODE_ID"));
    int newOrder = Integer.parseInt((String) tps.get(SessionManager.PDS,
        "NEW_ORDER"));
    int newResponsibility = Integer.parseInt((String) tps.get(
        SessionManager.PDS, "NEW_RESPONSIBILITY"));
    try {
      ExecuteFacade.createTaskExecutor(instanceId, nodeId, sNewStaffId,
          newOrder, newResponsibility);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    return null;
  }

  private String deleteTaskExecutorOrder() throws GeneralException {
    String sTaskExecutorOrderId = (String) tps.get(SessionManager.PDS,
        "TASK_EXECUTOR_ORDER_ID");
    String sNodeId = (String) tps.get(SessionManager.PDS, "NODE_ID");
    int taskExecutorOrderId = Integer.parseInt(sTaskExecutorOrderId);
    ;
    try {
      ExecuteFacade.removeTaskExecutor(taskExecutorOrderId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.PDS, "NODE_ID", sNodeId);
    return null;
  }

  private String modifyTaskExecutorOrder() throws GeneralException {
    String sNodeId = (String) tps.get(SessionManager.PDS, "NODE_ID");
    int listSize = Integer.parseInt((String) tps.get(SessionManager.PDS,
        "LIST_SIZE"));
    ;
    int taskExecutorOrderId[] = new int[listSize];
    int newOrder[] = new int[listSize];
    for (int i = 0; i < listSize; i++) {
      String sNewOrder = (String) tps.get(SessionManager.PDS, "EXECUTOR_ORDER"
          .concat(String.valueOf(i)));
      String sTaskExecutorOrderId = (String) tps.get(SessionManager.PDS,
          "TASK_EXECUTOR_ORDER_ID".concat(String.valueOf(i)));
      taskExecutorOrderId[i] = Integer.parseInt(sTaskExecutorOrderId);
      newOrder[i] = Integer.parseInt(sNewOrder);
    }

    try {
      ExecuteFacade.resetTaskExecutor(taskExecutorOrderId, newOrder);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.PDS, "NODE_ID", sNodeId);
    return null;
  }

  private String getCommentList() throws GeneralException {
    List commentList = null;
    List nodeList = null;
    String sInstanceId = (String) tps.get(SessionManager.TDS, "INSTANCE_ID");
    nodeList = new ArrayList();
    int instanceId = Integer.parseInt(sInstanceId);

    try {
      commentList = ExecuteFacade.getActionListByInstance(instanceId,1);
      for (int i = 0; i < commentList.size(); i++) {
        int instanceId2 = ((ActionMeta) commentList.get(i)).getInstanceId();
        int templateId = ExecuteFacade.getInstance(instanceId2).getTemplateId();
        nodeList.addAll(ConfigureFacade.getNodeList(templateId));
      }

    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "COMMENT_LIST", commentList);
    tps.put(SessionManager.TDS, "NODE_LIST", nodeList);
    return "OK";
  }

  public String perform(String event) throws GeneralException {
    int eventId = Integer.parseInt(event);
    switch (eventId) {
    case 2: // '\002'
      return executeTask();

    case 3: // '\003'
      return getMonitorList();

    case 4: // '\004'
      return getHistoryList();

    case 5: // '\005'
      return getTodoList();

    case 6: // '\006'
      return getCurrentTask();

    case 8: // '\b'
      return listNextTaskNode();

    case 9: // '\t'
      return getTaskExecutorOrderList();

    case 10: // '\n'
      return addTaskExecutorOrder();

    case 11: // '\013'
      return deleteTaskExecutorOrder();

    case 12: // '\f'
      return modifyTaskExecutorOrder();

    case 13: // '\r'
      return getCommentList();
    default:
      throw new GeneralException("Î´Öª¹¦ÄÜ¡£");
    }
  }

}
