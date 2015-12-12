package com.anyi.gp.workflow.trace;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.taglib.components.Page;

public class WFTraceWriter {

	public static final String NEW_LINE = System.getProperty("line.separator");

	public static final String BKColor = "#FBFBFB";

	public static final String DSColor = "#606060";

	public static final int nodeColWidth = 100; // 节点列宽度

	public static final int lineColWidth = 100; // 连线列宽度

	public static final int titleRowHeight = 50; // 上层行高度

	public static final int imgRowHeight = 180; // 下层行高度

	private static String opt = GeneralFunc.getOption("OPT_CP_BILL_AUDIT_RTN");// 退回到第一个节点时为“-1”

	private static String commitHtml = "<img border=\"0\" src=\""+ Page.LOCAL_RESOURCE_PATH	+ "style/img/workflow/arrowline_commit.gif\">" + NEW_LINE;

	private static String untreadHtml = "<img border=\"0\" src=\""+ Page.LOCAL_RESOURCE_PATH+ "style/img/workflow/arrowline_rollback.gif\">" + NEW_LINE;

	private static List nodeLineHtmlList = new ArrayList();
	private static List nodeCommitTimeList = new ArrayList();
	
	public static String getTitleBlankHtml() {
		StringBuffer result = new StringBuffer();
		result.append("<td align=\"center\" valign=\"top\" ");
		result.append("width=\"" + lineColWidth + "\" ");
		result.append("height=\"" + titleRowHeight + "\">");
		result.append("</td>");
		return result.toString();
	}

	public static String getTitleHtml(WFTrace trace) {
		StringBuffer result = new StringBuffer();
		ListIterator iterator = trace.listIterator();
		while (iterator.hasNext()) {
			WFTraceNode node = (WFTraceNode) iterator.next();
			if (iterator.nextIndex() > 1) {
				result.append(getTitleBlankHtml());
				result.append(NEW_LINE);
			}
			result.append(node.titleToHtml());
			result.append(NEW_LINE);
		}
		return result.toString();
	}

	public static String getImageLineHtml(int i) {
		StringBuffer result = new StringBuffer();
		result.append("<td align=\"center\" valign=\"top\" ");
		result.append("width=\"" + lineColWidth + "\" ");
		result.append("height=\"" + imgRowHeight + "\">");
		result.append("<p class=\"clsFreeTable\">");
		// result.append("<font color=\"" + DSColor + "\"><i>" +previousNode.commitTime + "<br>");
		result.append(nodeCommitTimeList.get(i - 1));
		result.append(nodeLineHtmlList.get(i));
		result.append("</i></font></td>");
		return result.toString();
	}

	public static String getImageHtml(WFTrace trace) {
		StringBuffer result = new StringBuffer();
		ListIterator iterator = trace.listIterator();
		int size = trace.list().size();
		for (int i = 0; i < size; i++) {
			setNodeLineHtml(trace, i);
			setNodeCommitTime(trace, i);
		}
		WFTraceNode node = null;
		while (iterator.hasNext()) {
			int i = iterator.nextIndex();
            node = (WFTraceNode) iterator.next();
			if (i > 0){
				result.append(getImageLineHtml(i));//取流程箭头html、通过时间、退回时间等。
			}
			result.append(NEW_LINE);
			result.append(node.imageToHtml());
			result.append(NEW_LINE);
		}
		return result.toString();
	}

	public static String getHtml(WFTrace trace) {
		StringBuffer result = new StringBuffer();
		result.append(NEW_LINE);
		result.append("<tr>");
		result.append(NEW_LINE);
		result.append(getTitleHtml(trace));
		result.append("</tr>");
		result.append(NEW_LINE);
		result.append("<tr>");
		result.append(NEW_LINE);
		result.append(getImageHtml(trace));
		result.append("</tr>");
		result.append(NEW_LINE);
        nodeLineHtmlList.clear();
        nodeCommitTimeList.clear();
		return result.toString();
	}

	private static void setNodeLineHtml(WFTrace trace, int index) {
		WFTraceNode currentNode = (WFTraceNode) trace.list().get(index);
		nodeLineHtmlList.add(index, "");
		String rollbackTime="<br>" + currentNode.rollbackTime;
		if (!currentNode.hasRollback && index > 0) {
			nodeLineHtmlList.remove(index);
			nodeLineHtmlList.add(index, commitHtml);
		}else if (currentNode.hasRollback && !"-1".equals(opt)) {
			nodeLineHtmlList.remove(index);
			nodeLineHtmlList.add(index, untreadHtml+rollbackTime);
		}else if (currentNode.hasRollback && "-1".equals(opt)) {
			for (int i = 1; i <=index; i++) {
				nodeLineHtmlList.remove(i);
				nodeLineHtmlList.add(i, untreadHtml+rollbackTime);
			}
		}
	}
	
	private static void setNodeCommitTime(WFTrace trace, int index) {
		nodeCommitTimeList.add(index, "<font color=\"" + DSColor + "\"><i><br>");
		WFTraceNode currentNode = (WFTraceNode) trace.list().get(index);
		WFTraceActor actor = (WFTraceActor) currentNode.listIterator().next();
		if(actor.actionName.equalsIgnoreCase("") || "已结束".equals(trace.getInstanceStatus())){
			for (int i = 0; i <=index; i++) {
				WFTraceNode node = (WFTraceNode) trace.list().get(i);
				String commitTime = "<font color=\"" + DSColor + "\"><i>" +node.commitTime + "<br>";
				nodeCommitTimeList.remove(i);
				nodeCommitTimeList.add(i, commitTime);
			}			
		}
	}

}
