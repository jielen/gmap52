package com.anyi.gp.access;

import java.util.List;
import java.util.Map;

import com.anyi.gp.meta.TableMeta;


public interface DBSupport {
  
  public String castDateField(String value);
  
  public String castDatetimeField(String value);
  
  public  String wrapSqlForCount(String sql);
  
  public  String wrapSqlForTotal(String sql, List fields);
  
  public String wrapPaginationSql(String sql);
  
  public Map parseParamsSimpleForSql(String condition);
  
  public String formatFieldValueByType(String strFieldType, String fieldValue);
  
  public String wrapSqlByTableName(String tableName, Map params, List newParams);
  
  public String wrapSqlByParams(String sql, Map params, List newParams);
  
  public String getWfdataSearchSql(String tableName, List colName, String listtype, boolean filterBySv);
  
  public SQLCluster createColumns(TableMeta meta, List lsListNames);
  
  public String wrapSqlByCondtion(String sql, String condition);
  
  public String wrapSqlByCondtion(String sql, String condition, boolean replaceCondition);
}
