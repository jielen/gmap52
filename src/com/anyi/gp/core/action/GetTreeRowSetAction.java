package com.anyi.gp.core.action;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.access.DBSupport;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.DBHelper;
import com.opensymphony.webwork.ServletActionContext;

public class GetTreeRowSetAction extends AjaxAction {

	private static final long serialVersionUID = 1L;

	private String sqlid;

	private String condition;

	private String searchCond;

  private String userNumLimCondition;
  
	private BaseDao baseDAO;

	public BaseDao getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDao baseDAO) {
		this.baseDAO = baseDAO;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getSearchCond() {
		return searchCond;
	}

	public void setSearchCond(String searchCond) {
		this.searchCond = searchCond;
	}

	public String getSqlid() {
		return sqlid;
	}

	public void setSqlid(String sqlid) {
		this.sqlid = sqlid;
	}

	public String getUserNumLimCondition() {
    return userNumLimCondition;
  }

  public void setUserNumLimCondition(String userNumLimCondition) {
    this.userNumLimCondition = userNumLimCondition;
  }

  public String doExecute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();

		String svNd = request.getParameter("svNd");
		String sCode = request.getParameter("sCode");
		String path = request.getParameter("path");
		String userId = request.getParameter("userId");
		String userCode = request.getParameter("userCode");
		String coCodes = request.getParameter("cocodes");
		String isOrgInRights = request.getParameter("isinrights");

		String cond = condition;
		String searCond = this.searchCond;
		if (searCond != null && !searCond.equals("")) {
			if (cond.equals("")) {
				cond = searCond;
			} else {
				cond += ";" + searCond;
			}
		}

		Map map = DBHelper.parseParamsSimpleForSql(cond);

		if (svNd != null)
			map.put("svNd", svNd);
		if (sCode != null)
			map.put("sCode", sCode);
		if (path != null)
			map.put("path", path);
		if (userId != null)
			map.put("userId", userId);
		if (userCode != null)
			map.put("userCode", userCode);
		if (coCodes != null)
			map.put("cocodes", coCodes);
		if (isOrgInRights != null)
			map.put("isinrights", isOrgInRights);

    List newParams = new ArrayList();
    String sqlPart = baseDAO.getSql(sqlid, map, newParams);
    String sql = sqlPart;
    if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
      DBSupport support = (DBSupport)ApplusContext.getBean("dbSupport");
      sql = support.wrapSqlByCondtion(sql, userNumLimCondition);
    }
    
		List treeList = baseDAO.queryForListBySql(sql, newParams.toArray());

		StringBuffer resultStr = new StringBuffer();
		if (treeList != null && treeList.size() > 0) {
			resultStr.append("<rowset>");

			for (int i = 0; i < treeList.size(); i++) {
				resultStr.append("<row>");
				Map tree = (Map) treeList.get(i);
				Set entrySet = tree.entrySet();
				Iterator intera = entrySet.iterator();
				while (intera.hasNext()) {
					Entry entry = (Entry) intera.next();
					String key = (String) entry.getKey();
					resultStr.append("<" + key + ">" + entry.getValue() + "</"
							+ key + ">");
				}
				resultStr.append("</row>");
			}

			resultStr.append("</rowset>");
		}

		this.resultstring = resultStr.toString();

		return SUCCESS;
	}

}
