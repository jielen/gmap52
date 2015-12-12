package com.anyi.gp.core.action;

import org.apache.log4j.Logger;

import com.anyi.gp.license.LicenseManager;

public class UploadLicenseAction extends AjaxAction {

  private static final long serialVersionUID = -7451064928530988307L;

  private static final Logger log = Logger.getLogger(UploadLicenseAction.class);

  private String text;

  private LicenseManager manager;
  
  public LicenseManager getManager() {
    return manager;
  }

  public void setManager(LicenseManager manager) {
    this.manager = manager;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String doExecute() throws Exception {
    String dataStr = "上传成功！";
    String flag = "false";
    try {
      manager.uploadLicense(text);
      flag = "true";
    } catch (Exception ex) {
      log.error(ex);
      dataStr = ex.getMessage();
    }
//    if("true".equals(flag)){
//      syncLicense();
//    }
    
    this.resultstring = this.wrapResultStr(flag, dataStr);
    
    return SUCCESS;
  }

  //同步其他应用中的license
//  private void syncLicense(){
//    LicenseManager licenseManager = (LicenseManager) ApplusContext.getBean("licenseManager");
//    if(licenseManager.license == null)
//      return;
//    
//    ServletContext servletContext  = ServletActionContext.getServletContext();
//    List appNames = (List)servletContext.getAttribute(SessionUtils.APP_NAME_LIST_KEY);
//    if (appNames == null) {
//      appNames = GeneralFunc.getAppNames();
//    }
//    
//    if(appNames !=  null){
//      HttpServletRequest request = ServletActionContext.getRequest();
//      HttpServletResponse response = ServletActionContext.getResponse();  
//      request.setAttribute("licenseSN", licenseManager.license.getSn());
//      
//      for(int i = 0; i < appNames.size(); i++){//遍历所有web应用
//        String appName = (String)appNames.get(i);
//        ServletContext sc = servletContext.getContext("/" + appName);
//        if(sc != null){
//          if(sc.getResourceAsStream("/syncLicense.jsp") != null){
//            RequestDispatcher proxyServlet = sc.getRequestDispatcher("/syncLicense.jsp");
//            try {
//              proxyServlet.include(request, response);
//            } catch (ServletException e) {
//              log.debug(e);
//            } catch (IOException e) {
//              log.debug(e);
//            }
//          }
//        }
//      }
//    }
//  }
}
