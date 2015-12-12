package com.anyi.gp.core.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Pub;
import com.anyi.gp.pub.ServiceFacade;
import com.opensymphony.webwork.interceptor.ServletRequestAware;

public class SaveAction extends AjaxAction implements ServletRequestAware{

  private static final long serialVersionUID = -2900622828728436084L;

  private static final Logger log = Logger.getLogger(SaveAction.class);
  
  private String data;
  
  private String isdigest;

  private HttpServletRequest request;
  
  private ServiceFacade serviceFacade;
  
  public void setData(String data) {
    this.data = data;
  }

  public void setIsdigest(String isdigest) {
    this.isdigest = isdigest;
  }

  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }
  
  public void setServiceFacade(ServiceFacade serviceFacade) {
    this.serviceFacade = serviceFacade;
  }

  public String doExecute() throws Exception {
    try{
      resultstring = serviceFacade.save(data, isdigest, request);
    }
    catch(BusinessException be){
      log.debug(be);
      resultstring = Pub.makeRetInfo2(false, "", be.getMessage(), "");
    }
    return SUCCESS;
  } 
}
