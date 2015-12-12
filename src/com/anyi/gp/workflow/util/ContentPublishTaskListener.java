package com.anyi.gp.workflow.util;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.workflow.WFFactory;
import com.anyi.gp.workflow.WFService;
import com.kingdrive.workflow.dto.ActionMeta;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.listener.TaskListener;

/**
 * 内容发布时,如果结束发布流程时,用于删除发布内容.以保证部件能够重新发布.
 * @author zhangcheng
 *
 */
public class ContentPublishTaskListener implements TaskListener {
	final static Logger logger=Logger.getLogger(ContentPublishTaskListener.class);
	final String SQL_DELETE_AS_PUBLISH_CONTENT="delete from AS_PUBLISH_CONTENT where PROCESS_INST_ID = ?"; 
	public void beforeExecution(CurrentTaskMeta arg0) {
	}

	public void afterExecution(CurrentTaskMeta task) {
		int nInstanceId=task.getInstanceId();
		WFService wfService=WFFactory.getInstance().getService();
		try {
			if(wfService.isInstanceFinished(nInstanceId)){
				deletePublishContent(nInstanceId);
				
			};
		} catch (WFException e) {
			logger.error("在执行内容发布同步类:ContentPublishTaskListener的时候,工作流引擎执行出错:"
					+e.getMessage());
			e.printStackTrace();
      throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error("在执行内容发布同步类:ContentPublishTaskListener的时候,更新AS_PUBLISH_CONTENT执行出错:"
					+e.getMessage());
			e.printStackTrace();
      throw new RuntimeException(e);
		}

	}

	private void deletePublishContent(int instanceId) throws SQLException {
		Integer[] params=new Integer[1];
		params[0]=new Integer(instanceId);
		DBHelper.executeUpdate(SQL_DELETE_AS_PUBLISH_CONTENT,params);
	}
    public void beforeUntread(ActionMeta meta) {
      ;//do nothing
    }

    public void afterUntread(ActionMeta meta)  {
      ;//do nothing
    }
    
}
