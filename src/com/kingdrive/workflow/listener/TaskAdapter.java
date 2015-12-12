package com.kingdrive.workflow.listener;

import com.kingdrive.workflow.dto.CurrentTaskMeta;

public abstract class TaskAdapter implements TaskListener {

  public TaskAdapter() {
  }

  public void beforeExecution(CurrentTaskMeta meta) {
  }

  public void afterExecution(CurrentTaskMeta meta) {
  }
}
