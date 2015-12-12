package com.anyi.gp.workflow.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.workflow.WFWorkList;
import com.anyi.gp.workflow.action.GetWorklistAction;
import com.anyi.gp.workflow.util.WFConst;
import com.kingdrive.workflow.util.DateTime;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;
import com.ufgov.workflow.engine.calendar.DefaultCalendarService;

public class WorklistInterceptor extends AroundInterceptor {

	private static final long serialVersionUID = -5644244563784814945L;

	private static final Log log = LogFactory.getLog(WorklistInterceptor.class);

	private int recordCount = 0;

	protected void before(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();

		this.setSearchCondition(request);

		String compoStr = (String) request.getAttribute("compoList");

		List compoList = null;
		if (compoStr != null && compoStr.length() > 0) {
			compoList = Arrays.asList(compoStr.split(","));
		} else {
			String workType = (String) request.getAttribute("workType");
			String userId = (String) SessionUtils.getAttribute(request, "svUserID");
			if (GetWorklistAction.WORK_TYPE_TODO_LIST.equals(workType)) {
				compoList = WFWorkList.getTodoCompoListByUser(userId);
			} else if (GetWorklistAction.WORK_TYPE_DONE_LIST.equals(workType)) {
				compoList = WFWorkList.getDoneCompoListByUser(userId);
			} else {
				compoList = WFWorkList.getDraftCompoListByUser(userId);
			}
		}
		request.setAttribute("compoArray", compoList);
		print("compoArray size",""+compoList.size());
	}

	private void setSearchCondition(HttpServletRequest request) {
		String workType = (String) request.getAttribute("workType");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String searchCondition = "";
		if (startTime != null && !"".equals(startTime.trim())) {
			if ("workDoneList".equals(workType)) {
				searchCondition += " and i.EXECUTE_TIME >= '" + startTime + "'";
			} else if ("workTodoList".equals(workType)) {
				searchCondition += " and i.CREATE_TIME >= '" + startTime + "'";
			}
		}
		if (endTime != null && !"".equals(endTime.trim())) {
			if ("workDoneList".equals(workType)) {
				searchCondition += " and i.EXECUTE_TIME <= '" + endTime + "'";
			} else if ("workTodoList".equals(workType)) {
				searchCondition += " and i.CREATE_TIME <= '" + endTime + "'";
			}
		}
		if (!"".equals(searchCondition)) {
			searchCondition = searchCondition.substring(4);
		}
		print("searchCondition",searchCondition);
		request.setAttribute("searchCondition", searchCondition);
	}
//private void print(int i,String msg){
//	System.out.println("No."+i+": "+msg);
//}
private void print(String index,String msg){
	System.out.println(index+": "+msg);
}
	protected void after(ActionInvocation invocation, String result)
			throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();
		Map data = (Map) request.getAttribute("data");
		String count = (String) request.getAttribute("count");
		String workType = (String) request.getAttribute("workType");
		String dataCount = "";
		if (GetWorklistAction.WORK_TYPE_TODO_LIST.equals(workType)) {
			data.putAll(getOtherWorkListData());
			dataCount = (String) request.getAttribute("dataCount");
			recordCount = Integer.parseInt(count) + Integer.parseInt(dataCount);
		} else {
			recordCount = Integer.parseInt(count);
		}
		PrintWriter out = null;

		try {
			response.setContentType("text/xml; charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			out = response.getWriter();
			out.println(outJsonData(data));
			out.flush();
		} catch (IOException e) {
			log.error(e);
		} finally {
			out.close();
		}
	}

	private String outJsonData(Map data) throws SQLException, JSONException {
		StringBuffer resultJson = new StringBuffer();
		resultJson.append("{totalCount:" + recordCount);
		resultJson.append(",dataList:");
		String jsonString = toJson(data);
		resultJson.append(jsonString);
		resultJson.append("}");
		return resultJson.toString();
	}

