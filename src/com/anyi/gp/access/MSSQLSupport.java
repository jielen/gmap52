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

public class MSSQLSupport extends AbstractDBSupport {

	public String castDateField(String value) {
		return castDatetimeField(value);
	}

	public String castDatetimeField(String value) {
		if (null == value || value.equals(""))
			return "NULL";
		return "'" + StringTools.doubleApos(value) + "'";
	}
	  /**
	   * ��װ���ɸ���rownum��ȡָ���ε����ݵ�sql�ı�
	   * @param sql
	   * @return
	   * TODO �����ݿ���Ƿ���order by������һ��Ƕ�ף�������
	   */
	  public String wrapPaginationSql(String sql){
		  return sql;
	  }
	  
	  
	  //TODO: ���sql server �����޸�
	  public String getWfdataSearchSql(String tableName, List colNames, String listtype, boolean filterBySv) {
	  	StringBuffer buffer = new StringBuffer(128);
	  	String fieldStr = "";
	  	List cols = DataTools.getUniqlList(colNames);
	  	TableMeta tableMeta = MetaManager.getTableMeta(tableName);
	  	SQLCluster sqls = createColumns(tableMeta, cols);

	  	String sqlText = "select " + sqls.getColumns() + " from " + tableName + sqls.getFrom();// + " where " + sqls.getWhere();
	  	if (sqls.getWhere().length() > 0) {
	  		sqlText += " where " + sqls.getWhere();
	  		sqlText += "and exists(";
	  	} else {
	  		sqlText += " where exists(";
	  	}
	  	sqlText += this.getWFCondition(tableName, listtype) + ")";
	  	return sqlText;
	  }
	  
	  //	TODO: ���sql server �����޸�
	  public SQLCluster createColumns(TableMeta meta, List lsListNames) {
	    StringBuffer columnNames = new StringBuffer();
	    List lsFrom = new ArrayList();
	    Map refToKey = new HashMap();
	    for (int i = 0; i < lsListNames.size(); i++) {
	      String fieldName = (String) lsListNames.get(i);
	      Field field = meta.getField(fieldName);
	      if(null!=field){
	        if (columnNames.length() > 0) {
	          columnNames.append(",");
	        }
	        columnNames.append(buildColumnName(meta, field, lsFrom, refToKey));
	      }
	    }

	    SQLCluster sqls = new SQLCluster();
	    sqls.setColumns(columnNames.toString());

	    StringBuffer s = new StringBuffer();
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
	  
	  //	TODO: ���sql server �����޸�
	  private String buildColumnName(TableMeta meta, final Field field,
	      List lsFrom, Map refToKey) {
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
	      //TODO:�˴�ֻ����һ�������Ҳδ���Ǳ�������
	      Foreign foreign = meta.getForeign(refName);
	      CompoMeta comMeta = MetaManager.getCompoMeta(foreign.getCompoName());
	      TableMeta foreignTable = comMeta.getTableMeta();
	      String foreignTName = foreignTable.getName();
	      List lsFKey = foreignTable.getKeyFieldNames(); // foreign��key����ȫ��
	      for (int i = 0; i < lsFKey.size(); i++) {
	        String strKey = (String) lsFKey.get(i); // �õ��ⲿʵ���Key����������
	        //String keyAlias = foreign.getAlias2(strKey); // ������е�����
	        if (sbWhere.length() > 0)
	          sbWhere.append(" and ");
	        sbWhere.append(refName + "." + strKey + "(+)=" + tableName + "." + strKey);
	      }
	      
	      lsFrom.add(foreignTName + " " + refName);
	      refToKey.put(refName, sbWhere.toString());
	      return "nvl(" + refName + "." + fieldName + ",' ') as " + fieldName;
	    }
	  
	  //	TODO: ���sql server �����޸�
	  private String getTableName(TableMeta mainMeta, String columnName) {
	    if (mainMeta.getFieldNames().contains(columnName)) {
	      return mainMeta.getName();
	    }
	    String result = null;
	    List lsChild = mainMeta.getChildTableNames();
	    for (int i = 0; i < lsChild.size(); i++) {
	      String childName = (String) lsChild.get(i);
	      TableMeta childMeta = mainMeta.getChildTable(childName, false);
	      result = getTableName(childMeta, columnName);
	      // TCJLODO: ������ʲô��˼��
	      break;
	    }
	    return result;
	  }
}
