package com.anyi.gp.core.bean;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.pub.SessionUtils;

public class PageRequestInfo {

  protected static final String NEW_LINE = System.getProperty("line.separator");

  protected static final String AS_FOREIGN_FOR_SQL = "AS_FOREIGN_FOR_SQL";
  
  protected String userId;

  protected String compoName;

  protected String lang;

  protected List calls;

  protected String tableName;
 
  protected LangResource resource;

  protected HttpServletRequest request;

  public PageRequestInfo(){
  }

  /**
 * 设置请求对象
 * @param newRequest   请求对象
 * @uml.property   name="request"
 */
  protected void setRequest(HttpServletRequest newRequest){
    request = newRequest;
  }

  /**
   * 初始化Bean
   */
  protected void beanInit(){
  	if(request == null)
  		return;
  	
    setCompoName();
    setTableName();
    setUser();
    setLang();
    setResource();
    setCalls();
  }

  /**
   * 设置部件名
   */
  protected void setCompoName(){
  	if(request.getParameter("componame") != null){
  		compoName = request.getParameter("componame");
  	}
  	else if(request.getAttribute("componame") != null){
      compoName = (String)request.getAttribute("componame");
    } else{
      throw new IllegalArgumentException("生成选择页面的请求参数中没有componame属性。");
    }
  }

  protected void setTableName(){
    if(!AS_FOREIGN_FOR_SQL.equalsIgnoreCase(compoName))
      this.tableName = MetaManager.getCompoMeta(compoName).getMasterTable();
  }
  
  /**
   * 设置用户名
   */
  protected void setUser(){
    this.userId = SessionUtils.getAttribute(request, "svUserID");
  }

  /**
   * 设置语言种类
   */
  protected void setLang(){
    lang = SessionUtils.getAttribute(request, "lang");
    if(lang == null)
    	lang = "C";
  }

  /**
   * 设置语言资源
   */
  protected void setResource(){
    resource = LangResource.getInstance();
  }

  /**
   * 设置方法列表
   */
  protected void setCalls(){
  	// TCJLODO:
  	this.calls = new ArrayList();
  }
  
  /**
 * 获得部件名
 * @return   部件名
 * @uml.property   name="compoName"
 */
  public String getCompoName(){
    return compoName;
  }

  /**
 * @return   Returns the tableName.
 * @uml.property   name="tableName"
 */
  public String getTableName(){
  	return tableName;
  }
  
}

