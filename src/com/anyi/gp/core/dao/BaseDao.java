package com.anyi.gp.core.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.BatchException;

public interface BaseDao {
	
  public DataSource getMyDataSource();
  
	public Object insert(String id, Object parameterObject) throws SQLException ;

	public Object insert(String id) throws SQLException ;

	public int update(String id, Object parameterObject) throws SQLException ;

	public int update(String id) throws SQLException;

	public int delete(String id, Object parameterObject) throws SQLException;

	public int delete(String id) throws SQLException;

	public Object queryForObject(String id, Object parameterObject) throws SQLException;

	public Object queryForObject(String id) throws SQLException;

	public Object queryForObject(String id, Object parameterObject, Object resultObject)throws SQLException;

	public List queryForList(String id, Object parameterObject) throws SQLException;

	public List queryForList(String id) throws SQLException;

	public void queryWithRowHandler(String id, Object parameterObject, RowHandler rowHandler) throws SQLException;
	
	public void queryWithRowHandler(String id, RowHandler rowHandler)throws SQLException;
	
	public void startBatch() throws SQLException;
	
	public int executeBatch() throws SQLException;
	
	public List executeBatchDetailed() throws SQLException, BatchException;
	
	public Object queryForObjectBySql(String sql, Object[] params);
	
	public Map queryForMapBySql(String sql, Object[] params);
	
	public List queryForListBySql(String sql, Object[] params);
		
	public void executeBySql(String sql, Object[] params);
	
	public Object queryBySql(String sql, Object[] params, ResultSetExtractor rse);
	
	/**
	 * 根据指定sql ID和查询参数获取分页数据
	 * @param id：sql ID
	 * @param parameterObject：查询参数（map类型）,包含rownum和rn
	 * @return list
	 */
	public List queryPaginationForList(String id, List parameterObject);
	
  /**
   * 通过回调函数来查询分页数据
   * @param id
   * @param params
   * @param rse
   * @return
   */
	public Object queryPagination(String id, List parameterObject, ResultSetExtractor rse);
	
	/**
	 * 获取sqlmap配置中的sql文本
	 * @param id
	 * @return
	 */
	public String getSql(String id, Object parameterObject);
	
	/**
	 * 获取sqlmap配置中的sql文本，并形成新参数
	 * @param id
	 * @param parameterObject
	 * @param newParams
	 * @return
	 */
	public String getSql(String id, Object parameterObject, List newParams);
	
}
