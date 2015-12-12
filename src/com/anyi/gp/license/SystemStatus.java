package com.anyi.gp.license;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

public class SystemStatus {

  private static final Logger log = Logger.getLogger(SystemStatus.class);

  private int companyCount = 0;

  private int accountCount = 0;

  private static String nd = null;

  public SystemStatus() {
    nd = getND();
  }

  public int getAccountCount() {
    Connection conn = DAOFactory.getInstance().getConnection();
    try {
      Object tempCount = DBHelper.queryOneValue(conn,
        "select count(*) from ma_co_acc where is_used = 'Y' and nd = " + nd, null);
      if (tempCount != null) {
        accountCount = Integer.parseInt(tempCount.toString());
      }
    } catch (Exception e) {
      log.error(e);
      throw new RuntimeException(e);
    } finally {
      DBHelper.closeConnection(conn, null, null);
    }
    return accountCount;
  }

  public int getCompanyCount() {
    Connection conn = DAOFactory.getInstance().getConnection();
    try {
      Object tempCount = DBHelper.queryOneValue(conn,
        "select count(*) from ma_company where co_type_code = '02' and nd = " + nd,
        null);
      if (tempCount != null) {
        companyCount = Integer.parseInt(tempCount.toString());
      }
    } catch (Exception e) {
      log.error(e);
      throw new RuntimeException(e);
    } finally {
      DBHelper.closeConnection(conn, null, null);
    }
    return companyCount;
  }

  private String getND() {
    GregorianCalendar currentDate = new GregorianCalendar();
    String ND = String.valueOf(currentDate.get(Calendar.YEAR));
    return ND;
  }

  public boolean setRemoteParameter(String host, int port) {
    Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement("delete from as_anyiserver");
      stmt.executeUpdate();
      stmt = conn.prepareStatement("insert into as_anyiserver values (?, ?)");
      stmt.setString(1, host);
      stmt.setInt(2, port);
      stmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      log.error(e);
      throw new RuntimeException(e);
    } finally {
      DBHelper.closeConnection(conn, stmt, null);
    }
  }

  public String getHostPort() {
    Connection conn = DAOFactory.getInstance().getConnection();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String host = null;
    try {
      stmt = conn
        .prepareStatement("select host,port from as_anyiserver order by host, port");
      rs = stmt.executeQuery();
      if (rs.next()) {
        host = rs.getString("host");
      }
    } catch (SQLException e) {
      log.error(e);
      throw new RuntimeException(e);
    } finally {
      DBHelper.closeConnection(conn, stmt, rs);
    }
    return host;
  }

}
