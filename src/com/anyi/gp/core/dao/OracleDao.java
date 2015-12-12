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
	 * ����ָ��sql ID�Ͳ�ѯ������ȡ��ҳ����
	 * @param id��sql ID
	 * @param parameterObject����ѯ������map���ͣ�,����rownum��rn
	 * @return list
	 */
	public List queryPaginationForList(String sql, List parameterObject){
		return queryForListBySql(sql, parameterObject.toArray());
	}
	
	public Object queryPagination(String sql, List parameterObject, ResultSetExtractor rse){
		return queryBySql(sql, parameterObject.toArray(), rse);
	}
}
