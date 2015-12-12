package com.anyi.gp.pub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.anyi.gp.Pub;
import com.anyi.gp.sso.SessionContext;

public class SessionUtils{

	public static final String CURRENT_USER_TOKEN = "current.user.token";

  public static final String TOKEN = "token";
  
  public static String APP_NAME_LIST_KEY = "app.name.list";
  
	/**
	 * 取session中的key对应的值
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getAttribute(HttpServletRequest request, String key){
		return getAttribute(request.getSession(), key);
	}

	/**
	 * 取session中的key对应的值
	 * 
	 * @param session
	 * @param key
	 * @return
	 */
	public static String getAttribute(HttpSession session, String key){
		ServletContext context = session.getServletContext();
		String token = getToken(session);
    if(token == null)
      return "";
    
		SessionContext sessionContext = (SessionContext) context.getAttribute(token);
		if(sessionContext == null)
			return "";
		
    return (String)Pub.isNull(sessionContext.get(key), "");
    
	}

  public static String getToken(HttpServletRequest request){
    return getToken(request.getSession());
  }
  
  public static String getToken(HttpSession session){
    return (String) session.getAttribute(CURRENT_USER_TOKEN);
  }
  
	public static Set getAllPropertyNames(HttpSession session){
		ServletContext context = session.getServletContext();
		String token = (String) session.getAttribute(CURRENT_USER_TOKEN);
		SessionContext sessionContext = (SessionContext) context.getAttribute(token);
    
		if(sessionContext == null)
			return null;
    
		return sessionContext.getAllPropertyNames();
	}
	
	public static Set getAllPropertyNames(HttpServletRequest request){
		return getAllPropertyNames(request.getSession());
	}

	public static Set getAllSvPropNames(HttpSession session){
		Set res = new HashSet();
		Set props = getAllPropertyNames(session);
		if(props != null){
			Iterator iterator = props.iterator();
			while(iterator.hasNext()){
				String pName = (String)iterator.next();
				if (pName.startsWith("sv")) {
					res.add(pName);
				}
			}
		}
		return res;
	}
	
	public static Set getAllSvPropNames(HttpServletRequest request){
		return getAllSvPropNames(request.getSession());
	}
  
  public static Map getAllSessionVariables(HttpServletRequest request){
    return getAllSessionVariables(request.getSession());
  }
  
  public static Map getAllSessionVariables(HttpSession session){
    ServletContext context = session.getServletContext();
    String token = (String) session.getAttribute(CURRENT_USER_TOKEN);
    SessionContext sessionContext = (SessionContext) context.getAttribute(token);

    if(sessionContext == null)
      return null;
   
    Set svPropNames = getAllSvPropNames(session);
    if(svPropNames == null)
      return null;
    
    Map sessionMap = new HashMap();
    Iterator itera = svPropNames.iterator();
    while(itera.hasNext()){
      Object obj = itera.next();
      sessionMap.put(obj, sessionContext.get(obj.toString()));
    }
    
    return sessionMap;
  }
  
  public static String getWinStatus(HttpServletRequest request){
		String res = "单位代码:" + getAttribute(request, "svCoCode") + "  ";
		res += "单位名称:" + getAttribute(request, "svCoName") + "  ";
		res += "内部机构代码:" + getAttribute(request, "svOrgCode") + "  ";
		res += "内部机构名称:" + getAttribute(request, "svOrgName") + "  ";
		res += "帐套代码:" + getAttribute(request, "svAccountId") + "  ";
		res += "帐套名称:" + getAttribute(request, "svAccountName") + "  ";
		res += "业务日期:" + getAttribute(request, "svTransDate") + "  ";
		res += "会计年度:" + getAttribute(request, "svFiscalYear") + "  ";
		res += "会计期间:" + getAttribute(request, "svFiscalPeriod") + "  ";
		res += "表套代码:" + getAttribute(request, "svRpType") + "  ";
		res += "表套名称:" + getAttribute(request, "svRpTypeName") + "  ";
		res += "职位代码:" + getAttribute(request, "svPoCode") + "  ";
		res += "职位名称:" + getAttribute(request, "svPoName") + "  ";
		res += "登录帐号名:" + getAttribute(request, "svUserName") + "  ";
		res += "系统日期:" + getAttribute(request, "svSysDate") + "  ";
		return res;
  }
}
