package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingdrive.workflow.access.LinkBean;
import com.kingdrive.workflow.dto.VariableValueMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.LinkModel;
import com.kingdrive.workflow.util.Sequence;
import com.kingdrive.workflow.util.SimpleExpressionParser;
import com.kingdrive.workflow.util.StringUtil;

/**
 * 流向
 * 
 * @todo 新增修改流向时,检查前置后续节点的执行方式为独签
 */
public class Link implements Serializable, Comparable {

  private int id;

  private String name;

  private String description;

  private String type;

  private int templateId;

  private int currentNodeId;

  private int nextNodeId;

  private String executorRelation;

  private String executorsMethod;

  private String numberOrPercent;

  private int passValue;

  private String expression;

  private String isDefault;

  private String actionName;

  /* 提交者与执行者关系 */
  public static final String EXECUTOR_RELATION_NONE = "0";// 无关系

  public static final String EXECUTOR_RELATION_MANAGER = "1";// 组织上级

  public static final String EXECUTOR_RELATION_SELF = "2";// 自己

  public static final String EXECUTOR_RELATION_BUSINESS_SUPPERIOR = "3";//业务上机

  public static final String NUMBERORPERCENT_NUMBER = "0";// 单位是数量

  public static final String NUMBERORPERCENT_PERCENT = "1";// 单位是百分比

  public static final String TYPE_ORDINARY = "0";// 普通类型

  public static final String TYPE_RETURN = "1";// 结束类型
    public static final String TYPE_COLLECT = "2";

  public static final String PATH_NOT_DEFAULT = "0";

  public static final String PATH_DEFAULT = "1";


  public Link() {
    type = "0";
    executorRelation = "0";
    executorsMethod = "Z";
    numberOrPercent = "0";
    isDefault = "0";
  }

