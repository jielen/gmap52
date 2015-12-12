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

public class StateValueMeta implements Serializable, Comparable {

  private int id;

  private int templateId;

  private int instanceId;

  private int stateId;

  private String name;

  private String description;

  private String value;

  public StateValueMeta() {
  }

  public int compareTo(Object obj) {
    return id - ((StateValueMeta) obj).id;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof StateValueMeta))
      return false;
    StateValueMeta o = (StateValueMeta) obj;
    return id == o.id;
  }

  public int hashCode() {
    return id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public int getStateId() {
    return stateId;
  }

  public void setStateId(int stateId) {
    this.stateId = stateId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getTemplateId() {
    return templateId;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
