package com.anyi.gp.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.debug.DataSourceWrapper;
import com.anyi.gp.pub.DataTools;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.sso.SessionContext;

public class RequestWrappingFilter implements Filter {

  private static final Logger logger = Logger.getLogger(RequestWrappingFilter.class);

  public void init(FilterConfig arg0) throws ServletException {

  }

  public void destroy() {

  }

  public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      setThreadLocalParameters((HttpServletRequest) request);
      if (checkSession((HttpServletRequest) request, (HttpServletResponse) response)) {
        chain.doFilter(request, response);
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private static void setThreadLocalParameters(HttpServletRequest request) {
    Object dsKey = request.getParameter("dsKey");
    if (dsKey == null) {
      dsKey = SessionUtils.getAttribute(request, "dsKey");
    }
    if (logger.isDebugEnabled()) {
      logger.debug("dsKey = " + dsKey);
    }
    DataSourceWrapper.setCurrentUser(dsKey);
  }

  private static boolean checkSession(HttpServletRequest request,
    HttpServletResponse response){
    SessionContext sessionContext = null;
    try{
      sessionContext = GeneralFunc.copySessionContext(request);
    }catch(Exception e){
      e.printStackTrace();
      logger.error(e);
    }
    
    if (sessionContext == null) {
      DataTools.printSessionError(request, response);
      return false;
    } else {
      return true;
    }
  }
}
