/*
 * 创建日期 2006-6-4
 *
 * 工作流功能类。
 * 主要是static的公用方法，从WFEditPageFilter,WFWorkList中移过来
 * 
 */
package com.anyi.gp.workflow.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.TableData;
import com.anyi.gp.access.DBSupport;
import com.anyi.gp.access.SQLCluster;
import com.anyi.gp.bean.FuncBean;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.taglib.ITag;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.WFFactory;
import com.anyi.gp.workflow.WFService;
import com.anyi.gp.workflow.bean.ActionBean;
import com.anyi.gp.workflow.bean.ActivityDefBean;
import com.anyi.gp.workflow.bean.BindStateInfo;
import com.anyi.gp.workflow.bean.ProcessDefBean;
import com.anyi.gp.workflow.bean.ProcessInstBean;
import com.anyi.gp.workflow.bean.VariableInfo;
import com.anyi.gp.workflow.bean.WorkitemBean;
import com.anyi.gp.workflow.userInterface.WorkflowUserFilter;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.business.Instance;
import com.kingdrive.workflow.business.Link;
import com.kingdrive.workflow.business.Node;
import com.kingdrive.workflow.business.TaskExecutor;
import com.kingdrive.workflow.business.Variable;
import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.InstanceMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.StateValueMeta;
import com.kingdrive.workflow.dto.TaskExecutorMeta;
import com.kingdrive.workflow.dto.TaskMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.dto.VariableValueMeta;
import com.kingdrive.workflow.exception.WorkflowException;

/**
 * @author zhangguanghui
 */
public class WFUtil {
	public static List userFileters = new ArrayList();

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(WFUtil.class);

	private static final String SQL_SELECT_AS_WF_BIND_STATE = "select FIELD_ID,TAB_ID,WF_STATE_ID,WF_TEMPLATE_ID"
			+ " from AS_WF_BIND_STATE" + " where WF_TEMPLATE_ID=? ";

	private static final String SQL_SELECT_AS_WF_BIND_VARIABLE = "select WF_VARIABLE, BIND_EXPRESSION ,TAB_ID ,CONDITION,"
			+ "FILTER_BY_ENTITYKEY,b.name name from AS_WF_BIND_VARIABLE a,WF_VARIABLE b"
			+ " where a.wf_variable=b.variable_id and WF_TEMPLATE_ID=? order by WF_VARIABLE";

	private static final String SQL_SELECT_AS_WF_ACTIVITY_COMPO = "select COMPO_ID, WF_ACTIVITY_COMMENT_FIELD_ID,WF_DATA_FIELDS"
			+ " from AS_WF_ACTIVITY_COMPO"
			+ " where WF_TEMPLATE_ID=? and WF_NODE_ID=?";

	private static final String SQL_SELECT_AS_WF_FUNC_ACTIVITY = "select FUNC_ID, WF_ACTION_NAME, WF_IS_BIND_COMMIT"
			+ " from AS_WF_FUNC_ACTIVITY where COMPO_ID=?"
			+ " and WF_TEMPLATE_ID=? and WF_NODE_ID=?";

	private static final String SELECT_AS_WF_ACTIVITY_FIELD = "select TAB_ID, DATA_ITEM, "
			+ "READ_WRITE, COMPO_ID "
			+ "from AS_WF_ACTIVITY_FIELD where COMPO_ID=? and WF_TEMPLATE_ID=? "
			+ "and WF_NODE_ID=? and (READ_WRITE=0 OR READ_WRITE=1 or READ_WRITE=2)";

	private static final String SELECT_AS_WF_VAR = "select name, variable_id,type from wf_variable where template_id = ?";

	/*
	 * 
	 * <entity name="WF_VARIABLE"> <row> <entity> <field name="VariableName"
	 * value="svCoCode" /> <field name="VariableValue" value="027" /> </entity>
	 * </row> <row> <entity> <field name="VariableName" value="svOrgCode" />
	 * <field name="VariableValue" value="p0015" /> </entity> </row> ...
	 * </entity>
	 */
	private static String sessionToVariableField(HttpServletRequest request) {
		StringBuffer result = new StringBuffer();
		result.append("<entity name=\"WF_VARIABLE\">\n");
		Set svSet = SessionUtils.getAllSvPropNames(request);
		Iterator iter = svSet.iterator();
		while (iter.hasNext()) {
			String svName = (String) iter.next();
			String svValue = (String) SessionUtils
					.getAttribute(request, svName);
			result.append(getVariableString(svName, svValue));
		}
		// just tests svCoLevel
		result.append(getVariableString("svCoLevel", GeneralFunc
				.getCoCodeLevel((String) SessionUtils.getAttribute(request,
						"svCoCode"))));
		// end of test
		result.append("</entity>\n");
		return result.toString();
	}

	private static String getVariableString(String varName, String varValue) {
		String result = new String();
		result += StringTools.getFieldXml("VariableName", varName);
		result += StringTools.getFieldXml("VariableValue", varValue);
		result = StringTools.getWraptXml("entity", result);
		result = StringTools.getWraptXml("row", result);
		return result;
	}

	/*
	 * 示例 <field name="WF_COMPANY_CODE" value="017203203101"/> <field
	 * name="WF_ORG_CODE" value="0001"/> <field name="WF_POSITION_ID"
	 * value="112623817006300001"/> <field name="ND" value="2006"/>
	 */
	private static String sessionToWfString(HttpServletRequest request) {
		StringBuffer result = new StringBuffer();
		result.append(StringTools.getFieldXml("WF_COMPANY_CODE", SessionUtils
				.getAttribute(request, "svCoCode")));
		result.append(StringTools.getFieldXml("WF_ORG_CODE", SessionUtils
				.getAttribute(request, "svOrgCode")));
		result.append(StringTools.getFieldXml("WF_POSITION_CODE", SessionUtils
				.getAttribute(request, "svPoCode")));
		result.append(StringTools.getFieldXml("WF_POSITION_ID", GeneralFunc
				.getOrgPosiCode(request)));
		result.append(StringTools.getFieldXml("ND", SessionUtils.getAttribute(
				request, "svNd")));
		return result.toString();
	}

	public static String getWFSessionXml(HttpServletRequest request) {
		StringBuffer result = new StringBuffer();
		result.append("<xml id=\"WFSessionXml\">\n<entity>\n");
		result.append(sessionToWfString(request));
		result.append(sessionToVariableField(request));
		result.append("</entity>\n</xml>\n");
		return result.toString();
	}

	/**
	 * wflisttype将首先从条件中取，queryParameter中取，如果没有，则从session中取，
	 * 如果仍然没有则从meta中取，如果仍没有，则取默认值WF_COMPO
	 */
	public static String getWFListType(HttpServletRequest request,
			String condition, CompoMeta meta) {
		String listType = null;
		if (null != meta && meta.isCompoSupportWF()) {
			if (null != condition) {
				listType = getListTypeFromString(condition)[0];// from
				// condition
			}
			if (null == listType) {
				listType = request.getParameter(WFCompoType.WF_COMPO_LIST_TYPE);
				if (null == listType) {
					listType = (String) request
							.getAttribute(WFCompoType.WF_COMPO_LIST_TYPE);
					if (null == listType) {
						if (StringTools.isEmptyString(meta.getWfListType())) {
							listType = WFCompoType.WF_COMPO;// default
						} else {
							listType = meta.getWfListType();
						}
					}
				}
			}
		}
		request.setAttribute(WFCompoType.WF_COMPO_LIST_TYPE, listType);
		return listType;
	}

