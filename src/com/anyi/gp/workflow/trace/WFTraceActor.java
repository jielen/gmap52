package com.anyi.gp.workflow.trace;

import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;

public class WFTraceActor {

    public String actorName;              // ִ����
    public String actionName;             // ��������
    public String actionDescription;      // �������
    public String executeTime;            // ִ��ʱ��
    public boolean isUntread = false;     // ���һ�ζ����Ƿ��ǻ��˴���

    public void loadFrom(TableData row) {
        actorName = row.getFieldValue(WFConst.WF_TASK_OWNER_NAME).trim();
        actionName = row.getFieldValue(WFConst.WF_ACTION_NAME).trim();
        actionDescription = row.getFieldValue(WFConst.WF_ACTION_DESCRIPTION).trim();
        executeTime = row.getFieldValue(WFConst.WF_ACTION_EXECUTE_TIME).trim();
        isUntread = (actionName.equalsIgnoreCase("����") || actionName.equalsIgnoreCase("���̻���"));
    }

    public String toHtml() {
        return WFTraceActorWriter.getHtml(this);
    }

}
