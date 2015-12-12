package com.anyi.gp.workflow.trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.workflow.util.WFConst;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.dto.InstanceMeta;
import com.kingdrive.workflow.exception.WorkflowException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author leidaohong
 */
public class WFTrace {

	private List list = new ArrayList();

	private String instanceId = "";

	private String instanceName = "";

	private String instanceStatus = "";

	private String templateName = "";

	private String startTime = "";

	private String firstActionTime = "";

	private String endTime = "";
	
	private int status = 0;

	private void queryInstanceState() {
		instanceStatus = "无法访问";
		InstanceMeta inst;
		try {
			inst = ExecuteFacade.getInstance(Integer.parseInt(instanceId));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (WorkflowException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		setStatus(inst.getStatus());
		switch (status) {
		case 1: {
			instanceStatus = "进行中";
			break;
		}
		case -1: {
			instanceStatus = "已挂起";
			break;
		}
		case 9: {
			instanceStatus = "已结束";
			this.endTime = formatTime(inst.getEndTime());
			break;
		}
		case -9: {
			instanceStatus = "已中止";
			break;
		}
		default: {
			instanceStatus = "未知状态";
		}
		}
	}

	/**
	 * @return Returns the instanceName.
	 * @uml.property name="instanceName"
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * @return Returns the instanceStatus.
	 * @uml.property name="instanceStatus"
	 */
	public String getInstanceStatus() {
		return instanceStatus;
	}

	/**
	 * @return Returns the templateName.
	 * @uml.property name="templateName"
	 */
	public String getTemplateName() {
		return templateName;
	}

	/*
	 * 结束时间
	 */
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return Returns the startTime.
	 * @uml.property name="startTime"
	 */
	public String getStartTime() {
		return (startTime.length() > 0 ? startTime : firstActionTime);
	}

	public WFTraceNode getNodeByName(String nodeName) {
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			WFTraceNode node = (WFTraceNode) iterator.next();
			if (node.nodeName.equalsIgnoreCase(nodeName))
				return node;
		}
		;
		return null;
	}

	public void loadFrom(Delta data) {
		clear();
		ListIterator iterator = data.listIterator(data.size());
		while (iterator.hasPrevious()) {
			TableData row = (TableData) iterator.previous();
			String nodeName = row.getFieldValue(WFConst.WF_NODE_NAME).trim();
			WFTraceNode node = getNodeByName(nodeName);
			if (node == null) {
				node = new WFTraceNode();
				list.add(node);
				node.nodeName = nodeName;
				node.nodeIndex = list.size();
			}
			node.addActor(row);
			if (instanceId.length() == 0)
				instanceId = row.getFieldValue(WFConst.WF_INSTANCE_ID).trim();
			if (instanceName.length() == 0)
				instanceName = row.getFieldValue(WFConst.WF_INSTANCE_NAME)
						.trim();
			if (templateName.length() == 0)
				templateName = row.getFieldValue(WFConst.WF_TEMPLATE_NAME)
						.trim();
			if (startTime.length() == 0)
				startTime = row.getFieldValue(WFConst.WF_INSTANCE_START_TIME)
						.trim();
			String s = row.getFieldValue(WFConst.WF_ACTION_EXECUTE_TIME).trim();
			if (firstActionTime.length() == 0
					|| firstActionTime.compareToIgnoreCase(s) > 0)
				firstActionTime = s;
		}
		queryInstanceState();
	}

	public void setModel(Delta model) {
		if (model == null)
			throw new IllegalArgumentException("无效的请求。缺少model参数。");
		loadFrom(model);
	}

	public void setRequest(HttpServletRequest request) {
		setModel((Delta) request.getAttribute("model"));
	}

	public String toHtml() {
		return WFTraceWriter.getHtml(this);
	}

	public void clear() {
		list.clear();
	}

	public Iterator iterator() {
		return list.iterator();
	}

	public ListIterator listIterator() {
		return list.listIterator();
	}
	public List list() {
		return list;
	}

	public ListIterator listIterator(int index) {
		return list.listIterator(index);
	}

	private String formatTime(String temp) {
		String result = "";
		String timePattern = "(\\d\\d\\d\\d)(\\d?\\d)(\\d?\\d)(\\d?\\d)(\\d?\\d)(\\d?\\d)";
		Pattern pattern = Pattern.compile(timePattern);
		Matcher matcher = pattern.matcher(temp);
		if (matcher.matches()) {
			result = matcher.group(1) + "-" + matcher.group(2) + "-"
					+ matcher.group(3) + " " + matcher.group(4) + ":"
					+ matcher.group(5) + ":" + matcher.group(6);
		} 
		return result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
