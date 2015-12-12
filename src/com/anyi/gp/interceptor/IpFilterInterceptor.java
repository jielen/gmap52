package com.anyi.gp.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.pub.GeneralFunc;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class IpFilterInterceptor implements Interceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5281307427913658085L;

	public void destroy() {
		// TCJLODO Auto-generated method stub

	}

	public void init() {
		// TCJLODO Auto-generated method stub

	}

	public String intercept(ActionInvocation invocation) throws Exception {
		if(checkTokenConsistent(invocation))
        {
            //System.out.println("通过token验证！");
            return invocation.invoke();
        }
		
        HttpServletRequest request = ServletActionContext.getRequest();
        List ipList = new ArrayList();
        List macList = new ArrayList();
        if(null == request.getParameter("iparray") || "".equals(request.getParameter("iparray"))){
        	ipList.add(request.getRemoteAddr());
        }else{
	        String ipArr[] = request.getParameter("iparray").trim().split(",");
	        for(int i = 0; i < ipArr.length; i++)
	            if(i % 2 == 0){
	                macList.add(ipArr[i]);
	                //System.out.println("mac" + i + " : " + ipArr[i]);
	            }else{
	                ipList.add(ipArr[i]);
	                //System.out.println("ip" + i + " : " + ipArr[i]);
	            }
        }
        String userId = request.getParameter("username");
        if(!GeneralFunc.checkIPValidity(ipList, macList, userId))
        {
            request.setAttribute("fail", "您无权登录！");
            return "input";
        } else{
            return invocation.invoke();
        }
	}
	
	private boolean checkTokenConsistent(ActionInvocation invocation)
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        String sToken = (String)invocation.getInvocationContext().getSession().get("current.user.token");
        String rToken = request.getParameter("token");
        return rToken != null && sToken != null && rToken.equals(sToken);
    }

}
