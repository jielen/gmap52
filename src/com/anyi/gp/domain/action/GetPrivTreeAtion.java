package com.anyi.gp.domain.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.domain.RightService;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.xwork.ActionSupport;

public class GetPrivTreeAtion extends ActionSupport implements ServletRequestAware{

  private static final long serialVersionUID = 6157182699954458385L;

  private String roleId;
  
  private String userId;
  
  private String menuId;
  
  private RightService service;
  
  private HttpServletRequest request;
  
  public String getMenuId() {
    return menuId;
  }

  public void setMenuId(String menuId) {
    this.menuId = menuId;
  }

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setServletRequest(HttpServletRequest request) {
    this.request = request; 
  }
  
  public void setService(RightService service) {
    this.service = service;
  }
  
  public String execute() throws Exception {
    String svUserID = SessionUtils.getAttribute(request, "svUserID");

    if(userId != null && userId.trim().length() > 0){
      service.createUserPrivMenu(userId, menuId, svUserID);
    }
    else if(roleId != null && roleId.trim().length() > 0){
      service.createRolePrivMenu(roleId, menuId, svUserID);
    }
    return NONE;
  } 
}
