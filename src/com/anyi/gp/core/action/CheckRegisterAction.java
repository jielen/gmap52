package com.anyi.gp.core.action;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.license.LicenseManager;

public class CheckRegisterAction extends AjaxAction{

  private static final long serialVersionUID = 2345379047031046904L;

  private LicenseManager manager;
  
  public void setManager(LicenseManager manager) {
    this.manager = manager;
  }

  public String doExecute() throws Exception {
    try {
        resultstring = this.wrapResultStr("true", manager.getLoginMessage());
    } catch (Exception ex) {
    	resultstring = this.wrapResultStr("false", ex.getMessage());
    }
    return SUCCESS;
  }

}
