//$Id: CZWFService.java,v 1.45 2009/04/30 07:16:46 liuxiaoyong Exp $

package com.anyi.gp.workflow;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.MethodUtils;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.meta.CompoMeta;
import com.anyi.gp.meta.MetaManager;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.util.StringTools;
import com.anyi.gp.workflow.bean.ActionBean;
import com.anyi.gp.workflow.bean.ActivityDefBean;
import com.anyi.gp.workflow.bean.BindStateInfo;
import com.anyi.gp.workflow.bean.LinkDefBean;
import com.anyi.gp.workflow.bean.ProcessDefBean;
import com.anyi.gp.workflow.bean.ProcessInstBean;
import com.anyi.gp.workflow.bean.WorkitemBean;
import com.anyi.gp.workflow.util.WFCompoType;
import com.anyi.gp.workflow.util.WFConst;
import com.anyi.gp.workflow.util.WFException;
import com.anyi.gp.workflow.util.WFUtil;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.access.OrgPositionBean;
import com.kingdrive.workflow.business.CurrentTask;
import com.kingdrive.workflow.business.Instance;
import com.kingdrive.workflow.business.Link;
import com.kingdrive.workflow.business.Staff;
import com.kingdrive.workflow.business.Template;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.dto.InstanceMeta;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.StateValueMeta;
import com.kingdrive.workflow.dto.TaskMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.listener.TaskListener;
import com.kingdrive.workflow.model.OrgPositionModel;

/**
 * 财政工作流接口类 变量命名说明: 此处ActivityId等同于nodeid , processid等同于templateid
 */
public class CZWFService implements WFService {
	// log4j for log
	final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(CZWFService.class);

	/**
	 * 提交工作项
	 * <p>
	 * 注意: 一旦提交， workitem 就不存在了，返回的 WorkitemBean 没什么意义
	 * 
	 * @param user
	 *            执行操作的用户
	 * @param workitem
	 *            要提交的工作项
	 * @param action
	 *            执行的操作
	 * @param parameters
	 *            提交到工作流的参数，元素类型为 VariableInfo。 将 action 和 parameters 分开，是考虑到
	 *            action 设置的是一些特殊的参数， 主要用于人工指定下一步的活动和执行者，而 parameters 主要用于由工作流机
	 *            根据参数自动判断下一步的活动。
	 * @param conn
	 *            数据库连接
	 * @return 提交后的工作项对象，活动状态为"已完成"
	 * @throws WFException
	 */
	public WorkitemBean completeWorkitem(String user, WorkitemBean workitem,
			ActionBean action, List parameters, TableData bndata)
			throws WFException {
		try {
			int activityId = Integer.parseInt(workitem.getActivityId()
					.toString(), 10);
			int processInstId = Integer.parseInt(workitem.getProcessInstId()
					.toString(), 10);
			int workitemId = Integer.parseInt(workitem.getWorkitemId()
					.toString(), 10);
			List variableValueList = parameters;

			String instName = workitem.getInstanceName();
			String instDesc = workitem.getInstanceDescription();
      int tpId = Integer.parseInt(workitem.getTemplateId().toString());
			// 1. 如果指定了执行者，创建下一个节点的动态执行者
			// 此处创建执行人，是对引擎的执行人的补充
			// 引擎中，只能定义时指定执行人，而此处可以在运行时指定执行人，对引擎的补充
			// TCJLODO：这一点有问题。得到的List只是根据模板得出的，未考虑变量值的因素
			// 可能把不会执行的节点的执行人也生成了出来。已修改。
      
      List nextLinkList = WFUtil.getRightFollowedTaskLinkList(processInstId, tpId, activityId, action.getActionId(), variableValueList);// 2条查询
			WFUtil.createNextExecutor(workitem, action, nextLinkList);//TODO:nextLinkList参数传入
			// 2. 提交工作流
			ExecuteFacade.executeTask(workitemId, processInstId, instName,
					instDesc, activityId, action.getActionId(), action
							.getComment(), variableValueList, nextLinkList, user, action
							.getPositionId());
			// 标记工作完成
			workitem.setActivityState(WFConst.ACT_DONE);
			ActivityDefBean activityDef = this.findActivityCompo(
					processInstId, activityId);
			rewriteComment(processInstId + "", activityId + "", action
					.getComment(), bndata, activityDef);
			refreshBrief(processInstId + "", activityId + "", bndata,
					activityDef);
		} catch (NumberFormatException e) {
			throw new WFException("Error031701:提交工作项出错！");
		} catch (WorkflowException e) {
			switch (e.getCode()) {
			case 1122:
				throw new WFException("任务节点没有预置执行者或指派任务执行者顺序错误，请正确指派任务执行者。");
			case 1225:
				throw new WFException("流程条件不满足,请检查流程流转分支的条件是否已经满足!");
			default:
				throw new WFException(e.toString());
			}
		}
		return workitem;
	}

	/**
	 * 最简洁的提交方法。所有缺失的参数和数据会从数据库中提取。 为批量提交和给其他系统提供提交接口的目的创建此方法。
	 * 
	 * @param strInstanceId
	 *            必需。流程实例Id，存在于业务表中的process_inst_id字段。新增时该值必须为0
	 * @param strTemplateId
	 *            必需。模板Id。当该提交是新增实例时必需，不是新增时值可为任意值。
	 * @param strCompoId
	 *            必需。部件真实Id.
	 * @param strUserId
	 *            必需。用户Id.
	 * @param wfData
	 *            非必需。工作流数据列表，如果为空，会从数据库提取。 内容包括：WF_POSITION_ID 职位代码(唯一码)；
	 *            WFData.setWFVariable(sVarName, sVarValue)来拼接该字符串 格式范例：
	 *            <entity> <field name="WF_INSTANCE_NAME"
	 *            value="关于区法院的拨款"/>//新建流程时必需 <field name="WF_INSTANCE_DESC"
	 *            value="关于区法院的拨款"/>//新建流程时使用 <field name="WF_COMPANY_CODE"
	 *            value="601"/>//使用业务上级时必需 <field name="WF_ORG_CODE"
	 *            value="02"/>//使用业务上级时必需 <field name="WF_POSITION_ID"
	 *            value="112955359445300002"/>//使用业务上级时必需 <field
	 *            name="WF_NEXT_EXECUTOR_ID" value="zhangsa"/>//指定下一节点的执行者时需要;
	 *            <entity name="WF_VARIABLE"> <row> <entity> <field
	 *            name="VariableId" value="701" /> <field name="VariableName"
	 *            value="vTest" /> <field name="VariableValue" value="1" />
	 *            </entity> </row> <row> <entity> <field name="VariableId"
	 *            value="702" /> <field name="VariableName" value="vAudit" />
	 *            <field name="VariableValue" value="Yes" /> </entity> </row>
	 *            </entity> </entity>
	 * @return 提交成功，返回"succes"，否则返回提示信息
	 */
	/* Author:zhanggh */
	public String commitSimply(String strInstanceId, String strTemplateId,
			String strCompoId, String strUserId, TableData wfData,
			TableData bnData) throws WFException {
    String strResult = "failed";
    String strExecutors = "";
    String strExecutors2 = "";
    TableData tableData = bnData;
    List valueList = null;
    try {
      // 必需的参数不完整
      if ((null == strInstanceId && null == strTemplateId) || null == strCompoId
        || null == strUserId) {
        return "必需的参数不完整";
      }
      if (tableData == null) {
        tableData = WFUtil.getBusinessDataByInsId(strCompoId, strInstanceId);//1条查询
        tableData.setName(strCompoId);
      }// 若从前台无发送tabledata ,从数据库查找

      WorkitemBean workitem = getWorkItemByInstance(strUserId, strInstanceId, 1);//1条查询
      // 取得workitem
      if (null == workitem) {
        return "找不到待办任务";
      }
      // getWorkItemByInstance原来返回Id,但实际抓取的workitemBean与findWorkitem查询的结果一样，故无需再次查询
      // WorkitemBean workitem = findWorkitem(strWorkItemId, 1, conn);
      String strTplId = workitem.getTemplateId().toString();
      int nNodeId = Integer.parseInt(workitem.getActivityId().toString());
      int nInstanceId = Integer.parseInt(strInstanceId);
      int nWorkItemId = Integer.parseInt(workitem.getWorkitemId().toString());
      int nTemplateId = Integer.parseInt(strTplId);

      String strCoCode = (String) wfData.getField("WF_COMPANY_CODE");
      String strOrgCode = (String) wfData.getField("WF_ORG_CODE");
      String strPositionId = (String) wfData.getField(WFConst.POSITION_ID);
      String strComment = (String) wfData.getFieldValue(WFConst.WF_COMMENT);
      String strNd = (String) wfData.getField(WFConst.ND);
      String actionName = wfData.getFieldValue("WF_ACTION");

      wfData.setField(WFConst.WF_TEMPLATE_ID, strTplId);
      wfData.setField(WFConst.PROCESS_INST_ID, strInstanceId);
      wfData.setField(WFConst.WF_ACTIVITY_ID, Integer.toString(nNodeId));
      wfData.setField(WFConst.POSITION_ID, strPositionId);
      wfData.setField(WFConst.ND, strNd);
      wfData.setField(WFConst.ACTION_NAME, actionName);

      TaskListener listener = Instance.getListener(nNodeId);
      CurrentTask taskHandler = new CurrentTask();
      CurrentTaskMeta currentTask = taskHandler.getCurrentTask(nWorkItemId, 1);
      //before
      exeExecListenerCallback(listener, currentTask, 0);

      if ((null == valueList || valueList.isEmpty())) {
        valueList = getWFVariableValueList(strCompoId, tableData, wfData);// 10条查询
      }
      //先查询出下一个节点信息，当参数传入。
      List nextLinkList = WFUtil.getRightFollowedTaskLinkList(nInstanceId,
        nTemplateId, nNodeId, actionName, valueList);// 2条查询
      // 业务数据直接从前台送回
      Set executors = getDefaultNextExecutor(tableData, wfData, strCompoId,
        strUserId, strCoCode, strOrgCode, strPositionId, true, strNd, valueList,
        nextLinkList);//11条查询
      if (null != executors) {
        for (Iterator i = executors.iterator(); i.hasNext();) {
          strExecutors += (String) i.next();
          strExecutors += ",";
        }
        if (null != strExecutors && strExecutors.length() > 0) {
          strExecutors = strExecutors.substring(0, strExecutors.length() - 1);
        }
      }
      if (!StringTools.isEmptyString(strExecutors)) {
        WFUtil.createNextExecutor(nTemplateId, nInstanceId, nNodeId, strExecutors,
          strExecutors2, nextLinkList, actionName);//5条查询
      }
      ExecuteFacade.executeTask(nWorkItemId, nInstanceId, null, null, nNodeId,
        actionName, strComment, valueList, nextLinkList, strUserId, strPositionId);//35条查询
      //after
      exeExecListenerCallback(listener, currentTask, 1);
      
      syncDataByBindedWFSate(nInstanceId, nTemplateId);// 6条查询
      ActivityDefBean activityDef = findActivityCompo(nInstanceId, nNodeId);// //3条查询
      rewriteComment(nInstanceId + "", nNodeId + "", strComment, tableData,
        activityDef);// 1条查询
      refreshBrief(nInstanceId + "", nNodeId + "", tableData, activityDef);//2条查询
      strResult = "success";// 成功
    } catch (WorkflowException wfe) {
      strResult = wfe.getMessage();
      logger.info(strResult);
      throw new WFException(strResult);
    } catch (BusinessException be) {
      strResult = be.getMessage();
      logger.info(strResult);
      throw new WFException(strResult);
    }

    return strResult;
	}

