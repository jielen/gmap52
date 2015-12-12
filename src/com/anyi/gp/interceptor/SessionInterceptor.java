package com.anyi.gp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.sso.SessionContext;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * 
 * @deprecated
 * @see RequestWrappingFilter
 */
public class SessionInterceptor implements Interceptor{

  private static final long serialVersionUID = 3084450132784978961L;

  public void destroy() {
  }

  public void init() {
  }

  /**
   * session��Ч�ڼ��
   * ���㣺session���Ƿ���token������webӦ�����Ƿ�����д�token��session��Ϣ
   */
  public String intercept(ActionInvocation invocation) throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();   
    SessionContext sessionContext = GeneralFunc.copySessionContext(request);
    if(sessionContext == null){      
      HttpServletResponse response = ServletActionContext.getResponse();        
      DataTools.printSessionError(request, response);
      
      return "none";     
    }else{
      return invocation.invoke();
    }
  }
  

}
