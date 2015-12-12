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
 * ���ݿ���ʽӿ�
 * �÷��μ� DAOFactoryTest
 */
public class DAOFactory {

  private static final Logger log = Logger.getLogger(DAOFactory.class);

  /**
   * ���ݿ�Ϊ Oracle
   * @see #getWhichFactory
   */
  public final static int ORACLE = 0;

  /**
   * ���ݿ�Ϊ Microsoft SQL Server
   * @see #getWhichFactory
   */
  public final static int MSSQL = 1;

  private static int DBTYPE = -1;

  private static DAOFactory g_theDAOFactory = new DAOFactory();

  private DAOFactory() {
  }

  /**
   * ��ȡ���ݿ����ӣ�������Ϊ webglDS ������Դ
   * @return ���ݿ�����
   * @throws RuntimeException ����Ҳ�������Դ���ȡ���ݿ�����ʧ��
   */
  public Connection getConnection() {
    DataSource ds = getDataSource();
    try {
      return ds.getConnection();
    } catch (SQLException e) {
      log.error("��ȡ���ݿ����ӳ���", e);
      throw new RuntimeException(e);
    }
  }

  public static DataSource getDataSource() {
    return (DataSource) ApplusContext.getBean("myDataSource");
  }

  /** ��ȡ���ݿ����ӹ���ʵ��������д�� <code>DAOFactory.getInstance().getConnection()</code>�� */
  public static DAOFactory getInstance() {
    return g_theDAOFactory;
  }

  /**
   * ��ȡĬ�����ݿ����ӵĺ�̨���ݿ�����
   * @return �������ʹ��� <code>DAOFactory.ORACLE</code> �� <code>DAOFactory.MSSQL</code>
   *  ����޷�ʶ���̨���ݿ����ͣ����� ORACLE
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
   * ���� SQL �쳣�Ĵ�����Ϣ
   * @param e SQLException ����
   * @param sql �����쳣��Ϣ�� SQL ���
   */
  public static String translateSQLException(SQLException e, String sql) {
    final int MSSQL_KEY_IS_NULL = 2627;
    String result = e.getErrorCode() + ":" + e.getMessage() + "------" + sql;
    int whichDB = getWhichFactory();
    switch (whichDB) {
    case DAOFactory.ORACLE:
      switch (e.getErrorCode()) {
      case 1:
        // �û�����������ظ�̫רҵ�����û��ĽǶȸĻ�һ��������leidh;20040531;
        // result = "�����ظ�";
        result = "�Բ����������ݳ����ظ����󣬲��ܱ��棻������޸ĺ󱣴棬������������������㡣\n������������±��棡";
        break;
      case 1401:
      case 1438:
        result = "�ı����ݵĳ��ȳ������ݿ�����ƣ�";
        break;
      }
    case DAOFactory.MSSQL:
      switch (e.getErrorCode()) {
      case MSSQL_KEY_IS_NULL:
        result = "�Բ����������ݳ����ظ����󣬲��ܱ��棻������޸ĺ󱣴棬������������������㡣\n������������±��棡";
        break;
      case 547:
        if (null != sql && sql.indexOf("delete") >= 0) {
          result = "ѡ�е������ѱ������������ã�����ɾ����";
        } else {
          result = "���õ����ݲ����ڡ�";
        }
        break;
      case 515:
        // ����515�����쳣�������ӱ�����Ϊ�ս�����ʾ��wantm;20040611
        result = "�ӱ�����ֵΪ�գ�";
        break;
      }
    }
    return result;
  }
  /**
   * �������ƻ�ȡָ��������Դ
   * ������д�� <code>DAOFactory.getDataSource("jndiName").getConnection()</code>��
   * @param jndiName ����Դ�� JNDI ע������
   * @return ����Դ�����᷵�ؿ�ֵ��
   * @throws RuntimeException ����Ҳ�������Դ���׳��쳣
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
      log.error("��" + dsName + "��ȡ�������ӳ���", e);
      throw new RuntimeException("��" + dsName + "��ȡ�������ӳ���", e);
    }
    if (null == conn) {
      String message = "Error_0002: ���ݿ����ӳص������Ѿ����꣬�޷�ȡ���µ����ӣ�����ϵͳ����Ա��ϵ��";
      log.error(message);
      throw new RuntimeException(message);
    }
    return conn;
  }

}
