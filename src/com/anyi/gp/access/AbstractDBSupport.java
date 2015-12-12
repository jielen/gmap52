package com.anyi.gp.access;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.util.WFConst;

public abstract class AbstractDBSupport implements DBSupport {

	/**
	 * 包装生成获取记录总数的sql文本
	 * 
	 * @param sql
	 * @return
	 */
	public String wrapSqlForCount(String sql) {
    sql = sql.trim().replaceAll("(\t|\n)+", " ");
    String lowerSql = sql.trim().toLowerCase();
    int pos = lowerSql.indexOf(" order by ");
    if(pos > 0){
      sql = sql.substring(0, pos);
    }
    
		StringBuffer sb = new StringBuffer();
		sb.append(" select count(1) from ( ");
		sb.append(sql);
		sb.append(" ) MASTER ");
		return sb.toString();
	}

	/**
	 * 封装合计值sql
	 * 
	 * @param sql
	 * @param fields
	 * @return
	 */
	public String wrapSqlForTotal(String sql, List fields) {
		if (fields == null)
			return sql;

    sql = sql.trim().replaceAll("(\t|\n)+", " ");
    String lowerSql = sql.trim().toLowerCase();
    int iOrder = lowerSql.indexOf(" order by ");
    if(iOrder > 0){
      sql = sql.substring(0, iOrder);
    }
    
		StringBuffer sb = new StringBuffer();
		sb.append(" select ");

		Iterator itera = fields.iterator();
		while (itera.hasNext()) {
			Object obj = itera.next();
			String field = obj.toString();
			int pos = field.indexOf(".");
			if (pos > 0) {
				field = field.substring(pos + 1);
			}
			sb.append(" sum( ");
			sb.append(field);
			sb.append(" ) as ");
			sb.append(field);
			sb.append(" ,");
		}

		return sb.substring(0, sb.length() - 1) + " from (" + sql + ") ";
	}

	/**
	 * 简单处理参数，参数通过";"隔开
	 * 
	 * @param condition
	 * @return
	 */
	public Map parseParamsSimpleForSql(String condition) {
		Map result = new HashMap();
		DBHelper.parseParamsSimpleForSql(condition, result);
		return result;
	}

	/**
	 * 根据字段的类型，格式化字段值，使得满足数据库查询的要求
	 * 
	 * @param strFieldType
	 * @param fieldValue
	 * @return
	 */
	public String formatFieldValueByType(String strFieldType, String fieldValue) {
		StringBuffer result = new StringBuffer();
		if (strFieldType.equalsIgnoreCase("Num")) {
			result.append(fieldValue);
		} else if (strFieldType.equalsIgnoreCase("Date")) {
			result.append(castDateField(fieldValue));
		} else if (strFieldType.equalsIgnoreCase("Datetime")) {
			result.append(castDatetimeField(fieldValue));
		} else {
			result.append("'" + StringTools.doubleApos(fieldValue) + "'");
		}
		return result.toString();
	}

	/**
	 * 根据tableName和参数params来形成sql和新参数
	 * 
	 * @param tableName
	 * @param params
	 * @param newParams
	 * @return
	 */
	public String wrapSqlByTableName(String tableName, Map params,
			List newParams) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from ");
		sb.append(tableName);
		sb.append(" MASTER ");
		if (params.size() != 0) {
			sb.append(" where (");
		}

