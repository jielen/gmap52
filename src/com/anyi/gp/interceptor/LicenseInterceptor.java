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
 * ����Ƿ����Ʒ
 * ��û�й����������ʾ���������������
 * 
 */
public class LicenseInterceptor implements Interceptor{

  private static final long serialVersionUID = -1105850429020615976L;

  public void destroy() {    
  }

  public void init() {   
  }

  /**
   * ����Ƿ���˲�Ʒ
   * 1����Դ�ҳ���action�����м��
   * 2����ʾ�治���м��
   * 3����û�й��������ʾ�����Ҳ������action
   */
  public String intercept(ActionInvocation invocation) throws Exception {

    LicenseManager licenseManager = (LicenseManager)ApplusContext.getBean("licenseManager");
    HttpServletRequest request = ServletActionContext.getRequest();
    
    // ��飬�Ƿ�����Ч��dog
    if (licenseManager.checkCompanyAccount()) {
      boolean canPass = licenseManager.canPassByca();
      if (!canPass) {
        request.setAttribute("fail", "��λ���������������������!");
        return Action.INPUT;
      }
    }
    
	  return invocation.invoke();
//    String actionName = invocation.getProxy().getActionName();
//    if(!actionName.startsWith("getpage_")){
//      return invocation.invoke();//����ҳ���action�����
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
//        return invocation.invoke();//����˲�Ʒ
//      }
//    }
//    
//    DataTools.printProductNotAllowedError(ServletActionContext.getRequest()
//      , ServletActionContext.getResponse());
//    
//    return "none";
  }

}
