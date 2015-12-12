package com.anyi.gp.access;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class YNBoolTypeHandlerCallback implements TypeHandlerCallback{
	private static String YES = "y";
	private static String NO = "n";

	public Object getResult(ResultGetter getter) throws SQLException{
		// TCJLODO Auto-generated method stub
		String res = getter.getString();
		if (YES.equalsIgnoreCase(res)) {
			return Boolean.TRUE;
		}else if (NO.equalsIgnoreCase(res)) {
			return Boolean.FALSE;
		}else if(res == null || res.trim().length() == 0){
		  return Boolean.FALSE;
    }
    else {
			throw new SQLException("Unexpected value " + res + " found where " + YES + " or " + NO + " was expected.");
		}
	}

	public void setParameter(ParameterSetter setter, Object obj) throws SQLException{
		// TCJLODO Auto-generated method stub
    if(obj == null)
      setter.setString(NO);
    
		Boolean bool = (Boolean)obj;
	  if (bool.booleanValue()) {
	  	setter.setString(YES);
	  } else {
	  	setter.setString(NO);
	  }
	}

	public Object valueOf(String value){
		// TCJLODO Auto-generated method stub
    if(value == null)
      return Boolean.FALSE;
    
		if (value.equalsIgnoreCase(YES)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

}
