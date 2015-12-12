// $Id: WFGeneral.java,v 1.14 2008/10/07 06:05:49 dingyy Exp $

package com.anyi.gp.workflow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.message.MessageFactory;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.util.XMLTools;
import com.anyi.gp.workflow.bean.ActionBean;
import com.anyi.gp.workflow.bean.ActivityDefBean;
import com.anyi.gp.workflow.bean.WorkitemBean;
import com.anyi.gp.workflow.util.WFConst;
import com.anyi.gp.workflow.util.WFException;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.DelegateFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.business.Executor;
import com.kingdrive.workflow.business.Link;
import com.kingdrive.workflow.business.Node;
import com.kingdrive.workflow.business.NodeState;
import com.kingdrive.workflow.business.Pass;
import com.kingdrive.workflow.business.State;
import com.kingdrive.workflow.business.TaskExecutor;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.DelegationMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TaskExecutorMeta;
import com.kingdrive.workflow.exception.WorkflowException;

/**
 *<p> Title:</p>
 *<p>Description: </p>
 *<p>Copyright: Copyright (c) 2004</p>
 *<p>Company: 用友政务</p>
 * @author zhangcheng
 * @time 2005-6-29
 *
 */
/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: 用友政务
 * </p>
 * 
 * @author zhangcheng
 * @time 2005-6-29
 * 
 */
public class WFGeneral {
	protected final static Logger log = Logger.getLogger(WFGeneral.class);

	static WFService service = WFFactory.getInstance().getService();

