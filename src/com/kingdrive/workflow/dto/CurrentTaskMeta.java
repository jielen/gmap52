package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class CurrentTaskMeta implements Serializable {

  public static final int TASK_IDENTITY_NORMAL = 1;

  public static final int TASK_IDENTITY_DELEGATED = -1;

  public static final int TASK_IDENTITY_DELEGATING = -9;

    public static final int TASK_TYPE_NORMAL=0;//��ͬ���ʹ���	
    
    public static final int TASK_TYPE_TOCOLLECT=-2;//�ȴ��������ʹ�������

    public static final int TASK_TYPE_COLLECTED=-1;//�Ѿ��������ʹ�������

    public static final int TASK_TYPE_TOCOLLECT_DETAIL=-999;//�������ʹ�������
  private int currentTaskId;

  private int instanceId;

  private String instanceName;

  private String instanceDescription;

  private String instanceStartTime;

  private String instanceEndTime;

  private int templateId;

  private String templateName;

  private String templateType;

  private int nodeId;

  private String nodeName;

  private String businessType;

  private String executor;

  private String executorName;

  private int identity;

  private String owner;

  private String ownerName;

  private String creator;

  private String creatorName;

  private String createTime;

  private String limitExecuteTime;

  private int responsibility;
    /**
     * parentTaskId��ʾ��ǰ��������ͣ���ǰ����һ����Ϊ5������
     * parentTaskId	0����Null	��ʾ��ͨ��������
     * parentTaskId	-999	��ʾ��������ϸ��������
     * parentTaskId	ĳ��currentTaskId	��ʾ�ѻ������Դ�������,�丸�������currentTaskId��ָ�Ĵ�������
     * parentTaskId	-2	��ʾδ���ܵĴ�������
     * parentTaskId	-1	��ʾ�ѻ��ܵĴ�������
     * 
     */
    private int parentTaskId;
  private String compoId;

  public CurrentTaskMeta() {
    identity = TASK_IDENTITY_NORMAL;
  }

  public int getNodeId() {
    return nodeId;
  }

  public String getNodeName() {
    return nodeName;
  }

  public int getTemplateId() {
    return templateId;
  }

  public int getCurrentTaskId() {
    return currentTaskId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  public void setCurrentTaskId(int currentTaskId) {
    this.currentTaskId = currentTaskId;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = instanceId;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public String getExecutor() {
    return executor;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
  }

  public String getExecutorName() {
    return executorName;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
  }

  public int getIdentity() {
    return identity;
  }

  public void setIdentity(int identity) {
    this.identity = identity;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getInstanceDescription() {
    return instanceDescription;
  }

  public void setInstanceDescription(String instanceDescription) {
    this.instanceDescription = instanceDescription;
  }

  public String getInstanceEndTime() {
    return instanceEndTime;
  }

  public void setInstanceEndTime(String instanceEndTime) {
    this.instanceEndTime = instanceEndTime;
  }

  public String getInstanceStartTime() {
    return instanceStartTime;
  }

  public void setInstanceStartTime(String instanceStartTime) {
    this.instanceStartTime = instanceStartTime;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getLimitExecuteTime() {
    return limitExecuteTime;
  }

  public void setLimitExecuteTime(String limitExecuteTime) {
    this.limitExecuteTime = limitExecuteTime;
  }

  public int getResponsibility() {
    return responsibility;
  }

  public void setResponsibility(int responsibility) {
    this.responsibility = responsibility;
  }

  public String getBusinessType() {
    return businessType;
  }

  public void setBusinessType(String businessType) {
    this.businessType = businessType;
  }

  public String getTemplateType() {
    return templateType;
  }

  public void setTemplateType(String templateType) {
    this.templateType = templateType;
  }

    /**
     * @return Returns the parentTaskId.
     */
    public int getParentTaskId() {
        return parentTaskId;
    }
    /**
     * @param parentTaskId The parentTaskId to set.
     */
    public void setParentTaskId(int parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
    public String getCompoId() {
      return compoId;
    }
    public void setCompoId(String compoId) {
      this.compoId = compoId;
    }
}
