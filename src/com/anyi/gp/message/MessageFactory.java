/** $Id: MessageFactory.java,v 1.1 2008/02/22 09:12:33 liuxiaoyong Exp $ */
package com.anyi.gp.message;

/**
 * <p>
 * Title: 获取消息相应接口
 * </p>
 * <p>
 * Description: 获取消息相应接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: 用友政务
 * </p>
 * 
 * @author majian
 * @version 1.0
 */

public class MessageFactory {
  /**
   * 获取消息实现接口
   * 
   * @return MessageImp 消息实现接口
   */
  public static IMessage getMessageImp() {
    return new MessageImp();
  }

  /**
   * 获取部门用户管理实现接口
   * 
   * @return DeptUsrMngImp 用户管理实现接口
   */
  public static IDeptUsrMng getDeptUsrMngImp() {
    return new DeptUsrMngImp();
  }
}
