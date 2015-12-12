package com.kingdrive.workflow.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.kingdrive.workflow.access.NodeBean;
import com.kingdrive.workflow.access.NodeQuery;
import com.kingdrive.workflow.access.PairQuery;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.NodeInfo;
import com.kingdrive.workflow.model.NodeModel;
import com.kingdrive.workflow.model.PairInfo;
import com.kingdrive.workflow.util.Sequence;

public class Node {

	/* 节点类型 */
	public static final String TYPE_START = "0";

	public static final String TYPE_END = "1";

	public static final String TYPE_TASK = "2";// 任务节点

	public static final String TYPE_NAVIGATION = "3";// 分支节点

	public static final String TYPE_EVENT = "4";// ??

	public static final String TYPE_AND = "5";// 与节点

	public static final String TYPE_OR = "6";// 或节点

	/* 执行方式 */
	public static final String EXECUTORS_METHOD_SOLO = "0";// 独签

	public static final String EXECUTORS_METHOD_PARALLEL = "1";// 并签

	public static final String EXECUTORS_METHOD_SERIAL = "2";// 顺序签

	public Set getBusinessSet(int templateId) throws WorkflowException {
		Set result = new HashSet();

		try {
			PairQuery query = new PairQuery();
			List list = query.getBusinessByTemplate(templateId);
			for (int i = 0; i < list.size(); i++) {
				result.add(((PairInfo) list.get(i)).getId());
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}

		return result;
	}

	public Set getBusinessSet(String templateType) throws WorkflowException {
		Set result = new HashSet();

		try {
			PairQuery query = new PairQuery();
			List list = query.getBusinessByTemplate(templateType);
			for (int i = 0; i < list.size(); i++) {
				result.add(((PairInfo) list.get(i)).getId());
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}

		return result;
	}

	public Set getActionSet(int templateId, int nodeId)
			throws WorkflowException {
		Set result = new TreeSet();
		try {
			PairQuery query = new PairQuery();
			List list = query.getActionByNode(templateId, nodeId);
			for (int i = 0; i < list.size(); i++) {
				result.add(((PairInfo) list.get(i)).getId());
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}

		return result;
	}

	public List getActionList(int templateId, int nodeId)
			throws WorkflowException {
		List result = new ArrayList();
		try {
			PairQuery query = new PairQuery();
			List list = query.getActionByNode(templateId, nodeId);
			for (int i = 0; i < list.size(); i++) {
				result.add(((PairInfo) list.get(i)).getId());
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}

		return result;
	}

	public String getDefaultActionName(int templateId, int nodeId)
			throws WorkflowException {
		String result = null;
		try {
			PairQuery query = new PairQuery();
			List list = query.getActionByNode(templateId, nodeId);
			if (list.size() == 1)
				return ((PairInfo) list.get(0)).getId();// 如果只有一个流向便返回之．
			for (int i = 0; i < list.size(); i++) {
				PairInfo pairInfo = (PairInfo) list.get(i);
				if (pairInfo.getDefaultPath().equalsIgnoreCase(
						PairInfo.TYPE_DEFAULT)) {
					result = pairInfo.getId();
					break;
				} else {
					continue;
				}
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}

		return result;
	}

	public Set getActionSet(String templateType, String businessType)
			throws WorkflowException {
		Set result = new TreeSet();

		try {
			PairQuery query = new PairQuery();
			List list = query.getActionByNode(templateType, businessType);
			for (int i = 0; i < list.size(); i++) {
				result.add(((PairInfo) list.get(i)).getId());
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}

		return result;
	}

	public List getTaskNodeList(int templateId) throws WorkflowException {
		List result = new ArrayList();
		try {
			NodeQuery bean = new NodeQuery();
			ArrayList list = bean.getTaskNodeListByTemplate(templateId);
			NodeMeta meta = null;
			for (int i = 0; i < list.size(); i++, result.add(meta)) {
				meta = wrap((NodeInfo) list.get(i));
			}
		} catch (SQLException e) {
			throw new WorkflowException(2001, e.toString());
		}

		Collections.sort(result);
		return result;
	}

	public List getNodeList(int templateId) throws WorkflowException {
		List result = new ArrayList();
		try {
			NodeQuery bean = new NodeQuery();
			ArrayList list = bean.getNodeListByTemplate(templateId);
			NodeMeta meta = null;
			for (int i = 0; i < list.size(); i++, result.add(meta)) {
				meta = wrap((NodeInfo) list.get(i));
			}
		} catch (SQLException e) {
			throw new WorkflowException(2001, e.toString());
		}

		Collections.sort(result);
		return result;
	}

	public List getFollowedNodeList(int templateId, int precedingNodeId,
			int followedNodeId) throws WorkflowException {
		List result = new ArrayList();

		if (precedingNodeId == -1) {
			result = getTaskNodeList(templateId);
			return result;
		}

		List nodeList = new ArrayList();
		nodeList.addAll(getNodeList(templateId));

		NodeMeta end = new NodeMeta();
		end.setId(-2);
		end.setName("结束节点");
		end.setType(Node.TYPE_END);
		end.setTemplateId(templateId);
		nodeList.add(end);

		List linkList = new ArrayList();
		Link linkHandler = new Link();
		linkList = linkHandler.getLinkList(templateId);

		NodeMeta precedingNode = getNode(precedingNodeId);

		for (int i = 0; i < nodeList.size(); i++) {
			NodeMeta node = (NodeMeta) nodeList.get(i);
			boolean flag = true;// should be add or not
			if (precedingNode.getType().equals(Node.TYPE_TASK)) {
				// do nothing, the task node's followed node can be any type
			} else if (precedingNode.getType().equals(Node.TYPE_NAVIGATION)) {
				// navigation node's followed node cann't be and/or node
				if (node.getType().equals(Node.TYPE_AND)
						|| node.getType().equals(Node.TYPE_OR)) {
					continue;
				}
			} else if (precedingNode.getType().equals(Node.TYPE_AND)
					|| precedingNode.getType().equals(Node.TYPE_OR)) {
				// and/or node's followed node cann't be navigation node
				if (node.getType().equals(Node.TYPE_NAVIGATION)) {
					continue;
				}
			} else {
				// throw exception
			}

			if (precedingNodeId == node.getId()) {
				continue;
			}

			for (int l = 0; l < linkList.size(); l++) {
				Link link = (Link) linkList.get(l);
				if ((precedingNodeId == link.getCurrentNodeId() && node.getId() == link
						.getCurrentNodeId())
						|| (precedingNodeId == link.getCurrentNodeId()
								&& node.getId() == link.getNextNodeId() && node
								.getId() != followedNodeId)
						|| (precedingNodeId == link.getNextNodeId() && node
								.getId() == link.getCurrentNodeId())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				result.add(node);
			}
		}
		return result;
	}

	public List getFollowedTaskNodeList(int templateId, int nodeId)
			throws WorkflowException {
		Link linkHandler = new Link();
		List followedLinkList = linkHandler.getFollowedLinkList(templateId,
				nodeId);
		return getFollowedTaskNodeList(templateId, followedLinkList);
	}

	public List getFollowedTaskNodeList(int templateId, int nodeId,
			String action) throws WorkflowException {
		Link linkHandler = new Link();
		List followedLinkList = linkHandler.getFollowedLinkList(templateId,
				nodeId, action);
		return getFollowedTaskNodeList(templateId, followedLinkList);
	}

	public Map getFollowedTaskNodeActionMap(int templateId, int nodeId)
			throws WorkflowException {
		Map result = new HashMap();

		Link handler = new Link();
		Map linkActionMap = handler
				.getFollowedLinkActionMap(templateId, nodeId);
		if (linkActionMap.size() > 0) {
			Object[] actions = linkActionMap.keySet().toArray();
			for (int i = 0; i < actions.length; i++) {
				List linkList = (List) linkActionMap.get(actions[i]);
				List nodeList = getFollowedTaskNodeList(templateId, linkList);
				result.put(actions[i], nodeList);
			}
		}
		return result;
	}

	private List getFollowedTaskNodeList(int templateId, List linkList)
			throws WorkflowException {
		List result = new ArrayList();

		for (int i = 0; i < linkList.size(); i++) {
			Link link = (Link) linkList.get(i);
			int followedNodeId = link.getNextNodeId();
			if (followedNodeId == -2)
				continue;
			NodeMeta followedNode = getNode(followedNodeId);
			switch (Integer.parseInt(followedNode.getType())) {
			case 2: // '\002'任务节点
				result.add(followedNode);
				break;
			case 3: // '\003'分支节点
				result.addAll(getFollowedTaskNodeListByNavigation(templateId,
						followedNodeId));
				break;
			case 4: // '\004'事件触发节点
				break;
			case 5: // '\005'与节点
				result.addAll(getFollowedTaskNodeListByAO(templateId,
						followedNodeId));
				break;
			case 6: // '\006'或节点
				result.addAll(getFollowedTaskNodeListByAO(templateId,
						followedNodeId));
				break;

			default:
				throw new WorkflowException(1220);
			}
		}

		return result;
	}

	private List getFollowedTaskNodeListByNavigation(int templateId, int nodeId)
			throws WorkflowException {
		List result = new ArrayList();

		Link linkHandler = new Link();
		List followedLinkList = linkHandler.getFollowedLinkList(templateId,
				nodeId);
		for (int i = 0; i < followedLinkList.size(); i++) {
			Link link = (Link) followedLinkList.get(i);
			int followedNodeId = link.getNextNodeId();
			if (followedNodeId == -2)
				continue;
			NodeMeta followedNode = getNode(followedNodeId);
			switch (Integer.parseInt(followedNode.getType())) {
			case 2: // '\002'任务节点
				result.add(followedNode);
				break;
			case 3: // '\003'分支节点
				result.addAll(getFollowedTaskNodeListByNavigation(templateId,
						followedNodeId));
				break;
			case 4: // '\004'事件触发节点
				break;
			case 5: // '\005'与节点
				break;
			case 6: // '\006'或节点
				break;
			default:
				throw new WorkflowException(1220);
			}
		}
		return result;
	}

	private List getFollowedTaskNodeListByAO(int templateId, int nodeId)
			throws WorkflowException {
		List result = new ArrayList();

		Link linkHandler = new Link();
		List followedLinkList = linkHandler.getFollowedLinkList(templateId,
				nodeId);
		for (int i = 0; i < followedLinkList.size(); i++) {
			Link link = (Link) followedLinkList.get(i);
			int followedNodeId = link.getNextNodeId();
			if (followedNodeId == -2)
				continue;
			NodeMeta followedNode = getNode(followedNodeId);
			switch (Integer.parseInt(followedNode.getType())) {
			case 2: // '\002'任务节点
				result.add(followedNode);
				break;
			case 3: // '\003'分支节点
				break;
			case 4: // '\004'事件触发节点
				break;
			case 5: // '\005'与节点
				result.addAll(getFollowedTaskNodeListByAO(templateId,
						followedNodeId));
				break;
			case 6: // '\006'或节点
				result.addAll(getFollowedTaskNodeListByAO(templateId,
						followedNodeId));
				break;
			default:
				throw new WorkflowException(1220);
			}
		}
		return result;
	}

	public void create(NodeMeta meta) throws WorkflowException {
		try {
			NodeBean bean = new NodeBean();
			meta.setId(Sequence.fetch(Sequence.SEQ_NODE));
			if (bean.insert(unwrap(meta)) != 1)
				throw new WorkflowException(2002);
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}
	}

	public NodeMeta getNode(int nodeId) throws WorkflowException {
		NodeMeta result = new NodeMeta();
		NodeQuery query = new NodeQuery();
		try {
			if (nodeId < 0) {
				result = new NodeMeta(nodeId);
			} else {
				result = wrap(query.getNode(nodeId));
			}
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}
		return result;
	}

	public void remove(int nodeId) throws WorkflowException {
		NodeState stateHandler = new NodeState();
		stateHandler.removeByNode(nodeId);

		Executor executorHandler = new Executor();
		executorHandler.removeByNode(nodeId);

		Link linkHandler = new Link();
		linkHandler.removeByNode(nodeId);

		try {
			NodeBean bean = new NodeBean();
			bean.delete(nodeId);
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}
	}

	public void update(NodeMeta node) throws WorkflowException {
		try {
			if (!EXECUTORS_METHOD_SERIAL.equals(node.getExecutorsMethod())) {
				Executor handler = new Executor();
				handler.resetOrderAsNonSerial(node.getId());
			}

			NodeBean bean = new NodeBean();
			if (bean.update(unwrap(node)) != 1)
				throw new WorkflowException(2004);
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}
	}

	public List getWholeFollowedTaskNodeList(int templateId, int nodeId)
			throws WorkflowException {
		List result = new ArrayList();

		Link linkHandler = new Link();
		List followedLinkList = linkHandler.getFollowedLinkList(templateId,
				nodeId);
		for (int i = 0; i < followedLinkList.size(); i++) {
			int followedNodeId = ((Link) followedLinkList.get(i))
					.getNextNodeId();
			NodeMeta followedNode = getNode(followedNodeId);
			if (followedNodeId == -2) {// 结束节点
				break;
			}
			switch (Integer.parseInt(followedNode.getType())) {
			case 2: // '\002'任务节点
				result.add(followedNode);
				break;
			case 3: // '\003'分支节点
				result.addAll(getWholeFollowedTaskNodeList(templateId,
						followedNodeId));
				break;
			case 4: // '\004'事件触发节点
				break;
			case 5: // '\005'与节点
				result.addAll(getWholeFollowedTaskNodeList(templateId,
						followedNodeId));
				break;
			case 6: // '\006'或节点
				result.addAll(getWholeFollowedTaskNodeList(templateId,
						followedNodeId));
				break;
			default:
				throw new WorkflowException(1220);
			}
		}
		return result;
	}

	public List getPrecedingNodeList(int templateId, int nodeId)
			throws WorkflowException {
		List result = new ArrayList();

		Link linkHandler = new Link();
		List precedingLinkList = linkHandler.getPrecedingLinkList(templateId,
				nodeId);
		for (int i = 0; i < precedingLinkList.size(); i++) {
			Link link = (Link) precedingLinkList.get(i);
			NodeMeta precedingNode = getNode(link.getCurrentNodeId());
			result.add(precedingNode);
		}
		return result;
	}

	public NodeMeta getStartNode(int templateId) throws WorkflowException {
		NodeMeta result = null;

		NodeQuery bean = new NodeQuery();
		try {
			result = wrap(bean.getStartNode(templateId));
		} catch (SQLException sqle) {
			throw new WorkflowException(1200, sqle.toString());
		}
		if (result == null || result.getId() == 0) {
			throw new WorkflowException(1207);
		}

		return result;
	}

	/**
	 * 取得员工可执行的任务节点
	 * 
	 * @param executor
	 *            int
	 * @param conn
	 *            Connection
	 * @throws WorkflowException
	 * @return List
	 */
	public List getTaskNodeListByExecutor(String executor)
			throws WorkflowException {
		List result = new ArrayList();

		try {
			NodeQuery bean = new NodeQuery();
			ArrayList list = bean.getNodeListByStaff(executor);
			NodeMeta node = null;
			for (int i = 0; i < list.size(); i++, result.add(node)) {
				node = wrap((NodeInfo) list.get(i));
			}
		} catch (SQLException e) {
			throw new WorkflowException(2055, e.toString());
		}
		return result;
	}

	public List getTaskNodeListByExecutor(String executor, String templateType)
			throws WorkflowException {
		List result = new ArrayList();

		try {
			NodeQuery bean = new NodeQuery();
			ArrayList list = bean.getNodeListByStaff(executor, templateType);
			NodeMeta node = null;
			for (int i = 0; i < list.size(); i++, result.add(node)) {
				node = wrap((NodeInfo) list.get(i));
			}
		} catch (SQLException e) {
			throw new WorkflowException(2055, e.toString());
		}
		return result;
	}

	private boolean isBetweenNode(int templateId, int startNodeId,
			int endNodeId, int fromNodeId, int toNodeId, Set tempSet,
			Set resultSet) throws WorkflowException {
		boolean result = false;
		Link linkHandler = new Link();
		List precedingLinkList = linkHandler.getPrecedingLinkList(templateId,
				toNodeId);
		for (int i = 0; i < precedingLinkList.size(); i++) {
			Link link = (Link) precedingLinkList.get(i);
			if ("1".equals(link.getType()))
				continue;
			if (link.getCurrentNodeId() == endNodeId) {
				result = true;
				continue;
			}
			if (link.getCurrentNodeId() == fromNodeId) {
				if (link.getNextNodeId() > 0
						&& link.getNextNodeId() != endNodeId)
					resultSet.add(getNode(link.getNextNodeId()));
				if (link.getCurrentNodeId() > 0
						&& link.getCurrentNodeId() != link.getNextNodeId())
					isBetweenNode(templateId, startNodeId, endNodeId, link
							.getCurrentNodeId(), link.getCurrentNodeId(),
							(new HashSet()), resultSet);
				result = true;
				continue;
			}
			if (link.getCurrentNodeId() < 0)
				return false;
			NodeMeta currentNode = getNode(link.getCurrentNodeId());
			if (resultSet.contains(currentNode)) {
				if (link.getNextNodeId() > 0
						&& link.getNextNodeId() != endNodeId)
					resultSet.add(getNode(link.getNextNodeId()));
				result = true;
				continue;
			}
			if (tempSet.contains(currentNode))
				continue;
			tempSet.add(currentNode);
			if (!isBetweenNode(templateId, startNodeId, endNodeId, fromNodeId,
					link.getCurrentNodeId(), tempSet, resultSet))
				continue;
			result = true;
			resultSet.add(getNode(link.getCurrentNodeId()));
			if (link.getNextNodeId() > 0
					&& link.getCurrentNodeId() != endNodeId
					&& link.getNextNodeId() != endNodeId)
				resultSet.add(getNode(link.getNextNodeId()));
		}

		return result;
	}

	public Set getNodeListBetween(int templateId, int fromNodeId, int toNodeId)
			throws WorkflowException {
		Set resultSet = new HashSet();
		Set tempSet = new HashSet();
		isBetweenNode(templateId, fromNodeId, toNodeId, fromNodeId, toNodeId,
				tempSet, resultSet);
		if (fromNodeId > 0)
			resultSet.remove(getNode(fromNodeId));
		if (toNodeId > 0)
			resultSet.remove(getNode(toNodeId));
		return resultSet;
	}

	public void removeByTemplate(int templateId) throws WorkflowException {
		NodeState stateHandler = new NodeState();
		stateHandler.removeByTemplate(templateId);

		Executor executorHandler = new Executor();
		executorHandler.removeByTemplate(templateId);

		Link linkHandler = new Link();
		linkHandler.removeByTemplate(templateId);

		try {
			NodeBean bean = new NodeBean();
			bean.removeByTemplate(templateId);
		} catch (SQLException e) {
			throw new WorkflowException(2000, e.toString());
		}
	}

	private NodeMeta wrap(NodeInfo model) {
		NodeMeta meta = new NodeMeta();
		if (model.getNodeId() != null)
			meta.setId(model.getNodeId().intValue());
		meta.setName(model.getName());
		meta.setDescription(model.getDescription());
		meta.setType(model.getType());
		if (model.getTemplateId() != null)
			meta.setTemplateId(model.getTemplateId().intValue());
		meta.setTemplateName(model.getTemplateName());
		meta.setExecutorsMethod(model.getExecutorsMethod());
		meta.setTaskListener(model.getTaskListener());
		meta.setBusinessType(model.getBusinessType());
		if (model.getLimitExecuteTerm() != null)
			meta.setLimitExecuteTerm(model.getLimitExecuteTerm().intValue());
		if (model.getRemindExecuteTerm() != null)
			meta.setRemindExecuteTerm(model.getRemindExecuteTerm().intValue());
		return meta;
	}

	private NodeModel unwrap(NodeMeta meta) {
		NodeModel model = new NodeModel();
		if (meta.getId() != 0)
			model.setNodeId(meta.getId());
		if (meta.getName() != null)
			model.setName(meta.getName());
		if (meta.getDescription() != null)
			model.setDescription(meta.getDescription());
		if (meta.getType() != null)
			model.setType(meta.getType());
		if (meta.getTemplateId() != 0)
			model.setTemplateId(meta.getTemplateId());
		if (meta.getExecutorsMethod() != null)
			model.setExecutorsMethod(meta.getExecutorsMethod());
		// if (meta.getTaskListener() != null)
		model.setTaskListener(meta.getTaskListener());
		if (meta.getBusinessType() != null)
			model.setBusinessType(meta.getBusinessType());
		if (meta.getLimitExecuteTerm() != 0)
			model.setLimitExecuteTerm(meta.getLimitExecuteTerm());
		if (meta.getRemindExecuteTerm() != 0)
			model.setRemindExecuteTerm(meta.getRemindExecuteTerm());
		return model;
	}

}
