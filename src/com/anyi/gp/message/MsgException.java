/** $Id: MsgException.java,v 1.1 2008/02/22 09:12:33 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;

/**
 * <p>
 * Title: ��Ϣ���ѵ��쳣��
 * </p>
 * <p>
 * Description: ����Ϣ���ѷ����쳣ʱ�׳�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: ��������
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