  public int compareTo(Object obj) {
    Link link = (Link) obj;
    if (currentNodeId != link.currentNodeId)
      return currentNodeId - link.currentNodeId;
    return nextNodeId - link.nextNodeId;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Link))
      return false;
    Link o = (Link) obj;
    return o.id == id;
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

  public int getCurrentNodeId() {
    return currentNodeId;
  }

  public void setCurrentNodeId(int currentNodeId) {
    this.currentNodeId = currentNodeId;
  }

  public int getNextNodeId() {
    return nextNodeId;
  }

  public void setNextNodeId(int nextNodeId) {
    this.nextNodeId = nextNodeId;
  }

  public String getExecutorRelation() {
    return executorRelation;
  }

  public void setExecutorRelation(String executorRelation) {
    this.executorRelation = executorRelation;
  }

  public String getExecutorsMethod() {
    return executorsMethod;
  }

  public void setExecutorsMethod(String executorsMethod) {
    this.executorsMethod = executorsMethod;
  }

  public String getNumberOrPercent() {
    return numberOrPercent;
  }

  public void setNumberOrPercent(String numberOrPercent) {
    this.numberOrPercent = numberOrPercent;
  }

  public int getPassValue() {
    return passValue;
  }

  public void setPassValue(int passValue) {
    this.passValue = passValue;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public String getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(String isDefault) {
    this.isDefault = isDefault;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getActionName() {
    return actionName;
  }

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  public List getLinkList(int templateId)
      throws WorkflowException {
    List result = new ArrayList();
    try {
      LinkBean bean = new LinkBean();
      ArrayList list = bean.getLinkList(templateId);
      Link link = null;
      for (int i = 0; i < list.size(); i++, result.add(link)) {
        link = wrap((LinkModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2009, e.toString());
    }
    Collections.sort(result);
    return result;
  }

  public List getFollowedLinkList(int templateId, int nodeId)
      throws WorkflowException {
    List result = new ArrayList();
    try {
      LinkBean bean = new LinkBean();
      ArrayList list = bean.getFollowedLinkList(templateId, nodeId);
      Link link = null;
      for (int i = 0; i < list.size(); i++, result.add(link)) {
        link = wrap((LinkModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2009, e.toString());
    }
    Collections.sort(result);
    return result;
  }

  public List getFollowedLinkList(int templateId, int nodeId, String action) throws WorkflowException {
    List result = new ArrayList();
    //取得该节点所有后续Link
    List followedLinkList = getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < followedLinkList.size(); i++) {
      Link link = (Link) followedLinkList.get(i);
      //取其中action符合的link，一般只会有一个
      //参数action==null时，以及action!=null 并且和link的actionName相等时添加
      if (null==action || action.equals("") ||
        (null!=action &&action.equals(link.getActionName()))) {
        //会有多个link拥有相同的ActionName吗？一般不可能
        result.add(link);
      }
    }
    return result;
  }
  
  public Link getLinkListByAction(int templateId, String action) throws WorkflowException {
    Link result = null;
    //取得该节点所有后续Link
    List linkList = getLinkList(templateId);
    for (int i = 0; i < linkList.size(); i++) {
      Link link = (Link) linkList.get(i);
      if (null!=action &&action.equals(link.getActionName())) {
        result = link;
        break;
      }
    }
    return result;
  }
  
  public Map getFollowedLinkActionMap(int templateId, int nodeId) throws WorkflowException {
    Map result = new HashMap();

    List followedLinkList = getFollowedLinkList(templateId, nodeId);
    for (int i = 0; i < followedLinkList.size(); i++) {
      Link link = (Link) followedLinkList.get(i);
      String action = link.getActionName();
      List list = (List) result.get(action);
      if (list == null) {
        list = new ArrayList();
        list.add(link);
        result.put(action, list);
      } else {
        list.add(link);
      }
    }

    return result;
  }

  public List getPrecedingLinkList(int templateId, int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      LinkBean bean = new LinkBean();
      ArrayList list = bean.getPrecedingLinkList(templateId, nodeId);
      Link link = null;
      for (int i = 0; i < list.size(); i++, result.add(link)) {
        link = wrap((LinkModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2011, e.toString());
    }
    Collections.sort(result);
    return result;
  }

  public void create(Link link) throws WorkflowException {
    try {
      LinkBean dao = new LinkBean();
      link.setId(Sequence.fetch(Sequence.SEQ_LINK));
      if (dao.insert(unwrap(link)) != 1)
        throw new WorkflowException(2012);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByTemplate(int templateId)
      throws WorkflowException {
    LinkState linkStateHandler = new LinkState();
    linkStateHandler.removeByTemplate(templateId);

    try {
      LinkBean bean = new LinkBean();
      bean.removeByTemplate(templateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByNode(int nodeId)
      throws WorkflowException {
    LinkState linkStateHandler = new LinkState();
    linkStateHandler.removeByNode(nodeId);

    try {
      LinkBean bean = new LinkBean();
      bean.removeByNode(nodeId, nodeId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void remove(int linkId) throws WorkflowException {
    LinkState linkStateHandler = new LinkState();
    linkStateHandler.removeByLink(linkId);

    try {
      LinkBean bean = new LinkBean();
      bean.delete(linkId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public Link getLink(int linkId) throws WorkflowException {
    Link result = null;
    try {
      LinkBean bean = new LinkBean();
      LinkModel model = bean.findByKey(linkId);
      result = wrap(model);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }
  public Link getLink(int activityId,int nextActivityId) throws WorkflowException {
    Link result = null;
    try {
      LinkBean bean = new LinkBean();
      LinkModel model = bean.findByActivityKey(activityId,nextActivityId);
      result = wrap(model);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }
  /**
   * get the node's right immediate followed navigation Link
   * 根据变量值计算出合适的Link
   * @param templateId
   *          int
   * @param nodeId
   *          int
   * @param valueList
   *          List
   * @param conn
   *          Connection
   * @throws WorkflowException
   * @return NodeLink
   */
  
  //TODO：只计算Link上的条件，不计算分支(路由)节点上的逻辑
  //TODO：难道分支节点上没有逻辑？不用计算？
  public Link getRIFNavigationLink(int templateId, int nodeId, List valueList) throws WorkflowException {
    Link result = null;
    //根据模板取得该节点所有后续Link
    List nodeLinkList = getFollowedLinkList(templateId, nodeId);    
    for (int i = 0; i < nodeLinkList.size(); i++) {
      Link link = (Link) nodeLinkList.get(i);
      //如果是默认路径(PATH_DEFAULT),则立即返回它。正确吗？
      //TODO：对默认路径的理解不同
      if (link.getIsDefault().equals(Link.PATH_DEFAULT)) {
        result = link;
      }
      //取得该Link的表达式
      String expression = link.getExpression();
      if(null==expression || "".equals(expression)){//如果没有表达式,视为无条件,可以通过
        result = link;
        break;
      }
      for (int j = 0; null!=valueList && j < valueList.size(); j++) {
        VariableValueMeta value = (VariableValueMeta) valueList.get(j);
        //用值代替变量名
        expression = StringUtil.replaceString(expression, value.getName(),
            value.getValue(), true);
      }

      try {
        //表达式值为true.找到第一个符合条件的Link，并返回它
        if (SimpleExpressionParser.shortCircuitExpressionParse(expression)) {
          result = link;
          break;
        }
      } catch (Exception e) {
        throw new WorkflowException(1230);
      }
    }

    return result;
  }

  public void update() throws WorkflowException {
    try {
      LinkBean bean = new LinkBean();
      if (bean.update(unwrap(this)) != 1)
        throw new WorkflowException(2013);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void setLinkExpression(int linkId, String expression,
      String isDefault) throws WorkflowException {
    Link link = getLink(linkId);
    if (link == null)
      throw new WorkflowException(2024, "流向不存在");
    link.setExpression(expression);
    link.setIsDefault(isDefault);
    if (isDefault.equals("1")) {
      LinkBean bean = new LinkBean();
      try {
        bean.removeDefaultPath(link.getCurrentNodeId());
      } catch (SQLException e) {
        throw new WorkflowException(2000, e.toString());
      }
    }
    link.update();
  }

  private Link wrap(LinkModel model) {
    Link link = new Link();
    if (model.getNodeLinkId() != null)
      link.setId(model.getNodeLinkId().intValue());
    link.setName(model.getName());
    link.setDescription(model.getDescription());
    link.setType(model.getLinkType());
    if (model.getTemplateId() != null)
      link.setTemplateId(model.getTemplateId().intValue());
    if (model.getCurrentNodeId() != null)
      link.setCurrentNodeId(model.getCurrentNodeId().intValue());
    if (model.getNextNodeId() != null)
      link.setNextNodeId(model.getNextNodeId().intValue());
    link.setExecutorRelation(model.getExecutorRelation());
    link.setExecutorsMethod(model.getExecutorsMethod());
    link.setNumberOrPercent(model.getNumberOrPercent());
    if (model.getPassValue() != null)
      link.setPassValue(model.getPassValue().intValue());
    link.setExpression(model.getExpression());
    link.setIsDefault(model.getDefaultPath());
    link.setActionName(model.getActionName());
    return link;
  }

  private LinkModel unwrap(Link link) {
    LinkModel model = new LinkModel();
    if (link.getId() != 0)
      model.setNodeLinkId(link.getId());
    if (link.getName() != null)
      model.setName(link.getName());
    if (link.getDescription() != null)
      model.setDescription(link.getDescription());
    if (link.getType() != null)
      model.setLinkType(link.getType());
    if (link.getTemplateId() != 0)
      model.setTemplateId(link.getTemplateId());
    if (link.getCurrentNodeId() != 0)
      model.setCurrentNodeId(link.getCurrentNodeId());
    if (link.getNextNodeId() != 0)
      model.setNextNodeId(link.getNextNodeId());
    if (link.getExecutorRelation() != null)
      model.setExecutorRelation(link.getExecutorRelation());
    if (link.getExecutorsMethod() != null)
      model.setExecutorsMethod(link.getExecutorsMethod());
    if (link.getNumberOrPercent() != null)
      model.setNumberOrPercent(link.getNumberOrPercent());
    if (link.getPassValue() != 0)
      model.setPassValue(link.getPassValue());
    if (link.getExpression() != null)
      model.setExpression(link.getExpression());
    if (link.getIsDefault() != null)
      model.setDefaultPath(link.getIsDefault());
    if (link.getActionName() != null)
      model.setActionName(link.getActionName());
    return model;
  }
}