	/**
	 * 工作流通用功能处理的统一入口
	 */
	public Delta wfCommon(String funcname, String indata, String strBnData)
			throws BusinessException, WFException, WorkflowException {
		TableData wfParameter = this.getWFParameterEntity(indata);
		String strProcessInstId = (String) wfParameter
				.getField(WFConst.WF_INSTANCE_ID);
		String strActivityId = (String) wfParameter
				.getField(WFConst.WF_NODE_ID);
		String nextNode = (String) wfParameter.getField("WF_NEXT_NODE_ID");// null
		String svUserId = (String) wfParameter
				.getField(WFConst.WF_CURRENT_EXECUTOR_ID);
		String strWorkitemId = (String) wfParameter
				.getField(WFConst.WF_WORKITEM_ID);// null
		String comment = (String) wfParameter.getField(WFConst.WF_COMMENT);
		String[] userIds = new String[0];
		int[] positionIds = null;
		String primaryUserIds = null;
		String secondUserIds = null;
		String[] pUserIDArray = new String[0];
		String[] sUserIDArray = new String[0];

		// 处理流程监控中的删除实例
		if (funcname.equalsIgnoreCase("delete")) {
			this.deleteInstance(indata);
			Delta result = new Delta();
			return result;// TCJLODO test : is null ok?
		}

		// /主办和辅办
		if (funcname.equalsIgnoreCase("impower")
				|| funcname.equalsIgnoreCase("handover")
				|| funcname.equalsIgnoreCase("untreadflow")
				|| funcname.equalsIgnoreCase("untreadCollectFlow")
				|| funcname.equalsIgnoreCase("transferflow")) {
			primaryUserIds = (String) wfParameter
					.getField(WFConst.WF_NEXT_EXECUTOR_ID);
			secondUserIds = (String) wfParameter
					.getField(WFConst.WF_NEXT_EXECUTOR_ASS_ID);
			// System.out.println("主办辅办:" + primaryUserIds + secondUserIds);
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
			// System.out.println(cPrimaryUserIds + "," + cSecondUserIds);
			int cUserIds = cPrimaryUserIds + cSecondUserIds;
			userIds = new String[cUserIds];
			positionIds = new int[cUserIds];
			int i = 0;
			for (; i < cPrimaryUserIds; i++) {
				userIds[i] = pUserIDArray[i];
				positionIds[i] = 1;
			}
			for (; i < cUserIds; i++) {
				userIds[i] = sUserIDArray[i - cPrimaryUserIds];
				positionIds[i] = -1;
			}
		}

		// /装配参数
		Map parameter = new HashMap();
		parameter.put(WFConst.WF_PROCESS_INST_ID, strProcessInstId);
		parameter.put(WFConst.WF_ACTIVITY_ID, strActivityId);
		parameter.put("nextNode", nextNode);
		parameter.put("svUserId", svUserId);
		parameter.put(WFConst.WF_WORKITEM_ID, strWorkitemId);
		parameter.put("comment", comment);
		parameter.put("userIds", userIds);
		parameter.put("pUserIds", pUserIDArray);
		parameter.put("sUserIds", sUserIDArray);
		parameter.put("positionIds", positionIds);
		parameter.put("funcname", funcname);
		parameter.put("strBnData", strBnData);

		// /用java的反射进行重构
		if (funcname.equalsIgnoreCase("impower")
				|| funcname.equalsIgnoreCase("handover")
				|| funcname.equalsIgnoreCase("withdraw")
				|| funcname.equalsIgnoreCase("callback")
				|| funcname.equalsIgnoreCase("transferflow")
				|| funcname.equalsIgnoreCase("untreadflow")
				|| funcname.equalsIgnoreCase("untreadCollectFlow")
				|| funcname.equalsIgnoreCase("interruptInstance")
				|| funcname.equalsIgnoreCase("activateInstance")
				|| funcname.equalsIgnoreCase("deactivateInstance")
				|| funcname.equalsIgnoreCase("restartInstance")) {
			String needMessage = wfParameter.getFieldValue("NEED_MESSAGE");
			String needShortMessage = wfParameter
					.getFieldValue("NEED_SHORTMESSAGE");
			String userName = wfParameter.getFieldValue("USERNAME");
			String needEmail = wfParameter.getFieldValue("NEED_EMAIL");
			// 如果是回收又需要发消息查找要回收的用户列表
			String callbackUserListStr = "";
			if ((needMessage.equalsIgnoreCase("true")
					|| needShortMessage.equalsIgnoreCase("true") || needEmail
					.equalsIgnoreCase("true"))
					&& funcname.equalsIgnoreCase("callback")) {
				List callbackList = ExecuteFacade.getTodoListByInstance(Integer
						.parseInt(strProcessInstId), 1);
				for (int i = 0; i < callbackList.size(); i++) {
					callbackUserListStr += ((CurrentTaskMeta) callbackList
							.get(i)).getExecutor()
							+ ",";
				}
				// System.out.println("callback:" + callbackUserListStr);
			}
			WorkflowService workflowService = (WorkflowService) ApplusContext
					.getBean("workflowService");
			Delta result = workflowService.wfCommonAction(parameter);
			log.debug("action done");
			// 发送消息

			CurrentTaskMeta curTaskMeta = null;
			String msg = "";
			String msgHtml = "";
			String title = "";
			String workContent = "";
			String userList = null;
			String baseUrl = ApplusContext.getEnvironmentConfig().get(
					"baseHost");
			if (needMessage.equalsIgnoreCase("true")
					|| needShortMessage.equalsIgnoreCase("true")
					|| needEmail.equalsIgnoreCase("true")) {
				List taskList = service.getTodoListByProcessInstanceId(Integer
						.parseInt(strProcessInstId), 1);
				Iterator iterator = taskList.iterator();

				if (iterator.hasNext()) {
					Object object = iterator.next();
					if (object instanceof CurrentTaskMeta) {
						curTaskMeta = (CurrentTaskMeta) object;
					}
				}
				workContent = curTaskMeta.getTemplateName() + "-"
						+ curTaskMeta.getNodeName();
				msg = workContent + "\r\n" + "他的意见是:" + comment;
				msgHtml = workContent + "<br>" + "他的意见是:" + comment + "<br>请您"
						+ "<A href=\"" + baseUrl + "\">" + "办理工作</A>";
				;
				// msg = msg + "当前工作:" + curTaskMeta.getTemplateName() + "\r\n"
				// +
				// "工作标题:"
				// + curTaskMeta.getInstanceName() + "\r\n" + "工作环节:"
				// + curTaskMeta.getNodeName() + "\r\n" + "意见:" + comment;

				if (funcname.equalsIgnoreCase("impower")) {
					title = userName + "授权给您一项工作:" + workContent;
					msg = userName + "授权给您一项工作:" + msg;
					msgHtml = userName + "授权给您一项工作:" + msg;
					userList = primaryUserIds;
					if (secondUserIds != null && secondUserIds.length() > 0) {
						userList = userList + "," + secondUserIds;
					}
				} else if (funcname.equalsIgnoreCase("handover")) {
					msg = userName + "移交给您一项工作:" + msg;
					msg = userName + "移交给您一项工作:" + workContent;
					msgHtml = userName + "移交给您一项工作:" + msg;
					userList = primaryUserIds;
					if (secondUserIds != null && secondUserIds.length() > 0) {
						userList = userList + "," + secondUserIds;
					}
				} else if (funcname.equalsIgnoreCase("withdraw")) {
					msg = userName + "回退给您一项工作:" + msg;
					title = userName + "回退给您一项工作:" + workContent;
					msgHtml = userName + "回退给您一项工作:" + msg;
					userList = curTaskMeta.getExecutor();
				} else if (funcname.equalsIgnoreCase("callback")) {
					msg = userName + "回收一项工作:" + msg;
					title = userName + "回收一项工作:" + workContent;
					msgHtml = userName + "回收一项工作:" + msg;
					userList = callbackUserListStr;
				} else if (funcname.equalsIgnoreCase("transferflow")) {
					msg = userName + "转移给您一项工作:" + msg;
					title = userName + "转移给您一项工作:" + workContent;
					msgHtml = userName + "转移给您一项工作:" + msg;
					userList = primaryUserIds;
					if (secondUserIds != null && secondUserIds.length() > 0) {
						userList = userList + "," + secondUserIds;
					}
				} else if (funcname.equalsIgnoreCase("untreadflow")) {
					msg = userName + "回退给您一项工作:" + msg;
					title = userName + "回退给您一项工作:" + workContent;
					msgHtml = userName + "回退给您一项工作:" + msg;
					userList = primaryUserIds;
					if (secondUserIds != null && secondUserIds.length() > 0) {
						userList = userList + "," + secondUserIds;
					}
				}
				if (needMessage.equalsIgnoreCase("true")) {
					if (userList != null && userList.length() > 0) {
						title = title + "\r\n";
						MessageFactory.getMessageImp().notify(userList, msg,
								"0", title);
					}
				}
				if (needShortMessage.equalsIgnoreCase("true")) {
					if (userList != null && userList.length() > 0) {
						MessageFactory.getMessageImp().sendShortMessageToUser(
								svUserId, userList, msg, "0");
					}
				}
				if (needEmail.equalsIgnoreCase("true")) {
					if (userList != null && userList.length() > 0
							&& curTaskMeta != null) {
						try {
							MessageFactory.getMessageImp().sendEmailToUser(
									svUserId, userList, null, null, title,
									msgHtml, null);
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}

			}
			return result;
		}

		// /用java的反射进行重构
		if (funcname.equalsIgnoreCase("getNodeList")) {
			String strTemplateId = (String) wfParameter
					.getField("WF_TEMPLATE_ID");
			return getNodeDelta(strTemplateId);
		}

		/*
		 * 平台4.0开始废弃使用模板类型以及业务节点类型
		 * if(funcname.equalsIgnoreCase("getBusinessTypes")){ String
		 * strTemplateType =
		 * (String)wfParameter.getField("WF_TEMPLATE_TYPE_ID"); return
		 * getBusinessTypeDelta(strTemplateType); }
		 */
		return null;
	}

	public Delta impowerAndHandover(String strProcessInstanceId,
			String strActivityId, String strWorkitemId, String[] userIds,
			int[] positionIds, String svUserId, String comment,
			String impowerOrHandover, String strBnData) throws WFException {
		if (strProcessInstanceId == null || strActivityId == null
				|| svUserId == null) {
			throw new IllegalArgumentException(
					"WFGeneral类的impowerAndHandover的参数为null");
		}
		if ((strWorkitemId == null)
				&& impowerOrHandover.equalsIgnoreCase("handover")) {
			throw new IllegalArgumentException(
					"WFGeneral类的impowerAndHandover的参数为null");
		}

		int processInstanceId = Integer.parseInt(strProcessInstanceId);
		int activityId = Integer.parseInt(strActivityId);
		int workitemId = Integer.parseInt(strWorkitemId);
		TableData bnData = getWFParameterEntity(strBnData);
		Delta result = new Delta();
		if (impowerOrHandover.equalsIgnoreCase("impower")) {
			service.impower(processInstanceId, activityId, userIds,
					positionIds, svUserId, comment, bnData);
		}
		if (impowerOrHandover.equalsIgnoreCase("handover")) {
			service.handover(processInstanceId, activityId, workitemId,
					userIds, positionIds, svUserId, comment, bnData);

		}
		return result;
	}

	public Delta withdrawAndCallback(String strProcessInstanceId,
			String strActivityId, String svUserId, String comment,
			String withdrawOrCallback, String strBnData) throws WFException {
		if (strProcessInstanceId == null || strActivityId == null
				|| svUserId == null) {
			throw new IllegalArgumentException(
					"WFGeneral类的withdrawAndCallback的参数为null");
		}

		int processInstanceId = Integer.parseInt(strProcessInstanceId);
		int activityId = Integer.parseInt(strActivityId);
		TableData bnData = getWFParameterEntity(strBnData);
		Delta result = new Delta();
		if (withdrawOrCallback.equalsIgnoreCase("withdraw")) {
			service.withdraw(processInstanceId, activityId, svUserId, comment,
					bnData);
		}
		if (withdrawOrCallback.equalsIgnoreCase("callback")) {
			service.callBack(processInstanceId, activityId, svUserId, comment,
					bnData);

		}
		return result;
	}

	public void createDelegate(Delta delegationDelta) throws WFException {
		try {
			String receiver, startTime, endTime, templateId, nodeId, owner, sender, parentId;
			for (Iterator iter = delegationDelta.iterator(); iter.hasNext();) {
				TableData tmp = (TableData) iter.next();
				receiver = tmp.getFieldValue("receiver");
				startTime = tmp.getFieldValue("startTime");
				endTime = tmp.getFieldValue("endTime");
				templateId = tmp.getFieldValue("templateId");
				nodeId = tmp.getFieldValue("nodeId");
				owner = tmp.getFieldValue("owner");
				sender = tmp.getFieldValue("sender");
				parentId = tmp.getFieldValue("parentId");
				DelegationMeta delegationMeta = new DelegationMeta();
				delegationMeta.setReceiver(receiver);
				delegationMeta.setStartTime(startTime);
				delegationMeta.setEndTime(endTime);
				delegationMeta.setTemplateId(Integer.parseInt(templateId));
				delegationMeta.setNodeId(Integer.parseInt(nodeId));
				delegationMeta.setOwner(owner);
				delegationMeta.setSender(sender);
				delegationMeta.setParentId(Integer.parseInt(parentId));
				DelegateFacade.delegate(delegationMeta);
			}
		} catch (Exception se) {
			se.printStackTrace();
			throw new WFException(se.toString());
		}
	}

	public void cancelDelegate(Delta delegationDelta) throws WFException {
		try {
			String delegationId;
			for (Iterator iter = delegationDelta.iterator(); iter.hasNext();) {
				TableData tmp = (TableData) iter.next();
				delegationId = tmp.getFieldValue("delegationId");
				DelegateFacade.cancelDelegation(Integer.parseInt(delegationId));
			}
		} catch (Exception se) {
			throw new WFException(se.toString());
		}
	}

	public Delta transferFlow(String strProcessInstanceId, String strNodeId,
			String strNextNodeId, String svUserId, String comment,
			String[] pUserIds, String[] sUserIds, String strBnData)
			throws WFException {
		if (strProcessInstanceId == null || strNextNodeId == null
				|| strNextNodeId == null || svUserId == null) {
			throw new IllegalArgumentException(
					"WFGeneral类的transferFlow的参数为null");
		}
		int instanceId = Integer.parseInt(strProcessInstanceId);
		int nodeId = Integer.parseInt(strNodeId);
		int nextNodeId = Integer.parseInt(strNextNodeId);
		Delta result = new Delta();
		try {
			List executors = ExecuteFacade.getTaskExecutorList(instanceId,
					nextNodeId);
			for (int i = 0; i < executors.size(); i++) {
				TaskExecutorMeta e = (TaskExecutorMeta) executors.get(i);
				ExecuteFacade.removeTaskExecutor(e.getId());
			}
			// 创建新的执行者
			for (int i = 0; i < pUserIds.length; i++) {
				ExecuteFacade.createTaskExecutor(instanceId, nextNodeId,
						pUserIds[i], i + 1, WFConst.MASTER_EXECUTOR_FLAG);
			}
			int countExecutors = pUserIds.length;
			for (int i = 0; i < sUserIds.length; i++) {
				ExecuteFacade.createTaskExecutor(instanceId, nextNodeId,
						sUserIds[i], i + countExecutors + 1,
						WFConst.SECOND_EXECUTOR_FLAG);
			}
			ExecuteFacade.transferFlow(instanceId, nodeId, nextNodeId,
					svUserId, comment);
			TableData bnData = getWFParameterEntity(strBnData);
			WFService wfs = WFFactory.getInstance().getService();
			wfs.syncDataByBindedWFSate(instanceId);
			ActivityDefBean activityDef = wfs.findActivityDefBean(instanceId,
					nodeId);
			wfs.rewriteComment(instanceId + "", nodeId + "", "", bnData,
					activityDef);
			wfs.refreshBrief(instanceId + "", nodeId + "", bnData, activityDef);
		} catch (Exception ex) {
			throw new WFException(ex.toString());
		}
		return result;
	}

	public Delta untreadFlow(String strProcessInstanceId, String strNodeId,
			String strNextNodeId, String svUserId, String comment,
			String[] pUserIds, String[] sUserIds, String strBnData)
			throws WFException {
		if (strProcessInstanceId == null || strNodeId == null
				|| strNextNodeId == null || svUserId == null) {
			throw new IllegalArgumentException("WFGeneral类的untreadFlow的参数为null");
		}
		int instanceId = Integer.parseInt(strProcessInstanceId);
		int nodeId = Integer.parseInt(strNodeId);
		if("".equals(strNextNodeId)){
			throw new WFException("开始任务节点不能执行退回处理");
		}		
		int nextNodeId = Integer.parseInt(strNextNodeId);
		Delta result = new Delta();
		try {
			for (int i = 0; null != pUserIds && i < pUserIds.length; i++) {
				ExecuteFacade.createTaskExecutor(instanceId, nextNodeId,
						pUserIds[i], i + 1, WFConst.MASTER_EXECUTOR_FLAG);
			}
			int countExecutors = pUserIds == null ? 0 : pUserIds.length;
			for (int i = 0; null != sUserIds && i < sUserIds.length; i++) {
				ExecuteFacade.createTaskExecutor(instanceId, nextNodeId,
						sUserIds[i], i + countExecutors + 1,
						WFConst.SECOND_EXECUTOR_FLAG);
			}
			ExecuteFacade.untreadFlow(instanceId, nodeId, nextNodeId, svUserId,
					comment);
			TableData bnData = getWFParameterEntity(strBnData);
			WFService wfs = WFFactory.getInstance().getService();
			wfs.syncDataByBindedWFSate(instanceId);
			ActivityDefBean activityDef = wfs.findActivityDefBean(instanceId,
					nodeId);
			wfs.rewriteComment(instanceId + "", nodeId + "", "", bnData,
					activityDef);
			wfs.refreshBrief(instanceId + "", nodeId + "", bnData, activityDef);
		} catch (Exception ex) {
			throw new WFException(ex.toString());
		}
		return result;
	}

	public Delta interruptInstance(String instanceId, String svUserId,
			String comment) throws WFException {
		if (instanceId == null || svUserId == null) {
			throw new IllegalArgumentException("WFGeneral类的interrupt的参数为null");
		}
		int intInstanceId = Integer.parseInt(instanceId);
		Delta result = new Delta();
		try {
			ExecuteFacade.interruptInstance(intInstanceId, svUserId, comment);
		} catch (Exception ex) {
			throw new WFException(ex.toString());
		}
		return result;
	}

	public Delta activateInstance(String strProcessInstanceId, String svUserId,
			String comment) throws WFException {
		if (strProcessInstanceId == null || svUserId == null) {
			throw new IllegalArgumentException("WFGeneral类的activate的参数为null");
		}
		int instanceId = Integer.parseInt(strProcessInstanceId);
		Delta result = new Delta();
		try {
			ExecuteFacade.activateInstance(instanceId, svUserId, comment);
		} catch (Exception ex) {
			throw new WFException(ex.toString());
		}
		return result;
	}

	public Delta deactivateInstance(String strProcessInstanceId,
			String svUserId, String comment) throws WFException {
		if (strProcessInstanceId == null || svUserId == null) {
			throw new IllegalArgumentException("WFGeneral类的activate的参数为null");
		}
		int instanceId = Integer.parseInt(strProcessInstanceId);
		Delta result = new Delta();
		try {
			ExecuteFacade.deactivateInstance(instanceId, svUserId, comment);
		} catch (Exception ex) {
			throw new WFException(ex.toString());
		}
		return result;
	}

	public Delta restartInstance(String strProcessInstanceId, String svUserId,
			String comment) throws WFException {
		if (strProcessInstanceId == null || svUserId == null) {
			throw new IllegalArgumentException("WFGeneral类的activate的参数为null");
		}
		int instanceId = Integer.parseInt(strProcessInstanceId);
		Delta result = new Delta();
		try {
			ExecuteFacade.restartInstance(instanceId, svUserId, comment, true);
		} catch (Exception ex) {
			throw new WFException(ex.toString());
		}
		return result;
	}

	// /用java的反射进行重构
	private Delta getNodeDelta(String strTemplateId) throws WFException {
		if (strTemplateId != null) {
			throw new IllegalArgumentException(
					"WFGeneral类的getNodeDelta的参数为null");
		}

		int templateId = Integer.parseInt(strTemplateId);
		Delta result = new Delta();
		List nodeList = null;
		nodeList = service.getNodeList(templateId);
		Iterator iterator = nodeList.iterator();
		while (iterator.hasNext()) {
			NodeMeta node = (NodeMeta) iterator.next();
			TableData entity = new TableData("Activity");
			entity.setField(WFConst.WF_ACTIVITY_ID, String
					.valueOf(node.getId()));
			entity.setField(WFConst.WF_ACTIVITY_NAME, node.getName());
			result.add(entity);
		}
		return result;
	}

	/**
	 * 把符合TableData格式规范的字符串转换为TableData对象
	 * 
	 * @param data
	 *            工作流传入参数
	 * @return TableData 对象数据
	 * @throws BusinessException
	 */
	private TableData getWFParameterEntity(String data) throws WFException {
		TableData entityData = null;
		if (data != null && !"".equals(data)) {
			Document xmlData = XMLTools.stringToDocument(data);

			try {
				entityData = new TableData(xmlData.getDocumentElement());
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(
						"WFGeneral的getWFParameterEntity方法的参数不符合TableData格式规范: "
								+ data);
			}
		}
		return entityData;
	}

	/**
	 * 更新流程信息
	 * 
	 * @param user
	 *            执行者
	 * @param sWfData
	 *            工作流数据
	 * @throws BusinessException
	 */
	public static void updateWFData(String user, String sWfData)
			throws BusinessException, WFException {
		TableData wfData = toTableData(sWfData);
		if (null == wfData) {
			throw new BusinessException(
					"错误031502: 参数 sWfData 不符合TableData格式规范: " + sWfData);
		}
		wfData.setField(WFConst.USER, user);
		if (null == wfData.getField(WFConst.PROCESS_INST_ID)) {
			throw new BusinessException("错误041601: 没有指定流程实例ID");
		}
		if (null != wfData.getField(WFConst.COMMENT)) {
			if (null == wfData.getField(WFConst.WORKITEM_ID)) {
				throw new BusinessException("错误041602: 没有指定工作项ID");
			}
			setTempComment(wfData);
		}
	}

	private static String SQL_SELECT_AS_WF_TEMP_COMMENT = "select WF_TEMP_COMMENT from AS_WF_TEMP_COMMENT where WF_WORKITEM_ID=?";

	private static String SQL_DELETE_AS_WF_TEMP_COMMENT = "delete AS_WF_TEMP_COMMENT where WF_WORKITEM_ID=?";

	/** 读取临时意见 */
	public static String selectTempComment(Object workitemId)
			throws BusinessException, SQLException {
		Integer nWorkitemId = null;
		try {
			nWorkitemId = Integer.valueOf(workitemId.toString(), 10);
		} catch (Exception e) {
			throw new BusinessException("Error042021: 无效的工作项ID: " + workitemId);
		}
		Object comment = DBHelper.queryOneValue(SQL_SELECT_AS_WF_TEMP_COMMENT,
				new Integer[] { nWorkitemId });
		return (null != comment) ? comment.toString() : null;
	}

	/** 删除临时意见 */
	public static int deleteTempComment(Object workitemId)
			throws BusinessException, SQLException {
		Integer nWorkitemId = null;
		try {
			nWorkitemId = Integer.valueOf(workitemId.toString(), 10);
		} catch (Exception e) {
			throw new BusinessException("Error042021: 无效的工作项ID: " + workitemId);
		}
		return DBHelper.executeUpdate(SQL_DELETE_AS_WF_TEMP_COMMENT,
				new Integer[] { nWorkitemId });
	}

	/** 设置流程意见（暂存，提交后删除） */
	public static void setTempComment(TableData wfData)
			throws BusinessException, WFException {
		try {
			// insert or update AS_WF_TEMP_COMMENT
			String sql = SQL_SELECT_AS_WF_TEMP_COMMENT;
			Integer workitemId = Integer.valueOf(wfData
					.getFieldValue(WFConst.WORKITEM_ID), 10);
			String comment = wfData.getFieldValue(WFConst.COMMENT);
			Map row = DBHelper.queryOneRow(sql, new Integer[] { workitemId });
			Object[] params = new Object[] { comment, workitemId };
			if (null == row) {
				DBHelper.executeUpdate(
						"insert into AS_WF_TEMP_COMMENT(WF_TEMP_COMMENT, WF_WORKITEM_ID)"
								+ " values(?, ?)", params);
			} else {
				DBHelper.executeUpdate(
						"update AS_WF_TEMP_COMMENT set WF_TEMP_COMMENT=?"
								+ " where WF_WORKITEM_ID=?", params);
			}
		} catch (NumberFormatException e) {
			throw new BusinessException(e.toString());
		} catch (SQLException e) {
			throw new BusinessException(e.toString());
		}
	}

	/**
	 * 将字符串转换为 TableData 格式
	 * 
	 * @param s
	 *            格式为...
	 * @return
	 */
	public static TableData toTableData(String s) {
		Document xmlData = XMLTools.stringToDocument(s);
		return new TableData(xmlData.getDocumentElement());
	}

	/** ************************************************************************* */
	/* add by xjh */
	public boolean isSerialNode(String activityId) throws WFException {
		Node nodeHandler = new Node();
		String executorsMethod;
		if (activityId == null) {
			return false;
		}

		try {
			NodeMeta currentNode = nodeHandler.getNode(Integer
					.parseInt(activityId));
			executorsMethod = currentNode.getExecutorsMethod();
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}

		if (executorsMethod != null) {
			return executorsMethod.equalsIgnoreCase(WFConst.WF_SERIAL_NODE);
		}
		return false;
	}

	/* judge if it is last serial executor */
	public static boolean isLastInSerialNode(String sTemplateId,
			String sActivityId, String sProcessInstId) throws WFException {

		int templateId = Integer.parseInt(sTemplateId);
		int activityId = Integer.parseInt(sActivityId);
		int processInstId = Integer.parseInt(sProcessInstId);

		boolean finished = false;

		Pass passCountHandler = new Pass();
		TaskExecutor taskExecutorOrderHandler = new TaskExecutor();

		try {
			Link linkHandler = new Link();
			List nodeLinkList = linkHandler.getFollowedLinkList(templateId,
					activityId);
			for (int i = 0; i < nodeLinkList.size(); i++) {
				Link nodeLink = (Link) nodeLinkList.get(i);

				int nextNodeId = nodeLink.getNextNodeId();
				int passValue = nodeLink.getPassValue();
				int passCount = passCountHandler.getPassNum(processInstId,
						activityId, nextNodeId) + 1;
				int passTotalCount = taskExecutorOrderHandler
						.getMainExecutorNumByNode(processInstId, activityId);
				if (passTotalCount == 0) {
					throw new WorkflowException(1235);
				}
				double passPercent = (double) passCount
						/ (double) passTotalCount;
				double needPercent = passValue / 100D;
				if (passPercent >= needPercent) { // 当前链链可完成
					finished = true;
				}
			}
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return finished;
	}

	/* get the only forward action */
	public static String getDefaultAction(String sTemplateId,
			String sActivityId, String sProcessInstId) throws BusinessException {

		if (sTemplateId == null && sActivityId == null
				&& sProcessInstId == null) {
			return "";
		}

		int templateId = Integer.parseInt(sTemplateId);
		int activityId = Integer.parseInt(sActivityId);

		String result = null;

		try {
			Link linkHandler = new Link();
			List nodeLinkList = linkHandler.getFollowedLinkList(templateId,
					activityId);
			Link nodeLink = (Link) nodeLinkList.get(0);
			result = nodeLink.getActionName();
		} catch (Exception e) {
			throw new BusinessException("Error052021: 无效的工作流定义，无后续流向: ");
		}
		return result;
	}

	/* get the first forward user */
	public static String getDefaultUser(String sUser, String sActivityId,
			String sProcessInstId) throws BusinessException {
		if (sActivityId == null && sProcessInstId == null) {
			return "";
		}

		int activityId = Integer.parseInt(sActivityId);
		int processInstId = Integer.parseInt(sProcessInstId);

		String result = "";
		TaskExecutor taskExecutorHandler = new TaskExecutor();

		try {
			List taskExecutorList = taskExecutorHandler
					.getFollowedExecutorList(processInstId, activityId, sUser);
			if (taskExecutorList.size() > 0) {
				TaskExecutorMeta taskExecutor = (TaskExecutorMeta) taskExecutorList
						.get(0);
				result = taskExecutor.getExecutor();
			}
		} catch (Exception e) {
			throw new BusinessException("Error052021: 无效的工作流定义，无后续用户: ");
		}
		return result;
	}

	/** ************************************************************************* */
	private static String getUserListStr() {
		StringBuffer buf = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			sql = "select STAFF_ID USER_ID, Name USER_NAME from v_as_staff";
			rs = stmt.executeQuery(sql);
			buf.append("<span id=\"U_EXECUTOR_LIST\">" + "\r\n");
			while (rs.next()) {
				buf.append("    <span id=\"U_" + rs.getString("USER_ID")
						+ "\" value=\"" + rs.getString("USER_NAME")
						+ "\" ></span>\r\n");
			}
			buf.append("</span>" + "\r\n");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}
		return buf.toString();
	}

	private static String getDeptListStr() {
		StringBuffer buf = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			sql = "select CO_CODE, CO_NAME from AS_COMPANY";
			rs = stmt.executeQuery(sql);
			buf.append("<span id=\"C_EXECUTOR_LIST\">" + "\r\n");
			while (rs.next()) {
				buf.append("    <span id=\"C_" + rs.getString("CO_CODE")
						+ "\" value=\"" + rs.getString("CO_NAME")
						+ "\" ></span>\r\n");
			}
			buf.append("</span>" + "\r\n");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return buf.toString();

	}

	private static String getRoleListStr() {
		StringBuffer buf = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			sql = "select ROLE_ID, ROLE_NAME from AS_ROLE";
			rs = stmt.executeQuery(sql);
			buf.append("<span id=\"R_EXECUTOR_LIST\">" + "\r\n");
			while (rs.next()) {
				buf.append("    <span id=\"R_" + rs.getString("ROLE_ID")
						+ "\" value=\"" + rs.getString("ROLE_NAME")
						+ "\" ></span>\r\n");
			}
			buf.append("</span>" + "\r\n");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return buf.toString();

	}

	private static String getPositionListStr() {
		StringBuffer buf = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			sql = "select POSI_CODE, POSI_NAME from AS_POSITION";
			rs = stmt.executeQuery(sql);
			buf.append("<span id=\"P_EXECUTOR_LIST\">" + "\r\n");
			while (rs.next()) {
				buf.append("    <span id=\"P_" + rs.getString("POSI_CODE")
						+ "\" value=\"" + rs.getString("POSI_NAME")
						+ "\" ></span>\r\n");
			}
			buf.append("</span>" + "\r\n");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return buf.toString();

	}

	private static String getOrgListStr() {
		StringBuffer buf = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			sql = "select ORG_CODE, ORG_NAME from AS_ORG";
			rs = stmt.executeQuery(sql);
			buf.append("<span id=\"O_EXECUTOR_LIST\">" + "\r\n");
			while (rs.next()) {
				buf.append("    <span id=\"O_" + rs.getString("ORG_CODE")
						+ "\" value=\"" + rs.getString("ORG_NAME")
						+ "\" ></span>\r\n");
			}
			buf.append("</span>" + "\r\n");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return buf.toString();
	}

	public static String getUserViewStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(getUserListStr());
		buf.append(getPositionListStr());
		buf.append(getRoleListStr());
		buf.append(getDeptListStr());
		buf.append(getOrgListStr());
		return buf.toString();
	}

	public static String getUserListXml(String type, String id) {
		StringBuffer buf = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.createStatement();
			String sql = "";
			if (type.equalsIgnoreCase("U")) {
				sql = "select USER_ID, EMP_NAME,EMP_INDEX from AS_EMP order by EMP_INDEX";
			} else if (type.equalsIgnoreCase("P")) {
				sql = "SELECT DISTINCT a.USER_ID AS USER_ID, a.EMP_NAME AS USER_NAME,a.EMP_INDEX FROM AS_EMP a ,AS_EMP_POSITION b WHERE a.EMP_CODE = b.EMP_CODE and b.POSI_CODE = '"
						+ id + "' order by a.EMP_INDEX";
			} else if (type.equalsIgnoreCase("R")) {
				sql = "SELECT DISTINCT a.USER_ID AS USER_ID, a.EMP_NAME AS USER_NAME,a.EMP_INDEX  FROM AS_EMP a ,AS_EMP_POSITION b,AS_POSI_ROLE c,AS_ROLE d WHERE a.EMP_CODE = b.EMP_CODE and b.POSI_CODE = c.POSI_CODE and c.ROLE_ID=d.ROLE_ID and d.ROLE_ID='"
						+ id + "' order by a.EMP_INDEX";
			} else if (type.equalsIgnoreCase("O")) {
				sql = "SELECT DISTINCT a.USER_ID AS USER_ID, a.EMP_NAME AS USER_NAME,a.EMP_INDEX  FROM AS_EMP a ,AS_EMP_POSITION b,AS_ORG c WHERE a.EMP_CODE = b.EMP_CODE and b.ORG_CODE = c.ORG_CODE and b.CO_CODE = c.CO_CODE and C.ORG_CODE='"
						+ id + "' order by a.EMP_INDEX";
			} else if (type.equalsIgnoreCase("C")) {
				sql = "SELECT DISTINCT a.USER_ID AS USER_ID, a.EMP_NAME AS USER_NAME,a.EMP_INDEX  FROM AS_EMP a ,AS_EMP_POSITION b,AS_COMPANY c WHERE a.EMP_CODE = b.EMP_CODE and b.CO_CODE = c.CO_CODE and C.CO_CODE='"
						+ id + "' order by a.EMP_INDEX";
			} else {
				throw new RuntimeException("缺少类型");
			}
			rs = stmt.executeQuery(sql);
			buf.append("<root_Data>" + "\r\n");
			while (rs.next()) {
				buf.append("  <RS_User>" + "\r\n");
				// buf.append(" <span id=\"U_" + rs.getString("USER_ID") + "\"
				// value=\""
				// + rs.getString("USER_NAME") + "\" ></span>\r\n");
				buf.append("    <USER_ID>" + rs.getString("USER_ID")
						+ "</USER_ID>\r\n");
				buf.append("    <USER_NAME>" + rs.getString("USER_NAME")
						+ "</USER_NAME>\r\n");
				buf.append("  </RS_User>" + "\r\n");
			}
			buf.append("</root_Data>" + "\r\n");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return buf.toString();
	}

	public static String getTaskExcutorInfoStr(String nodeIdStr,
			String instanceIdStr, String user, String action) {
		List lstExecutorInfo = new ArrayList();
		String strExecutorName_main = "";
		String strExecutorCode_main = "";
		String strExecutorName_assistant = "";
		String strExecutorCode_assistant = "";
		String strLabel = "";
		try {
			int nodeId = Integer.parseInt(nodeIdStr);
			int instanceId = Integer.parseInt(instanceIdStr);
			Node nodeHandler = new Node();
			Link linkHandler = new Link();

			NodeMeta currentNode = null;
			String executorsMethod;
			List nodeLinkList = null;

			currentNode = nodeHandler.getNode(nodeId);
			int templateId = currentNode.getTemplateId();
			executorsMethod = currentNode.getExecutorsMethod();
			nodeLinkList = linkHandler.getFollowedLinkList(templateId, nodeId,
					action);
			boolean isSetDefaultExecutorRelation = false;
			for (Iterator it = nodeLinkList.iterator(); it.hasNext();) {
				Link link = (Link) it.next();
				if (link.getExecutorRelation().equalsIgnoreCase(
						Link.EXECUTOR_RELATION_NONE)) {
					isSetDefaultExecutorRelation = false;
				} else {
					isSetDefaultExecutorRelation = true;
					strLabel = "默认办理人:";
					// strExecutorCode_main=wfs.getd
					if (link.getExecutorRelation().equalsIgnoreCase(
							Link.EXECUTOR_RELATION_MANAGER)) {
						// lstExecutorInfo.add("默认办理人是组织上级");
						strExecutorName_main = "组织上级";
					} else if (link.getExecutorRelation().equalsIgnoreCase(
							Link.EXECUTOR_RELATION_BUSINESS_SUPPERIOR)) {
						strExecutorName_main = "业务上级";
					} else if (link.getExecutorRelation().equalsIgnoreCase(
							Link.EXECUTOR_RELATION_SELF)) {
						strExecutorName_main = "自己";
						// strExecutor

					}
				}
			}
			if (!isSetDefaultExecutorRelation) {
				if (executorsMethod.equals(Node.EXECUTORS_METHOD_SOLO)
				/* || canNodeBeFinished */) {
					List nextNodes = ExecuteFacade.getFollowedTaskNodeList(
							templateId, nodeId, action);
					if (null != nextNodes && nextNodes.size() > 0) {
						for (Iterator it = nextNodes.iterator(); it.hasNext();) {
							NodeMeta node = (NodeMeta) it.next();
							ConfigureFacade.resetExecutorOrder(node.getId(),
									Node.EXECUTORS_METHOD_SOLO);
							List executors = ConfigureFacade
									.getExecutorListByOrder(node.getId());
							for (int i = 0; i < executors.size(); i++) {
								Executor e = (Executor) executors.get(i);
								if (e.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN) {
									if (strExecutorName_main == "")
										strExecutorName_main += e
												.getExecutorName();
									else
										strExecutorName_main += ";"
												+ e.getExecutorName();

									if (strExecutorCode_main == "")
										strExecutorCode_main += e.getExecutor();
									else
										strExecutorCode_main += ";"
												+ e.getExecutor();
								} else if (e.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_ASSISTANT) {
									if (strExecutorName_assistant == "")
										strExecutorName_assistant += e
												.getExecutorName();
									else
										strExecutorName_assistant += ";"
												+ e.getExecutorName();

									if (strExecutorCode_assistant == "")
										strExecutorCode_assistant += e
												.getExecutor();
									else
										strExecutorCode_assistant += ";"
												+ e.getExecutor();
								}
							}
						}
					}
					boolean existDefaultExecutor = (strExecutorCode_main
							.length() > 0 || strExecutorCode_assistant.length() > 0);
					if (existDefaultExecutor)
						strLabel = "默认办理人:";
					else
						;// 就不显示默认信息了
				} else {
					if (executorsMethod.equals(Node.EXECUTORS_METHOD_PARALLEL)) {
						strLabel = "当前环节是并签环节: ";
						strExecutorName_main = "没有默认提交人";
					} else {
						strLabel = "当前环节是顺签环节。 ";
						TaskExecutor taskExecutorHandler = new TaskExecutor();
						List taskExecutorList = taskExecutorHandler
								.getFollowedExecutorList(instanceId, nodeId,
										user);
						for (int i = 0; i < taskExecutorList.size(); i++) {
							TaskExecutorMeta e = (TaskExecutorMeta) taskExecutorList
									.get(i);
							if (e.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_MAIN) {
								if (strExecutorName_main == "")
									strExecutorName_main += e.getExecutorName();
								else
									strExecutorName_main += ";"
											+ e.getExecutorName();

								if (strExecutorCode_main == "")
									strExecutorCode_main += e.getExecutor();
								else
									strExecutorCode_main += ";"
											+ e.getExecutor();
							} else if (e.getResponsibility() == TaskExecutorMeta.TASK_EXECUROR_RESPONSIBILITY_ASSISTANT) {
								if (strExecutorName_assistant == "")
									strExecutorName_assistant += e
											.getExecutorName();
								else
									strExecutorName_assistant += ";"
											+ e.getExecutorName();

								if (strExecutorCode_assistant == "")
									strExecutorCode_assistant += e
											.getExecutor();
								else
									strExecutorCode_assistant += ";"
											+ e.getExecutor();
							}
							// result = result + "," +
							// taskExecutor.getExecutorName();
						}
						boolean existDefaultExecutor = (strExecutorCode_main
								.length() > 0 || strExecutorCode_assistant
								.length() > 0);
						if (existDefaultExecutor) {
							strLabel = "顺签环节默认办理人 默认提交人是:";
						} else {
							strLabel = "当前环节是顺签环节:";
							strExecutorName_main = "没有指定默认提交人.";
						}
					}
				}
			}// end if set default executor relation;

			lstExecutorInfo.add(strLabel);
			lstExecutorInfo.add(strExecutorName_main);
			lstExecutorInfo.add(strExecutorCode_main);
			lstExecutorInfo.add(strExecutorName_assistant);
			lstExecutorInfo.add(strExecutorCode_assistant);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return StringTools.getMiddleString(lstExecutorInfo.toString(), 1);
	}

	/**
	 * 删除流程实例
	 * 
	 * @param deltaData
	 *            字符串类型的delta格式数据，包含要删除的流程实例id信息。并且处理多个流程实例的批量删除。
	 * @return 返回空
	 * @throws
	 * @see
	 */
	public void deleteInstance(String deltaData) throws BusinessException {
		Delta wfdelta = new Delta(deltaData);// 包含工作流部件主键信息和业务部件主键信息
		Iterator iter = wfdelta.iterator();
		try {
			while (iter.hasNext()) {
				TableData wfTableData = (TableData) iter.next();
				WorkflowService workflowService = (WorkflowService) ApplusContext
						.getBean("workflowService");
				workflowService.deleteWithWorkflow(wfTableData);
			}
		} catch (Exception e) {
			throw new BusinessException("WFGeneral类的deleteInstance方法错误，"
					+ e.toString());
		}
	}// :~

	/**
	 * 提供给客户端调用的接口，在config.xml中配置 配置项为
	 * 
	 * <pre>
	 *  
	 *      &lt;function name=&quot;queryWFState&quot; componame=&quot;all&quot;&gt;
	 *      &lt;params&gt;
	 *      &lt;parameter name=&quot;nodeId&quot; type=&quot;String&quot; from=&quot;browser&quot;/&gt;
	 *      &lt;parameter name=&quot;wfStateName&quot; type=&quot;String&quot; from=&quot;browser&quot;/&gt;
	 *      &lt;parameter name=&quot;offset&quot; type=&quot;String&quot; from=&quot;browser&quot;/&gt;
	 *      &lt;/params&gt;
	 *      &lt;model classname=&quot;com.anyi.erp.workflow.WFGeneral&quot; methodname=&quot;queryWFState&quot; returntype=&quot;string&quot;/&gt;
	 *      &lt;/function&gt;
	 *  
	 * </pre>
	 * 
	 * 作用：查询指定节点（或者指定节点前面的或者后续的节点的状态值），预期的调用者为用以同步工作流状态与业务数据。
	 * 
	 * @param nodeId
	 *            节点id
	 * @param wfStateName
	 *            需要查询的状态名称
	 * @param offset
	 *            需要查询的节点的偏移值，0表示当前节点，即第一个参数节点。
	 * @param ActionName
	 *            该流向的名称
	 * @return
	 * @throws
	 * @see
	 */
	public static String queryWFState(String nodeId, String wfStateName,
			String offset, String actionName) {
		/**/
		int nNodeId = -1;// 客户端传过来的节点Id
		int nOffset = 0;// 偏移量
		int nTemplateId = -1;
		Node node = null;
		String result = null;

		/* 处理参数 */
		try {
			nNodeId = Integer.parseInt(nodeId);
			nOffset = Integer.parseInt(offset);
		} catch (Exception e) {
			// TCJLODO: handle exception
			log.error("nodeId和offset必须是数字！");
			throw new RuntimeException("nodeId和offset必须是数字！");
		}

		if (!(nOffset == 0 || nOffset == -1 || nOffset == 1)) {
			log.error("WFGeneral.getWFState()参数不正确，nOffset只能取-1,1,0执！");
			throw new RuntimeException("获取工作流状态值出现异常");
		}
		node = new Node();

		try {
			NodeMeta nodeMeta = node.getNode(nNodeId);
			if (null != nodeMeta) {
				nTemplateId = nodeMeta.getTemplateId();
			}
			result = getWFStateValue(getNodeIdByOffset(nTemplateId, nNodeId,
					nOffset, actionName), nTemplateId, wfStateName);
		} catch (WorkflowException e) {
			log.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * 获取指定节点前面或后面n个节点,偏移量n由参数offset指定
	 * 
	 * @param conn
	 * @param nodeId
	 * @param offset
	 * @return
	 * @throws
	 * @see
	 */
	static int getNodeIdByOffset(int templateId, int nodeId, int offset,
			String excecuteMethodName) {
		if (offset == 0)
			return nodeId;
		NodeMeta theNode = null;
		Node node = new Node();
		try {
			if (offset == 1) {
				// 向后查找
				List list = node.getFollowedTaskNodeList(templateId, nodeId,
						excecuteMethodName);
				if (list.size() == 0) {
					log.error("请确认你要查询的后续节点是否存在!");
					throw new RuntimeException("请确认你要查询的后续节点是否存在!");
				} else if (list.size() > 1) {
					log.error("WFGeneral.getNodeByOffset(...)出现异常!");
					throw new RuntimeException("获取工作流状态值时出现异常!");
				} else if (list.size() == 1) {
					theNode = (NodeMeta) list.get(0);
					nodeId = theNode.getId();
				}
			} else if (offset == -1) {
				// 向前查找
				List list = node.getPrecedingNodeList(templateId, nodeId);
				if (list.size() == 0) {
					log.error("请确认你要查询的前面节点是否存在!");
					throw new RuntimeException("请确认你要查询的后续节点是否存在!");
				} else if (list.size() > 1) {
					log.error("WFGeneral.getNodeByOffset(...)出现异常!");
					throw new RuntimeException("获取工作流状态值时出现异常!");
				} else if (list.size() == 1) {
					theNode = (NodeMeta) list.get(0);
					nodeId = theNode.getId();
				}
			} else {
				log.error("WFGeneral.getWFState()参数不正确，nOffset只能取-1,1,0执！");
				throw new RuntimeException("获取工作流状态值出现异常");
			}
		} catch (WorkflowException e) {
			// TCJLODO: handle exception
			log.error("工作流异常!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return theNode.getId();
	}

	/**
	 * 获取当前节点的的指定状态值。
	 * 
	 * @param statusName
	 * @return
	 * @throws
	 * @see
	 */
	static String getWFStateValue(int nodeId, int templateId, String statusName) {
		String result = null;
		NodeState ns = new NodeState();
		try {
			List list = ns.getStateListByNode(nodeId);
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				NodeState nodeState = (NodeState) iter.next();
				State state = new State();
				String wfStateName = state.getState(nodeState.getStateId())
						.getName();//
				if (wfStateName.equalsIgnoreCase(statusName))
					result = nodeState.getStateValue();
			}
		} catch (WorkflowException e) {
			log.error("WFStatusSnycTaskh获取状态时出错!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	// TCJLODO add arguments userId ,it should come from session.
	public String wfCommit(String sWfData, String strBnData) {
		String result = "";
		TableData wfData = null;
		TableData bnData = null;
		try {
			Document xmlData = XMLTools.stringToDocument(sWfData);
			wfData = new TableData(xmlData.getDocumentElement());
			bnData = new TableData(strBnData);

			List lsWFVariable = new ArrayList();
			String user = wfData.getFieldValue(WFConst.USER);
			//
			user = "sa";
			ActionBean action = new ActionBean(wfData);
			WFService wfs = WFFactory.getInstance().getService();
			WorkitemBean item = wfs.findWorkitem(wfData
					.getField(WFConst.WORKITEM_ID), 1);
			item = wfs.completeWorkitem(user, item, action, lsWFVariable,
					bnData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}

	/**
	 * 回退汇总任务
	 * 
	 * @param strInstanceId
	 * @param strNodeId
	 * @param executor
	 * @param comment
	 * @return
	 * @throws WFException
	 */
	public Delta untreadCollectFlow(String strInstanceId, String strNodeId,
			String executor, String comment, String strBnData)
			throws WFException {
		WFService wfs = WFFactory.getInstance().getService();
		try {
			int nInstanceId = Integer.parseInt(strInstanceId);
			int nNodeId = Integer.parseInt(strNodeId);
			TableData bnData = getWFParameterEntity(strBnData);
			ExecuteFacade.givebackCollectedFlow(nInstanceId, nNodeId, executor,
					comment);
			wfs.syncDataByBindedWFSate(nInstanceId);
			ActivityDefBean activityDef = wfs.findActivityDefBean(nInstanceId,
					nNodeId);
			wfs.rewriteComment(nInstanceId + "", nNodeId + "", "", bnData,
					activityDef);
			wfs.refreshBrief(nInstanceId + "", nNodeId + "", bnData,
					activityDef);
		} catch (WorkflowException e) {
			e.printStackTrace();
			throw new WFException("回退汇总任务出错！");
		}
		return new Delta();
	}
}
