package com.anyi.gp.interceptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anyi.gp.access.CommonService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;

public class LoggingInterceptor extends AroundInterceptor {

  private static final long serialVersionUID = -8969274015745774436L;

  private static final Log log = LogFactory.getLog(LoggingInterceptor.class);

  private static final String FINISH_MESSAGE = "Finishing execution stack for action ";

  private static final String START_MESSAGE = "Starting execution stack for action ";

  protected void after(ActionInvocation invocation, String result) throws Exception {
    logMessage(invocation, FINISH_MESSAGE);
    CommonService service = (CommonService)ApplusContext.getBean("commonService");
    HttpServletRequest request = ServletActionContext.getRequest();
    String userId = SessionUtils.getAttribute(request, "svUserID");
    String userName = SessionUtils.getAttribute(request, "svUserName");
    String compoId = (String)request.getAttribute("componame");
    String funcId = invocation.getProxy().getActionName();
    if("all".equalsIgnoreCase(compoId) || "*".equals(compoId))
      return;
    service.logUserFunc(userId, userName, compoId, funcId, "", true);
  }

  protected void before(ActionInvocation invocation) throws Exception {
    logMessage(invocation, START_MESSAGE);
  }

  private void logMessage(ActionInvocation invocation, String baseMessage) {
    if (log.isDebugEnabled()) {
      StringBuffer message = new StringBuffer(baseMessage);
      String namespace = invocation.getProxy().getNamespace();

      if ((namespace != null) && (namespace.trim().length() > 0)) {
        message.append(namespace).append("/");
      }

      message.append(invocation.getProxy().getActionName());
      
      if(START_MESSAGE.equals(baseMessage)){
        Map parameters = invocation.getInvocationContext().getParameters();
        Set entrySet = parameters.entrySet();
        Iterator iterator = entrySet.iterator();
        while(iterator.hasNext()){
          Entry entry = (Entry)iterator.next();
          message.append("\n" + entry.getKey());
          message.append("=");
          Object value = entry.getValue();
          if(value instanceof String[]){
            String[] st = (String[])(entry.getValue());
            for(int i = 0; i < st.length; i++){
              message.append(st[i]);
            }
          }else{
            message.append(value);
          }
        }
      }
      log.info(message.toString());
    }
  }
}
