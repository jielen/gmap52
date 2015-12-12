/*
 * Created on 2005-3-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.anyi.gp.workflow;

/**
 * @author work
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CZWFFactory extends WFFactory {

  /*
   * (non-Javadoc)
   * 
   * @see com.anyi.erp.workflow.WFFactory#getService()
   */
  public WFService getService() {
    // TCJLODO Auto-generated method stub
    return new CZWFService();
  }

}
