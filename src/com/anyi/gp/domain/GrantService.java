package com.anyi.gp.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;

public class GrantService {
  public GrantService() {
  }

  /**
   * ��Ȩ
   * 
   * @param grantedUser:
   *          ������
   * @param grantUser:
   *          ��������
   * @param startDate:
   *          ��Ȩ��ʼ����
   * @param endDate:
   *          ��Ȩ��������
   * @throws BusinessException
   */
  public void grantUser(String grantedUser, String grantUser, String startDate,
      String endDate) throws BusinessException {
    String delSql = " delete from AS_USER_GRANT where GRANTED_USER = ? and GRANT_USER = ? ";
    String insertSql = " insert into AS_USER_GRANT(GRANTED_USER, GRANT_USER, GRANT_START_DATE, GRANT_END_DATE) "
                     + " values(?, ?, ?, ?)";
    
    Connection conn = null;
    PreparedStatement pst = null;
    
    try {
      conn = DAOFactory.getInstance().getConnection();
      conn.setAutoCommit(false);
      
      pst = conn.prepareStatement(delSql);
      int i = 1;
      pst.setString(i++, grantedUser);
      pst.setString(i++, grantUser);
      pst.executeUpdate();
      
      pst = conn.prepareStatement(insertSql);
      i = 1;
      pst.setString(i++, grantedUser);
      pst.setString(i++, grantUser);
      pst.setString(i++, startDate);
      pst.setString(i++, endDate);
      
      conn.commit();
    } catch (SQLException ex) {
      throw new RuntimeException("GrantService���grantUser������" + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, pst, null);
    }
  }

  /**
   * ȡ����Ȩ
   * 
   * @param grantedUser:
   *          ������
   * @param grantUser:
   *          ��������
   * @throws BusinessException
   */
  public void cancelGrantUser(String grantedUser, String grantUser)
      throws BusinessException {
    String delSql = " delete from AS_USER_GRANT where GRANTED_USER = ? and GRANT_USER = ? ";
    Object[] params = new Object[]{grantedUser, grantUser};
    
    Connection conn = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      DBHelper.executeUpdate(conn, delSql, params);
    }catch (SQLException ex) {
      throw new RuntimeException("GrantService���cancelGrantUser������" + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, null, null);
    }
    
  }

  /**
   * ��ѯ�Ƿ���������������Ȩ
   * 
   * @param grantedUser:
   *          ������
   * @param grantUser:
   *          ��������
   * @return
   * @throws BusinessException
   */
  public Delta queryGrant(String grantedUser, String grantUser)
      throws BusinessException {
    
    String currentDate = GeneralFunc.getCurrDate();
    String sql = " select GRANT_START_DATE, GRANT_END_DATE from AS_USER_GRANT where "
               + " GRANTED_USER = ? and GRANT_USER = ? "
               + " and ( GRANT_START_DATE <= ? or GRANT_START_DATE = NULL or GRANT_START_DATE = '' ) "
               + " and ( GRANT_END_DATE > ? or GRANT_END_DATE = NULL or GRANT_END_DATE = '' ) ";
    
    Object[] params = new Object[]{grantedUser, grantUser, currentDate, currentDate};
    
    // ��Ҫ�����뵱ǰ���������Ƚ�
    Connection conn = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      return DBHelper.queryToDelta(conn, sql, params);
    }catch (Exception ex) {
      throw new RuntimeException("GrantService���cancelGrantUser������" + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, null, null);
    }

  }

  /**
   * �ж�һ���û���ǰ���ޱ������ڹ�Ȩ��
   * 
   * @param grantedUser:������
   * @return
   * @throws BusinessException
   *           GRANT_START_DATE GRANT_END_DATE
   */
  public Delta queryGrantByGrantedUser(String grantedUser)
      throws BusinessException {
    
    String currentDate = GeneralFunc.getCurrDate();
    String sql = " select count(*) COUNT from AS_USER_GRANT where GRANTED_USER = ? "
               + " and ( GRANT_START_DATE <= ? or GRANT_START_DATE = NULL or GRANT_START_DATE = '' ) "
               + " and ( GRANT_END_DATE > ? or GRANT_END_DATE = NULL or GRANT_END_DATE = '' ) ";
    
    Object[] params = new Object[]{grantedUser, currentDate, currentDate};
    Connection conn = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      return DBHelper.queryToDelta(conn, sql, params);
    }catch (Exception ex) {
      throw new RuntimeException("GrantService���cancelGrantUser������" + ex.toString());
    } finally {
      DBHelper.closeConnection(conn, null, null);
    }
  }

}