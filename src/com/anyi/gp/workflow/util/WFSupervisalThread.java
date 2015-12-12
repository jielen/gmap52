/*
 * Created on 2004-11-22
 *
 * 修改记录 (from 2004-11-22)
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
      log.info("督办线程开始扫描!");
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
        log.info("督办线程一次扫描完成!进入等待...");
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
   * 当前任务是否到达截至期限. 当limit_executor_time时间超过当前系统时间时返回true
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
   * 通过email的方式，督促任务执行人办理当前任务
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
    msgHtml = workContent + "<br>" + "<br>请您" + "<A href=\"" + baseUrl + "\">"
      + "办理工作</A>";
    title = "督办邮件！";
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
