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
 * �����������
 * @param newRequest   �������
 * @uml.property   name="request"
 */
  protected void setRequest(HttpServletRequest newRequest){
    request = newRequest;
  }

  /**
   * ��ʼ��Bean
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
   * ���ò�����
   */
  protected void setCompoName(){
  	if(request.getParameter("componame") != null){
  		compoName = request.getParameter("componame");
  	}
  	else if(request.getAttribute("componame") != null){
      compoName = (String)request.getAttribute("componame");
    } else{
      throw new IllegalArgumentException("����ѡ��ҳ������������û��componame���ԡ�");
    }
  }

  protected void setTableName(){
    if(!AS_FOREIGN_FOR_SQL.equalsIgnoreCase(compoName))
      this.tableName = MetaManager.getCompoMeta(compoName).getMasterTable();
  }
  
  /**
   * �����û���
   */
  protected void setUser(){
    this.userId = SessionUtils.getAttribute(request, "svUserID");
  }

  /**
   * ������������
   */
  protected void setLang(){
    lang = SessionUtils.getAttribute(request, "lang");
    if(lang == null)
    	lang = "C";
  }

  /**
   * ����������Դ
   */
  protected void setResource(){
    resource = LangResource.getInstance();
  }

  /**
   * ���÷����б�
   */
  protected void setCalls(){
  	// TCJLODO:
  	this.calls = new ArrayList();
  }
  
  /**
 * ��ò�����
 * @return   ������
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

