package com.anyi.gp.workflow.asyn.wfcommand;

public class WfCommandFactory {
	public synchronized static WfCommand getCommand(String command) throws WfNoCommandException {
		
		if (WfCommand.NEWCOMMIT.equals(command)) {
			return new WfNewcommitCommand();
		}
		if (WfCommand.COMMITSIMPLY.equals(command)) {
			return new WfCommitsimplyCommand();
		}
		if (WfCommand.COMMIT.equals(command)) {
			return new WfCommitCommand();
		}
		throw new WfNoCommandException();
	}
}