	/**
	 * 最简洁的退回方法。所有缺失的参数和数据会从数据库中提取。 为批量退回和给其他系统提供提交接口的目的创建此方法。
	 * 
	 * @param strInstanceId
	 *            必需。流程实例Id，存在于业务表中的process_inst_id字段。
	 * @param strUserId
	 *            必需。用户Id.
	 * @return 提交成功，返回"succes"，否则返回提示信息
	 */
	/* Author:zhanggh */
	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData) throws WFException {
		return untreadSimply(strInstanceId, strUserId, comment, bnData, -1);
	}

	public String untreadSimply(String strInstanceId, String strUserId,
			TableData bnData) throws WFException {
		return untreadSimply(strInstanceId, strUserId, "", bnData, -1);
	}

	public String untreadSimply(String strInstanceId, String strUserId,
			String comment, TableData bnData, int toWhere) throws WFException {
		String strResult = "failed";
		String strWorkItemId, strNodeId;
		int nInstanceId, nNodeId;

		try {
			// 必需的参数不完整
			if (null == strInstanceId || null == strUserId) {
				return "必需的参数不完整";
			}
			WorkitemBean workItem = getWorkItemByInstance(strUserId,
					strInstanceId, 1);
			// 取得workitem
			if (null == workItem) {
				return "找不到待办任务";
			}
			// WorkitemBean workitem =
			// findWorkitem(workItemBean.getWorkitemId(), 1,
			// conn);
			strNodeId = workItem.getActivityId().toString();
			nInstanceId = Integer.parseInt(strInstanceId);
			nNodeId = Integer.parseInt(strNodeId);
			untread(nInstanceId, nNodeId, toWhere, strUserId, comment, bnData);
			// rewriteComment(strInstanceId, nNodeId + "", comment, bnData);
			// refreshBrief(strInstanceId, nNodeId + "", bnData);
			strResult = "success";
		} catch (BusinessException be) {
			strResult = be.getMessage();
			logger.info(be.getMessage());
			throw new WFException(strResult);
		}
		return strResult;
	}

	/**
	 * 重做实例流程 实例结束后(状态为结束(9))，重新操作，进行修改。
	 * 
	 */
	public String rework(String instanceId, String strUserId, String comment,
			TableData bnData) throws WFException {
		String strResult = "failed";
		int nInstanceId;
		try {
			// 必需的参数不完整
			if (null == instanceId || null == strUserId) {
				return "必需的参数不完整";
			}
			nInstanceId = Integer.parseInt(instanceId);
			ExecuteFacade.rework(nInstanceId, strUserId, comment);
			syncDataByBindedWFSate(nInstanceId);
			ActivityDefBean activityDef = this.findActivityCompo(nInstanceId,
					-1);
			rewriteComment(instanceId, null, comment, bnData, activityDef);
			refreshBrief(instanceId, null, bnData, activityDef);
			strResult = "success";
		} catch (WorkflowException wfe) {
			// //strResult = wfe.getMessage();
			throw new WFException(wfe.getMessage());
		}
		return strResult;
	}

	// 中止实例 至状态为-9
	public void interruptInstance(String instanceId, String svUserId,
			String comment) throws BusinessException {
		if (instanceId == null || svUserId == null) {
			throw new IllegalArgumentException("interrupt的参数为null");
		}
		int intInstanceId = Integer.parseInt(instanceId);
		try {
			ExecuteFacade.interruptInstance(intInstanceId, svUserId, comment);
		} catch (Exception ex) {
			throw new BusinessException(ex.toString());
		}
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

	/**
	 * 取待办事宜(根据用户和流程实例ID)
	 * 
	 * @param user
	 * @param processInstId
	 * @param conn
	 * @return 元素类型为 WorkitemBean，如果没有，返回长度为 0 的 List
	 * @throws WFException
	 */
	public List getTodoListByProcessInst(String user, Object processInstId,
			int isValid) throws WFException {
		List workitems = new ArrayList();
		if (null == processInstId) {
			return workitems;
		}
		List taskMetas = null;
		try {
			taskMetas = ExecuteFacade.getTodoListByExecutorAndInstance(user,
					Integer.parseInt(processInstId.toString(), 10), isValid);
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		if (null == taskMetas) {
			return workitems;
		}
		// 将返回值的元素类型由 CurrentTaskMeta 变为 WorkitemBean
		for (Iterator i = taskMetas.iterator(); i.hasNext();) {
			CurrentTaskMeta item = (CurrentTaskMeta) i.next();
			workitems.add(WFUtil.copyWorkitem(item));
		}
		return workitems;
	}

	/**
	 * 取待办事宜(根据用户和流程实例ID)
	 * 
	 * @return 元素类型为 WorkitemBean
	 */
	public List getTodoListByProcessInstanceId(int processInstanceId,
			int isValid) throws WFException {
		List result = new ArrayList();
		try {
			// TCJLODO: 改变返回值元素类型为 WorkitemBean，现在是 ActionMeta
			result = ExecuteFacade.getTodoListByInstance(processInstanceId,
					isValid);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return result;
	}

	/**
	 * 取得流程历史
	 * 
	 * @param processInstId
	 *            流程实例ID
	 * @param conn
	 *            数据库连接
	 * @return 元素类型为 WorkitemBean，为空时返回长度为 0 的 List
	 * @throws WFException
	 */
	public List getProcessHistory(Object processInstId, int isValid)
			throws WFException {
		List workitems = new ArrayList();
		if (null == processInstId) {
			return workitems;
		}
		List actionMetas = null;
		try {
			actionMetas = ExecuteFacade.getActionListByInstance(Integer
					.parseInt(processInstId.toString(), 10), isValid);
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		if (null == actionMetas) {
			return workitems;
		}
		// 将返回值的元素类型 ActionMeta 变为 WorkitemBean
		for (Iterator i = actionMetas.iterator(); i.hasNext();) {
			ActionMeta item = (ActionMeta) i.next();
			workitems.add(WFUtil.copyWorkitem(item));
		}
		return workitems;
	}

	/**
	 * 取待办事宜(根据流程实例ID)
	 * <p>
	 * 目前这个函数与 getProcessHistory 重复，功能相同，只是返回值类型不同
	 * 
	 * @return 元素类型为 WorkitemBean
	 */
	public List getDoneListByProcessInstanceId(Object processInstanceId,
			int isValid) throws WFException {
		List result = new ArrayList();
		try {
			// TCJLODO: 改变返回值元素类型为 WorkitemBean，现在是 ActionMeta
			result = ExecuteFacade.getActionListByInstance(Integer.parseInt(
					processInstanceId.toString(), 10), isValid);
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		return result;
	}

	public List getInstaceListByTemplate(String templateId) throws WFException {
		List result = new ArrayList();
		try {
			// TCJLODO: 改变返回值元素类型为 WorkitemBean，现在是 ActionMeta
			if (templateId == null) {
				result = ExecuteFacade.getInstanceList();
			} else {
				result = ExecuteFacade.getInstanceList(Integer
						.parseInt(templateId));
			}
		} catch (Exception e) {
			throw new WFException(e.toString());
		}
		return result;
	}

	public List getDoneListByProcessInstanceId(int processInstanceId,
			int isValid) throws WFException {
		return getDoneListByProcessInstanceId(new Integer(processInstanceId),
				isValid);
	}

	/**
	 * 创建实例
	 * 
	 * @param templateId
	 *            流程ID
	 * @param name
	 *            实例名称
	 * @param description
	 *            实例描述
	 * @param user
	 *            创建人
	 * @param conn
	 *            数据库连接
	 * @return 一个新的流程实例对象，*一定*不为 null
	 * @throws WFException
	 */
	public ProcessInstBean createInstance(String userId, ProcessInstBean inst,
			TableData bnData) throws WFException {
		TaskMeta task = null;
		try {
			task = ExecuteFacade.createInstance(Integer.parseInt(inst
					.getTemplateId().toString(), 10), inst.getName(), inst
					.getDescription(), userId);
			ActivityDefBean activityDef = this.findActivityDefBean(task
					.getInstanceId(), -1);
			refreshBrief(task.getInstanceId() + "", null, bnData, activityDef);
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		if (null == task) {
			throw new WFException("Error031201:创建工作流失败！");
		}
		return WFUtil.copyProcessInst(task);
	}

	/**
	 * 更新流程信息(名称、描述等)
	 * 
	 * @param inst
	 * @param conn
	 * @return
	 * @throws WFException
	 */
	public ProcessInstBean updateInstance(ProcessInstBean inst)
			throws WFException {
		try {
			ExecuteFacade.updateInstance(Integer.parseInt(inst
					.getProcessInstId().toString(), 10), inst.getName(), inst
					.getDescription());
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		return inst;
	}

	/** 删除流程 */
	public void deleteInstance(String userId, Object instId) throws WFException {
		try {
			// 先 deactivate，再删除
			ExecuteFacade.deactivateInstance(Integer.parseInt(
					instId.toString(), 10), userId, ""); // _hd
			ExecuteFacade.removeInstance(Integer
					.parseInt(instId.toString(), 10));
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
	}

	/**
	 * 根据 ID 构造流程实例对象
	 * 
	 * @return *一定*不为 null
	 */
	public ProcessInstBean findProcessInst(Object processInstId)
			throws WFException {
		InstanceMeta inst = null;
		try {
			int n = Integer.parseInt(processInstId.toString(), 10);
			inst = ExecuteFacade.getInstance(n);
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		if (null == inst) {
			throw new WFException("Error031401:找不到指定的流程实例！");
		}
		return WFUtil.copyProcessInst(inst);
	}

	/**
	 * 根据工作项ID构造工作项对象
	 * <p>
	 * TODO: 是否要加上执行者?
	 * 
	 * @return *一定*不为 null
	 */
	public WorkitemBean findWorkitem(Object workitemId, int isValid)
			throws WFException {
		TaskMeta task = null;
		try {
			int n = Integer.parseInt(workitemId.toString(), 10);
			task = ExecuteFacade.getTask(n, isValid);
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		if (null == task || 0 == task.getCurrentTaskId()) {
			throw new WFException("Error031402:找不到指定的工作项！");
		}
		return WFUtil.copyWorkitem(task);
	}

	public Set getDefaultNextExecutor(TableData entityData, TableData wfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, String nd, List wfVarList,List nextLinkList)
			throws WFException {
		return getDefaultNextExecutor(entityData, wfData, entityName, junior,
				junCoCode, junOrgCode, junPosiCode, false, nd, wfVarList,nextLinkList);
	}

	/**
	 * 根据前后执行者的关系取得下一节点的主办执行人
	 */
	public Set getDefaultNextExecutor(TableData entityData, TableData wfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, boolean flag, String nd,
			List wfVarList,List nextLinkList) throws WFException {
		Set executors = new HashSet();
		int nodeId = Integer.parseInt(wfData.getFieldValue(WFConst.WF_ACTIVITY_ID));
		String action = wfData.getFieldValue(WFConst.ACTION_NAME);
		if ("".equals(action)) {// 如果没有该参数，就改为null
		  action = null;
		}
		int templateId = Integer.parseInt(wfData.getFieldValue(WFConst.WF_TEMPLATE_ID));
		int instanceId = Integer.parseInt(wfData.getFieldValue(WFConst.PROCESS_INST_ID));
		// 不使用从session中取来的positionsId,改用WFData中的
		String orgPosiCode = wfData.getFieldValue(WFConst.POSITION_ID);
		// 从orgposicode到positionid的转换
		try {
			/*
    			List valueList = wfVarList;// getWFVariableValueList(entityName,entityData, wfData);
          List nodeLinkList = new Link().getFollowedLinkList(templateId,nodeId, action);
    			nodeLinkList = new Instance().getRightFollowedTaskLinkList(instanceId, templateId, nodeLinkList, valueList);
    			if (nodeLinkList == null
    					|| (nodeLinkList != null && nodeLinkList.isEmpty())) {
    				logger.error("getDefaultNextExecutor method，获得的nodeLinkList为空");
    				return executors;
    			}
      */
      if (nextLinkList == null
        || (nextLinkList != null && nextLinkList.isEmpty())) {
      logger.error("getDefaultNextExecutor method，获得的nodeLinkList为空");
      return executors;
    }
			// 目前工作流模式不支持与/或节点，所以下一节点应该只有1个元素，如果获得多个，以第1个为准
			Link nodeLink = (Link) nextLinkList.get(0);
			action = nodeLink.getActionName();// 确定真正的流向
			String executorDef = wfData.getFieldValue(WFConst.NEXT_EXECUTOR);
			// change by liubo
			if (executorDef != null && !executorDef.equals("")) {
				HashSet temp = new HashSet(Arrays
						.asList(executorDef.split(",")));
				executors.addAll(temp);
			} else {
				executors = getExecutorsByRelation(entityData, wfData,
						entityName, junior, junCoCode, junOrgCode, junPosiCode,
						nd, action, orgPosiCode);
			}
			try {
				Object listener = Instance.getListener(nodeId);
				if (listener != null) {
					executors = (Set) MethodUtils
							.invokeMethod(
									listener,
									"userFilter",
									new Object[] {
											executors,
											wfData
													.getFieldValue(WFConst.PROCESS_INST_ID) });
				}
			} catch (NoSuchMethodException ex) {
			}
		} catch (BusinessException be) {
			throw new WFException(be.toString());
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		} catch (IllegalAccessException ex) {
			throw new WFException(ex.toString());
		} catch (InvocationTargetException ex) {
			throw new WFException(ex.toString());
		}

		return executors;
	}

	public Set getExecutorsByRelation(TableData entityData, TableData wfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, String nd, String action,
			String orgPosiCode) throws WFException, WorkflowException,
			BusinessException {
		Set executors = new HashSet();
		int templateId = Integer.parseInt(wfData.getFieldValue(WFConst.WF_TEMPLATE_ID));
		int instanceId = Integer.parseInt(wfData.getFieldValue(WFConst.PROCESS_INST_ID));

		try {
			if (!StringTools.isEmptyString(orgPosiCode)) {
				OrgPositionModel orgPositionModel = new OrgPositionBean().findByKey(orgPosiCode);
				if (null != orgPositionModel) {
					String strPosiCode = orgPositionModel.getPositionId();
					if (strPosiCode != null) {
						junPosiCode = orgPositionModel.getPositionId();
					}
				}
			}
			Link nodeLink = new Link().getLinkListByAction(templateId, action);
			// 取得下一节点执行者关系
			String executorRelation = nodeLink.getExecutorRelation();
			// 如果下一节点为当前节点执行者自己
			if (executorRelation.equals(Link.EXECUTOR_RELATION_SELF)) {
				executors.add(junior);
			}// end if 自己
			// 为当前节点执行者组织上级
			else if (executorRelation.equals(Link.EXECUTOR_RELATION_MANAGER)) {
				Staff staffHandler = new Staff();
				Set managerSet = staffHandler.getSuperStaffSet(junior,orgPosiCode);
				Object[] managers = managerSet.toArray();
				for (int m = 0; m < managers.length; m++) {
					String manager = (String) managers[m];
					executors.add(manager);
				}
				// DBHelper.closeConnection(con);
			}// end if 上级
			// 通过业务上级算法获得下一节点执行人
			else if (executorRelation
					.equals(Link.EXECUTOR_RELATION_BUSINESS_SUPPERIOR)) {
				// 解析部件页面数据
				// 根据单位代码、机构代码、职位代码和员工，从业务上级表中查询符合的记录
				executors.addAll(BusinessJunior.getSuperiorByPri(entityData,junior, junCoCode, junOrgCode, junPosiCode, nd));

			} else if (executorRelation.equals(Link.EXECUTOR_RELATION_NONE)) {

				executors.addAll(WFUtil.getPreSetExecutor(instanceId, nodeLink.getNextNodeId()));
			}
		} catch (SQLException se) {
			throw new WFException(se.toString());
		} catch (BusinessException be) {
			throw new WFException(be.toString());
		}
		return executors;
	}

	/**
	 * 搜索流程定义
	 * 
	 * @param processId
	 *            流程ID
	 * @param conn
	 *            数据库连接
	 * @return 流程定义，不会为 null
	 * @throws WFException
	 *             没有指定的流程
	 */
	public ProcessDefBean findProcessDef(Object processId) throws WFException {
		ProcessDefBean d = null;
		try {
			TemplateMeta t = ConfigureFacade.getTemplate(Integer.parseInt(
					processId.toString(), 10));
			if (null != t) {
				d = WFUtil.copyProcessDef(t);
			}
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		if (null == d) {
			throw new WFException("Error032432: 查找流程定义出错！");
		}
		WFUtil.findBindVariable(d);
		WFUtil.findBindState(d);
		return d;
	}

	/**
	 * 搜索流程包含的活动
	 * 
	 * @return 元素类型为 ActivityDefBean
	 */
	public List findProcessActivitys(ProcessDefBean process) throws WFException {
		List list = new ArrayList();
		return list; // TCJLODO:0314
	}

	/** 搜索活动定义 */
	public ActivityDefBean findActivityDef(NodeMeta nodeMeta)
			throws WFException {
		ActivityDefBean a = null;
		try {
			if (null != nodeMeta) {
				a = WFUtil.copyActivityDef(nodeMeta);
				// ProcessDefBean p = findProcessDef(a.getTemplateId(), conn);
				WFUtil.findActivityCompo(a);
				WFUtil.findActivityFunc(a);
				WFUtil.findActivityFieldAccess(a);
			}
		} catch (NumberFormatException e) {
			throw new WFException("Error031702: 查找活动定义出错！");
		} catch (SQLException e) {
			throw new WFException(e.toString());
		}
		if (null == a) {
			throw new WFException("Error031402:找不到指定的工作项！");
		}
		return a;
	}

	public ActivityDefBean findActivityDef(Object activityId)
			throws WFException {
		int nActivityId = Integer.parseInt(activityId.toString(), 10);
		try {

			NodeMeta nodeMeta = ConfigureFacade.getNode(nActivityId);
			if (nodeMeta == null || nodeMeta.getId() == 0) {
				throw new WFException("Error031402:找不到指定的工作项！");
			}
			return this.findActivityDef(nodeMeta);
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
	}

	/**
	 * 指定源活动搜索所有的链接
	 * 
	 * @return 元素类型为 LinkDefBean
	 */
	public List findActivityLinksBySrc(ActivityDefBean activity)
			throws WFException {
		List list = new ArrayList();
		LinkDefBean link = new LinkDefBean();
		list.add(link);
		return list;
	}

	/**
	 * 查找下一步可能的活动
	 * <p>
	 * 结合了 findActivityLinksBySrc 和 findActivityDef
	 * 
	 * @return 元素类型为 ActionBean
	 */
	public List getNextActionList(String user, ActivityDefBean activity)
			throws WFException {
		List list = new ArrayList();
		ActionBean action = new ActionBean();
		list.add(action);
		return list; // TCJLODO:0314
	}

	/**
	 * 根据模板id获取所有模板中定义的节点列表
	 */
	public List getNodeList(int templateId) throws WFException {
		List nodeList = null;
		try {
			nodeList = ConfigureFacade.getNodeList(templateId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return nodeList;
	}

	/**
	 * 授权
	 */
	public void impower(int processInstanceId, int activityId,
			String[] svUserIdList, int[] responsibility, String authorizor,
			String comment, TableData bnData) throws WFException {
		try {
			ExecuteFacade.authorizeTask(processInstanceId, activityId,
					svUserIdList, responsibility, authorizor, comment);
			ActivityDefBean activityDef = this.findActivityCompo(
					processInstanceId, activityId);
			rewriteComment(processInstanceId + "", activityId + "", comment,
					bnData, activityDef);
			refreshBrief(processInstanceId + "", activityId + "", bnData,
					activityDef);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	/**
	 * 移交
	 */
	public void handover(int processInstanceId, int activityId, int workitemId,
			String[] svUserIdList, int[] responsibility, String forwarder,
			String comment, TableData bnData) throws WFException {
		try {
			ExecuteFacade.forwardTask(processInstanceId, activityId,
					workitemId, svUserIdList, responsibility, forwarder,
					comment);
			ActivityDefBean activityDef = this.findActivityCompo(
					processInstanceId, activityId);
			rewriteComment(processInstanceId + "", activityId + "", comment,
					bnData, activityDef);
			refreshBrief(processInstanceId + "", activityId + "", bnData,
					activityDef);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	/**
	 * 回退多步
	 */
	public void untread(int processInstanceId, int activityId,
			int prevActivityId, String svUserId, String comment,
			TableData bnData) throws WFException {
		try {
			ExecuteFacade.untreadFlow(processInstanceId, activityId,
					prevActivityId, svUserId, comment);
			syncDataByBindedWFSate(processInstanceId);
			ActivityDefBean activityDef = this.findActivityCompo(
					processInstanceId, activityId);
			rewriteComment(processInstanceId + "", activityId + "", comment,
					bnData, activityDef);
			refreshBrief(processInstanceId + "", activityId + "", bnData,
					activityDef);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	/**
	 * 回退
	 */
	public void withdraw(int processInstanceId, int activityId,
			String svUserId, String comment, TableData bnData)
			throws WFException {
		try {
			ExecuteFacade.givebackFlow(processInstanceId, activityId, svUserId,
					comment);
			ActivityDefBean activityDef = this.findActivityCompo(
					processInstanceId, activityId);
			rewriteComment(processInstanceId + "", activityId + "", comment,
					bnData, activityDef);
			refreshBrief(processInstanceId + "", activityId + "", bnData,
					activityDef);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	/**
	 * 回收
	 */
	public void callBack(int processInstanceId, int activityId,
			String svUserId, String comment, TableData bnData)
			throws WFException {
		try {
			String lastNode = WFUtil.getNodeIdByInstanceId(processInstanceId);
			if("".equals(lastNode)){
				throw new WFException("该流程下级节点为空，可能已被收回!");	
			}
			if (activityId != Integer.parseInt(lastNode)) {
				throw new WFException("该单据在当前状态不能收回!");
			} else {
				ExecuteFacade.callbackFlow(processInstanceId, activityId,
						svUserId, comment);
				syncDataByBindedWFSate(processInstanceId);
				ActivityDefBean activityDef = this.findActivityCompo(
						processInstanceId, activityId);
				rewriteComment(processInstanceId + "", activityId + "",
						comment, bnData, activityDef);
				refreshBrief(processInstanceId + "", activityId + "", bnData,
						activityDef);
			}
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		} catch (SQLException ex) {
			throw new WFException(ex.getMessage());
		}

	}

	public boolean isInstanceFinished(int instanceId) throws WFException {
		try {
			return ExecuteFacade.isInstanceFinished(instanceId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	/**
	 * 根据用户获取代办列表
	 * 
	 * @see com.anyi.erp.workflow.WFService#getTodoListByTemplateType(java.lang.String,
	 *      java.lang.String)
	 */
	public List getTodoListByUser(String userId) throws WFException {
		try {
			return ExecuteFacade.getTodoListByExecutor(userId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	public List getTodoCompoListByUser(String userId) throws WFException {
		try {
			return ExecuteFacade.getTodoCompoListByExecutor(userId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * 根据用户获取代办实例列表
	 * 
	 * @see com.anyi.erp.workflow.WFService#getTodoListByTemplateType(java.lang.String,
	 *      java.lang.String)
	 */
	public List getTodoInstListByUser(String userId) throws WFException {
		try {
			return ExecuteFacade.getTodoInstListByExecutor(userId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	// 取作废流程id(根据用户,包括待办和已办)
	public List getInvalidInstListByUser(String userId) throws WFException {
		List result = new ArrayList();
		try {
			List resultTodo = ExecuteFacade
					.getInvalidTodoInstListByExecutor(userId);
			List resultDone = ExecuteFacade
					.getInvalidDoneInstListByExecutor(userId);
			if (resultTodo != null)
				result.addAll(resultTodo);
			for (int i = 0; resultDone != null && i < resultDone.size(); i++) {
				if (!result.contains((String) resultDone.get(i))) {
					result.add(resultDone.get(i));
				}
			}
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return result;
	}

	/**
	 * 获取所有活动模板的列表
	 * 
	 * @see com.anyi.erp.workflow.WFService#getTemplateByTemplateId(int)
	 */
	public List getAllActiveTemplate() throws WFException {
		try {
			return ExecuteFacade.getActiveTemplateList();
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * 
	 * @see com.anyi.erp.workflow.WFService#getActionDeltaByBusinessType(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Delta getActionDeltaByTemplateAndNode(String compoName,
			int templateId, int nodeId) throws WFException {
		// TCJLODO 需

		return null;
	}

	/**
	 * 获取指定用户已办任务列表
	 * 
	 * @see com.anyi.erp.workflow.WFService#getDoneListByUserAndTemplate(java.lang.String,
	 *      int, java.lang.String, java.lang.String)
	 */
	public List getDoneListByUser(String userId, String startTime,
			String endTime) throws WFException {
		/* 设定默认时间 */
		if (startTime == null) {
			startTime = "0";
		}
		if (endTime == null) {
			endTime = StringTools.getDateString(new Date());
			endTime += "235959";
		}
		if (startTime != null) {
			startTime = StringTools.replaceText(startTime, "-", "");
		}
		if (endTime != null) {
			endTime = StringTools.replaceText(endTime, "-", "");
		}
		try {
			// TCJLODO: 改变返回值元素类型为 WorkitemBean，现在是 ActionMeta
			return ExecuteFacade.getActionListByExecutor(userId, startTime,
					endTime);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * 获取指定用户已办任务实例列表
	 * 
	 * @see com.anyi.erp.workflow.WFService#getDoneListByUserAndTemplate(java.lang.String,
	 *      int, java.lang.String, java.lang.String)
	 */
	public List getDoneInstListByUser(String userId, String startTime,
			String endTime) throws WFException {
		/* 设定默认时间 */
		if (startTime == null) {
			startTime = "0";
		}
		if (endTime == null) {
			endTime = StringTools.getDateString(new Date());
			endTime += "235959";
		}
		if (startTime != null) {
			startTime = StringTools.replaceText(startTime, "-", "");
		}
		if (endTime != null) {
			endTime = StringTools.replaceText(endTime, "-", "");
		}
		try {
			return ExecuteFacade.getActionInstListByExecutor(userId, startTime,
					endTime);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * 获取指定用户已办任务列表
	 * 
	 * @see com.anyi.erp.workflow.WFService#getDoneListByUserAndTemplate(java.lang.String,
	 *      int, java.lang.String, java.lang.String)
	 */
	public List getDoneCompoListByUser(String userId) throws WFException {
		try {
			// TCJLODO: 改变返回值元素类型为 WorkitemBean，现在是 ActionMeta
			return ExecuteFacade.getActionCompoListByExecutor(userId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * 根据工作流实例获取部件id
	 * 
	 * @param instanceId
	 *            工作流实例id
	 * @return 工作流对应的部件id，
	 * @throws
	 * @see
	 */
	public List getCompoIdByInstanceId(int instanceId) throws WFException {
		List compoIdList = new ArrayList();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			// 获取流程模板id
			InstanceMeta instanceMeta = ExecuteFacade.getInstance(instanceId);
			// 获取部件名
			String sql = "select distinct COMPO_ID from AS_WF_ACTIVITY_COMPO where "
					+ "WF_TEMPLATE_ID =?";
			conn = DAOFactory.getInstance().getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, instanceMeta.getTemplateId());
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				compoIdList.add(rs.getString("compo_id"));
			}
		} catch (Exception e) {
			logger.error("工作流异常！" + e.getMessage());
			throw new WFException("工作流异常！" + e.getMessage());
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return compoIdList;
	}

	/**
	 * 根据工作流实例获取部件id
	 * 
	 * @param instanceId
	 *            工作流实例id
	 * @param nodeId
	 *            工作流环节id
	 * @return 工作流对应的部件id，
	 * @throws
	 * @see
	 */
	public String getCompoIdByNodeId(int instanceId, int nodeId)
			throws WFException {
		String result = null;
		try {
			// 获取流程模板id
			InstanceMeta instanceMeta = ExecuteFacade.getInstance(instanceId);
			// 获取部件名
			String sql = "select distinct COMPO_ID from AS_WF_ACTIVITY_COMPO where "
					+ "WF_TEMPLATE_ID =? and WF_NODE_ID= ?";
			Integer[] params = new Integer[2];
			params[0] = new Integer(instanceMeta.getTemplateId());
			params[1] = new Integer(nodeId);
			result = (String) DBHelper.queryOneValue(sql, params);

		} catch (Exception e) {
			logger.error("工作流异常！" + e.getMessage());
			throw new WFException("工作流异常！" + e.getMessage());
		}

		return result;
	}

	/**
	 * 返回部件可以启动的模板列表
	 * 
	 * @compoName 部件名，必须是工作流支持的部件，或者工作流部件
	 * @throws WFException
	 * @return 返回部件可以启动的模板列表 返回类型是List element为Integer，值为模板id值。
	 * @throws WorkflowException
	 * @throws NumberFormatException
	 */
	public List getCompoEnableStartedTemplate(String compoName)
			throws WFException {
		try {
			// /业务部件是否支持工作流
			if (compoName.equalsIgnoreCase(WFCompoType.WF_TODO)
					|| compoName.equalsIgnoreCase(WFCompoType.WF_DONE)) {
				return getAllActiveTemplate();
			}
			// 不是工作流部件，说明是工作流支持的业务部件
			// 所有的该部件挂接的流程
			List result;
			String sql = "select ac.wf_template_id from as_wf_activity_compo ac ,"
					+ "wf_link l,wf_template t"
					+ " where ac.compo_id = '"
					+ compoName
					+ "' and ac.wf_template_id=t.template_id "
					+ " and ac.wf_node_id=l.next_node_id  and"
					+ " l.current_node_id=-1 and t.is_active='0'";
			result = DBHelper.queryToList(sql, null);
			return result;
		} catch (Exception e) {
			logger.error("获取部件能够启动的工作流模板失败！");
			throw new WFException("获取部件能够启动的工作流模板失败！");
		}
	}

	/**
	 * 根据工作流实例获取业务表数据。 这里有个情况:参数当中compoIds是个部件名称列表,但实际上一个实例最多只可能对应一个部件.
	 * 这里流程实例实际上会起到过滤作用,把流程实例对应的业务数据找出来.并返回一个包含compId,和业务数据tableData的Map Todo
	 * remove
	 * 
	 * @param instanceId
	 *            工作流实例id
	 * @return 返回业务数据TableData,如果没有业务数据就返回空.
	 * @throws
	 * @see
	 */
	public TableData getCompoDataByCurTask(int processInstId,
			ActivityDefBean activityDef) throws WFException {
		TableData result = null;
		try {
			String compoId = activityDef.getCompoName();
			String tableName = MetaManager.getCompoMeta(compoId)
					.getMasterTable();
			// 目前只求主表数据
			Map params = new HashMap();
			params.put(WFConst.PROCESS_INST_ID_FIELD, String
					.valueOf(processInstId));
			Datum datum = ((ServiceFacade) ApplusContext
					.getBean("serviceFacade")).getEntityWithDatum(tableName,
					params);
			List datas = datum.getData();
			if (datas.size() == 0) {
				throw new WFException("无业务数据!");
			} else {
				Map data = (Map) datas.get(0);
				result = new TableData(data);
			}
		} catch (Exception be) {
			throw new WFException(be.getMessage());
		}
		return result;
	}

	/**
	 * 根据模板id获取TemplateMeta
	 * 
	 * @see com.anyi.erp.workflow.WFService#getTemplateMetaByTemplateId()
	 */
	public TemplateMeta getTemplateMetaByTemplateId(int templateId) {
		Template template = new Template();
		try {
			return template.getTemplate(templateId);
		} catch (WorkflowException e) {
			logger.error("获取工作流模板时出错！");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anyi.erp.workflow.WFService#getActionSetByTemplateAndNode(int,
	 *      int)
	 */
	public Set getActionSetByTemplateAndNode(int templateId, int nodeId)
			throws WFException {

		return null;
	}

	/*
	 * @see com.anyi.erp.workflow.WFService#findCompoByTask(com.kingdrive.workflow.dto.CurrentTaskMeta)
	 */
	public String findCompoByTask(int templateId, int nodeId) {
		ActivityDefBean activityDef = new ActivityDefBean();
		activityDef.setActivityId(new Integer(nodeId));
		activityDef.setTemplateId(new Integer(templateId));
		try {
			WFUtil.findActivityCompo(activityDef);
		} catch (WFException e) {
			logger.error("根据当前任务获取部件名称,时出现异常.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error("根据当前任务获取部件名称,时出现异常.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return activityDef.getCompoName();
	}

	/**
	 * 汇总提交
	 * 
	 * @param entityName
	 *            实体名（部件名）
	 * @param wfData
	 *            工作流相关数据，包括工作项、下一步的活动及执行者等
	 * @param detailTaskId
	 *            需要汇总的子任务id列表
	 * @return 当前工作项，包含执行人、流程变量等
	 * @throws BusinessException
	 */
	public TableData collectCommit(TableData wfData, String entityName,
			List lstDetailTaskId, TableData bnData) throws WFException {
		TableData result = new TableData();
		ActionBean action = new ActionBean(wfData);
		/* 汇总明细任务 */
		Object workitemId = wfData.getField(WFConst.WORKITEM_ID);
		Iterator iter = lstDetailTaskId.iterator();
		while (iter.hasNext()) {
			Integer element = (Integer) iter.next();
			WFUtil.collectCurrentTask(element.intValue(), Integer
					.parseInt(workitemId.toString()));
			// 为子任务创建下一步执行人
			WorkitemBean childWorkitem = findWorkitem(element, 1);
			try {
				// 汇总提交可以忽略工作流变量的计算
				WFUtil.createNextExecutor(childWorkitem, action,
						new ArrayList());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new WFException("创建下一步执行人出错");
			} catch (WorkflowException e) {
				e.printStackTrace();
				throw new WFException("创建下一步执行人出错");
			}
		}

		/* 提交汇总任务 */
		WorkitemBean item = findWorkitem(workitemId, 1);
		/* 设置工作流名称 */
		String instanceName = null;
		if (wfData.getField(WFConst.INSTANCE_NAME) != null) {
			instanceName = wfData.getField(WFConst.INSTANCE_NAME).toString();
		}
		if (instanceName != null
				&& !instanceName.equals(WFConst.MSG_TOCOLLECT_TASK)) {
			item.setInstanceName(instanceName);
		} else {
			item.setInstanceName(WFConst.MSG_COLLECTED_TASK);
		}

		String user = wfData.getFieldValue(WFConst.WF_CURRENT_EXECUTOR_ID);

		// 汇总提交可以忽略工作流变量的计算
		List lstWFVariable = new ArrayList();
		item = completeWorkitem(user, item, action, lstWFVariable, bnData);
		item.saveToTableData(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anyi.erp.workflow.WFService#getWorkItemByInstance(java.lang.String,
	 *      java.lang.Object)
	 */
	public WorkitemBean getWorkItemByInstance(String user, Object processId,
			int isValid) throws WFException {
		WorkitemBean result = null;
		try {
			List lstWorkitemBean = getTodoListByProcessInst(user, processId,
					isValid);
			if (lstWorkitemBean.size() == 0)
				return null;
			else
				// 只取一个
				result = (WorkitemBean) lstWorkitemBean.get(0);

		} catch (WFException e) {
			e.printStackTrace();
			throw new WFException("获取工作项时出错!");
		}
		return result;
	}

	public CurrentTaskMeta getWorkItemByInstance(int processId, int isValid)
			throws WFException {
		CurrentTaskMeta result = null;
		try {
			List lstWorkitemBean = getTodoListByProcessInstanceId(processId,
					isValid);
			if (lstWorkitemBean.size() == 0)
				return null;
			else
				result = (CurrentTaskMeta) lstWorkitemBean.get(0);
		} catch (WFException ex) {
			throw new WFException("获取工作项时出错!");
		}
		return result;
	}

	public void syncDataByBindedWFSate(int instanceId) throws WFException {
		int templateId = 0;
		try {
			templateId = ExecuteFacade.getInstance(instanceId).getTemplateId();
			syncDataByBindedWFSate(instanceId, templateId);
		} catch (WorkflowException e) {
			throw new WFException(e.getMessage());
		}
	}

	/**
	 * 设置绑定了工作流状态变量的业务字段值。即把工作流状态值同步到绑定的业务字段上面 目前只考虑把状态设置到主表字段的情况
	 * 
	 * @param instanceId
	 * @param lstBindStateInfo
	 * @param conn
	 * @throws WFException
	 */
	public void syncDataByBindedWFSate(int instanceId, int templateId)
			throws WFException {
		String updateSql;
		ProcessDefBean pdf = findProcessDef("" + templateId);
		List lstBindStateInfo = pdf.getBindStateInfo();
		// 获取状态值
		List stateList;
		try {
			stateList = ExecuteFacade.getStateListByInstance(instanceId);
			Iterator iterAllInstanceState = stateList.iterator();
			while (iterAllInstanceState.hasNext()) {
				StateValueMeta elemStateValueMeta = (StateValueMeta) iterAllInstanceState
						.next();
				Iterator iterBindedState = lstBindStateInfo.iterator();
				while (iterBindedState.hasNext()) {
					BindStateInfo elemStateInfo = (BindStateInfo) iterBindedState
							.next();

					if (elemStateValueMeta.getStateId() == elemStateInfo
							.getState_id()) {
						// 设置状态值到业务数据中
						updateSql = "update ";
						updateSql += elemStateInfo.getTabName().toUpperCase();
						updateSql += " set ";
						updateSql += elemStateInfo.getFieldName();
						// 因为目前工作流状态只有整型和字符串类型,所以都可以按照字符串进行插入.
						updateSql += "=?";
						updateSql += " where PROCESS_INST_ID=?";

						try {
							Object[] params = { elemStateValueMeta.getValue(),
									new Integer(instanceId) };
							DBHelper.executeUpdate(updateSql, params);
						} catch (Exception sex) {
							throw new WFException(sex.getMessage());
						}
					}
				}
			}

		} catch (WorkflowException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param parentInstanceId
	 * @param conn
	 * @return
	 * @throws WFException
	 */
	public List getChildTodoListByParentInstance(int parentInstanceId)
			throws WFException {
		List result = new ArrayList();
		try {
			result = ExecuteFacade
					.getChildInstanceByParentInstance(parentInstanceId);
		} catch (WorkflowException e) {
			e.printStackTrace();
			throw new WFException("根据父流程获取子流程时出错！");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anyi.erp.workflow.WFService#syncCollectedFlowDataByBindedWFSate(int,
	 *      java.util.List, java.sql.Connection)
	 */
	public void syncCollectedFlowDataByBindedWFSate(int nParentInstanceId,
			int parentTemplateId) throws WFException {
		List lstChildInstanceInfo = getChildTodoListByParentInstance(nParentInstanceId);
		Iterator iter = lstChildInstanceInfo.iterator();
		while (iter.hasNext()) {
			InstanceMeta element = (InstanceMeta) iter.next();
			syncDataByBindedWFSate(element.getInstanceId(), parentTemplateId);
		}
	}

	public List getWFVariableValueList(String entityName, TableData entity,
			TableData wfData) throws WFException {
		List result = WFUtil.initWFVariableValueByWFData(wfData);
		ProcessDefBean pdf = findProcessDef(wfData.getField(WFConst.WF_TEMPLATE_ID));
		Object[] masterTab_KeyFieldCondition = WFUtil.getMasterTabKeyFieldCondition(entity, entityName);// TCJLODO:liubo
		result = WFUtil.setBindWFVariableValue(result, pdf.getBindVariableInfo(),entityName, masterTab_KeyFieldCondition);
		return result;
	}

	public List getDoneListByUserAndInstance(String userId, int nInstanceId,
			int isValid) throws WFException {
		List result = new ArrayList();
		try {
			result = ExecuteFacade.getActionListByExecutorAndInstance(userId,
					nInstanceId, isValid);
		} catch (NumberFormatException e) {
			throw new WFException(e.toString());
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
		return result;
	}

	public int getInstanceStatus(String instanceId) {
		try {
			InstanceMeta inst = ExecuteFacade.getInstance(Integer
					.parseInt(instanceId));
			return inst.getStatus();
		} catch (NumberFormatException e) {
			// TCJLODO Auto-generated catch block
			e.printStackTrace();
			logger
					.error("WFService.getInstanceStatus(String instanceId)调用的时候参数不正确.参数值为:"
							+ instanceId);
			throw new RuntimeException(e);
		} catch (WorkflowException e) {
			// TCJLODO Auto-generated catch block
			logger
					.error("WFService.getInstanceStatus(String instanceId)调用的时候抛出工作流异常");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * 把当前意见写到当前节点的意见字段中。 区别于以前每次都把所有累加意见重写一次的做法， 解决可能流程中有不同意见字段的问题。
	 */
	public void rewriteComment(String instanceId, String nodeId,
			String comment, TableData bnDadta, ActivityDefBean activityDef)
			throws WFException {
		int instId = Integer.parseInt(instanceId);
		int nodeId2 = -1;
		if (nodeId != null) {
			nodeId2 = Integer.parseInt(nodeId);
		}
		try {
			// ActivityDefBean activityDef = findActivityDefBean(instId,
			// nodeId2);
			List workitems;
			workitems = getProcessHistory(instanceId, 1);
			WorkitemBean b = null;
			for (Iterator i = workitems.iterator(); i.hasNext();) {
				WorkitemBean b1 = (WorkitemBean) i.next();
				if (b1.getActivityId().toString().equals(nodeId)) {
					b = b1;// is the last action at the node
				}
			}
			StringBuffer commentBuff = new StringBuffer();
			boolean exist = false;
			if (b != null && !StringTools.isEmptyString(comment)) {
				commentBuff.append(b.getExecutorName().trim());
				commentBuff.append("(" + b.getActivityName() + ")");
				commentBuff.append(StringTools.toTimeString(b.getExecuteTime()
						+ "")
						+ ":");
				commentBuff.append(comment);
				exist = true;
			}
			String data = WFUtil.getBriefContent(instId, nodeId2, false,
					bnDadta, activityDef);
			if (b != null && !StringTools.isEmptyString(data)) {
				if (commentBuff.length() == 0) {
					commentBuff.append(b.getExecutorName().trim());
					commentBuff.append("(" + b.getActivityName() + ")");
					commentBuff.append(StringTools.toTimeString(b
							.getExecuteTime()
							+ "")
							+ ":");
				}
				commentBuff.append("[历史数据：");
				commentBuff.append(data);
				commentBuff.append("]");
				exist = true;
			}
			if (exist) {
				commentBuff.append("\r\n");
			}
			String commentFieldName = activityDef.getCommentFieldName();
			String tabId = MetaManager.getTableMetaByCompoName(
					activityDef.getCompoName()).getName();
			if (!StringTools.isEmptyString(commentFieldName) && exist) {
				String sql = "update " + tabId + " set " + commentFieldName
						+ "=? where process_inst_id=?";
				String[] para = { commentBuff.toString(), instanceId };
				DBHelper.executeUpdate(sql, para);
			}
		} catch (SQLException se) {
			throw new WFException(se.getMessage());
		} catch (BusinessException be) {
			throw new WFException(be.getMessage());
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	/**
	 * 获取工作流节点挂接信息
	 * 
	 * @param editPageType
	 * @param wfData
	 * @return
	 * @throws WorkflowException
	 * @throws NumberFormatException
	 * @throws WorkflowException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	public ActivityDefBean findActivityDefBean(int instanceId, int nNodeId)
			throws NumberFormatException, WFException {
		ActivityDefBean activityDef = null;
		try {
			int nTemplateId = ExecuteFacade.getInstance(instanceId)
					.getTemplateId();
			if (-1 == nNodeId) {
				NodeMeta node = ConfigureFacade.getStartNode(nTemplateId);
				activityDef = findActivityDef(node);
			} else {
				activityDef = findActivityDef("" + nNodeId);
			}
		} catch (WorkflowException ex) {
			throw new WFException(ex.getMessage());
		}
		return activityDef;
	}

	/**
	 * 从工作流直接读取意见内容，并设置到db中 并且内容需要累加所有完成步骤地每个意见
	 * 
	 * @param data
	 * @param wfData
	 * @param activityDef
	 * @throws WFException
	 */
	public void refreshBrief(String instanceId, String nodeId,
			TableData bnData, ActivityDefBean activityDef) throws WFException {
		int instId = Integer.parseInt(instanceId);
		int nodeId2 = -1;
		if (nodeId != null) {
			nodeId2 = Integer.parseInt(nodeId);
		}
		try {
			InstanceMeta instMeta = ExecuteFacade.getInstance(instId);
			// instMeta.setInstanceId(Integer.parseInt(instanceId));
			String brief = WFUtil.getBriefContent(instId, nodeId2, true,
					bnData, activityDef);
			if (brief != null) {
				if (brief.length() > 200) {// length retrict
					brief = brief.substring(0, 200);
				}
				instMeta.setDescription(brief);
			}
			new Instance().update(instMeta);
		} catch (SQLException se) {
			throw new WFException(se.getMessage());
		} catch (BusinessException be) {
			throw new WFException(be.getMessage());
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.getMessage());
		}
	}

	public String getWfdataByProcessInstId(String user, String templateId,
			String instanceId, HttpServletRequest request)
			throws BusinessException {
		String res = "";
		TableData wfData = new TableData();
		try {
			int nInstanceId = Integer.parseInt(instanceId);

			int tId = Integer.parseInt(templateId);
			if (tId != 0) {
				wfData
						.setField(WFConst.WF_TEMPLATE_ID, new Integer(
								templateId));
			}
			wfData.setField(WFConst.WF_INSTANCE_ID, "" + nInstanceId);
			// 获取CurrentTaskId,即workitemId
			// 只选有效的任务
			user = URLDecoder.decode(user, "UTF-8");
			WorkitemBean workitem = getWorkItemByInstance(user, ""
					+ nInstanceId, 1);

			if (workitem == null) {
				// 有可能任务已经结束了
				// if(wfs.isInstanceFinished(nInstanceId,conn)){
				// List actionMeta = new ArrayList();
				List actionMeta = this.getDoneListByUserAndInstance(user,
						nInstanceId, 1);
				if (actionMeta == null || actionMeta.size() == 0) {
					actionMeta = getDoneListByProcessInstanceId(nInstanceId, 0);
				}

				// 取最后一个Action,取得最后运行的环节id
				if (actionMeta.size() < 1) {
					throw new BusinessException("工作流内部有脏数据,请使用工作流监控清理脏数据!");
				} else {
					ActionMeta lastExecutedActionMeta = (ActionMeta) actionMeta
							.get(actionMeta.size() - 1);
					wfData.setField(WFConst.WF_NODE_ID, ""
							+ lastExecutedActionMeta.getNodeId());
					wfData.setField(WFConst.WF_TEMPLATE_ID, ""
							+ lastExecutedActionMeta.getTemplateId());
					wfData.setField(WFConst.WF_INSTANCE_NAME, ""
							+ lastExecutedActionMeta.getInstanceName());

					InstanceMeta instance = ExecuteFacade
							.getInstance(nInstanceId);
					wfData.setField(WFConst.WF_INSTANCE_STATUS, String
							.valueOf(instance.getStatus()));
					wfData.setField(WFConst.WF_INSTANCE_NAME, String
							.valueOf(instance.getName()));
					wfData.setField(WFConst.WF_INSTANCE_NAME, String
							.valueOf(instance.getName()));
				}
				// }

			} else {
				// WFConst.WF_NODE_ID
				// 可能是无效的任务
				if (workitem != null) {
					wfData.setField(WFConst.WF_TEMPLATE_ID, workitem
							.getTemplateId());
					wfData.setField(WFConst.WF_NODE_ID, workitem
							.getActivityId());
					wfData.setField(WFConst.WF_TASK_ID, workitem
							.getWorkitemId());
				}
			}// end workItem_Id
			// res = wfRelatedDataToString4(wfData,request);
			// Page page = new Page();
			// page.setRequest(request);
			// page.setWfData(wfData);
			// res = page.wfRelatedDataToString4();
			res = WFUtil.wfRelatedDataToString4(request, wfData);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (WorkflowException e) {
			logger.error(e);
		} catch (Exception e) {
			throw new BusinessException("无法从工作流引擎获取流程实例");
		}
		return res;
	}

	/**
	 * 手动提交
	 */
	public TableData commit(TableData entity, String entityName,
			TableData wfData) throws BusinessException {
		// ; 1. 查找工作项定义 2. 提交工作流 3. 将业务数据写到业务数据表中
		TableData result = new TableData();
		try {
			String user = wfData.getFieldValue(WFConst.WF_CURRENT_EXECUTOR_ID);
			CompoMeta entityMeta = MetaManager.getCompoMeta(entityName);

			/* 处理工作流变量 */
			List lsWFVariable = WFUtil.getWFVariableFromPage(wfData);
			ProcessDefBean pdf = findProcessDef(wfData
					.getField(WFConst.WF_TEMPLATE_ID));
			Object[] masterTab_KeyFieldCondition = WFUtil
					.getMasterTabKeyFieldCondition(entity, entityName);
			WFUtil.setBindWFVariableValue(lsWFVariable, pdf
					.getBindVariableInfo(), entityMeta.getName(),
					masterTab_KeyFieldCondition);

			// 2. 提交工作流
			WorkitemBean item = findWorkitem(wfData
					.getField(WFConst.WORKITEM_ID), 1);
			String titleField = entityMeta.getTitleField();
			if (titleField == null || titleField.length() == 0) {
				titleField = "TITLE";
			}
			String title = entity.getFieldValue(titleField);
			if (title != null && title.length() > 0) {
				item.setInstanceName(title);
			}
			ActionBean action = new ActionBean(wfData);
			item = completeWorkitem(user, item, action, lsWFVariable, entity);
			item.saveToTableData(result);

			// 4. 同步状态
			syncDataByBindedWFSate(Integer.parseInt(wfData.getField(
					WFConst.PROCESS_INST_ID).toString()), Integer
					.parseInt(wfData.getField(WFConst.WF_TEMPLATE_ID)
							.toString()));
		} catch (Exception e) {
			throw new BusinessException(e.toString());
		}
		return result;
	}

	// 以下方法目前主要是为满足OA的需要而添加的 _ start
	/**
	 * 追加下一步执行人
	 * 
	 * @param strInstanceId
	 * @param nodeId
	 * @param strUserId
	 * @param strWfData
	 * @return
	 * @throws WFException
	 */
	public String appendExecutor(String strInstanceId, String strTemplateId,
			String strNodeId, String strCompoId, String strUserId,
			TableData wfData, String direction) throws WFException {
		String resStr = "failed";
		TableData tableData = null;
		List valueList = null;

		int instanceId = Integer.parseInt(strInstanceId);
		int templateId = Integer.parseInt(strTemplateId);
		int nodeId = Integer.parseInt(strNodeId);

		try {
			wfData.setField(WFConst.WF_TEMPLATE_ID, strTemplateId);
			wfData.setField(WFConst.PROCESS_INST_ID, strInstanceId);
			wfData.setField(WFConst.WF_ACTIVITY_ID, strNodeId);
			// 取业务数据
			tableData = WFUtil
					.getBusinessDataByInsId(strCompoId, strInstanceId);
			if (null == tableData) {
				throw new WFException("找不到业务数据");
			}
			// 参数中没有变量信息，而且该模板中有变量
			if ((null == valueList || (null != valueList && valueList.size() == 0))) {
				valueList = getWFVariableValueList(strCompoId, tableData,
						wfData);
			}
			String action = wfData.getFieldValue(WFConst.ACTION_NAME);
			if ("".equals(action)) {// 如果没有该参数，就改为null
				action = null;
			}
			int nextNodeId = Integer.parseInt(strNodeId);
			if (direction.equals("0")) {
				List nodeLinkList = new Link().getFollowedLinkList(templateId,
						nodeId, action);
				nodeLinkList = new Instance().getRightFollowedTaskLinkList(
						instanceId, templateId, nodeLinkList, valueList);
				if (nodeLinkList == null
						|| (nodeLinkList != null && nodeLinkList.isEmpty())) {
					logger.error("获得的nodeLinkList为空");
					throw new WFException("获得的nodeLinkList为空");
				}
				Link nodeLink = (Link) nodeLinkList.get(0);
				nextNodeId = nodeLink.getNextNodeId();
			}
			String[] res = WFUtil.instanceHasCommited(instanceId, nextNodeId);
			int count = Integer.parseInt(res[0]);
			if (count == 0) {
				throw new WFException("已经提交，不能追加执行人!");
			}
			String creator = res[1];
			ExecuteFacade.appendExecutor(instanceId, nextNodeId, creator,
					wfData.getFieldValue(WFConst.NEXT_EXECUTOR));
			resStr = "success";
		} catch (BusinessException bex) {
			resStr = bex.getMessage();
			throw new WFException(resStr);
		} catch (WorkflowException ex) {
			resStr = ex.getMessage();
			throw new WFException(ex.getMessage());
		}
		return resStr;
	}

	public String removeExecutor(String strInstanceId, String strNodeId,
			String strUserId, String comment) throws WFException {
		String result = "failed";
		int instanceId = Integer.parseInt(strInstanceId);
		int nodeId = Integer.parseInt(strNodeId);
		try {
			String[] res = WFUtil.instanceHasCommited(instanceId, nodeId);
			int count = Integer.parseInt(res[0]);
			if (count > 1) {
				ExecuteFacade.removeExecutor(instanceId, nodeId, strUserId);
				ActivityDefBean activityDef = this.findActivityDefBean(
						instanceId, nodeId);
				rewriteComment(strInstanceId, strNodeId + "", comment, null,
						activityDef);
				result = "success";
			} else {
				result = untreadSimply(strInstanceId, strUserId, comment, null);
			}
		} catch (WorkflowException ex) {
			result = ex.getMessage();
			throw new WFException(ex.getMessage());
		}
		return result;
	}

	/**
	 * 获取默认的执行人
	 * 
	 * @param templateId
	 * @param nodeId
	 * @param action
	 * @param nd
	 * @return
	 * @throws WFException
	 */
	public String getNodeExecutorBySource(String templateId, String nodeId,
			String action, String nd) throws WFException {
		String result = "";
		String sql = "select s.executor, u.user_name from wf_executor_source s,as_user u where s.executor=u.user_id and s.node_id=? and extnd=? and s.source=5";
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		int nextNodeId = 0;
		try {
			conn = DAOFactory.getInstance().getConnection();
			Link linkHandle = new Link();
			List links = linkHandle.getFollowedLinkList(Integer.parseInt(templateId), Integer.parseInt(nodeId), action);
			Link link = (Link) links.get(0);
			if (link != null) {
				nextNodeId = link.getNextNodeId();
				sta = conn.prepareStatement(sql);
				sta.setInt(1, nextNodeId);
				sta.setInt(2, Integer.parseInt(nd));
				res = sta.executeQuery();
				while (res.next()) {
					String temp = "{";
					temp += "id:'" + res.getString(1) + "',";
					temp += "name:'" + res.getString(2) + "'},";
					result += temp;
				}
				if (result.length() > 1) {
					result = result.substring(0, result.length() - 1);
				}
			}
		} catch (SQLException ex) {
			throw new WFException(ex.getMessage());
		} catch (WorkflowException ex) {
			throw new WFException(ex.getMessage());
		} finally {
			DBHelper.closeConnection(conn, sta, res);
		}
		result = "[" + result + "]";
		return result;
	}

	/**
	 * 获取运行时的执行人
	 * 
	 * @param strTemplateId
	 * @param strInstanceId
	 * @param strNodeId
	 * @param action
	 * @return
	 * @throws WFException
	 */
	public String getRuntimeExecutor(String strTemplateId,
			String strInstanceId, String strNodeId, String action)
			throws WFException {
		String result = "";
		int templateId = Integer.parseInt(strTemplateId);
		int instanceId = Integer.parseInt(strInstanceId);
		int nodeId = Integer.parseInt(strNodeId);

		String sql = "select u.user_id,u.user_name from wf_task_executor e, as_user u where e.executor=u.user_id and e.instance_id=? and e.node_id=?";
		Connection conn = null;
		PreparedStatement sta = null;
		ResultSet res = null;
		try {
			conn = DAOFactory.getInstance().getConnection();
			if (action != null && !action.equals("")) {
				List nodeLinkList = new Link().getFollowedLinkList(templateId,nodeId, action);
				Link link = (Link) nodeLinkList.get(0);
				nodeId = link.getNextNodeId();
			}
			sta = conn.prepareStatement(sql);
			sta.setInt(1, instanceId);
			sta.setInt(2, nodeId);
			res = sta.executeQuery();
			while (res.next()) {
				String temp = "{";
				temp += "id:'" + res.getString(1) + "',";
				temp += "name:'" + res.getString(2) + "'},";
				result += temp;
			}
			if (result.length() > 1) {
				result = result.substring(0, result.length() - 1);
			}
		} catch (SQLException ex) {
			throw new WFException(ex.getMessage());
		} catch (WorkflowException ex) {
			throw new WFException(ex.getMessage());
		} finally {
			DBHelper.closeConnection(conn, sta, res);
		}
		result = "[" + result + "]";
		return result;
	}

	/**
	 * 删除下一结点的执行人
	 * 
	 * @param instanceId
	 * @param userId
	 * @param wfData
	 * @param comment
	 * @return
	 * @throws WFException
	 */
	public String removeNextNodeExecutor(String instanceId, String userId,
			TableData wfData, String comment) throws WFException {
		String result = "";
		Connection conn = null;
		String templateId = wfData.getFieldValue(WFConst.WF_TEMPLATE_ID);
		String nodeId = wfData.getFieldValue(WFConst.WF_NODE_ID);
		String action = wfData.getFieldValue(WFConst.ACTION_NAME);
		String executor = wfData.getFieldValue(WFConst.WF_NEXT_EXECUTOR_ID);
		String[] exeArray = executor.split(",");
		try {
			conn = DAOFactory.getInstance().getConnection();
			List nodeLinkList = new Link().getFollowedLinkList(Integer
					.parseInt(templateId), Integer.parseInt(nodeId), action);
			Link link = (Link) nodeLinkList.get(0);
			int nextNodeId = link.getNextNodeId();
			String[] res = WFUtil.instanceHasCommited(Integer
					.parseInt(instanceId), nextNodeId);
			int count = Integer.parseInt(res[0]);
			if (count == 0) {
				result = "流程在下一节点已经通过，不能删除执行人!";
			} else if (exeArray.length < count) {
				ExecuteFacade.removeExecutors(userId, Integer
						.parseInt(instanceId), nextNodeId, exeArray);
				result = "success";
			} else {
				result = untreadSimply(instanceId, userId, comment, null);
			}
		} catch (WorkflowException ex) {
			result = ex.getMessage();
			throw new WFException(ex.getMessage());
		} finally {
			DBHelper.closeConnection(conn);
		}
		return result;
	}

	public String removeNodeExecutor(String instanceId, String userId,
			TableData wfData, String comment) throws WFException {
		String result = "";
		Connection conn = null;
		String templateId = wfData.getFieldValue(WFConst.WF_TEMPLATE_ID);

		String nodeId = wfData.getFieldValue(WFConst.WF_NODE_ID);
		String action = wfData.getFieldValue(WFConst.ACTION_NAME);
		String executor = wfData.getFieldValue(WFConst.WF_NEXT_EXECUTOR_ID);
		String[] exeArray = executor.split(",");
		try {
			conn = DAOFactory.getInstance().getConnection();
			String[] res = WFUtil.instanceHasCommited(Integer
					.parseInt(instanceId), Integer.parseInt(nodeId));
			int count = Integer.parseInt(res[0]);
			if (count == 0) {
				result = "流程在此节点已经通过，不能删除执行人!";
			} else if (exeArray.length < count) {
				ExecuteFacade.removeExecutors(userId, Integer
						.parseInt(instanceId), Integer.parseInt(nodeId),
						exeArray);
				result = "success";
			} else {
				result = untreadSimply(instanceId, userId, comment, null);
			}
		} catch (WorkflowException ex) {
			result = ex.getMessage();
			throw new WFException(ex.getMessage());
		} finally {
			DBHelper.closeConnection(conn);
		}
		return result;
	}

	// 一下方法目前主要是为满足OA的需要而添加的 _ end

	private ActivityDefBean findActivityCompo(int instanceId, int nNodeId)
			throws WFException {
		ActivityDefBean a = null;
		NodeMeta nodeMeta = null;
		try {
			int nTemplateId = ExecuteFacade.getInstance(instanceId)
					.getTemplateId();
			if (-1 == nNodeId) {
				nodeMeta = ConfigureFacade.getStartNode(nTemplateId);
			} else {
				nodeMeta = ConfigureFacade.getNode(nNodeId);
			}
			a = WFUtil.copyActivityDef(nodeMeta);
			WFUtil.findActivityCompo(a);
		} catch (WorkflowException ex) {
			throw new WFException(ex.getMessage());
		} catch (SQLException e) {
			throw new WFException(e.getMessage());
		}
		return a;
	}
	
  private void exeExecListenerCallback(TaskListener listener, CurrentTaskMeta task,
    int position) throws WFException {
    try {
      if (listener != null) {
        if (task != null) {
          if (position == 0) {
            listener.beforeExecution(task);
          }
          if (position == 1) {
            listener.afterExecution(task);
          }
        }
      }
    } catch (Exception ex) {
      throw new WFException(ex.getMessage());
    }
  }

}
