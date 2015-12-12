package com.anyi.gp.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.anyi.gp.BusinessException;
import com.anyi.gp.access.CommonService;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;

public class ApplusSessionListener implements HttpSessionListener, HttpSessionAttributeListener {

  public void sessionCreated(HttpSessionEvent se) {
  }

  public void sessionDestroyed(HttpSessionEvent se) {
    HttpSession session = se.getSession();
    String token = (String) session.getAttribute(SessionUtils.CURRENT_USER_TOKEN);
    ServletContext context = session.getServletContext();

    if (token != null) {
      context.setAttribute(token, null);
      context.removeAttribute(token);
      
      try {
        OnLineList.getInstance().remove(token);
      } catch (BusinessException e) {
        e.printStackTrace();
      }
    }
    clearLoginInfo(session);
    session.setAttribute(SessionUtils.CURRENT_USER_TOKEN, null);
    session.removeAttribute(SessionUtils.CURRENT_USER_TOKEN);
  }

  /**
   * session属性添加事件，session中仅存放SessionUtils.CURRENT_USER_TOKEN
   */
  public void attributeAdded(HttpSessionBindingEvent se) {
    //if(!SessionUtils.CURRENT_USER_TOKEN.equals(se.getName())
     // && !se.getName().startsWith("svLocalResource")){
      //se.getSession().setAttribute(se.getName(), null);
      //se.getSession().removeAttribute(se.getName());
    //}
  }

  public void attributeRemoved(HttpSessionBindingEvent se) {
   
  }

  public void attributeReplaced(HttpSessionBindingEvent se) {
    
  }

  protected void clearLoginInfo(HttpSession session){
    ServletContext sc = session.getServletContext();
    String token = (String)session.getAttribute(SessionUtils.CURRENT_USER_TOKEN);
    boolean clearDB = true;
    if(token == null) return;
    
    List appNames = GeneralFunc.getAppNames();
    for(int i = 0; i < appNames.size(); i++){
      ServletContext tmp = sc.getContext("/" + appNames.get(i));
      if(tmp == null) continue;
      if(tmp.getAttribute(token) != null){
        clearDB = false;
        break;
      }
    }
    
    if(clearDB){
      CommonService service = (CommonService)ApplusContext.getBean("commonService");
      String userId = SessionUtils.getAttribute(session, "svUserID");
      Map param = new HashMap();
      param.put("USER_ID", userId);
      param.put("TICKET_ID", token);
      try {
        service.logout(param);
      } catch (BusinessException e) {
        e.printStackTrace();
      }
    }
  }
}
