/*
 * Created on 2004-12-20
 *
 * �޸ļ�¼ (from 2004-12-20)
 */
package com.anyi.gp.workflow.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.TableData;
import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.workflow.WFFactory;
import com.anyi.gp.workflow.WFService;
import com.anyi.gp.workflow.bean.ActivityDefBean;
import com.kingdrive.workflow.business.Node;
import com.kingdrive.workflow.business.NodeState;
import com.kingdrive.workflow.business.State;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.listener.TaskListener;

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
 * Company: ��������
 * </p>
 * 
 * @author zhangcheng
 * @time 2004-12-20
 * 
 */
public abstract class WFStatusSyncTask implements TaskListener{
	static Logger logger = Logger.getLogger(WFStatusSyncTask.class);

	private Map syncStatusMap;// ��Ҫͬ����״̬�����Լ������ֶ���

	/**
	 * ָ����Ҫͬ���Ķ��״̬��,�����������и��ֶ�status,Ҫ�͹������ж����һ��״ֵ̬audit_statusͬ�� ���ָ���� String
	 * []syncFieldNames={"status"}; String []syncWFStatusNames={"audit_status"};
	 * designateSyncStatusName(syncFieldNames,syncWFStatusNames)
	 * 
	 * @param String
	 *          []fieldNames ��������Ҫ��ͬ�����ֶ������б�
	 * @param String
	 *          []WFStatusNames ��Ҫͬ���Ĺ�����״̬��
	 * @throws ���ָ���Ĳ��������С����ȣ��׳�����ʱ�쳣��
	 * @see
	 */
	public void addSyncStatusName(String[] fieldNames, String[] WFStatusNames){
		if(fieldNames == null || WFStatusNames == null)
			throw new RuntimeException("��������Ϊ��");
		if(fieldNames.length != WFStatusNames.length)
			throw new RuntimeException("��Ҫͬ���Ĳ�������Ĵ�СҪ��ͬ");
		if(syncStatusMap == null)
			syncStatusMap = new HashMap();
		for(int i = 0, j = fieldNames.length; i < j; i++){
			syncStatusMap.put(fieldNames[i], WFStatusNames[i]);
		}
	}

	/**
	 * ָ����Ҫͬ���ĵ���״̬��,���Զ�ε�����ָ�����ͬ��״̬��
	 * 
	 * @param fieldName
	 * @param WFStatusName
	 * @throws
	 * @see
	 */
	public void addSyncStatusName(String fieldName, String WFStatusName){
		if(fieldName == null || WFStatusName == null)
			throw new RuntimeException("��������Ϊ��");
		if(syncStatusMap == null)
			syncStatusMap = new HashMap();
		syncStatusMap.put(fieldName, WFStatusName);
	}

	/*
	 * public void designateSyncStatusName(Map map) throws BusinessException{
	 * if(map.size()<=0)throw new RuntimeException("��������Ϊ��"); syncStatusMap=new
	 * HashMap(map); }
	 */
	/**
	 * ��ʼ��Ҫ���Ĺ�����ָ����Ҫͬ���Ĺ�����״̬���������Ӧ���ֶ����ƶԡ�
	 * ���Ե��ýӿ�WFStatusSyncTask�Ľӿ�:addSyncStatusName(String[],String[])
	 * ����addSyncStatusName(String,String)��
	 * Ҫָ�������Ҫͬ���Ĺ�����״̬���ȿ��Ե���addSyncStatusName(String[],String[]),
	 * Ҳ���Զ�ε���addSyncStatusName(String,String)�� Ŀǰֻ֧�������ֶε�ͬ��
	 */
	public abstract void init();

	/*
	 * @see com.kingdrive.workflow.listener.TaskListener#beforeExecution(com.kingdrive.workflow.dto.CurrentTaskMeta)
	 */
	public void beforeExecution(CurrentTaskMeta arg0){
	}

	/*
	 * @see com.kingdrive.workflow.listener.TaskListener#afterExecution(com.kingdrive.workflow.dto.CurrentTaskMeta)
	 */
	public void afterExecution(CurrentTaskMeta curTask){
		init();
		// ����Ƿ�ָ����ͬ��״̬����
		if(syncStatusMap == null)
			throw new RuntimeException("��δָ����Ҫͬ����" + "������״̬��,��ο�init()�������������Ҫͬ��״̬��������������,��ֱ��ʵ��TaskListener�ӿڣ�");
		// ��ȡ��Ҫ���͵�tabledata����
		int nodeId = curTask.getNodeId();
		try{
			WFService service = WFFactory.getInstance().getService();
			Node nodeHandle = new Node();
			ActivityDefBean activityDef = service.findActivityDef(nodeHandle.getNode(nodeId));

			TableData tableData = service.getCompoDataByCurTask(curTask.getInstanceId(), activityDef);
			if(tableData == null)
				return;
			// �����û�ָ������Ҫ���µ��ֶν��и���
			Iterator iter = syncStatusMap.keySet().iterator();
			while(iter.hasNext()){
				String fieldName = (String) iter.next();
				String WFStatusName = (String) syncStatusMap.get(fieldName);
				tableData.setField(fieldName, getWFStatusValue(curTask.getNodeId(), curTask.getTemplateId(), WFStatusName));
			}
			WorkflowService workflowService = (WorkflowService) ApplusContext.getBean("workflowService");
			workflowService.update(tableData, activityDef.getCompoName());
		}catch(BusinessException e){
			// TCJLODO: handle exception
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}catch(WorkflowException e){
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ȡ��ǰ�ڵ�ĵ���һ���ڵ��ָ��״ֵ̬��
	 * 
	 * @param statusName
	 * @return
	 * @throws
	 * @see
	 */
	private String getWFStatusValue(int nodeId, int templateId, String statusName){
		String result = null;
		NodeState ns = new NodeState();
		try{
			List list = ns.getStateListByNode(nodeId);
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				NodeState nodeState = (NodeState) iter.next();
				State state = new State();
				String wfStateName = (state.getState(nodeState.getStateId())).getName();//
				if(wfStateName.equalsIgnoreCase(statusName)){
					result = nodeState.getStateValue();
					break;
				}
			}
		}catch(WorkflowException e){
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	public void beforeUntread(CurrentTaskMeta meta){
		;// do nothing
	}

	public void afterUntread(CurrentTaskMeta meta){
		;// do nothing
	}
}
