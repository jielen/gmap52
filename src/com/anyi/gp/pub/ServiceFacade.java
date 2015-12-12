package com.anyi.gp.pub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.Delta;
import com.anyi.gp.Pub;
import com.anyi.gp.TableData;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.access.PrintService;
import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.persistence.RowManager;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;
import com.anyi.gp.workflow.WFFactory;
import com.anyi.gp.workflow.WFService;
import com.anyi.gp.workflow.util.WFConst;
import com.anyi.gp.workflow.util.WFException;
import com.kingdrive.workflow.exception.WorkflowException;

public class ServiceFacade {

	private static final Logger log = Logger.getLogger(ServiceFacade.class);

	private CommonService commonService;

	private WorkflowService wfService;

	private PrintService printService;

	WFService wfs = WFFactory.getInstance().getService();

	public ServiceFacade() {
	}

	public ServiceFacade(CommonService commonService,
			WorkflowService wfService, PrintService printService) {
		this.commonService = commonService;
		this.wfService = wfService;
		this.printService = printService;
	}

	/**
	 * 数据持久化方法 谨慎使用本方法，此方法调用rowmanager的save方法，但是没有进行degist检查
	 * data数据为符合tableData格式的数据
	 * 
	 * @param data
	 * @param actionType:
	 *            insert, delete, update
	 * @param entityName
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public String save(String data, String action, String entityName,
			String userId) throws BusinessException {
		TableData entityData = null;
		try {
			Document xmlData = XMLTools.stringToDocument(data);
			entityData = new TableData(xmlData.getDocumentElement());
		} catch (Exception e) {
			throw new BusinessException(
					"Error_1001: 参数 data 不符合TableData格式规范: " + data);
		}

		RowDelta rowDelta = new RowDelta(action, entityName, entityData);

		RowManager rowManager = null;
		BoolMessage saveBM = null;
		try {
			rowManager = (RowManager) ApplusContext.getBean("rowManager");
			rowManager.setData(rowDelta.toString());
			rowManager.setIsdigest(false);
			rowManager.init();
			saveBM = rowManager.save(userId);
		} catch (BusinessException e) {
			saveBM = new BoolMessage();
			saveBM.setSuccess(false);
			// saveBM.setMessage(e.getMessage());
			log.error(e);
			throw new BusinessException(e);
		}
		return saveBM.getMessage();
	}

	/**
	 * 数据持久化
	 * 
	 * @param data
	 * @param isDigest
	 * @param request
	 * @return
	 * @throws BusinessException
	 */
	public String save(String data, String isDigest, HttpServletRequest request)
			throws BusinessException {
		BoolMessage saveBM = doSave(data, isDigest, request);
		String result = Pub.makeRetInfo2(saveBM.isSuccess(), saveBM
				.getMessage(), saveBM.vsBusiMsg, saveBM.vsDigest);

		return result;
	}

	/**
	 * 
	 * 数据持久化
	 * 业务类在处理BoolMessage时只要BoolMessage.isSuccess()是false时就会抛出Exception,各业务拦截方法同时回滚。
	 * 
	 * @param data
	 * @param isDigest
	 * @param request
	 * @return
	 * @throws BusinessException
	 */
	public String saveWithThrow(String data, String isDigest,
			HttpServletRequest request) throws BusinessException {
		BoolMessage saveBM = doSave(data, isDigest, request);
		String result = Pub.makeRetInfo2(saveBM.isSuccess(), saveBM
				.getMessage(), saveBM.vsBusiMsg, saveBM.vsDigest);

		if (!saveBM.isSuccess())
			throw new BusinessException(saveBM.vsBusiMsg);

		return result;
	}

