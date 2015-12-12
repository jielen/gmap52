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
   * 无参数构造函数
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
   * 有参数构造函数
   * 
   * @param m
   *          String 异常信息
   */
  public PrintingException(String m) {
    super(m);
    setMessageInfor(m);
    log.info(m);
    System.out.println(m);
  }

  /**
   * 设置异常信息
   * @param messageInfor   String
   * @uml.property   name="messageInfor"
   */
  private void setMessageInfor(String messageInfor) {
    this.messageInfor = messageInfor;
  }

  /**
   * 返回异常信息
   * @return   String
   * @uml.property   name="messageInfor"
   */
  public String getMessageInfor() {
    return messageInfor;
  }

  /**
   * 返回错误信息
   * 
   * @return String
   */
  public String toString() {
    return messageInfor;
  }

}
