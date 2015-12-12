package com.kingdrive.framework.controller.web.action;

import com.kingdrive.framework.controller.web.SessionManager;
import com.kingdrive.framework.exception.GeneralException;

public abstract class WebAction {

  protected SessionManager tps;

  public WebAction() {
  }

  public abstract String perform(String event) throws GeneralException;

  public void setSessionManager(SessionManager tps) {
    this.tps = tps;
  }
}
