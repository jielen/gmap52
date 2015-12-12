package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.OrgPositionBean;
import com.kingdrive.workflow.access.OrgPositionLevelBean;
import com.kingdrive.workflow.access.OrgPositionQuery;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.OrgPositionInfo;
import com.kingdrive.workflow.model.OrgPositionLevelModel;
import com.kingdrive.workflow.model.OrgPositionModel;
import com.kingdrive.workflow.util.Sequence;

public class OrgPosition implements Serializable {

  private String id;

  private String companyId;

  private String companyName;

  private String orgId;

  private String orgName;

  private String positionId;

  private String positionName;

  public OrgPosition() {
  }

  public List getSuperOrgPositionList(String orgPositionId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      OrgPositionQuery query = new OrgPositionQuery();
      ArrayList list = query.getSuperOrgPositionList(orgPositionId);
      OrgPosition position = null;
      for (int i = 0; i < list.size(); i++, result.add(position)) {
        position = wrap((OrgPositionInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public void resetSuper(String orgPositionId, String parentId[]) throws WorkflowException {
    OrgPositionLevelBean bean = new OrgPositionLevelBean();
    try {
      bean.removeByOrgPosition(orgPositionId);
      if (parentId == null)
        return;
      OrgPositionLevelModel model = new OrgPositionLevelModel();
      for (int i = 0; i < parentId.length; i++) {
        model.setOrgPositionId(orgPositionId);
        model.setParentId(parentId[i]);
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public List getOrgPositionList() throws WorkflowException {
    List result = new ArrayList();
    try {
      OrgPositionQuery bean = new OrgPositionQuery();
      ArrayList list = bean.getOrgPositionList();
      OrgPosition position = null;
      for (int i = 0; i < list.size(); i++, result.add(position)) {
        position = wrap((OrgPositionInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getOrgPositionListByOrg(String orgId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      OrgPositionQuery query = new OrgPositionQuery();
      ArrayList list = query.getOrgPositionListByOrg(orgId);
      OrgPosition position = null;
      for (int i = 0; i < list.size(); i++, result.add(position)) {
        position = wrap((OrgPositionInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getOrgPositionListByPosition(String positionId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      OrgPositionQuery query = new OrgPositionQuery();
      ArrayList list = query.getOrgPositionListByPosition(positionId);
      OrgPosition position = null;
      for (int i = 0; i < list.size(); i++, result.add(position)) {
        position = wrap((OrgPositionInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getOrgPositionListByStaff(String staffId)
      throws WorkflowException {
    List result = new ArrayList();

    try {
      OrgPositionQuery query = new OrgPositionQuery();
      ArrayList list = query.getOrgPositionListByStaff(staffId);
      OrgPosition meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((OrgPositionInfo) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public OrgPosition getOrgPosition(String orgPositionId)
      throws WorkflowException {
    OrgPosition position = new OrgPosition();

    try {
      OrgPositionQuery bean = new OrgPositionQuery();
      position = wrap(bean.getOrgPosition(orgPositionId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return position;
  }

  public void reset(String organizationId, String[] positionId)
      throws WorkflowException {
    OrgPositionBean bean = new OrgPositionBean();
    try {
      bean.removeByOrg(organizationId);
      if (positionId == null)
        return;
      for (int i = 0; i < positionId.length; i++) {
        OrgPositionModel model = new OrgPositionModel();
        model.setOrgPositionId(String.valueOf(Sequence
            .fetch(Sequence.SEQ_ORG_POSITION)));
        model.setOrganizationId(organizationId);
        model.setPositionId(positionId[i]);
        bean.insert(model);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private OrgPosition wrap(OrgPositionInfo model) {
    OrgPosition pos = new OrgPosition();
    pos.setId(model.getOrgPositionId());
    pos.setCompanyId(model.getCompanyId());
    pos.setCompanyName(model.getCompanyName());
    pos.setOrgId(model.getOrganizationId());
    pos.setOrgName(model.getOrganizationName());
    pos.setPositionId(model.getPositionId());
    pos.setPositionName(model.getPositionName());
    return pos;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof OrgPosition))
      return false;
    OrgPosition o = (OrgPosition) obj;
    return o.id == id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getOrgId() {
    return orgId;
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

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getPositionId() {
    return positionId;
  }

  public void setPositionId(String positionId) {
    this.positionId = positionId;
  }

  public String getPositionName() {
    return positionName;
  }

  public void setPositionName(String positionName) {
    this.positionName = positionName;
  }
}
