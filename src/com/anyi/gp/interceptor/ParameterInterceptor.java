package com.anyi.gp.interceptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.debug.DataSourceWrapper;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * 
 * 增加参数化处理拦截器：将参数放入request的attributeMap中
 * @author liuxiaoyong
 *
 */
public class ParameterInterceptor implements Interceptor{

  private static final long serialVersionUID = 5445981977566748845L;

  public void destroy() {
  }

  public void init() { 
  }

  public String intercept(ActionInvocation invocation) throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();
    Map parameters = request.getParameterMap();
    
    Set entrySet = parameters.entrySet();
    Iterator iterator = entrySet.iterator();
    
    while(iterator.hasNext()){
      Entry entry = (Entry)iterator.next();
      Object obj = entry.getValue();
      if(obj instanceof Object[] && ((Object[])obj).length == 1){
        request.setAttribute((String)entry.getKey(), ((Object[])obj)[0]);
      }
      else{
        request.setAttribute((String)entry.getKey(), obj);
      }
    }
    
    //setThreadLocalParameters();
    
    return invocation.invoke();
  }

  private void setThreadLocalParameters(){
    HttpServletRequest request = ServletActionContext.getRequest();
    Object dsKey = request.getParameter("dsKey");
    if(dsKey == null){
      dsKey = SessionUtils.getAttribute(request, "dsKey");
    }      
     
    DataSourceWrapper.setCurrentUser(dsKey);
  }
}
