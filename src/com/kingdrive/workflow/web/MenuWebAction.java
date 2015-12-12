package com.kingdrive.workflow.web;

import java.util.List;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.business.Link;
import com.kingdrive.workflow.business.State;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.StringUtil;
import com.kingdrive.workflow.util.TreeNode;

public class MenuWebAction extends WebAction {

  private static final String TOKEN_ROOT = "RT";

  private static final String TOKEN_TEMPLATE = "TPL";

  private static final String TOKEN_VARIABLE = "VAR";

  private static final String TOKEN_STATE = "STA";

  private static final String TOKEN_NODE = "NOD";

  private static final String TOKEN_LINK = "LNK";

  private static final String TOKEN_SEPARATE = ">";

  private String line = "\n";

  private String id = null;

  public MenuWebAction() {
  }

  public String perform(String event) throws GeneralException {
    if ("createmenu".equals(event)) {
      try {
        id = StringUtil.string2string((String) tps.get(SessionManager.PDS,
            "TREE_NODE_ID"));
        TreeNode tree = buildTree();
        String html = getHTML(tree, 0);
        // System.out.println(html);
        tps.put(SessionManager.TDS, "treeTable", html);
      } catch (WorkflowException wfe) {
        throw new GeneralException(wfe.toString());
      }
      return null;
    }
    throw new GeneralException("未知功能。");
  }

  private String getIndent(int level) {
    String blank = "";
    for (int i = 0; i < level; i++)
      blank = blank.concat("  ");

    return blank;
  }

  private String getImg(String id, String img) {
    StringBuffer result = new StringBuffer("");

    int size = img.equals("dot.gif") ? 8 : 10;

    result.append("<img id=\"").append(id).append("IMG\"");
    result.append(" width=\"").append(size).append("\"");
    result.append(" height=\"").append(size).append("\"");
    result.append(" src=\"./images/").append(img).append("\"");
    result.append(" onclick=\"javascript : return expand();\"");
    result.append(">");

    return result.toString();
  }

