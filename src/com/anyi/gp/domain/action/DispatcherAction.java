package com.anyi.gp.domain.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.domain.OnLineList;
import com.anyi.gp.domain.SecurityRandom;
import com.anyi.gp.domain.User;
import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.sso.NoSuchUserException;
import com.anyi.gp.sso.SessionContext;
import com.anyi.gp.sso.SessionContextBuilder;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.interceptor.ApplicationAware;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

public class DispatcherAction extends ActionSupport implements ServletRequestAware, SessionAware, ApplicationAware {

	private static final long serialVersionUID = 1L;
	
	private HttpServletRequest request = null;

	private Map session = null;

	private Map applicationContext = null;

	private SessionContextBuilder scBuilder = null;
	
	private LicenseManager licenseManager = null;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void setApplication(Map applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	public void setScBuilder(SessionContextBuilder scBuilder) {
		this.scBuilder = scBuilder;
	}
	
	public LicenseManager getLicenseManager() {
		return licenseManager;
	}

	public void setLicenseManager(LicenseManager licenseManager) {
		this.licenseManager = licenseManager;
	}

	public String execute() throws Exception {
		if (checkTokenConsistent()) {
      Object showGlobal = request.getAttribute("showGlobal");
      request.setAttribute("showGlobal", showGlobal == null ? "false" : showGlobal);
			return Action.SUCCESS;
		}
    request.setAttribute("showGlobal", "true");
    
		String token = SecurityRandom.getToken(applicationContext.keySet());
		String username = null;
		User user = (User)request.getAttribute("user");
		if(user == null){
			username = (String)request.getAttribute("username");
			user = GeneralFunc.getLoginUser(username);    
		  if (user == null) {
		    throw new NoSuchUserException();
		  }
		}
		SessionContext sc = this.scBuilder.getSessionContext(user);
		session.put(SessionUtils.CURRENT_USER_TOKEN, token);
		applicationContext.put(token, sc);
		GeneralFunc.saveSessionToDB(user.getUserCode(), sc.getSessionMap());
		loginAfter(user, token);
		
		boolean localResource = Pub.parseBool(ApplusContext.getEnvironmentConfig().get("localResource"));
		localResource=false;
		if (localResource) {
			return "resource";
		} else {
      String url = (String)request.getAttribute("url");
      if(url == null || url.length() == 0){
        url = "/dispatcher.action?function=mainFrame&token=" + token;
      }
//      if(url != null && url.length() > 0){
//        RequestDispatcher dispatcher = request.getRequestDispatcher(url);
//        dispatcher.forward(request, ServletActionContext.getResponse());
//        return Action.NONE;
//      }
//			return Action.SUCCESS;
      url = Pub.encodeUrl(url);
      ServletActionContext.getResponse().sendRedirect(request.getContextPath() + url);
      return Action.NONE;
		}
	}
	
	/**
	 * 设置在线统计记录并将token放入表as_user_ticket中
	 * 
	 * @param user
	 * @param token
	 */
	private void loginAfter(User user, String token) {
		Map data = new HashMap();
		data.put("LOGIN_TIME", System.currentTimeMillis() + "");
		data.put("LOING_USER_ID", user.getUserCode());
		data.put("LOING_USER_NAME", user.getUserName());
		data.put("REMOTE_IP", request.getRemoteAddr());
		OnLineList.getInstance().add(token, data);

		GeneralFunc.saveTokenToDB(token, user.getUserCode());
	}
	
	public DispatcherAction(SessionContextBuilder scBuilder) {
		super();
		this.scBuilder = scBuilder;
	}
	
	/**
	 * 检查token是否一致，从而避免重复登录
	 * 
	 * @return
	 */
	private boolean checkTokenConsistent() {
		String rToken = (String)request.getAttribute("token");
		if (rToken != null) {
			String sToken = (String) session.get(SessionUtils.CURRENT_USER_TOKEN);
			if (sToken != null && rToken.equals(sToken))
				return true;
		}
		return false;
	}

}
