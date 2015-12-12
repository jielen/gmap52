package com.kingdrive.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.kingdrive.workflow.business.Company;
import com.kingdrive.workflow.business.Executor;
import com.kingdrive.workflow.business.Link;
import com.kingdrive.workflow.business.LinkState;
import com.kingdrive.workflow.business.Node;
import com.kingdrive.workflow.business.NodeState;
import com.kingdrive.workflow.business.Org;
import com.kingdrive.workflow.business.OrgPosition;
import com.kingdrive.workflow.business.Position;
import com.kingdrive.workflow.business.Role;
import com.kingdrive.workflow.business.Staff;
import com.kingdrive.workflow.business.State;
import com.kingdrive.workflow.business.Template;
import com.kingdrive.workflow.business.Variable;
import com.kingdrive.workflow.dto.NodeMeta;
import com.kingdrive.workflow.dto.TemplateMeta;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.exception.WorkflowException;

public class ConfigureFacade{

	public ConfigureFacade(){
	}

	public static List getTemplateList() throws WorkflowException{
		Template templateHandler = new Template();
		return templateHandler.getTemplateList();
	}

	public static List getTemplateList(String templateType) throws WorkflowException{
		Template templateHandler = new Template();
		return templateHandler.getTemplateList(templateType);
	}

	public static List getActiveTemplateList(String templateType) throws WorkflowException{
		Template templateHandler = new Template();
		return templateHandler.getActiveTemplateList(templateType);
	}

	public static void addTemplate(TemplateMeta template) throws WorkflowException{
		Template templateHandler = new Template();
		templateHandler.create(template);
	}

	public static TemplateMeta getTemplate(int templateId) throws WorkflowException{
		Template templateHandler = new Template();
		return templateHandler.getTemplate(templateId);
	}

	public static void removeTemplate(int templateId) throws WorkflowException{
		Template templateHandler = new Template();
		templateHandler.remove(templateId);
	}

	public static void updateTemplate(TemplateMeta template) throws WorkflowException{
		Template templateHandler = new Template();
		templateHandler.update(template);
	}

	public static List getNodeList(int templateId) throws WorkflowException{
		Node nodeHandler = new Node();
		return nodeHandler.getNodeList(templateId);
	}

	public static List getFollowedNodeList(int templateId, int nodeId, int followedNodeId) throws WorkflowException{
		Node nodeHandler = new Node();
		return nodeHandler.getFollowedNodeList(templateId, nodeId, followedNodeId);
	}

	public static List getTaskNodeList(int templateId) throws WorkflowException{
		Node nodeHandler = new Node();
		return nodeHandler.getTaskNodeList(templateId);
	}

	public static NodeMeta getNode(int nodeId) throws WorkflowException{
		Node nodeHandler = new Node();
		return nodeHandler.getNode(nodeId);
	}

	public static NodeMeta getStartNode(int templateId) throws WorkflowException{
		Node nodeHandler = new Node();
		return nodeHandler.getStartNode(templateId);
	}

	public static void removeNode(int nodeId) throws WorkflowException{
		Node nodeHandler = new Node();
		nodeHandler.remove(nodeId);
	}

	public static void addNode(NodeMeta node) throws WorkflowException{
		Node nodeHandler = new Node();
		nodeHandler.create(node);
	}

	public static void updateNode(NodeMeta node) throws WorkflowException{
		Node nodeHandler = new Node();
		nodeHandler.update(node);
	}

	public static List getLinkList(int templateId) throws WorkflowException{
		Link linkHandler = new Link();
		return linkHandler.getLinkList(templateId);
	}

	public static List getFollowedLinkList(int templateId, int nodeId) throws WorkflowException{
		Link linkHandler = new Link();
		return linkHandler.getFollowedLinkList(templateId, nodeId);
	}

	public static List getPrecedingLinkList(int templateId, int nodeId) throws WorkflowException{
		Link linkHandler = new Link();
		return linkHandler.getPrecedingLinkList(templateId, nodeId);
	}

	public static void removeLink(int linkId) throws WorkflowException{
		Link linkHandler = new Link();
		linkHandler.remove(linkId);
	}

