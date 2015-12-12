package com.kingdrive.workflow.listener;

import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.exception.WorkflowException;

public interface TaskListener {

  public abstract void beforeExecution(CurrentTaskMeta meta) throws WorkflowException;

  public abstract void afterExecution(CurrentTaskMeta meta) throws WorkflowException;
  
  public abstract void beforeUntread(ActionMeta meta) throws WorkflowException;

  public abstract void afterUntread(ActionMeta meta) throws WorkflowException;
  
}
