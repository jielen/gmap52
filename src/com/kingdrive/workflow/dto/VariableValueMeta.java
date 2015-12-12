package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class VariableValueMeta implements Serializable {

  private int templateId;

  private int instanceId;

  private int variableId;

  private String name;

  private String description;

  private String type;

  private int valueId;

  private String value;

  public VariableValueMeta() {
  }

  public int getVariableId() {
    return variableId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }

  public int getTemplateId() {
    return templateId;
  }

  public int getValueId() {
    return valueId;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public String getValue() {
    return value;
  }

  public void setVariableId(int variableId) {
    this.variableId = variableId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public void setValueId(int valueId) {
    this.valueId = valueId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("templateId = ").append(templateId).append("\r\n");
    s.append("instanceId = ").append(instanceId).append("\r\n");
    s.append("variableId = ").append(variableId).append("\r\n");
    s.append("name = ").append(name).append("\r\n");
    s.append("description = ").append(description).append("\r\n");
    s.append("type = ").append(type).append("\r\n");
    s.append("valueId = ").append(valueId).append("\r\n");
    s.append("value = ").append(value).append("\r\n");
    return s.toString();
  }
}