	public static void addLink(Link link) throws WorkflowException{
		Link handler = new Link();
		handler.create(link);
	}

	public static Link getLink(int linkId) throws WorkflowException{
		Link linkHandler = new Link();
		return linkHandler.getLink(linkId);
	}

	public static List getExecutorListByOrder(int nodeId) throws WorkflowException{
		Executor handler = new Executor();
		return handler.getExecutorListByOrder(nodeId);
	}

	public static void resetExecutorOrder(int nodeId, String executeMethod) throws WorkflowException{
		Executor handler = new Executor();
		handler.resetOrder(nodeId);
	}

	public static void resetExecutorOrder(int nodeId, String[] executor, int[] executorOrder, int[] responsibility) throws WorkflowException{
		Executor handler = new Executor();
		handler.resetOrder(nodeId, executor, executorOrder, responsibility);
	}

	public static Set getActionSet(int templateId, int nodeId) throws WorkflowException{
		Node handler = new Node();
		return handler.getActionSet(templateId, nodeId);
	}
	public static List getActionList(int templateId, int nodeId) throws WorkflowException{
		Node handler = new Node();
		return handler.getActionList(templateId, nodeId);
	}
	

	public static String getDefaultAction(int templateId, int nodeId) throws WorkflowException{
		Node handler = new Node();
		return handler.getDefaultActionName(templateId, nodeId);
	}

	public static Set getActionSet(String templateType, String businessType, Connection conn) throws WorkflowException{
		Node handler = new Node();
		return handler.getActionSet(templateType, businessType);
	}

	public static Set getBusinessSet(String templateType) throws WorkflowException{
		Node handler = new Node();
		return handler.getBusinessSet(templateType);
	}

	public static Set getBusinessSet(int templateId) throws WorkflowException{
		Node handler = new Node();
		return handler.getBusinessSet(templateId);
	}

	public static void setLinkExpression(int linkId, String expression, String isDefault) throws WorkflowException{
		Link linkHandler = new Link();
		linkHandler.setLinkExpression(linkId, expression, isDefault);
	}

	public static void addVariable(VariableMeta var) throws WorkflowException{
		Variable variableHandler = new Variable();
		variableHandler.create(var);
	}

	public static void removeVariable(int varId) throws WorkflowException{
		Variable variableHandler = new Variable();
		variableHandler.remove(varId);
	}

	public static void updateVariable(VariableMeta var) throws WorkflowException{
		Variable variableHandler = new Variable();
		variableHandler.update(var);
	}

	public static VariableMeta getVariable(int varId) throws WorkflowException{
		Variable variableHandler = new Variable();
		return variableHandler.getVariable(varId);
	}

	public static List getVariableList(int templateId) throws WorkflowException{
		Variable variableHandler = new Variable();
		return variableHandler.getVariableListByTemplate(templateId);
	}

	public static List getStaffPositionList(String staffId) throws WorkflowException{
		Staff staffHandler = new Staff();
		return staffHandler.getStaffPositionList(staffId);
	}

	public static void resetStaffPosition(String staffId, String[] orgPositionId) throws WorkflowException{
		Staff staffHandler = new Staff();
		staffHandler.resetStaffPosition(staffId, orgPositionId);
	}

	public static void addStaff(Staff staff) throws WorkflowException{
		Staff staffHandler = new Staff();
		staffHandler.create(staff);
	}

	public static void removeStaff(String staffId) throws WorkflowException{
		Staff staffHandler = new Staff();
		staffHandler.delete(staffId);
	}

	public static void updateStaff(Staff staff) throws WorkflowException{
		staff.update();
	}

	public static Staff getStaff(String staffId) throws WorkflowException{
		Staff staffHandler = new Staff();
		return staffHandler.getStaff(staffId);
	}

	public static List getStaffList() throws WorkflowException{
		Staff staffHandler = new Staff();
		return staffHandler.getStaffList();
	}

	public static void addOrganization(Org org) throws WorkflowException{
		Org organizationHandler = new Org();
		organizationHandler.create(org);
	}

	public static Org getOrganization(String orgId) throws WorkflowException{
		Org organizationHandler = new Org();
		return organizationHandler.getOrganization(orgId);
	}