  private String getHTML(TreeNode node, int level) {
    StringBuffer result = new StringBuffer("");

    boolean expanded = expanded(id, node.getId());

    result
        .append(getIndent(level))
        .append(
            "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
        .append(line);
    if (level > 0) {
      result.append(getIndent(level + 1)).append("<tr><td>");
      for (int i = 0; i < level - 1; i++) {
        result.append("&nbsp;");
      }
      if (node.hasChild()) {
        if (expanded) {
          result.append(getImg(node.getId(), "minus.gif"));
        } else {
          result.append(getImg(node.getId(), "plus.gif"));
        }
      } else {
        result.append(getImg(node.getId(), "dot.gif"));
      }

      result.append("<span id=\"").append(node.getId()).append("SPAN\"");
      if (node.getURL() != null) {
        result.append(" onclick=\"javascript : return doAction(\'").append(
            node.getURL()).append("\');\"");
      }
      result.append(">");
      result.append("<font id=\"").append(node.getId()).append(
          "\" size=\"2.5\">").append(node.getName()).append("</font>");
      result.append("</span>");

      result.append("</td></tr>").append(line);
    }

    if (node.hasChild()) {
      result.append(getIndent(level + 1)).append("<tr id=\"").append(
          node.getId().concat("SUB\""));
      if (!expanded) {
        result.append(" style=\"display:none\"");
      }
      result.append(">");
      result.append("<td>").append(line);

      List children = node.getChildren();
      for (int i = 0; i < children.size(); i++) {
        result.append(getHTML((TreeNode) children.get(i), level + 1));
      }
      result.append(line);
      result.append(getIndent(level + 2)).append("</td>").append(line);
      result.append(getIndent(level + 1)).append("</tr>").append(line);
    }

    result.append(getIndent(level)).append("</table>").append(line);

    return result.toString();
  }

  private TreeNode buildTree() throws WorkflowException {

    TreeNode root = new TreeNode(TOKEN_ROOT, "控制中心");

    root.add(buildTemplateTree(TOKEN_ROOT));

    // root.add(buildStructure(TOKEN_ROOT));

    return root;
  }

  private TreeNode buildTemplateTree(String parentUniqueId)
      throws WorkflowException {
    String prefix = getUnique(parentUniqueId, TOKEN_TEMPLATE);

    TreeNode main = new TreeNode(prefix, "流程");
    main.setURL("addtemplate.do");

    List templateList = ConfigureFacade.getTemplateList();
    for (int i = 0; i < templateList.size(); i++) {
      TemplateMeta t = (TemplateMeta) templateList.get(i);

      String uniqueId = getUnique(prefix, t.getTemplateId());
      TreeNode item = new TreeNode(uniqueId, t.getName().concat("(").concat(
          t.getVersion()).concat(")"));
      item.setURL("viewtemplate.do?templateid=".concat(String.valueOf(t
          .getTemplateId())));

      item.add(buildVariableTree(uniqueId, t.getTemplateId()));

      item.add(buildStateTree(uniqueId, t.getTemplateId()));

      item.add(buildNodeTree(uniqueId, t.getTemplateId()));

      main.add(item);
    }

    return main;
  }

  private TreeNode buildNodeTree(String parentUniqueId, int templateId)
      throws WorkflowException {
    String prefix = getUnique(parentUniqueId, TOKEN_NODE);

    TreeNode main = new TreeNode(prefix, "节点");
    main.setURL("addnode.do?templateid=".concat(String.valueOf(templateId)));

    TreeNode itemStart = new TreeNode(getUnique(prefix, -1), "开始节点");
    List beginLink = ConfigureFacade.getFollowedLinkList(templateId, -1);
    if (beginLink == null || beginLink.size() == 0) {
      itemStart.setURL("addlink.do?templateid=".concat(
          String.valueOf(templateId)).concat("&nodeid=-1"));
    } else {
      Link bnl = (Link) beginLink.get(0);
      itemStart.setURL("modifylink.do?templateid=".concat(
          String.valueOf(templateId)).concat("&nodeid=-1").concat("&linkid=")
          .concat(String.valueOf(bnl.getId())));

    }
    main.add(itemStart);

    List nodeList = ConfigureFacade.getNodeList(templateId);
    for (int i1 = 0; i1 < nodeList.size(); i1++) {
      NodeMeta meta = (NodeMeta) nodeList.get(i1);

      String uniqueId = getUnique(prefix, meta.getId());

      TreeNode item = new TreeNode(uniqueId, meta.getName());
      item.setURL("modifynode.do?templateid="
          .concat(String.valueOf(templateId)).concat("&nodeid=").concat(
              String.valueOf(meta.getId())));

      item.add(buildLinkTree(uniqueId, templateId, meta.getId()));

      main.add(item);
    }

    TreeNode itemEnd = new TreeNode(getUnique(prefix, -2), "结束节点");
    main.add(itemEnd);

    return main;
  }

  private TreeNode buildLinkTree(String parentUniqueId, int templateId,
      int nodeId) throws WorkflowException {
    String prefix = getUnique(parentUniqueId, TOKEN_LINK);

    TreeNode main = new TreeNode(prefix, "流向");
    main.setURL("addlink.do?templateid=".concat(String.valueOf(templateId))
        .concat("&nodeid=").concat(String.valueOf(nodeId)));

    List linkList = ConfigureFacade.getFollowedLinkList(templateId, nodeId);
    for (int i2 = 0; i2 < linkList.size(); i2++) {
      Link meta = (Link) linkList.get(i2);

      String uniqueId = getUnique(prefix, meta.getId());

      TreeNode item = new TreeNode(uniqueId, meta.getName());
      item.setURL("modifylink.do?templateid="
          .concat(String.valueOf(templateId)).concat("&nodeid=").concat(
              String.valueOf(nodeId).concat("&linkid=").concat(
                  String.valueOf(meta.getId()))));

      main.add(item);
    }

    return main;
  }

  private TreeNode buildStateTree(String parentUniqueId, int templateId)
      throws WorkflowException {
    String prefix = getUnique(parentUniqueId, TOKEN_STATE);

    TreeNode main = new TreeNode(prefix, "状态");
    main.setURL("addstate.do?templateid=".concat(String.valueOf(templateId)));

    List stateList = ConfigureFacade.getStateList(templateId);
    for (int i = 0; i < stateList.size(); i++) {
      State meta = (State) stateList.get(i);

      String uniqueId = getUnique(prefix, meta.getId());

      TreeNode item = new TreeNode(uniqueId, meta.getName());
      item.setURL("modifystate.do?templateid=".concat(
          String.valueOf(templateId)).concat("&stateid=").concat(
          String.valueOf(meta.getId())));

      main.add(item);
    }

    return main;
  }

  private TreeNode buildVariableTree(String parentUniqueId, int templateId)
      throws WorkflowException {
    String prefix = getUnique(parentUniqueId, TOKEN_VARIABLE);

    TreeNode main = new TreeNode(prefix, "变量");
    main
        .setURL("addvariable.do?templateid=".concat(String.valueOf(templateId)));

    List variableList = ConfigureFacade.getVariableList(templateId);
    for (int i = 0; i < variableList.size(); i++) {
      VariableMeta meta = (VariableMeta) variableList.get(i);

      String uniqueId = getUnique(prefix, meta.getId());

      TreeNode item = new TreeNode(uniqueId, meta.getName());
      item.setURL("modifyvariable.do?templateid=".concat(
          String.valueOf(templateId)).concat("&varid=").concat(
          String.valueOf(meta.getId())));

      main.add(item);
    }

    return main;
  }

  private String getUnique(String prefix, String id) {
    return prefix.concat(TOKEN_SEPARATE).concat(id);
  }

  private String getUnique(String prefix, int id) {
    return getUnique(prefix, String.valueOf(id));
  }

  private boolean expanded(String id, String nodeId) {
    boolean result = false;

    if ((id == null || id.equals(""))) {
      if (nodeId.equals(TOKEN_ROOT)
          || nodeId.equals(getUnique(TOKEN_ROOT, TOKEN_TEMPLATE))) {
        result = true;
      } else {
        result = false;
      }
    } else {
      result = id.indexOf(nodeId) > -1;
    }
    return result;
  }
}