	private BoolMessage doSave(String data, String isDigest,
			HttpServletRequest request) throws BusinessException {
		String vsBusiMsg = "";
		String vsDigest = "";
		RowManager rowManager = null;
		BoolMessage saveBM = null;

		try {
			rowManager = (RowManager) ApplusContext.getBean("rowManager");
			rowManager.setData(data);
			rowManager.setIsdigest(Boolean.valueOf(isDigest).booleanValue());
			rowManager.init();
			
			String userId = SessionUtils.getAttribute(request, "svUserID");
      BoolMessage checkBM = rowManager.check(userId);
      
			if (checkBM.isSuccess()) {
				saveBM = rowManager.save(userId);
				if (checkBM.getMessage() != null
						&& !checkBM.getMessage().trim().equals("")) {
					saveBM.setMessage(saveBM.getMessage() + "\n"
							+ checkBM.getMessage());
				}
				if (saveBM.isSuccess())
					vsDigest = rowManager.getNewDigestRet();
			} else {
				vsBusiMsg = checkBM.getMessage();
				checkBM.setMessage("");
				saveBM = checkBM;
			}
		} catch (BusinessException e) {
			saveBM = new BoolMessage();
			saveBM.setSuccess(false);
			vsBusiMsg = XMLTools.getValidStringForXML(e.toString());
			log.error(e);
			throw new BusinessException(e);
		}

		saveBM.vsBusiMsg = vsBusiMsg;
		saveBM.vsDigest = vsDigest;

		return saveBM;
	}

	/**
	 * 取登记簿列表信息
	 * 
	 * @param tableName
	 * @param fromRow
	 * @param toRow
	 * @return
	 * @throws BusinessException
	 */
	public Datum getEntities(String tableName, Map keys, int fromRow, int toRow)
			throws BusinessException {
		return commonService.getEntities(tableName, keys, fromRow, toRow);
	}

	public Datum getEntities(String tableName, Map keys)
			throws BusinessException {
		return commonService.getEntities(tableName, keys);
	}

	/**
	 * 根据tablename和keys获取表数据
	 * 
	 * @param tableName
	 * @param keys
	 * @param containChildTable
	 * @return TableData
	 * @throws BusinessException
	 */
	public TableData getEntity(String tableName, Map keys,
			boolean containChildTable) throws BusinessException {
		TableData tableData = null;
		Datum datum = getEntityWithDatum(tableName, keys, containChildTable);
		if (!datum.getData().isEmpty()) {
			tableData = new TableData((Map) datum.getData().get(0));
			Map childDatum = datum.getChildDatums();
			Set entrySet = childDatum.entrySet();
			Iterator itera = entrySet.iterator();
			while (itera.hasNext()) {
				Datum datumTemp = (Datum) itera.next();
				tableData.setChildTable(datumTemp.getName(), datumTemp
						.getData(), 0);
			}
		}
		return tableData;
	}

	/**
	 * 根据tablename和keys获取表数据
	 * 
	 * @param tableName
	 * @param keys
	 * @param containChildTable
	 * @return Datum
	 * @throws BusinessException
	 */
	public Datum getEntityWithDatum(String tableName, Map keys,
			boolean containChildTable) throws BusinessException {
		return commonService.getEntity(tableName, keys, containChildTable);
	}

	/**
	 * 根据tablename和keys获取表数据
	 * 
	 * @param tableName
	 * @param keys
	 * @return Datum
	 * @throws BusinessException
	 */
	public Datum getEntityWithDatum(String tableName, Map keys)
			throws BusinessException {
		return commonService.getEntity(tableName, keys);
	}

	/**
	 * 根据tablename和keys获取表数据
	 * 
	 * @param tableName
	 * @param keys
	 * @return
	 */
	public TableData getEntity(String tableName, Map keys)
			throws BusinessException {
		TableData tableData = null;
		Datum datum = getEntityWithDatum(tableName, keys);
		if (!datum.getData().isEmpty()) {
			tableData = new TableData((Map) datum.getData().get(0));
		}
		return tableData;
	}

	/**
	 * 根据取数规则获取数据
	 * 
	 * @param ruleID
	 * @param param
	 * @return 符合delta格式的datum
	 * @throws BusinessException
	 */
	public Delta getDBData(String ruleID, TableData param)
			throws BusinessException {
		return commonService.getDBData(ruleID, param);
	}

	/**
	 * 删除草稿
	 * 
	 * @param compoId
	 * @param title
	 * @param masterTabId
	 * @param userId
	 * @return
	 * @throws BusinessException
	 */
	public String createDraft(String compoId, String title, String masterTabId,
			String userId) throws BusinessException {
		return wfService.createDraft(compoId, title, masterTabId, userId);
	}

	/**
	 * 删除草稿
	 * 
	 * @param draftId
	 * @throws BusinessException
	 */
	public void deleteDraft(String draftId) throws BusinessException {
		wfService.deleteDraft(draftId);
	}

