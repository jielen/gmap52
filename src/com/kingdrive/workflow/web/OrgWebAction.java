package com.kingdrive.workflow.web;

import java.util.List;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;
import com.kingdrive.workflow.ConfigureFacade;
import com.kingdrive.workflow.business.Company;
import com.kingdrive.workflow.business.Org;
import com.kingdrive.workflow.business.OrgPosition;
import com.kingdrive.workflow.business.Position;
import com.kingdrive.workflow.business.Role;
import com.kingdrive.workflow.business.Staff;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.util.StringUtil;

public class OrgWebAction extends WebAction {

  public OrgWebAction() {
  }

  public String perform(String event) throws GeneralException {
    if ("login".equals(event)) {
      return login();
    }
    if ("listcompany".equals(event)) {
      return listCompany();
    }
    if ("getcompany".equals(event)) {
      return getCompany();
    }
    if ("addcompany".equals(event)) {
      return addCompany();
    }
    if ("modifycompany".equals(event)) {
      return modifyCompany();
    }
    if ("deletecompany".equals(event)) {
      return removeCompany();
    }
    if ("listposition".equals(event)) {
      return listPosition();
    }
    if ("getposition".equals(event)) {
      return getPosition();
    }
    if ("addposition".equals(event)) {
      return addPosition();
    }
    if ("modifyposition".equals(event)) {
      return modifyPosition();
    }
    if ("deleteposition".equals(event)) {
      return removePosition();
    }
    if ("listpositionrole".equals(event)) {
      return listPositionRole();
    }
    if ("resetpositionrole".equals(event)) {
      return resetPositionRole();
    }
    if ("listrole".equals(event)) {
      return listRole();
    }
    if ("getrole".equals(event)) {
      return getRole();
    }
    if ("addrole".equals(event)) {
      return addRole();
    }
    if ("modifyrole".equals(event)) {
      return modifyRole();
    }
    if ("deleterole".equals(event)) {
      return removeRole();
    }
    if ("listroleposition".equals(event)) {
      return listRolePosition();
    }
    if ("resetroleposition".equals(event)) {
      return resetRolePosition();
    }
    if ("listorg".equals(event)) {
      return listOrg();
    }
    if ("getorg".equals(event)) {
      return getOrg();
    }
    if ("addorg".equals(event)) {
      return addOrg();
    }
    if ("modifyorg".equals(event)) {
      return modifyOrg();
    }
    if ("deleteorg".equals(event)) {
      return removeOrg();
    }
    if ("listorgposition".equals(event)) {
      return getPositionListByOrg();
    }
    if ("resetorgposition".equals(event)) {
      return resetOrgPosition();
    }
    if ("listorgpositionparent".equals(event)) {
      return listSuperOrgPosition();
    }
    if ("resetorgpositionparent".equals(event)) {
      return resetSuperOrgPosition();
    }
    if ("liststaff".equals(event)) {
      return listStaff();
    }
    if ("getstaff".equals(event)) {
      return getStaff();
    }
    if ("addstaff".equals(event)) {
      return addStaff();
    }
    if ("modifystaff".equals(event)) {
      return modifyStaff();
    }
    if ("deletestaff".equals(event)) {
      return removeStaff();
    }
    if ("liststaffposition".equals(event)) {
      return listStaffPosition();
    }
    if ("resetstaffposition".equals(event)) {
      return resetStaffPosition();
    }
    throw new GeneralException("未知功能。");
  }

