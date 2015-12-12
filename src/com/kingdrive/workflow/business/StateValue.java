package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.kingdrive.workflow.access.StateValueBean;
import com.kingdrive.workflow.access.StateValueQuery;
import com.kingdrive.workflow.dto.StateValueMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.StateValueInfo;
import com.kingdrive.workflow.model.StateValueModel;
import com.kingdrive.workflow.util.Sequence;

/**
 * 状态综述 因原来不能区分正常提交和退回到某个节点的状态值。故对状态作变更和厘清。
 * 正常提交时，会将生成的待办任务的节点的状态值赋给对应状态。如“正在某科室处理” 退回时，会将目标节点后的流向的状态值赋给对应状态。如“退回到某科室”
 * 收回和跳转时，会将生成的待办任务的节点的状态值赋给对应状态。如“正在某科室处理” 流程结束时，会将最后经过的流向的状态值赋给对应状态。如“审核结束”
 * 
 * by zhanggh. 060412
 */
public class StateValue implements Serializable{

	public StateValue(){
	}

	public List getStateListByInstance(int instanceId) throws WorkflowException{
		List result = new ArrayList();

		try{
			StateValueQuery query = new StateValueQuery();
			ArrayList list = query.getValueListByInstance(instanceId);
			StateValueMeta meta = null;
			for(int i = 0; i < list.size(); i++, result.add(meta)){
				meta = wrap((StateValueInfo) list.get(i));
			}
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
		return result;
	}

	public void setNodeState(int instanceId, int nodeId) throws WorkflowException{
			List instanceStateValueList = getStateListByInstance(instanceId);

			NodeState stateHandler = new NodeState();
			List stateList = stateHandler.getStateListByNode(nodeId);
			for(int m = 0; m < stateList.size(); m++){
				boolean existed = false;
				NodeState nodeState = (NodeState) stateList.get(m);
				for(int i = 0; i < instanceStateValueList.size(); i++){
					StateValueMeta instanceStateValue = (StateValueMeta) instanceStateValueList.get(i);
					if(nodeState.getStateId() == instanceStateValue.getStateId()){
						existed = true;

						instanceStateValue.setValue(nodeState.getStateValue());
						update(instanceStateValue);

						break;
					}
				}
				if(!existed){
					StateValueMeta stateValue = new StateValueMeta();
					stateValue.setInstanceId(instanceId);
					stateValue.setStateId(nodeState.getStateId());
					stateValue.setValue(nodeState.getStateValue());
					create(stateValue);
				}
			}
	}

	public void removeNodeState(int instanceId, int nodeId) throws WorkflowException{
		List instanceStateValueList = getStateListByInstance(instanceId);
		Set nodeStateSet = new HashSet();
		NodeState nodeStateHandler = new NodeState();
		nodeStateSet.addAll(nodeStateHandler.getStateListByNode(nodeId));

		Iterator nodeStateIterator = nodeStateSet.iterator();
		while(nodeStateIterator.hasNext()){
			NodeState nodeState = (NodeState) nodeStateIterator.next();
			for(int i = 0; i < instanceStateValueList.size(); i++){
				StateValueMeta instanceStateValue = (StateValueMeta) instanceStateValueList.get(i);
				if(nodeState.getStateId() == instanceStateValue.getStateId()){
					delete(instanceStateValue.getId());
					break;
				}
			}
		}
	}

	public void setLinkState(int instanceId, List linkList) throws WorkflowException{
		List instanceStateValueList = getStateListByInstance(instanceId);

		LinkState linkStateHandler = new LinkState();
		// cuiliguo 2006.06.14 不要用无序的集合类型存储 nodeLinkState 列表。
		List nodeLinkStateList = new ArrayList();
		for(int i = 0; i < linkList.size(); i++){
			Link nodeLink = (Link) linkList.get(i);
			nodeLinkStateList.addAll(linkStateHandler.getStateListByLink(nodeLink.getId()));
		}

		Iterator nodeLinkStateIterator = nodeLinkStateList.iterator();
		while(nodeLinkStateIterator.hasNext()){
			boolean updated = false;

			LinkState nodeLinkState = (LinkState) nodeLinkStateIterator.next();
			for(int i = 0; i < instanceStateValueList.size(); i++){
				StateValueMeta instanceStateValue = (StateValueMeta) instanceStateValueList.get(i);
				if(nodeLinkState.getStateId() == instanceStateValue.getStateId()){
					updated = true;

					instanceStateValue.setValue(nodeLinkState.getStateValue());
					update(instanceStateValue);

					break;
				}
			}
			if(!updated){
				StateValueMeta stateValue = new StateValueMeta();
				stateValue.setInstanceId(instanceId);
				stateValue.setStateId(nodeLinkState.getStateId());
				stateValue.setValue(nodeLinkState.getStateValue());
				create(stateValue);
			}
		}
	}

	public void removeLinkState(int instanceId, List nodeLinkList) throws WorkflowException{
		List instanceStateValueList = getStateListByInstance(instanceId);

		LinkState linkStateHandler = new LinkState();
		Set linkStateSet = new HashSet();
		for(int i = 0; i < nodeLinkList.size(); i++){
			Link nodeLink = (Link) nodeLinkList.get(i);
			linkStateSet.addAll(linkStateHandler.getStateListByLink(nodeLink.getId()));
		}

		Iterator linkStateIterator = linkStateSet.iterator();
		while(linkStateIterator.hasNext()){
			LinkState linkState = (LinkState) linkStateIterator.next();
			for(int i = 0; i < instanceStateValueList.size(); i++){
				StateValueMeta instanceStateValue = (StateValueMeta) instanceStateValueList.get(i);
				if(linkState.getStateId() == instanceStateValue.getStateId()){
					delete(instanceStateValue.getId());
					break;
				}
			}
		}
	}

	private void create(StateValueMeta meta) throws WorkflowException{
		try{
			StateValueBean bean = new StateValueBean();
			meta.setId(Sequence.fetch(Sequence.SEQ_STATE_VALUE));
			if(bean.insert(unwrap(meta)) != 1)
				throw new WorkflowException(2002);
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
	}

	private void delete(int stateValueId) throws WorkflowException{
		try{
			StateValueBean bean = new StateValueBean();
			bean.delete(stateValueId);
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
	}

	private void update(StateValueMeta meta) throws WorkflowException{
		try{
			StateValueBean bean = new StateValueBean();
			if(bean.update(unwrap(meta)) != 1)
				throw new WorkflowException(2004);
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
	}

	public void removeByInstance(int instanceId) throws WorkflowException{
		try{
			StateValueBean bean = new StateValueBean();
			bean.removeByInstance(instanceId);
		}catch(SQLException e){
			throw new WorkflowException(2000, e.toString());
		}
	}

	private StateValueMeta wrap(StateValueInfo model){
		StateValueMeta meta = new StateValueMeta();
		if(model.getStateValueId() != null)
			meta.setId(model.getStateValueId().intValue());
		if(model.getTemplateId() != null)
			meta.setTemplateId(model.getTemplateId().intValue());
		if(model.getInstanceId() != null)
			meta.setInstanceId(model.getInstanceId().intValue());
		if(model.getStateId() != null)
			meta.setStateId(model.getStateId().intValue());
		meta.setName(model.getName());
		meta.setDescription(model.getDescription());
		meta.setValue(model.getValue());
		return meta;
	}

	private StateValueModel unwrap(StateValueMeta meta){
		StateValueModel model = new StateValueModel();
		if(meta.getId() != 0)
			model.setStateValueId(meta.getId());
		if(meta.getValue() != null)
			model.setValue(meta.getValue());
		if(meta.getStateId() != 0)
			model.setStateId(meta.getStateId());
		if(meta.getInstanceId() != 0)
			model.setInstanceId(meta.getInstanceId());
		return model;
	}

	/**
	 * 根据下一流向列表设置节点状态
	 * 
	 * @param instanceId
	 * @param followedNodeLinkList
	 * @param conn
	 * @throws WorkflowException
	 */
	public void setNodeState(int instanceId, List followedNodeLinkList) throws WorkflowException{
		List instanceStateValueList = getStateListByInstance(instanceId);

		NodeState nodeStateHandler = new NodeState();
		Set nodeStateSet = new HashSet();
		for(int i = 0; i < followedNodeLinkList.size(); i++){
			Link nodeLink = (Link) followedNodeLinkList.get(i);
			nodeStateSet.addAll(nodeStateHandler.getStateListByNode(nodeLink.getNextNodeId()));
		}

		Iterator nodeStateIterator = nodeStateSet.iterator();
		while(nodeStateIterator.hasNext()){
			boolean updated = false;

			NodeState nodeState = (NodeState) nodeStateIterator.next();
			for(int i = 0; i < instanceStateValueList.size(); i++){
				StateValueMeta instanceStateValue = (StateValueMeta) instanceStateValueList.get(i);
				if(nodeState.getStateId() == instanceStateValue.getStateId()){
					updated = true;

					instanceStateValue.setValue(nodeState.getStateValue());
					update(instanceStateValue);

					break;
				}
			}
			if(!updated){
				StateValueMeta stateValue = new StateValueMeta();
				stateValue.setInstanceId(instanceId);
				stateValue.setStateId(nodeState.getStateId());
				stateValue.setValue(nodeState.getStateValue());
				create(stateValue);
			}
		}
	}
}
