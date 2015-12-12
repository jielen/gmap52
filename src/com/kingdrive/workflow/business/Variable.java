package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.VariableBean;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.VariableModel;
import com.kingdrive.workflow.util.Sequence;

public class Variable implements Serializable {

  public static final String TYPE_NUMBER = "0";

  public static final String TYPE_CHARACTER = "1";

  public Variable() {
  }

  public void create(VariableMeta meta)
      throws WorkflowException {
    int templateId = meta.getTemplateId();
    String name = meta.getName();
    VariableMeta temp = getVariable(templateId, name);
    if (temp != null && templateId == temp.getTemplateId() && name != null
        && name.equals(temp.getName())) {
      throw new WorkflowException(2020);
    }

    try {
      VariableBean bean = new VariableBean();
      meta.setId(Sequence.fetch(Sequence.SEQ_VARIABLE));
      if (bean.insert(unwrap(meta)) != 1)
        throw new WorkflowException(2021);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void remove(int variableId) throws WorkflowException {
    try {
      VariableBean bean = new VariableBean();
      bean.delete(variableId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void removeByTemplate(int templateId)
      throws WorkflowException {
    try {
      VariableBean bean = new VariableBean();
      bean.removeByTemplate(templateId);
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
  }

  public void update(VariableMeta meta)
      throws WorkflowException {
    try {
      VariableBean bean = new VariableBean();
      VariableMeta temp = getVariable(meta.getTemplateId(), meta.getName());
      if (temp.getName() != null
          && (temp.getId() != meta.getId() && temp.getName().equals(
              meta.getName())))
        throw new WorkflowException(2020);
      if (bean.update(unwrap(meta)) != 1)
        throw new WorkflowException(2023);
    } catch (SQLException e) {
      throw new WorkflowException(2023, e.toString());
    }
  }

  public VariableMeta getVariable(int variableId)
      throws WorkflowException {
    VariableMeta result = new VariableMeta();
    try {
      VariableBean bean = new VariableBean();
      result = wrap(bean.findByKey(variableId));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return result;
  }

  public List getVariableListByTemplate(int templateId)
      throws WorkflowException {
    List result = new ArrayList();
    try {
      VariableBean bean = new VariableBean();
      ArrayList list = bean.getVariableListByTemplate(templateId);
      VariableMeta meta = null;
      for (int i = 0; i < list.size(); i++, result.add(meta)) {
        meta = wrap((VariableModel) list.get(i));
      }
    } catch (SQLException e) {
      throw new WorkflowException(2019, e.toString());
    }
    return result;
  }

  public VariableMeta getVariable(int templateId, String name)
      throws WorkflowException {
    VariableMeta meta;

    try {
      VariableBean bean = new VariableBean();
      meta = wrap(bean.getVariable(templateId, name));
    } catch (SQLException e) {
      throw new WorkflowException(2000, e.toString());
    }
    return meta;
  }

  private VariableMeta wrap(VariableModel model) {
    VariableMeta meta = new VariableMeta();
    if (model.getVariableId() != null)
      meta.setId(model.getVariableId().intValue());
    meta.setName(model.getName());
    meta.setDescription(model.getDescription());
    meta.setType(model.getType());
    if (model.getTemplateId() != null)
      meta.setTemplateId(model.getTemplateId().intValue());
    return meta;
  }

  private VariableModel unwrap(VariableMeta meta) {
    VariableModel model = new VariableModel();
    if (meta.getId() != 0)
      model.setVariableId(meta.getId());
    if (meta.getName() != null)
      model.setName(meta.getName());
    if (meta.getDescription() != null)
      model.setDescription(meta.getDescription());
    if (meta.getType() != null)
      model.setType(meta.getType());
    if (meta.getTemplateId() != 0)
      model.setTemplateId(meta.getTemplateId());
    return model;
  }

}
