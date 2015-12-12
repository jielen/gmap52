/* $Id: WFFactory.java,v 1.1 2008/02/22 08:27:16 liubo Exp $ */

package com.anyi.gp.workflow;

public abstract class WFFactory {

  public static int getWhichFactory() {
    int result = -1;
    result = OAWF;
    service = result;
    return result;
  }

  public static WFFactory getInstance() {
    int whichFactory = -1;
    if (service != -1) {
      whichFactory = service;
    } else
      whichFactory = getWhichFactory();
    switch (whichFactory) {
    case OAWF:
    // return new OAWFFactory();
    case CZWF:
      return new CZWFFactory();
    default:
      return null;
    }
  }

  public static void setFactory(int whichFactory) {
    service = whichFactory;
  }

  public abstract WFService getService();

  private static int service = 1;

  public static final int OAWF = 0;

  public static final int CZWF = 1;
}
