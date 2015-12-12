package com.anyi.gp.workflow.asyn.jmslistener;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.TableData;
import com.anyi.gp.access.WorkflowService;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.workflow.asyn.wfcommand.WfBaseCommand;
import com.anyi.gp.workflow.asyn.wfcommand.WfCommandFactory;
import com.anyi.gp.workflow.asyn.wfcommand.WfNoCommandException;
import com.anyi.gp.workflow.util.WFConst;
import com.anyi.gp.workflow.util.WFException;
import com.anyi.gp.workflow.util.WFUtil;

public class WfCommandListener implements MessageListener {
	private static Logger logger = Logger.getLogger(WfCommandListener.class);

	private WorkflowService wfService;

	public WorkflowService getWfService() {
		return wfService;
	}

	public void setWfService(WorkflowService wfService) {
		this.wfService = wfService;
	}

	public void onMessage(Message mes) {
		// TCJLODO Auto-generated method stub
		MapMessage message = (MapMessage) mes;
		String instanceid = "0";
		String entityName = "";
		try {
			//获取工作流id
			TableData entityData = DataTools.parseData(message
					.getString("entityData"));
			if (entityData != null) {
				instanceid = entityData
						.getFieldValue(WFConst.PROCESS_INST_ID_FIELD);
				//entityName = entityData.getFieldValue("entityName");
			} else {
				instanceid = message.getString("instanceid");
			}
			//获取部件名
			entityName = message.getString("entityName");
			
			String type = mes.getStringProperty("wf_type");
			WfBaseCommand command = (WfBaseCommand) WfCommandFactory
					.getCommand(type);
			command.setWorkflowService(wfService);
			command.execute(mes);
		} catch (Exception ex) {
			WFUtil.setASWfState(instanceid, entityName, "2");
			logger.error(ex);			
		}
		
//		catch (JMSException ex) {
//			WFUtil.setASWfState(instanceid, entityName, "2");
//			logger.error(ex);
//		} catch (WFException ex) {
//			WFUtil.setASWfState(instanceid, entityName, "2");
//			logger.error(ex);
//		} catch (BusinessException ex) {
//			WFUtil.setASWfState(instanceid, entityName, "2");
//			logger.error(ex);
//		} catch (WfNoCommandException ex) {
//			logger.error(ex);
//		}
	}

}
