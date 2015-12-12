package com.anyi.gp.workflow.trace;

import java.util.Iterator;

public class WFTraceNodeWriter {

    public static String getTitleHtml(WFTraceNode node) {
        StringBuffer result = new StringBuffer();
        result.append("<td align=\"center\" valign=\"top\" ");
        result.append("width=\"" + WFTraceWriter.nodeColWidth + "\" ");
        result.append("height=\"" + WFTraceWriter.titleRowHeight + "\" ");
        result.append("bgcolor=\"" + WFTraceWriter.BKColor + "\">");
        result.append("<p class=\"clsFreeTable\"><u><b>" + node.nodeIndex + "." + node.nodeName + "</b></u>");
        result.append("</td>");
        return result.toString();
    }

    public static String getImageHtml(WFTraceNode node) {
        StringBuffer result = new StringBuffer();
        result.append("<td align=\"center\" valign=\"top\"" );
        result.append("width=\"" + WFTraceWriter.nodeColWidth + "\" ");
        result.append("height=\"" + WFTraceWriter.imgRowHeight + "\" ");
        result.append("bgcolor=\"" + WFTraceWriter.BKColor + "\">");
        result.append(WFTraceWriter.NEW_LINE);
        Iterator iterator = node.iterator();
        while (iterator.hasNext()) {
            WFTraceActor actor = (WFTraceActor) iterator.next();
            result.append(actor.toHtml());
            result.append("<br>");
            result.append(WFTraceWriter.NEW_LINE);
        }
        result.append("</td>");
        return result.toString();
    }

}
