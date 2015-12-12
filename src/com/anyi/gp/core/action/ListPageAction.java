package com.anyi.gp.core.action;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anyi.gp.Datum;
import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.util.StringTools;
import com.opensymphony.webwork.ServletActionContext;

/**
 * 
 * 列表页面数据获取
 * 
 * @author liuxiaoyong
 * 
 */
public class ListPageAction extends PageAction {

	private static final long serialVersionUID = -8219476279851024009L;

	private int pagesize;

	private int currentpage;

	private int totalcount;

	private String direction;

	private String totalfields;
  
  private String sqlCountid;
  
  private String sqlSumid;

  private String userNumLimCondition;
  
	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setTotalfields(String totalfields) {
		this.totalfields = totalfields;
	}

	public String getSqlCountid() {
    String result = sqlCountid == null || sqlCountid.equals("")?this.sqlid:sqlCountid;
    return result;
  }

  public void setSqlCountid(String sqlCountid) {
    this.sqlCountid = sqlCountid;
  }

  public String getSqlSumid() {
    String result = sqlSumid == null || sqlSumid.equals("")?this.sqlid:sqlSumid;
    return result;
  }

  public String getUserNumLimCondition() {
    return userNumLimCondition;
  }

  public void setUserNumLimCondition(String userNumLimCondition) {
    this.userNumLimCondition = userNumLimCondition;
  }

  public void setSqlSumid(String sqlSumid) {
    this.sqlSumid = sqlSumid;
  }

  /**
	 * 业务处理方法
	 */
	public String doExecute() {
		String cond = condition;
		String searCond = this.searchCond;
		Map paramsMap = new HashMap();
		/**
		 * 判断搜索类型是否为高级搜索,如果是高级搜索，searCond是一个条件串；
		 * 如果不是高级搜索，searCond是一组值对，由"；"隔开
		 */
		DBHelper.parseParamsSimpleForSql(cond, paramsMap);
		if (!type.equalsIgnoreCase(ADVANCED_SEARCH) && !type.equalsIgnoreCase(ADVANCED_PAGINATION)) {
			DBHelper.parseParamsSimpleForSql(searCond, paramsMap);
		}
		

		if (userid == null)
			userid = (String) paramsMap.get("userid");

//		String userNumLimCondition = "";
//		if (tablename.equals(MetaManager.getCompoMeta(componame)
//				.getMasterTable())) {
//			userNumLimCondition = RightUtil.getUserNumLimCondition(ServletActionContext.getRequest(), userid,
//					"fwatch", componame, null, null);
//		}
    String sUserNumLimCondition = userNumLimCondition;
		if(type.equalsIgnoreCase(ADVANCED_SEARCH) || type.equalsIgnoreCase(ADVANCED_PAGINATION)){//搜索类型为高级搜索时，将搜索条件附加上；
      String conSql = searCond.substring(0, searCond.indexOf("/"));
      String keySql = searCond.substring(searCond.indexOf("/")+1);
      DBHelper.parseParamsSimpleForSql(keySql, paramsMap);
      if(sUserNumLimCondition != null && sUserNumLimCondition.length() > 0)
        sUserNumLimCondition += " and ";
      sUserNumLimCondition += conSql;
		}
		provider.setUserNumLimCondition(sUserNumLimCondition);

		if (totalcount < 0) {// 取总条数
			totalcount = provider.getTotalCount(this.getSqlCountid(), paramsMap);
		}

		//if (totalcount <= 0) {// 没有数据
			//return SUCCESS;
		//}

		int pageIndex = Pub.calcPageIndex(currentpage, direction, pagesize, totalcount);
		if (pageIndex <= 0)
			pageIndex = 1;

		int rowmin = (pageIndex - 1) * pagesize + 1;
		int rowmax = pageIndex * pagesize;
		paramsMap.put("rownum", rowmax + "");
		paramsMap.put("rn", rowmin + "");

		// 取分页数据
		Datum datum = provider.getPaginationData(pageIndex, totalcount, pagesize, tablename, sqlid, paramsMap, false);

		datum.addMetaField("sqlid", sqlid);
		datum.addMetaField("condition", condition);
		datum.addMetaField("searchCond", searchCond);
    datum.addMetaField("sqlCountid", this.sqlCountid);
    datum.addMetaField("sqlSumid", this.sqlSumid);
    datum.addMetaField("userNumLimCondition", this.userNumLimCondition);
    String digest = DataTools.getDigest(datum, tablename);
    datum.addMetaField("digest", digest);
    
		// 取合计数据
		Map totalData = null;
		if (totalfields != null && !totalfields.trim().equals("") && !PAGINATION.equals(type)) {
			List totalFieldList = StringTools.split(totalfields, ",");
			totalData = provider.getPageTotalData(this.getSqlSumid(), paramsMap, totalFieldList);
		}

		if (!PAGINATION.equals(type)&&!ADVANCED_PAGINATION.equals(type)) {
			resultstring = wrapResultWithSearch(datum, totalData);
		} else {
			resultstring = wrapResultWithPagination(datum);
		}

		return SUCCESS;
	}
	/**
	 * 参数处理
	 */
	public void before() {
		if (pagesize <= 0) {
			pagesize = Integer.parseInt(ApplusContext.getEnvironmentConfig()
					.get("pagesize"));
			if (pagesize <= 0) {
				pagesize = 100;
			}
		}
    this.condition = StringTools.convertXML(condition);
    this.searchCond = StringTools.convertXML(searchCond);
	}

	private String wrapResultWithSearch(Datum datum, Map totalData) {
		StringWriter out = new StringWriter();
		out.write("<?xml version=\"1.0\" encoding=\"GBK\"?>\n");
		out.write("<root>\n");

		out.write("<data>\n");
		try {
			datum.printData(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.write("</data>\n");

		out.write("<total>\n");
		if (totalData != null)
			out.write(DataTools.getDBTotal(tablename, totalfields, totalData));
		out.write("</total>\n");

		out.write("<digest></digest>\n");

		out.write("</root>\n");

		return out.toString();
	}

	private String wrapResultWithPagination(Datum datum) {
		StringWriter out = new StringWriter();
		out.write("<?xml version=\"1.0\" encoding=\"GBK\"?>\n");
		try {
			datum.printData(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}
}