	public static boolean isNeedHandleWithWF(String wfListType) {
		// TCJLODO Auto-generated method stub
		return wfListType != null
				&& wfListType.startsWith(WFCompoType.WF_FILTER_COMPO);
	}

	public static boolean isNeedHandleWithDraft(String wfListType) {
		// TCJLODO Auto-generated method stub
		return WFCompoType.WF_COMPO_DRAFT.equals(wfListType);
	}

	/**
	 * @return 返回数组 第一个元素为标准的listtype, 第二个元素为除了listtype=%type%或%type%的其余部分
	 */
	public static String[] getListTypeFromString(String src) {
		String result[] = new String[] { null, src };
		if (null != src) {
			if (-1 != indexOfIgnoreCase(src, WFCompoType.WF_FILTER_COMPO_TODO)) {
				result[0] = WFCompoType.WF_FILTER_COMPO_TODO;
				result[1] = filterListType(src,
						WFCompoType.WF_FILTER_COMPO_TODO);
			} else if (-1 != indexOfIgnoreCase(src,
					WFCompoType.WF_FILTER_COMPO_DONE)) {
				result[0] = WFCompoType.WF_FILTER_COMPO_DONE;
				result[1] = filterListType(src,
						WFCompoType.WF_FILTER_COMPO_DONE);
			} else if (-1 != indexOfIgnoreCase(src,
					WFCompoType.WF_FILTER_COMPO_INVALID)) {
				result[0] = WFCompoType.WF_FILTER_COMPO_INVALID;
				result[1] = filterListType(src,
						WFCompoType.WF_FILTER_COMPO_INVALID);
			} else if (-1 != indexOfIgnoreCase(src, WFCompoType.WF_FILTER_COMPO)) {
				result[0] = WFCompoType.WF_FILTER_COMPO;
				result[1] = filterListType(src, WFCompoType.WF_FILTER_COMPO);
			} else if (-1 != indexOfIgnoreCase(src, WFCompoType.WF_COMPO_DRAFT)) {
				result[0] = WFCompoType.WF_COMPO_DRAFT;
				result[1] = filterListType(src, WFCompoType.WF_COMPO_DRAFT);
			} else if (-1 != indexOfIgnoreCase(src, WFCompoType.WF_COMPO_OTHER)) {
				result[0] = WFCompoType.WF_COMPO_OTHER;
				result[1] = filterListType(src, WFCompoType.WF_COMPO_OTHER);
			} else if (-1 != indexOfIgnoreCase(src, WFCompoType.WF_COMPO)) {
				result[0] = WFCompoType.WF_COMPO;
				result[1] = filterListType(src, WFCompoType.WF_COMPO);
			}
		}
		return result;
	}

	public static int indexOfIgnoreCase(String src, String dist) {
		int index = -1;
		index = src.indexOf(dist);
		if (index == -1) {
			index = src.indexOf(dist.toUpperCase());
			if (index == -1) {
				index = src.indexOf(dist.toLowerCase());
			}
		}
		return index;
	}

	public static String filterListType(String src, String listtype) {
		String result = null;
		if (-1 != indexOfIgnoreCase(src, listtype)) {
			if (-1 != indexOfIgnoreCase(src, WFCompoType.WF_COMPO_LIST_TYPE)) {
				result = src.replaceAll(WFCompoType.WF_COMPO_LIST_TYPE + "="
						+ listtype.toUpperCase(), "");
			} else {
				result = src.replaceAll(listtype.toUpperCase(), "");
			}
		}
		return result;
	}

