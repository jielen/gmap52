/* $Id: TreeViewList.java,v 1.7 2009/05/26 12:08:34 liuxiaoyong Exp $ */
package com.anyi.gp.pub;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.StringTools;

/**
 * @author   leidaohong
 */
public class TreeViewList {
  public String getTreeWithRoot(String ruleID, TableData entity, String rootCaption)
    throws BusinessException {
    ServiceFacade facade = (ServiceFacade)ApplusContext.getBean("serviceFacade");
    Delta delta = facade.getDBData(ruleID, entity);
    return getTreeWithRoot(delta, rootCaption);
  }

  public String getTreeWithRoot(Delta delta, String rootCaption) {
    if (delta.size() == 0)
      return "";
    Delta tree = addRoot(delta, rootCaption);
    return drawTree(tree, "root", false);
  }

  public String getTreeWithRoot(Delta delta, String rootCaption, Writer out) {
    if (delta.size() == 0)
      return null;
    Delta tree = addRoot(delta, rootCaption);
    try {
      this.printTree(tree, "root", false, out);
    } catch (IOException e) {
      e.printStackTrace();
      return "TreeViewList.getTreeWithRoot方法发生异常:" + e.getMessage();
    }
    return null;
  }

  private Delta addRoot(Delta delta, String rootCaption) {
    TableData root = new TableData();
    root.setField("CODE", "root");
    root.setField("NAME", rootCaption);
    root.setField("P_CODE", "");
    //处理创建树时，当所有叶子节点默认不选中时，根节点仍然被选中的错误 by mxl 2007.08.28
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
        TableData item = (TableData) iter.next();
        if (((String) item.getField("P_CODE")).equalsIgnoreCase("")) {
          item.setField("P_CODE", "root");
        }
      }
    if (isCheckTree(delta)) {
      root.setField("IS_CHECKED", isAllChecked(delta) ? "Y" : "N");
    }
    delta.add(root);
    return delta;
  }

  public void printTreeWithRoot(String ruleID, TableData entity, String rootCaption,
    boolean isAddCode, Writer out, String defaultMsg) throws IOException,
    BusinessException {
    ServiceFacade facade = (ServiceFacade)ApplusContext.getBean("serviceFacade");
    Delta delta = facade.getDBData(ruleID, entity);
    if (delta.size() == 0) {
      out.write(defaultMsg);
    }
    Delta tree = addRoot(delta, rootCaption);
    printTree(tree, "root", isAddCode, out);
  }

  public String getTreeWithRoot(Delta delta, String rootCaption, boolean isAddCode) {
    if (delta.size() == 0) {
      return "";
    }
    Delta tree = addRoot(delta, rootCaption);
    return drawTree(tree, "root", isAddCode);
  }

  private String createNbsp(int n) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < n; i++) {
      result.append("&nbsp;&nbsp;&nbsp;");
    }
    return result.toString();
  }

  private boolean isCheckTree(Delta delta) {
    TableData item = (TableData) delta.get(0);
    return item.getFieldNames().contains("IS_CHECKED");
  }

  private boolean isAllChecked(Delta delta) {
    boolean result = true;
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      TableData child = (TableData) iter.next();
      String p_code = (String) child.getField("P_CODE");
      if (p_code.equalsIgnoreCase("root")) { //根结点肯定不为空 by mxl 2007.08.28
        if (((String) child.getField("IS_CHECKED")).equalsIgnoreCase("n")) {
          result = false;
          break;
        }
      }
    }
    return result;
  }

  private String drawTree(Delta delta, String rootCode, boolean isAddCode) {
    if (delta.size() == 0)
      return "";
    isCheckTree = isCheckTree(delta);
    TableData rootItem = null;
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      rootItem = (TableData) iter.next();
      if (((String) rootItem.getField("CODE")).equalsIgnoreCase(rootCode)) {
        break;
      }
    }
    StringBuffer result = new StringBuffer();
    result.append("<span id=\"rootID\" value=\"" + rootCode + "\"></span>\n");
    result.append("<span id=\"currID\" value=\"" + rootCode + "\"></span>\n");
    result.append("<span id=fieldNames>\n");
    TableData firstRow = (TableData) delta.get(0);
    List fieldNames = firstRow.getFieldNames();
    for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
      String fieldName = iter.next().toString();
      if (fieldName.equalsIgnoreCase("code")
        || (fieldName.equalsIgnoreCase("p_code")
          || (fieldName.equalsIgnoreCase("name")) || (fieldName
          .equalsIgnoreCase("is_checked")))) {
        continue;
      }
      result.append("<span name=\"" + fieldName + "\"></span>\n");
    }
    result.append("</span>\n");
    parentNodes = getParentNodes(delta);
    result.append(nodeToHTML(delta, rootItem, 0, isAddCode));
    return result.toString();
  }

  private void printTree(Delta delta, String rootCode, boolean isAddCode, Writer out)
    throws IOException {
    isCheckTree = isCheckTree(delta);
    TableData rootItem = null;
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      rootItem = (TableData) iter.next();
      if (((String) rootItem.getField("CODE")).equalsIgnoreCase(rootCode)) {
        break;
      }
    }
    out.write("<span id=\"rootID\" value=\"" + rootCode + "\"></span>\n");
    out.write("<span id=\"currID\" value=\"" + rootCode + "\"></span>\n");
    out.write("<span id=fieldNames>\n");
    TableData firstRow = (TableData) delta.get(0);
    List fieldNames = firstRow.getFieldNames();
    for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
      String fieldName = iter.next().toString();
      if (fieldName.equalsIgnoreCase("code")
        || (fieldName.equalsIgnoreCase("p_code")
          || (fieldName.equalsIgnoreCase("name")) || (fieldName
          .equalsIgnoreCase("is_checked")))) {
        continue;
      }
      out.write("<span name=\"" + fieldName + "\"></span>\n");
    }
    out.write("</span>\n");
    parentNodes = getParentNodes(delta);
    printNode(delta, rootItem, 0, isAddCode, out);
  }

  /**
   * 增加通过 TableData的'NODE_ICON'字段自定义树节点图标功能 Add by chihf 2005/06/29
   * @param delta
   * @param item
   * @param deep
   * @param isAddCode
   * @return
   */
  private void printNode(Delta delta, TableData item, int deep, boolean isAddCode,
    Writer out) throws IOException {
    String title = getItemTitle(item);
    String code = (String) item.getField("CODE");
    String p_code = (String) item.getField("P_CODE");
    boolean checked = false;
    if (isCheckTree) {
      checked = ((String) item.getField("IS_CHECKED")).equalsIgnoreCase("y");
    }
    String icon = (String) item.getField("NODE_ICON");
    icon = StringTools.isEmptyString(icon) ? "" : "<img src=\"" + icon + "\">";
    boolean isLeaf = !hasChild(delta, item);
    String name = (String) item.getField("NAME");
    out.write(StringTools.getMargin(deep));
    out.write("&nbsp;<span id=\"" + code + "\"");
    out.write(additionalProperties(item));
    out.write(">\n");
    out.write(StringTools.getMargin(deep + 1));
    out.write(createNbsp(deep));
    out.write("\n");
    out.write(StringTools.getMargin(deep + 2));
    if (deep == 0) {
      out.write("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/star.gif\"  code=\"");
      out.write(code);
      out.write("\" id=\"IMG_");
      out.write(code);
      out.write("\" height=10 width=10 border=0>\n");
    } else if (isLeaf) {
      out.write("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/dot.gif\"  code=\"");
      out.write(code);
      out.write("\" id=\"IMG_");
      out.write(code);
      out.write("\" height=16 width=16 border=0>\n");
    } else {
      out.write("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/plus.gif\" code=\"");
      out.write(code);
      out.write("\" id=\"IMG_");
      out.write(code);
      out.write("\" height=16 width=16 border=0\n");
      out.write(" onclick=\"openBranch();\">\n");
    }
    if (isCheckTree) {
      out.write(StringTools.getMargin(deep + 2));
      out.write("<input type=\"checkbox\" style=\"CURSOR:hand\" id=\"");
      out.write(code);
      out.write("CHK\" code=\"");
      out.write(code);
      out.write("\" p_code=\"");
      out.write(p_code);
      out.write("\"");
      if (checked) {
        out.write(" checked");
      }
      out.write(" onclick=\"checkClick()\"></input>\n");
    }
    out.write(StringTools.getMargin(deep + 2));
    out.write(icon);
    if ((!isLeaf) && onlySelectLeaf) {
      out.write("<span class=\"clsTreeNode\" title=\"" + title + "\" id=\"");
      out.write(code);
      out.write("TXT\" value=\"");
      out.write(name);
      if (isAddCode)
        out.write("\" code=\"" + code + "\">" + "[" + code + "]" + name
          + "</span><br>\n");
      else
        out.write("\" code=\"" + code + "\">" + name + "</span><br>\n");
      out.write(StringTools.getMargin(deep + 1));
      out.write("</span>\n");
    } else {
      out.write("<span class=\"clsTreeNode\" title=\"" + title + "\" id=\"");
      out.write(code);
      out
        .write("TXT\" onclick=\"clickNode();\" onDblClick=\"dblClickNode();\" value=\"");
      out.write(name);
      if (isAddCode)
        out.write("\" code=\"" + code + "\">" + "[" + code + "]" + name
          + "</span><br>\n");
      else
        out.write("\" code=\"" + code + "\">" + name + "</span><br>\n");
      out.write(StringTools.getMargin(deep + 1));
      out.write("</span>\n");
    }
    // ----------------------------------------------------------
    if (!isLeaf) {
      out.write(StringTools.getMargin(deep + 1));
      out.write("<span id=\"");
      out.write(code);
      out.write("Child\"");
      if (deep != 0) {
        out.write(" style=\"DISPLAY: none\"");
      }
      out.write(" folder=\"Y\" changed=\"N\">\n");
      for (Iterator iter = delta.iterator(); iter.hasNext();) {
        TableData child = (TableData) iter.next();
        if (child.getFieldNames().isEmpty()) {
          continue;
        }
        p_code = (String) child.getField("P_CODE");
        if (code.equalsIgnoreCase(p_code)) {
          printNode(delta, child, deep + 1, isAddCode, out);
        }
      }
      out.write(StringTools.getMargin(deep + 1));
      out.write("</span>\n");
    }
  }

  private Set getParentNodes(Delta delta) {
    Set result = new HashSet();
    Map tmp2 = new HashMap();
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      TableData o = (TableData) iter.next();
      tmp2.put(o.getField("CODE"), o);
    }

    // 对于 40101 类科目的提示,上级为 401; 此时没有 root;在此重新发现 root; leidh;20040509;
    Object vTableData = null;
    boolean vIsFoundRoot = false;

    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      TableData o = (TableData) iter.next();
      Object p_code = o.getField("P_CODE");
      // result.add(tmp2.get(p_code));

      // 对于 40101 类科目的提示,上级为 401; 此时没有 root;在此重新发现 root; leidh;20040509;
      vTableData = tmp2.get(p_code);
      if (vTableData == null && vIsFoundRoot == false && p_code != null
        && (String.valueOf(p_code)).trim().length() > 0) {
        o.setField("P_CODE", "root");
        p_code = o.getField("P_CODE");
        vTableData = tmp2.get(p_code);
      }

      result.add(vTableData);
    }
    return result;
  }

  private boolean hasChild(Delta delta, TableData item) {
    return parentNodes.contains(item);
  }

  /**
   * 增加通过 TableData的'NODE_ICON'字段自定义树节点图标功能 Add by chihf 2005/06/29
   * @param delta
   * @param item
   * @param deep
   * @param isAddCode
   * @return
   */
  private String nodeToHTML(Delta delta, TableData item, int deep, boolean isAddCode) {
    String title = getItemTitle(item);
    String code = (String) item.getField("CODE");
    String p_code = (String) item.getField("P_CODE");
    boolean checked = false;
    if (isCheckTree) {
      checked = ((String) item.getField("IS_CHECKED")).equalsIgnoreCase("y");
    }
    String icon = (String) item.getField("NODE_ICON");
    icon = StringTools.isEmptyString(icon) ? "" : "<img src=\"" + icon + "\">";
    boolean isLeaf = !hasChild(delta, item);
    String name = (String) item.getField("NAME");
    StringBuffer result = new StringBuffer();
    result.append(StringTools.getMargin(deep));
    result.append("&nbsp;<span id=\"" + code + "\"");
    result.append(additionalProperties(item));
    result.append(">\n");
    result.append(StringTools.getMargin(deep + 1));
    result.append(createNbsp(deep));
    result.append("\n");
    result.append(StringTools.getMargin(deep + 2));
    if (deep == 0) {
      result.append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/star.gif\"  code=\"");
      result.append(code);
      result.append("\" id=\"IMG_");
      result.append(code);
      result.append("\" height=10 width=10 border=0>\n");
    } else if (isLeaf) {
      result.append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/dot.gif\"  code=\"");
      result.append(code);
      result.append("\" id=\"IMG_");
      result.append(code);
      result.append("\" height=16 width=16 border=0>\n");
    } else {
      result.append("<img src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/main/plus.gif\" code=\"");
      result.append(code);
      result.append("\" id=\"IMG_");
      result.append(code);
      result.append("\" height=16 width=16 border=0\n");
      result.append(" onclick=\"openBranch();\">\n");
    }
    if (isCheckTree) {
      result.append(StringTools.getMargin(deep + 2));
      result.append("<input type=\"checkbox\" style=\"CURSOR:hand\" id=\"");
      result.append(code);
      result.append("CHK\" code=\"");
      result.append(code);
      result.append("\" p_code=\"");
      result.append(p_code);
      result.append("\"");
      if (checked) {
        result.append(" checked");
      }
      result.append(" onclick=\"checkClick()\"></input>\n");
    }
    result.append(StringTools.getMargin(deep + 2));
    result.append(icon);
    if ((!isLeaf) && onlySelectLeaf) {
      result.append("<span class=\"clsTreeNode\" title=\"" + title + "\" id=\"");
      result.append(code);
      result.append("TXT\" value=\"");
      result.append(name);
      if (isAddCode)
        result.append("\" code=\"" + code + "\">" + "[" + code + "]" + name
          + "</span><br>\n");
      else
        result.append("\" code=\"" + code + "\">" + name + "</span><br>\n");
      result.append(StringTools.getMargin(deep + 1));
      result.append("</span>\n");
    } else {
      result.append("<span class=\"clsTreeNode\" title=\"" + title + "\" id=\"");
      result.append(code);
      result
        .append("TXT\" onclick=\"clickNode();\" onDblClick=\"dblClickNode();\" value=\"");
      result.append(name);
      if (isAddCode)
        result.append("\" code=\"" + code + "\">" + "[" + code + "]" + name
          + "</span><br>\n");
      else
        result.append("\" code=\"" + code + "\">" + name + "</span><br>\n");
      result.append(StringTools.getMargin(deep + 1));
      result.append("</span>\n");
    }
    // ----------------------------------------------------------
    if (!isLeaf) {
      result.append(StringTools.getMargin(deep + 1));
      result.append("<span id=\"");
      result.append(code);
      result.append("Child\"");
      if (deep != 0) {
        result.append(" style=\"DISPLAY: none\"");
      }
      result.append("	folder=\"Y\" changed=\"N\">\n");
      for (Iterator iter = delta.iterator(); iter.hasNext();) {
        TableData child = (TableData) iter.next();
        if (child.getFieldNames().isEmpty()) {
          continue;
        }
        p_code = (String) child.getField("P_CODE");
        if (code.equalsIgnoreCase(p_code)) {
          result.append(nodeToHTML(delta, child, deep + 1, isAddCode));
        }
      }
      result.append(StringTools.getMargin(deep + 1));
      result.append("</span>\n");
    }
    return result.toString();
  }

  private String additionalProperties(TableData item) {
    item.deleteField("CODE");
    item.deleteField("NAME");
    if (isCheckTree) {
      item.deleteField("IS_CHECKED");
    }
    StringBuffer result = new StringBuffer();
    List fieldNames = item.getFieldNames();
    for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      result.append(" " + fieldName + "=\"" + item.getField(fieldName) + "\"");
      item.deleteField(fieldName);
    }
    return result.toString();
  }

  private String getItemTitle(TableData item) {
    StringBuffer result = new StringBuffer();
    List fieldNames = item.getFieldNames();
    for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
      String fieldName = (String) iter.next();
      if (fieldName.equalsIgnoreCase("CODE") || fieldName.equalsIgnoreCase("NAME")
        || fieldName.equalsIgnoreCase("IS_CHECKED")
        || fieldName.equalsIgnoreCase("P_CODE"))
        continue;
      result.append(" " + lang.getLang(fieldName) + ":" + item.getField(fieldName)
        + "\n");
    }
    return result.toString();
  }

  public String getTree(String ruleID, TableData entity) throws BusinessException {
    ServiceFacade facade = (ServiceFacade)ApplusContext.getBean("serviceFacade");
    Delta delta = facade.getDBData(ruleID, entity);
    return getTree(delta);
  }

  public String getTree(Delta delta) {
    if (delta.size() == 0)
      return "";
    String rootCode = null;
    for (Iterator iter = delta.iterator(); iter.hasNext();) {
      TableData item = (TableData) iter.next();
      if (((String) item.getField("P_CODE")).equalsIgnoreCase("")) {
        rootCode = (String) item.getField("CODE");
        break;
      }
    }
    if (rootCode == null) {
      return getTreeWithRoot(delta, "根结点");
    }
    return drawTree(delta, rootCode, false);
  }

  public String getTreeByRoot(String ruleID, TableData entity, String rootCode)
    throws BusinessException {
    ServiceFacade facade = (ServiceFacade)ApplusContext.getBean("serviceFacade");
    Delta delta = facade.getDBData(ruleID, entity);
    return getTreeByRoot(delta, rootCode);
  }

  public String getTreeByRoot(Delta delta, String rootCode) {
    if (delta.size() == 0)
      return "";
    Delta subTree = getSubTreeData(delta, rootCode);
    return drawTree(subTree, rootCode, false);
  }

  private Delta getSubTreeData(Delta data, String rootCode) {
    Delta result = new Delta();
    for (Iterator iter = data.iterator(); iter.hasNext();) {
      TableData item = (TableData) iter.next();
      if (((String) item.getField("CODE")).equalsIgnoreCase(rootCode)) {
        result.add(item);
      } else if (((String) item.getField("P_CODE")).equalsIgnoreCase(rootCode)) {
        result.addAll(getSubTreeData(data, (String) item.getField("CODE")));
      }
    }
    return result;
  }

  // -- add by wunianyang -----------------------
  /**
   * @param onlySelectLeaf   The onlySelectLeaf to set.
   * @uml.property   name="onlySelectLeaf"
   */
  public void setOnlySelectLeaf(boolean is) {
    this.onlySelectLeaf = is;
  }

  // ---------------------------------------------

  private Set parentNodes = null;

  private boolean isCheckTree = false;

  private boolean onlySelectLeaf = false;// add by wunianyang

  private LangResource lang = LangResource.getInstance();

}