	/**
	 * WF_LIMIT_EXECUTE_TIME 执行期限单位为小时。 WF_REMIND_EXECUTE_TERM 提醒期限单位为小时。
	 * 
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	private String toJson(Map data) throws JSONException {
		JSONArray result = new JSONArray();

		Iterator iterator = data.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			String compoId = (String) entry.getKey();
			String compoName = LangResource.getInstance().getLang(compoId);
			List dataList = (List) entry.getValue();
			for (int i = 0; i < dataList.size(); i++) {
				JSONObject json = new JSONObject();
				Map row = (Map) dataList.get(i);
				json.put("compoId", compoId);
				json.put("compoName", compoName);
				if (row.get(WFConst.PROCESS_INST_ID_FIELD) == null) {
					json.put("processInsId", "");
				} else {
					json.put("processInsId", row.get(WFConst.PROCESS_INST_ID_FIELD));
				}
				json.put("title", row.get(WFConst.WF_PAGE_TITLE));
				json.put("brief", (String) (row.get(WFConst.WF_BRIEF)));
				String wfCreateTime = "";
				if (row.containsKey(WFConst.WF_CREATE_TIME)) {
					wfCreateTime = (String) row.get(WFConst.WF_CREATE_TIME);
					json.put("wfCreateTime", wfCreateTime);
				}
				String wfLimitExecuteTime = "";
				if (row.containsKey(WFConst.WF_LIMIT_EXECUTE_TIME)) {

					wfLimitExecuteTime = row.get(WFConst.WF_LIMIT_EXECUTE_TIME) == null ? ""
							: String.valueOf(row.get(WFConst.WF_LIMIT_EXECUTE_TIME));
					json.put("wfLimitExecuteTime", wfLimitExecuteTime);
				}
				// System.out.println("wfLimitExecuteTime===="+wfLimitExecuteTime);
				String wfRemindExecuteTerm = "";
				if (row.containsKey(WFConst.WF_REMIND_EXECUTE_TERM)) {
					wfRemindExecuteTerm = row.get(WFConst.WF_REMIND_EXECUTE_TERM) == null ? ""
							: String.valueOf(row.get(WFConst.WF_REMIND_EXECUTE_TERM));
					json.put("wfRemindExecuteTerm", wfRemindExecuteTerm);
				}
				DefaultCalendarService calendarService = new DefaultCalendarService();
				String wfStatusLamp = "0"; // 0:grey;1:green;2:yellow;3:red
				if ((wfCreateTime != null && !wfCreateTime.equals(""))
						&& (wfLimitExecuteTime != null && !wfLimitExecuteTime.equals(""))
						&& (wfRemindExecuteTerm != null && !wfRemindExecuteTerm.equals(""))) {
					try {
						Date limitExecuteTime = calendarService.dateAfter(wfCreateTime,
								Double.valueOf(wfLimitExecuteTime));
						Date remindExecuteTime = calendarService.dateAfter(wfCreateTime,
								Double.valueOf(wfRemindExecuteTerm));
						Date nowDate = new SimpleDateFormat("yyyyMMddHHmmss")
								.parse(DateTime.getSysTime());
						if (nowDate.compareTo(limitExecuteTime) > 0) {
							wfStatusLamp = "3";
						} else if (nowDate.compareTo(remindExecuteTime) < 0) {
							wfStatusLamp = "1";
						} else if (nowDate.compareTo(remindExecuteTime) >= 0
								&& nowDate.compareTo(limitExecuteTime) <= 0) {
							wfStatusLamp = "2";
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					wfStatusLamp = "1";
				}
				json.put("lamp", wfStatusLamp);

				if (row.containsKey(WFConst.WF_CREATOR_NAME)) {
					json.put("wfCreatorName", row.get(WFConst.WF_CREATOR_NAME));
				}
				if (row.containsKey(WFConst.WF_EXECUTE_TIME)) {
					json.put("wfExecuteTime", row.get(WFConst.WF_EXECUTE_TIME));
				}

				json.put("url", row.get(WFConst.WF_PAGE_URL));
				json.put("compoSize", row.get("compoSize"));
				result.put(json);
			}
		}

		return result.toString();
	}

	/**
	 * 待办提醒
	 * 
	 * @return
	 */
	public Map getOtherWorkListData() {
		Map workMap = new HashMap();
		HttpServletRequest request = ServletActionContext.getRequest();
		String userId = (String) SessionUtils.getAttribute(request, "svUserID");
		List compoList = (List) this.getCompoListByUser(userId);
		int count = 0;
		if (compoList != null) {
			for (int i = 0; i < compoList.size(); i++) {
				String compoId = (String) compoList.get(i);
				int countTmp = this.getDataCountByUserComp(userId, compoId);
				if (countTmp > 0) {
					int start = Integer.parseInt(request.getParameter("start"));
					int limit = Integer.parseInt(request.getParameter("limit"));
					List wfdata = this.getWrappedDataListByUserComp(userId, compoId,
							start + 1, start + limit);
					GetWorklistAction action = new GetWorklistAction();
					wfdata = action.populateUrl(compoId, wfdata);
					wfdata = action.addCompoSize(countTmp + "", wfdata);
					workMap.put(compoId, wfdata);
					count += countTmp;
				}
			}
		}
		request.setAttribute("dataCount", count + "");
		return workMap;
	}

	public List getCompoListByUser(String userId) {
		String sql = "SELECT t.COMPOID from V_GK_CURRENT_TASK t WHERE t.USERID = ? GROUP BY t.COMPOID";
		List result = new ArrayList();
		try {
			BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
			List rows = dao.queryForListBySql(sql, new String[] { userId });
			String compoName = "";
			for (int i = 0; i < rows.size(); i++) {
				Map row = (Map) rows.get(i);
				compoName = (String) row.get("COMPOID");
				result.add(compoName);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return result;
	}

	public int getDataCountByUserComp(String userId, String compoId) {
		String sql = "select count(*) as dataCount from V_GK_CURRENT_TASK where userId = ? and compoId = ?";
		List params = new ArrayList();
		params.add(userId);
		params.add(compoId);
		try {
			BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
			Object result = dao.queryForObjectBySql(sql, params.toArray());
			if (result != null) {
				return Integer.valueOf(result.toString()).intValue();
			}
		} catch (Exception ex) {
			log.error(ex);
		}
		return 0;
	}

	public List getWrappedDataListByUserComp(String userId, String compoId,
			int start, int end) {
		List dataList = new ArrayList();
		List params = new ArrayList();
		String sql = "select * from V_GK_CURRENT_TASK where userId = ? and compoId = ?";
		sql = DBHelper.wrapPaginationSqlForOracle(sql);
		params.add(userId);
		params.add(compoId);
		params.add(end + "");
		params.add(start + "");
		try {
			BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
			dataList = dao.queryForListBySql(sql, params.toArray());
		} catch (Exception e) {
			log.error(e);
		}
		return dataList;
	}

}
