package com.anyi.gp.core.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.anyi.gp.Delta;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.BatchException;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.mapping.parameter.BasicParameterMapping;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMapping;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.RequestScope;

/**
 * 
 * 封装SqlMapClient，增加jdbcTemple访问数据库，增加根据id获取sql文本的方法
 * @author liuxiaoyong
 * 
 */
public abstract class AbstractDao extends SqlMapClientDaoSupport implements BaseDao{

	public AbstractDao() {

	}

  public DataSource getMyDataSource(){
    return super.getDataSource();
  }
  
	public Object insert(String id, Object parameterObject) throws SQLException {
		return getSqlMapClient().insert(id, parameterObject);
	}

	public Object insert(String id) throws SQLException {
		return getSqlMapClient().insert(id);
	}

	public int update(String id, Object parameterObject) throws SQLException {
		return getSqlMapClient().update(id, parameterObject);
	}

	public int update(String id) throws SQLException{
		return getSqlMapClient().update(id);
	}

	public int delete(String id, Object parameterObject) throws SQLException{
		return getSqlMapClient().delete(id, parameterObject);
	}

	public int delete(String id) throws SQLException{
		return getSqlMapClient().delete(id);
	}

	public Object queryForObject(String id, Object parameterObject) throws SQLException{
		return getSqlMapClient().queryForObject(id, parameterObject);
	}

	public Object queryForObject(String id) throws SQLException{
		return getSqlMapClient().queryForObject(id);
	}

	public Object queryForObject(String id, Object parameterObject, Object resultObject)
			throws SQLException{
		return getSqlMapClient().queryForObject(id, parameterObject, resultObject);
	}

	public List queryForList(String id, Object parameterObject) throws SQLException{
		return getSqlMapClient().queryForList(id, parameterObject);
	}

	public List queryForList(String id) throws SQLException{
		return getSqlMapClient().queryForList(id);
	}
	
	public void queryWithRowHandler(String id, Object parameterObject,
			RowHandler rowHandler) throws SQLException{
		getSqlMapClient().queryWithRowHandler(id, parameterObject, rowHandler);
	}
	
	public void queryWithRowHandler(String id, RowHandler rowHandler)
			throws SQLException{
		getSqlMapClient().queryWithRowHandler(id, rowHandler);
	}
	
	public void startBatch() throws SQLException{
		getSqlMapClient().startBatch();
	}
	
	public int executeBatch() throws SQLException{
		return getSqlMapClient().executeBatch();
	}
	
	public List executeBatchDetailed() throws SQLException, BatchException{
		return getSqlMapClient().executeBatchDetailed();
	}
	
	public Object queryForObjectBySql(String sql, Object[] params){
		JdbcTemplate temp = new JdbcTemplate(getDataSource());
		return temp.queryForObject(sql, params, Object.class);
	}

	public Map queryForMapBySql(String sql, Object[] params){
		JdbcTemplate temp = new JdbcTemplate(getDataSource());
		return temp.queryForMap(sql, params);
	}
	
	public List queryForListBySql(String sql, Object[] params){
		JdbcTemplate temp = new JdbcTemplate(getDataSource());
		return temp.queryForList(sql, params);
	}
	
	public void executeBySql(String sql, Object[] params){
		JdbcTemplate temp = new JdbcTemplate(getDataSource());
		temp.update(sql, params);
	}
	
	public Object queryBySql(String sql, Object[] params, ResultSetExtractor rse){
		JdbcTemplate temp = new JdbcTemplate(getDataSource());
		return temp.query(sql, params, rse);
	}
	
	public String getSql(String id, Object parameterObject){
    MappedStatement mappedStatement = ((SqlMapClientImpl)getSqlMapClient()).getMappedStatement(id);
    Sql sql = mappedStatement.getSql();
    
    RequestScope requestScope = new RequestScope();   
    mappedStatement.initRequest(requestScope);   
    return sql.getSql(requestScope, parameterObject); 
	}
	
	public String getSql(String id, Object parameterObject, List newParams){
		MappedStatement mappedStatement = ((SqlMapClientImpl)getSqlMapClient()).getMappedStatement(id);
		Sql sql = mappedStatement.getSql();
		
    RequestScope requestScope = new RequestScope();   
    mappedStatement.initRequest(requestScope);   
    String sqlString = sql.getSql(requestScope, parameterObject);   
    ParameterMap parameterMap = sql.getParameterMap(requestScope, parameterObject);   
    requestScope.setParameterMap(parameterMap);
    ParameterMapping[] mappings = parameterMap.getParameterMappings();

    if(parameterObject instanceof Map){
    	for(int i = 0; i < mappings.length; i++){
    		ParameterMapping mapping = mappings[i];
    		String type = null;
    		if(mapping instanceof BasicParameterMapping){
    			type = ((BasicParameterMapping)mapping).getJdbcTypeName();
    		}
    	
    		String name = mapping.getPropertyName();
    		if("date".equalsIgnoreCase(type)){
    			Object value = java.sql.Date.valueOf(((Map)parameterObject).get(name).toString());;
    			newParams.add(value);
    		}
    		else if ("datetime".equalsIgnoreCase(type)){
    			Object value = java.sql.Timestamp.valueOf(((Map)parameterObject).get(name).toString());;
    			newParams.add(value);
    		}
    		else{
    			Object value = ((Map)parameterObject).get(name);
          if(value == null){
            int sPos = name.indexOf("[");
            int ePos = name.indexOf("]");
            //if(name.matches("\\w*[\\d*]")){//处理list
            if(sPos > 0 && ePos > 0){
              String tempName = name.substring(0, sPos);
              value = ((Map)parameterObject).get(tempName);
              if(value instanceof List){
                int pos = Integer.parseInt(name.substring(sPos + 1, ePos));
                if(pos < ((List)value).size()){
                  newParams.add(((List)value).get(pos));
                }
              }
            }
            //}
          }
          else{
            newParams.add(value);
          }
    		}
    	}
    }
    else{
    	newParams.addAll(java.util.Arrays.asList(parameterMap.getParameterObjectValues(requestScope, parameterObject)));  
    }
    
    return sqlString;
	}
	
}
