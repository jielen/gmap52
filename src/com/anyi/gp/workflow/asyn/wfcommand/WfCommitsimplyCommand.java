package com.anyi.gp.workflow.asyn.wfcommand;

import javax.jms.MapMessage;

import com.anyi.gp.TableData;
import com.anyi.gp.pub.DataTools;

public class WfCommitsimplyCommand extends WfBaseCommand {
	
	public void doExecute(MapMessage message) throws Exception {
		String instanceid = message.getString("instanceid");

		String compoid = message.getString("entityName");
		
		String templateid = message.getString("templateid");
		
		String user = message.getString("user");
		
		TableData wfData = DataTools.parseData(message.getString("wfData"));
		TableData bnData = DataTools.parseData(message.getString("bnData"));
		
		this.getWorkflowService().commitSimply(instanceid, templateid, compoid, user, wfData, bnData);
		
		this.setInstanceid(instanceid);
		this.setEntityName(compoid);
	}
}
