package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.OrgBean;
import com.kingdrive.workflow.access.OrgQuery;
import com.kingdrive.workflow.access.StaffPositionBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.OrgInfo;
import com.kingdrive.workflow.model.OrgModel;
import com.kingdrive.workflow.util.Sequence;

public class Org implements Serializable {

  private String id;

  private String companyId;

  private String companyName;

  private String name;

  private String description;

  private String parentId;

  public Org() {
  }

  public List getOrganizationList() throws WorkflowException {
    List result = new ArrayList();

    OrgQuery query = new OrgQuery();
    try {
      ArrayList list = query.getOrgList();
      Org org = null;
      for (int i = 0; i < list.size(); i++, result.add(org)) {
        org = wrap((OrgInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getOrgListByCompany(String companyId)
      throws WorkflowException {
    List result = new ArrayList();

    OrgQuery query = new OrgQuery();
    try {
      ArrayList list = query.getOrgListByCompany(companyId);
      Org org = null;
      for (int i = 0; i < list.size(); i++, result.add(org)) {
        org = wrap((OrgInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getOrgListByExecutor(int nodeId, int responsibility) throws WorkflowException {
    List result = new ArrayList();

    OrgQuery query = new OrgQuery();
    try {
      ArrayList list = query.getOrgListByExecutor(nodeId, responsibility);
      Org org = null;
      for (int i = 0; i < list.size(); i++, result.add(org)) {
        org = wrap((OrgInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getOrgListByNonExecutor(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();

    OrgQuery query = new OrgQuery();
    try {
      ArrayList list = query.getOrgListByNonExecutor(nodeId);
      Org org = null;
      for (int i = 0; i < list.size(); i++, result.add(org)) {
        org = wrap((OrgInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void create(Org org) throws WorkflowException {
    if (org.getId() == null || org.getId().equals(""))
      org.setId(String.valueOf(Sequence.fetch(Sequence.SEQ_ORG)));
    try {
      OrgBean bean = new OrgBean();
      if (bean.insert(unwrap(org)) != 1)
        throw new WorkflowException(2100);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void create() throws WorkflowException {
    create(this);
  }

  public Org getOrganization(String orgId)
      throws WorkflowException {
    Org result = new Org();

    OrgQuery query = new OrgQuery();
    try {
      result = wrap(query.getOrg(orgId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public boolean load() throws WorkflowException {
    Org org = getOrganization(id);
    if (org == null) {
      return false;
    }
    name = org.getName();
    description = org.getDescription();
    parentId = org.getParentId();
    return true;
  }

  public void delete(String orgId) throws WorkflowException {
    try {
      OrgBean bean = new OrgBean();
      StaffPositionBean staffPositionBean = new StaffPositionBean();
      bean.delete(orgId);
      staffPositionBean.removeByPosition(orgId);
      List children = getChildren(orgId);
      for (int i = 0; i < children.size(); i++) {
        delete(((Org) children.get(i)).getId());
      }

    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private List getChildren(String orgId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      OrgQuery query = new OrgQuery();
      ArrayList list = query.getOrgListByParent(orgId);
      Org child = null;
      for (int i = 0; i < list.size(); i++, result.add(child)) {
        child = wrap((OrgInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void delete() throws WorkflowException {
    delete(id);
  }

  public void update(Org org) throws WorkflowException {
    try {
      OrgBean bean = new OrgBean();
      if (bean.update(unwrap(org)) != 1)
        throw new WorkflowException(2101);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update() throws WorkflowException {
    update(this);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Org))
      return false;
    Org o = (Org) obj;
    return o.id == id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getParentId() {
    return parentId;
  }

  private Org wrap(OrgInfo model) {
    Org org = new Org();
    org.setId(model.getOrganizationId());
    org.setCompanyId(model.getCompanyId());
    org.setCompanyName(model.getCompanyName());
    org.setName(model.getName());
    org.setDescription(model.getDescription());
    org.setParentId(model.getParentId());
    return org;
  }

  private OrgModel unwrap(Org org) {
    OrgModel model = new OrgModel();
    if (org.getId() != null)
      model.setOrganizationId(org.getId());
    if (org.getCompanyId() != null)
      model.setCompanyId(org.getCompanyId());
    if (org.getName() != null)
      model.setName(org.getName());
    if (org.getDescription() != null)
      model.setDescription(org.getDescription());
    if (org.getParentId() != null)
      model.setParentId(org.getParentId());
    return model;
  }
}
