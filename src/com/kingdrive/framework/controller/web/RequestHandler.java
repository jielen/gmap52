package com.kingdrive.framework.controller.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kingdrive.framework.controller.web.action.WebAction;
import com.kingdrive.framework.exception.GeneralException;

public class RequestHandler extends HttpServlet {

  private HashMap mappings;

  private String exceptionScreen;

  public RequestHandler() {
  }

  public void init(ServletConfig sc) throws ServletException {
    super.init(sc);
    try {
      String requestMappingsURL = sc.getServletContext().getResource(
          "/WEB-INF/mappings.xml").toString();
      mappings = MappingsXMLDao.loadRequestMappings(requestMappingsURL);
      exceptionScreen = MappingsXMLDao.loadExceptionScreen(requestMappingsURL);
    } catch (Exception e) {
      throw new ServletException(e.getMessage());
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    doProcess(req, res);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    doProcess(req, res);
  }

  private void doProcess(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {

    SessionManager tps = SessionManager.getSessionManager(req.getSession(true));
    try {
      String screen = processRequest(req, tps);
      JspDispatcher.forward(getServletContext(), req, res, screen);
    } catch (Exception ex) {
      ex.printStackTrace();
      if (exceptionScreen == null) {
        try {
          PrintWriter out = res.getWriter();
          out.write("<HTML>");
          out.write("<TITLE>");
          out.write("Default error page");
          out.write("</TITLE>");
          out.write("<BODY>");
          out.write(ex.getMessage());
          out.write("</BODY>");
          out.write("</HTML>");
        } catch (Exception exception) {
          throw new ServletException(exception.getMessage());
        }
      }
      tps.put(SessionManager.TDS, "EXCEPTION", ex);
      JspDispatcher.forward(getServletContext(), req, res, exceptionScreen);
    }
  }

  private String processRequest(HttpServletRequest req, SessionManager tps)
      throws GeneralException {
    // get the request url name
    String fullURL = req.getRequestURI();
    String selectedURL = null;
    int lastPathSeparator = fullURL.lastIndexOf("/") + 1;
    if (lastPathSeparator != -1) {
      selectedURL = fullURL.substring(lastPathSeparator, fullURL.length());
    }

    URLMapping mapping = (URLMapping) mappings.get(selectedURL);

    if (mapping.isClearParameter()) {
      tps.clear(SessionManager.PDS);
    }
    if (mapping.isClearTemp()) {
      tps.clear(SessionManager.TDS);
    }

    for (Enumeration params = req.getParameterNames(); params.hasMoreElements();) {
      String name = (String) params.nextElement();
      String value = "";
      String values[] = req.getParameterValues(name);
      for (int i = 0; i < values.length; i++) {
        value += values[i];
        if (i < values.length - 1) {
          value += ",";
        }
      }
      if (value != null) {
        try {
          value = new String(value.getBytes("iso-8859-1"), "gb2312");
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException("字符集转换错误！Byte字符处理：" + e.toString());
        }
      }
      tps.put(SessionManager.PDS, name, value);
    }

    String screen = null;
    if (mapping.getWebActionClass() != null) {
      String webActionClassName = mapping.getWebActionClass();
      WebAction action = null;
      try {
        action = (WebAction) Class.forName(webActionClassName).newInstance();
      } catch (Exception e) {
        throw new RuntimeException("实始化类" + webActionClassName
            + "时发生错误！\n详细错误信息为：" + e.getMessage());
      }
      action.setSessionManager(tps);

      List events = mapping.getEventMappings();
      for (int i = 0; i < events.size(); i++) {
        EventMapping event = (EventMapping) events.get(i);
        String eventName = event.getEvent();
        if (eventName == null) {
          throw new RuntimeException(selectedURL + "配置错误，事件为空！");
        }
        String eventResult = action.perform(eventName);
        if (eventResult != null) {
          screen = event.getScreen(eventResult);
          if (screen != null) {
            break;
          }
        }
      }
    }

    if (screen == null) {
      screen = mapping.getScreen();
      if (screen == null) {
        throw new RuntimeException(selectedURL + "配置错误，没有指定任何返回页面！");
      }
    }
    return screen;
  }
}
