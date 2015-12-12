package com.anyi.gp.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.domain.OnLineList;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class SessionCheckInterceptor implements Interceptor{

  private static final long serialVersionUID = -2158947383630418537L;

  private static final Logger logger = Logger.getLogger(SessionCheckInterceptor.class);
    
  public void destroy() {
    // TCJLODO Auto-generated method stub
    
  }

  public void init() {
    // TCJLODO Auto-generated method stub
    
  }

  public String intercept(ActionInvocation invocation) throws Exception {
    checkSessionDestroyed(invocation);
    return invocation.invoke();
  }

  private void checkSessionDestroyed(ActionInvocation invocation){
    Map applicationContext = invocation.getInvocationContext().getApplication();
    
    String userId = ServletActionContext.getRequest().getParameter("username");
    String sToken = (String) ServletActionContext.getRequest().getSession().getAttribute(SessionUtils.CURRENT_USER_TOKEN);
    List tokenList = GeneralFunc.getTokenFromDB(null, userId);
    if(tokenList != null && !tokenList.isEmpty()){
      CommonService service = (CommonService)ApplusContext.getBean("commonService");
      for(int i = 0; i < tokenList.size(); i++){
        String token = (String)((Map)tokenList.get(i)).get("TICKET_ID");
        if(token.equals(sToken)) continue;
        String createTime = (String)((Map)tokenList.get(i)).get("CREATE_TIME");
        long currentTime = System.currentTimeMillis();
        if((currentTime - Long.parseLong(createTime)) < 3600 * 60 * 24) continue;//ÑÓ³ÙÒ»ÌìÏú»Ùtoken
        
        if(applicationContext.containsKey(token)){
          logger.warn(" have token : " + token);
          applicationContext.put(token, null);
          applicationContext.remove(token);
          try {
            OnLineList.getInstance().remove(token);
          } catch (BusinessException e) {
            logger.error(e);
          }
        }
        Map sqlMap = new HashMap();
        sqlMap.put("USER_ID", userId);
        sqlMap.put("TICKET_ID", token);
        try {
          service.logout(sqlMap);
        } catch (BusinessException e) {
          logger.error(e);
        }
      }
    }
  }
}
