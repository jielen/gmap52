package com.anyi.gp.access;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.Field;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.meta.TableMeta;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.RightUtil;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.WFFactory;
import com.anyi.gp.workflow.WFGeneral;
import com.anyi.gp.workflow.WFService;
import com.anyi.gp.workflow.bean.ProcessInstBean;
import com.anyi.gp.workflow.util.WFConst;
import com.anyi.gp.workflow.util.WFException;
import com.anyi.gp.workflow.util.WFUtil;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.access.ActionHistoryQuery;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.ActionHistoryInfo;
import com.kingdrive.workflow.util.Sequence;
import com.opensymphony.webwork.ServletActionContext;

public class WorkflowService {
	private static final Logger logger = Logger
			.getLogger(WorkflowService.class);

	private static final String SQL_INSERT_DRAFT = "insert into as_wf_draft(WF_DRAFT_ID, WF_DRAFT_NAME, COMPO_ID, MASTER_TAB_ID, USER_ID) values (?, ?, ?, ?, ?)";

	private static final String SQL_DELETE_DRAFT = "delete from as_wf_draft where WF_DRAFT_ID = ?";

	private static final String SQL_VALID_DRAFT = "select WF_DRAFT_ID from AS_WF_DRAFT where WF_DRAFT_ID = ?";

	private DBSupport support;

	private DataSource myDataSource;

	public WorkflowService() {
	}

	public WorkflowService(DBSupport support, DataSource myDataSource) {
		this.myDataSource = myDataSource;
		this.support = support;
	}

	/**
	 * 首先创建工作流实例，然后将实例ID写入业务记录并更新数据库。
	 * 
	 * @param entity
	 *            数据，格式？
	 * @param entityName
	 *            实体名（部件名）
	 * @param wfData
	 *            工作流相关数据，包括创建者名称，创建者ID，工作流模板等等
	 * @return TableData 结构，数据项包括 WFConst.DATA_PK_VALUE, PROCESS_INST_ID,
	 *         WORKITEM_ID, 分别为业务数据主键值、工作流实例ID、新的工作项ID等
	 * @throws BusinessException
	 *             插入数据或创建工作流时发生的错误
	 */
	private String newWFInstance(TableData entity, String entityName,
			TableData wfData) throws BusinessException {
		String result = "";
		try {
			// 1. 创建工作流
			String user = wfData.getFieldValue(WFConst.USER);
			ProcessInstBean instParam = new ProcessInstBean(wfData);
			WFService wfs = WFFactory.getInstance().getService();
			ProcessInstBean inst = wfs.createInstance(user, instParam, entity);
			// 2. 将实例ID放到业务数据中 PROCESS_INST_ID
			String draftId = entity
					.getFieldValue(WFConst.PROCESS_INST_ID_FIELD);
			result = inst.getProcessInstId().toString();
			entity.setField(WFConst.PROCESS_INST_ID_FIELD, inst
					.getProcessInstId());
			// 3.1 保存业务数据
			update(entity, entityName);
			CompoMeta entityMeta = MetaManager.getCompoMeta(entityName);
			String titleField = entityMeta.getTitleField();
			if (titleField == null || titleField.length() == 0) {
				titleField = "TITLE";
			}
			String title = entity.getFieldValue(titleField);
			if (title != null && title.length() > 0) {
				ExecuteFacade.updateInstance(Integer.parseInt(result), title,
						"");
			}
			// 4. 删除草稿
			deleteDraft(draftId);
		} catch (Exception e) {
			throw new BusinessException(e.toString());
		}
		return result;
	}

	public String createDraft(String compoId, String title, String masterTabId,
			String userId) throws BusinessException {

		int draftId = Sequence.fetch(Sequence.SEQ_INSTANCE);
		draftId = draftId * (-1);// 取序列号的负值

		List params = new ArrayList();
		params.add(draftId + "");
		params.add(title);
		params.add(compoId);
		params.add(masterTabId);
		params.add(userId);

		try {
			DBHelper.executeUpdate(SQL_INSERT_DRAFT, params.toArray());
		} catch (SQLException sql) {
			throw new BusinessException(sql.getMessage());
		}

		return draftId + "";
	}

