package com.kingdrive.framework.controller.web;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspDispatcher {

  public JspDispatcher() {
  }

  public static void forward(ServletContext context,
      HttpServletRequest request, HttpServletResponse response, String url) {
    try {
      response.setContentType("text/html;charset=gb2312");
      context.getRequestDispatcher(url).forward(request, response);
    } catch (IOException e) {
      throw new IllegalStateException("定向到" + url + "发生IOException。"
          + e.toString());
    } catch (ServletException e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static void redirect(HttpServletRequest request,
      HttpServletResponse response, String url) throws IOException {
    try {
      response.sendRedirect(request.getContextPath() + url);
    } catch (IOException e) {
      throw new IllegalStateException("定向到" + url + "发生IOException。"
          + e.toString());
    }
  }
}
