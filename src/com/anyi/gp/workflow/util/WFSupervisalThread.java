/*
 * Created on 2004-11-22
 *
 * �޸ļ�¼ (from 2004-11-22)
 */
package com.anyi.gp.workflow.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.message.MessageFactory;
import com.anyi.gp.message.MsgException;
import com.anyi.gp.util.StringTools;
import com.kingdrive.workflow.ExecuteFacade;
import com.kingdrive.workflow.dto.CurrentTaskMeta;
import com.kingdrive.workflow.exception.WorkflowException;

/**
 * @author   leidaohong
 * @time  2004-11-22
 */
public class WFSupervisalThread extends Thread {
  private static Logger log = Logger.getLogger(WFSupervisalThread.class);

  private int interval = 2000;// default interval 2 seconds

  public void run() {

    while (true) {
      // scan all the todo list
      log.info("�����߳̿�ʼɨ��!");
      try {
        List todoList = ExecuteFacade.getTodoList();
        Iterator iter = todoList.iterator();
        while (iter.hasNext()) {
          CurrentTaskMeta task = (CurrentTaskMeta) iter.next();
          if (!isDeadline(task))
            continue;// TCJLODO handle the condition
          // notifyExecutorBySMS(task);
          notifyExecutorByEmail(task);
        }
        log.info("�����߳�һ��ɨ�����!����ȴ�...");
        sleep(interval);
        // throw new RuntimeException("sdf");
      } catch (WorkflowException e) {
        log.error(e);
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (MsgException e) {
        log.error(e);
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (InterruptedException e) {
        log.error(e);
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * ��ǰ�����Ƿ񵽴��������. ��limit_executor_timeʱ�䳬����ǰϵͳʱ��ʱ����true
   * 
   * @param task
   * @return
   * @throws
   * @see
   */
  private boolean isDeadline(CurrentTaskMeta task) {
    String deadLine = task.getLimitExecuteTime();
    if (deadLine == null)
      return false;
    String currentTime = StringTools.getDateString(new Date(), "yyMMddhhmmss");
    if (deadLine.length() != currentTime.length())
      return false;
    for (int i = 0, j = currentTime.length(); i < j; i++) {
      if (currentTime.charAt(i) > deadLine.charAt(i))
        return true;
    }
    return false;
  }

  /**
   * ͨ��email�ķ�ʽ����������ִ���˰���ǰ����
   * 
   * @param task
   * @throws
   * @see
   */
  private void notifyExecutorByEmail(CurrentTaskMeta task) throws Exception {
    String msgHtml = "";
    String title = "";
    String workContent = "";
    String baseUrl = ApplusContext.getEnvironmentConfig().get("baseHost");
    workContent = task.getTemplateName() + "-" + task.getNodeName();
    msgHtml = workContent + "<br>" + "<br>����" + "<A href=\"" + baseUrl + "\">"
      + "������</A>";
    title = "�����ʼ���";
    MessageFactory.getMessageImp().sendEmailToUser("majian", task.getExecutor(),
      null, null, title, msgHtml, null);
  }

  /**
   * @return   Returns the interval.
   * @uml.property   name="interval"
   */
  public int getInterval() {
    return interval;
  }

  /**
   * @param interval   The interval to set.
   * @uml.property   name="interval"
   */
  public void setInterval(int interval) {
    this.interval = interval;
  }
}
