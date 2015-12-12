package com.kingdrive.workflow.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.access.VariableValueBean;
import com.kingdrive.workflow.access.VariableValueQuery;
import com.kingdrive.workflow.dto.VariableMeta;
import com.kingdrive.workflow.dto.VariableValueMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import com.kingdrive.workflow.model.VariableValueInfo;
import com.kingdrive.workflow.model.VariableValueModel;
import com.kingdrive.workflow.util.Sequence;

public class VariableValue implements Serializable {

  public VariableValue() {
  }

  /**
   * ���ݲ���valueList����instance�ı���ֵ�����Ҹ������ݿ�
   * valueList��������ϣ�����µı���VariableValueMeta(dto����)
   * 
   * @param templateId
   * @param instanceId
   * @param valueList
   *          ��VariableValueMeta���
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  public void reset(int templateId, int instanceId, List valueList)
    throws WorkflowException {

    if (valueList != null) {

      Variable variableHandler = new Variable();
      /* ��ȡ��instance�����б����б� */
      List variableList = variableHandler
        .getVariableListByTemplate(templateId);

      /* ����VariableValueMeta���������ԣ�Ϊ��������׼����������Ƿ���δ����Ĳ��� */
      for (int loop = 0; loop < valueList.size(); loop++) {
        VariableValueMeta value = (VariableValueMeta) valueList.get(loop);

        if (value == null || value.getValue() == null) {
          throw new WorkflowException(1312, "null");
        }

        for (int i = 0; i < variableList.size(); i++) {
          VariableMeta variable = (VariableMeta) variableList.get(i);
          // if (value.getVariableId() == variable.getId()) {//take place with
          // name
          if (value.getName().equals(variable.getName())) {
            // check the type and value is matched or not when numeric
            if (Variable.TYPE_NUMBER.equals(variable.getType())) {
              try {
                Double.parseDouble(value.getValue());
              } catch (NumberFormatException e) {
                throw new WorkflowException(2025);
              }
            }

            // set the value's other attribute
            value.setTemplateId(variable.getTemplateId());
            value.setInstanceId(instanceId);
            value.setName(variable.getName());
            value.setDescription(variable.getDescription());
            value.setType(variable.getType());

            break;
          }
        }
        /*
         * if (!defined) { throw new WorkflowException(1313, value.toString()); }
         */
      }

      /* ��ȡ��ǰʵ��������VariableValueMeta */
      List instanceValueList = getValueListByInstance(instanceId);

