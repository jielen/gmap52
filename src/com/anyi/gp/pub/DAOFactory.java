/* $Id: DAOFactory.java,v 1.3 2008/09/03 06:42:13 liuxiaoyong Exp $ */
package com.anyi.gp.pub;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.debug.DataSourceWrapper;
import com.anyi.gp.util.StringTools;

/**
 * 数据库访问接口
 * 用法参见 DAOFactoryTest
 */
public class DAOFactory {

  private static final Logger log = Logger.getLogger(DAOFactory.class);

  /**
   * 数据库为 Oracle
   * @see #getWhichFactory
   */
  public final static int ORACLE = 0;

  /**
   * 数据库为 Microsoft SQL Server
   * @see #getWhichFactory
   */
  public final static int MSSQL = 1;

  private static int DBTYPE = -1;

  private static DAOFactory g_theDAOFactory = new DAOFactory();

  private DAOFactory() {
  }

  /**
   * 获取数据库连接，调用名为 webglDS 的数据源
   * @return 数据库连接
   * @throws RuntimeException 如果找不到数据源或获取数据哭连接失败
   */
  public Connection getConnection() {
    DataSource ds = getDataSource();
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      log.error("获取数据库连接出错", e);
      throw new RuntimeException(e);
    }
  }

  public static DataSource getDataSource() {
    return (DataSource) ApplusContext.getBean("myDataSource");
  }

  /** 获取数据库连接工厂实例（常用写法 <code>DAOFactory.getInstance().getConnection()</code>） */
  public static DAOFactory getInstance() {
    return g_theDAOFactory;
  }

  /**
   * 获取默认数据库连接的后台数据库类型
   * @return 返回类型代码 <code>DAOFactory.ORACLE</code> 或 <code>DAOFactory.MSSQL</code>
   *  如果无法识别后台数据库类型，返回 ORACLE
   * @see #ORACLE
   * @see #MSSQL
   * @see #getConnection
   */
  public static int getWhichFactory() {
    if (DBTYPE != -1) {
      return DBTYPE;
    }
    int result = ORACLE;
    Connection myConn = null;
    try {
      myConn=DAOFactory.getInstance().getConnection();
      DatabaseMetaData dbmd = myConn.getMetaData();
      String dbName = dbmd.getDatabaseProductName();
      if (dbName.indexOf("Oracle") != -1) {
        result = ORACLE;
      } else if (dbName.indexOf("Microsoft") != -1) {
        result = MSSQL;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e.toString());
    } finally {
      DBHelper.closeConnection(myConn);
    }
    DBTYPE = result;
    return result;
  }

  public static boolean isBusinessException(SQLException e){
    int whichDB = getWhichFactory();
    switch (whichDB) {
    case DAOFactory.ORACLE:
      switch (e.getErrorCode()) {
      case 1:
      case 1401:
      case 1438:
        return true;
      }
    case DAOFactory.MSSQL:
      switch (e.getErrorCode()) {
      case 2627:
      case 547:
      case 515:
        return true;
      }
    }
    return false;
  }
  /**
   * 翻译 SQL 异常的错误信息
   * @param e SQLException 对象
   * @param sql 产生异常信息的 SQL 语句
   */
  public static String translateSQLException(SQLException e, String sql) {
    final int MSSQL_KEY_IS_NULL = 2627;
    String result = e.getErrorCode() + ":" + e.getMessage() + "------" + sql;
    int whichDB = getWhichFactory();
    switch (whichDB) {
    case DAOFactory.ORACLE:
      switch (e.getErrorCode()) {
      case 1:
        // 用户意见：主键重复太专业，从用户的角度改换一下语气。leidh;20040531;
        // result = "主键重复";
        result = "对不起，数据内容出现重复现象，不能保存；如果是修改后保存，则可能是主键描述不足。\n请检查更正后重新保存！";
        break;
      case 1401:
      case 1438:
        result = "文本数据的长度超过数据库的限制！";
        break;
      }
    case DAOFactory.MSSQL:
      switch (e.getErrorCode()) {
      case MSSQL_KEY_IS_NULL:
        result = "对不起，数据内容出现重复现象，不能保存；如果是修改后保存，则可能是主键描述不足。\n请检查更正后重新保存！";
        break;
      case 547:
        if (null != sql && sql.indexOf("delete") >= 0) {
          result = "选中的数据已被其他数据引用，不能删除！";
        } else {
          result = "引用的数据不存在。";
        }
        break;
      case 515:
        // 增加515主键异常处理，对子表主键为空进行提示，wantm;20040611
        result = "子表主键值为空！";
        break;
      }
    }
    return result;
  }
  /**
   * 根据名称获取指定的数据源
   * （常用写法 <code>DAOFactory.getDataSource("jndiName").getConnection()</code>）
   * @param jndiName 数据源的 JNDI 注册名称
   * @return 数据源，不会返回空值。
   * @throws RuntimeException 如果找不到数据源，抛出异常
   */
  public static DataSource getDataSource(String jndiName) {
    return DataSourceWrapper.getDataSource(jndiName);
  }

  public Connection getConnection(String dsName) {
    if (StringTools.isEmptyString(dsName)) {
      throw new IllegalArgumentException("Argument dsName is empty;");
    }
    DataSource dataSource = DAOFactory.getDataSource(dsName);
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
    } catch (SQLException e) {
      log.error("从" + dsName + "获取数据连接出错", e);
      throw new RuntimeException("从" + dsName + "获取数据连接出错", e);
    }
    if (null == conn) {
      String message = "Error_0002: 数据库连接池的连接已经用完，无法取得新的连接！请与系统管理员联系。";
      log.error(message);
      throw new RuntimeException(message);
    }
    return conn;
  }

}
