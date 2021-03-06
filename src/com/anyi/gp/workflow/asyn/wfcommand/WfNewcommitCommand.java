package com.anyi.gp.workflow.asyn.wfcommand;

import javax.jms.MapMessage;

import com.anyi.gp.TableData;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.workflow.util.WFConst;

public class WfNewcommitCommand extends WfBaseCommand {
	
	public void doExecute(MapMessage message) throws Exception {

			TableData entityData = DataTools.parseData(message
					.getString("entityData"));
			String entityName = message.getString("entityName");
			String user = message.getString("user");
			TableData data = DataTools.parseData(message.getString("wfData"));
			this.getWorkflowService().wfNewCommit(entityData, entityName, user, data);
			
			String instanceid = entityData.getFieldValue(WFConst.PROCESS_INST_ID_FIELD);
			
			this.setInstanceid(instanceid);
			this.setEntityName(entityName);
	}
}
