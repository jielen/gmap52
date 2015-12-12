package com.anyi.gp.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.license.LicenseManager;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * 
 * 检查是否购买产品
 * 若没有购买则给出提示，并不允许其操作
 * 
 */
public class LicenseInterceptor implements Interceptor{

  private static final long serialVersionUID = -1105850429020615976L;

  public void destroy() {    
  }

  public void init() {   
  }

  /**
   * 检查是否购买此产品
   * 1、针对打开页面的action来进行检查
   * 2、演示版不进行检查
   * 3、若没有购买给出提示，并且不处理此action
   */
  public String intercept(ActionInvocation invocation) throws Exception {

    LicenseManager licenseManager = (LicenseManager)ApplusContext.getBean("licenseManager");
    HttpServletRequest request = ServletActionContext.getRequest();
    
    // 检查，是否是有效的dog
    if (licenseManager.checkCompanyAccount()) {
      boolean canPass = licenseManager.canPassByca();
      if (!canPass) {
        request.setAttribute("fail", "单位数或帐套数超过最大限制!");
        return Action.INPUT;
      }
    }
    
	  return invocation.invoke();
//    String actionName = invocation.getProxy().getActionName();
//    if(!actionName.startsWith("getpage_")){
//      return invocation.invoke();//不是页面打开action不检查
//    }
//    String product = null;
//    int dotPos = actionName.indexOf("_");
//    String compoName = actionName.substring(dotPos + 1);
//    dotPos = compoName.indexOf("_");
//    if(dotPos > 0){
//      product = compoName.substring(0, dotPos);
//    }
//    if(product != null && product.length() > 0){
//      if(product.equalsIgnoreCase("AS") || product.equalsIgnoreCase("MA")
//        || product.equalsIgnoreCase("WF")){
//        return invocation.invoke();
//      }
//      
//      LicenseManager manager = (LicenseManager)ApplusContext.getBean("licenseManager");
//      if (!manager.checkProduct()) {
//    	  return invocation.invoke();
//      }
//      List allowedProducts = manager.getAllowedProducts();
//      if(allowedProducts != null && allowedProducts.contains(product)){
//        return invocation.invoke();//购买此产品
//      }
//    }
//    
//    DataTools.printProductNotAllowedError(ServletActionContext.getRequest()
//      , ServletActionContext.getResponse());
//    
//    return "none";
  }

}
