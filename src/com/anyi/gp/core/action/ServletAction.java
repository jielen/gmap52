package com.anyi.gp.core.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.webwork.interceptor.ServletResponseAware;

/**
 *
 * ��װRequest��Response��action
 * �̳�ServletAction���࣬����ʵ�ֳ��󷽷�execute(HttpServletRequest, HttpServletResponse)
 * ���з���ֵ�������resultstring��execute(HttpServletRequest, HttpServletResponse)�ķ���ֵ������webwork�ķ�������
 *
 * @author liuxiaoyong
 *
 */
public abstract class ServletAction extends AjaxAction implements ServletRequestAware, ServletResponseAware{

  private static final Logger log = Logger.getLogger(ServletAction.class);
  
  protected HttpServletRequest request;

  protected HttpServletResponse response;

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setServletResponse(HttpServletResponse response) {
    this.response = response;
  }

  public String doExecute(){
    JtaTransactionManager txManager = (JtaTransactionManager)ApplusContext.getBean("currentTransactionManager");
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    TransactionStatus status = txManager.getTransaction(def);
    boolean bRollback = false;
    String result = SUCCESS;
    
    try{
      result = execute(request, response);
    }
    catch(Exception be){
      log.debug(be);
      resultstring = Pub.makeRetInfo2(false, "", be.getMessage(), "");
      txManager.rollback(status);
      bRollback = true;
    }catch(Error be){
      log.debug(be);
      resultstring = Pub.makeRetInfo2(false, "", be.getMessage(), "");
      txManager.rollback(status);
      bRollback = true;
    }
    
    if(!bRollback)
      txManager.commit(status);
    
    return result;
  }

  /**
   *
   * @param request
   * @param response
   * @return SUCCESS, NONE, FAILED, ERROR�ȣ������ķ���ֵ��resultstring��resultstring�ڴ˷������趨��
   */
  public abstract String execute(HttpServletRequest request, HttpServletResponse response) throws BusinessException;
}
