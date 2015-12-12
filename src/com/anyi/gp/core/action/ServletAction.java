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
 * 封装Request和Response的action
 * 继承ServletAction的类，必须实现抽象方法execute(HttpServletRequest, HttpServletResponse)
 * 若有返回值则得设置resultstring，execute(HttpServletRequest, HttpServletResponse)的返回值仅仅是webwork的返回类型
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
   * @return SUCCESS, NONE, FAILED, ERROR等，真正的返回值是resultstring，resultstring在此方法中设定。
   */
  public abstract String execute(HttpServletRequest request, HttpServletResponse response) throws BusinessException;
}
