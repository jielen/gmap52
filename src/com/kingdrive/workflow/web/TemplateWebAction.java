package com.kingdrive.workflow.web;

import java.util.ArrayList;
import java.util.List;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.business.Company;
import com.kingdrive.workflow.business.Executor;
import com.kingdrive.workflow.business.Link;
import com.kingdrive.workflow.business.LinkState;
import com.kingdrive.workflow.business.Node;
import com.kingdrive.workflow.business.NodeState;
import com.kingdrive.workflow.business.Org;
import com.kingdrive.workflow.business.Position;
import com.kingdrive.workflow.business.Role;
import com.kingdrive.workflow.business.Staff;
import com.kingdrive.workflow.business.State;
import com.kingdrive.workflow.business.Template;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TaskExecutorMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.DateTime;
import com.kingdrive.workflow.util.StringUtil;

public class TemplateWebAction extends WebAction {

  public TemplateWebAction() {
  }

  public String perform(String event) throws GeneralException {
    if ("gettemplate".equals(event)) {
      return getTemplate();
    } else if ("addtemplate".equals(event)) {
      return addTemplate();
    } else if ("modifytemplate".equals(event)) {
      return modifyTemplate();
    } else if ("deletetemplate".equals(event)) {
      return removeTemplate();
    } else if ("listvariable".equals(event)) {
      return listVariable();
    } else if ("addvariable".equals(event)) {
      return addVariable();
    } else if ("modifyvariable".equals(event)) {
      return modifyVariable();
    } else if ("getvariable".equals(event)) {
      return getVariable();
    } else if ("deletevariable".equals(event)) {
      return removeVariable();
    } else if ("liststate".equals(event)) {
      return listState();
    } else if ("addstate".equals(event)) {
      return addState();
    } else if ("modifystate".equals(event)) {
      return modifyState();
    } else if ("getstate".equals(event)) {
      return getState();
    } else if ("deletestate".equals(event)) {
      return removeState();
    } else if ("listnode".equals(event)) {
      return listNode();
    } else if ("addnode".equals(event)) {
      return addNode();
    } else if ("modifynode".equals(event)) {
      return modifyNode();
    } else if ("getnode".equals(event)) {
      return getNode();
    } else if ("deletenode".equals(event)) {
      return removeNode();
    } else if ("listnodestate".equals(event)) {
      return listNodeState();
    } else if ("resetnodestate".equals(event)) {
      return resetNodeState();
    } else if ("listnodeexecutorsource".equals(event)) {
      return listNodeExecutorSource();
    } else if ("resetnodeexecutorsource".equals(event)) {
      return resetNodeExecutorSource();
    } else if ("listnodeexecutororder".equals(event)) {
      return listNodeExecutorOrder();
    } else if ("resetnodeexecutororder".equals(event)) {
      return resetNodeExecutorOrder();
    } else if ("getlink".equals(event)) {
      return getLink();
    } else if ("addlink".equals(event)) {
      return addLink();
    } else if ("modifylink".equals(event)) {
      return modifyLink();
    } else if ("deletelink".equals(event)) {
      return removeLink();
    } else if ("modifylinkexpression".equals(event)) {
      return modifyLinkExpression();
    } else if ("listlinkstate".equals(event)) {
      return listLinkState();
    } else if ("resetlinkstate".equals(event)) {
      return resetLinkState();
    } else {
      throw new GeneralException("未知功能。");
    }
  }