  private String listOrg() throws GeneralException {
    List result;

    String companyid = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "companyid"));

    try {
      result = ConfigureFacade.getOrgListByCompany(companyid);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "orglist", result);
    return null;
  }

  private String getOrg() throws GeneralException {
    String orgId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgid"));

    Org org;
    try {
      org = ConfigureFacade.getOrganization(orgId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "orginfo", org);
    return null;
  }

  private String modifyOrg() throws GeneralException {
    String orgId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "orgname"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgdesc"));
    String companyId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "companyid"));
    String parentId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgparent"));
    ;

    Org org = new Org();
    org.setId(orgId);
    org.setName(name);
    org.setDescription(description);
    org.setCompanyId(companyId);
    org.setParentId(parentId);

    try {
      ConfigureFacade.updateOrganization(org);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "修改组织成功！");
    return null;
  }

  private String addOrg() throws GeneralException {
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "orgname"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgdesc"));
    String companyId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "companyid"));
    String parentId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgparent"));
    ;

    Org org = new Org();
    org.setName(name);
    org.setDescription(description);
    org.setCompanyId(companyId);
    org.setParentId(parentId);

    try {
      ConfigureFacade.addOrganization(org);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建组织成功！");
    return null;
  }

  private String removeOrg() throws GeneralException {
    String orgId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgid"));

    try {
      ConfigureFacade.removeOrganization(orgId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除组织成功！");
    return null;
  }

  private String resetOrgPosition() throws GeneralException {
    String orgId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgid"));
    String[] positionId = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "positionid"));
    try {
      ConfigureFacade.resetOrgPosition(orgId, positionId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置组织职位成功！");
    return null;
  }

  private String listSuperOrgPosition() throws GeneralException {
    String orgPosId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgposid"));

    OrgPosition op;
    List list;
    List superlist;
    try {
      op = ConfigureFacade.getOrgPosition(orgPosId);
      list = ConfigureFacade.getOrgPositionList();
      superlist = ConfigureFacade.getSuperOrgPositionList(orgPosId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "orgposition", op);
    tps.put(SessionManager.TDS, "orgpositionlist", list);
    tps.put(SessionManager.TDS, "superorgpositionlist", superlist);
    return null;
  }

  private String resetSuperOrgPosition() throws GeneralException {
    String orgPosId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgposid"));
    String parentId[] = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "parentid"));
    try {
      ConfigureFacade.resetSuperOrgPosition(orgPosId, parentId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置上级组织职位成功！");
    return null;
  }

  private String listStaff() throws GeneralException {
    List list;
    try {
      list = ConfigureFacade.getStaffList();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "stafflist", list);
    return null;
  }

  private String getStaff() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffid"));

    Staff staff;
    try {
      staff = ConfigureFacade.getStaff(staffId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "staffinfo", staff);
    return null;
  }

  private String modifyStaff() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "staffname"));
    String password = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffpass"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffdesc"));
    String email = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffemail"));

    Staff staff = new Staff();
    staff.setId(staffId);
    staff.setName(name);
    staff.setPassword(password.trim());
    staff.setDescription(description);
    staff.setEmail(email);

    try {
      ConfigureFacade.updateStaff(staff);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "修改员工成功！");
    return null;
  }

  private String addStaff() throws GeneralException {
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "staffname"));
    String password = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffpass"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffdesc"));
    String email = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffemail"));

    Staff staff = new Staff();
    staff.setName(name);
    staff.setPassword(password);
    staff.setDescription(description);
    staff.setEmail(email);

    staff.setStatus("0");
    try {
      ConfigureFacade.addStaff(staff);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建员工成功！");
    return null;
  }

  private String removeStaff() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffid"));
    try {
      ConfigureFacade.removeStaff(staffId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除员工成功！");
    return null;
  }

  private String listStaffPosition() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffid"));

    Staff staff;
    List staffpositionlist;
    List orgpositionlist;
    try {
      staff = ConfigureFacade.getStaff(staffId);
      orgpositionlist = ConfigureFacade.getOrgPositionList();
      staffpositionlist = ConfigureFacade.getOrgPositionListByStaff(staffId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }

    tps.put(SessionManager.TDS, "staff", staff);
    tps.put(SessionManager.TDS, "staffpositionlist", staffpositionlist);
    tps.put(SessionManager.TDS, "orgpositionlist", orgpositionlist);
    return null;
  }

  private String resetStaffPosition() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "staffid"));
    String[] orgPositionId = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "orgpositionid"));
    try {
      ConfigureFacade.resetStaffPosition(staffId, orgPositionId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置员工任职成功！");
    return null;
  }

  private String login() throws GeneralException {
    String staffId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "user_id"));
    // String password =
    // StringUtil.string2string((String)tps.get(SessionManager.PDS,
    // "password"));
    String password = (String) tps.get(SessionManager.PDS, "password");
    String isencoding = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "isencoding"));

    boolean encoded = false;
    if (isencoding != null && "true".equals(isencoding)) {
      encoded = true;
    }
    // System.out.println("encoded" + encoded);
    try {
      if (!ConfigureFacade.login(staffId, password, encoded)) {
        return "LOGINFAIL";
      }
      Staff staff = ConfigureFacade.getStaff(staffId);
      tps.put(SessionManager.TDS, "staffinfo", staff);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    return null;
  }

  private String addCompany() throws GeneralException {
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "name"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "description"));
    String parentid = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "parentid"));

    Company com = new Company();
    com.setName(name);
    com.setDescription(description);
    com.setParentId(parentid);

    try {
      ConfigureFacade.addCompany(com);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建单位成功！");
    return null;
  }

  private String removeCompany() throws GeneralException {
    String companyId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "companyid"));
    try {
      ConfigureFacade.removeCompany(companyId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除单位成功！");
    return null;
  }

  private String modifyCompany() throws GeneralException {
    String companyId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "companyid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "name"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "description"));
    String parentid = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "parentid"));

    Company com = new Company();
    com.setId(companyId);
    com.setName(name);
    com.setDescription(description);
    com.setParentId(parentid);

    try {
      ConfigureFacade.updateCompany(com);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "修改单位成功！");
    return null;
  }

  private String getCompany() throws GeneralException {
    String companyId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "companyid"));

    Company com;
    try {
      com = ConfigureFacade.getCompany(companyId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "companyinfo", com);
    return null;
  }

  private String listCompany() throws GeneralException {
    List list;
    try {
      list = ConfigureFacade.getCompanyList();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "companylist", list);
    return null;
  }

  private String addPosition() throws GeneralException {
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "name"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "description"));

    Position position = new Position();
    position.setName(name);
    position.setDescription(description);
    try {
      ConfigureFacade.addPosition(position);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建职位成功！");
    return null;
  }

  private String removePosition() throws GeneralException {
    String positionId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "positionid"));
    try {
      ConfigureFacade.removePosition(positionId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除职位成功！");
    return null;
  }

  private String resetPositionRole() throws GeneralException {
    String positionId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "positionid"));
    String[] roleId = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "roleid"));
    try {
      ConfigureFacade.resetPositionRole(positionId, roleId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置职位角色成功！");
    return null;
  }

  private String modifyPosition() throws GeneralException {
    String positionId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "positionid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "name"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "description"));

    Position position = new Position();
    position.setId(positionId);
    position.setName(name);
    position.setDescription(description);
    try {
      ConfigureFacade.updatePosition(position);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "修改职位成功！");
    return null;
  }

  private String getPosition() throws GeneralException {
    String positionId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "positionid"));

    Position position;
    try {
      position = ConfigureFacade.getPosition(positionId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "positioninfo", position);
    return null;
  }

  private String listPosition() throws GeneralException {
    List list;
    try {
      list = ConfigureFacade.getPositionList();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "positionlist", list);
    return null;
  }

  private String getPositionListByOrg() throws GeneralException {
    String orgId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "orgid"));
    List list;
    try {
      list = ConfigureFacade.getPositionListByOrg(orgId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "positionlistbyorg", list);
    return null;
  }

  private String listRolePosition() throws GeneralException {
    String roleId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "roleid"));
    List list;
    try {
      list = ConfigureFacade.getPositionListByRole(roleId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "positionlistbyrole", list);
    return null;
  }

  private String addRole() throws GeneralException {
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "name"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "description"));

    Role role = new Role();
    role.setName(name);
    role.setDescription(description);
    try {
      ConfigureFacade.addRole(role);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "创建角色成功！");
    return null;
  }

  private String removeRole() throws GeneralException {
    String roleId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "roleid"));
    try {
      ConfigureFacade.removeRole(roleId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "删除角色成功！");
    return null;
  }

  private String resetRolePosition() throws GeneralException {
    String roleId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "roleid"));
    String[] positionId = StringUtil.spliteString((String) tps.get(
        SessionManager.PDS, "positionid"));
    try {
      ConfigureFacade.resetRolePosition(roleId, positionId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "设置角色职位成功！");
    return null;
  }

  private String modifyRole() throws GeneralException {
    String roleId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "roleid"));
    String name = StringUtil.string2string((String) tps.get(SessionManager.PDS,
        "name"));
    String description = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "description"));

    Role role = new Role();
    role.setId(roleId);
    role.setName(name);
    role.setDescription(description);

    try {
      ConfigureFacade.updateRole(role);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "success_message", "修改角色成功！");
    return null;
  }

  private String getRole() throws GeneralException {
    String roleId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "roleid"));

    Role role;
    try {
      role = ConfigureFacade.getRole(roleId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "roleinfo", role);
    return null;
  }

  private String listRole() throws GeneralException {
    List list;
    try {
      list = ConfigureFacade.getRoleList();
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "rolelist", list);
    return null;
  }

  private String listPositionRole() throws GeneralException {
    String positionId = StringUtil.string2string((String) tps.get(
        SessionManager.PDS, "positionid"));
    List list;
    try {
      list = ConfigureFacade.getRoleListByPosition(positionId);
    } catch (WorkflowException wfe) {
      throw new GeneralException(wfe.toString());
    }
    tps.put(SessionManager.TDS, "rolelistbyposition", list);
    return null;
  }
}
