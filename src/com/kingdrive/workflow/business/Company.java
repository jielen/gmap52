package com.kingdrive.workflow.business;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.CompanyBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.CompanyModel;
import com.kingdrive.workflow.util.Sequence;

public class Company implements Serializable {
  private String id;

  private String name;

  private String description;

  private String parentId;

  public Company() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public Company getCompany(String companyId)
      throws WorkflowException {
    Company result = new Company();
    CompanyBean bean = new CompanyBean();
    try {
      result = wrap(bean.findByKey(companyId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getCompanyList() throws WorkflowException {
    List result = new ArrayList();
    CompanyBean bean = new CompanyBean();
    try {
      ArrayList list = bean.find();
      Company com = null;
      for (int i = 0; i < list.size(); i++, result.add(com)) {
        com = wrap((CompanyModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getCompanyListByExecutor(int nodeId, int responsibility) throws WorkflowException {
    List result = new ArrayList();
    CompanyBean bean = new CompanyBean();
    try {
      ArrayList list = bean.getCompanyListByExecutor(nodeId,
          responsibility);
      Company com = null;
      for (int i = 0; i < list.size(); i++, result.add(com)) {
        com = wrap((CompanyModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getCompanyListByNonExecutor(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();
    CompanyBean bean = new CompanyBean();
    try {
      ArrayList list = bean.getCompanyListByNonExecutor(nodeId);
      Company com = null;
      for (int i = 0; i < list.size(); i++, result.add(com)) {
        com = wrap((CompanyModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  /**
   * 
   * @param companyId
   *          String
   * @param conn
   *          Connection
   * @throws WorkflowException
   * @todo delete the company relates.
   */
  public void delete(String companyId)
      throws WorkflowException {
    try {
      CompanyBean bean = new CompanyBean();
      bean.delete(companyId);
      /*
       * List children = getChildren(orgId, conn); for(int i = 0; i <
       * children.size(); i++){ delete(((Organization)children.get(i)).getId(),
       * conn); }
       */
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void insert(Company meta) throws WorkflowException {
    meta.setId(String.valueOf(Sequence.fetch(Sequence.SEQ_COMPANY)));
    try {
      CompanyBean bean = new CompanyBean();
      bean.insert(unwrap(meta));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update(Company meta) throws WorkflowException {
    try {
      CompanyBean bean = new CompanyBean();
      bean.update(unwrap(meta));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private CompanyModel unwrap(Company meta) {
    CompanyModel model = new CompanyModel();
    if (meta.getId() != null)
      model.setCompanyId(meta.getId());
    if (meta.getName() != null)
      model.setName(meta.getName());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    if (meta.getParentId() != null)
      model.setParentId(meta.getParentId());
    return model;
  }

  private Company wrap(CompanyModel model) {
    Company meta = new Company();
    meta.setId(model.getCompanyId());
    meta.setName(model.getName());
    meta.setDescription(model.getDescription());
    meta.setParentId(model.getParentId());
    return meta;
  }

}
