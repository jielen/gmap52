package com.anyi.gp.core.dao;

import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 
 * @author liuxiaoyong
 *
 */
public class OracleDao extends AbstractDao{
	
	/**
	 * 根据指定sql ID和查询参数获取分页数据
	 * @param id：sql ID
	 * @param parameterObject：查询参数（map类型）,包含rownum和rn
	 * @return list
	 */
	public List queryPaginationForList(String sql, List parameterObject){
		return queryForListBySql(sql, parameterObject.toArray());
	}
	
	public Object queryPagination(String sql, List parameterObject, ResultSetExtractor rse){
		return queryBySql(sql, parameterObject.toArray(), rse);
	}
}
