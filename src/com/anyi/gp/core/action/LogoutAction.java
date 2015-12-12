package com.anyi.gp.core.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.anyi.gp.access.CommonService;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.webwork.interceptor.ServletResponseAware;

public class LogoutAction extends AjaxAction implements ServletRequestAware,
		ServletResponseAware {

	private static final long serialVersionUID = -169692263124760244L;

  private static final Logger logger = Logger.getLogger(LogoutAction.class);
  
  private static final String SESSION_ID = "JSESSIONID";
  
	private String userid;

	private String token;

	private CommonService service;

	private HttpServletRequest request;

	private HttpServletResponse response;

	public CommonService getService() {
		return service;
	}

	public void setService(CommonService service) {
		this.service = service;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String doExecute() throws Exception {

		long currTime = System.currentTimeMillis();// //GeneralFunc.getSysTime();
		Map sqlMap = new HashMap();
		sqlMap.put("CURRENT_TIME", currTime + "");
		sqlMap.put("USER_ID", userid);
		sqlMap.put("TICKET_ID", token);

		service.logout(sqlMap);
		
    String sessionId = request.getSession().getId();
		if (token != null) {
			ServletContext servletContext = request.getSession().getServletContext();
			List appNames = (List) servletContext.getAttribute(SessionUtils.APP_NAME_LIST_KEY);
			if (appNames == null) {
				appNames = GeneralFunc.getAppNames();
			}

			if (appNames != null) {
				for (int i = 0; i < appNames.size(); i++) {// 遍历所有web应用
					String appName = (String) appNames.get(i);
					ServletContext sc = servletContext.getContext("/" + appName);
          if (sc != null) {
            Object obj = sc.getAttribute(token);
            if (obj != null) {
              sc.setAttribute(token, null);
              sc.removeAttribute(token);
              if (sc.getResourceAsStream("/sessionInvalidate.jsp") != null) {
                try{
                  String url = "/sessionInvalidate.jsp?" + SESSION_ID + "=" + sessionId;
                  RequestDispatcher proxyServlet = sc.getRequestDispatcher(url);
                  proxyServlet.include(request, response);
                }catch(IOException e){
                  logger.error(e);
                }
              }
            }
          }
				}
			}
			request.getSession().invalidate();
		}

		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
}
