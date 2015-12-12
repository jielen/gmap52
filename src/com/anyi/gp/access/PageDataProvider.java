package com.anyi.gp.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.anyi.gp.Datum;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DBHelper;

/**
 * 
 * ��ȡҳ������
 * 
 * @author liuxiaoyong
 * 
 */
public class PageDataProvider {

	private BaseDao dao;

	private DBSupport support;

	private String userNumLimCondition;// ��ֵȨ��sqlƬ��

	private List userNumLimParams;// ��ֵȨ�޲���

	public void setDao(BaseDao dao) {
		this.dao = dao;
	}

	public void setSupport(DBSupport support) {
		this.support = support;
	}

	public void setUserNumLimCondition(String userNumLimCondition) {
		this.userNumLimCondition = userNumLimCondition;
	}

	public void setUserNumLimParams(List userNumLimParams) {
		this.userNumLimParams = userNumLimParams;
	}

	/**
	 * ����sqlid��params��ȡ��ҳ����
	 * 
	 * @param pageIndex
	 * @param totalCount
	 * @param pageSize
	 * @param tableName
	 * @param sql
	 * @param params
	 * @return
	 */
	public Datum getPaginationData(int pageIndex, int totalCount, int pageSize,
			String tableName, String sqlid, Map params, boolean isBlank) {

		List newParams = new ArrayList();
		String sqlPart = dao.getSql(sqlid, params, newParams);
		String sql = sqlPart;

		if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
			sql = support.wrapSqlByCondtion(sql, userNumLimCondition);
			if (userNumLimParams != null)
				newParams.addAll(userNumLimParams);
		}

		if (isBlank) {
			Map tp = new HashMap();
			tp.put("1", "0");
			sql = support.wrapSqlByParams(sql, tp, newParams);
			if (support instanceof MSSQLSupport) {
				newParams.add("1");// ��֤��getPaginationData�п����Ƴ�start��max
				newParams.add("1");
			}
		} else {
			newParams.add(((Map) params).get("rownum"));
			newParams.add(((Map) params).get("rn"));
			sql = support.wrapPaginationSql(sql);
		}