  private String addTemplate() throws GeneralException {

    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "NAME"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "DESCRIPTION"));
    String version = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "VERSION"));
    String startTime = DateTime.getSimpleDate(
        (String) tps.get(SessionManager.PDS, "START_TIME")).concat("000000");
    String expireTime = DateTime.getSimpleDate(
        (String) tps.get(SessionManager.PDS, "EXPIRE_TIME")).concat("235959");
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    String createTime = DateTime.getSysTime();
    String isActive = Template.TEMPLATE_INACTIVE;
    String type = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "TYPE"));

    TemplateMeta template = new TemplateMeta();
    template.setName(name);
    template.setDescription(description);
    template.setVersion(version);
    template.setStartTime(startTime);
    template.setExpireTime(expireTime);
    template.setCreateTime(createTime);
    template.setCreateStaffId(staffId);
    template.setIsActive(isActive);
    template.setTemplateType(type);

    try {
      ConfigureFacade.addTemplate(template);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建流程成功！");
    return null;
  }

  private String getTemplate() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    TemplateMeta templateInfo;
    try {
      templateInfo = ConfigureFacade.getTemplate(templateId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "TEMPLATE_INFO", templateInfo);
    return null;
  }

  private String removeTemplate() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));

    try {
      ConfigureFacade.removeTemplate(templateId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除流程成功！");
    return null;
  }

  private String modifyTemplate() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "NAME"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "DESCRIPTION"));
    String version = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "VERSION"));
    String startTime = DateTime.getSimpleDate(
        (String) tps.get(SessionManager.PDS, "START_TIME")).concat("000000");
    String expireTime = DateTime.getSimpleDate(
        (String) tps.get(SessionManager.PDS, "EXPIRE_TIME")).concat("235959");
    String createTime = DateTime.getSysTime();
    String isActive = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "IS_ACTIVE"));
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.SDS, "STAFF_ID"));
    String type = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "TYPE"));

    TemplateMeta template = new TemplateMeta();
    template.setTemplateId(templateId);
    template.setName(name);
    template.setDescription(description);
    template.setVersion(version);
    template.setStartTime(startTime);
    template.setExpireTime(expireTime);
    template.setIsActive(isActive);
    template.setCreateStaffId(staffId);
    template.setCreateTime(createTime);
    template.setTemplateType(type);

    try {
      ConfigureFacade.updateTemplate(template);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "修改流程成功！");
    return null;
  }

  private String listVariable() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));

    List list;
    try {
      list = ConfigureFacade.getVariableList(templateId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "varlist", list);
    return null;
  }

  private String addVariable() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    String varName = StringUtil.string2string(
        (String) tps.get(SessionManager.PDS, "varname")).toUpperCase();
    String varDesc = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "vardesc"));
    String varType = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "vartype"));

    VariableMeta var = new VariableMeta();
    var.setName(varName);
    var.setDescription(varDesc);
    var.setType(varType);
    var.setTemplateId(templateId);
    try {
      ConfigureFacade.addVariable(var);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "创建流程变量成功！");
    return null;
  }

  private String modifyVariable() throws GeneralException {
    int varId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "varid"));
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    String varName = StringUtil.string2string(
        (String) tps.get(SessionManager.PDS, "varname")).toUpperCase();
    String varDesc = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "vardesc"));
    String varType = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "vartype"));

    VariableMeta var = new VariableMeta();
    var.setId(varId);
    var.setName(varName);
    var.setDescription(varDesc);
    var.setType(varType);
    var.setTemplateId(templateId);
    try {
      ConfigureFacade.updateVariable(var);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "修改流程变量成功！");
    return null;
  }

  private String getVariable() throws GeneralException {
    int varId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "varid"));
    ;

    VariableMeta var;
    try {
      var = ConfigureFacade.getVariable(varId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "varinfo", var);
    return null;
  }

  private String removeVariable() throws GeneralException {
    int varId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "varid"));
    ;

    try {
      ConfigureFacade.removeVariable(varId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "删除流程变量成功！");
    return null;
  }

  private String listState() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));

    java.util.List list;
    try {
      list = ConfigureFacade.getStateList(templateId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "statelist", list);
    return null;
  }

  private String getState() throws GeneralException {
    int stateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "stateid"));
    ;

    State state;
    try {
      state = ConfigureFacade.getState(stateId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "stateinfo", state);
    return null;
  }

  private String removeState() throws GeneralException {
    int stateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "stateid"));
    ;

    try {
      ConfigureFacade.removeState(stateId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "删除流程状态成功！");
    return null;
  }

  private String modifyState() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int stateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "stateid"));
    ;
    String stateName = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "statename"));
    String stateDesc = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "statedesc"));

    State state = new State();
    state.setId(stateId);
    state.setName(stateName);
    state.setDescription(stateDesc);
    state.setTemplateId(templateId);

    try {
      ConfigureFacade.updateState(state);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "修改流程状态成功！");
    return null;
  }

  private String addState() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    String stateName = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "statename"));
    String stateDesc = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "statedesc"));

    State state = new State();
    state.setName(stateName);
    state.setDescription(stateDesc);
    state.setTemplateId(templateId);

    try {
      ConfigureFacade.addState(state);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建流程状态成功！");
    return null;
  }

  private String listNode() throws GeneralException {
    List result = new ArrayList();

    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    try {
      result = ConfigureFacade.getNodeList(templateId);
      for (int i = 0; i < result.size(); i++) {
        NodeMeta n = (NodeMeta) result.get(i);
        if (n.getId() == nodeId) {
          result.remove(i);
          break;
        }
      }
      NodeMeta end = new NodeMeta();
      end.setId(-2);
      end.setName("结束节点");
      end.setType(Node.TYPE_END);
      end.setTemplateId(templateId);
      result.add(end);

    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    if (result.size() == 0) {
      throw new GeneralException("无可选的后续节点。");
    }
    tps.put(SessionManager.TDS, "nodelist", result);
    /*
     * int templateId =
     * StringUtil.string2int((String)tps.get(SessionManager.PDS, "templateid"));
     * 
     * NodeMeta begin = new NodeMeta(); begin.setTemplateId(templateId);
     * begin.setId( -1); begin.setName("开始节点"); begin.setType(Node.TYPE_START);
     * 
     * List nodeList; try{ nodeList = ConfigureFacade.getNodeList(templateId); }
     * catch(WorkflowException wfe){ throw new GeneralException(wfe.toString()); }
     * 
     * NodeMeta end = new NodeMeta(); end.setId( -2); end.setName("结束节点");
     * end.setType(Node.TYPE_END); end.setTemplateId(templateId);
     * 
     * result.add(begin); result.addAll(nodeList); result.add(end);
     * 
     * tps.put(SessionManager.TDS, "nodelist", result);
     */
    return null;
  }

  private String addNode() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "nodename"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "nodedesc"));
    String type = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "nodetype"));
    String method = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "nodemethod"));
    String listener = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "listener"));
    String businessType = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "businesstype"));
    int limitExecuteTerm = StringUtil.string2int((String) tps.get(
        SessionManager.PDS, "limitexecuteterm"));

    NodeMeta node = new NodeMeta();
    node.setName(name);
    node.setDescription(description);
    node.setType(type);
    node.setTemplateId(templateId);
    node.setExecutorsMethod(method);
    node.setTaskListener(listener);
    node.setBusinessType(businessType);
    node.setLimitExecuteTerm(limitExecuteTerm);

    try {
      ConfigureFacade.addNode(node);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.PDS, "nodeid", "".concat(String
        .valueOf(node.getId())));
    tps.put(SessionManager.TDS, "success_message", "创建流程节点成功！");
    return null;
  }

  private String getNode() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));

    if (nodeId == -1) {
      NodeMeta begin = new NodeMeta();
      begin.setTemplateId(templateId);
      begin.setId(-1);
      begin.setName("开始节点");
      begin.setType(Node.TYPE_START);
      tps.put(SessionManager.TDS, "nodeinfo", begin);
    } else {
      try {
        tps
            .put(SessionManager.TDS, "nodeinfo", ConfigureFacade
                .getNode(nodeId));
      } catch (WorkflowException wfe) {
        throw new GeneralException(wfe.toString());
      }
    }
    return null;
  }

  private String modifyNode() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int limitExecuteTerm = StringUtil.string2int((String) tps.get(
        SessionManager.PDS, "limitexecuteterm"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "nodename"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "nodedesc"));
    String type = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "nodetype"));
    String method = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "nodemethod"));
    String listener = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "listener"));
    String businessType = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "businesstype"));

    NodeMeta node = new NodeMeta();
    node.setId(nodeId);
    node.setName(name);
    node.setDescription(description);
    node.setType(type);
    node.setTemplateId(templateId);
    node.setExecutorsMethod(method);
    node.setTaskListener(listener);
    node.setBusinessType(businessType);
    node.setLimitExecuteTerm(limitExecuteTerm);

    try {
      ConfigureFacade.updateNode(node);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "修改流程节点成功！");
    return null;
  }

  private String removeNode() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));

    try {
      ConfigureFacade.removeNode(nodeId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除流程节点成功！");
    return null;
  }

  private String listNodeState() throws GeneralException {
    List result = new ArrayList();
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));

    List stateList;
    List nodeStateList;
    try {
      stateList = ConfigureFacade.getStateList(templateId);
      nodeStateList = ConfigureFacade.getNodeStateList(nodeId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    for (int i = 0; i < stateList.size(); i++) {
      State state = (State) stateList.get(i);
      boolean existed = false;
      for (int j = 0; j < nodeStateList.size(); j++) {
        NodeState nodeState = (NodeState) nodeStateList.get(j);
        if (nodeState.getStateId() == state.getId()) {
          existed = true;
          nodeState.setStateName(state.getName());
          result.add(nodeState);
          break;
        }
      }
      if (!existed) {
        NodeState nodeState = new NodeState();
        nodeState.setNodeId(nodeId);
        nodeState.setStateId(state.getId());
        nodeState.setStateName(state.getName());
        result.add(nodeState);
      }
    }
    if (result.size() == 0) {
      throw new GeneralException("没有定义流程状态");
    }

    tps.put(SessionManager.TDS, "nodestatelist", result);
    return null;
  }

  private String resetNodeState() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    String[] state = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "state"));
    int[] stateId = null;
    String[] stateValue = null;
    if (state != null) {
      stateId = new int[state.length];
      stateValue = new String[state.length];
      for (int i = 0; i < state.length; i++) {
        stateId[i] = StringUtil.string2int(state[i]);
        stateValue[i] = StringUtil.string2string((String) tps.get(
            SessionManager.PDS, "value_".concat(state[i])));
      }
    }

    try {
      ConfigureFacade.resetNodeState(nodeId, stateId, stateValue);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "设置流程节点状态成功！");
    return null;
  }

  private String listNodeExecutorSource() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    int source = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "sourcetype"));
    int mainresponsibility = TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN;
    int assistantresponsibility = TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_ASSISTANT;

    StringBuffer sourceselect = new StringBuffer("");
    StringBuffer mainexecutorselect = new StringBuffer("");
    StringBuffer assistantexecutorselect = new StringBuffer("");

    List sourcelist;
    List mainexecutorlist;
    List assistantexecutorlist;
    try {
      switch (source) {
      case Executor.EXECUTOR_SOURCE_COMPANY:
        sourcelist = ConfigureFacade.getCompanyListByNonExecutor(nodeId);
        mainexecutorlist = ConfigureFacade.getCompanyListByExecutor(nodeId,
            mainresponsibility);
        assistantexecutorlist = ConfigureFacade.getCompanyListByExecutor(
            nodeId, assistantresponsibility);
        for (int i = 0; i < sourcelist.size(); i++) {
          Company com = (Company) sourcelist.get(i);
          sourceselect.append("<option value=\"").append(com.getId()).append(
              "\">").append(com.getName()).append("</option>").append("\n");
        }
        for (int i = 0; i < mainexecutorlist.size(); i++) {
          Company com = (Company) mainexecutorlist.get(i);
          mainexecutorselect.append("<option value=\"").append(com.getId())
              .append("\">").append(com.getName()).append("</option>").append(
                  "\n");
        }
        for (int i = 0; i < assistantexecutorlist.size(); i++) {
          Company com = (Company) assistantexecutorlist.get(i);
          assistantexecutorselect.append("<option value=\"")
              .append(com.getId()).append("\">").append(com.getName()).append(
                  "</option>").append("\n");
        }
        break;
      case Executor.EXECUTOR_SOURCE_ORGANIZATION:
        sourcelist = ConfigureFacade.getOrgListByNonExecutor(nodeId);
        mainexecutorlist = ConfigureFacade.getOrgListByExecutor(nodeId,
            mainresponsibility);
        assistantexecutorlist = ConfigureFacade.getOrgListByExecutor(nodeId,
            assistantresponsibility);
        for (int i = 0; i < sourcelist.size(); i++) {
          Org org = (Org) sourcelist.get(i);
          sourceselect.append("<option value=\"").append(org.getId()).append(
              "\">").append(org.getCompanyName()).append("》").append(
              org.getName()).append("</option>").append("\n");
        }
        for (int i = 0; i < mainexecutorlist.size(); i++) {
          Org org = (Org) mainexecutorlist.get(i);
          mainexecutorselect.append("<option value=\"").append(org.getId())
              .append("\">").append(org.getCompanyName()).append("》").append(
                  org.getName()).append("</option>").append("\n");
        }
        for (int i = 0; i < assistantexecutorlist.size(); i++) {
          Org org = (Org) assistantexecutorlist.get(i);
          assistantexecutorselect.append("<option value=\"")
              .append(org.getId()).append("\">").append(org.getCompanyName())
              .append("》").append(org.getName()).append("</option>").append(
                  "\n");
        }
        break;
      case Executor.EXECUTOR_SOURCE_ROLE:
        sourcelist = ConfigureFacade.getRoleListByNonExecutor(nodeId);
        mainexecutorlist = ConfigureFacade.getRoleListByExecutor(nodeId,
            mainresponsibility);
        assistantexecutorlist = ConfigureFacade.getRoleListByExecutor(nodeId,
            assistantresponsibility);
        for (int i = 0; i < sourcelist.size(); i++) {
          Role role = (Role) sourcelist.get(i);
          sourceselect.append("<option value=\"").append(role.getId()).append(
              "\">").append(role.getName()).append("</option>").append("\n");
        }
        for (int i = 0; i < mainexecutorlist.size(); i++) {
          Role role = (Role) mainexecutorlist.get(i);
          mainexecutorselect.append("<option value=\"").append(role.getId())
              .append("\">").append(role.getName()).append("</option>").append(
                  "\n");
        }
        for (int i = 0; i < assistantexecutorlist.size(); i++) {
          Role role = (Role) assistantexecutorlist.get(i);
          assistantexecutorselect.append("<option value=\"").append(
              role.getId()).append("\">").append(role.getName()).append(
              "</option>").append("\n");
        }
        break;
      case Executor.EXECUTOR_SOURCE_POSITION:
        sourcelist = ConfigureFacade.getPositionListByNonExecutor(nodeId);
        mainexecutorlist = ConfigureFacade.getPositionListByExecutor(nodeId,
            mainresponsibility);
        assistantexecutorlist = ConfigureFacade.getPositionListByExecutor(
            nodeId, assistantresponsibility);
        for (int i = 0; i < sourcelist.size(); i++) {
          Position position = (Position) sourcelist.get(i);
          sourceselect.append("<option value=\"").append(position.getId())
              .append("\">").append(position.getName()).append("</option>")
              .append("\n");
        }
        for (int i = 0; i < mainexecutorlist.size(); i++) {
          Position position = (Position) mainexecutorlist.get(i);
          mainexecutorselect.append("<option value=\"")
              .append(position.getId()).append("\">")
              .append(position.getName()).append("</option>").append("\n");
        }
        for (int i = 0; i < assistantexecutorlist.size(); i++) {
          Position position = (Position) assistantexecutorlist.get(i);
          assistantexecutorselect.append("<option value=\"").append(
              position.getId()).append("\">").append(position.getName())
              .append("</option>").append("\n");
        }
        break;
      case Executor.EXECUTOR_SOURCE_STAFF:
        sourcelist = ConfigureFacade.getStaffListByNonExecutor(nodeId);
        mainexecutorlist = ConfigureFacade.getStaffListByExecutor(nodeId,
            mainresponsibility);
        assistantexecutorlist = ConfigureFacade.getStaffListByExecutor(nodeId,
            assistantresponsibility);
        for (int i = 0; i < sourcelist.size(); i++) {
          Staff staff = (Staff) sourcelist.get(i);
          sourceselect.append("<option value=\"").append(staff.getId()).append(
              "\">").append(staff.getName()).append("</option>").append("\n");
        }
        for (int i = 0; i < mainexecutorlist.size(); i++) {
          Staff staff = (Staff) mainexecutorlist.get(i);
          mainexecutorselect.append("<option value=\"").append(staff.getId())
              .append("\">").append(staff.getName()).append("</option>")
              .append("\n");
        }
        for (int i = 0; i < assistantexecutorlist.size(); i++) {
          Staff staff = (Staff) assistantexecutorlist.get(i);
          assistantexecutorselect.append("<option value=\"").append(
              staff.getId()).append("\">").append(staff.getName()).append(
              "</option>").append("\n");
        }
        break;
      default:

      }
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "sourceselect", sourceselect.toString());
    tps.put(SessionManager.TDS, "mainexecutorselect", mainexecutorselect
        .toString());
    tps.put(SessionManager.TDS, "assistantexecutorselect",
        assistantexecutorselect.toString());
    return null;
  }

  private String resetNodeExecutorSource() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    int source = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "sourcetype"));
    String[] mainexecutor = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "mainexecutor"));
    String[] assistantexecutor = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "assistantexecutor"));
    try {
      ConfigureFacade.resetExecutorSource(nodeId, mainexecutor, source,
          TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN);
      ConfigureFacade.resetExecutorSource(nodeId, assistantexecutor, source,
          TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_ASSISTANT);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "设置流程节点执行者成功！");
    return null;
  }

  private String listNodeExecutorOrder() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    String executorsMethod = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "nodemethod"));
    List executorlist;
    try {
      ConfigureFacade.resetExecutorOrder(nodeId, executorsMethod);
      executorlist = ConfigureFacade.getExecutorListByOrder(nodeId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "executorlist", executorlist);
    return null;
  }

  private String resetNodeExecutorOrder() throws GeneralException {
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    String[] executor = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "executor"));
    int[] order = new int[0];
    int[] responsibility = new int[0];
    if (executor != null) {
      order = new int[executor.length];
      responsibility = new int[executor.length];
      for (int i = 0; i < executor.length; i++) {
        order[i] = StringUtil.string2int((String) tps.get(SessionManager.PDS,
            "order_".concat(executor[i])));
        responsibility[i] = StringUtil.string2int((String) tps.get(
            SessionManager.PDS, "responsibility_".concat(executor[i])));
      }
    }

    try {
      ConfigureFacade.resetExecutorOrder(nodeId, executor, order,
          responsibility);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置流程节点执行者执行顺序成功！");
    return null;
  }

  private String addLink() throws GeneralException {
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    int nextNodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nextnodeid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "linkname"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "linkdesc"));
    String type = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "linktype"));
    String linkGroup = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "linkgroup"));
    String relation = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "relation"));
    int passValue = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "passvalue"));
    String numberOrPercent = null;
    if (StringUtil.string2boolean((String) tps.get(SessionManager.PDS,
        "percent"))) {
      numberOrPercent = Link.NUMBERORPERCENT_PERCENT;
    } else {
      numberOrPercent = Link.NUMBERORPERCENT_NUMBER;
    }

    Link link = new Link();
    link.setName(name);
    link.setDescription(description);
    link.setTemplateId(templateId);
    link.setCurrentNodeId(nodeId);
    link.setNextNodeId(nextNodeId);
    link.setType(type);
    link.setActionName(linkGroup);
    link.setExecutorRelation(relation);
    link.setPassValue(passValue);
    link.setNumberOrPercent(numberOrPercent);

    try {
      ConfigureFacade.addLink(link);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.PDS, "linkid", "".concat(String
        .valueOf(link.getId())));
    tps.put(SessionManager.TDS, "success_message", "创建流向成功！");
    return null;
  }

  private String getLink() throws GeneralException {
    int linkId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "linkid"));

    Link link;
    try {
      link = ConfigureFacade.getLink(linkId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "linkinfo", link);
    return null;
  }

  private String modifyLink() throws GeneralException {
    int linkId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "linkid"));
    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int nodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nodeid"));
    int nextNodeId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "nextnodeid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "linkname"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "linkdesc"));
    String type = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "linktype"));
    String linkGroup = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "linkgroup"));
    String relation = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "relation"));
    int passValue = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "passvalue"));
    String numberOrPercent = null;
    if (StringUtil.string2boolean((String) tps.get(SessionManager.PDS,
        "percent"))) {
      numberOrPercent = Link.NUMBERORPERCENT_PERCENT;
    } else {
      numberOrPercent = Link.NUMBERORPERCENT_NUMBER;
    }

    Link link = new Link();
    link.setId(linkId);
    link.setName(name);
    link.setDescription(description);
    link.setTemplateId(templateId);
    link.setCurrentNodeId(nodeId);
    link.setNextNodeId(nextNodeId);
    link.setType(type);
    link.setActionName(linkGroup);
    link.setExecutorRelation(relation);
    link.setPassValue(passValue);
    link.setNumberOrPercent(numberOrPercent);

    try {
      ConfigureFacade.updateLink(link);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "修改流向成功！");
    return null;
  }

  private String removeLink() throws GeneralException {
    int linkId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "linkid"));

    try {
      ConfigureFacade.removeLink(linkId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "删除流向成功！");
    return null;
  }

  private String modifyLinkExpression() throws GeneralException {
    int linkId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "linkid"));
    String expression = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "expression"));
    String defaultPath = null;
    if (StringUtil.string2boolean((String) tps.get(SessionManager.PDS,
        "defaultpath"))) {
      defaultPath = Link.PATH_DEFAULT;
    } else {
      defaultPath = Link.PATH_NOT_DEFAULT;
    }

    try {
      ConfigureFacade.setLinkExpression(linkId, expression, defaultPath);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "success_message", "设置流转表达式成功！");
    return null;
  }

  private String listLinkState() throws GeneralException {
    List result = new ArrayList();

    int templateId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "templateid"));
    int linkId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "linkid"));

    List stateList;
    List linkStateList;
    try {
      stateList = ConfigureFacade.getStateList(templateId);
      linkStateList = ConfigureFacade.getLinkStateList(linkId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    for (int i = 0; i < stateList.size(); i++) {
      State state = (State) stateList.get(i);
      boolean existed = false;
      for (int j = 0; j < linkStateList.size(); j++) {
        LinkState linkState = (LinkState) linkStateList.get(j);
        if (linkState.getStateId() == state.getId()) {
          existed = true;
          linkState.setStateName(state.getName());
          result.add(linkState);
          break;
        }
      }
      if (!existed) {
        LinkState linkState = new LinkState();
        linkState.setNodeLinkId(linkId);
        linkState.setStateId(state.getId());
        linkState.setStateName(state.getName());
        result.add(linkState);
      }
    }
    if (result.size() == 0) {
      throw new GeneralException("没有定义流程状态");
    }

    tps.put(SessionManager.TDS, "linkstatelist", result);
    return null;
  }

  private String resetLinkState() throws GeneralException {
    int linkId = StringUtil.string2int((String) tps.get(SessionManager.PDS,
        "linkid"));
    String[] state = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "state"));
    int[] stateId = null;
    String[] stateValue = null;
    if (state != null) {
      stateId = new int[state.length];
      stateValue = new String[state.length];
      for (int i = 0; i < state.length; i++) {
        stateId[i] = StringUtil.string2int(state[i]);
        stateValue[i] = StringUtil.string2string((String) tps.get(
            SessionManager.PDS, "value_".concat(state[i])));
      }
    }

    try {
      ConfigureFacade.resetLinkState(linkId, stateId, stateValue);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置流向状态成功！");
    return null;
  }

}
