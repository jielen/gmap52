package com.anyi.gp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.desktop.Node;
import com.anyi.gp.desktop.Title;
import com.anyi.gp.desktop.Tree;
import com.anyi.gp.desktop.TreeBuilder;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.pub.TreeViewList;

public class PrivMenuControl{

  private String userId;

  private String roleId;

  private TreeBuilder treeBuilder;

  private HttpServletRequest request;
  
  private HttpServletResponse response;
  
  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public void setTreeBuilder(TreeBuilder treeBuilder) {
    this.treeBuilder = treeBuilder;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
    this.userId = request.getParameter("userId");
    if("null".equalsIgnoreCase(userId))
      userId = null;
    this.roleId = request.getParameter("roleId");
    if("null".equalsIgnoreCase(roleId))
      roleId = null;
  }

  public void setResponse(HttpServletResponse response){
    this.response = response;
  }
  
  public String generateMenuTree() throws Exception {
    Delta delta = new Delta();
    
    List groupList = null;
    if(userId != null && userId.length() > 0)
      groupList = GeneralFunc.getGroupByUserId(userId);    
    else
      groupList = GeneralFunc.getGroupByRoleId(roleId);
    
    for (int i = 0; i < groupList.size(); i++) {
      Group group = (Group) groupList.get(i);

      TableData groupData = new TableData();
      groupData.setField("CODE", group.getId());
      groupData.setField("NAME", group.getGroupName());
      groupData.setField("P_CODE", "");
      delta.add(groupData);
    }
    
    List pageList = GeneralFunc.getTitleByGroup(groupList);
    for (int j = 0; j < pageList.size(); j++) {
      Title title = (Title) pageList.get(j);

      TableData pageData = new TableData();
      pageData.setField("CODE", title.getTitleId());
      pageData.setField("NAME", title.getTitleName());
      pageData.setField("P_CODE", title.getGroupId());
      delta.add(pageData);
      
      Map params = new HashMap();
      params.put("rootCode", title.getTitleId());
      params.put("isRemoveEmpty", "true");
      params.put("userId", SessionUtils.getAttribute(request, "svUserID"));
      params.put("posiCode", SessionUtils.getAttribute(request, "svPoCode"));//增加职位过滤
      
      Tree tree = treeBuilder.generateTree(params);
      if(tree != null && tree.root != null){
        List treeChildren = tree.root.getChildren();
        for(int k = 0; k < treeChildren.size(); k++){
          tree.addToDelta(delta, (Node)treeChildren.get(k), true);
        }
      }
    }
    
    String result = null;
    if(response != null){
      result = new TreeViewList().getTreeWithRoot(delta, "管理权限", response.getWriter());
    }else{
      result = new TreeViewList().getTreeWithRoot(delta, "管理权限");
    }
    
    return result;
  }
}