      for (int loop = 0; loop < valueList.size(); loop++) {
        boolean updated = false;//

        VariableValueMeta value = (VariableValueMeta) valueList.get(loop);
        /* ���ڸ��£������ڴ���֮ */
        for (int i = 0; i < instanceValueList.size(); i++) {
          VariableValueMeta instanceValue = (VariableValueMeta) instanceValueList
            .get(i);
          // if (value.getVariableId() == instanceValue.getVariableId()) {//take
          // place whith name
          if (value.getName().equals(instanceValue.getName())) {
            updated = true;

            instanceValue.setValue(value.getValue());
            update(instanceValue);

            break;
          }
        }

        if (!updated) {
          create(value);
        }
      }
    }
  }

  /**
   * ��������
   * 
   * @param meta
   *          dto����
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  public void create(VariableValueMeta meta)
    throws WorkflowException {
    try {
      VariableValueBean bean = new VariableValueBean();
      meta.setValueId(Sequence.fetch(Sequence.SEQ_VARIABLE_VALUE));
      if (bean.insert(unwrap(meta)) != 1)
        throw new WorkflowException(1310);
    } catch (SQLException sqle) {
      throw new WorkflowException(1311, sqle.toString());
    }
  }

  public void update(VariableValueMeta meta)
    throws WorkflowException {
    try {
      VariableValueBean bean = new VariableValueBean();
      if (bean.update(unwrap(meta)) != 1)
        throw new WorkflowException(1315);
    } catch (SQLException sqle) {
      throw new WorkflowException(1316, sqle.toString());
    }
  }

  /**
   * ��ȡָ��instance���е�VariableValue����,�������������Ѿ������˵�VariableValue����
   * ��Ҫ�������ж����˵�ȴ��δ������VariableValue ��ע�������ط���getValueListByInstnace( int
   * instanceId, Connection conn)�Ƚ�
   * 
   * @param templateId
   * @param instanceId
   * @param conn
   * @return
   * @throws WorkflowException
   * @throws
   * @see
   */
  public List getValueListByInstnace(int templateId, int instanceId)
    throws WorkflowException {
    List result = new ArrayList();

    Variable variableHandler = new Variable();
    List variableList = variableHandler.getVariableListByTemplate(templateId);
    List valueList = getValueListByInstance(instanceId);

    for (int i = 0; i < variableList.size(); i++) {
      VariableMeta variable = (VariableMeta) variableList.get(i);
      boolean added = false;
      for (int j = 0; j < valueList.size(); j++) {
        VariableValueMeta value = (VariableValueMeta) valueList.get(j);
        if (variable.getId() == value.getVariableId()) {
          result.add(value);
          added = true;
          break;
        }
      }
      if (!added) {
        VariableValueMeta value = new VariableValueMeta();
        value.setTemplateId(variable.getTemplateId());
        value.setVariableId(variable.getId());
        value.setName(variable.getName());
        value.setDescription(variable.getDescription());
        value.setType(variable.getType());
        result.add(value);
      }
    }

    return result;
  }

  /**
   * ��ȡ��ǰʵ�������Ѿ�������Variable,ע���������ж����˵�Varaible�������Ѿ������˵�Variable
   * 
   * @param instanceId
   * @param conn
   * @return
   * @throws WorkflowException
   * @throws
   * @see
   */
  public List getValueListByInstance(int instanceId)
    throws WorkflowException {
    List valueList = new ArrayList();

    try {
      VariableValueQuery query = new VariableValueQuery();
      ArrayList list = query.getValueListByInstance(instanceId);
      VariableValueMeta meta = null;
      for (int i = 0; i < list.size(); i++, valueList.add(meta)) {
        meta = wrap((VariableValueInfo) list.get(i));
      }
    } catch (SQLException sqle) {
      throw new WorkflowException(1320, sqle.toString());
    }
    return valueList;
  }

  /**
   * ɾ��instance����VariableValue
   * 
   * @param instanceId
   * @param conn
   * @throws WorkflowException
   * @throws
   * @see
   */
  public void removeByInstance(int instanceId)
    throws WorkflowException {
    try {
      VariableValueBean bean = new VariableValueBean();
      bean.removeByInstance(instanceId);
    } catch (SQLException sqle) {
      throw new WorkflowException(1311, sqle.toString());
    }
  }

  /**
   * Model����(PO)->Meta����(dto)��ת��
   * 
   * @param model
   * @return
   * @throws
   * @see
   */
  private VariableValueMeta wrap(VariableValueInfo model) {
    VariableValueMeta meta = new VariableValueMeta();
    if (model.getTemplateId() != null)
      meta.setTemplateId(model.getTemplateId().intValue());
    if (model.getInstanceId() != null)
      meta.setInstanceId(model.getInstanceId().intValue());
    if (model.getVariableId() != null)
      meta.setVariableId(model.getVariableId().intValue());
    meta.setName(model.getName());
    meta.setDescription(model.getDescription());
    meta.setType(model.getType());
    if (model.getValueId() != null)
      meta.setValueId(model.getValueId().intValue());
    meta.setValue(model.getValue());
    return meta;
  }

  /**
   * Meta����(dto)��ת��->Model����(PO)
   * 
   * @param model
   * @return
   * @throws
   * @see
   */
  private VariableValueModel unwrap(VariableValueMeta meta) {
    VariableValueModel unwrapper = new VariableValueModel();
    if (meta.getValueId() != 0)
      unwrapper.setValueId(meta.getValueId());
    if (meta.getInstanceId() != 0)
      unwrapper.setInstanceId(meta.getInstanceId());
    if (meta.getVariableId() != 0)
      unwrapper.setVariableId(meta.getVariableId());
    if (meta.getValue() != null)
      unwrapper.setValue(meta.getValue());
    return unwrapper;
  }
}
