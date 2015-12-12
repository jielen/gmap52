package com.anyi.gp.domain.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.xwork.ActionSupport;

public class SavePasswordAction extends ActionSupport implements ServletRequestAware{

  private static final long serialVersionUID = -3536041933916516371L;
  
  private static final Logger log = Logger.getLogger(SavePasswordAction.class);

  private static final String RESETERROR = "reset_error";
  
  private String userId;

  private String oldPassword;

  private String newPassword;
    
  private HttpServletRequest request; 
  
  public void setServletRequest(HttpServletRequest request) {
    this.request = request; 
  }
    
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String execute() throws Exception {
    if(request.getParameter("ISRESET").toLowerCase().equals("y")){
      if(doChange()) return SUCCESS;
      else return RESETERROR;
    }else{
      oldPassword = GeneralFunc.encodePwd(oldPassword);
      if (checkPasswd()) {
        if(doChange()) return SUCCESS;
        else return ERROR;
      } else {
        request.setAttribute("ERRORINFO", "旧口令不对");
        return ERROR;
      }
    }
  }


  public boolean doChange()
    throws ServletException, IOException {
    newPassword = GeneralFunc.encodePwd(newPassword);
    ////newPassword = StringTools.doubleApos(newPassword);
    return changePasswd();
  }
    
  /**
   * 修改口令
   */
  public boolean changePasswd() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    java.util.Date currDate = new java.util.Date();
    String nowtime = formatter.format(currDate);    
    
    String sqlStr = null;
    PreparedStatement pst = null;
    
    if (userId != null && !userId.equals("")) {
      Connection conn = DAOFactory.getInstance().getConnection();
      try {
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        
        sqlStr = " update AS_USER set PASSWD = ? ,MODI_TIME = ? where USER_ID = ? ";
        pst = conn.prepareStatement(sqlStr);
        
        int i = 1;
        pst.setString(i++, newPassword);
        pst.setString(i++, nowtime);
        pst.setString(i++, userId);
      
        int infectedRows = pst.executeUpdate();
        if (infectedRows > 1) {
          conn.rollback();
          conn.setAutoCommit(autoCommit);
          log.error(sqlStr);
          //System.out.println("非法修改口令，请系统管理员检查系统日志(rolling.log)。");
          return false;
        }
        
        conn.commit();
        conn.setAutoCommit(autoCommit);
        
        return true;
     } catch (SQLException se) {
        throw new RuntimeException("类SavePasswordAction方法changePasswd()出错：" + se.getMessage());
      } finally {
        DBHelper.closeConnection(conn, pst, null);
      }
    }
    return false;
  }
  /**
   * 验证口令
   * 
   * @return flag boolean变量
   */
  public boolean checkPasswd() {
    boolean flag = false;
    String sqlStr = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    sqlStr = "select PASSWD from AS_USER where USER_ID = ?";
    
    if (userId != null && oldPassword != null) {
      Connection conn = DAOFactory.getInstance().getConnection();
      try {
        pst = conn.prepareStatement(sqlStr);
        int i = 1;
        pst.setString(i++, userId);
        
        rs = pst.executeQuery();
        if (rs.next()) {
          String p = rs.getString(1);
          if (p != null && p.equals("")) {
            p = null;
          }
          if (oldPassword != null && oldPassword.equals("")) {
            oldPassword = null;
          }
          if (p == null) {
            if (oldPassword == null) {
              flag = true;
            }
          } else {
            if (oldPassword != null && p.equals(oldPassword)) {
              flag = true;
            }
          }
        }
       } catch (SQLException se) {
        throw new RuntimeException("类SavPasswordAction方法checkPasswd()出错：" + se.getMessage());
      } finally {
        DBHelper.closeConnection(conn, pst, rs);
      }
    }
    return flag;
  }

}
