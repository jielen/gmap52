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

public class NodeMeta implements Serializable, Comparable {
  private int id;

  private String name;

  private String description;

  private String type;

  private int templateId;

  private String templateName;

  private String executorsMethod;

  private String taskListener;

  private String businessType;

  private int limitExecuteTerm;

  private int remindExecuteTerm;

  public NodeMeta() {
    type = "2";
    executorsMethod = "0";
  }
  public NodeMeta(int id){
  	this.id = id;
  	if(-1 == id){
  		type = "0";
  	}else if (-2 == id){
  		type = "1";
  	}else{
  		type = "2";
  	}
  	executorsMethod = "0";
  }

  public int compareTo(Object obj) {
    return id - ((NodeMeta) obj).id;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof NodeMeta))
      return false;
    NodeMeta o = (NodeMeta) obj;
    return id == o.id;
  }

  public int hashCode() {
    return id;
  }

  public String getBusinessType() {
    return businessType;
  }

  public void setBusinessType(String businessType) {
    this.businessType = businessType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExecutorsMethod() {
    return executorsMethod;
  }

  public void setExecutorsMethod(String executorsMethod) {
    this.executorsMethod = executorsMethod;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getLimitExecuteTerm() {
    return limitExecuteTerm;
  }

  public void setLimitExecuteTerm(int limitExecuteTerm) {
    this.limitExecuteTerm = limitExecuteTerm;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTaskListener() {
    return taskListener;
  }

  public void setTaskListener(String taskListener) {
    this.taskListener = taskListener;
  }

  public int getTemplateId() {
    return templateId;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return Returns the remindExecuteTerm.
   */
  public int getRemindExecuteTerm() {
    return remindExecuteTerm;
  }

  /**
   * @param remindExecuteTerm
   *          The remindExecuteTerm to set.
   */
  public void setRemindExecuteTerm(int remindExecuteTerm) {
    this.remindExecuteTerm = remindExecuteTerm;
  }
}
