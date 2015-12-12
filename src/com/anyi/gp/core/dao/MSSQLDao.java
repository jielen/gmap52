package com.anyi.gp.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import com.anyi.gp.access.DataMapperResultSetExtractor;
import com.anyi.gp.pub.DBHelper;

/**
 * 
 * @author liuxiaoyong
 *
 */
public class MSSQLDao extends AbstractDao {
	
	/**
	 * 根据指定sql ID和查询参数获取分页数据
	 * @param id：sql ID
	 * @param parameterObject：查询参数（map类型）,包含rownum和rn
	 * @return list
	 */
	public List queryPaginationForList(String sql, List parameterObject){
		int size = parameterObject.size();
		String start = (String)parameterObject.get(size - 1);
		String end = (String)parameterObject.get(size - 2);
		parameterObject.remove(parameterObject.size() - 1);
		parameterObject.remove(parameterObject.size() - 1);
		
		return queryForList(sql, parameterObject.toArray(), null, Integer.parseInt(start), Integer.parseInt(end));
	}
	
	public Object queryPagination(String sql, List parameterObject,ResultSetExtractor rse){
		int size = parameterObject.size();
		String start = (String)parameterObject.get(size - 1);
		String end = (String)parameterObject.get(size - 2);
		parameterObject.remove(parameterObject.size() - 1);
		parameterObject.remove(parameterObject.size() - 1);
		return queryForList(sql, parameterObject.toArray(), rse, Integer.parseInt(start), Integer.parseInt(end));
	}
	
	
	private List queryForList(String sql, Object[] params, ResultSetExtractor rse, int start, int end) {
		List result = null;
		DataSource datasource = null;
		Connection conn = null;
		PreparedStatement statement = null;
		ResultSet res = null;
		try {
			datasource = getSqlMapClient().getDataSource();
			conn = DataSourceUtils.getConnection(datasource);
			statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			DBHelper.setStatementParameters(statement, params);
			res = statement.executeQuery();
			int max = end - start;
			if (rse != null) {
				DataMapperResultSetExtractor extractor = (DataMapperResultSetExtractor)rse;
				result = (List)extractor.extractData(res, start, max);
			} else {
				ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
				int count = 0;
				int begin = start - 1;
				if (begin <= 0) {
					res.beforeFirst();
				} else {
					res.absolute(begin);
				}
				while (res.next() && count <= max) {
					Map map = (Map)rowMapper.mapRow(res, count);
					result.add(map);
					count++;
				}		
				if (result.isEmpty()) {
					result.add(DataMapperResultSetExtractor.getBlankRow(res));
				}
			}
		} catch (SQLException exception) {
			throw new RuntimeException(exception);
		} finally {
			JdbcUtils.closeResultSet(res);
			JdbcUtils.closeStatement(statement);
			DataSourceUtils.releaseConnection(conn, datasource);	
		}
		return result;
	}
	
}
