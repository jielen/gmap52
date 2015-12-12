package com.anyi.gp.domain.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;

import com.anyi.gp.TableData;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.XMLTools;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class SaveSessionAction extends AjaxAction implements ServletRequestAware{

  private static final long serialVersionUID = -1759470950541448997L;
    
  private String cookie;
  
  private HttpServletRequest request;

  public void setCookie(String cookie) {
    this.cookie = cookie;
  }

  public void setServletRequest(HttpServletRequest request) {
    this.request = request; 
  }
  
  public String doExecute() throws Exception{
    Document xmlDoc = XMLTools.stringToDocument(cookie);
    TableData data = new TableData(xmlDoc.getDocumentElement());
    
    List keys = null;
    List values = new ArrayList();
    if(data != null){
      keys = data.getFieldNames();
      for(int i = 0; i < keys.size(); i++){
        String name = keys.get(i).toString();
        values.add(data.getFieldValue(name));
      }
      
      if(saveSessionToDB(keys, values)){
        syncSession(keys, values);
      }
    }
    
    resultstring = "true";
    
    return SUCCESS;
  }
  
  /**
   * 将session信息保存到数据库中
   * @param keys
   * @param values
   * @return
   */
  private boolean saveSessionToDB(List keys, List values){
    final String updateSql = " update as_user_session set SESSION_VALUE = ? where USER_ID = ? and SESSION_KEY = ? ";
    final String insertSql = " insert into as_user_session(SESSION_VALUE, USER_ID, SESSION_KEY) values(?, ?, ?) ";
    
    Connection conn = null;
    PreparedStatement pst = null;
    String userId = SessionUtils.getAttribute(request, "svUserID");
    
    try{
      conn = DAOFactory.getInstance().getConnection();
      boolean re = conn.getAutoCommit();
      conn.setAutoCommit(false);
      
      for(int i = 0; i < keys.size(); i++){
        Object[] params = new Object[]{values.get(i), userId, keys.get(i)}; 
        int result = DBHelper.executeUpdate(conn, updateSql, params);
        if(result > 0) continue;
        DBHelper.executeUpdate(conn, insertSql, params);
      }
      
      conn.commit();
      conn.setAutoCommit(re);
      
    }catch (SQLException ex) {
      throw new RuntimeException("SaveSessionAction类的saveSessionToDB方法：" + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, pst, null);
    }
    return true;
  }
  
  /**
   * 更新application中的session信息
   * @param keys
   * @param values
   */
  private void syncSession(List keys, List values){
    HttpSession session = request.getSession();
    String token = (String)session.getAttribute(SessionUtils.CURRENT_USER_TOKEN);
    if(token == null)
      return;
    
    ServletContext servletContext = session.getServletContext();
    List appNames = (List)session.getServletContext().getAttribute(SessionUtils.APP_NAME_LIST_KEY);
    if (appNames == null) {
      appNames = GeneralFunc.getAppNames();
      session.getServletContext().setAttribute(SessionUtils.APP_NAME_LIST_KEY, appNames);
    }
    
    if(appNames == null || appNames.isEmpty()) return;
    
    for(int i = 0; i < appNames.size(); i++){//同步更新所有的应用
      String appName = (String)appNames.get(i);
      ServletContext sc = servletContext.getContext("/" + appName);
      if(sc != null){
        Object sessionContext = sc.getAttribute(token);
        try{
        _syncSession(sessionContext, keys, values);
        }catch(Exception e){
          e.printStackTrace();
          //this.LOG.error(e);
        }
        sc.setAttribute(token, sessionContext);
      }
    }
  }

  private void _syncSession(Object sc, List keys, List values) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
    if(sc == null)
      return;
    
    Method method = null;
    try{
      method = sc.getClass().getMethod("put", new Class[]{String.class, String.class});
    }catch(NoSuchMethodException e){
      method = sc.getClass().getMethod("put", new Class[]{String.class, Object.class});//兼容gmap-new
    }
    
    if(method != null){
      for(int i = 0; i < keys.size(); i++){
        method.invoke(sc, new Object[]{keys.get(i), values.get(i)});
      }
    }    
  }
}
