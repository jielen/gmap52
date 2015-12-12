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
   * ��������ֶ�ת��
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
   * ʱ���ֶ�����ת��
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
   * ��װ���ɸ���rownum��ȡָ���ε����ݵ�sql�ı�
   * 
   * @param sql
   * @return TODO �����ݿ���Ƿ���order by������һ��Ƕ�ף�������
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
    String fieldName = field.getName(); // �õ���������ֶ���
    if (field.isSave()) {
      return tableName + "." + fieldName;
    }
    final String refName = field.getRefName();
    if (null == refName) {
      return fieldName;
    }
    StringBuffer sbWhere = new StringBuffer();

    // �ⲿʵ������1: IS_FK='Y' ���ֶ�
    // TCJLODO:�˴�ֻ����һ�������Ҳδ���Ǳ�������
    Foreign foreign = meta.getForeign(refName);
    // �ĵ�����Ƚ������ˣ�զ����ô�ɣ� by����wbw
    CompoMeta comMeta = MetaManager.getCompoMeta(foreign.getCompoName());
    TableMeta foreignTable = comMeta.getTableMeta();
    String foreignTName = foreignTable.getName();
    
    List lsFKey = foreignTable.getKeyFieldNames(); 
    for (int i = 0; i < lsFKey.size(); i++){
      String strKey = (String) lsFKey.get(i); // �õ��ⲿʵ���Key����������
      if (sbWhere.length() > 0)
        sbWhere.append(" and ");
      sbWhere.append(refName + "." + strKey + "(+)=" + tableName + "." + strKey);
    }
    
    Map kemMap = foreign.getEffectFields();// foreign��key����ȫ��
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
