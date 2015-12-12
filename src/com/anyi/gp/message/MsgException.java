/** $Id: MsgException.java,v 1.1 2008/02/22 09:12:33 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;

/**
 * <p>
 * Title: 消息提醒的异常类
 * </p>
 * <p>
 * Description: 在消息提醒发生异常时抛出
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: 用友政务
 * </p>
 * 
 * @author:majian
 * @version: 1.0
 * @time: 2004/04/30
 */

public class MsgException extends BusinessException {
  private static Logger log = Logger.getLogger(MsgException.class);

  public MsgException(String messagePattern) {
    super(messagePattern);
    log.info(messagePattern);
  }

  public MsgException(String messagePattern, Object[] arguments) {
    /////super(messagePattern, arguments);
    log.info(messagePattern);
  }
}
