package com.anyi.gp.domain.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.Pub;

/*
 * 安全非法过滤，包括SQL INJECTION 和 XSS两方面的问题
 * XSS过滤：HTML标签，SCRIPT标签，STYLE标签
 * SQL INJECTION过滤：主要对condition参数过滤 select |update |insert|delete|drop|create|union |= 等关键字
 * zuodf 2009-03-18
 */
public class ApplusSafeFilter implements Filter {
  
  private static final Logger logger = Logger.getLogger(ApplusSafeFilter.class);
   
  public static final String REQUEST_INVALIDE_URL = "/jsp/platform/requestInvalidity.jsp";
  
  public static final byte CHECK_LEVEL_NONE = 0;
  
  public static final byte CHECK_LEVEL_NORMAL = 1;
  
  public static final byte CHECK_LEVEL_BASE64 = 2;
  
  protected byte checkLevel = 0;
  
  protected List defaultCheckUriList = new ArrayList();
  
  public void init(FilterConfig filterConfig) throws ServletException {
    String value = filterConfig.getInitParameter("checkLevel");
    try{
      checkLevel = (byte)Integer.parseInt(value);
    }catch(Exception e){
      logger.error(e);
    }
    String tmp = filterConfig.getInitParameter("defaultCheckUrlList");
    if(tmp != null && tmp.length() > 0){
      defaultCheckUriList = java.util.Arrays.asList(tmp.split(","));
    }
  }

  public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain next) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    
    request.setCharacterEncoding("GBK");
    
    if(!"get".equalsIgnoreCase(httpRequest.getMethod()) 
      || !httpRequest.getParameterNames().hasMoreElements()){//no parameters
      next.doFilter(httpRequest, response);
      return;
    }
    
    boolean success = true;
    boolean check = false;
    if(defaultCheckUriList.size() > 0){
      String uri = httpRequest.getRequestURI();
      String pathInfo = httpRequest.getQueryString();
      if(pathInfo != null && pathInfo.length() > 0){
        uri += "?" + pathInfo;
      }
      for(int i = 0; i < defaultCheckUriList.size(); i++){
        if(uri.indexOf((String)defaultCheckUriList.get(i)) > 0){
          check = true;
          break;
        }
      }
    }
    
    if(check){
      httpRequest = new ApplusSafeRequestWrapper(httpRequest);
      success = success && checkBase64Digest(httpRequest);
    }else{
      if(checkLevel > CHECK_LEVEL_NONE){
        httpRequest = new ApplusSafeRequestWrapper(httpRequest);
      }
      
      if(success && checkLevel > CHECK_LEVEL_NORMAL){
        success = checkBase64Digest(httpRequest);
      }
    }
    
    if(success){
      next.doFilter(httpRequest, response);
    }else{
      ((HttpServletResponse)response).sendRedirect(httpRequest.getContextPath() + REQUEST_INVALIDE_URL);
    }
  }

  public void destroy() {

  }
  
  
  private boolean checkBase64Digest(HttpServletRequest request){
    String state = request.getParameter(Pub.KEY_APPLUS_STATE);
    if(state == null || state.length() == 0){
      return false;
    }
    
    String pathInfo = request.getQueryString();
    pathInfo = pathInfo.substring(0, pathInfo.indexOf(Pub.KEY_APPLUS_STATE) - 1);
    //if(pathInfo.indexOf("userId") < 0){
    //  pathInfo += "&userId=" + SessionUtils.getAttribute(request, "svUserID");
    //}
    if(state.equals(Pub.encodeHex(pathInfo))){
      return true;
    }
    return false;
  }
}