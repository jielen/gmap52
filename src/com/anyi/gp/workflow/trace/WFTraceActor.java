package com.anyi.gp.workflow.trace;

import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;

public class WFTraceActor {

    public String actorName;              // 执行者
    public String actionName;             // 处理名称
    public String actionDescription;      // 处理意见
    public String executeTime;            // 执行时间
    public boolean isUntread = false;     // 最近一次动作是否是回退处理

    public void loadFrom(TableData row) {
        actorName = row.getFieldValue(WFConst.WF_TASK_OWNER_NAME).trim();
        actionName = row.getFieldValue(WFConst.WF_ACTION_NAME).trim();
        actionDescription = row.getFieldValue(WFConst.WF_ACTION_DESCRIPTION).trim();
        executeTime = row.getFieldValue(WFConst.WF_ACTION_EXECUTE_TIME).trim();
        isUntread = (actionName.equalsIgnoreCase("回退") || actionName.equalsIgnoreCase("流程回退"));
    }

    public String toHtml() {
        return WFTraceActorWriter.getHtml(this);
    }

}