	/**
	 * 提交工作流项
	 * 
	 * @param entity
	 * @param entityName
	 * @param wfData
	 * @return
	 * @throws BusinessException
	 */
	public String commit(String strBnData, String entityName, String strWfData)
			throws BusinessException {
		TableData bnData = DataTools.parseData(strBnData);
		TableData wfData = DataTools.parseData(strWfData);
		TableData td = wfService.commit(bnData, entityName, wfData);
		if (td != null)
			return td.toString();
		return null;
	}

	/**
	 * 
	 * @param strBnData
	 * @param entityName
	 * @param strWfData
	 * @return
	 * @throws BusinessException
	 */
	public String commit(String strBnData, String entityName, String user,
			String strWfData) throws BusinessException {
		TableData bnData = DataTools.parseData(strBnData);
		TableData wfData = DataTools.parseData(strWfData);
		wfData.setField(WFConst.USER, user);
		TableData td = wfService.commit(bnData, entityName, wfData);
		if (td != null)
			return td.toString();
		return null;
	}

	/**
	 * 提交工作流项
	 * 
	 * @param strInstanceId
	 * @param strTemplateId
	 * @param strCompoId
	 * @param strUserId
	 * @param strWfData
	 * @return
	 * @throws BusinessException
	 */
	public String commitSimply(String strInstanceId, String strTemplateId,
			String strCompoId, String strUserId, String strWfData,
			String strBnData, String asyn) throws BusinessException {
		String[] insIds = strInstanceId.split(",");
		TableData wfData = DataTools.parseData(strWfData);
		Delta strBuDelta = getDelta(strBnData);
		TableData bnData = null;
		for (int i = 0, len = insIds.length; i < len; i++) {
			if (strBnData != null && !strBnData.equals("")) {
				bnData = (TableData) strBuDelta.get(i);
			}
			// wfService.commitSimply(strInstanceId, strTemplateId,
			// strCompoId, strUserId, wfData, bnData, asyn);
			wfService.commitSimply(insIds[i], strTemplateId, strCompoId,
					strUserId, wfData, bnData);
		}
		return "success";
	}

	public String commitSimply(String strInstanceId, String strTemplateId,
			String strCompoId, String strUserId, String strWfData,
			String strBnData) throws BusinessException {
		return commitSimply(strInstanceId, strTemplateId, strCompoId,
				strUserId, strWfData, strBnData, "false");
	}

	private Delta getDelta(String strData) {
		Delta delta = null;
		if (strData != null && !strData.trim().equals("")) {
			delta = new Delta(strData);
		}
		return delta;
	}

