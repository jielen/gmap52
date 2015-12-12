package com.anyi.gp.access;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import com.anyi.gp.meta.Field;
import com.anyi.gp.util.StringTools;

import org.springframework.jdbc.core.ColumnMapRowMapper;

public class ColMapFormatRowMapper extends ColumnMapRowMapper {
	
	public Object mapRow(ResultSet rs, int rowNum, Map fieldMap) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map mapOfColValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(rsmd.getColumnName(i));
			Object obj = getColumnValue(rs, i);
      if(fieldMap != null)
        obj = formatValueByType(obj, (Field)fieldMap.get(key));
			mapOfColValues.put(key, obj);
		}
		return mapOfColValues;
	}
	
	private Object formatValueByType(Object value, Field field) {
		Object result = value;
		String strValue = (value != null)?value.toString():"";
		if ((field!=null) && "datetime".equalsIgnoreCase(field.getType())) {
			int position = strValue.lastIndexOf('.');
			if (position > 0) {
				strValue = strValue.substring(0, position);
			}
			result = strValue;
		}
		if((field!=null) && "date".equalsIgnoreCase(field.getType())){
			int position = strValue.lastIndexOf(' ');
			if (position > 0) {
				strValue = strValue.substring(0, position);
			}
			result = strValue;
		}
		if((field!=null)&& "num".equalsIgnoreCase(field.getType())){
			if(field.getKiloStyle()){
				strValue=StringTools.kiloStyle(strValue);
				result = strValue;
			}
			if(field.getDecLength()>0){
				result = StringTools.addZero(strValue, field.getDecLength()); 
			}
		}
		return result;
	}

}