		return getPaginationData(pageIndex, totalCount, pageSize, tableName,
				sql, newParams, isBlank);
	}

	/**
	 * ����sql��params��ѯ��ҳ����
	 * 
	 * @param pageIndex
	 * @param totalCount
	 * @param pageSize
	 * @param tableName
	 * @param sql
	 * @param params
	 * @param isBlank
	 * @return
	 */
	public Datum getPaginationData(int pageIndex, int totalCount, int pageSize,
			String tableName, String sql, List params, boolean isBlank) {

		Map fieldMap = new HashMap();
		if (tableName != null && tableName.length() > 0) {
			TableMeta meta = MetaManager.getTableMeta(tableName);
			if (meta != null) {
				List fieldNameList = meta.getFieldNames();
				if (fieldNameList != null && fieldNameList.size() > 0) {
					for (int i = 0; i < fieldNameList.size(); i++) {
						String fieldName = (String) fieldNameList.get(i);
						Field field = meta.getField(fieldName);
						fieldMap.put(fieldName, field);
					}
				}
			}
		}

		DataMapperResultSetExtractor handler = new DataMapperResultSetExtractor();
		handler.setFieldMap(fieldMap);

		List data = (List) dao.queryPagination(sql, params, handler);

		Datum datum = new Datum();
		datum.setData(data);
		datum.setName(tableName);
		if (handler.isBlank()) {
			datum.addMetaField("pageindex", "1");
			datum.addMetaField("rowcountofpage", "0");
			datum.addMetaField("fromrow", "0");
			datum.addMetaField("torow", "0");
			datum.addMetaField("rowcountofdb", "0");
		} else {
			datum.addMetaField("pageindex", pageIndex + "");
			datum.addMetaField("rowcountofpage", pageSize + "");
			datum.addMetaField("fromrow", pageSize * (pageIndex - 1) + 1 + "");
			datum.addMetaField("torow", pageSize * pageIndex + "");
			datum.addMetaField("rowcountofdb", totalCount + "");
		}
		return datum;
	}

	public Datum getEditPageData(String tableName, String sqlid,
			String condition) {
		Map params = new HashMap();
		DBHelper.parseParamsSimpleForSql(condition, params);

		boolean isBlank = false;
		if (condition.indexOf("1=0") >= 0) {
			isBlank = true;
		}
		return getPageData(0, 0, 0, tableName, sqlid, params, isBlank);
	}

	/**
	 * ��ȡ����
	 * 
	 * @param pageIndex
	 * @param totalCount
	 * @param pageSize
	 * @param tableName
	 * @param sqlid
	 * @param params
	 * @return
	 */
	public Datum getPageData(int pageIndex, int totalCount, int pageSize,
			String tableName, String sqlid, Map params, boolean isBlank) {

		List newParams = new ArrayList();
		String sql = dao.getSql(sqlid, params, newParams);
		if (isBlank) {
			Map tp = new HashMap();
			tp.put("1", "0");
			sql = support.wrapSqlByParams(sql, tp, newParams);
		}

		if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
			sql = support.wrapSqlByCondtion(sql, userNumLimCondition);
			if (userNumLimParams != null)
				newParams.addAll(userNumLimParams);
		}

		return getPageData(pageIndex, totalCount, pageSize, tableName, sql,
				newParams, isBlank);
	}

	/**
	 * ����sql��params��ȡ����
	 * 
	 * @param pageIndex
	 * @param totalCount
	 * @param pageSize
	 * @param tableName
	 * @param sql
	 * @param params
	 * @param isBlank
	 * @return
	 */
	public Datum getPageData(int pageIndex, int totalCount, int pageSize,
			String tableName, String sql, List params, boolean isBlank) {

		Map fieldMap = new HashMap();
		if (tableName != null && tableName.length() > 0) {
			TableMeta meta = MetaManager.getTableMeta(tableName);
			if (meta != null) {
				List fieldNameList = meta.getFieldNames();
				if (fieldNameList != null && fieldNameList.size() > 0) {
					for (int i = 0; i < fieldNameList.size(); i++) {
						String fieldName = (String) fieldNameList.get(i);
						Field field = meta.getField(fieldName);
						fieldMap.put(fieldName, field);
					}
				}
			}
		}

		DataMapperResultSetExtractor handler = new DataMapperResultSetExtractor();
		handler.setFieldMap(fieldMap);
		List data = (List) dao.queryBySql(sql, params.toArray(), handler);

		Datum datum = new Datum();
		datum.setData(data);
		datum.setName(tableName);
		if (handler.isBlank()) {
			datum.addMetaField("pageindex", "1");
			datum.addMetaField("rowcountofpage", "0");
			datum.addMetaField("fromrow", "0");
			datum.addMetaField("torow", "0");
			datum.addMetaField("rowcountofdb", "0");
		} else {
			datum.addMetaField("pageindex", pageIndex + "");
			datum.addMetaField("rowcountofpage", pageSize + "");
			datum.addMetaField("fromrow", pageSize * (pageIndex - 1) + 1 + "");
			datum.addMetaField("torow", totalCount + "");
			datum.addMetaField("rowcountofdb", totalCount + "");
		}
		return datum;
	}

	/**
	 * ����sqlID�Ͳ�ѯ������ȡָ������ֶε�ÿ���ֶε�ֵ�ϼ�
	 * 
	 * @param sqlCode��sql
	 *            ID
	 * @param params����ѯ������map���ͣ�
	 * @param totalFields���ֶ��б�list���ͣ�
	 * @return
	 */
	public Map getPageTotalData(String sqlCode, Map params, List totalFields) {

		List newParams = new ArrayList();
		String partSql = dao.getSql(sqlCode, params, newParams);

		if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
			partSql = support.wrapSqlByCondtion(partSql, userNumLimCondition);
			if (userNumLimParams != null)
				newParams.addAll(userNumLimParams);
		}

		Map map = new HashMap();
		String sql = support.wrapSqlForTotal(partSql, totalFields);

		List result = dao.queryForListBySql(sql, newParams.toArray());
		Iterator itera = result.iterator();
		while (itera.hasNext()) {
			map.putAll((Map) itera.next());
		}

		return map;
	}

	/**
	 * ����sqlID�Ͳ�ѯ������ȡ��¼����
	 * 
	 * @param sqlCode
	 * @param params
	 * @return
	 */
	public int getTotalCount(String sqlCode, Map params) {
		List newParams = new ArrayList();
		String partSql = dao.getSql(sqlCode, params, newParams);

		if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
			partSql = support.wrapSqlByCondtion(partSql, userNumLimCondition);
			if (userNumLimParams != null)
				newParams.addAll(userNumLimParams);
		}

		String sql = support.wrapSqlForCount(partSql);

		return getTotalCount(sql, newParams);
	}

	/**
	 * ����sql��params��ȡ��¼����
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public int getTotalCount(String sql, List params) {

		int totalCount = 0;
		Object obj = dao.queryForObjectBySql(sql, params.toArray());
		if (obj != null) {
			totalCount = Integer.parseInt(obj.toString());
		}

		return totalCount;
	}
}
