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

/** 与数据库操作相关的一些辅助方法 */
public class DBHelper {

	private static final Logger logger = Logger.getLogger(DBHelper.class);

	/**
	 * 返回数据集的最大行数，被 resultSetToList, queryToList 用到
	 * 
	 * @see #resultSetToList
	 * @see #queryToList
	 */
	public final static int MAX_ROW_COUNT = 10000; // _hd

	/**
	 * 关闭数据库连接和语句，用法示例： DBTools.closeConnection(conn, stmt, rs);
	 * DBTools.closeConnection(null, stmt, rs); 如果有的参数没有值，传空值即可。
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

	/** 关闭数据库连接 */
	public static void closeConnection(Connection conn) {
		try {
			if (null != conn && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** 查找指定的列 */
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
	 * 设置参数：用于变量绑定中参数的设置
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
	 * 执行指定的查询并返回结果的第一个字段值。 本方法主要是为了确保释放数据库相关资源，减轻书写 finally 块的繁琐。
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            查询语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应，可以是 null， 表示没有参数
	 * @return 如果有返回结果，返回第一行的第一个字段值，否则返回 null
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
			while (rs.next()) {// 多行
				List oneRowList = new ArrayList();
				List colNameList = new ArrayList();
				List valueList = new ArrayList();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i = 1; i <= columnCount; ++i) {// 多列
					colNameList.add(rsmd.getColumnName(i));
					Object temp = rs.getObject(i);
					temp = temp == null ? "":temp.toString();
					valueList.add(temp);
				}
				oneRowList.add(colNameList);
				oneRowList.add(valueList);
				result.add(oneRowList);// 元素是一行的map
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
	 * 执行指定的查询并返回结果的第一个字段值
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
	 * 执行指定的查询，并返回结果的第一行。
	 * 
	 * @param ds
	 *            数据源
	 * @param sql
	 *            查询语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应，可以是 null， 表示没有参数
	 * @return 如果有返回结果，返回第一行，否则返回 null，返回结果为 (字段名, 字段值) 的映射表，
	 *         字段值一定是字符串类型(ResultSet.getString)
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
	 * 执行指定的查询的查询并返回所有结果行的第一列。 本方法主要是为了确保释放数据库相关资源，减轻书写 finally 块的繁琐。
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            查询语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应，可以是 null， 表示没有参数
	 * @return 如果有返回结果，返回所有结果行的第一列，否则返回 null
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
	 * 执行带参数的 SQL 语句 本方法主要是为了确保释放数据库相关资源，减轻书写 finally 块的繁琐。 调用本方法需注意参数值 params
	 * 必须是 JDBC 驱动支持的数据类型，否则会有类似于 "The specified SQL type is not supported by
	 * this driver." 的异常。 如果参数值为空值，认为该参数的类型为 java.sql.Types.CHAR 。
	 * 
	 * @param ds
	 *            数据源
	 * @param sql
	 *            带参数的 SQL 语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应， 可以是 null，
	 *            表示没有参数。注意，如果参数值为null，认为该参数类型为 Types.CHAR 。
	 * @return 影响的行数
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
	 * 用 and 连接两个字符串，用于构造 SQL 条件语句
	 * 
	 * @param s1
	 * @param s2
	 * @return 如果 s2 为空或空串，直接返回 s1，否则返回正确拼上 and 的串。
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
	 * 将日期条件转换为 Oracle 接收的格式
	 * 
	 * @param condition
	 *            条件语句
	 * @param dateField
	 *            日期字段值
	 * @return 经过处理后的条件语句
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
	 * 将指定查询的结果集转换为列表，列表元素为一维对象数组，对应于一行查询结果 最大返回行数被限制为 MAX_ROW_COUNT 行。
	 * 本方法主要是为了确保释放数据库相关资源，减轻书写 finally 块的繁琐。 调用本方法需注意参数值 params 必须是 JDBC
	 * 驱动支持的数据类型，否则会有类似于 "The specified SQL type is not supported by this
	 * driver." 的异常。 如果参数值为空值，认为该参数的类型为 java.sql.Types.CHAR 。
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            带参数的 SQL 语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应， 可以是 null，
	 *            表示没有参数。注意，如果参数值为null，认为该参数类型为 Types.CHAR 。
	 * @return List 列表对象，结果集中的每一行数据将被转换为二维数组依次添加到该 列表中。
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
	 * 将结果集转换为二维数组（限制 MAX_ROW_COUNT 行） 使用 queryToList 会更方便
	 * 
	 * @param rs
	 *            被转换的结果集
	 * @param rows
	 *            [输出] 返回结果，结果集中的每一行数据将被转换为二维数组依次 添加到 rows 中。
	 * @param arrayLength
	 *            返回二维数组每一行（相当于第二维）的长度，如果为 0， 取 rs 结果集的列数
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
	 * 包装生成根据rownum获取指定段的数据的sql文本
	 * 
	 * @param sql
	 * @return TODO 分数据库和是否有order by（减少一次嵌套）来处理
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
	 * 包装生成根据top获取指定段的数据的sql文本
	 * 
	 * @param sql
	 * @return
	 */
	public static String wrapPaginationSqlForMSSQL(String sql) {
		// TCJLODO
		return "";
	}

	/**
	 * 包装生成获取记录总数的sql文本
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
	 * 封装合计值sql
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
   * 简单处理参数，参数通过";"隔开
   * @param condition
   * @return
   */
  public static Map parseParamsSimpleForSql(String condition){
    Map map = new HashMap();
    parseParamsSimpleForSql(condition, map);
    return map;
  }
  
	/**
	 * 简单处理参数，参数通过";"隔开
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
					if (entry[1].indexOf("|") > 0) {// 处理数组
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
	 * 根据tableName和参数params来形成sql和新参数
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
	 * 将指定查询的结果集转换为 Delta
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            带参数的 SQL 语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应， 可以是 null，
	 *            表示没有参数。注意，如果参数值为null，认为该参数类型为 Types.CHAR 。
	 * @return Delta 对象，元素类型为 TableData
	 * @see #queryToList
	 * @deprecated 不推荐使用
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
	 * 执行带参数的 SQL 语句 本方法主要是为了确保释放数据库相关资源，减轻书写 finally 块的繁琐。 调用本方法需注意参数值 params
	 * 必须是 JDBC 驱动支持的数据类型，否则会有类似于 "The specified SQL type is not supported by
	 * this driver." 的异常。 如果参数值为空值，认为该参数的类型为 java.sql.Types.CHAR 。
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            带参数的 SQL 语句
	 * @param params
	 *            参数值，应该与 SQL 语句中的 ? 个数相同且一一对应， 可以是
	 *            null，表示没有参数。注意，如果参数值为null，认为该参数类型为 Types.CHAR 。
	 * @return 影响的行数
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
	 * 给对象值加上单引号，如果内部有单引号则改为两个单引号，用于构造 SQL 语句
	 * 
	 * @param s
	 *            要添加到的字符串缓冲
	 * @param o
	 *            要添加的对象，null 时添加 null，Number 类型直接添加，否则用单引号括起来
	 * @return 结果字符串缓冲
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
	 * 是否是虚部件，目前认为所有名为 XX_TEMP 的表都是虚部件对应的表
	 */
	public static boolean isVirtualTable(String tableName) {
		return tableName.matches(".{2,10}_TEMP");
	}
}
