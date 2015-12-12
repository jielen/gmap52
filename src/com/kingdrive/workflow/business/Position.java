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

import com.kingdrive.workflow.access.PositionBean;
import com.kingdrive.workflow.access.PositionRoleBean;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.PositionModel;
import com.kingdrive.workflow.util.Sequence;

public class Position implements Serializable {
  private String id;

  private String name;

  private String description;

  public Position() {
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

  public Position getPosition(String positionId)
      throws WorkflowException {
    Position result = new Position();
    PositionBean bean = new PositionBean();
    try {
      result = wrap(bean.findByKey(positionId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getPositionList() throws WorkflowException {
    List result = new ArrayList();
    PositionBean bean = new PositionBean();
    try {
      ArrayList list = bean.find();
      Position meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((PositionModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getPositionListByRole(String roleId)
      throws WorkflowException {
    List result = new ArrayList();
    PositionBean bean = new PositionBean();
    try {
      ArrayList list = bean.getPositionListByRole(roleId);
      Position meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((PositionModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getPositionListByOrg(String organizationId)
      throws WorkflowException {
    List result = new ArrayList();
    PositionBean bean = new PositionBean();
    try {
      ArrayList list = bean.getPositionListByOrg(organizationId);
      Position meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((PositionModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getPositionListByExecutor(int nodeId, int responsibility) throws WorkflowException {
    List result = new ArrayList();
    PositionBean bean = new PositionBean();
    try {
      ArrayList list = bean.getPositionListByExecutor(nodeId,
          responsibility);
      Position meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((PositionModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getPositionListByNonExecutor(int nodeId)
      throws WorkflowException {
    List result = new ArrayList();
    PositionBean bean = new PositionBean();
    try {
      ArrayList list = bean.getPositionListByNonExecutor(nodeId);
      Position meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((PositionModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  /**
   * 
   * @param positionId
   *          String
   * @param conn
   *          Connection
   * @throws WorkflowException
   * @todo delete the position relates.
   */
  public void delete(String positionId)
      throws WorkflowException {
    try {
      PositionBean bean = new PositionBean();
      bean.delete(positionId);
      /*
       * List children = getChildren(orgId, conn); for(int i = 0; i <
       * children.size(); i++){ delete(((Organization)children.get(i)).getId(),
       * conn); }
       */
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void insert(Position meta) throws WorkflowException {
    meta.setId(String.valueOf(Sequence.fetch(Sequence.SEQ_POSITION)));
    try {
      PositionBean bean = new PositionBean();
      bean.insert(unwrap(meta));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update(Position meta) throws WorkflowException {
    try {
      PositionBean bean = new PositionBean();
      bean.update(unwrap(meta));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void resetPositionRole(String positionId, String[] roleId) throws WorkflowException {
    PositionRoleBean bean = new PositionRoleBean();
    try {
      bean.removeByPosition(positionId);
      if (roleId == null)
        return;
      for (int i = 0; i < roleId.length; i++) {
        bean.add(positionId, roleId[i]);
      }
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  private PositionModel unwrap(Position meta) {
    PositionModel model = new PositionModel();
    if (meta.getId() != null)
      model.setPositionId(meta.getId());
    if (meta.getName() != null)
      model.setName(meta.getName());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    return model;
  }

  private Position wrap(PositionModel model) {
    Position meta = new Position();
    meta.setId(model.getPositionId());
    meta.setName(model.getName());
    meta.setDescription(model.getDescription());
    return meta;
  }
}
