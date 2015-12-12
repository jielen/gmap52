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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.PositionRoleBean;
import com.kingdrive.workflow.access.RoleBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.RoleModel;
import com.kingdrive.workflow.util.Sequence;

public class Role implements Serializable {
  private String id;

  private String name;

  private String description;

  public Role() {
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

  public Role getRole(String roleId) throws WorkflowException {
    Role result = new Role();
    RoleBean bean = new RoleBean();
    try {
      result = wrap(bean.findByKey(roleId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getRoleList() throws WorkflowException {
    List result = new ArrayList();
    RoleBean bean = new RoleBean();
    try {
      ArrayList list = bean.find();
      Role meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((RoleModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getRoleListByPosition(String positionId)
      throws WorkflowException {
    List result = new ArrayList();
    RoleBean bean = new RoleBean();
    try {
      ArrayList list = bean.getRoleListByPosition(positionId);
      Role meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((RoleModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getRoleListByExecutor(int nodeId, int responsibility) throws WorkflowException {
    List result = new ArrayList();
    RoleBean bean = new RoleBean();
    try {
      ArrayList list = bean.getRoleListByExecutor(nodeId, responsibility);
      Role meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((RoleModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getRoleListByNonExecutor(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();
    RoleBean bean = new RoleBean();
    try {
      ArrayList list = bean.getRoleListByNonExecutor(nodeId);
      Role meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((RoleModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  /**
   * 
   * @param roleId
   *          String
   * @param conn
   *          Connection
   * @throws WorkflowException
   * @todo delete the role relates.
   */
  public void delete(String roleId) throws WorkflowException {
    try {
      RoleBean bean = new RoleBean();
      bean.delete(roleId);
      /*
       * List children = getChildren(orgId, conn); for(int i = 0; i <
       * children.size(); i++){ delete(((Organization)children.get(i)).getId(),
       * conn); }
       */
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void insert(Role meta) throws WorkflowException {
    meta.setId(String.valueOf(Sequence.fetch(Sequence.SEQ_ROLE)));
    try {
      RoleBean bean = new RoleBean();
      bean.insert(unwrap(meta));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update(Role meta) throws WorkflowException {
    try {
      RoleBean bean = new RoleBean();
      bean.update(unwrap(meta));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void resetRolePosition(String roleId, String[] positionId) throws WorkflowException {
    PositionRoleBean bean = new PositionRoleBean();
    try {
      bean.removeByRole(roleId);
      if (positionId == null)
        return;
      for (int i = 0; i < positionId.length; i++) {
        bean.add(positionId[i], roleId);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private RoleModel unwrap(Role meta) {
    RoleModel model = new RoleModel();
    if (meta.getId() != null)
      model.setRoleId(meta.getId());
    if (meta.getName() != null)
      model.setName(meta.getName());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    return model;
  }

  private Role wrap(RoleModel model) {
    Role meta = new Role();
    meta.setId(model.getRoleId());
    meta.setName(model.getName());
    meta.setDescription(model.getDescription());
    return meta;
  }
}
