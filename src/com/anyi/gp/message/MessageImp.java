/** $Id: MessageImp.java,v 1.3 2008/03/15 07:39:05 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import com.anyi.gp.bean.OptionBean;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.pub.GeneralFunc;
import com.anyi.gp.util.StringTools;

/**
 * <p>
 * Title: 消息提醒相关接口实现
 * </p>
 * <p>
 * Description:
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

public class MessageImp implements IMessage {
  public boolean isMessageCanBeUsed() {
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      RtxsvrapiObj.Init();
      RtxsvrapiObj.UnInit();
      return true;
    } catch (Error e) {
      return false;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 发送消息(用户到用户)
   * 
   * @param String
   *          sender 发送者的用户名
   * @param String
   *          receivers 接受者的用户列表，用,分隔，例如sa,zhangsan,lisi
   * @param String
   *          msg 消息内容
   * @throws MsgException
   */

  public void sendInfomation(String sender, String receivers, String msg)
      throws MsgException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    RTXSvrApi rtxsvrapiObj = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      // 初始化RTXSvrApi对象
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // 查找发送者所对应的相关腾讯通RTX号
        stmt = conn.createStatement();
        String senderUin = getUserUin(stmt, sender);
        // 查找接收者所对应的相关腾讯通RTX号
        String receiverUinList = getReceiverUinList(stmt, receivers);
        // 发送
        rtxsvrapiObj.sendIM(senderUin, receiverUinList, msg);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    } finally {
      DBHelper.closeConnection(conn, stmt, rs);
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }
  }

  /**
   * 发送短信(用户到手机)
   * 
   * @param String
   *          sender 发送者的用户名
   * @param String
   *          receivers 接受者的手机列表，用,分隔，例如13310681234,13819002333
   * @param String
   *          msg 消息内容
   * @param String
   *          cut 是否截断 '1'-是 '0'-否
   * @throws MsgException
   */

  public void sendShortMessage(String sender, String receivers, String msg,
      String cut) throws MsgException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    RTXSvrApi rtxsvrapiObj = null;
    try {
      // int needCut=Integer.parseInt(cut);
      // 初始化RTXSvrApi对象
      conn = DAOFactory.getInstance().getConnection();
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // 查找发送者所对应的相关腾讯通RTX号
        stmt = conn.createStatement();
        String senderUin = getUserUin(stmt, sender);

        // 发送
        rtxsvrapiObj.sendSm2(senderUin, receivers, msg, cut);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    } finally {
    	DBHelper.closeConnection(conn, stmt, rs);
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }
  }

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
      String msg, String cut) throws MsgException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    RTXSvrApi rtxsvrapiObj = null;
    try {
      if (receivers == null || receivers.length() <= 0) {
        rtxsvrapiObj.UnInit();
        throw new Exception("没有接收者");
      }
      conn = DAOFactory.getInstance().getConnection();
      // 初始化RTXSvrApi对象
      // int needCut=Integer.parseInt(cut);
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // 查找发送者所对应的相关腾讯通RTX号
        stmt = conn.createStatement();
        String senderUin = getUserUin(stmt, sender);
        String receiverMobileList = getReceiverMobileList(stmt, rtxsvrapiObj,
            receivers);
        // 发送
        rtxsvrapiObj.sendSm2(senderUin, receiverMobileList, msg, cut);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    } finally {
      DBHelper.closeConnection(conn, stmt, rs);
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }

  }

  /**
   * 即时消息提醒(系统到用户)
   * 
   * @param String
   *          receivers 接受者的用户列表，用,分隔，例如sa,zhangsan,lisi
   * @param String
   *          msg 消息内容
   * @param String
   *          type 消息类别 '1'，'0'
   * @param String
   *          title 消息标题
   * @throws MsgException
   */

  public void notify(String receivers, String msg, String type, String title)
      throws MsgException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    RTXSvrApi rtxsvrapiObj = null;
    try {
      if (receivers == null || receivers.length() <= 0) {
        rtxsvrapiObj.UnInit();
        throw new Exception("没有接收者");
      }
      conn = DAOFactory.getInstance().getConnection();
      // 初始化RTXSvrApi对象
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // 查找接受者所对应的相关腾讯通RTX号
        stmt = conn.createStatement();
        String receiverUinList = getReceiverUinList(stmt, receivers);
        // 发送
        rtxsvrapiObj.notify(receiverUinList, msg, type, title);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new MsgException(ex.getMessage());
    } finally {
      DBHelper.closeConnection(conn, stmt, rs);
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }

  }

  /**
   * 通过RTX用户发送消息(RTX用户到RTX用户)
   * 
   * @param String
   *          sender 发送者的RTX用户uin
   * @param String
   *          receivers 接受者的RTX用户uin列表，用,分隔，例如1001,900,800
   * @param String
   *          msg 消息内容
   * @throws MsgException
   */

  public void sendInfomationByRtxUser(String sender, String receivers,
      String msg) throws MsgException {
    RTXSvrApi rtxsvrapiObj = null;
    try {
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        rtxsvrapiObj.sendIM(sender, receivers, msg);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    } finally {
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }

  }

  /**
   * 通过RTX用户发送短信(RTX用户到手机)
   * 
   * @param String
   *          sender 发送者的RTX用户uin
   * @param String
   *          receivers 接受者的手机列表，用,分隔，例如13310681234,13819002333
   * @param String
   *          msg 消息内容
   * @param String
   *          cut 是否截断 '1'-是 '0'-否
   * @throws MsgException
   */

  public void sendShortMessageByRtxUser(String sender, String receivers,
      String msg, String cut) throws MsgException {
    RTXSvrApi rtxsvrapiObj = null;
    try {
      // int needCut=Integer.parseInt(cut);
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        rtxsvrapiObj.sendSm2(sender, receivers, msg, cut);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    } finally {
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }

  }

  /**
   * 即时消息提醒给RTX用户(系统到RTX用户)
   * 
   * @param String
   *          receivers 接受者的RTX用户uin列表，用,分隔，例如1001,900,800
   * @param String
   *          msg 消息内容
   * @param String
   *          type 消息类别 '1'，'0'
   * @param String
   *          title 消息标题
   * @throws MsgException
   */

  public void notifyToRtxUser(String receivers, String msg, String type,
      String title) throws MsgException {
    RTXSvrApi rtxsvrapiObj = null;
    try {
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        rtxsvrapiObj.notify(receivers, msg, type, title);
        rtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    } finally {
      if (rtxsvrapiObj != null) {
        rtxsvrapiObj.UnInit();
      }
    }
  }

  /**
   * 发送邮件
   * 
   * @param String
   *          from 发送人
   * @param String
   *          to
   *          接收人列表，用','分隔，例如sa@ufgov.com.cn,zhangsan@ufgov.com.cn,lisi@ufgov.com.cn
   * @param String
   *          cc 抄送人列表
   * @param String
   *          bcc 暗送人列表
   * @param String
   *          subject 主题
   * @param String
   *          body 内容
   * @param String
   *          mimeType 默认text/html
   * @throws Exception
   */

  public void sendEmail(String from, String to, String cc, String bcc,
      String subject, String body, String mimeType) throws Exception {
    String smtpHost = null;
    String needAuthStr = null;
    String confAdminEmail = null;
    String user = null;
    String passWd = null;
    
    List options = GeneralFunc.getOptions("AS_MAIL_OPT");
    
    for (int i = 0; i < options.size(); i++) {
      OptionBean op = (OptionBean) options.get(i);
      if (op.getOptId().equalsIgnoreCase("OPT_MAIL_SMTPHOST")) {
        smtpHost = op.getOptVal();
      }
      if (op.getOptId().equalsIgnoreCase("OPT_MAIL_NEEDAUTH")) {
        needAuthStr = op.getOptVal();
      }
      if (op.getOptId().equalsIgnoreCase("OPT_MAIL_ADMINEMAIL")) {
        confAdminEmail = op.getOptVal();
      }
      if (op.getOptId().equalsIgnoreCase("OPT_MAIL_USER")) {
        user = op.getOptVal();
      }
      if (op.getOptId().equalsIgnoreCase("OPT_MAIL_PASSWORD")) {
        passWd = op.getOptVal();
      }
    }

    // ApplusContext g = ApplusContext.getInstance();
    // String smtpHost = (String)g.get("smtpHost");
    if (smtpHost == null || smtpHost.length() == 0) {
      throw new MsgException("smtp主机不能为空");
    }
    boolean needAuth = false;
    // String needAuthStr = (String)g.get("needAuthStr");
    if (needAuthStr.equalsIgnoreCase("Y")) {
      needAuth = true;
    }
    if (from == null || from.length() == 0) {
      from = confAdminEmail;
    }
    //
    // String user = (String)g.get("user");
    // String passWd = (String)g.get("passWord");

    // MailSender m = new MailSender(getMailSession(), from, to, cc, bcc,
    // subject, body);
    MailSender m = new MailSender(smtpHost, needAuth, from, to, cc, bcc,
        subject, body, user, passWd);
    if (mimeType != null && mimeType.length() > 0) {
      m.setMimeType(mimeType);
    }
    m.send();
  }

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
      throws Exception {
    String from = null;
    String to = null;
    String cc = null;
    String bcc = null;
    Connection conn = null;
    Statement stmt = null;
    try {
      if (uTo == null || uTo.length() == 0) {
        throw new MsgException("没有收件人");
      }
      to = uTo;
      String smtpHost = null;
      String needAuthStr = null;
      String confAdminEmail = null;
      String user = null;
      String passWd = null;

      List options = GeneralFunc.getOptions("AS_MAIL_OPT");
      for (int i = 0; i < options.size(); i++) {
        OptionBean op = (OptionBean) options.get(i);
        if (op.getOptId().equalsIgnoreCase("OPT_MAIL_SMTPHOST")) {
          smtpHost = op.getOptVal();
        }
        if (op.getOptId().equalsIgnoreCase("OPT_MAIL_NEEDAUTH")) {
          needAuthStr = op.getOptVal();
        }
        if (op.getOptId().equalsIgnoreCase("OPT_MAIL_ADMINEMAIL")) {
          confAdminEmail = op.getOptVal();
        }
        if (op.getOptId().equalsIgnoreCase("OPT_MAIL_USER")) {
          user = op.getOptVal();
        }
        if (op.getOptId().equalsIgnoreCase("OPT_MAIL_PASSWORD")) {
          passWd = op.getOptVal();
        }
      }

      // ApplusContext g = ApplusContext.getInstance();
      // String smtpHost = (String)g.get("smtpHost");
      if (smtpHost == null || smtpHost.length() == 0) {
        throw new MsgException("smtp主机不能为空");
      }
      boolean needAuth = false;
      // String needAuthStr = (String)g.get("needAuth");
      if (needAuthStr.equalsIgnoreCase("Y")) {
        needAuth = true;
      }
      conn = DAOFactory.getInstance().getConnection();
      stmt = conn.createStatement();
      if (uFrom == null || uFrom.length() == 0) {
        from = confAdminEmail;
      } else {
        from = getUserEmail(stmt, uFrom);
        if (from == null || from.length() == 0) {
          from = confAdminEmail;
        }
      }
      to = getReceiverEmailList(stmt, to);
      if (uCc != null && uCc.length() > 0) {
        cc = getReceiverEmailList(stmt, uCc);
      }
      if (uBcc != null && uBcc.length() > 0) {
        bcc = getReceiverEmailList(stmt, uBcc);
      }
      // String user = (String)g.get("user");
      // String passWd = (String)g.get("passWord");
      // MailSender m = new MailSender(getMailSession(), from, to, cc, bcc,
      // subject,
      // body);
      MailSender m = new MailSender(smtpHost, needAuth, from, to, cc, bcc,
          subject, body, user, passWd);
      if (mimeType != null && mimeType.length() > 0) {
        m.setMimeType(mimeType);
      }
      m.send();
    } finally {
      DBHelper.closeConnection(conn, stmt, null);
    }
  }

  public boolean isEmailCanBeUsed() {
    boolean emailCanBeUsed = false;
    try {
      // Session mailSession = getMailSession();
      // if(mailSession != null){
      // emailCanBeUsed = true;
      // }
      String smtpHost = null;

      List options = GeneralFunc.getOptions("AS_MAIL_OPT");
      for (int i = 0; i < options.size(); i++) {
        OptionBean op = (OptionBean) options.get(i);
        if (op.getOptId().equalsIgnoreCase("OPT_MAIL_SMTPHOST")) {
          smtpHost = op.getOptVal();
          emailCanBeUsed = true;
        }
      }
      if (smtpHost == null || smtpHost.length() == 0) {
        emailCanBeUsed = false;
      }
    } catch (Exception ex) {
      emailCanBeUsed = false;
    }
    return emailCanBeUsed;
  }

  /**
   * 获取接收者的Uin列表
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          receivers 接收人用户列表，用','分隔，例如sa,zhangsan,lisi
   * @return String 用,分隔如1001,1002,1003
   * @throws Exception
   */

  private String getReceiverUinList(Statement stmt, String receivers)
      throws SQLException, Exception {
    String receiverUinList = "";
    ResultSet rs = null;
    try {
      String receiverList = converToSqlReceiverList(receivers);
      String sqlStr = "select rtxUin from AS_User where user_Id in ("
          + receiverList + ")";
      rs = stmt.executeQuery(sqlStr);
      String userUin;
      while (rs.next()) {
        userUin = rs.getString("rtxUin");
        if (userUin != null) {
          receiverUinList = receiverUinList + userUin + ",";
        }
      }
      rs.close();
      if (receiverUinList.length() == 0) {
        throw new Exception(receivers + "用户在消息系统上没有注册");
      }
      receiverUinList = receiverUinList.substring(0,
          receiverUinList.length() - 1);
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return receiverUinList;
  }

  /**
   * 获取用户的Uin
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          sender 发送人的用户名
   * @return String
   * @throws Exception
   */

  private String getUserUin(Statement stmt, String sender) throws SQLException,
      Exception {
    String senderUin = "";
    String sqlStr = "select rtxUin from AS_User where user_Id ='" + sender
        + "'";
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery(sqlStr);
      if (rs.next()) {
        senderUin = rs.getString("rtxUin");
      } else {
        rs.close();
        throw new Exception(sender + "用户在消息系统上没有注册");
      }
      rs.close();
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return senderUin;
  }

  /**
   * 改变接收者列表的形式 如从sa,zhangsan,lisi转成'sa','zhangsan','lisi'
   * 
   * @param String
   *          receivers 原列表字符串
   * @return String
   * @throws Exception
   */

  private String converToSqlReceiverList(String receivers) {
    String receiverList = "";
    Vector rec = StringTools.split(receivers, ',');
    for (int i = 0; i < rec.size(); i++) {
      receiverList = receiverList + "'" + ((String) rec.elementAt(i)).trim()
          + "',";
    }
    receiverList = receiverList.substring(0, receiverList.length() - 1);
    return receiverList;
  }

  /**
   * 获取接收者的手机列表
   * 
   * @param Statement
   *          stmt statment
   * @param RTXSvrApi
   *          rtxsvrapiObj rtx对象
   * @param String
   *          receivers 接收人用户列表，用','分隔，例如sa,zhangsan,lisi
   * @return String 用,分隔如13910332322,13801211212,13702323232
   * @throws Exception
   */

  private String getReceiverMobileList(Statement stmt, RTXSvrApi rtxsvrapiObj,
      String receivers) throws Exception {
    String receiverMobileList = "";
    ResultSet rs = null;
    try {
      String receiverList = converToSqlReceiverList(receivers);
      String sqlStr = "select rtxUin from AS_User where user_Id in ("
          + receiverList + ")";
      rs = stmt.executeQuery(sqlStr);
      String userUin = "";
      String userMobile = "";
      while (rs.next()) {
        userUin = rs.getString("rtxUin");
        if (userUin != null) {
          userMobile = rtxsvrapiObj.getUserInfo(userUin, RtxUser.KEY_MOBILE);
          if (userMobile != null && userMobile.length() > 0) {
            receiverMobileList = receiverMobileList + userMobile + ",";
          }
        }
      }
      rs.close();
      if (receiverMobileList.length() == 0) {
        throw new Exception(receivers + "用户在消息系统上没有注册");
      }
      receiverMobileList = receiverMobileList.substring(0, receiverMobileList
          .length() - 1);
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return receiverMobileList;
  }

  /**
   * 获取用户的Email
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          sender 发送人的用户名
   * @return String
   * @throws Exception
   */

  private String getUserEmail(Statement stmt, String sender)
      throws SQLException, Exception {
    String senderEmail = "";
    String sqlStr = "SELECT b.email FROM AS_USER a,AS_EMP b where a.user_id=b.user_id AND a.user_Id ='"
        + sender + "'";
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery(sqlStr);
      if (rs.next()) {
        senderEmail = rs.getString("email");
      } else {
        rs.close();
        throw new Exception(sender + "用户在平台中没有注册邮件");
      }
      rs.close();
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return senderEmail;
  }

  /**
   * 获取接收者的邮件列表
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          sender 接收人用户列表，用','分隔，例如sa,zhangsan,lisi
   * @return String
   *         用,分隔如例如sa@ufgov.com.cn,zhangsan@ufgov.com.cn,lisi@ufgov.com.cn
   * @throws Exception
   */

  private String getReceiverEmailList(Statement stmt, String receivers)
      throws SQLException, Exception {
    String receiverEmailList = "";
    ResultSet rs = null;
    try {
      String receiverList = converToSqlReceiverList(receivers);
      String sqlStr = "SELECT b.email FROM AS_USER a,AS_EMP b where a.user_id=b.user_id AND b.EMAIL IS NOT NULL and a.user_Id in ("
          + receiverList + ")";
      // System.out.println(sqlStr);
      rs = stmt.executeQuery(sqlStr);
      String email;
      while (rs.next()) {
        email = rs.getString("email");
        if (email != null) {
          receiverEmailList = receiverEmailList + email + ",";
        }
      }
      rs.close();
      if (receiverEmailList.length() != 0) {
        // throw new Exception(receivers + "用户在平台中没有注册邮件");
        receiverEmailList = receiverEmailList.substring(0, receiverEmailList
            .length() - 1);
      }

    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return receiverEmailList;
  }
}
