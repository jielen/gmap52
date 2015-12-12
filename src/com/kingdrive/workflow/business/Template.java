package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.CountQuery;
import com.kingdrive.workflow.access.TemplateBean;
import com.kingdrive.workflow.access.TemplateQuery;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.TemplateInfo;
import com.kingdrive.workflow.model.TemplateModel;
import com.kingdrive.workflow.util.DateTime;
import com.kingdrive.workflow.util.Sequence;

public class Template implements Serializable {

  public static final String TEMPLATE_ACTIVE = "0";

  public static final String TEMPLATE_INACTIVE = "1";

  public Template() {
  }

  public List getTemplateList() throws WorkflowException {
    List templateList = new ArrayList();

    try {
      TemplateQuery query = new TemplateQuery();
      ArrayList list = query.getTemplateList();
      TemplateMeta meta = null;
      for (int i = 0; i < list.size(); i++, templateList.add(meta)) {
        meta = wrap((TemplateInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1010, sqle.toString());
    }
    return templateList;
  }

  public List getTemplateList(String templateType)
      throws WorkflowException {
    List templateList = new ArrayList();

    try {
      TemplateQuery query = new TemplateQuery();
      ArrayList list = query.getTemplateListByType(templateType);
      TemplateMeta meta = null;
      for (int i = 0; i < list.size(); i++, templateList.add(meta)) {
        meta = wrap((TemplateInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1010, sqle.toString());
    }
    return templateList;
  }

  public List getActiveTemplateList(String templateType)
      throws WorkflowException {
    if (templateType == null || templateType.length() == 0)
      throw new WorkflowException(1035);
    List result = new ArrayList();

    try {
      TemplateQuery query = new TemplateQuery();
      String currentTime = DateTime.getSysTime();
      ArrayList list = query.getActiveTemplateList(templateType,
          currentTime);
      TemplateMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TemplateInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1011, sqle.toString());
    }
    return result;
  }

  public List getActiveTemplateList() throws WorkflowException {
    List result = new ArrayList();

    try {
      TemplateQuery query = new TemplateQuery();
      String currentTime = DateTime.getSysTime();
      ArrayList list = query.getActiveTemplateList( currentTime);
      TemplateMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TemplateInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1011, sqle.toString());
    }
    return result;
  }

  public void create(TemplateMeta meta )
      throws WorkflowException {
    try {
      TemplateBean bean = new TemplateBean();
      meta.setTemplateId(Sequence.fetch(Sequence.SEQ_TEMPLATE));
      if (bean.insert(unwrap(meta)) != 1)
        throw new WorkflowException(1015);
    } catch (SQLException sqle) {
      throw new WorkflowException(1016, sqle.toString());
    }
  }

  public TemplateMeta getTemplate(int templateId )
      throws WorkflowException {
    TemplateMeta result = new TemplateMeta();

    try {
      TemplateQuery query = new TemplateQuery();
      result = wrap(query.getTemplate(templateId));
    } catch (SQLException sqle) {
      throw new WorkflowException(1020, sqle.toString());
    }
    return result;
  }

  public void remove(int templateId) throws WorkflowException {
    if (hasInstance(templateId)) {
      throw new WorkflowException(10251);
    }

    Node nodeHandler = new Node();
    nodeHandler.removeByTemplate(templateId);

    State stateHandler = new State();
    stateHandler.removeByTemplate(templateId);

    Variable variableHandler = new Variable();
    variableHandler.removeByTemplate(templateId);

    try {
      TemplateBean bean = new TemplateBean();
      bean.delete( templateId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1026, sqle.toString());
    }
  }

  public void update(TemplateMeta meta)
      throws WorkflowException {
    int templateId = meta.getTemplateId();
    String currentIsActive = meta.getIsActive();
    String originalIsActive = getTemplate(templateId).getIsActive();
    if (originalIsActive.equals(TEMPLATE_ACTIVE)) {
      if (currentIsActive.equals(TEMPLATE_ACTIVE)) {
        clone(meta);
      } else if (currentIsActive.equals(TEMPLATE_INACTIVE)) {
        if (hasActiveInstance(templateId))
          throw new WorkflowException(10301);
        modify(meta);
      } else {
        throw new WorkflowException(1031);
      }
    } else if (originalIsActive.equals(TEMPLATE_INACTIVE)) {
      if (currentIsActive.equals(TEMPLATE_ACTIVE)) {
        /**
         * @todo Check whether the template is configured integratedly and
         *       properly.
         */
        modify(meta);
      } else if (currentIsActive.equals(TEMPLATE_INACTIVE)) {
        modify(meta);
      } else {
        throw new WorkflowException(1031);
      }
    } else {
      throw new WorkflowException(1032);
    }
  }

  private void modify(TemplateMeta meta)
      throws WorkflowException {
    try {
      TemplateBean bean = new TemplateBean();
      if (bean.update(unwrap(meta)) != 1)
        throw new WorkflowException(1030);
    } catch (SQLException sqle) {
      throw new WorkflowException(1033, sqle.toString());
    }
  }

  public void clone(TemplateMeta template)
      throws WorkflowException {
    int oldTemplateId = 0;
    int newTemplateId = 0;

    int[] oldStateIdArray;
    int[] newStateIdArray;

    int[] oldNodeIdArray;
    int[] newNodeIdArray;

    State stateHandler = new State();
    Variable variableHandler = new Variable();
    Node nodeHandler = new Node();
    NodeState nodeStateHandler = new NodeState();
    Executor executorHandler = new Executor();
    Link linkHandler = new Link();
    LinkState linkStateHandler = new LinkState();

    oldTemplateId = template.getTemplateId();
    template.setIsActive(TEMPLATE_INACTIVE);
    create(template);
    newTemplateId = template.getTemplateId();

    List stateList = stateHandler.getStateListByTemplate(oldTemplateId);

    oldStateIdArray = new int[stateList.size()];
    newStateIdArray = new int[stateList.size()];

    for (int i = 0; i < stateList.size(); i++) {
      State state = (State) stateList.get(i);
      oldStateIdArray[i] = state.getId();
      state.setTemplateId(newTemplateId);
      stateHandler.create(state);
      newStateIdArray[i] = state.getId();
    }

    List variableList = variableHandler.getVariableListByTemplate(
        oldTemplateId);
    for (int i = 0; i < variableList.size(); i++) {
      VariableMeta var = (VariableMeta) variableList.get(i);
      var.setTemplateId(newTemplateId);
      variableHandler.create(var);
    }

    List nodeList = nodeHandler.getNodeList(oldTemplateId);

    // init the nodes id Array
    oldNodeIdArray = new int[nodeList.size() + 2];
    newNodeIdArray = new int[nodeList.size() + 2];

    // init the start node id to Array
    oldNodeIdArray[0] = -1;
    newNodeIdArray[0] = -1;

    for (int nodeloop = 0; nodeloop < nodeList.size(); nodeloop++) {
      NodeMeta node = (NodeMeta) nodeList.get(nodeloop);
      oldNodeIdArray[nodeloop + 1] = node.getId();
      node.setTemplateId(newTemplateId);
      nodeHandler.create(node);
      newNodeIdArray[nodeloop + 1] = node.getId();
    }

    // init the end node id to Array
    oldNodeIdArray[oldNodeIdArray.length - 1] = -2;
    newNodeIdArray[newNodeIdArray.length - 1] = -2;

    for (int nodeloop = 1; nodeloop < oldNodeIdArray.length - 1; nodeloop++) {
      int oldNodeId = oldNodeIdArray[nodeloop];
      List nodeStateList = nodeStateHandler.getStateListByNode(oldNodeId);
      for (int nodeStateLoop = 0; nodeStateLoop < nodeStateList.size(); nodeStateLoop++) {
        NodeState nodeState = (NodeState) nodeStateList.get(nodeStateLoop);
        int oldStateId = nodeState.getStateId();
        int newStateId = 0;
        for (int index = 0; index < oldStateIdArray.length; index++) {
          if (oldStateId == oldStateIdArray[index]) {
            newStateId = newStateIdArray[index];
          }
        }
        nodeState.setStateId(newStateId);
        nodeStateHandler.create(nodeState);
      }
    }

    for (int nodeloop = 1; nodeloop < oldNodeIdArray.length - 1; nodeloop++) {
      executorHandler.clone(oldNodeIdArray[nodeloop], newNodeIdArray[nodeloop]);
    }

    for (int nodeloop = 0; nodeloop < oldNodeIdArray.length - 1; nodeloop++) {
      int oldPrecedingNodeId = oldNodeIdArray[nodeloop];
      int newPrecedingNodeId = newNodeIdArray[nodeloop];

      List linkList = linkHandler.getFollowedLinkList(oldTemplateId,
          oldPrecedingNodeId);
      for (int linkloop = 0; linkloop < linkList.size(); linkloop++) {
        Link link = (Link) linkList.get(linkloop);
        int oldLinkId = link.getId();

        int oldFollowedNodeId = link.getNextNodeId();
        int newFollowedNodeId = 0;
        for (int index = 0; index < oldNodeIdArray.length; index++) {
          if (oldFollowedNodeId == oldNodeIdArray[index]) {
            newFollowedNodeId = newNodeIdArray[index];
            break;
          }
        }

        link.setTemplateId(newTemplateId);
        link.setCurrentNodeId(newPrecedingNodeId);
        link.setNextNodeId(newFollowedNodeId);

        linkHandler.create(link);

        int newLinkId = link.getId();

        List linkStateList = linkStateHandler.getStateListByLink(oldLinkId);
        for (int linkStateLoop = 0; linkStateLoop < linkStateList.size(); linkStateLoop++) {
          LinkState linkState = (LinkState) linkStateList.get(linkStateLoop);
          int oldStateId = linkState.getStateId();
          int newStateId = 0;
          for (int index = 0; index < oldStateIdArray.length; index++) {
            if (oldStateId == oldStateIdArray[index]) {
              newStateId = newStateIdArray[index];
            }
          }
          linkState.setNodeLinkId(newLinkId);
          linkState.setStateId(newStateId);
          linkStateHandler.create(linkState);
        }
      }
    }
  }

  private boolean hasActiveInstance(int templateId)
      throws WorkflowException {
    boolean result = false;
    try {
      CountQuery query = new CountQuery();
      int num = query.getActiveInstanceNumByTemplate(templateId)
          .getCount().intValue();
      if (num != 0) {
        result = true;
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1100, sqle.toString());
    }
    return result;
  }

  private boolean hasInstance(int templateId)
      throws WorkflowException {
    boolean result = false;
    try {
      CountQuery query = new CountQuery();
      int num = query.getInstanceNumByTemplate(templateId).getCount()
          .intValue();
      if (num != 0) {
        result = true;
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1100, sqle.toString());
    }
    return result;
  }

  public List getActiveTemplateListByExecutor(String executor)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      TemplateQuery query = new TemplateQuery();
      String currentTime = DateTime.getSysTime();
      ArrayList list = query.getActiveTemplateListByExecutor(executor,currentTime);
      TemplateMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TemplateInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1035, sqle.toString());
    }
    return result;
  }

  public List getActiveTemplateListByExecutor(String executor,
      String templateType) throws WorkflowException {
    List result = new ArrayList();

    if (templateType == null || templateType.length() == 0)
      throw new WorkflowException(1035);

    try {
      TemplateQuery query = new TemplateQuery();
      String currentTime = DateTime.getSysTime();
      ArrayList list = query.getActiveTemplateListByExecutor(executor,
          templateType, currentTime);
      TemplateMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((TemplateInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1035, sqle.toString());
    }
    return result;
  }

  private TemplateMeta wrap(TemplateInfo model) {
    TemplateMeta wrapper = new TemplateMeta();
    if (model.getTemplateId() != null)
      wrapper.setTemplateId(model.getTemplateId().intValue());
    wrapper.setName(model.getName());
    wrapper.setDescription(model.getDescription());
    wrapper.setVersion(model.getVersion());
    wrapper.setStartTime(model.getStartTime());
    wrapper.setExpireTime(model.getExpireTime());
    wrapper.setCreateTime(model.getCreateTime());
    wrapper.setCreateStaffId(model.getCreateStaffId());
    wrapper.setIsActive(model.getIsActive());
    wrapper.setCreateStaffName(model.getCreateStaffName());
    wrapper.setTemplateType(model.getTemplateType());
    return wrapper;
  }

  private TemplateModel unwrap(TemplateMeta meta) {
    TemplateModel unwrapper = new TemplateModel();
    if (meta.getTemplateId() != 0)
      unwrapper.setTemplateId(meta.getTemplateId());
    if (meta.getName() != null)
      unwrapper.setName(meta.getName());
    if (meta.getDescription() != null)
      unwrapper.setDescription(meta.getDescription());
    if (meta.getVersion() != null)
      unwrapper.setVersion(meta.getVersion());
    if (meta.getStartTime() != null)
      unwrapper.setStartTime(meta.getStartTime());
    if (meta.getExpireTime() != null)
      unwrapper.setExpireTime(meta.getExpireTime());
    if (meta.getCreateTime() != null)
      unwrapper.setCreateTime(meta.getCreateTime());
    if (meta.getCreateStaffId() != null)
      unwrapper.setCreateStaffId(meta.getCreateStaffId());
    if (meta.getIsActive() != null)
      unwrapper.setIsActive(meta.getIsActive());
    if (meta.getTemplateType() != null)
      unwrapper.setTemplateType(meta.getTemplateType());
    return unwrapper;
  }
}
