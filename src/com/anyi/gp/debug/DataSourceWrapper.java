package com.anyi.gp.debug;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.anyi.gp.AppServer;

public class DataSourceWrapper implements DataSource {
  
  private static final Logger log = Logger.getLogger(DataSourceWrapper.class);
  
  private static final ThreadLocal currentUser = new ThreadLocal();

  private static Map dataSourceMap = new HashMap();

  private DataSource ds = null;
    
  public DataSourceWrapper(DataSource ds){
    this.ds = ds;
  }

  public DataSourceWrapper(DataSource ds, String jndiName){
    this.ds = ds;
  }
  
  public Connection getConnection() throws SQLException {
    DataSource tempDs = ds;
    Object obj = currentUser.get();
    //System.out.println("currentUser:" + obj);
    if(obj != null){
      String jndiName = (String)DataSourceConfig.getJndiName(obj);
      if(jndiName != null && jndiName.length() > 0)
        tempDs = getDataSource(jndiName);
    //  System.out.println("currentUser:" + obj + ",jndi:" + jndiName);
      if(tempDs instanceof DataSourceWrapper){
        tempDs = ((DataSourceWrapper)tempDs).getDataSource(); 
      }
    }
    
    Connection conn = tempDs.getConnection();
    return new ConnectionWrapper(conn,new Exception());
  }

  public Connection getConnection(String username, String password) throws SQLException {
    Connection conn = ds.getConnection(username, password);
    return new ConnectionWrapper(conn,new Exception());
  }

  public DataSource getDataSource(){
    return ds;
  }
  
  public int getLoginTimeout() throws SQLException {
    return ds.getLoginTimeout();
  }

  public PrintWriter getLogWriter() throws SQLException {
    return ds.getLogWriter();
  }

  public void setLoginTimeout(int seconds) throws SQLException {
    ds.setLoginTimeout(seconds);
  }

  public void setLogWriter(PrintWriter out) throws SQLException {
    ds.setLogWriter(out);
  }

  public static void setCurrentUser(Object obj){
    currentUser.set(obj);
  }

  public static DataSource getDataSource(String jndiName) {
    DataSource dataSource = (DataSource) dataSourceMap.get(jndiName);
    if (null != dataSource)
      return dataSource;

    try {
      dataSource = findDataSource(jndiName);
    } catch (RuntimeException e) {
    }

    dataSourceMap.put(jndiName, dataSource);
    return dataSource;
  }

  /** @return 不会返回空值，找不到时抛运行时异常 */
  private static DataSource findDataSource(String jndiName) {
    DataSource ds = null;
    try {
      ds = (DataSource) AppServer.ContextJndiObject(jndiName);
    } catch (NamingException e) {
      log.info(e);
    }
    if (null == ds) {
      String message = "Error_0001: 没有找到名为 " + jndiName
        + " 的数据源，请检查应用服务器的数据源配置是否正确！";
      log.error(message);
      throw new RuntimeException(message);
    }
    return new DataSourceWrapper(ds);
  }
}
