package com.anyi.gp.access;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 
 * 数据提取类，对rs数据进行提取，若rs.next()为false则返回一条空的数据包，
 * 若rs中含有数据则返回List格式的数据包
 * @author liuxiaoyong
 *
 */
public class DataMapperResultSetExtractor implements ResultSetExtractor{
	
	private boolean blank = false;
	
	private Map fieldMap;
	
	
	
	public void setFieldMap(Map fieldMap) {
		this.fieldMap = fieldMap;
	}

	public Object extractData(ResultSet rs) throws SQLException {
		
		List result = new ArrayList();
		ColMapFormatRowMapper rowMapper = new ColMapFormatRowMapper();
		
		int i = 0;
		while(rs.next()){
			result.add(rowMapper.mapRow(rs, i++, fieldMap));
		}
		
		if(result.isEmpty()){
			blank = true;
			result.add(getBlankRow(rs));
		}
		
		return result;
	}
	
	public Object extractData(ResultSet rs, int start, int max) throws SQLException {
		List result = new ArrayList();
		ColMapFormatRowMapper rowMapper = new ColMapFormatRowMapper();
		int i = 0;
		int begin = start - 1;
		if (begin <= 0) {
			rs.beforeFirst();
		} else {
			rs.absolute(begin);
		}
		while (rs.next() && i <= max) {
			result.add(rowMapper.mapRow(rs, i++, fieldMap));
		}
		if (result.isEmpty()) {
			blank = true;
			result.add(getBlankRow(rs));
		}
		return result;
	}
	
	public static Map getBlankRow(ResultSet rs) throws SQLException{
		Map row = new HashMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for(int i = 1; i <= count; i++){
			row.put(rsmd.getColumnName(i), "");
		}
		return row;
	}
	
	public boolean isBlank(){
		return blank;
	}
}
