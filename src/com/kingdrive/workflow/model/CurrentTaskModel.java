package com.kingdrive.workflow.model;

public class CurrentTaskModel implements java.io.Serializable {
  private Integer currentTaskId = null;

  private boolean currentTaskIdModifyFlag;

  private Integer instanceId = null;

  private boolean instanceIdModifyFlag;

  private Integer nodeId = null;

  private boolean nodeIdModifyFlag;

  private String executor = null;

  private boolean executorModifyFlag;

  private Integer delegationId = null;

  private boolean delegationIdModifyFlag;

  private String owner = null;

  private boolean ownerModifyFlag;

  private String creator = null;

  private boolean creatorModifyFlag;

  private String createTime = null;

  private boolean createTimeModifyFlag;

  private String limitExecuteTime = null;

  private boolean limitExecuteTimeModifyFlag;

  private Integer responsibility = null;

  private boolean responsibilityModifyFlag;
	private Integer parentTaskId=null;
	private boolean parentTaskIdModifyFlag;

  private String action = "";

  public CurrentTaskModel() {
    clearModifyFlag();
  }

  public void clearModifyFlag() {
    this.currentTaskIdModifyFlag = false;
    this.instanceIdModifyFlag = false;
    this.nodeIdModifyFlag = false;
    this.executorModifyFlag = false;
    this.delegationIdModifyFlag = false;
    this.ownerModifyFlag = false;
    this.creatorModifyFlag = false;
    this.createTimeModifyFlag = false;
    this.limitExecuteTimeModifyFlag = false;
    this.responsibilityModifyFlag = false;
		this.parentTaskIdModifyFlag=false;
  }

  public void setCurrentTaskId(int currentTaskId) {
    this.currentTaskId = new Integer(currentTaskId);
    this.currentTaskIdModifyFlag = true;
  }

  public void setCurrentTaskId(Integer currentTaskId) {
    this.currentTaskId = currentTaskId;
    this.currentTaskIdModifyFlag = true;
  }

  public Integer getCurrentTaskId() {
    return this.currentTaskId;
  }

  public void setInstanceId(int instanceId) {
    this.instanceId = new Integer(instanceId);
    this.instanceIdModifyFlag = true;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
    this.instanceIdModifyFlag = true;
  }

  public Integer getInstanceId() {
    return this.instanceId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = new Integer(nodeId);
    this.nodeIdModifyFlag = true;
  }

  public void setNodeId(Integer nodeId) {
    this.nodeId = nodeId;
    this.nodeIdModifyFlag = true;
  }

  public Integer getNodeId() {
    return this.nodeId;
  }

  public void setExecutor(String executor) {
    this.executor = executor;
    this.executorModifyFlag = true;
  }

  public String getExecutor() {
    return this.executor;
  }

  public void setDelegationId(int delegationId) {
    this.delegationId = new Integer(delegationId);
    this.delegationIdModifyFlag = true;
  }

  public void setDelegationId(Integer delegationId) {
    this.delegationId = delegationId;
    this.delegationIdModifyFlag = true;
  }

  public Integer getDelegationId() {
    return this.delegationId;
  }

  public void setOwner(String owner) {
    this.owner = owner;
    this.ownerModifyFlag = true;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setCreator(String creator) {
    this.creator = creator;
    this.creatorModifyFlag = true;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
    this.createTimeModifyFlag = true;
  }

  public String getCreateTime() {
    return this.createTime;
  }

  public void setLimitExecuteTime(String limitExecuteTime) {
    this.limitExecuteTime = limitExecuteTime;
    this.limitExecuteTimeModifyFlag = true;
  }

  public String getLimitExecuteTime() {
    return this.limitExecuteTime;
  }

  public void setResponsibility(int responsibility) {
    this.responsibility = new Integer(responsibility);
    this.responsibilityModifyFlag = true;
  }

  public void setResponsibility(Integer responsibility) {
    this.responsibility = responsibility;
    this.responsibilityModifyFlag = true;
  }

  public Integer getResponsibility() {
    return this.responsibility;
  }

  public boolean getCurrentTaskIdModifyFlag() {
    return this.currentTaskIdModifyFlag;
  }

  public boolean getInstanceIdModifyFlag() {
    return this.instanceIdModifyFlag;
  }

  public boolean getNodeIdModifyFlag() {
    return this.nodeIdModifyFlag;
  }

  public boolean getExecutorModifyFlag() {
    return this.executorModifyFlag;
  }

  public boolean getDelegationIdModifyFlag() {
    return this.delegationIdModifyFlag;
  }

  public boolean getOwnerModifyFlag() {
    return this.ownerModifyFlag;
  }

  public boolean getCreatorModifyFlag() {
    return this.creatorModifyFlag;
  }

  public boolean getCreateTimeModifyFlag() {
    return this.createTimeModifyFlag;
  }

  public boolean getLimitExecuteTimeModifyFlag() {
    return this.limitExecuteTimeModifyFlag;
  }

  public boolean getResponsibilityModifyFlag() {
    return this.responsibilityModifyFlag;
  }

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }
    /**
     * @return Returns the parentTaskId.
     */
    public Integer getParentTaskId() {
        return parentTaskId;
    }
    /**
     * @param parentTaskId The parentTaskId to set.
     */
    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
    		this.parentTaskIdModifyFlag = true;
    }
 
    /**
     * @param parentTaskId The parentTaskId to set.
     */
    public void setParentTaskId(int parentTaskId) {
        this.parentTaskId = new Integer(parentTaskId);
    		this.parentTaskIdModifyFlag = true;
    }
 
    /**
     * @return Returns the parentTaskIdModifyFlag.
     */
    public boolean getParentTaskIdModifyFlag() {
        return parentTaskIdModifyFlag;
    }
}
