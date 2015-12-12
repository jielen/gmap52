package com.anyi.gp.workflow.trace;

import com.anyi.gp.taglib.components.Page;

public class WFTraceActorWriter {
    
    public static String getHtml(WFTraceActor actor) {
        StringBuffer result = new StringBuffer();
        result.append("<img border=\"0\" src=\"" + Page.LOCAL_RESOURCE_PATH + "style/img/workflow/");
        if (actor.actionName.equalsIgnoreCase(""))
             result.append("user_task.gif");
        else if (actor.isUntread)
             result.append("user_untread.gif");
        else
             result.append("user_commit.gif");
        result.append("\"><br><p class=\"clsFreeTable\">" + actor.actorName + "<br>");

        String sNote = ((actor.actionDescription.length() > 14)?
                actor.actionDescription.substring(0, 12) + "бнбн":
                actor.actionDescription);
        result.append("<div tips=\"" + actor.actionDescription + "\" onmousemove=\"showTips(this.tips, 1)\" onmouseout=\"showTips(this.tips, 0)\"" );
        result.append("class=\"clsFreeTable\"><i><font color=\"" + WFTraceWriter.DSColor + "\">" + sNote + "</font></i></div></p>");
        return result.toString();
    }

}
