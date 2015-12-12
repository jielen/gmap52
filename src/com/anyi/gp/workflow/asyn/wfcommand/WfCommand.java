package com.anyi.gp.workflow.asyn.wfcommand;

import javax.jms.Message;

import com.anyi.gp.workflow.util.WFException;

public interface WfCommand {
	public static final String NEWCOMMIT = "newcommit";
	public static final String COMMITSIMPLY = "commitSimply";
	public static final String COMMIT = "commit";
	
	public void execute(Message mess) throws WFException;
}
