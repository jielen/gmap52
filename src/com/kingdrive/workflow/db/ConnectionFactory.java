package com.kingdrive.workflow.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.kingdrive.workflow.exception.WorkflowException;

public class ConnectionFactory {

  private static DataSource myds = null;
  
  public ConnectionFactory(DataSource ds) {
    myds = ds;
  }

  public static Connection getConnection() throws SQLException {
      return myds.getConnection();
  }
  
  public static DataSource getDataSource() {
    return myds;
  }
  
  public static void freeConnection(Connection conn) throws WorkflowException {
    DBHelper.closeConnection(conn);
  }
}
