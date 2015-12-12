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
 * �����������ӿ��� ��������˵��: �˴�ActivityId��ͬ��nodeid , processid��ͬ��templateid
 */
public class CZWFService implements WFService {
	// log4j for log
	final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(CZWFService.class);

	/**
	 * �ύ������
	 * <p>
	 * ע��: һ���ύ�� workitem �Ͳ������ˣ����ص� WorkitemBean ûʲô����
	 * 
	 * @param user
	 *            ִ�в������û�
	 * @param workitem
	 *            Ҫ�ύ�Ĺ�����
	 * @param action
	 *            ִ�еĲ���
	 * @param parameters
	 *            �ύ���������Ĳ�����Ԫ������Ϊ VariableInfo�� �� action �� parameters �ֿ����ǿ��ǵ�
	 *            action ���õ���һЩ����Ĳ����� ��Ҫ�����˹�ָ����һ���Ļ��ִ���ߣ��� parameters ��Ҫ�����ɹ�������
	 *            ���ݲ����Զ��ж���һ���Ļ��
	 * @param conn
	 *            ���ݿ�����
	 * @return �ύ��Ĺ�������󣬻״̬Ϊ"�����"
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
			// 1. ���ָ����ִ���ߣ�������һ���ڵ�Ķ�ִ̬����
			// �˴�����ִ���ˣ��Ƕ������ִ���˵Ĳ���
			// �����У�ֻ�ܶ���ʱָ��ִ���ˣ����˴�����������ʱָ��ִ���ˣ�������Ĳ���
			// TCJLODO����һ�������⡣�õ���Listֻ�Ǹ���ģ��ó��ģ�δ���Ǳ���ֵ������
			// ���ܰѲ���ִ�еĽڵ��ִ����Ҳ�����˳��������޸ġ�
      
      List nextLinkList = WFUtil.getRightFollowedTaskLinkList(processInstId, tpId, activityId, action.getActionId(), variableValueList);// 2����ѯ
			WFUtil.createNextExecutor(workitem, action, nextLinkList);//TODO:nextLinkList��������
			// 2. �ύ������
			ExecuteFacade.executeTask(workitemId, processInstId, instName,
					instDesc, activityId, action.getActionId(), action
							.getComment(), variableValueList, nextLinkList, user, action
							.getPositionId());
			// ��ǹ������
			workitem.setActivityState(WFConst.ACT_DONE);
			ActivityDefBean activityDef = this.findActivityCompo(
					processInstId, activityId);
			rewriteComment(processInstId + "", activityId + "", action
					.getComment(), bndata, activityDef);
			refreshBrief(processInstId + "", activityId + "", bndata,
					activityDef);
		} catch (NumberFormatException e) {
			throw new WFException("Error031701:�ύ���������");
		} catch (WorkflowException e) {
			switch (e.getCode()) {
			case 1122:
				throw new WFException("����ڵ�û��Ԥ��ִ���߻�ָ������ִ����˳���������ȷָ������ִ���ߡ�");
			case 1225:
				throw new WFException("��������������,����������ת��֧�������Ƿ��Ѿ�����!");
			default:
				throw new WFException(e.toString());
			}
		}
		return workitem;
	}

	/**
	 * ������ύ����������ȱʧ�Ĳ��������ݻ�����ݿ�����ȡ�� Ϊ�����ύ�͸�����ϵͳ�ṩ�ύ�ӿڵ�Ŀ�Ĵ����˷�����
	 * 
	 * @param strInstanceId
	 *            ���衣����ʵ��Id��������ҵ����е�process_inst_id�ֶΡ�����ʱ��ֵ����Ϊ0
	 * @param strTemplateId
	 *            ���衣ģ��Id�������ύ������ʵ��ʱ���裬��������ʱֵ��Ϊ����ֵ��
	 * @param strCompoId
	 *            ���衣������ʵId.
	 * @param strUserId
	 *            ���衣�û�Id.
	 * @param wfData
	 *            �Ǳ��衣�����������б����Ϊ�գ�������ݿ���ȡ�� ���ݰ�����WF_POSITION_ID ְλ����(Ψһ��)��
	 *            WFData.setWFVariable(sVarName, sVarValue)��ƴ�Ӹ��ַ��� ��ʽ������
	 *            <entity> <field name="WF_INSTANCE_NAME"
	 *            value="��������Ժ�Ĳ���"/>//�½�����ʱ���� <field name="WF_INSTANCE_DESC"
	 *            value="��������Ժ�Ĳ���"/>//�½�����ʱʹ�� <field name="WF_COMPANY_CODE"
	 *            value="601"/>//ʹ��ҵ���ϼ�ʱ���� <field name="WF_ORG_CODE"
	 *            value="02"/>//ʹ��ҵ���ϼ�ʱ���� <field name="WF_POSITION_ID"
	 *            value="112955359445300002"/>//ʹ��ҵ���ϼ�ʱ���� <field
	 *            name="WF_NEXT_EXECUTOR_ID" value="zhangsa"/>//ָ����һ�ڵ��ִ����ʱ��Ҫ;
	 *            <entity name="WF_VARIABLE"> <row> <entity> <field
	 *            name="VariableId" value="701" /> <field name="VariableName"
	 *            value="vTest" /> <field name="VariableValue" value="1" />
	 *            </entity> </row> <row> <entity> <field name="VariableId"
	 *            value="702" /> <field name="VariableName" value="vAudit" />
	 *            <field name="VariableValue" value="Yes" /> </entity> </row>
	 *            </entity> </entity>
	 * @return �ύ�ɹ�������"succes"�����򷵻���ʾ��Ϣ
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
      // ����Ĳ���������
      if ((null == strInstanceId && null == strTemplateId) || null == strCompoId
        || null == strUserId) {
        return "����Ĳ���������";
      }
      if (tableData == null) {
        tableData = WFUtil.getBusinessDataByInsId(strCompoId, strInstanceId);//1����ѯ
        tableData.setName(strCompoId);
      }// ����ǰ̨�޷���tabledata ,�����ݿ����

      WorkitemBean workitem = getWorkItemByInstance(strUserId, strInstanceId, 1);//1����ѯ
      // ȡ��workitem
      if (null == workitem) {
        return "�Ҳ�����������";
      }
      // getWorkItemByInstanceԭ������Id,��ʵ��ץȡ��workitemBean��findWorkitem��ѯ�Ľ��һ�����������ٴβ�ѯ
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
        valueList = getWFVariableValueList(strCompoId, tableData, wfData);// 10����ѯ
      }
      //�Ȳ�ѯ����һ���ڵ���Ϣ�����������롣
      List nextLinkList = WFUtil.getRightFollowedTaskLinkList(nInstanceId,
        nTemplateId, nNodeId, actionName, valueList);// 2����ѯ
      // ҵ������ֱ�Ӵ�ǰ̨�ͻ�
      Set executors = getDefaultNextExecutor(tableData, wfData, strCompoId,
        strUserId, strCoCode, strOrgCode, strPositionId, true, strNd, valueList,
        nextLinkList);//11����ѯ
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
          strExecutors2, nextLinkList, actionName);//5����ѯ
      }
      ExecuteFacade.executeTask(nWorkItemId, nInstanceId, null, null, nNodeId,
        actionName, strComment, valueList, nextLinkList, strUserId, strPositionId);//35����ѯ
      //after
      exeExecListenerCallback(listener, currentTask, 1);
      
      syncDataByBindedWFSate(nInstanceId, nTemplateId);// 6����ѯ
      ActivityDefBean activityDef = findActivityCompo(nInstanceId, nNodeId);// //3����ѯ
      rewriteComment(nInstanceId + "", nNodeId + "", strComment, tableData,
        activityDef);// 1����ѯ
      refreshBrief(nInstanceId + "", nNodeId + "", tableData, activityDef);//2����ѯ
      strResult = "success";// �ɹ�
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
	 * ������˻ط���������ȱʧ�Ĳ��������ݻ�����ݿ�����ȡ�� Ϊ�����˻غ͸�����ϵͳ�ṩ�ύ�ӿڵ�Ŀ�Ĵ����˷�����
	 * 
	 * @param strInstanceId
	 *            ���衣����ʵ��Id��������ҵ����е�process_inst_id�ֶΡ�
	 * @param strUserId
	 *            ���衣�û�Id.
	 * @return �ύ�ɹ�������"succes"�����򷵻���ʾ��Ϣ
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
			// ����Ĳ���������
			if (null == strInstanceId || null == strUserId) {
				return "����Ĳ���������";
			}
			WorkitemBean workItem = getWorkItemByInstance(strUserId,
					strInstanceId, 1);
			// ȡ��workitem
			if (null == workItem) {
				return "�Ҳ�����������";
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
	 * ����ʵ������ ʵ��������(״̬Ϊ����(9))�����²����������޸ġ�
	 * 
	 */
	public String rework(String instanceId, String strUserId, String comment,
			TableData bnData) throws WFException {
		String strResult = "failed";
		int nInstanceId;
		try {
			// ����Ĳ���������
			if (null == instanceId || null == strUserId) {
				return "����Ĳ���������";
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

	// ��ֹʵ�� ��״̬Ϊ-9
	public void interruptInstance(String instanceId, String svUserId,
			String comment) throws BusinessException {
		if (instanceId == null || svUserId == null) {
			throw new IllegalArgumentException("interrupt�Ĳ���Ϊnull");
		}
		int intInstanceId = Integer.parseInt(instanceId);
		try {
			ExecuteFacade.interruptInstance(intInstanceId, svUserId, comment);
		} catch (Exception ex) {
			throw new BusinessException(ex.toString());
		}
	}

	/**
	 * ȡָ��ģ��ͻ�ĵ����ж���
	 * 
	 * @param templateId
	 *            ģ��
	 * @param activityId
	 *            �
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
	 * ȡָ��ģ��ͻ�ĵ����ж���
	 * 
	 * @param templateId
	 *            ģ��
	 * @param activityId
	 *            �
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
	 * ȡ��������(�����û�������ʵ��ID)
	 * 
	 * @param user
	 * @param processInstId
	 * @param conn
	 * @return Ԫ������Ϊ WorkitemBean�����û�У����س���Ϊ 0 �� List
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
		// ������ֵ��Ԫ�������� CurrentTaskMeta ��Ϊ WorkitemBean
		for (Iterator i = taskMetas.iterator(); i.hasNext();) {
			CurrentTaskMeta item = (CurrentTaskMeta) i.next();
			workitems.add(WFUtil.copyWorkitem(item));
		}
		return workitems;
	}

	/**
	 * ȡ��������(�����û�������ʵ��ID)
	 * 
	 * @return Ԫ������Ϊ WorkitemBean
	 */
	public List getTodoListByProcessInstanceId(int processInstanceId,
			int isValid) throws WFException {
		List result = new ArrayList();
		try {
			// TCJLODO: �ı䷵��ֵԪ������Ϊ WorkitemBean�������� ActionMeta
			result = ExecuteFacade.getTodoListByInstance(processInstanceId,
					isValid);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
		return result;
	}

	/**
	 * ȡ��������ʷ
	 * 
	 * @param processInstId
	 *            ����ʵ��ID
	 * @param conn
	 *            ���ݿ�����
	 * @return Ԫ������Ϊ WorkitemBean��Ϊ��ʱ���س���Ϊ 0 �� List
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
		// ������ֵ��Ԫ������ ActionMeta ��Ϊ WorkitemBean
		for (Iterator i = actionMetas.iterator(); i.hasNext();) {
			ActionMeta item = (ActionMeta) i.next();
			workitems.add(WFUtil.copyWorkitem(item));
		}
		return workitems;
	}

	/**
	 * ȡ��������(��������ʵ��ID)
	 * <p>
	 * Ŀǰ��������� getProcessHistory �ظ���������ͬ��ֻ�Ƿ���ֵ���Ͳ�ͬ
	 * 
	 * @return Ԫ������Ϊ WorkitemBean
	 */
	public List getDoneListByProcessInstanceId(Object processInstanceId,
			int isValid) throws WFException {
		List result = new ArrayList();
		try {
			// TCJLODO: �ı䷵��ֵԪ������Ϊ WorkitemBean�������� ActionMeta
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
			// TCJLODO: �ı䷵��ֵԪ������Ϊ WorkitemBean�������� ActionMeta
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
	 * ����ʵ��
	 * 
	 * @param templateId
	 *            ����ID
	 * @param name
	 *            ʵ������
	 * @param description
	 *            ʵ������
	 * @param user
	 *            ������
	 * @param conn
	 *            ���ݿ�����
	 * @return һ���µ�����ʵ������*һ��*��Ϊ null
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
			throw new WFException("Error031201:����������ʧ�ܣ�");
		}
		return WFUtil.copyProcessInst(task);
	}

	/**
	 * ����������Ϣ(���ơ�������)
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

	/** ɾ������ */
	public void deleteInstance(String userId, Object instId) throws WFException {
		try {
			// �� deactivate����ɾ��
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
	 * ���� ID ��������ʵ������
	 * 
	 * @return *һ��*��Ϊ null
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
			throw new WFException("Error031401:�Ҳ���ָ��������ʵ����");
		}
		return WFUtil.copyProcessInst(inst);
	}

	/**
	 * ���ݹ�����ID���칤�������
	 * <p>
	 * TODO: �Ƿ�Ҫ����ִ����?
	 * 
	 * @return *һ��*��Ϊ null
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
			throw new WFException("Error031402:�Ҳ���ָ���Ĺ����");
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
	 * ����ǰ��ִ���ߵĹ�ϵȡ����һ�ڵ������ִ����
	 */
	public Set getDefaultNextExecutor(TableData entityData, TableData wfData,
			String entityName, String junior, String junCoCode,
			String junOrgCode, String junPosiCode, boolean flag, String nd,
			List wfVarList,List nextLinkList) throws WFException {
		Set executors = new HashSet();
		int nodeId = Integer.parseInt(wfData.getFieldValue(WFConst.WF_ACTIVITY_ID));
		String action = wfData.getFieldValue(WFConst.ACTION_NAME);
		if ("".equals(action)) {// ���û�иò������͸�Ϊnull
		  action = null;
		}
		int templateId = Integer.parseInt(wfData.getFieldValue(WFConst.WF_TEMPLATE_ID));
		int instanceId = Integer.parseInt(wfData.getFieldValue(WFConst.PROCESS_INST_ID));
		// ��ʹ�ô�session��ȡ����positionsId,����WFData�е�
		String orgPosiCode = wfData.getFieldValue(WFConst.POSITION_ID);
		// ��orgposicode��positionid��ת��
		try {
			/*
    			List valueList = wfVarList;// getWFVariableValueList(entityName,entityData, wfData);
          List nodeLinkList = new Link().getFollowedLinkList(templateId,nodeId, action);
    			nodeLinkList = new Instance().getRightFollowedTaskLinkList(instanceId, templateId, nodeLinkList, valueList);
    			if (nodeLinkList == null
    					|| (nodeLinkList != null && nodeLinkList.isEmpty())) {
    				logger.error("getDefaultNextExecutor method����õ�nodeLinkListΪ��");
    				return executors;
    			}
      */
      if (nextLinkList == null
        || (nextLinkList != null && nextLinkList.isEmpty())) {
      logger.error("getDefaultNextExecutor method����õ�nodeLinkListΪ��");
      return executors;
    }
			// Ŀǰ������ģʽ��֧����/��ڵ㣬������һ�ڵ�Ӧ��ֻ��1��Ԫ�أ������ö�����Ե�1��Ϊ׼
			Link nodeLink = (Link) nextLinkList.get(0);
			action = nodeLink.getActionName();// ȷ������������
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
			// ȡ����һ�ڵ�ִ���߹�ϵ
			String executorRelation = nodeLink.getExecutorRelation();
			// �����һ�ڵ�Ϊ��ǰ�ڵ�ִ�����Լ�
			if (executorRelation.equals(Link.EXECUTOR_RELATION_SELF)) {
				executors.add(junior);
			}// end if �Լ�
			// Ϊ��ǰ�ڵ�ִ������֯�ϼ�
			else if (executorRelation.equals(Link.EXECUTOR_RELATION_MANAGER)) {
				Staff staffHandler = new Staff();
				Set managerSet = staffHandler.getSuperStaffSet(junior,orgPosiCode);
				Object[] managers = managerSet.toArray();
				for (int m = 0; m < managers.length; m++) {
					String manager = (String) managers[m];
					executors.add(manager);
				}
				// DBHelper.closeConnection(con);
			}// end if �ϼ�
			// ͨ��ҵ���ϼ��㷨�����һ�ڵ�ִ����
			else if (executorRelation
					.equals(Link.EXECUTOR_RELATION_BUSINESS_SUPPERIOR)) {
				// ��������ҳ������
				// ���ݵ�λ���롢�������롢ְλ�����Ա������ҵ���ϼ����в�ѯ���ϵļ�¼
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
	 * �������̶���
	 * 
	 * @param processId
	 *            ����ID
	 * @param conn
	 *            ���ݿ�����
	 * @return ���̶��壬����Ϊ null
	 * @throws WFException
	 *             û��ָ��������
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
			throw new WFException("Error032432: �������̶������");
		}
		WFUtil.findBindVariable(d);
		WFUtil.findBindState(d);
		return d;
	}

	/**
	 * �������̰����Ļ
	 * 
	 * @return Ԫ������Ϊ ActivityDefBean
	 */
	public List findProcessActivitys(ProcessDefBean process) throws WFException {
		List list = new ArrayList();
		return list; // TCJLODO:0314
	}

	/** ��������� */
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
			throw new WFException("Error031702: ���һ�������");
		} catch (SQLException e) {
			throw new WFException(e.toString());
		}
		if (null == a) {
			throw new WFException("Error031402:�Ҳ���ָ���Ĺ����");
		}
		return a;
	}

	public ActivityDefBean findActivityDef(Object activityId)
			throws WFException {
		int nActivityId = Integer.parseInt(activityId.toString(), 10);
		try {

			NodeMeta nodeMeta = ConfigureFacade.getNode(nActivityId);
			if (nodeMeta == null || nodeMeta.getId() == 0) {
				throw new WFException("Error031402:�Ҳ���ָ���Ĺ����");
			}
			return this.findActivityDef(nodeMeta);
		} catch (WorkflowException e) {
			throw new WFException(e.toString());
		}
	}

	/**
	 * ָ��Դ��������е�����
	 * 
	 * @return Ԫ������Ϊ LinkDefBean
	 */
	public List findActivityLinksBySrc(ActivityDefBean activity)
			throws WFException {
		List list = new ArrayList();
		LinkDefBean link = new LinkDefBean();
		list.add(link);
		return list;
	}

	/**
	 * ������һ�����ܵĻ
	 * <p>
	 * ����� findActivityLinksBySrc �� findActivityDef
	 * 
	 * @return Ԫ������Ϊ ActionBean
	 */
	public List getNextActionList(String user, ActivityDefBean activity)
			throws WFException {
		List list = new ArrayList();
		ActionBean action = new ActionBean();
		list.add(action);
		return list; // TCJLODO:0314
	}

	/**
	 * ����ģ��id��ȡ����ģ���ж���Ľڵ��б�
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
	 * ��Ȩ
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
	 * �ƽ�
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
	 * ���˶ಽ
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
	 * ����
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
	 * ����
	 */
	public void callBack(int processInstanceId, int activityId,
			String svUserId, String comment, TableData bnData)
			throws WFException {
		try {
			String lastNode = WFUtil.getNodeIdByInstanceId(processInstanceId);
			if("".equals(lastNode)){
				throw new WFException("�������¼��ڵ�Ϊ�գ������ѱ��ջ�!");	
			}
			if (activityId != Integer.parseInt(lastNode)) {
				throw new WFException("�õ����ڵ�ǰ״̬�����ջ�!");
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
	 * �����û���ȡ�����б�
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
	 * �����û���ȡ����ʵ���б�
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

	// ȡ��������id(�����û�,����������Ѱ�)
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
	 * ��ȡ���лģ����б�
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
		// TCJLODO ��

		return null;
	}

	/**
	 * ��ȡָ���û��Ѱ������б�
	 * 
	 * @see com.anyi.erp.workflow.WFService#getDoneListByUserAndTemplate(java.lang.String,
	 *      int, java.lang.String, java.lang.String)
	 */
	public List getDoneListByUser(String userId, String startTime,
			String endTime) throws WFException {
		/* �趨Ĭ��ʱ�� */
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
			// TCJLODO: �ı䷵��ֵԪ������Ϊ WorkitemBean�������� ActionMeta
			return ExecuteFacade.getActionListByExecutor(userId, startTime,
					endTime);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * ��ȡָ���û��Ѱ�����ʵ���б�
	 * 
	 * @see com.anyi.erp.workflow.WFService#getDoneListByUserAndTemplate(java.lang.String,
	 *      int, java.lang.String, java.lang.String)
	 */
	public List getDoneInstListByUser(String userId, String startTime,
			String endTime) throws WFException {
		/* �趨Ĭ��ʱ�� */
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
	 * ��ȡָ���û��Ѱ������б�
	 * 
	 * @see com.anyi.erp.workflow.WFService#getDoneListByUserAndTemplate(java.lang.String,
	 *      int, java.lang.String, java.lang.String)
	 */
	public List getDoneCompoListByUser(String userId) throws WFException {
		try {
			// TCJLODO: �ı䷵��ֵԪ������Ϊ WorkitemBean�������� ActionMeta
			return ExecuteFacade.getActionCompoListByExecutor(userId);
		} catch (WorkflowException wfe) {
			throw new WFException(wfe.toString());
		}
	}

	/**
	 * ���ݹ�����ʵ����ȡ����id
	 * 
	 * @param instanceId
	 *            ������ʵ��id
	 * @return ��������Ӧ�Ĳ���id��
	 * @throws
	 * @see
	 */
	public List getCompoIdByInstanceId(int instanceId) throws WFException {
		List compoIdList = new ArrayList();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			// ��ȡ����ģ��id
			InstanceMeta instanceMeta = ExecuteFacade.getInstance(instanceId);
			// ��ȡ������
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
			logger.error("�������쳣��" + e.getMessage());
			throw new WFException("�������쳣��" + e.getMessage());
		} finally {
			DBHelper.closeConnection(conn, stmt, rs);
		}

		return compoIdList;
	}

	/**
	 * ���ݹ�����ʵ����ȡ����id
	 * 
	 * @param instanceId
	 *            ������ʵ��id
	 * @param nodeId
	 *            ����������id
	 * @return ��������Ӧ�Ĳ���id��
	 * @throws
	 * @see
	 */
	public String getCompoIdByNodeId(int instanceId, int nodeId)
			throws WFException {
		String result = null;
		try {
			// ��ȡ����ģ��id
			InstanceMeta instanceMeta = ExecuteFacade.getInstance(instanceId);
			// ��ȡ������
			String sql = "select distinct COMPO_ID from AS_WF_ACTIVITY_COMPO where "
					+ "WF_TEMPLATE_ID =? and WF_NODE_ID= ?";
			Integer[] params = new Integer[2];
			params[0] = new Integer(instanceMeta.getTemplateId());
			params[1] = new Integer(nodeId);
			result = (String) DBHelper.queryOneValue(sql, params);

		} catch (Exception e) {
			logger.error("�������쳣��" + e.getMessage());
			throw new WFException("�������쳣��" + e.getMessage());
		}

		return result;
	}

	/**
	 * ���ز�������������ģ���б�
	 * 
	 * @compoName �������������ǹ�����֧�ֵĲ��������߹���������
	 * @throws WFException
	 * @return ���ز�������������ģ���б� ����������List elementΪInteger��ֵΪģ��idֵ��
	 * @throws WorkflowException
	 * @throws NumberFormatException
	 */
	public List getCompoEnableStartedTemplate(String compoName)
			throws WFException {
		try {
			// /ҵ�񲿼��Ƿ�֧�ֹ�����
			if (compoName.equalsIgnoreCase(WFCompoType.WF_TODO)
					|| compoName.equalsIgnoreCase(WFCompoType.WF_DONE)) {
				return getAllActiveTemplate();
			}
			// ���ǹ�����������˵���ǹ�����֧�ֵ�ҵ�񲿼�
			// ���еĸò����ҽӵ�����
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
			logger.error("��ȡ�����ܹ������Ĺ�����ģ��ʧ�ܣ�");
			throw new WFException("��ȡ�����ܹ������Ĺ�����ģ��ʧ�ܣ�");
		}
	}

	/**
	 * ���ݹ�����ʵ����ȡҵ������ݡ� �����и����:��������compoIds�Ǹ����������б�,��ʵ����һ��ʵ�����ֻ���ܶ�Ӧһ������.
	 * ��������ʵ��ʵ���ϻ��𵽹�������,������ʵ����Ӧ��ҵ�������ҳ���.������һ������compId,��ҵ������tableData��Map Todo
	 * remove
	 * 
	 * @param instanceId
	 *            ������ʵ��id
	 * @return ����ҵ������TableData,���û��ҵ�����ݾͷ��ؿ�.
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
			// Ŀǰֻ����������
			Map params = new HashMap();
			params.put(WFConst.PROCESS_INST_ID_FIELD, String
					.valueOf(processInstId));
			Datum datum = ((ServiceFacade) ApplusContext
					.getBean("serviceFacade")).getEntityWithDatum(tableName,
					params);
			List datas = datum.getData();
			if (datas.size() == 0) {
				throw new WFException("��ҵ������!");
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
	 * ����ģ��id��ȡTemplateMeta
	 * 
	 * @see com.anyi.erp.workflow.WFService#getTemplateMetaByTemplateId()
	 */
	public TemplateMeta getTemplateMetaByTemplateId(int templateId) {
		Template template = new Template();
		try {
			return template.getTemplate(templateId);
		} catch (WorkflowException e) {
			logger.error("��ȡ������ģ��ʱ����");
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
			logger.error("���ݵ�ǰ�����ȡ��������,ʱ�����쳣.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error("���ݵ�ǰ�����ȡ��������,ʱ�����쳣.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return activityDef.getCompoName();
	}

	/**
	 * �����ύ
	 * 
	 * @param entityName
	 *            ʵ��������������
	 * @param wfData
	 *            ������������ݣ������������һ���Ļ��ִ���ߵ�
	 * @param detailTaskId
	 *            ��Ҫ���ܵ�������id�б�
	 * @return ��ǰ���������ִ���ˡ����̱�����
	 * @throws BusinessException
	 */
	public TableData collectCommit(TableData wfData, String entityName,
			List lstDetailTaskId, TableData bnData) throws WFException {
		TableData result = new TableData();
		ActionBean action = new ActionBean(wfData);
		/* ������ϸ���� */
		Object workitemId = wfData.getField(WFConst.WORKITEM_ID);
		Iterator iter = lstDetailTaskId.iterator();
		while (iter.hasNext()) {
			Integer element = (Integer) iter.next();
			WFUtil.collectCurrentTask(element.intValue(), Integer
					.parseInt(workitemId.toString()));
			// Ϊ�����񴴽���һ��ִ����
			WorkitemBean childWorkitem = findWorkitem(element, 1);
			try {
				// �����ύ���Ժ��Թ����������ļ���
				WFUtil.createNextExecutor(childWorkitem, action,
						new ArrayList());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new WFException("������һ��ִ���˳���");
			} catch (WorkflowException e) {
				e.printStackTrace();
				throw new WFException("������һ��ִ���˳���");
			}
		}

		/* �ύ�������� */
		WorkitemBean item = findWorkitem(workitemId, 1);
		/* ���ù��������� */
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

		// �����ύ���Ժ��Թ����������ļ���
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
				// ֻȡһ��
				result = (WorkitemBean) lstWorkitemBean.get(0);

		} catch (WFException e) {
			e.printStackTrace();
			throw new WFException("��ȡ������ʱ����!");
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
			throw new WFException("��ȡ������ʱ����!");
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
	 * ���ð��˹�����״̬������ҵ���ֶ�ֵ�����ѹ�����״ֵ̬ͬ�����󶨵�ҵ���ֶ����� Ŀǰֻ���ǰ�״̬���õ������ֶε����
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
		// ��ȡ״ֵ̬
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
						// ����״ֵ̬��ҵ��������
						updateSql = "update ";
						updateSql += elemStateInfo.getTabName().toUpperCase();
						updateSql += " set ";
						updateSql += elemStateInfo.getFieldName();
						// ��ΪĿǰ������״ֻ̬�����ͺ��ַ�������,���Զ����԰����ַ������в���.
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
			throw new WFException("���ݸ����̻�ȡ������ʱ����");
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
					.error("WFService.getInstanceStatus(String instanceId)���õ�ʱ���������ȷ.����ֵΪ:"
							+ instanceId);
			throw new RuntimeException(e);
		} catch (WorkflowException e) {
			// TCJLODO Auto-generated catch block
			logger
					.error("WFService.getInstanceStatus(String instanceId)���õ�ʱ���׳��������쳣");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/*
	 * �ѵ�ǰ���д����ǰ�ڵ������ֶ��С� ��������ǰÿ�ζ��������ۼ������дһ�ε������� ��������������в�ͬ����ֶε����⡣
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
				commentBuff.append("[��ʷ���ݣ�");
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
	 * ��ȡ�������ڵ�ҽ���Ϣ
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
	 * �ӹ�����ֱ�Ӷ�ȡ������ݣ������õ�db�� ����������Ҫ�ۼ�������ɲ����ÿ�����
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
			// ��ȡCurrentTaskId,��workitemId
			// ֻѡ��Ч������
			user = URLDecoder.decode(user, "UTF-8");
			WorkitemBean workitem = getWorkItemByInstance(user, ""
					+ nInstanceId, 1);

			if (workitem == null) {
				// �п��������Ѿ�������
				// if(wfs.isInstanceFinished(nInstanceId,conn)){
				// List actionMeta = new ArrayList();
				List actionMeta = this.getDoneListByUserAndInstance(user,
						nInstanceId, 1);
				if (actionMeta == null || actionMeta.size() == 0) {
					actionMeta = getDoneListByProcessInstanceId(nInstanceId, 0);
				}

				// ȡ���һ��Action,ȡ��������еĻ���id
				if (actionMeta.size() < 1) {
					throw new BusinessException("�������ڲ���������,��ʹ�ù������������������!");
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
				// ��������Ч������
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
			throw new BusinessException("�޷��ӹ����������ȡ����ʵ��");
		}
		return res;
	}

	/**
	 * �ֶ��ύ
	 */
	public TableData commit(TableData entity, String entityName,
			TableData wfData) throws BusinessException {
		// ; 1. ���ҹ������ 2. �ύ������ 3. ��ҵ������д��ҵ�����ݱ���
		TableData result = new TableData();
		try {
			String user = wfData.getFieldValue(WFConst.WF_CURRENT_EXECUTOR_ID);
			CompoMeta entityMeta = MetaManager.getCompoMeta(entityName);

			/* ������������ */
			List lsWFVariable = WFUtil.getWFVariableFromPage(wfData);
			ProcessDefBean pdf = findProcessDef(wfData
					.getField(WFConst.WF_TEMPLATE_ID));
			Object[] masterTab_KeyFieldCondition = WFUtil
					.getMasterTabKeyFieldCondition(entity, entityName);
			WFUtil.setBindWFVariableValue(lsWFVariable, pdf
					.getBindVariableInfo(), entityMeta.getName(),
					masterTab_KeyFieldCondition);

			// 2. �ύ������
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

			// 4. ͬ��״̬
			syncDataByBindedWFSate(Integer.parseInt(wfData.getField(
					WFConst.PROCESS_INST_ID).toString()), Integer
					.parseInt(wfData.getField(WFConst.WF_TEMPLATE_ID)
							.toString()));
		} catch (Exception e) {
			throw new BusinessException(e.toString());
		}
		return result;
	}

	// ���·���Ŀǰ��Ҫ��Ϊ����OA����Ҫ����ӵ� _ start
	/**
	 * ׷����һ��ִ����
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
			// ȡҵ������
			tableData = WFUtil
					.getBusinessDataByInsId(strCompoId, strInstanceId);
			if (null == tableData) {
				throw new WFException("�Ҳ���ҵ������");
			}
			// ������û�б�����Ϣ�����Ҹ�ģ�����б���
			if ((null == valueList || (null != valueList && valueList.size() == 0))) {
				valueList = getWFVariableValueList(strCompoId, tableData,
						wfData);
			}
			String action = wfData.getFieldValue(WFConst.ACTION_NAME);
			if ("".equals(action)) {// ���û�иò������͸�Ϊnull
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
					logger.error("��õ�nodeLinkListΪ��");
					throw new WFException("��õ�nodeLinkListΪ��");
				}
				Link nodeLink = (Link) nodeLinkList.get(0);
				nextNodeId = nodeLink.getNextNodeId();
			}
			String[] res = WFUtil.instanceHasCommited(instanceId, nextNodeId);
			int count = Integer.parseInt(res[0]);
			if (count == 0) {
				throw new WFException("�Ѿ��ύ������׷��ִ����!");
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
	 * ��ȡĬ�ϵ�ִ����
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
	 * ��ȡ����ʱ��ִ����
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
	 * ɾ����һ����ִ����
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
				result = "��������һ�ڵ��Ѿ�ͨ��������ɾ��ִ����!";
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
				result = "�����ڴ˽ڵ��Ѿ�ͨ��������ɾ��ִ����!";
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

	// һ�·���Ŀǰ��Ҫ��Ϊ����OA����Ҫ����ӵ� _ end

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
