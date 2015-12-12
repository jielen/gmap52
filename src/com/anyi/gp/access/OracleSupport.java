package com.anyi.gp.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.Foreign;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.util.StringTools;

public class OracleSupport extends AbstractDBSupport {

  /**
   * 日期相关字段转换
   * 
   * @param value
   */
  public String castDateField(String value) {
    if (null == value)
      return "NULL";
    StringBuffer voBuf = new StringBuffer();
    voBuf.append("to_date('");
    voBuf.append(StringTools.formatDate(value));
    voBuf.append("', 'YYYY-MM-DD')");
    return voBuf.toString();
  }

  /**
   * 时间字段类型转换
   * 
   * @param
   */
  public String castDatetimeField(String value) {
    if (null == value)
      return "NULL";
    StringBuffer voBuf = new StringBuffer();
    int i = value.indexOf('.');
    if (-1 != i)
      value = value.substring(0, i);
    voBuf.append("to_date('");
    voBuf.append(value);
    voBuf.append("', 'YYYY-MM-DD HH24:MI:SS')");
    return voBuf.toString();
  }

  /**
   * 包装生成根据rownum获取指定段的数据的sql文本
   * 
   * @param sql
   * @return TODO 分数据库和是否有order by（减少一次嵌套）来处理
   */
  public String wrapPaginationSql(String sql) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from ( ").append(
      " select temp_20080118.*, rownum rn from ( ").append(sql).append(
      " ) temp_20080118 where rownum <= ? ").append(" ) where rn >= ? ");
    return sb.toString();
  }

  public String getWfdataSearchSql(String tableName, List colNames, String listtype, boolean filterBySv) {
    List cols = DataTools.getUniqlList(colNames);
    TableMeta tableMeta = MetaManager.getTableMeta(tableName);
    SQLCluster sqls = createColumns(tableMeta, cols);
    
    sqls = this.addWorkflowSqlpart(sqls, tableName, listtype, filterBySv);//zhangting  20090101  
    
    String sqlText = "select " + sqls.getColumns() + " from " + sqls.getFrom();
    if (sqls.getWhere().length() > 0) {
      sqlText += " where " + sqls.getWhere();
    }
    //System.out.println(sqlText);
    return sqlText;
  }

  public SQLCluster createColumns(TableMeta meta, List lsListNames) {
    StringBuffer columnNames = new StringBuffer();
    List lsFrom = new ArrayList();
    Map refToKey = new HashMap();
    for (int i = 0; i < lsListNames.size(); i++) {
      String fieldName = (String) lsListNames.get(i);
      Field field = meta.getField(fieldName);
      if (null != field) {
        if (columnNames.length() > 0) {
          columnNames.append(",");
        }
        columnNames.append(buildColumnName(meta, field, lsFrom, refToKey));
      }
    }

    SQLCluster sqls = new SQLCluster();
    sqls.setColumns(columnNames.toString());

    StringBuffer s = new StringBuffer(meta.getName());
    for (Iterator i = lsFrom.iterator(); i.hasNext();) {
      s.append(",");
      s.append(i.next());
    }
    sqls.setFrom(s.toString());

    StringBuffer strWhere = new StringBuffer();
    Object[] refKey = refToKey.keySet().toArray();
    for (int i = 0; i < refKey.length; i++) {
      String refWhere = (String) refToKey.get(refKey[i]);
      if (strWhere.length() > 0)
        strWhere.append("\n and ");
      strWhere.append("(").append(refWhere).append(")");
    }
    sqls.setWhere(strWhere.toString());
    return sqls;
  }

  private String buildColumnName(TableMeta meta, final Field field, List lsFrom,
    Map refToKey) {
    String tableName = meta.getName();
    String fieldName = field.getName(); // 得到主表保存的字段名
    if (field.isSave()) {
      return tableName + "." + fieldName;
    }
    final String refName = field.getRefName();
    if (null == refName) {
      return fieldName;
    }
    StringBuffer sbWhere = new StringBuffer();

    // 外部实体条件1: IS_FK='Y' 的字段
    // TCJLODO:此处只考虑一种情况，也未考虑别名问题
    Foreign foreign = meta.getForeign(refName);
    // 改到这里比较郁闷了，咋能这么干？ by——wbw
    CompoMeta comMeta = MetaManager.getCompoMeta(foreign.getCompoName());
    TableMeta foreignTable = comMeta.getTableMeta();
    String foreignTName = foreignTable.getName();
    
    List lsFKey = foreignTable.getKeyFieldNames(); 
    for (int i = 0; i < lsFKey.size(); i++){
      String strKey = (String) lsFKey.get(i); // 得到外部实体的Key名在主表中
      if (sbWhere.length() > 0)
        sbWhere.append(" and ");
      sbWhere.append(refName + "." + strKey + "(+)=" + tableName + "." + strKey);
    }
    
    Map kemMap = foreign.getEffectFields();// foreign的key并不全！
    Object[] keySet = kemMap.keySet().toArray();
    for (int i = 0; i < keySet.length; i++) {
      String name = (String) keySet[i];
      String Refname = (String) kemMap.get(name);
      
      if (sbWhere.length() > 0)
        sbWhere.append(" and ");
      sbWhere.append(refName + "." + Refname + "(+)=" + tableName + "." + name);
    }

    lsFrom.add(foreignTName + " " + refName);
    refToKey.put(refName, sbWhere.toString());
    return "nvl(" + refName + "." + fieldName + ",' ') as " + fieldName;
  }
}
