package com.anyi.gp.workflow.asyn.wfcommand;

public class WfNoCommandException extends Exception {
	
	public WfNoCommandException() {}
	
	public String getMessage() {
		return "没有相应的工作流操作!";
	}
}
