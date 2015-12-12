/* $Id: PrintingException.java,v 1.2 2009/07/10 08:36:56 liuxiaoyong Exp $ */
package com.anyi.gp.print.exception;

import org.apache.log4j.Logger;

/**
 * @author   zhangyw
 */
public class PrintingException extends Exception {

  private static final long serialVersionUID = -4347156797337233704L;

  private static Logger log = Logger.getLogger(PrintingException.class);

  /**
   * �޲������캯��
   */
  private String messageInfor;

  public PrintingException() {
    super();
  }

  /**
   * 
   * @param exception
   */
  public PrintingException(Exception exception) {
    super(exception);
    setMessageInfor(exception.getMessage());
    log.info(exception.getMessage());
    System.out.println(exception.getMessage());
  }

  /**
   * �в������캯��
   * 
   * @param m
   *          String �쳣��Ϣ
   */
  public PrintingException(String m) {
    super(m);
    setMessageInfor(m);
    log.info(m);
    System.out.println(m);
  }

  /**
   * �����쳣��Ϣ
   * @param messageInfor   String
   * @uml.property   name="messageInfor"
   */
  private void setMessageInfor(String messageInfor) {
    this.messageInfor = messageInfor;
  }

  /**
   * �����쳣��Ϣ
   * @return   String
   * @uml.property   name="messageInfor"
   */
  public String getMessageInfor() {
    return messageInfor;
  }

  /**
   * ���ش�����Ϣ
   * 
   * @return String
   */
  public String toString() {
    return messageInfor;
  }

}
