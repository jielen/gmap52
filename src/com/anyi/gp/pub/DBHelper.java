// $Id: DBHelper.java,v 1.22 2008/09/25 02:53:09 liuxiaoyong Exp $

package com.anyi.gp.pub;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.anyi.gp.Delta;
import com.anyi.gp.debug.Debug;
import com.anyi.gp.util.StringTools;

/** �����ݿ������ص�һЩ�������� */
public class DBHelper {

	private static final Logger logger = Logger.getLogger(DBHelper.class);

	/**
	 * �������ݼ�������������� resultSetToList, queryToList �õ�
	 * 
	 * @see #resultSetToList
	 * @see #queryToList
	 */
	public final static int MAX_ROW_COUNT = 10000; // _hd

	/**
	 * �ر����ݿ����Ӻ���䣬�÷�ʾ���� DBTools.closeConnection(conn, stmt, rs);
	 * DBTools.closeConnection(null, stmt, rs); ����еĲ���û��ֵ������ֵ���ɡ�
	 */
	public static void closeConnection(Connection conn, Statement stmt,
			ResultSet rs) {
		try {
			if (null != rs) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (null != stmt) {
				stmt.close();
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

	/** �ر����ݿ����� */
	public static void closeConnection(Connection conn) {
		try {
			if (null != conn && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** ����ָ������ */
	public static int findColumnNo(ResultSetMetaData meta, String name) {
		if (null == name) {
			return -1;
		}
		try {
			int n = meta.getColumnCount();
			for (int i = 0; i < n; ++i) {
				String colName = meta.getColumnName(i + 1);
				if (null != colName && 0 == colName.compareToIgnoreCase(name)) {
					return i;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * ���ò��������ڱ������в���������
	 * 
	 * @param pst
	 * @param params
	 * @throws SQLException
	 */
	public static void setStatementParameters(PreparedStatement pst,
			Object[] params) throws SQLException {
		for (int i = 0; i < params.length; ++i) {
			Object obj = params[i];
			if (null == obj) {
				pst.setNull(i + 1, Types.CHAR);
			} else if (obj instanceof java.sql.Date) {
				pst.setDate(i + 1, (java.sql.Date) obj);
			} else if (obj instanceof java.sql.Timestamp) {
				pst.setTimestamp(i + 1, (java.sql.Timestamp) obj);
			} else {
				pst.setObject(i + 1, obj);
			}
		}
	}

	public static int queryRecCount(DataSource ds, String tStr) {
		return ((BigDecimal) queryOneValue(ds, tStr, null)).intValue();
	}

	/**
	 * ִ��ָ���Ĳ�ѯ�����ؽ���ĵ�һ���ֶ�ֵ�� ��������Ҫ��Ϊ��ȷ���ͷ����ݿ������Դ��������д finally ��ķ�����
	 * 
	 * @param conn
	 *            ���ݿ�����
	 * @param sql
	 *            ��ѯ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�������� null�� ��ʾû�в���
	 * @return ����з��ؽ�������ص�һ�еĵ�һ���ֶ�ֵ�����򷵻� null
	 * 
	 */
	public static Object queryOneValue(DataSource ds, String sql,
			Object[] params) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return queryOneValue(conn, sql, params);
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn);
		}
	}

	public static Object queryOneValue(String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			return queryOneValue(conn, sql, params);
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn);
		}
	}

	public static List queryMultiRow(Connection conn, String sql,
			Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List result = new ArrayList();
		try {
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			rs = stmt.executeQuery();
			while (rs.next()) {// ����
				List oneRowList = new ArrayList();
				List colNameList = new ArrayList();
				List valueList = new ArrayList();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; ++i) {// ����
					colNameList.add(rsmd.getColumnName(i));
					Object temp = rs.getObject(i);
					temp = temp == null ? "":temp.toString();
					valueList.add(temp);
				}
				oneRowList.add(colNameList);
				oneRowList.add(valueList);
				result.add(oneRowList);// Ԫ����һ�е�map
			}
		} catch (SQLException e) {
			logger.error(sql, e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(null, stmt, rs);
		}
		return result;
	}

	/**
	 * ִ��ָ���Ĳ�ѯ�����ؽ���ĵ�һ���ֶ�ֵ
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 */
	public static Object queryOneValue(Connection conn, String sql,
			Object[] params) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getObject(1);
			}
		} finally {
			closeConnection(null, stmt, rs);
		}
		return null;
	}

	/**
	 * ִ��ָ���Ĳ�ѯ�������ؽ���ĵ�һ�С�
	 * 
	 * @param ds
	 *            ����Դ
	 * @param sql
	 *            ��ѯ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�������� null�� ��ʾû�в���
	 * @return ����з��ؽ�������ص�һ�У����򷵻� null�����ؽ��Ϊ (�ֶ���, �ֶ�ֵ) ��ӳ���
	 *         �ֶ�ֵһ�����ַ�������(ResultSet.getString)
	 */
	public static Map queryOneRow(DataSource ds, String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return queryOneRow(conn, sql, params);
		} catch (SQLException e) {
			logger.error(sql, e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn);
		}
	}

	public static Map queryOneRow(Connection conn, String sql, Object[] params)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				Map result = new HashMap();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; ++i) {
					Object temp = rs.getObject(i);
					temp = temp == null?"":temp.toString();
					result.put(rsmd.getColumnName(i), temp);
				}
				return result;
			}
		} finally {
			closeConnection(null, stmt, rs);
		}
		return null;
	}

	public static Map queryOneRow(String sql, Object[] params)
			throws SQLException {
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			return queryOneRow(conn, sql, params);
		} finally {
			DBHelper.closeConnection(conn);
		}
	}

	/**
	 * ִ��ָ���Ĳ�ѯ�Ĳ�ѯ���������н���еĵ�һ�С� ��������Ҫ��Ϊ��ȷ���ͷ����ݿ������Դ��������д finally ��ķ�����
	 * 
	 * @param conn
	 *            ���ݿ�����
	 * @param sql
	 *            ��ѯ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�������� null�� ��ʾû�в���
	 * @return ����з��ؽ�����������н���еĵ�һ�У����򷵻� null
	 */
	public static Object[] queryOneColumn(DataSource ds, String sql,
			Object[] params) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return queryOneColumn(conn, sql, params);
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn);
		}
	}

	public static Object[] queryOneColumn(Connection conn, String sql,
			Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				List rows = new ArrayList();
				rows.add(rs.getObject(1));
				while (rs.next()) {
					rows.add(rs.getObject(1));
				}
				return rows.toArray();
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(null, stmt, rs);
		}
		return null;
	}

	/**
	 * ִ�д������� SQL ��� ��������Ҫ��Ϊ��ȷ���ͷ����ݿ������Դ��������д finally ��ķ����� ���ñ�������ע�����ֵ params
	 * ������ JDBC ����֧�ֵ��������ͣ�������������� "The specified SQL type is not supported by
	 * this driver." ���쳣�� �������ֵΪ��ֵ����Ϊ�ò���������Ϊ java.sql.Types.CHAR ��
	 * 
	 * @param ds
	 *            ����Դ
	 * @param sql
	 *            �������� SQL ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�� ������ null��
	 *            ��ʾû�в�����ע�⣬�������ֵΪnull����Ϊ�ò�������Ϊ Types.CHAR ��
	 * @return Ӱ�������
	 */
	public static int executeUpdate(DataSource ds, String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return executeUpdate(conn, sql, params);
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage());
		} finally {
			DBHelper.closeConnection(conn);
		}
	}

	public static int executeUpdate(String sql, Object[] params)
			throws SQLException {
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			return executeUpdate(conn, sql, params);
		} finally {
			DBHelper.closeConnection(conn);
		}
	}

	/**
	 * �� and ���������ַ��������ڹ��� SQL �������
	 * 
	 * @param s1
	 * @param s2
	 * @return ��� s2 Ϊ�ջ�մ���ֱ�ӷ��� s1�����򷵻���ȷƴ�� and �Ĵ���
	 */
	public static String andString(String s1, String s2) {
		if (null == s2 || 0 == s2.length()) {
			return s1;
		}
		if (null == s1 || 0 == s1.length()) {
			return s2;
		}
		return s1 + " and " + s2;
	}

	/**
	 * ����������ת��Ϊ Oracle ���յĸ�ʽ
	 * 
	 * @param condition
	 *            �������
	 * @param dateField
	 *            �����ֶ�ֵ
	 * @return �����������������
	 */
	public static String convertDateConditionForOracle(String condition,
			String dateField) {
		if (null == condition) {
			return condition;
		}
		if (null == dateField) {
			return condition;
		}
		int fieldLen = dateField.length();
		int first = condition.indexOf(dateField + ">=");
		if (first > -1) {
			String startDate = condition.substring(first + fieldLen + 2, first
					+ fieldLen + 2 + 12);
			condition = condition.substring(0, first + fieldLen + 2)
					+ "to_date("
					+ startDate
					+ ", 'YYYY-MM-DD')"
					+ condition.substring(first + fieldLen + 14, condition
							.length());
		}
		int last = condition.indexOf(dateField + "<=");
		if (last > -1) {
			String startDate = condition.substring(last + fieldLen + 2, last
					+ fieldLen + 2 + 12);
			condition = condition.substring(0, last + fieldLen + 2)
					+ "to_date("
					+ startDate
					+ ", 'YYYY-MM-DD')"
					+ condition.substring(last + fieldLen + 14, condition
							.length());
		}
		return condition;
	}

	/**
	 * ��ָ����ѯ�Ľ����ת��Ϊ�б��б�Ԫ��Ϊһά�������飬��Ӧ��һ�в�ѯ��� ��󷵻�����������Ϊ MAX_ROW_COUNT �С�
	 * ��������Ҫ��Ϊ��ȷ���ͷ����ݿ������Դ��������д finally ��ķ����� ���ñ�������ע�����ֵ params ������ JDBC
	 * ����֧�ֵ��������ͣ�������������� "The specified SQL type is not supported by this
	 * driver." ���쳣�� �������ֵΪ��ֵ����Ϊ�ò���������Ϊ java.sql.Types.CHAR ��
	 * 
	 * @param conn
	 *            ���ݿ�����
	 * @param sql
	 *            �������� SQL ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�� ������ null��
	 *            ��ʾû�в�����ע�⣬�������ֵΪnull����Ϊ�ò�������Ϊ Types.CHAR ��
	 * @return List �б���󣬽�����е�ÿһ�����ݽ���ת��Ϊ��ά����������ӵ��� �б��С�
	 */
	public static List queryToList(String sql, Object[] params)
			throws SQLException {
		List rows = new ArrayList();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			rs = stmt.executeQuery();
			resultSetToList(rs, rows, 0);
		} finally {
			closeConnection(conn, stmt, rs);
		}
		return rows;
	}

	public static void executeSQLs(List sqls) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			// conn.setAutoCommit(false);
			stmt = conn.createStatement();
			if (logger.isDebugEnabled()) {
				logger.debug(sqls.toString());
			}
			for (Iterator i = sqls.iterator(); i.hasNext();) {
				String s = i.next().toString();
				stmt.addBatch(s);
			}
			stmt.executeBatch();
			// conn.commit();
		} catch (SQLException e) {
			logger.error(sqls.toString());
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	public static int executeSQL(String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			return executeUpdate(conn, sql, params);
		} catch (SQLException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn, null, null);
		}

	}

	/**
	 * �������ת��Ϊ��ά���飨���� MAX_ROW_COUNT �У� ʹ�� queryToList �������
	 * 
	 * @param rs
	 *            ��ת���Ľ����
	 * @param rows
	 *            [���] ���ؽ����������е�ÿһ�����ݽ���ת��Ϊ��ά�������� ��ӵ� rows �С�
	 * @param arrayLength
	 *            ���ض�ά����ÿһ�У��൱�ڵڶ�ά���ĳ��ȣ����Ϊ 0�� ȡ rs �����������
	 * @see #queryToList
	 */
	public static void resultSetToList(ResultSet rs, List rows, int arrayLength)
			throws SQLException {
		if (null == rs || null == rows || arrayLength < 0) {
			return;
		}

		int columnCount = rs.getMetaData().getColumnCount();
		if (arrayLength == 0) {
			arrayLength = columnCount;

		}
		int i;
		int lineCount = 0;
		while (rs.next()) {
			Object[] cols = new Object[arrayLength];
			for (i = 0; i < arrayLength && i < columnCount; ++i) {
				cols[i] = rs.getObject(i + 1);
			}
			rows.add(cols);
			if (++lineCount > MAX_ROW_COUNT) {
				break;
			}
		}
	}

	/**
	 * ��װ���ɸ���rownum��ȡָ���ε����ݵ�sql�ı�
	 * 
	 * @param sql
	 * @return TODO �����ݿ���Ƿ���order by������һ��Ƕ�ף�������
	 */
	public static String wrapPaginationSqlForOracle(String sql) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ( ").append(
				"	select temp_20080118.*, rownum rn from ( ").append(sql)
				.append(" ) temp_20080118 where rownum <= ? ").append(
						" ) where rn >= ? ");

		return sb.toString();
	}

	/**
	 * ��װ���ɸ���top��ȡָ���ε����ݵ�sql�ı�
	 * 
	 * @param sql
	 * @return
	 */
	public static String wrapPaginationSqlForMSSQL(String sql) {
		// TCJLODO
		return "";
	}

	/**
	 * ��װ���ɻ�ȡ��¼������sql�ı�
	 * 
	 * @param sql
	 * @return
	 */
	public static String wrapSqlForCount(String sql) {
		StringBuffer sb = new StringBuffer();
		int fpos = sql.indexOf("from");
		String temp = sql.substring(fpos, sql.length());
		sb.append(" select count(1) ").append(temp);

		return sb.toString();
	}

	/**
	 * ��װ�ϼ�ֵsql
	 * 
	 * @param sql
	 * @param fields
	 * @return
	 */
	public static String wrapSqlForTotal(String sql, List fields) {
		if (fields == null) {
			return sql;
		}

		StringBuffer sb = new StringBuffer();

		int fpos = sql.indexOf("from");
		String temp = sql.substring(fpos, sql.length());
		sb.append(" select ");

		Iterator itera = fields.iterator();
		while (itera.hasNext()) {
			Object obj = itera.next();
			sb.append(" sum( ");
			sb.append(obj);
			sb.append(" ) as ");
			sb.append(obj);
			sb.append(" ,");
		}

		return sb.substring(0, sb.length() - 1) + temp;
	}

  /**
   * �򵥴������������ͨ��";"����
   * @param condition
   * @return
   */
  public static Map parseParamsSimpleForSql(String condition){
    Map map = new HashMap();
    parseParamsSimpleForSql(condition, map);
    return map;
  }
  
	/**
	 * �򵥴������������ͨ��";"����
	 * 
	 * @param condition
	 * @return
	 */
	public static void parseParamsSimpleForSql(String condition, Map map) {
		if (map == null) {
			map = new HashMap();
		}
		if (condition != null && condition.length() != 0) {
			String[] conds = condition.split(";");
			for (int i = 0; i < conds.length; i++) {
				if (conds[i].trim().length() == 0) {
					continue;
				}
				String[] entry = conds[i].split("=");
				if (entry.length > 1) {
					if (entry[1].indexOf("|") > 0) {// ��������
						String[] multiValue = entry[1].split("\\|");
						List valueList = new ArrayList();
						for (int j = 0; j < multiValue.length; j++) {
							if (multiValue[j] != null
									&& multiValue[j].trim().length() > 0)
								valueList.add(multiValue[j].trim());
						}
						map.put(entry[0].trim(), valueList);
					} else {
						map.put(entry[0].trim(), entry[1].trim());
					}
				} else {
					map.remove(entry[0]);
				}
			}
		}
	}

	/**
	 * ����tableName�Ͳ���params���γ�sql���²���
	 * 
	 * @param tableName
	 * @param params
	 * @param newParams
	 * @return
	 */
	public static String wrapSqlByParams(String tableName, Map params,
			List newParams) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ");
		sb.append(tableName);
		sb.append(" where ");

		Set entrySet = params.entrySet();
		Iterator itera = entrySet.iterator();
		while (itera.hasNext()) {
			Entry entry = (Entry) itera.next();
			sb.append(entry.getKey());
			sb.append(" = ? and ");
			newParams.add(entry.getValue());
		}

		return sb.toString().substring(0, sb.length() - 4);
	}

	/**
	 * ��ָ����ѯ�Ľ����ת��Ϊ Delta
	 * 
	 * @param conn
	 *            ���ݿ�����
	 * @param sql
	 *            �������� SQL ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�� ������ null��
	 *            ��ʾû�в�����ע�⣬�������ֵΪnull����Ϊ�ò�������Ϊ Types.CHAR ��
	 * @return Delta ����Ԫ������Ϊ TableData
	 * @see #queryToList
	 * @deprecated ���Ƽ�ʹ��
	 */
	public static Delta queryToDelta(Connection conn, String sql,
			Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			rs = stmt.executeQuery();
			return new Delta(rs);
		} catch (SQLException e) {
			logger.error(sql, e);
			throw new RuntimeException(e);
		}

		finally {
			closeConnection(null, stmt, rs);
		}
	}

	public static Delta queryToDelta(String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			return queryToDelta(conn, sql, params);
		} finally {
			closeConnection(conn, null, null);
		}
	}

	public static Delta queryToDelta(DataSource ds, String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return queryToDelta(conn, sql, params);
		} catch (SQLException e) {
			logger.error(sql, e);
			throw new RuntimeException(e);
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * ִ�д������� SQL ��� ��������Ҫ��Ϊ��ȷ���ͷ����ݿ������Դ��������д finally ��ķ����� ���ñ�������ע�����ֵ params
	 * ������ JDBC ����֧�ֵ��������ͣ�������������� "The specified SQL type is not supported by
	 * this driver." ���쳣�� �������ֵΪ��ֵ����Ϊ�ò���������Ϊ java.sql.Types.CHAR ��
	 * 
	 * @param conn
	 *            ���ݿ�����
	 * @param sql
	 *            �������� SQL ���
	 * @param params
	 *            ����ֵ��Ӧ���� SQL ����е� ? ������ͬ��һһ��Ӧ�� ������
	 *            null����ʾû�в�����ע�⣬�������ֵΪnull����Ϊ�ò�������Ϊ Types.CHAR ��
	 * @return Ӱ�������
	 */
	public static int executeUpdate(Connection conn, String sql, Object[] params)
			throws SQLException {
		PreparedStatement stmt = null;
		try {
			Debug.log(DBHelper.class, sql);
			stmt = conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				setStatementParameters(stmt, params);
			}
			return stmt.executeUpdate();
		} finally {
			closeConnection(null, stmt, null);
		}
	}

	public static Connection getConnection(DataSource datasource) {
		try {
			return datasource.getConnection();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * ������ֵ���ϵ����ţ�����ڲ��е��������Ϊ���������ţ����ڹ��� SQL ���
	 * 
	 * @param s
	 *            Ҫ��ӵ����ַ�������
	 * @param o
	 *            Ҫ��ӵĶ���null ʱ��� null��Number ����ֱ����ӣ������õ�����������
	 * @return ����ַ�������
	 */
	public static StringBuffer appendQuotedValue(StringBuffer s, Object o) {
		if (null == o) {
			s.append("null");
		} else {
			String v = o.toString();
			if (o instanceof Number) {
				s.append(v);
			} else if (-1 != v.indexOf('\'')) {
				s.append("'").append(StringTools.doubleApos(v)).append("'");
			} else {
				s.append("'").append(v).append("'");
			}
		}
		return s;
	}

	/**
	 * �Ƿ����鲿����Ŀǰ��Ϊ������Ϊ XX_TEMP �ı����鲿����Ӧ�ı�
	 */
	public static boolean isVirtualTable(String tableName) {
		return tableName.matches(".{2,10}_TEMP");
	}
}