	public static void updateOrganization(Org org) throws WorkflowException{
		Org organizationHandler = new Org();
		organizationHandler.update(org);
	}

	public static void removeOrganization(String orgId) throws WorkflowException{
		Org organizationHandler = new Org();
		organizationHandler.delete(orgId);
	}

	public static List getOrganizationList() throws WorkflowException{
		Org organizationHandler = new Org();
		return organizationHandler.getOrganizationList();
	}

	public static List getOrgListByCompany(String companyId) throws WorkflowException{
		Org organizationHandler = new Org();
		return organizationHandler.getOrgListByCompany(companyId);
	}

	public static OrgPosition getOrgPosition(String posId) throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		return positionHandler.getOrgPosition(posId);
	}

	public static List getOrgPositionList() throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		return positionHandler.getOrgPositionList();
	}

	public static List getOrgPositionListByOrg(String organizationId, Connection conn) throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		return positionHandler.getOrgPositionListByOrg(organizationId);
	}

	public static List getOrgPositionListByStaff(String staffId) throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		return positionHandler.getOrgPositionListByStaff(staffId);
	}

	public static List getSuperOrgPositionList(String orgPositionId) throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		return positionHandler.getSuperOrgPositionList(orgPositionId);
	}

	public static void resetOrgPosition(String organizationId, String[] positionId) throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		positionHandler.reset(organizationId, positionId);
	}

	public static void resetSuperOrgPosition(String orgPositionId, String parentId[]) throws WorkflowException{
		OrgPosition positionHandler = new OrgPosition();
		positionHandler.resetSuper(orgPositionId, parentId);
	}

	public static void addStaffPosition(String staffId, String positionId, String orgId) throws WorkflowException{
		Staff staffHandler = new Staff();
		staffHandler.createStaffPosition(staffId, positionId, orgId);
	}

	public static boolean login(String staffId, String password, boolean encoded) throws WorkflowException{
		Staff handler = new Staff();
		return handler.login(staffId, password, encoded);
	}

	public static void updateLink(Link link) throws WorkflowException{
		link.update();
	}

	public static void addState(State state) throws WorkflowException{
		state.create(state);
	}

	public static void updateState(State state) throws WorkflowException{
		state.update(state);
	}

	public static void removeState(int stateId) throws WorkflowException{
		State stateHandler = new State();
		stateHandler.remove(stateId);
	}

	public static State getState(int stateId) throws WorkflowException{
		State stateHandler = new State();
		return stateHandler.getState(stateId);
	}

	public static List getStateList(int templateId) throws WorkflowException{
		State stateHandler = new State();
		return stateHandler.getStateListByTemplate(templateId);
	}

	public static void resetLinkState(int linkId, int[] stateId, String[] stateValue) throws WorkflowException{
		LinkState linkStateHandler = new LinkState();
		linkStateHandler.reset(linkId, stateId, stateValue);
	}

	public static List getLinkStateList(int linkId) throws WorkflowException{
		LinkState linkStateHandler = new LinkState();
		return linkStateHandler.getStateListByLink(linkId);
	}

	public static void resetNodeState(int nodeId, int[] stateId, String[] stateValue) throws WorkflowException{
		NodeState nodeStateHandler = new NodeState();
		nodeStateHandler.reset(nodeId, stateId, stateValue);
	}

	public static List getNodeStateList(int nodeId) throws WorkflowException{
		NodeState nodeStateHandler = new NodeState();
		return nodeStateHandler.getStateListByNode(nodeId);
	}

	public static Company getCompany(String companyId) throws WorkflowException{
		Company com = new Company();
		return com.getCompany(companyId);
	}

	public static List getCompanyList() throws WorkflowException{
		Company com = new Company();
		return com.getCompanyList();
	}

	public static void removeCompany(String companyId) throws WorkflowException{
		Company com = new Company();
		com.delete(companyId);
	}

	public static void addCompany(Company meta) throws WorkflowException{
		Company com = new Company();
		com.insert(meta);
	}

	public static void updateCompany(Company meta) throws WorkflowException{
		Company com = new Company();
		com.update(meta);
	}

	public static Position getPosition(String positionId) throws WorkflowException{
		Position position = new Position();
		return position.getPosition(positionId);
	}

	public static List getPositionList() throws WorkflowException{
		Position position = new Position();
		return position.getPositionList();
	}

	public static List getPositionListByRole(String roleId) throws WorkflowException{
		Position position = new Position();
		return position.getPositionListByRole(roleId);
	}

	public static void removePosition(String positionId) throws WorkflowException{
		Position position = new Position();
		position.delete(positionId);
	}

	public static void resetPositionRole(String positionId, String[] roleId) throws WorkflowException{
		Position position = new Position();
		position.resetPositionRole(positionId, roleId);
	}

	public static void addPosition(Position meta) throws WorkflowException{
		Position position = new Position();
		position.insert(meta);
	}

	public static void updatePosition(Position meta) throws WorkflowException{
		Position position = new Position();
		position.update(meta);
	}

	public static Role getRole(String roleId) throws WorkflowException{
		Role role = new Role();
		return role.getRole(roleId);
	}

	public static List getRoleList() throws WorkflowException{
		Role role = new Role();
		return role.getRoleList();
	}

	public static List getRoleListByPosition(String positionId) throws WorkflowException{
		Role role = new Role();
		return role.getRoleListByPosition(positionId);
	}

	public static void removeRole(String roleId) throws WorkflowException{
		Role role = new Role();
		role.delete(roleId);
	}

	public static void resetRolePosition(String roleId, String[] positionId) throws WorkflowException{
		Role role = new Role();
		role.resetRolePosition(roleId, positionId);
	}

	public static void addRole(Role meta) throws WorkflowException{
		Role role = new Role();
		role.insert(meta);
	}

	public static void updateRole(Role meta) throws WorkflowException{
		Role role = new Role();
		role.update(meta);
	}

	public static List getCompanyListByExecutor(int nodeId, int responsibility) throws WorkflowException{
		Company com = new Company();
		return com.getCompanyListByExecutor(nodeId, responsibility);
	}

	public static List getCompanyListByNonExecutor(int nodeId) throws WorkflowException{
		Company com = new Company();
		return com.getCompanyListByNonExecutor(nodeId);
	}

	public static List getOrgListByExecutor(int nodeId, int responsibility) throws WorkflowException{
		Org org = new Org();
		return org.getOrgListByExecutor(nodeId, responsibility);
	}

	public static List getOrgListByNonExecutor(int nodeId) throws WorkflowException{
		Org org = new Org();
		return org.getOrgListByNonExecutor(nodeId);
	}

	public static List getRoleListByExecutor(int nodeId, int responsibility) throws WorkflowException{
		Role role = new Role();
		return role.getRoleListByExecutor(nodeId, responsibility);
	}

	public static List getRoleListByNonExecutor(int nodeId) throws WorkflowException{
		Role role = new Role();
		return role.getRoleListByNonExecutor(nodeId);
	}

	public static List getPositionListByExecutor(int nodeId, int responsibility) throws WorkflowException{
		Position position = new Position();
		return position.getPositionListByExecutor(nodeId, responsibility);
	}

	public static List getPositionListByNonExecutor(int nodeId) throws WorkflowException{
		Position position = new Position();
		return position.getPositionListByNonExecutor(nodeId);
	}

	public static List getStaffListByExecutor(int nodeId, int responsibility) throws WorkflowException{
		Staff staff = new Staff();
		return staff.getStaffListByExecutor(nodeId, responsibility);
	}

	public static List getStaffListByNonExecutor(int nodeId) throws WorkflowException{
		Staff staff = new Staff();
		return staff.getStaffListByNonExecutor(nodeId);
	}

	public static void resetExecutorSource(int nodeId, String executor[], int source, int responsibility) throws WorkflowException{
		Executor handler = new Executor();
		handler.resetSource(nodeId, executor, source, responsibility);
	}

	public static List getPositionListByOrg(String organizationId) throws WorkflowException{
		Position position = new Position();
		return position.getPositionListByOrg(organizationId);
	}

}