	/*
	 * 业务部件是否支持工作流
	 */
	public static boolean isCompoSupportWF(String compoId) {
		CompoMeta meta = MetaManager.getCompoMeta(compoId);
		if (null == meta) {
			return false;
		}
		// 如果是工作流的部件，就 不是 支持工作流的部件
		if (isWorkflowCompo(meta.getName())) {
			return false;
		} else if ((meta.isCompoSupportWF())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是工作流部件 弃用WF_TODO和WF_DONE列表类型。在列表时也不再需要切换componame
	 * 
	 * @param wf_flow_type
	 *            部件描述的是否是工作流相关的部件字段值
	 */
	public static boolean isWorkflowCompo(String compoName) {
		if (WFCompoType.AS_WF_INSTANCE_TRACE.equalsIgnoreCase(compoName)
				|| WFCompoType.WF_DONE.equalsIgnoreCase(compoName)
				|| WFCompoType.WF_TODO.equalsIgnoreCase(compoName)
				|| WFCompoType.WF_WATCH.equalsIgnoreCase(compoName)
				|| WFCompoType.WF_TEMPLATE.equalsIgnoreCase(compoName)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否需要工作流过滤列表.只有 使用工作流的部件列表类型为WF_FILTER_COMPO%才需要此列表
	 */
	public static boolean needWfFilterList(String compoName, String wfListType) {
		CompoMeta meta = MetaManager.getCompoMeta(compoName);
		if (null != meta && meta.isCompoSupportWF()
				&& isNeedHandleWithWF(wfListType)) {
			return true;
		}
		return false;
	}

	public static boolean useWfListOrIsWfList(String compoName,
			String wfListType) {
		return (needWfFilterList(compoName, wfListType) || isWorkflowCompo(compoName));
	}

	public static boolean isWfListField(String fieldName) {
		if ("WF_TASK_ID".equalsIgnoreCase(fieldName)
				|| "WF_TEMPLATE_ID".equalsIgnoreCase(fieldName)
				|| "WF_ACTIVITY_ID".equalsIgnoreCase(fieldName)) {
			return true;
		}
		return false;
	}

	// public static boolean isWfListCompo(TableMeta entityMeta){
	// String wfType=entityMeta.getWF_FLOW_TYPE();
	// if (wfType!=null && !wfType.equalsIgnoreCase("workflow")){
	// return true;
	// }
	// return false;
	// }

	// /是工作流部件，而且不用授权给用户的，所有用户都有查看权限
	public static boolean isWorkflowCompoAndImpowerFWatch(String action,
			String compoName) {
		if (action == null || "".equals(action))
			return false;
		if (compoName == null || "".equals(compoName))
			return false;
		if (action.equalsIgnoreCase("fwatch")
				|| (WFCompoType.WF_COMPO.equalsIgnoreCase(compoName))
				|| (WFCompoType.WF_COMPO_OTHER.equalsIgnoreCase(compoName))
				|| (WFCompoType.WF_ACTION.equalsIgnoreCase(compoName))
				|| (WFCompoType.WF_TEMPLATE.equalsIgnoreCase(compoName))
				|| (WFCompoType.AS_WF_INSTANCE_TRACE
						.equalsIgnoreCase(compoName))) {
			return true;
		}
		return false;
	}

	public static String wfRelatedDataToString4(HttpServletRequest request,
			TableData td) {
		int templateId = -1;
		int instanceId = -1;
		if (td == null)
			return "";
		templateId = td.getFieldInt(WFConst.WF_TEMPLATE_ID);
		instanceId = td.getFieldInt(WFConst.PROCESS_INST_ID);

		StringBuffer voBuf = new StringBuffer();
		voBuf.append("<xml id=\"WFDataXML\" asynch=\"false\">\n");
		voBuf.append("<").append(WFConst.WFDATA4).append(">\n");
		voBuf.append(wfDataToString4(request, td)).append("\n");
		if (templateId != -1)
			voBuf.append(wfStateToString4(request, td, instanceId))
					.append("\n");
		if (templateId != -1 && instanceId != -1)
			voBuf.append(
					wfVariableToString4(request, td, templateId, instanceId))
					.append("\n");
		voBuf.append("</").append(WFConst.WFDATA4).append(">\n");
		voBuf.append("</xml>\n");
		return voBuf.toString();
	}

	public static String wfDataToString4(HttpServletRequest request,
			TableData td) {
		if (td == null) {
			return "";
		}

		StringBuffer s = new StringBuffer();
		List names = td.getFieldNames();
		for (int i = 0; i < names.size(); i++) {
			String v = StringTools.ifNull(td.getFieldValue(names.get(i)
					.toString()), "");
			if (!StringTools.isEmptyString(v)) {
				v = StringTools.packSpecial(v);
				s.append("<").append(names.get(i)).append(">").append(v)
						.append("</").append(names.get(i)).append(">\n");
			}
		}
		return s.toString();
	}

	private static String wfStateToString4(HttpServletRequest request,
			TableData td, int instance) {
		try {
			if (td == null) {
				return "";
			}
			StringBuffer s = new StringBuffer();
			s.append("<").append(WFConst.WFSTATE4).append(">\n");
			s.append("<ROWSET>\n");
			List stateList = ExecuteFacade.getStateListByInstance(instance);
			for (int i = 0; i < stateList.size(); i++) {
				StateValueMeta state = (StateValueMeta) stateList.get(i);
				s.append("<ROW>\n");
				s.append("<STATE_ID>");
				s.append(state.getId());
				s.append("</STATE_ID>\n");
				s.append("<STATE_NAME>");
				s.append(state.getName());
				s.append("</STATE_NAME>\n");
				s.append("<STATE_VALUE>");
				s.append(StringTools.ifNull(state.getValue(), ""));
				s.append("</STATE_VALUE>\n");
				s.append("</ROW>\n");
			}
			s.append("</ROWSET>\n");
			s.append("</").append(WFConst.WFSTATE4).append(">\n");
			return s.toString();
		} catch (WorkflowException ex) {
			return null;
		}
	}

	private static String wfVariableToString4(HttpServletRequest request,
			TableData td, int templateId, int instanceId) {
		try {
			String varName, varValue;
			if (td == null) {
				return "";
			}
			StringBuffer s = new StringBuffer();
			s.append("<").append(WFConst.WFVARIABLE4).append(">\n");
			s.append("<ROWSET>\n");
			List variableList = ExecuteFacade.getValueListByInstnace(
					templateId, instanceId);
			for (int i = 0; i < variableList.size(); i++) {
				VariableValueMeta variable = (VariableValueMeta) variableList
						.get(i);
				varName = variable.getName();
				varValue = StringTools.ifNull(variable.getValue(), "");
				varValue = getVarValueFromSessionOrNot(request, varName,
						varValue);
				s.append("<ROW>\n");
				s.append("<VARIABLE_ID>");
				s.append(variable.getVariableId());
				s.append("</VARIABLE_ID>\n");
				s.append("<VARIABLE_NAME>");
				s.append(varName);
				s.append("</VARIABLE_NAME>\n");
				s.append("<VARIABLE_VALUE>");
				s.append(varValue);
				s.append("</VARIABLE_VALUE>\n");
				s.append("<VARIABLE_TYPE>");
				s.append(variable.getType());
				s.append("</VARIABLE_TYPE>\n");
				s.append("</ROW>\n");
			}
			s.append("</ROWSET>\n");
			s.append("</").append(WFConst.WFVARIABLE4).append(">\n");
			return s.toString();
		} catch (WorkflowException ex) {
			return null;
		}
	}

	private static String getVarValueFromSessionOrNot(
			HttpServletRequest request, String varName, String varValue) {
		String result = "";
		Set svSet = SessionUtils.getAllSvPropNames(request);
		Iterator iter = svSet.iterator();
		while (iter.hasNext()) {
			String svName = (String) iter.next();
			if (svName.equals(varName)) {
				result = SessionUtils.getAttribute(request, svName);
				if (svName.equals("svCoLevel")) {
					result = GeneralFunc.getCoCodeLevel((String) SessionUtils
							.getAttribute(request, "svCoCode"));
				}
				break;
			}
		}
		return result;
	}

	/**
	 * 判断下一结点是否已经提交:add by liubo
	 * 
	 * @param instanceId
	 * @param nodeId
	 * @return
	 */
	public static String[] instanceHasCommited(int instanceId, int nodeId) {
		String[] result = { "", "" };
		String sql = "select count(*),min(creator) from wf_current_task where instance_id=? and node_id=?";
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			sta = conn.prepareStatement(sql);
			sta.setInt(1, instanceId);
			sta.setInt(2, nodeId);
			res = sta.executeQuery();
			if (res.next()) {
				String count = res.getString(1);
				String creator = res.getString(2);
				result[0] = count;
				result[1] = creator;
			}
		} catch (Exception ex) {
			return result;
		} finally {
			DBHelper.closeConnection(conn, sta, res);
		}
		return result;
	}

	public static String getNodeIdByInstanceId(int instanceId)
			throws SQLException {
		String nodeId = "";
		List nodeList = new ArrayList();
		String sql = "select NODE_ID from WF_ACTION where INSTANCE_ID=? order by ACTION_ID";
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		Object[] params = new Integer[] { new Integer(instanceId) };
		try {
			conn = DAOFactory.getInstance().getConnection();
			sta = conn.prepareStatement(sql);
			DBHelper.setStatementParameters(sta, params);
			res = sta.executeQuery();
			while (res.next()) {
				nodeId = res.getString(1);
			}
		} finally {
			DBHelper.closeConnection(conn, sta, res);
		}
		return nodeId;
	}

	/**
	 * @param instanceId
	 * @param instId
	 * @param conn
	 * @throws NumberFormatException
	 * @throws WorkflowException
	 * @throws BusinessException
	 * @throws WFException
	 * @throws SQLException
	 */
	public static String getBriefContent(int instId, int nodeId,
			boolean isBrief, TableData bnData, ActivityDefBean activityDef)
			throws NumberFormatException, WorkflowException, BusinessException,
			WFException, SQLException {
		// TCJLODO:liubo
		StringBuffer brief = new StringBuffer(128);
		String resStr = "";
		try {
			// ActivityDefBean activityDef = findActivityDefBean(instId,
			// nodeId);
			CompoMeta meta = MetaManager.getCompoMeta(activityDef
					.getCompoName());
			if (bnData == null) {
				bnData = getBusinessDataByInsId(meta.getName(), String
						.valueOf(instId));
			}
			String strFields = null;
			if (isBrief) {
				strFields = meta.getBriefFields();// brief
			} else {
				strFields = activityDef.getDataFields();// data field
			}
			if (strFields == null || strFields.length() == 0) {
				return brief.toString();
			}
			List colNames = StringTools.split(strFields, ",", true);
			int length = colNames.size();
			for (int i = 0; i < length; i++) {
				String colName = (String) colNames.get(i);
				String colValue = bnData.getFieldValue(colName);
				if (!isBrief) {
					brief.append(colName + ":" + colValue);
				} else {
					brief.append(colValue);
				}
				brief.append(";");
			}
			resStr = brief.toString();
			if (resStr.length() > 0) {
				resStr = resStr.substring(0, resStr.length() - 1);
			}
		} catch (NumberFormatException nfe) {
			throw new WFException(nfe.toString());
		}
		return resStr;
	}

	public static TableData getBusinessDataByInsId(String compoId, String insId) {
		DBSupport dbSupport = (DBSupport) ApplusContext.getBean("dbSupport");
		List colList = new ArrayList();
		try {
			CompoMeta compoMeta = MetaManager.getCompoMeta(compoId);
			TableMeta tableMeta = compoMeta.getTableMeta();
			String tableName = tableMeta.getName();
			// List keyFieldNames = tableMeta.getKeyFieldNames();
			List allFiledNames = getMasterTableField(tableMeta);
			String titleField = compoMeta.getTitleField();
			List briefFieldNames = StringTools.split(
					compoMeta.getBriefFields(), ",");

			colList.add(WFConst.PROCESS_INST_ID_FIELD);// 1st: process_inst_id
			colList.add(titleField);// 2nd:%titlefiled%
			// colList.addAll(keyFieldNames);// 3rd:all key field names
			colList.addAll(allFiledNames);// 3rd:all field names
			colList.addAll(briefFieldNames);// following:%briefFileds%

			SQLCluster sqls = dbSupport.createColumns(tableMeta, DataTools
					.getUniqlList(colList));
			String sql = "select " + sqls.getColumns() + " from " + tableName
					+ " " + sqls.getFrom() + " where " + tableName + "."
					+ WFConst.PROCESS_INST_ID_FIELD + "=?";
			if (sqls.getWhere().length() > 0) {
				sql += " and " + sqls.getWhere();
			}
			Map data = DBHelper.queryOneRow(sql, new String[] { insId });
			return new TableData(data);
		} catch (Exception ex) {
			return new TableData();
		}
	}

	/**
	 * @see createNextExecutor(WorkitemBean , ActionBean , List ,Connection )
	 *      throws NumberFormatException, WorkflowException
	 */
	public static void createNextExecutor(int nTemplateId, int nInstanceId,
			int nActivityId, String strExecutors, String strExecutors2,
			List variableValueList, String actionName)
			throws NumberFormatException, WorkflowException {
		WorkitemBean workitem = new WorkitemBean();
		workitem.setTemplateId("" + nTemplateId);
		workitem.setProcessInstId("" + nInstanceId);
		workitem.setActivityId("" + nActivityId);
		ActionBean action = new ActionBean();
		action.setNextExecutor(strExecutors);
		action.setNextExecutor2(strExecutors2);
		action.setActionId(actionName);
		createNextExecutor(workitem, action, variableValueList);

	}

	/**
	 * 提交工作流任务前，创建下一步执行人，如果是汇总的任务还要创建子任务的下一步执行人。
	 * 
	 * @param workitem
	 * @param action
	 * @param conn
	 * @param templateId
	 * @param activityId
	 * @param processInstId
	 * @throws NumberFormatException
	 * @throws WorkflowException
	 */
	public static void createNextExecutor(WorkitemBean workitem,
			ActionBean action, List nextLinkList) throws NumberFormatException,
			WorkflowException {
		int templateId, activityId, processInstId;
		templateId = Integer.parseInt(workitem.getTemplateId().toString());
		activityId = Integer.parseInt(workitem.getActivityId().toString());
		processInstId = Integer
				.parseInt(workitem.getProcessInstId().toString());
		Object resp = workitem.getResponsibility();
		// 特殊情况时为null，也视为主办
		if (null == resp
				|| (resp != null && Integer.parseInt(resp.toString(), 10) == WFConst.MASTER_EXECUTOR_FLAG)) {
			if (!StringTools.isEmptyString(action.getNextExecutor())) {
				List newMainExeList = new ArrayList();
				List newAssiExeList = new ArrayList();

				String[] nextExecutors = StringTools.split2(action
						.getNextExecutor(), ",");
				String[] nextExecutors2 = new String[0];
				for (int i = 0; nextExecutors != null
						&& i < nextExecutors.length; i++) {
					newMainExeList.add(nextExecutors[i].trim());
				}
				if (!StringTools.isEmptyString(action.getNextExecutor2())) {
					nextExecutors2 = StringTools.split2(action
							.getNextExecutor2(), ",");
				}
				for (int i = 0; nextExecutors2 != null
						&& i < nextExecutors2.length; i++) {
					newAssiExeList.add(nextExecutors2[i].trim());
				}
				/*
				 * List linkList = new ArrayList(); List nodeLinkList = new
				 * Link().getFollowedLinkList(templateId, activityId,
				 * action.getActionId()); //
				 * 取第一个元素，是因为根据NodeId和action取得的一般只有一个link Link tempLink = (Link)
				 * nodeLinkList.get(0); linkList.add(tempLink); List
				 * nextLinksList = new Instance()
				 * .getRightFollowedTaskLinkList(processInstId, templateId,
				 * linkList, variableValueList); // 更正结束
				 */
				List nextNodes = new ArrayList();
				for (int j = 0; nextLinkList != null && j < nextLinkList.size(); j++) {
					nextNodes.add(new Node().getNode(((Link) nextLinkList
							.get(j)).getNextNodeId()));
				}
				if (null != nextNodes && nextNodes.size() > 0) {
					for (Iterator it = nextNodes.iterator(); it.hasNext();) {
						NodeMeta node = (NodeMeta) it.next();
						List executors = ExecuteFacade.getTaskExecutorList(
								processInstId, node.getId());
						List oldMainExeList = new ArrayList();
						List oldAssiExeList = new ArrayList();
						Iterator allExes = executors.iterator();
						while (allExes.hasNext()) {
							TaskExecutorMeta e = (TaskExecutorMeta) allExes
									.next();
							if (e.getResponsibility() == WFConst.MASTER_EXECUTOR_FLAG) {
								oldMainExeList.add(e.getExecutor());
							} else {
								oldAssiExeList.add(e.getExecutor());
							}
							ExecuteFacade.removeTaskExecutor(e.getId());
						}// 删除所有旧的代办任务
						for (int i = 0; i < newMainExeList.size(); i++) {
							String exeId = (String) newMainExeList.get(i);
							if (!oldMainExeList.contains(exeId)) {
								oldMainExeList.add(exeId);
							}
						}// 所有主办人列表，剔除重复项
						for (int i = 0; i < newAssiExeList.size(); i++) {
							String exeId = (String) newAssiExeList.get(i);
							if (!oldAssiExeList.contains(exeId)) {
								oldAssiExeList.add(exeId);
							}
						}// 所有辅办人列表，剔除重复项
						int i = 1;
						// 过滤代办人员
						for (int ii = 0; ii < userFileters.size(); ii++) {
							WorkflowUserFilter workflowUserFilter = (WorkflowUserFilter) userFileters
									.get(ii);
							try {
								HashSet hs = new HashSet();
								hs.addAll(oldMainExeList);
								Set set = workflowUserFilter.doFiler(hs,
										processInstId);
								oldMainExeList = new ArrayList();
								oldMainExeList.addAll(set);

								hs = new HashSet();
								hs.addAll(oldAssiExeList);
								set = workflowUserFilter.doFiler(hs,
										processInstId);
								oldAssiExeList = new ArrayList();
								oldAssiExeList.addAll(set);
							} catch (Exception e) {
								// TCJLODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						Iterator mainIterator = oldMainExeList.iterator();
						Iterator assiIterator = oldAssiExeList.iterator();
						while (mainIterator.hasNext()) {
							String exeId = (String) mainIterator.next();
							ExecuteFacade.createTaskExecutor(processInstId,
									node.getId(), exeId, i,
									WFConst.MASTER_EXECUTOR_FLAG);
							i++;
						}// 创建主办人代办任务
						while (assiIterator.hasNext()) {
							String exeId = (String) assiIterator.next();
							ExecuteFacade.createTaskExecutor(processInstId,
									node.getId(), exeId, i,
									WFConst.SECOND_EXECUTOR_FLAG);
							i++;
						}// 创建辅办人代办任务
					}
				}// node 循环
			}// 如果action里没有执行者信息，则不用管它，根据默认执行人走
		}
	}

	/**
	 * 汇总当前任务 将子任务汇总到父任务当中,即建立两个任务的父子关系
	 * 
	 * @param childTaskId
	 *            被汇总的子任务
	 * @param parentTaskId
	 *            汇总任务
	 */
	public static void collectCurrentTask(int childTaskId, int parentTaskId) {
		try {
			ExecuteFacade.collectCurrentTask(childTaskId, parentTaskId);
		} catch (WorkflowException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Object[] getMasterTabKeyFieldCondition(TableData td,
			String compoName) {
		Object[] result = new Object[2];
		String SQL_AND = " and ";
		TableMeta meta = MetaManager.getTableMetaByCompoName(compoName);
		List lsKeyFields = meta.getKeyFieldNames();
		StringBuffer resultStr = new StringBuffer();
		List paramList = new ArrayList();
		Iterator iter = lsKeyFields.iterator();
		while (null != td && iter.hasNext()) {
			String fieldName = (String) iter.next();
			resultStr.append(" " + meta.getName() + "." + fieldName + "=?");
			Field keyField = meta.getField(fieldName);
			String strFieldType = keyField.getType();
			String fieldValue = null;
			Object o = td.getField(fieldName);
			if (null != o) {
				fieldValue = o.toString();
				fieldValue = StringTools.oplt(fieldValue);
				fieldValue = StringTools.opgt(fieldValue);
			}
			/* 根据数据类型,来拼串 */
			// resultStr.append(StringTools.formatFieldValueByType(strFieldType,
			// fieldValue, DAOFactory.getWhichFactory()));
			resultStr.append(SQL_AND);
			paramList.add(StringTools.formatFieldValueByType(strFieldType,
					fieldValue));
		}
		String s = resultStr.toString();
		if (s.endsWith(SQL_AND))
			s = s.substring(1, s.length() - SQL_AND.length());
		result[0] = s;
		result[1] = paramList;
		return result;
	}

	public static List getWFVariableFromPage(TableData wfData) {
		List parameters = new ArrayList();
		List ls = wfData.getChildTables(WFConst.WFVARIABLE);
		for (int i = 0; i < ls.size(); i++) {
			TableData wfVariable = (TableData) ls.get(i);
			VariableValueMeta v = new VariableValueMeta();
			v.setName(wfVariable.getFieldValue("VariableName"));
			v.setVariableId(Integer.parseInt(wfVariable
					.getFieldValue("VariableId")));// TCJLODO:有VariableId?
			v.setValue(wfVariable.getFieldValue("VariableValue"));
			parameters.add(v);
		}
		return parameters;
	}

	public static Object[] replaceVarValue(String sql, List lsWFVariable) {
		Object[] res = new Object[2];
		List params = new ArrayList();
		List vars;
		String quots = ":";
		if (!StringTools.isEmptyString(sql) && sql.indexOf(quots) > -1) {
			VariableValueMeta wfVariable;
			vars = StringTools.findWords(sql, quots);
			for (int i = 0; i < vars.size(); i++) {
				for (int j = 0; j < lsWFVariable.size(); j++) {
					wfVariable = (VariableValueMeta) lsWFVariable.get(j);
					if (wfVariable.getName().equals(vars.get(i).toString())) {
						sql = sql.replaceAll(quots + vars.get(i) + quots, "?");
						params.add(wfVariable.getValue());
					}
				}
			}
		}
		res[0] = sql;
		res[1] = params;
		return res;
	}

	/**
	 * @param brief
	 * @param colNames
	 * @param cols
	 * @param values
	 * @param j
	 * @return
	 */
	public static String getOneFieldContent(List colNames, List cols,
			Object[] values, int j, boolean isLast, boolean isBrief) {
		String brief = "";
		String value;
		for (int k = 0; !isBrief && cols != null && k < cols.size(); k++) {
			Object[] col = (Object[]) cols.get(k);
			if (colNames.get(j) != null && colNames.get(j).equals(col[0])) {
				brief = (col[1] == null ? "" : (col[1] + ": ")); // colname
			}
		}
		value = values[j] == null ? "" : values[j].toString();
		brief += (value == null ? "" : (value + (!isLast ? ";" : "")));
		return brief;
	}

	public static List initWFVariableValueByWFData(TableData wfData) {
		List parameters = new ArrayList();
		List ls = wfData.getChildTables(WFConst.WFVARIABLE);
		try {
			Map varMap = WFUtil.getVarMap(wfData
					.getFieldInt(WFConst.WF_TEMPLATE_ID));
			for (int i = 0; i < ls.size(); i++) {
				TableData wfVariable = (TableData) ls.get(i);
				String vid = wfVariable.getFieldValue("VariableId");
				String vn = wfVariable.getFieldValue("VariableName");
				VariableMeta vm;
				if (StringTools.isEmptyString(vid)) {
					// vm = new
					// Variable().getVariable(wfData.getFieldInt(WFConst.WF_TEMPLATE_ID),
					// vn);
					// vid = vm.getId() + "";
					vm = (VariableMeta) varMap.get(vn);// 修改为不用循环查询数据，在getVarMap()中一次取出数据
					if (vm == null)
						continue;
					vid = vm.getId() + "";
				} else {
					vm = new Variable().getVariable(Integer.parseInt(wfVariable
							.getFieldValue("VariableId")));
				}
				if (0 == vm.getId())// 跳过本模板中不存在的变量
					continue;
				String vtype = null;
				if (null != vm) {
					vtype = vm.getType();
				}

				VariableValueMeta v = new VariableValueMeta();
				v.setName(wfVariable.getFieldValue("VariableName"));
				v.setVariableId(Integer.parseInt(vid));
				v.setValue(wfVariable.getFieldValue("VariableValue"));
				v.setType(vtype);
				parameters.add(v);
			}
		} catch (WorkflowException e) {
			throw new RuntimeException(e);
		}
		return parameters;
	}

	/**
	 * @param masterTab_KeyCondition
	 * @param conn
	 * @param masterTabName
	 * @param vvm
	 * @param vi
	 * @throws SQLException
	 */
	public static String getOneBoundVariableValue(
			Object[] masterTab_KeyCondition, String masterTabName,
			VariableValueMeta vvm, VariableInfo vi, List lsVariables)
			throws SQLException {
		Object o = null;
		String bindVariableValue = "";
		String sql = vi.getBind_expression();
		Object[] res = WFUtil.replaceVarValue(sql, lsVariables);
		List params = (List) res[1];
		if (vi.isIs_filter_by_entitykey()
				&& vi.getTab_id().indexOf(masterTabName) != -1) {
			if (vi.getCondition() != null
					&& vi.getCondition().trim().length() > 0) {
				sql += " and " + masterTab_KeyCondition[0];
			} else {
				sql += " where " + masterTab_KeyCondition[0];
			}
			params.addAll((List) masterTab_KeyCondition[1]);
		}
		o = DBHelper.queryOneValue(sql, params.toArray());
		if (o != null) {
			bindVariableValue = o.toString();
		}
		return bindVariableValue;
	}

	/**
	 * 根据绑定的工作流变量，设置其值
	 * 
	 * @param lsWFVariable
	 *            某模板下的工作流变量列表 元素为VariableValueMeta对象
	 * @param lsBindWFVariableInfo
	 *            某模板下绑定的工作流变量列表,其中元素为VariableInfo对象
	 * @param masterTab_KeyCondition
	 *            主表的主键过滤条件
	 */
	public static List setBindWFVariableValue(List lsWFVariable,
			List lsBindWFVariableInfo, String entityName,
			Object[] masterTab_KeyCondition) {
		boolean isFound = false;
		String masterTabName = MetaManager.getTableMetaByCompoName(entityName)
				.getName();
		// Iterator iter = lsWFVariable.iterator();
		try {
			for (int i = 0; i < lsBindWFVariableInfo.size(); i++) {
				isFound = false;
				VariableInfo vi = (VariableInfo) lsBindWFVariableInfo.get(i);
				for (int j = 0; j < lsWFVariable.size(); j++) {
					VariableValueMeta vvm = (VariableValueMeta) lsWFVariable
							.get(j);
					if (vi.getId() == vvm.getVariableId()) {
						// if(StringTools.isEmptyString(vi.getName()) &&
						// vi.getName().equals(vvm.getName())){
						isFound = true;
						String vValue = WFUtil.getOneBoundVariableValue(
								masterTab_KeyCondition, masterTabName, vvm, vi,
								lsWFVariable);

						vvm.setValue(vValue);
						break;
					} // end if
				} // end for
				if (!isFound) {// not found
					VariableValueMeta vv = new VariableValueMeta();
					VariableMeta vm = new Variable().getVariable(vi.getId());
					vv.setVariableId(vi.getId());
					vv.setName(vm.getName());
					vv.setType(vm.getType());
					String vValue2 = WFUtil.getOneBoundVariableValue(
							masterTab_KeyCondition, masterTabName, vv, vi,
							lsWFVariable);
					if ((vm.getType().equals("0"))
							&& ((vValue2 == null) || (vValue2.equals("")))) {
						vValue2 = "0";
					}
					vv.setValue(vValue2);
					lsWFVariable.add(vv);
				}
			} // end while outside
			return lsWFVariable;
		} catch (SQLException e) {
			logger.error("在向数据库查询绑定工作流变量时,出现异常,请确认绑定表达式" + "以及条件是否正确!");
			throw new RuntimeException(e);
		} catch (WorkflowException e) {
			logger.error("在向数据库查询绑定工作流变量时,出现异常,请确认绑定表达式" + "以及条件是否正确!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param processDefBean
	 * @param conn
	 * @throws WFException
	 */
	public static void findBindState(ProcessDefBean processDefBean)
			throws WFException {
		// 查找绑定了的状态,并从工作流中获取到状态值
		String sql = SQL_SELECT_AS_WF_BIND_STATE;
		Object[] params = new Integer[] { new Integer(processDefBean
				.getTemplateId().toString()) };
		List rows = null;
		try {
			rows = DBHelper.queryToList(sql, params);
		} catch (SQLException e) {
			logger.error("查询工作流绑定变量时失败!");
			e.printStackTrace();
			throw new WFException("查询工作流绑定变量时失败!");
		}

		// //返回的数值对应值的信息为0-FIELD_ID，1-TAB_ID，2-WF_STATE_ID，3-WF_TEMPLATE_ID
		Iterator iterAllBindedState = rows.iterator();
		while (iterAllBindedState.hasNext()) {
			Object[] cols = (Object[]) iterAllBindedState.next();
			BindStateInfo bindStateInfo = new BindStateInfo();
			bindStateInfo.setFieldName(cols[0].toString());
			bindStateInfo.setTabName(cols[1].toString());
			bindStateInfo.setState_id(Integer.parseInt(cols[2].toString()));

			processDefBean.addBindStateInfo(bindStateInfo);
		}
	}

	/**
	 * 增加绑定工作流变量的信息,并写入ProcessDefBean中
	 * 
	 * @param d
	 * @param conn
	 */
	public static void findBindVariable(ProcessDefBean d) {
		String sql = SQL_SELECT_AS_WF_BIND_VARIABLE;
		Object[] params = new Integer[] { new Integer(d.getTemplateId()
				.toString()) };
		List rows = null;
		try {
			rows = DBHelper.queryToList(sql, params);
		} catch (SQLException e) {
			logger.error("查询工作流绑定变量时失败!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// 返回的数值对应值的信息为0-WF_Variable,1-BIND_EXPRESSION,2-TAB_ID,3-CONDITION
		Iterator iter = rows.iterator();
		String cond;
		while (iter.hasNext()) {
			Object[] cols = (Object[]) iter.next();
			VariableInfo v = new VariableInfo();
			if (cols[0] == null || cols[1] == null || cols[2] == null)
				// || cols[3] == null)
				continue;
			if (StringTools.isEmptyString(cols[3])) {
				cond = "1=1";
			} else {
				cond = cols[3].toString();
			}
			v.setId(Integer.parseInt(cols[0].toString()));
			String condition = "";
			if (cond.trim().length() > 0)
				condition += " where " + cond;
			String bind_sql_expression = "select " + cols[1].toString()
					+ " from " + cols[2].toString() + condition;
			v.setBind_expression(bind_sql_expression);
			v.setCondition(cond);
			v.setTab_id(cols[2].toString());
			v.setName(cols[5].toString());
			if (cols[4].toString().equalsIgnoreCase("Y"))
				v.setIs_filter_by_entitykey(true);
			else
				v.setIs_filter_by_entitykey(false);
			d.addBindVariableInfo(v);
		}
	}

	/**
	 * 获得预置执行者
	 * 
	 * @param instanceId
	 * @param nodeId
	 * @param con
	 * @return
	 * @throws WorkflowException
	 */
	// 未使用
	public static Set getPreSetExecutor(int instanceId, int nodeId)
			throws WorkflowException {
		Set executors = new HashSet();
		TaskExecutor taskExecutorHandler = new TaskExecutor();
		Node nodeHandler = new Node();
		NodeMeta node = nodeHandler.getNode(nodeId);
		String executorsMethod = node.getExecutorsMethod();
		// 如果没有在运行期指派任务执行者，则根据预置执行者创建任务执行者
		// 本来方法上写的只是get，这里却进行了set，然后再去get
		taskExecutorHandler.set(instanceId, nodeId, executorsMethod);
		if (executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO)
				|| executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)) {
			List taskExecutorList = taskExecutorHandler.getExecutorList(
					instanceId, nodeId);
			for (int index = 0; index < taskExecutorList.size(); index++) {
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList
						.get(index);
				executors.add(taskExecutor.getExecutor());
			}
		}// end if 独签 或 并签
		else if (executorsMethod.equals(Node.EXECUTORS_METHOD_SERIAL)) {
			List taskExecutorList = taskExecutorHandler
					.getForemostExecutorList(instanceId, nodeId);
			for (int index = 0; index < taskExecutorList.size(); index++) {
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList
						.get(index);
				executors.add(taskExecutor.getExecutor());
			}
		}// end if 顺序签
		else {
			throw new WorkflowException(1215);
		}

		for (int i = 0; i < userFileters.size(); i++) {
			WorkflowUserFilter workflowUserFilter = (WorkflowUserFilter) userFileters
					.get(i);
			try {
				executors = workflowUserFilter.doFiler(executors, instanceId);
			} catch (Exception e) {
				// TCJLODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return executors;
	}

	/** 找活动对应的部件等 */
	public static void findActivityCompo(ActivityDefBean a)
			throws SQLException, WFException {
		checkActivityDef(a);
		String sql = SQL_SELECT_AS_WF_ACTIVITY_COMPO;
		Object[] params = new Integer[] {
				new Integer(a.getTemplateId().toString()),
				new Integer(a.getActivityId().toString()) };
		List rows = DBHelper.queryToList(sql, params);
		// 返回 0 行或多于 1 行都不行
		if (1 == rows.size()) {
			Object[] cols = (Object[]) rows.get(0);
			a.setCompoName(isNull(cols[0], "").toString());
			a.setCommentFieldName(isNull(cols[1], "").toString());
			a.setDataFields(isNull(cols[2], "").toString());
		}
	}

	/** 找活动对应的部件功能 */
	public static void findActivityFunc(ActivityDefBean a) throws SQLException,
			WFException {
		checkActivityDef(a);
		String sql = SQL_SELECT_AS_WF_FUNC_ACTIVITY;
		Object[] params = new Object[] { a.getCompoName(),
				new Integer(a.getTemplateId().toString()),
				new Integer(a.getActivityId().toString()) };
		List rows = DBHelper.queryToList(sql, params);
		List functions = new ArrayList();
		for (Iterator i = rows.iterator(); i.hasNext();) {
			Object[] cols = (Object[]) i.next();
			FuncBean func = GeneralFunc.getFunc(cols[0].toString());
			func.setWfActionId(cols[1]);
			func.setIsBindCommit(StringTools.toBooleanYN(cols[2]));
			functions.add(func);
		}
		a.setFunctions(functions);
	}

	/** 数据有效性性检查 */
	public static void checkActivityDef(ActivityDefBean a) throws WFException {
		if (null == a.getTemplateId() || null == a.getActivityId()) {
			throw new WFException("Error032431: 无效的活动定义，模板ID或节点id为空！");
		}
	}

	// ; 返回对象，null 时返回 v
	private static Object isNull(Object obj, Object defaultValue) {
		return (null == obj) ? defaultValue : obj;
	}

	/**
	 * 找活动对应业务字段读写权限
	 * 
	 * @param a
	 * @param conn
	 * @throws WFException
	 * @throws SQLException
	 */
	public static void findActivityFieldAccess(ActivityDefBean a)
			throws WFException, SQLException {
		WFUtil.checkActivityDef(a);
		String sql = SELECT_AS_WF_ACTIVITY_FIELD;
		Object[] params = new Object[] { a.getCompoName(),
				new Integer(a.getTemplateId().toString()),
				new Integer(a.getActivityId().toString()) };
		List rows = DBHelper.queryToList(sql, params);
		List m = new ArrayList();
		for (Iterator i = rows.iterator(); i.hasNext();) {
			Object[] cols = (Object[]) i.next();
			m.add(cols);
		}
		a.setNodeDefineFieldAcessList(m);
	}

	/** 将templatemeta对象转换为processdefbean对象 */
	public static ProcessDefBean copyProcessDef(TemplateMeta item) {
		ProcessDefBean b = new ProcessDefBean();
		b.setTemplateType(item.getTemplateType());
		b.setTemplateId(new Integer(item.getTemplateId()));
		b.setName(item.getName());
		// TCJLODO:...
		return b;
	}

	/** 将NodeMeta对象转换为ActivityDefBean对象 */
	public static ActivityDefBean copyActivityDef(NodeMeta nodeMeta) {
		ActivityDefBean a = new ActivityDefBean();
		a.setTemplateId(new Integer(nodeMeta.getTemplateId()));
		a.setActivityId(new Integer(nodeMeta.getId()));
		a.setName(nodeMeta.getName());
		a.setDescription(nodeMeta.getDescription());
		// a.setBusinessType(nodeMeta.getBusinessType());
		return a;
	}

	public static ProcessInstBean copyProcessInst(TaskMeta task) {
		ProcessInstBean b = new ProcessInstBean();
		b.setProcessInstId(new Integer(task.getInstanceId()));
		b.setTemplateType(task.getTemplateType());
		b.setTemplateId(new Integer(task.getTemplateId()));
		b.setName(task.getInstanceName());
		b.setDescription(task.getInstanceDescription());
		return b;
	}

	public static ProcessInstBean copyProcessInst(InstanceMeta inst) {
		ProcessInstBean b = new ProcessInstBean();
		b.setProcessInstId(new Integer(inst.getInstanceId()));
		// ++b.setTemplateType(inst.getTemplateType());
		b.setTemplateId(new Integer(inst.getTemplateId()));
		b.setName(inst.getName());
		b.setDescription(inst.getDescription());
		// ...
		return b;
	}

	/**
	 * 注意: 执行者、执行时间等
	 */
	public static WorkitemBean copyWorkitem(CurrentTaskMeta task) {
		WorkitemBean b = new WorkitemBean();
		b.setProcessInstId(new Integer(task.getInstanceId()));
		b.setWorkitemId(new Integer(task.getCurrentTaskId()));
		b.setTemplateId(new Integer(task.getTemplateId()));
		b.setActivityId(new Integer(task.getNodeId()));
		b.setResponsibility(new Integer(task.getResponsibility()));
		b.setInstanceName(task.getInstanceName());
		b.setInstanceDescription(task.getInstanceDescription());

		// System.out.println("内部给值:" + task.getResponsibility());
		return b;
	}

	/**
	 * 注意: 没有工作项ID
	 */
	public static WorkitemBean copyWorkitem(ActionMeta action) {
		WorkitemBean b = new WorkitemBean();
		b.setProcessInstId(new Integer(action.getInstanceId()));
		// ? b.setWorkitemId(new Integer(action.getCurrentTaskId()));
		b.setTemplateId(new Integer(action.getTemplateId()));
		b.setActivityId(new Integer(action.getNodeId()));
		b.setActivityName(action.getNodeName());
		b.setActivityState(WFConst.ACT_DONE); // _hd: 应该是已完成的
		b.setExecutor(action.getExecutor());
		b.setExecutorName(action.getExecutorName());
		b.setExecuteTime(action.getExecuteTime());
		b.setComment(action.getDescription());
		return b;
	}

	public static String getTemplateByInsId(String instanceId) {
		String sql = "select template_id from wf_instance where instance_id = ?";
		Object value = DBHelper.queryOneValue(sql, new Object[] { instanceId });
		return value.toString();

	}

	public static ActivityDefBean findActivityDefBean(boolean isDraft,
			int nTemplateId, int nActivityId) throws WorkflowException,
			WFException {
		ActivityDefBean activityDef = null;
		WFService wfs = WFFactory.getInstance().getService();
		if (!isDraft) {
			NodeMeta node = ConfigureFacade.getStartNode(nTemplateId);
			activityDef = wfs.findActivityDef(node);
		} else {
			activityDef = wfs.findActivityDef("" + nActivityId);
		}
		return activityDef;
	}

	public static Map getNodeWfDesignFunc(ActivityDefBean activityDef) {
		Map funcMap = null;
		if (activityDef != null) {
			List funcList = activityDef.getFunctions();
			if (funcList.size() > 0) {
				funcMap = new HashMap();
				for (int i = 0; i < funcList.size(); i++) {
					FuncBean func = (FuncBean) funcList.get(i);
					funcMap.put(new Integer(i), func.getFuncId());
				}
			}
		}
		return funcMap;
	}

	public static Map getNodeAccessMap(ActivityDefBean activityDef) {
		Map voMap = null;

		// 获取工作流挂接处的字段读写权限定义
		if (activityDef != null) {
			voMap = new HashMap();
			List lsFieldAccess = activityDef.getNodeDefineFieldAcessList();
			Iterator iterFieldAccessCol = lsFieldAccess.iterator();
			while (iterFieldAccessCol.hasNext()) {
				// col[0]是tab_id,col[1]是field_name,col[2]是read_write;col[3]
				// compo_id;
				Object[] col = (Object[]) iterFieldAccessCol.next();
				String vsTable = (String) col[0];
				if (StringTools.isEmptyString(vsTable.trim())
						|| vsTable.trim().equalsIgnoreCase("none")) {
					String vsCompo = (String) col[3];
					TableMeta voMeta = MetaManager
							.getTableMetaByCompoName(vsCompo);
					vsTable = voMeta.getName();
				}

				Map voTableMap = (Map) voMap.get(vsTable);
				if (voTableMap == null) {
					voTableMap = new HashMap();
					voMap.put(vsTable, voTableMap);
				}

				int viProp = 0;
				switch (Integer.parseInt(col[2].toString())) {
				case 0: // 不可见;
					viProp += ITag.TAG_INTERFACE_FIELD_INVISIBLE;
					break;
				case 1: // 只读;
					viProp += ITag.TAG_INTERFACE_FIELD_READONLY;
					break;
				case 2: // 可读可写;
					viProp += ITag.TAG_INTERFACE_FIELD_READWRITE;
					break;
				}
				voTableMap.put(col[1], String.valueOf(viProp));
			}
		}
		return voMap;
	}

	private static Map getVarMap(int templateId) throws WorkflowException {
		Map result = new HashMap();
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet rs = null;
		VariableMeta vm = null;
		try {
			conn = ConnectionFactory.getConnection();
			sta = conn.prepareStatement(SELECT_AS_WF_VAR);
			sta.setInt(1, templateId);
			rs = sta.executeQuery();
			while (rs.next()) {
				vm = new VariableMeta();
				vm.setName(rs.getString("name"));
				vm.setId(rs.getInt("variable_id"));
				vm.setType(rs.getString("type"));
				result.put(rs.getString("name"), vm);
			}
			return result;
		} catch (SQLException ex) {
			throw new WorkflowException(2209);
		} finally {
			DBHelper.closeConnection(conn, sta, rs);
		}
	}

	public static List getRightFollowedTaskLinkList(int instanceId,
			int templateId, int nodeId, String action, List valueList)
			throws WFException {
		try {
			List nodeLinkList = new Link().getFollowedLinkList(templateId,
					nodeId, action);
			nodeLinkList = new Instance().getRightFollowedTaskLinkList(
					instanceId, templateId, nodeLinkList, valueList);
			return nodeLinkList;
		} catch (WorkflowException ex) {
			throw new WFException(ex.getMessage());
		}
	}

	public static List getRightFollowedTaskLinkList(TableData wfData,
			List valueList) throws WFException {
		int nNodeId = Integer.parseInt(wfData
				.getFieldValue(WFConst.WF_ACTIVITY_ID));
		int nInstanceId = Integer.parseInt(wfData
				.getFieldValue(WFConst.PROCESS_INST_ID));
		int nTemplateId = Integer.parseInt(wfData
				.getFieldValue(WFConst.WF_TEMPLATE_ID));
		String actionName = wfData.getFieldValue("WF_ACTION");
		return getRightFollowedTaskLinkList(nInstanceId, nTemplateId, nNodeId,
				actionName, valueList);
	}

	// 0: 处理完成 1: 正在处理 2：处理失败
	public static void setASWfState(String instanceid, String entityName,
			String status) {
		try {
			String tableName = MetaManager.getCompoMeta(entityName)
					.getTableMeta().getName();
			String sql = "update " + tableName
					+ " set WF_ASYN_STATUS=? where PROCESS_INST_ID=?";
			DBHelper.executeUpdate(sql, new String[] { status, instanceid });
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	public static void removeASWfState(String instanceid) throws WFException {
		try {
			String deleteSql = "DELETE FROM as_wf_asyn_state WHERE instanceid=?";
			DBHelper.executeUpdate(deleteSql, new String[] { instanceid });
		} catch (SQLException ex) {
			throw new WFException(ex.getMessage());
		}
	}

	private static List getMasterTableField(TableMeta meta) {
		List result = new ArrayList();
		Map fields = meta.getFields();
		Iterator iter = fields.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			Field f = (Field) entry.getValue();
			if (f.isSave()) {
				result.add(f.getName());
			}
		}
		return result;
	}

	public static void setTodoStatus(List dataList) {
		Map data = null;
		int length = dataList.size();
		if (dataList.size() > 0) {
			data = (Map) dataList.get(0);
			if (data.get("PROCESS_INST_ID") != null) {
				List ids = new ArrayList();
				for (int i = 0; i < length; i++) {
					data = (Map) dataList.get(i);
					ids.add(data.get("PROCESS_INST_ID"));
				}
				List statusList = getWfDataStatus(ids);
				for (int i = 0; i < length; i++) {
					data = (Map) dataList.get(i);
					String instanceId = data.get("PROCESS_INST_ID").toString();
					if (statusList.contains(instanceId)) {
						data.put("BACK_STATUS", "1");
					} else {
						data.put("BACK_STATUS", "0");
					}
				}
			}
		}
	}

	private static List getWfDataStatus(List ids) {
		String idString = "";
		Map data = null;
		List result = new ArrayList();

		for (int i = 0; i < ids.size(); i++) {
			idString += "'" + ids.get(i) + "',";
		}
		if (idString.length() > 0) {
			idString = idString.substring(0, idString.length() - 1);
		}
		String sql = "SELECT wt.Instance_Id FROM wf_current_task wt INNER JOIN wf_action_history ah ON wt.Instance_Id=ah.Instance_Id AND wt.executor=ah.executor AND wt.node_id=ah.node_id WHERE wt.Instance_Id IN ("
				+ idString + ")";
		BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");
		List list = dao.queryForListBySql(sql, null);
		for (int i = 0; i < list.size(); i++) {
			data = (Map) list.get(i);
			result.add(data.get("INSTANCE_ID").toString());
		}
		return result;
	}
}
