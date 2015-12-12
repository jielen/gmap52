package com.anyi.gp.domain.action;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.domain.User;
import com.anyi.gp.pub.SessionUtils;
import com.anyi.gp.sso.AuthenticationFailedException;
import com.anyi.gp.sso.IdentityAuthenticator;
import com.anyi.gp.sso.IpIncorrectException;
import com.anyi.gp.sso.NoSuchUserException;
import com.anyi.gp.sso.SessionContext;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.interceptor.ServletRequestAware;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

public class LoginAction extends ActionSupport implements ServletRequestAware{

	private static final long serialVersionUID = -4206402674905296285L;

	private HttpServletRequest request = null;

	private IdentityAuthenticator authenticator = null;

	public String execute() throws Exception{
		if (!isAllowLogin()) {
			request.setAttribute("fail", "Çë´ÓadminµÇÂ½£¡");
			return Action.INPUT;
		}
		
    User ui = null;
    if(checkTokenConsistent()){
      ServletContext context = request.getSession().getServletContext();
      SessionContext sessionContext = (SessionContext) context.getAttribute((String)request.getAttribute("token"));
      ui = sessionContext.getCurrentUser();
    }else{
  		try {
  			ui = authenticator.getLoginUser(request);
  		} catch (NoSuchUserException e) {
  			addFieldError("invalidUser", "*");
  			request.setAttribute("fail", "ÓÃ»§Ãû´íÎó£¡");
  			return Action.INPUT;
  		} catch (AuthenticationFailedException e) {
  			addFieldError("invalidPassword", "*");
  			request.setAttribute("fail", "ÃÜÂë´íÎó£¡");
  			return Action.INPUT;
  		} catch (IpIncorrectException e) {
  			addFieldError("invalidIp", "*");
  			request.setAttribute("fail", "µÇÂ¼ipµØÖ·´íÎó£¡");
  			return Action.INPUT;
  		}
    }
		request.setAttribute("user", ui);
		
		String url = "";
		String function = request.getParameter("function");
		if(function == null || "".equals(function)){
			url = request.getContextPath() + "/loginDispatcher.action";
		}else{
			url = request.getContextPath() + "/" + function + ".action";
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		dispatcher.forward(request, ServletActionContext.getResponse());
		return null;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public LoginAction(IdentityAuthenticator authenticator) {
		super();
		this.authenticator = authenticator;
	}

	private boolean isAllowLogin() {
		String uri = request.getRequestURI();
		uri = uri.substring(1);
		String product = uri.substring(0, uri.indexOf("/"));
		return "admin".equals(product) || "portal".equals(product);
	}
  
  /**
   * ¼ì²étokenÊÇ·ñÒ»ÖÂ£¬´Ó¶ø±ÜÃâÖØ¸´µÇÂ¼
   * 
   * @return
   */
  private boolean checkTokenConsistent() {
    String rToken = (String)request.getAttribute("token");
    if (rToken != null) {
      String sToken = (String) request.getSession().getAttribute(SessionUtils.CURRENT_USER_TOKEN);
      if (sToken != null && rToken.equals(sToken))
        return true;
    }
    return false;
  }
}
