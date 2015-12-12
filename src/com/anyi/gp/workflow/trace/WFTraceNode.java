package com.anyi.gp.workflow.trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;


public class WFTraceNode {

    private List list = new ArrayList();
    
    public int nodeIndex = 0;               // �ڵ�����ֵ
    public String nodeName = "";            // �ڵ�����
    public boolean hasRollback = false;     // �Ƿ�������˴���
    public String createTime = "";          // �ڵ�������յ������ʱ��
    public String commitTime = "";          // �ڵ������ύʱ��
    public String rollbackTime = "";        // �ڵ��������ʱ��

    public WFTraceActor getActor(String actorName) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            WFTraceActor actor = (WFTraceActor)iterator.next();
            if (actor.actorName.equalsIgnoreCase(actorName))
                return actor;
        }
        return null;
    }
    
    public void addActor(TableData row) {
        String actorName = row.getFieldValue(WFConst.WF_TASK_OWNER_NAME).trim();
        WFTraceActor actor = getActor(actorName);
        if (actor == null) {
            actor = new WFTraceActor();
            list.add(actor);
        }
        actor.loadFrom(row);
        if (actor.actionName.equalsIgnoreCase("����") || actor.actionName.equalsIgnoreCase("���̻���")) {
            hasRollback = true;
            if (actor.executeTime.length() > 0 && rollbackTime.compareToIgnoreCase(actor.executeTime) < 0)
                rollbackTime = actor.executeTime;
        } else {
            if (actor.executeTime.length() > 0 && commitTime.compareToIgnoreCase(actor.executeTime) < 0)
                commitTime = actor.executeTime;
        }
    }

    public String titleToHtml() {
        return WFTraceNodeWriter.getTitleHtml(this);
    }

    public String imageToHtml() {
        return WFTraceNodeWriter.getImageHtml(this);
    }

    public Iterator iterator() {
        return list.iterator();
    }

    public ListIterator listIterator() {
        return list.listIterator();
    }

    public ListIterator listIterator(int index) {
        return list.listIterator(index);
    }
    
}
