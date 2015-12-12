/** $Id: IMessage.java,v 1.1 2008/02/22 09:12:32 liuxiaoyong Exp $ */
package com.anyi.gp.message;

/**
 * <p>
 * Title: 消息提醒接口
 * </p>
 * <p>
 * Description: 消息提醒接口
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
 * @time: 2004/03/30
 */

public interface IMessage {
  /**
   * 发送消息(用户到用户)
   * 
   * @param String
   *          sender 发送者的用户名
   * @param String
   *          receivers 接受者的用户列表，用','分隔，例如sa,zhangsan,lisi
   * @param String
   *          msg 消息内容
   * @throws MsgException
   */

  public void sendInfomation(String sender, String receivers, String msg)
      throws MsgException;

  /**
   * 通过RTX用户发送消息(RTX用户到RTX用户)
   * 
   * @param String
   *          sender 发送者的RTX用户uin
   * @param String
   *          receivers 接受者的RTX用户uin列表，用','分隔，例如1001,900,800
   * @param String
   *          msg 消息内容
   * @throws MsgException
   */

  public void sendInfomationByRtxUser(String sender, String receivers,
      String msg) throws MsgException;

  /**
   * 即时消息提醒(系统到用户)
   * 
   * @param String
   *          receivers 接受者的用户列表，用','分隔，例如sa,zhangsan,lisi
   * @param String
   *          msg 消息内容
   * @param String
   *          type 消息类别 1，0
   * @param String
   *          title 消息标题
   * @throws MsgException
   */

  public void notify(String receivers, String msg, String type, String title)
      throws MsgException;

  /**
   * 即时消息提醒给RTX用户(系统到RTX用户)
   * 
   * @param String
   *          receivers 接受者的RTX用户uin列表，用','分隔，例如1001,900,800
   * @param String
   *          msg 消息内容
   * @param String
   *          type 消息类别 1，0
   * @param String
   *          title 消息标题
   * @throws MsgException
   */

  public void notifyToRtxUser(String receivers, String msg, String type,
      String title) throws MsgException;

  /**
   * 发送短信(用户到手机)
   * 
   * @param String
   *          sender 发送者的用户名
   * @param String
   *          receivers 接受者的手机列表，用','分隔，例如13310681234,13819002333
   * @param String
   *          msg 消息内容
   * @param String
   *          cut 是否截断 '1'-是 '0'-否
   * @throws MsgException
   */

  public void sendShortMessage(String sender, String receivers, String msg,
      String cut) throws MsgException;

  /**
   * 通过RTX用户发送短信(RTX用户到手机)
   * 
   * @param String
   *          sender 发送者的RTX用户uin
   * @param String
   *          receivers 接受者的手机列表，用','分隔，例如13310681234,13819002333
   * @param String
   *          msg 消息内容
   * @param String
   *          cut 是否截断 '1'-是 '0'-否
   * @throws MsgException
   */

  public void sendShortMessageByRtxUser(String sender, String receivers,
      String msg, String cut) throws MsgException;

  /**
   * 发送短信(用户到用户)
   * 
   * @param String
   *          sender 发送者的用户名
   * @param String
   *          receivers 接受者的手机列表，用,分隔，例如sa,zhangsan
   * @param String
   *          msg 消息内容
   * @param String
   *          cut 是否截断 '1'-是 '0'-否
   * @throws MsgException
   */

  public void sendShortMessageToUser(String sender, String receivers,
      String msg, String cut) throws MsgException;

  /**
   * 判断腾讯通短信是否可用
   * 
   * @return boolean true-可用 false-不可用
   */

  public boolean isMessageCanBeUsed();

  /**
   * 发送邮件
   * 
   * @param String
   *          from 发送人
   * @param String
   *          to
   *          接收人列表，用','分隔，例如sa@ufgov.com.cn,zhangsan@ufgov.com.cn,lisi@ufgov.com.cn
   * @param String
   *          cc 抄送人
   * @param String
   *          bcc 暗送人
   * @param String
   *          subject 主题
   * @param String
   *          body 内容
   * @param String
   *          mimeType 默认text/html
   * @throws Exception
   */

  public void sendEmail(String from, String to, String cc, String bcc,
      String subject, String body, String mimeType) throws Exception;

  /**
   * 发送邮件给用户
   * 
   * @param String
   *          uFrom 发送用户,如果null则用config.xml里设定邮件管理员邮件发送邮件
   * @param String
   *          uTo 接收人用户列表，用','分隔，例如sa,zhangsan,lisi
   * @param String
   *          uCc 抄送人用户列表
   * @param String
   *          uBcc 暗送人用户列表
   * @param String
   *          subject 主题
   * @param String
   *          body 内容
   * @param String
   *          mimeType 默认text/html
   * @throws Exception
   */

  public void sendEmailToUser(String uFrom, String uTo, String uCc,
      String uBcc, String subject, String body, String mimeType)
      throws Exception;

  /**
   * 判断邮件是否可用
   * 
   * @return boolean true-可用 false-不可用
   */

  public boolean isEmailCanBeUsed();

}
