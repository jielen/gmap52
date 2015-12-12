package com.kingdrive.workflow.dto;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.io.Serializable;
import java.util.List;

public class TaskResultMeta implements Serializable {

  private int instanceId;

  private List stateValueList;

  private List variableValueList;

  public TaskResultMeta() {
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public List getStateValueList() {
    return stateValueList;
  }

  public void setStateValueList(List stateValueList) {
    this.stateValueList = stateValueList;
  }

  public List getVariableValueList() {
    return variableValueList;
  }

  public void setVariableValueList(List variableValueList) {
    this.variableValueList = variableValueList;
  }

}
