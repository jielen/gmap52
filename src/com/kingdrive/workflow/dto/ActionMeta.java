package com.kingdrive.workflow.dto;

import java.io.Serializable;

public class ActionMeta
    implements Serializable{
    public static final String ACTION_TYPE_AUTHORIZE_TASK = "authorize_task";//授权
    public static final String ACTION_TYPE_FORWARD_TASK = "forward_task";//提交
    public static final String ACTION_TYPE_CALLBACK_FLOW = "callback_flow";//回收
    public static final String ACTION_TYPE_GIVEBACK_FLOW = "giveback_flow";//回退一步
    public static final String ACTION_TYPE_TRANSFER_FLOW = "transfer_flow";//流程跳转
    public static final String ACTION_TYPE_UNTREAD_FLOW = "untread_flow";//回退多步.zhanggh
    public static final String ACTION_TYPE_ACTIVATE_INSTANCE = "activate_instance";//激活	
    public static final String ACTION_TYPE_DEACTIVATE_INSTANCE = "deactivate_instance";//冻结
    public static final String ACTION_TYPE_INTERRUPT_INSTANCE = "interrupt_instance";//中止
    public static final String ACTION_TYPE_RESTART_INSTANCE = "restart_instance";//重启
    public static final String ACTION_TYPE_REDO_INSTANCE = "redo_instance";//重做

    public static final int ACTION_NODE_UNKNOWN = -9;

    private int id;
    private int instanceId;
    private String instanceName;
    private String instanceDescription;
    private int templateId;
    private int parentInstanceId;
    private String templateName;
    private String templateType;
    private int nodeId;
    private String nodeName;
    private String businessType;
    private String actionName;
    private String executor;
    private String executorName;
    private String description;
    private String executeTime;
    private String owner;
    private String ownerName;
    private String limitExecuteTime;

    public ActionMeta(){
    }

    public int getId(){
        return id;
    }

    public int getInstanceId(){
        return instanceId;
    }

    public int getNodeId(){
        return nodeId;
    }

    public String getActionName(){
        return actionName;
    }

    public String getExecutor(){
        return executor;
    }

    public String getExecuteTime(){
        return executeTime;
    }

    public String getDescription(){
        return description;
    }

    public String getExecutorName(){
        return executorName;
    }

    public String getInstanceName(){
        return instanceName;
    }


    /**
     * @return Returns the parentInstanceId.
     */
    public int getParentInstanceId() {
        return parentInstanceId;
    }
    /**
     * @param parentInstanceId The parentInstanceId to set.
     */
    public void setParentInstanceId(int parentInstanceId) {
        this.parentInstanceId = parentInstanceId;
    }
    public String getInstanceDescription(){
        return instanceDescription;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setInstanceId(int instanceId){
        this.instanceId = instanceId;
    }

    public void setNodeId(int nodeId){
        this.nodeId = nodeId;
    }

    public void setActionName(String actionName){
        this.actionName = actionName;
    }

    public String getNodeName(){
        return nodeName;
    }

    public void setNodeName(String nodeName){
        this.nodeName = nodeName;
    }

    public void setExecutor(String executor){
        this.executor = executor;
    }

    public void setExecuteTime(String executeTime){
        this.executeTime = executeTime;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setExecutorName(String executorName){
        this.executorName = executorName;
    }

    public void setInstanceName(String instanceName){
        this.instanceName = instanceName;
    }

    public void setInstanceDescription(String instanceDescription){
        this.instanceDescription = instanceDescription;
    }

	public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getOwnerName(){
        return ownerName;
    }

    public void setOwnerName(String ownerName){
        this.ownerName = ownerName;
    }

    public String getLimitExecuteTime(){
        return limitExecuteTime;
    }

    public void setLimitExecuteTime(String limitExecuteTime){
        this.limitExecuteTime = limitExecuteTime;
    }

    public int getTemplateId(){
        return templateId;
    }

    public void setTemplateId(int templateId){
        this.templateId = templateId;
    }

    public String getTemplateName(){
        return templateName;
    }

    public void setTemplateName(String templateName){
        this.templateName = templateName;
    }

    public String getBusinessType(){
        return businessType;
    }

    public void setBusinessType(String businessType){
        this.businessType = businessType;
    }

    public String getTemplateType(){
        return templateType;
    }

    public void setTemplateType(String templateType){
        this.templateType = templateType;
    }
}
