package com.anyi.gp.core.action;

import org.apache.log4j.Logger;

import com.anyi.gp.access.CommonService;

public class SaveOptionsAction extends AjaxAction {

  /**
   * 保存系统选项
   * 
   * @author guohui
   */

  ////private static final Logger logger = Logger.getLogger(SaveOptionsAction.class);

  private static final long serialVersionUID = 1L;
  
  private static final Logger log = Logger.getLogger(SaveOptionsAction.class);

  private String optionXml;

  private CommonService service;

  public String getOptionXml() {
    return optionXml;
  }

  public void setOptionXml(String optionXml) {
    this.optionXml = optionXml;
  }

  public CommonService getService() {
    return service;
  }

  public void setService(CommonService service) {
    this.service = service;
  }

  public String doExecute() throws Exception {
    // TCJLODO Auto-generated method stub
    String msg = "";
    try {
      msg = service.saveOptions(optionXml);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e);
    }

    //return Pub.makeRetString(ok, msg);
    this.resultstring = this.wrapResultStr("true", msg);

    return SUCCESS;

  }
}
