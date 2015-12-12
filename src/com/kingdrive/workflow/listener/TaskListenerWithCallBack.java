package com.kingdrive.workflow.listener;

import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.exception.WorkflowException;

public interface TaskListenerWithCallBack extends TaskListener{
	
  public abstract void beforeCallback(ActionMeta meta) throws WorkflowException;
  
  public abstract void afterCallback(ActionMeta meta) throws WorkflowException;
  
}