	/**
	 * 收回工作流项
	 * 
	 * @param strInstanceId
	 * @param strUserId
	 * @param comment
	 * @return
	 * @throws BusinessException
	 */
	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, String strBnData) throws BusinessException {
		TableData bnData = DataTools.parseData(strBnData);
		return wfService.untreadSimply(strInstanceId, strUserId, comment,
				bnData, -1);
	}

	/**
	 * 收回工作流项
	 * 
	 * @param strInstanceId
	 * @param strUserId
	 * @param comment
	 * @param toWhere
	 * @return
	 * @throws BusinessException
	 */
	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, String strBnData, String toWhere)
			throws BusinessException {
		String[] insIds = strInstanceId.split(",");
		// TableData bnData = DataTools.parseData(strBnData);
		TableData bnData = null;
		if (strBnData != null && !strBnData.equals("")) {
			Delta delta = new Delta(strBnData);
			for (int i = 0; i < delta.size(); i++) {
				bnData = (TableData) delta.get(i);
				wfService.untreadSimply(insIds[i], strUserId, comment, bnData,
						Integer.parseInt(toWhere));
			}
		} else {
			wfService.untreadSimply(insIds[0], strUserId, comment, bnData,
					Integer.parseInt(toWhere));
		}
		return "success";
	}

	/**
	 * 
	 * @param strInstanceId
	 * @param strUserId
	 * @param strComment
	 * @return
	 * @throws BusinessException
	 */
	public String rework(String strInstanceId, String strUserId,
			String strComment, String strBnData) throws BusinessException {
		TableData bnData = DataTools.parseData(strBnData);
		return wfService.rework(strInstanceId, strUserId, strComment, bnData);
	}

	/**
	 * 删除工作流实例
	 * 
	 * @param wfData
	 * @throws BusinessException
	 */
	public void deleteWithWorkflow(TableData wfData) throws BusinessException {
		wfService.deleteWithWorkflow(wfData);
	}

	/**
	 * 删除工作流实例
	 * 
	 * @param instId
	 * @param userId
	 * @throws BusinessException
	 */
	public void deleteWithWorkflow(int instId, String userId)
			throws BusinessException {
		wfService.deleteWithWorkflow(instId, userId);
	}

	/**
	 * 送审工作流项
	 * 
	 * @param data
	 * @param entityName
	 * @param user
	 * @param wfData
	 * @return
	 * @throws BusinessException
	 */
	public String wfNewCommit(String data, String entityName, String user,
			String strWfData, String asyn) throws BusinessException {

		Delta entityDelta = null;
		try {
			entityDelta = new Delta(data);
		} catch (Exception e) {
			throw new BusinessException("Error_1001: 参数 data 不符合 Delta 格式规范: "
					+ data);
		}
		TableData entityWFData = DataTools.parseData(strWfData);
		String result = "";
		if (entityDelta != null && entityWFData != null) {
			entityWFData.setField(WFConst.USER, user);
			// result = wfService.wfNewCommit(entityDelta, entityName, user,
			// entityWFData, asyn);
			result = wfService.wfNewCommit(entityDelta, entityName, user,
					entityWFData);
		}
		return result;
	}

	// 缺省: 同步送审
	public String wfNewCommit(String data, String entityName, String user,
			String strWfData) throws BusinessException {
		return wfNewCommit(data, entityName, user, strWfData, "false");
	}

	// 从WFClientCallFacade移动到这里的方法。。。
	public String getCompoEnableStartedTemplate(String compoId) {
		String result = "";
		try {
			List templateList = wfs.getCompoEnableStartedTemplate(compoId);
			boolean flag = false;
			Iterator iter = templateList.iterator();
			while (iter.hasNext()) {
				if (flag) {
					result += ",";
				}
				// TemplateMeta element = (TemplateMeta) iter.next();
				// result += element.getTemplateId();
				result += iter.next().toString();
				flag = true;
			}
		} catch (WFException e) {
			result = "";
			log.error("获取流程模版出错", e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	// 中止实例
	public void interruptInstance(String instanceId, String svUserId,
			String comment) throws BusinessException {
		wfs.interruptInstance(instanceId, svUserId, comment);
	}

	public String getDefaultActionName(String templateId, String nodeId)
			throws NumberFormatException, WFException {
		String result = "";
		result = wfService.getDefaultActionName(Integer.parseInt(templateId),
				Integer.parseInt(nodeId));
		return result;
	}

	public String getExecutorsByRelation(String data, String sWfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, String nd, String action,
			String orgPosiCode) throws WFException, WorkflowException,
			BusinessException {
		String ret = null;
		TableData entityData = DataTools.parseData(data);
		TableData wfData = DataTools.parseData(sWfData);
		Set executors = wfs.getExecutorsByRelation(entityData, wfData,
				entityName, junior, junCoCode, junOrgCode, junPosiCode, nd,
				action, null);
		if (executors == null)
			return null;
		String retUsers = "";
		String retEmps = "";
		Object[] users = executors.toArray();
		for (int i = 0; i < users.length; i++) {
			retUsers += users[i].toString();
			retUsers += ",";
		}
		if (retUsers.length() > 0) {
			retUsers = retUsers.substring(0, retUsers.length() - 1);
			for (int i = 0; i < users.length; i++) {
				retEmps += "";// //new User(users[i].toString()).getEmpName();
				retEmps += ",";
			}
			if (retEmps.length() > 0) {
				retEmps = retEmps.substring(0, retEmps.length() - 1);
			}
			ret = retUsers + ";" + retEmps;
		}
		return ret;
	}

	/**
	 * 获取流程状态
	 * 
	 * @param sInstanceId
	 */
	public String getInstanceStatus(String sInstanceId) {
		return "" + wfs.getInstanceStatus(sInstanceId);
	}

	public List getWfdataListByUserComp(String userId, String compoId,
			String listType, int pageSize) throws BusinessException {
		return wfService.getWfdataListByUserComp(userId, compoId, listType,
				pageSize);
	}

	public List getTodoListByProcessInstanceId(int processInstanceId,
			int isValid) throws BusinessException {
		try {
			return wfs.getTodoListByProcessInstanceId(processInstanceId,
					isValid);
		} catch (WFException ex) {
			throw new BusinessException(ex.getMessage());
		}
	}

	public void deleteDraft(String compoId, String instanceId)
			throws BusinessException {
		try {
			wfService.deleteDraftAndEntity(compoId, instanceId);
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage());
		}
	}

	public String getDefaultNextExecutor(String strEntity, String strWfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, String nd)
			throws BusinessException {
		try {
			TableData entityData = DataTools.parseData(strEntity);
			TableData wfData = DataTools.parseData(strWfData);
			return wfService.getDefaultNextExecutor(entityData, wfData,
					entityName, junior, junCoCode, junOrgCode, junPosiCode, nd);
		} catch (Exception ex) {
			throw new BusinessException(ex);
		}
	}

	// 工作流通用函数接口
	public void wfCommon(String funcname, String indata, String strBnData)
			throws BusinessException {
		TableData wfParameter = DataTools.parseData(indata);
		String primaryUserIds = wfParameter
				.getFieldValue(WFConst.WF_NEXT_EXECUTOR_ID);
		String secondUserIds = wfParameter
				.getFieldValue(WFConst.WF_NEXT_EXECUTOR_ASS_ID);
		Object[] userInfo = initUserInfo(primaryUserIds, secondUserIds);
		if (funcname.equalsIgnoreCase("delete")) {
			wfService.deleteWithWorkflow(indata);
		}
		// /装配参数
		Map parameter = new HashMap();
		parameter.put(WFConst.WF_PROCESS_INST_ID, wfParameter
				.getFieldValue(WFConst.WF_INSTANCE_ID));
		parameter.put(WFConst.WF_ACTIVITY_ID, wfParameter
				.getFieldValue(WFConst.WF_NODE_ID));
		parameter.put("nextNode", wfParameter.getFieldValue("WF_NEXT_NODE_ID"));
		parameter.put("svUserId", wfParameter
				.getFieldValue(WFConst.WF_CURRENT_EXECUTOR_ID));
		parameter.put(WFConst.WF_WORKITEM_ID, wfParameter
				.getFieldValue(WFConst.WF_WORKITEM_ID));
		parameter.put("comment", wfParameter.getFieldValue(WFConst.WF_COMMENT));
		parameter.put("userIds", userInfo[0]);
		parameter.put("pUserIds", userInfo[2]);
		parameter.put("sUserIds", userInfo[3]);
		parameter.put("positionIds", userInfo[1]);
		parameter.put("funcname", funcname);
		parameter.put("strBnData", strBnData);
		Delta result = wfService.wfCommonAction(parameter);
	}

	private Object[] initUserInfo(String primaryUserIds, String secondUserIds) {

		String pUserIDArray[] = new String[0];
		String sUserIDArray[] = new String[0];
		if (!StringTools.isEmptyString(primaryUserIds)) {
			pUserIDArray = StringTools.split2(primaryUserIds, ",");
		}
		if (!StringTools.isEmptyString(secondUserIds)) {
			sUserIDArray = StringTools.split2(secondUserIds, ",");
		}
		int cPrimaryUserIds = 0;
		int cSecondUserIds = 0;
		if (pUserIDArray != null) {
			cPrimaryUserIds = pUserIDArray.length;
		}
		if (sUserIDArray != null) {
			cSecondUserIds = sUserIDArray.length;
		}
		int cUserIds = cPrimaryUserIds + cSecondUserIds;
		String userIds[] = new String[cUserIds];
		int positionIds[] = new int[cUserIds];
		int i;
		for (i = 0; i < cPrimaryUserIds; i++) {
			userIds[i] = pUserIDArray[i];
			positionIds[i] = 1;
		}
		for (; i < cUserIds; i++) {
			userIds[i] = sUserIDArray[i - cPrimaryUserIds];
			positionIds[i] = -1;
		}
		Object result[] = new Object[4];
		result[0] = userIds;
		result[1] = positionIds;
		result[2] = pUserIDArray;
		result[3] = sUserIDArray;
		return result;
	}
}
