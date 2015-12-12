/** $Id: MessageFactory.java,v 1.1 2008/02/22 09:12:33 liuxiaoyong Exp $ */
package com.anyi.gp.message;

/**
 * <p>
 * Title: ��ȡ��Ϣ��Ӧ�ӿ�
 * </p>
 * <p>
 * Description: ��ȡ��Ϣ��Ӧ�ӿ�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: ��������
 * </p>
 * 
 * @author majian
 * @version 1.0
 */

public class MessageFactory {
  /**
   * ��ȡ��Ϣʵ�ֽӿ�
   * 
   * @return MessageImp ��Ϣʵ�ֽӿ�
   */
  public static IMessage getMessageImp() {
    return new MessageImp();
  }

  /**
   * ��ȡ�����û�����ʵ�ֽӿ�
   * 
   * @return DeptUsrMngImp �û�����ʵ�ֽӿ�
   */
  public static IDeptUsrMng getDeptUsrMngImp() {
    return new DeptUsrMngImp();
  }
}
