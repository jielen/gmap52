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
	 * ȡsession�е�key��Ӧ��ֵ
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getAttribute(HttpServletRequest request, String key){
		return getAttribute(request.getSession(), key);
	}

	/**
	 * ȡsession�е�key��Ӧ��ֵ
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
		String res = "��λ����:" + getAttribute(request, "svCoCode") + "  ";
		res += "��λ����:" + getAttribute(request, "svCoName") + "  ";
		res += "�ڲ���������:" + getAttribute(request, "svOrgCode") + "  ";
		res += "�ڲ���������:" + getAttribute(request, "svOrgName") + "  ";
		res += "���״���:" + getAttribute(request, "svAccountId") + "  ";
		res += "��������:" + getAttribute(request, "svAccountName") + "  ";
		res += "ҵ������:" + getAttribute(request, "svTransDate") + "  ";
		res += "������:" + getAttribute(request, "svFiscalYear") + "  ";
		res += "����ڼ�:" + getAttribute(request, "svFiscalPeriod") + "  ";
		res += "���״���:" + getAttribute(request, "svRpType") + "  ";
		res += "��������:" + getAttribute(request, "svRpTypeName") + "  ";
		res += "ְλ����:" + getAttribute(request, "svPoCode") + "  ";
		res += "ְλ����:" + getAttribute(request, "svPoName") + "  ";
		res += "��¼�ʺ���:" + getAttribute(request, "svUserName") + "  ";
		res += "ϵͳ����:" + getAttribute(request, "svSysDate") + "  ";
		return res;
  }
}
