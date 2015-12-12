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
 * ���ݷ���ʱ,���������������ʱ,����ɾ����������.�Ա�֤�����ܹ����·���.
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
			logger.error("��ִ�����ݷ���ͬ����:ContentPublishTaskListener��ʱ��,����������ִ�г���:"
					+e.getMessage());
			e.printStackTrace();
      throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error("��ִ�����ݷ���ͬ����:ContentPublishTaskListener��ʱ��,����AS_PUBLISH_CONTENTִ�г���:"
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