		Set entrySet = params.entrySet();
		Iterator itera = entrySet.iterator();
		while (itera.hasNext()) {
			Entry entry = (Entry) itera.next();
			sb.append(" MASTER.");
			sb.append(entry.getKey());
			sb.append(" = ? and ");
			newParams.add(entry.getValue());
		}
		if (params.size() == 0)
			return sb.toString();
		return sb.toString().substring(0, sb.length() - 4)+")";
	}

	public String wrapSqlByParams(String sql, Map params, List newParams) {
    String orderSql = "";
    sql = sql.trim().replaceAll("(\t|\n)+", " ");
    String lowerSql = sql.trim().toLowerCase();
    int iOrder = lowerSql.indexOf(" order by ");
    if(iOrder > 0){
      sql = sql.substring(0, iOrder);
      orderSql = sql.substring(iOrder);
    }
    
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from (");
		sb.append(sql);
		sb.append(") MASTER where ");

		Set entrySet = params.entrySet();
		Iterator itera = entrySet.iterator();
		while (itera.hasNext()) {
			Entry entry = (Entry) itera.next();
			sb.append(entry.getKey());
			sb.append(" = ? and ");
			newParams.add(entry.getValue());
		}
		return sb.toString().substring(0, sb.length() - 4) + orderSql;
	}

	public String wrapSqlByCondtion(String sql, String condition) {
	  return wrapSqlByCondtion(sql, condition, false);
	}
	/**
	 * 用条件condition拼接sql
	 * 
	 * @param sql
	 * @param condition
	 * @return
	 */
	public String wrapSqlByCondtion(String sql, String condition, boolean replaceCondition) {
		if (condition == null || condition.length() == 0
				|| condition.equals("null")) {
			return sql;
		}
		sql = sql.trim().replaceAll("(\t|\n)+", " ");
		String lowerSql = sql.trim().toLowerCase();
		int whereIndex = indexOfKeyInOuterSql(lowerSql, " where ");
		//int joinIndex = indexOfKeyInOuterSql(lowerSql, " join ");
		int groupIndex = indexOfKeyInOuterSql(lowerSql, " group by ");
		int orderIndex = indexOfKeyInOuterSql(lowerSql, " order by ");

		String orderSql = "";
		String groupSql = "";
		String partSql = null;

		if (orderIndex > groupIndex) {// order在group的后面
			if (orderIndex > 0) {
				orderSql = sql.substring(orderIndex, sql.length());
				partSql = sql.substring(0, orderIndex);
			}
			if (groupIndex > 0) {
				groupSql = partSql.substring(groupIndex, partSql.length());
				partSql = sql.substring(0, groupIndex);
			}
		} else if (orderIndex < groupIndex) {// order在group的前面，应该报错
			if (groupIndex > 0) {
				groupSql = sql.substring(groupIndex, sql.length());
				partSql = sql.substring(0, groupIndex);
			}
			if (orderIndex > 0) {
				orderSql = partSql.substring(orderIndex, partSql.length());
				partSql = sql.substring(0, orderIndex);
			}
		} else {
			partSql = sql;
		}
		
		String lowerCond = condition.toLowerCase();
		orderIndex = lowerCond.indexOf(" order by ");
		groupIndex = lowerCond.indexOf(" group by ");
		if (orderIndex > 0) {// 存在condition中的order或group则不用sql中的order和group
			orderSql = "";
		}
		if (groupIndex > 0) {
			groupSql = "";
		}
		
    if (whereIndex > 0){
      if(replaceCondition){
        condition = " where " + condition;
        partSql = partSql.substring(0, whereIndex);//替换sql中的where条件
      }else{
        condition = " and " + condition;
      }
    }
    else{
      condition = " where " + condition;
    }
		
    partSql = partSql.trim();
    String tmp = partSql.toLowerCase();
    if(tmp.endsWith("and")){
      partSql = partSql.substring(0, partSql.length() - 3) + " ";
    }
    
		return partSql + condition + " " + groupSql + " " + orderSql;
	}

	/**
	 * 外层sql片段中是否存在关键字key的位置
	 * 
	 * @param sql
	 * @param key
	 * @return: -1表示不存在, >0表示key的位置
	 */
	private int indexOfKeyInOuterSql(String sql, String key) {
		int returnValue = -1;

		while (sql.lastIndexOf(key) > 0) {
			int pos = sql.lastIndexOf(key);
			sql = sql.substring(0, pos);
			int j = count(sql, "(");
			int k = count(sql, ")");
			if (j == k) {
				returnValue = pos;
				break;
			}
		}

		return returnValue;
	}

	private int count(String s, String c) {
		int count = 0;
		int m = s.indexOf(c);

		while (m != -1) {
			m = s.indexOf(c, m + 1);
			count++;
		}
		return count;
	}

	public String getWFCondition(String tableName, String listType) {
		String result = "";
		if (listType.equals(WFConst.WF_DRAFT_TYPE)) {
			result = "select WF_DRAFT_ID from as_wf_draft i  where "
					+ tableName
					+ ".process_inst_id=i.WF_DRAFT_ID and i.user_id=? and i.compo_id=?";
		}
		if (listType.equals(WFConst.WF_TODO_TYPE)) {
			result = "select instance_id from v_wf_current_task i where "
					+ tableName
					+ ".process_inst_id=i.instance_id and i.executor =? and i.compo_id=?";
		}
		if (listType.equals(WFConst.WF_DONE_TYPE)) {
			result = "select instance_id from v_wf_action_history i where "
					+ tableName
					+ ".process_inst_id=i.instance_id and i.executor =? and i.compo_id=?";
		}
		return result;
	}
  
  /**
   * 增加工作流选择字段的包装以及条件的过滤
   * @param sqls
   * @param mainTable
   * @param listType
   * @param filterBySv
   */
  protected SQLCluster addWorkflowSqlpart(SQLCluster sqls, String mainTable, String listType, 
    boolean filterBySv){
    if(mainTable == null || mainTable.length() == 0){
      return sqls;
    }
    
    String cols = sqls.getColumns();
    String from = sqls.getFrom();
    String where = sqls.getWhere();
    
    //System.out.println("cols:" + cols + ", from:" + from + ", where" + where);
    
    if(cols == null || cols.length() == 0
      || from == null || from.length() == 0){
      return sqls;
    }
    
    if(listType.equals(WFConst.WF_DRAFT_TYPE) || listType.equals(WFConst.WF_TODO_TYPE)
      || listType.equals(WFConst.WF_DONE_TYPE)){     
        cols += ",";   
        from += ",";     
    }
    //zhangting add  
    if(where != null && where.length() > 0) {
    	where+=" and ";
    }	
    	
    
    if (listType.equals(WFConst.WF_DRAFT_TYPE)) {
      cols += "i.WF_DRAFT_ID";
      from += "as_wf_draft i";
      where += mainTable + ".process_inst_id=i.WF_DRAFT_ID and i.user_id=? and i.compo_id=?";
    }
    if (listType.equals(WFConst.WF_TODO_TYPE)) {      
      cols += "i.CURRENT_TASK_ID TASK_ID, i.NODE_NAME WF_NODE_NAME, i.CREATOR WF_CREATOR,i.REMIND_EXECUTE_TERM WF_REMIND_EXECUTE_TERM,i.CREATOR_NAME WF_CREATOR_NAME" 
           + ", i.CREATE_TIME WF_CREATE_TIME, i.LIMIT_EXECUTE_TIME WF_LIMIT_EXECUTE_TIME";
      from += "v_wf_current_task i";
      where += mainTable + ".process_inst_id=i.instance_id and i.executor =? and i.compo_id=? ";
      if(filterBySv){
        where += " and exists (select 1 from wf_current_task_relation r where r.current_task_id = i.CURRENT_TASK_ID and (r.co_code = '*' or r.co_code = ?)) "
              + " and exists (select 1 from wf_current_task_relation r where r.current_task_id = i.CURRENT_TASK_ID and (r.org_code = '*' or r.org_code = ?)) "
              + " and exists (select 1 from wf_current_task_relation r where r.current_task_id = i.CURRENT_TASK_ID and (r.posi_code = '*' or r.posi_code = ?))" 
              ;
      }
      where += " order by i.CREATE_TIME desc";
    }
    if (listType.equals(WFConst.WF_DONE_TYPE)) {      
      cols += "i.ACTION_HISTORY_ID TASK_ID, i.NODE_NAME WF_NODE_NAME, i.EXECUTE_TIME WF_EXECUTE_TIME, i.LIMIT_EXECUTE_TIME WF_LIMIT_EXECUTE_TIME";
      from += "v_wf_action_history i";
      where += mainTable + ".process_inst_id=i.instance_id and i.executor =? and i.compo_id=? ";
      if(filterBySv){
        where += " and exists (select 1 from wf_action_history_relation r where r.action_history_id = i.action_history_id and (r.co_code = '*' or r.co_code = ?)) "
              + " and exists (select 1 from wf_action_history_relation r where r.action_history_id = i.action_history_id and (r.org_code = '*' or r.org_code = ?)) "
              + " and exists (select 1 from wf_action_history_relation r where r.action_history_id = i.action_history_id and (r.posi_code = '*' or r.posi_code = ?))";
      }
      where += " order by i.EXECUTE_TIME desc";
    }
    
    SQLCluster tmp = new SQLCluster();
    tmp.setColumns(cols);
    tmp.setFrom(from);
    tmp.setWhere(where);
    
    return tmp;
  }
}
