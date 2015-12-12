/*
 * Created on 2004-12-20
 *
 * 修改记录 (from 2004-12-20)
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
 * Company: 用友政务
 * </p>
 * 
 * @author zhangcheng
 * @time 2004-12-20
 * 
 */
public abstract class WFStatusSyncTask implements TaskListener{
	static Logger logger = Logger.getLogger(WFStatusSyncTask.class);

	private Map syncStatusMap;// 需要同步的状态名称以及主表字段名

	/**
	 * 指定需要同步的多个状态名,例如主表中有个字段status,要和工作流中定义的一个状态值audit_status同步 如此指定： String
	 * []syncFieldNames={"status"}; String []syncWFStatusNames={"audit_status"};
	 * designateSyncStatusName(syncFieldNames,syncWFStatusNames)
	 * 
	 * @param String
	 *          []fieldNames 主表中需要被同步的字段名称列表
	 * @param String
	 *          []WFStatusNames 需要同步的工作流状态名
	 * @throws 如果指定的参数数组大小不相等，抛出运行时异常。
	 * @see
	 */
	public void addSyncStatusName(String[] fieldNames, String[] WFStatusNames){
		if(fieldNames == null || WFStatusNames == null)
			throw new RuntimeException("参数不能为空");
		if(fieldNames.length != WFStatusNames.length)
			throw new RuntimeException("需要同步的参数数组的大小要相同");
		if(syncStatusMap == null)
			syncStatusMap = new HashMap();
		for(int i = 0, j = fieldNames.length; i < j; i++){
			syncStatusMap.put(fieldNames[i], WFStatusNames[i]);
		}
	}

	/**
	 * 指定需要同步的单个状态名,可以多次调用来指定多个同步状态。
	 * 
	 * @param fieldName
	 * @param WFStatusName
	 * @throws
	 * @see
	 */
	public void addSyncStatusName(String fieldName, String WFStatusName){
		if(fieldName == null || WFStatusName == null)
			throw new RuntimeException("参数不能为空");
		if(syncStatusMap == null)
			syncStatusMap = new HashMap();
		syncStatusMap.put(fieldName, WFStatusName);
	}

	/*
	 * public void designateSyncStatusName(Map map) throws BusinessException{
	 * if(map.size()<=0)throw new RuntimeException("参数不能为空"); syncStatusMap=new
	 * HashMap(map); }
	 */
	/**
	 * 初始化要做的工作是指定需要同步的工作流状态名与主表对应的字段名称对。
	 * 可以调用接口WFStatusSyncTask的接口:addSyncStatusName(String[],String[])
	 * 或者addSyncStatusName(String,String)。
	 * 要指定多个需要同步的工作流状态，既可以调用addSyncStatusName(String[],String[]),
	 * 也可以多次调用addSyncStatusName(String,String)。 目前只支持主表字段的同步
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
		// 检查是否指定了同步状态名称
		if(syncStatusMap == null)
			throw new RuntimeException("尚未指定需要同步的" + "工作流状态名,请参考init()方法。如果不需要同步状态数据与主表数据,请直接实现TaskListener接口！");
		// 获取需要发送的tabledata数据
		int nodeId = curTask.getNodeId();
		try{
			WFService service = WFFactory.getInstance().getService();
			Node nodeHandle = new Node();
			ActivityDefBean activityDef = service.findActivityDef(nodeHandle.getNode(nodeId));

			TableData tableData = service.getCompoDataByCurTask(curTask.getInstanceId(), activityDef);
			if(tableData == null)
				return;
			// 根据用户指定的需要更新的字段进行更新
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
	 * 获取当前节点的的下一个节点的指定状态值。
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
