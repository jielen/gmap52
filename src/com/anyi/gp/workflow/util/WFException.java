// $Id: WFException.java,v 1.1 2008/03/24 03:55:44 liubo Exp $

package com.anyi.gp.workflow.util;

import com.anyi.gp.BusinessException;

/**
 * <p>
 * �������쳣
 * </p>
 * <p>
 * �������쳣���ʾ����������ִ�й�����ִ��ʧ�ܡ�
 * 
 * @author xiary
 * @version 1.0
 */
public class WFException extends BusinessException {

  public WFException(String messagePattern) {
    super(messagePattern);
  }
}
