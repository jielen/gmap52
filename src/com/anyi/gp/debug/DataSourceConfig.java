package com.anyi.gp.debug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * 
 * 数据源信息配置
 * 1.配置文件方式
 * 2.数据库表方式
 * 
 */
public class DataSourceConfig {

  private static final Logger logger = Logger.getLogger(DataSourceConfig.class);

  private static Map jndiMap = new HashMap();

  public DataSourceConfig(DataSource datasource) {
    String sql = " select * from as_db_conf ";
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      conn = datasource.getConnection();
      pst = conn.prepareStatement(sql);
      rs = pst.executeQuery();
      while (rs.next()) {
        jndiMap.put(rs.getString("KEY"), rs.getString("VALUE"));
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      try {
        if (null != rs) {
          rs.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (null != pst) {
          pst.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        if (null != conn) {
          if (!conn.isClosed()) {
            conn.close();
          }
        }
      } catch (SQLException e) {

        e.printStackTrace();
      }
    }
  }

  public static Object getJndiName(Object key) {
    return jndiMap.get(key);
  }
}