	/**
	 * 提交工作项
	 * 
	 * @param entity
	 *            实体数据，可以包含所有字段，在这里根据工作流定义对指定的字段进行更新
	 * @param entityName
	 *            实体名（部件名）
	 * @param wfData
	 *            工作流相关数据，包括工作项、下一步的活动及执行者等
	 * @return 当前工作项，包含执行人、流程变量等
	 * @throws BusinessException
	 */
	public TableData commit(TableData entity, String entityName,
			TableData wfData) throws BusinessException {
		// ; 1. 查找工作项定义 2. 提交工作流 3. 将业务数据写到业务数据表中
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.commit(entity, entityName, wfData);
	}

	/**
	 * 使其成为一个事务
	 * 
	 * @param strInstanceId
	 * @param strTemplateId
	 * @param strCompoId
	 * @param strUserId
	 * @param wfData
	 * @return
	 * @throws WFException
	 * @throws BusinessException
	 */
	public String commitSimply(String strInstanceId, String strTemplateId,
			String strCompoId, String strUserId, TableData wfData,
			TableData bnData) throws WFException, BusinessException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.commitSimply(strInstanceId, strTemplateId, strCompoId,
				strUserId, wfData, bnData);
	}

	/**
	 * 简单退回
	 */
	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData, int toWhere)
			throws BusinessException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.untreadSimply(strInstanceId, strUserId, comment, bnData,
				toWhere);
	}

	/**
	 * 重启工作流实例
	 */
	public String rework(String strInstanceId, String strUserId,
			String strComment, TableData bnData) throws BusinessException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.rework(strInstanceId, strUserId, strComment, bnData);
	}

	/**
	 * @Title 更新主表
	 * @throws BusinessException
	 * @param entity
	 * @pre $none
	 * @post $none
	 */
	public void update(TableData entity, String entityName)
			throws BusinessException {
		if (entity == null) {
			throw new RuntimeException("DBdmlBean类的update方法：参数entity为null！");
		}
		TableMeta tableMeta = MetaManager.getCompoMeta(entityName)
				.getTableMeta();

		if (tableMeta == null) {
			throw new RuntimeException("DBdmlBean类的update方法：取得TableMeta错误！");
		}
		String mainTableName = tableMeta.getName(); // 得到主表名
		Object[] res = getWhereByMainTable(entity, tableMeta);
		String setStr = (String) res[0];
		List setParams = (List) res[1];
		List allParams = new ArrayList();
		allParams.add(entity.getField(WFConst.PROCESS_INST_ID_FIELD));
		allParams.addAll(setParams);
		if (setStr.length() > 0) {
			String szSqlUpdate = "update " + mainTableName + " set "
					+ WFConst.PROCESS_INST_ID_FIELD + "=? " + " where "
					+ setStr;
			try {
				DBHelper.executeUpdate(szSqlUpdate, allParams.toArray());
			} catch (SQLException sql) {
				throw new BusinessException(sql.getMessage());
			}
		} else {
			throw new BusinessException("未找到相应的业务数据!");
		}
	}

	private Object[] getWhereByMainTable(TableData entity, TableMeta tableMeta) {
		Object[] res = new Object[2];
		String sqlText = "";
		List params = new ArrayList();
		List fields = entity.getFieldNames();
		for (int i = 0; i < fields.size(); i++) {
			String fName = (String) fields.get(i);
			String fValue = entity.getFieldValue(fName);
			Field field = tableMeta.getField(fName);
			if (fValue.trim().length() > 0 && field != null && field.isPk()) {
				fValue = StringTools.oplt(fValue);
				fValue = StringTools.opgt(fValue);
				Object temp = StringTools.formatFieldValueByType(field
						.getType(), fValue);
				params.add(temp);
				sqlText += fName + "=? and ";
			}
		}
		if (!sqlText.equals("")) {
			sqlText = sqlText.substring(0, sqlText.length() - 4);
		}
		res[0] = sqlText;
		res[1] = params;
		return res;
	}
	/**
	 * 删除业务数据和工作流程实例
	 * 
	 * @param wfData
	 *            工作流相关数据
	 * @throws BusinessException
	 * @throws RemoteException
	 */
	public void deleteWithWorkflow(TableData wfData) throws BusinessException {
		Object processInstId = null;
		int instId;
		try {
			processInstId = wfData.getFieldValue(WFConst.PROCESS_INST_ID);
			if (processInstId == null || processInstId == "") {
				processInstId = wfData.getFieldValue(WFConst.WF_INSTANCE_ID);
			}
			if (processInstId == null || processInstId == "") {
				throw new BusinessException("无法获取流程实例数据");
			}
			instId = Integer.valueOf(processInstId.toString(), 10).intValue();
		} catch (NumberFormatException e) {
			throw new BusinessException("Error041271: 无效的流程ID "
					+ wfData.getFieldValue(WFConst.PROCESS_INST_ID));
		}
		String userId = wfData.getFieldValue(WFConst.USER);
		deleteWithWorkflow(instId, userId);
	}

	public void deleteWithWorkflow(String deltaStr) throws BusinessException {
		Delta delta = new Delta(deltaStr);
		Iterator iter = delta.iterator();
		try {
			while (iter.hasNext()) {
				TableData wfTableData = (TableData) iter.next();
				WorkflowService workflowService = (WorkflowService) ApplusContext
						.getBean("workflowService");
				workflowService.deleteWithWorkflow(wfTableData);
			}
		} catch (Exception e) {
			throw new BusinessException("WorkflowService类的deleteInstance方法错误，"
					+ e.toString());
		}
	}

	public void deleteWithWorkflow(int instId, String userId)
			throws BusinessException {
		try {
			/* 1. 删除所有与流程数据 */
			WFService wfs = WFFactory.getInstance().getService();
			wfs.deleteInstance(userId, new Integer(instId));
			/* 2.删除行管理器历史纪录 */
			// deleteActionHistoryRowManagerData(instId);
		} catch (WFException e) {
			throw new BusinessException(e.toString());
		}
	}

	public void deleteDraft(String draftId) throws BusinessException {
		try {
			DBHelper.executeUpdate(SQL_DELETE_DRAFT, new Object[] { draftId });
		} catch (SQLException sql) {
			throw new BusinessException(sql.getMessage());
		}
	}

	// cuiliguo 2006.05.28
	public String wfNewCommit(Delta data, String entityName, String user,
			TableData wfData) throws BusinessException {
		String result = "";
		// 分条处理数据
		for (Iterator iter = data.iterator(); iter.hasNext();) {
			TableData entityData = (TableData) iter.next();
			result = wfNewCommit(entityData, entityName, user, wfData);
		}
		return result;
	}

	public String wfNewCommit(TableData entityData, String entityName,
			String user, TableData wfData) throws BusinessException {
		// String entityDataXml = entityData.toXML();
		String draftId = "";
		String strTemplateId = wfData.getFieldValue(WFConst.WF_TEMPLATE_ID);
		String result = "";
		try {
			draftId = entityData.getFieldValue(WFConst.PROCESS_INST_ID_FIELD);
			if (null != draftId && draftId.length() > 1) {
				if (isValidDraft(draftId)) {
					wfData.setField(WFConst.WF_INSTANCE_ID, draftId);
					// 1、新建实例
					String strInstanceId = newWFInstance(entityData,
							entityName, wfData);
					entityData.setField(WFConst.PROCESS_INST_ID_FIELD,
							strInstanceId);
					wfData.setField(WFConst.WF_INSTANCE_ID, strInstanceId);
					// 2、自动提交
					result = commitSimply(strInstanceId, strTemplateId,
							entityName, user, wfData, entityData);
					// 如果成功了，返回实例Id，而不是success
					/*
					 * if ("success".equals(result)) { result = strInstanceId; }
					 */
				} else
					throw new BusinessException("单据:" + draftId
							+ " 已送审，无需重复操作！");
			} else
				throw new BusinessException("单据:" + draftId + " 没有流程信息，无法送审！");
		} catch (Exception e) {
			throw new BusinessException(e.toString() + "\n" + "有问题的单据:"
					+ draftId);
		}
		return result;
	}

	private boolean isValidDraft(String draftId) throws BusinessException {
		if (!draftId.startsWith("-"))
			return false;
		if ("-1".equals(draftId))// 特殊的,不产生草稿但要送审的单据的特殊草稿id
			return true;
		Object result = DBHelper.queryOneValue(SQL_VALID_DRAFT,
				new Object[] { draftId });
		return (result != null);
	}

	public void interruptInstance(String instanceId, String svUserId,
			String comment) throws BusinessException {
		WFService wfs = WFFactory.getInstance().getService();
		wfs.interruptInstance(instanceId, svUserId, comment);
	}

	/**
	 * 工作流通用功能
	 * 
	 * @param wfGenerel
	 *            工作流通用功能调用接口
	 * @param parameter
	 *            函数调用所需的参数
	 * @return
	 * @throws BusinessException
	 */
	public Delta wfCommonAction(Map parameter) throws BusinessException {
		String strProcessInstId = (String) parameter
				.get(WFConst.WF_PROCESS_INST_ID);
		String strActivityId = (String) parameter.get(WFConst.WF_ACTIVITY_ID);
		String svUserId = (String) parameter.get("svUserId");
		String strWorkitemId = (String) parameter.get(WFConst.WF_WORKITEM_ID);
		String comment = (String) parameter.get("comment");
		String[] userIds = (String[]) parameter.get("userIds");
		String[] pUserIds = (String[]) parameter.get("pUserIds");
		String[] sUserIds = (String[]) parameter.get("sUserIds");
		String strBnData = (String) parameter.get("strBnData");
		int[] positionIds = (int[]) parameter.get("positionIds");
		String funcname = (String) parameter.get("funcname");
		Delta result = null;

		try {
			WFGeneral wfGenerel = new WFGeneral();
			if (funcname.equalsIgnoreCase("impower")) {
				result = wfGenerel.impowerAndHandover(strProcessInstId,
						strActivityId, strWorkitemId, userIds, positionIds,
						svUserId, comment, "impower", strBnData);
			}
			if (funcname.equalsIgnoreCase("handover")) {
				result = wfGenerel.impowerAndHandover(strProcessInstId,
						strActivityId, strWorkitemId, userIds, positionIds,
						svUserId, comment, "handover", strBnData);
			}
			if (funcname.equalsIgnoreCase("withdraw")) {
				result = wfGenerel
						.withdrawAndCallback(strProcessInstId, strActivityId,
								svUserId, comment, "withdraw", strBnData);
			}
			if (funcname.equalsIgnoreCase("callback")) {
				// cuiliguo 2006.05.30 如果是批量退回，则 activityId 的值是 -1 。
				int activityId = Integer.parseInt(strActivityId);
				if (activityId == -1) {
					int processInstanceId = Integer.parseInt(strProcessInstId);
					ActionHistoryQuery actionQuery = new ActionHistoryQuery();
					List actions = actionQuery
							.getActionListByExecutorAndInstance(svUserId,
									processInstanceId);
					ListIterator iterator = actions.listIterator();
					String lastTime = "", t = "";
					Integer nodeId = null;
					while (iterator.hasNext()) {
						ActionHistoryInfo actionInfo = (ActionHistoryInfo) iterator
								.next();
						t = actionInfo.getExecuteTime();
						if (lastTime.compareToIgnoreCase(t) < 0) {
							lastTime = t;
							nodeId = actionInfo.getNodeId();
						}
					}
					strActivityId = nodeId.toString();
				}
				result = wfGenerel
						.withdrawAndCallback(strProcessInstId, strActivityId,
								svUserId, comment, "callback", strBnData);
			}
			if (funcname.equalsIgnoreCase("transferflow")) {
				result = wfGenerel.transferFlow((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), (String) parameter
						.get(WFConst.WF_NODE_ID), (String) parameter
						.get("nextNode"), svUserId, comment, pUserIds,
						sUserIds, strBnData);
			}
			if (funcname.equalsIgnoreCase("untreadflow")) {
				result = wfGenerel.untreadFlow((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), strActivityId,
						(String) parameter.get("nextNode"), svUserId, comment,
						pUserIds, sUserIds, strBnData);
			}
			if (funcname.equalsIgnoreCase("untreadCollectFlow")) {
				result = wfGenerel.untreadCollectFlow((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), strActivityId,
						svUserId, comment, strBnData);
			}
			if (funcname.equalsIgnoreCase("interruptInstance")) {
				result = wfGenerel.interruptInstance((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), svUserId, comment);
			}
			if (funcname.equalsIgnoreCase("activateInstance")) {
				result = wfGenerel.activateInstance((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), svUserId, comment);
			}
			if (funcname.equalsIgnoreCase("deactivateInstance")) {
				result = wfGenerel.deactivateInstance((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), svUserId, comment);
			}
			if (funcname.equalsIgnoreCase("restartInstance")) {
				result = wfGenerel.restartInstance((String) parameter
						.get(WFConst.WF_PROCESS_INST_ID), svUserId, comment);
			}
		} catch (SQLException e) {
			logger.error(e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(e.getMessage());
		}
		return result;
	}

	public List getWrappedWfdataListByUserComp(String userId, String compoId,
    String listType, String searchCondition, int start, int end) throws BusinessException{
	  CompoMeta compoMeta = MetaManager.getCompoMeta(compoId);
    TableMeta tableMeta = compoMeta.getTableMeta();
    List keyFieldNames = tableMeta.getKeyFieldNames();
    List briefFieldNames = StringTools.split(compoMeta.getBriefFields(),",");
    String titleField = compoMeta.getTitleField();
    
    List queryResult = _getWfdataListByUserComp(userId, compoId, listType, searchCondition, start, end); 
    int size = queryResult == null ? 0 : queryResult.size();
    if (size > 0) {
      for (int i = 0; i < size; i++) {
        Map rowData = (Map) queryResult.get(i);
        rowData.put(WFConst.WF_BRIEF, getWrappedWFBrief(briefFieldNames, rowData));
        rowData.put(WFConst.WF_CONDITION, getWrappedWFCondtion(keyFieldNames, rowData));
        rowData.put(WFConst.WF_PAGE_TITLE, rowData.get(titleField));
      }
    }
    return queryResult;
	}
  public List getWrappedWfdataListByUserComp(String userId, String compoId,
    String listType, int pageSize) throws BusinessException{
    return this.getWrappedWfdataListByUserComp(userId, compoId, listType, null, 1, pageSize);
  }
  
  protected List _getWfdataListByUserComp(String userId, String compoId,
    String listType, int pageSize) throws BusinessException{
    return this._getWfdataListByUserComp(userId, compoId, listType, null, 1, pageSize);
  }
  
  protected List _getWfdataListByUserComp(String userId, String compoId,
    String listType, String searchCondition, int start, int end) throws BusinessException{
    List params = new ArrayList();
    String sql = _getWfdataSqlByUserComp(userId, compoId, listType, searchCondition, params);
    params.add(end + "");
    params.add(start + "");
    
    try {
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");      
      String pageSql = support.wrapPaginationSql(sql);
      System.out.println("workflowservice._getWfdataListByUserComp:"+sql+" ");
      for(int i=0;i<params.size();i++){
    	  System.out.println(params.get(i));
      }
      return dao.queryForListBySql(pageSql, params.toArray());
    }catch (Exception ex) {
      throw new BusinessException(ex.getMessage());
    }
  }
  
  protected String _getWfdataSqlByUserComp(String userId, String compoId,
    String listType, String searchCondition, List newParams){
    List colList = new ArrayList();

    CompoMeta compoMeta = MetaManager.getCompoMeta(compoId);
    TableMeta tableMeta = compoMeta.getTableMeta();
    String tableName = tableMeta.getName();
    List keyFieldNames = tableMeta.getKeyFieldNames();
    String titleField = compoMeta.getTitleField();
    List briefFieldNames = StringTools.split(compoMeta.getBriefFields(),",");

    colList.add(WFConst.PROCESS_INST_ID_FIELD);// 1st: process_inst_id
    colList.add(titleField);// 2nd:%titlefiled%
    colList.addAll(keyFieldNames);// 3rd:all key field names
    colList.addAll(briefFieldNames);// following:%briefFileds%

    boolean filterBySv = false;
    HttpServletRequest request = ServletActionContext.getRequest();
    if(request != null){
      Object tmp = request.getParameter("filterWfdataBySv");
      if(tmp == null){
        tmp = request.getAttribute("filterWfdataBySv");
      }
      filterBySv = Pub.parseBool(tmp);
    }
    
    String sql = support.getWfdataSearchSql(tableName, colList, listType, filterBySv);
    if(newParams != null){ 
      newParams.add(userId);
      newParams.add(compoId);
      if(filterBySv){
        newParams.add(SessionUtils.getAttribute(request, "svCoCode"));
        newParams.add(SessionUtils.getAttribute(request, "svOrgCode"));
        newParams.add(SessionUtils.getAttribute(request, "svPoCode"));
      }
    }
    
    String userNumLimCondition = RightUtil.getUserNumLimCondition(ServletActionContext.getRequest(), userId,
          "fwatch", compoId, null, null);
    if (userNumLimCondition != null && userNumLimCondition.length() > 0) {
      userNumLimCondition = userNumLimCondition.replaceAll("MASTER", tableName);
    }else{
    	userNumLimCondition = "1=1";
    }
    if(searchCondition != null && searchCondition.length() > 0){
      userNumLimCondition = "(" + userNumLimCondition + ") and (" 
      + searchCondition.replaceAll("MASTER", tableName) + ")";
    }
    
    return support.wrapSqlByCondtion(sql, userNumLimCondition);   
  }
  
  public int getWfdataCountByUserComp(String userId, String compoId,
    String listType, String searchCondition) throws BusinessException{
    List params = new ArrayList();
    String sql = _getWfdataSqlByUserComp(userId, compoId, listType, searchCondition, params);
    sql = support.wrapSqlForCount(sql);
    try {
      BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");      
      Object result = dao.queryForObjectBySql(sql, params.toArray());
      System.out.println("workflowservice.getwfdatacountbyusercomp:"+sql+"\n ");
      for(int i=0;i<params.size();i++){
    	  System.out.println(params.get(i));
      }
      if(result != null){
        return Integer.valueOf(result.toString()).intValue();
      }
      return 0;
    }catch (Exception ex) {
      throw new BusinessException(ex.getMessage());
    }
  }
	/**
	 * TODO:重构
	 * @param userId :
	 *            用户ID
	 * @param compoId :
	 *            部件ID
	 * @param listType :
	 *            工作流类型
	 * @param pageSize :
	 *            页大下
	 * @return
	 */
	public List getWfdataListByUserComp(String userId, String compoId,
			String listType, int pageSize) throws BusinessException {
    CompoMeta compoMeta = MetaManager.getCompoMeta(compoId);
    TableMeta tableMeta = compoMeta.getTableMeta();
    List keyFieldNames = tableMeta.getKeyFieldNames();
    String titleField = compoMeta.getTitleField();
    List briefFieldNames = StringTools.split(compoMeta.getBriefFields(),",");
    
    List result = new ArrayList();
	  try{
      List queryResult = _getWfdataListByUserComp(userId, compoId, listType, pageSize);
			int size = queryResult == null ? 0 : queryResult.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					List conditionOfOneRow = new ArrayList();
					Map rowData = (Map) queryResult.get(i);
					          
					conditionOfOneRow.add(getWrappedWFBrief(briefFieldNames, rowData));// Brieffield字段的值
          conditionOfOneRow.add(rowData.get(WFConst.PROCESS_INST_ID_FIELD));// process_inst_id
          conditionOfOneRow.add(getWrappedWFCondtion(keyFieldNames, rowData));
          conditionOfOneRow.add(rowData.get(titleField));// titlefield字段的值
          
					result.add(conditionOfOneRow);
				}
			}
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage());
		}
		return result;
	}

  /**
   * 获取工作流条件字符串，包含主表的主键字段和工作流的实例id的条件
   * @param keyFieldNames
   * @param rowData
   * @return
   */
  public String getWrappedWFCondtion(List keyFieldNames, Map rowData){
    String result = "";
    int keyFieldCount = keyFieldNames.size();
    String processInsId = rowData.get(WFConst.PROCESS_INST_ID_FIELD).toString();
    for (int j = 0; j < keyFieldCount; j++) {
      result += keyFieldNames.get(j) + "=" + rowData.get(keyFieldNames.get(j)) + ";";
    }
    result += "PROCESS_INST_ID=" + processInsId;
    return result;
  }
  
  /**
   * 获取工作流相关的摘要信息
   * @param briefFieldNames
   * @param rowData
   * @return
   */
  public String getWrappedWFBrief(List briefFieldNames, Map rowData){
    String brief = "";
    for (int k = 0; k < briefFieldNames.size(); k++) {
      String briefField = (String) briefFieldNames.get(k);
      if (!StringTools.isEmptyString(briefField)) {
        Object obj = rowData.get(briefField);
        if (obj instanceof Timestamp && obj !=null) {
          obj = StringTools.formatDate(obj.toString());
        }
        brief += obj == null? "" : obj + ";";
      }
    }
    if (brief.length() > 0)
      brief = brief.substring(0, brief.length() - 1);
    
    return brief;
  }
  
	/**
	 * 删除业务表和草稿记录，业务表包含主表和子表 TODO
	 * 业务表主子表间不一定有外键联系，使用数据库的级联删除不一定可行，故在程序中删除，若业务表间有此关系，此方法仅需删除主表即可（会级联删除子表）
	 * 
	 * @param compoId
	 * @param instanceId
	 * @throws BusinessException
	 */
	public void deleteDraftAndEntity(String compoId, String instanceId)
			throws BusinessException {
		try {
			String sql = null;
			TableMeta mainTableMeta = MetaManager
					.getTableMetaByCompoName(compoId);// 取主表主键信息
			List keyFieldNames = mainTableMeta.getKeyFieldNames();
			String tableName = mainTableMeta.getName();
			sql = "select ";
			for (int i = 0; i < keyFieldNames.size(); i++) {
				sql += keyFieldNames.get(i) + ",";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += " from  " + tableName + " where PROCESS_INST_ID = ? ";
			Map keyFiledValues = DBHelper.queryOneRow(sql,
					new Object[] { instanceId });// 获取主表key值

			CommonService service = (CommonService) ApplusContext
					.getBean("commonService");
			service.deleteEntity(tableName, keyFiledValues);

			deleteDraft(instanceId);
		} catch (SQLException e) {
			logger.error(e);
			throw new BusinessException(e.getMessage());
		}
	}

	public String getDefaultNextExecutor(TableData entityData,
			TableData wfData, String entityName, String junior,
			String junCoCode, String junOrgCode, String junPosiCode, String nd)
			throws BusinessException {
		WFService wfs = WFFactory.getInstance().getService();
		String result = "";
		try {
      List wfVarList = wfs.getWFVariableValueList(entityName, entityData,wfData);
      List nextLinkList = WFUtil.getRightFollowedTaskLinkList(wfData, wfVarList);
			Set executors = wfs.getDefaultNextExecutor(entityData, wfData,
					entityName, junior, junCoCode, junOrgCode, junPosiCode, nd,
          wfVarList,nextLinkList);
			Iterator iterator = executors.iterator();
			while (iterator.hasNext()) {
				result += (String) iterator.next() + ",";
			}
			if (result.length() > 0) {
				result = result.substring(0, result.length() - 1);
			}
			return result;
		} catch (Exception ex) {
			throw new BusinessException(ex);
		}
	}

	/**
	 * 取指定模板和活动的的默认动作
	 * 
	 * @param templateId
	 *            模板
	 * @param activityId
	 *            活动
	 * @throws WFException
	 */
	public String getDefaultActionName(int templateId, int activityId)
			throws WFException {
		String result = null;
		try {
			result = ConfigureFacade.getDefaultAction(templateId, activityId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return result;
	}

	/**
	 * 取指定模板和活动的的所有动作
	 * 
	 * @param templateId
	 *            模板
	 * @param activityId
	 *            活动
	 * @throws WFException
	 */
	public Delta getActionDeltaByActivity(String compoName, int templateId,
			int activityId) throws WFException {
		Delta result = new Delta();
		Set actionSet = this.getActionSetByActivity(templateId, activityId);
		Iterator i = actionSet.iterator();
		while (i.hasNext()) {
			String actionName = (String) i.next();
			TableData entity = new TableData(compoName);
			entity.setField(WFConst.WF_ACTION_NAME, actionName);
			result.add(entity);
		}
		return result;
	}

	/**
	 * 取指定模板和活动的的所有动作
	 * 
	 * @param templateId
	 *            模板
	 * @param activityId
	 *            活动
	 * @throws WFException
	 */
	public Set getActionSetByActivity(int templateId, int activityId)
			throws WFException {
		Set result = null;
		try {
			result = ConfigureFacade.getActionSet(templateId, activityId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return result;
	}

	public String appendExecutor(String strInstanceId, String strTemplateId,
			String strNodeId, String strCompoId, String strUserId,
			TableData wfData, String direction) throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.appendExecutor(strInstanceId, strTemplateId, strNodeId,
				strCompoId, strUserId, wfData, direction);
	}

	public String removeExecutor(String strInstanceId, String strNodeId,
			String strUserId, String comment) throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.removeExecutor(strInstanceId, strNodeId, strUserId, comment);
	}

	public String getNodeExecutorBySource(String templateId, String nodeId,
			String action, String nd) throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.getNodeExecutorBySource(templateId, nodeId, action, nd);
	}

	public String getRuntimeExecutor(String strTemplateId,
			String strInstanceId, String strNodeId, String action)
			throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.getRuntimeExecutor(strTemplateId, strInstanceId, strNodeId,
				action);
	}

	public String removeNextNodeExecutor(String instanceId, String userId,
			TableData wfData, String comment) throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.removeNextNodeExecutor(instanceId, userId, wfData, comment);
	}

	public String removeNodeExecutor(String instanceId, String userId,
			TableData wfData, String comment) throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		return wfs.removeNodeExecutor(instanceId, userId, wfData, comment);
	}
}
