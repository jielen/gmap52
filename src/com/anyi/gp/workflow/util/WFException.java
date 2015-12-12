// $Id: WFException.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.util;

import com.anyi.gp.BusinessException;

/**
 * <p>
 * 工作流异常
 * </p>
 * <p>
 * 工作流异常类表示工作流程序执行过程中执行失败。
 * 
 * @author xiary
 * @version 1.0
 */
public class WFException extends BusinessException {

  public WFException(String messagePattern) {
    super(messagePattern);
  }
}
