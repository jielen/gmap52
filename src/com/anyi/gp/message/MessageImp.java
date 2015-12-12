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
 * Title: ��Ϣ������ؽӿ�ʵ��
 * </p>
 * <p>
 * Description:
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
   * ������Ϣ(�û����û�)
   * 
   * @param String
   *          sender �����ߵ��û���
   * @param String
   *          receivers �����ߵ��û��б���,�ָ�������sa,zhangsan,lisi
   * @param String
   *          msg ��Ϣ����
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
      // ��ʼ��RTXSvrApi����
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // ���ҷ���������Ӧ�������ѶͨRTX��
        stmt = conn.createStatement();
        String senderUin = getUserUin(stmt, sender);
        // ���ҽ���������Ӧ�������ѶͨRTX��
        String receiverUinList = getReceiverUinList(stmt, receivers);
        // ����
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
   * ���Ͷ���(�û����ֻ�)
   * 
   * @param String
   *          sender �����ߵ��û���
   * @param String
   *          receivers �����ߵ��ֻ��б���,�ָ�������13310681234,13819002333
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          cut �Ƿ�ض� '1'-�� '0'-��
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
      // ��ʼ��RTXSvrApi����
      conn = DAOFactory.getInstance().getConnection();
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // ���ҷ���������Ӧ�������ѶͨRTX��
        stmt = conn.createStatement();
        String senderUin = getUserUin(stmt, sender);

        // ����
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
   * ���Ͷ���(�û����û�)
   * 
   * @param String
   *          sender �����ߵ��û���
   * @param String
   *          receivers �����ߵ��ֻ��б���,�ָ�������sa,zhangsan
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          cut �Ƿ�ض� '1'-�� '0'-��
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
        throw new Exception("û�н�����");
      }
      conn = DAOFactory.getInstance().getConnection();
      // ��ʼ��RTXSvrApi����
      // int needCut=Integer.parseInt(cut);
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // ���ҷ���������Ӧ�������ѶͨRTX��
        stmt = conn.createStatement();
        String senderUin = getUserUin(stmt, sender);
        String receiverMobileList = getReceiverMobileList(stmt, rtxsvrapiObj,
            receivers);
        // ����
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
   * ��ʱ��Ϣ����(ϵͳ���û�)
   * 
   * @param String
   *          receivers �����ߵ��û��б���,�ָ�������sa,zhangsan,lisi
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          type ��Ϣ��� '1'��'0'
   * @param String
   *          title ��Ϣ����
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
        throw new Exception("û�н�����");
      }
      conn = DAOFactory.getInstance().getConnection();
      // ��ʼ��RTXSvrApi����
      rtxsvrapiObj = new RTXSvrApi();
      if (rtxsvrapiObj.Init()) {
        // ���ҽ���������Ӧ�������ѶͨRTX��
        stmt = conn.createStatement();
        String receiverUinList = getReceiverUinList(stmt, receivers);
        // ����
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
   * ͨ��RTX�û�������Ϣ(RTX�û���RTX�û�)
   * 
   * @param String
   *          sender �����ߵ�RTX�û�uin
   * @param String
   *          receivers �����ߵ�RTX�û�uin�б���,�ָ�������1001,900,800
   * @param String
   *          msg ��Ϣ����
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
   * ͨ��RTX�û����Ͷ���(RTX�û����ֻ�)
   * 
   * @param String
   *          sender �����ߵ�RTX�û�uin
   * @param String
   *          receivers �����ߵ��ֻ��б���,�ָ�������13310681234,13819002333
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          cut �Ƿ�ض� '1'-�� '0'-��
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
   * ��ʱ��Ϣ���Ѹ�RTX�û�(ϵͳ��RTX�û�)
   * 
   * @param String
   *          receivers �����ߵ�RTX�û�uin�б���,�ָ�������1001,900,800
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          type ��Ϣ��� '1'��'0'
   * @param String
   *          title ��Ϣ����
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
   * �����ʼ�
   * 
   * @param String
   *          from ������
   * @param String
   *          to
   *          �������б���','�ָ�������sa@ufgov.com.cn,zhangsan@ufgov.com.cn,lisi@ufgov.com.cn
   * @param String
   *          cc �������б�
   * @param String
   *          bcc �������б�
   * @param String
   *          subject ����
   * @param String
   *          body ����
   * @param String
   *          mimeType Ĭ��text/html
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
      throw new MsgException("smtp��������Ϊ��");
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
   * �����ʼ����û�
   * 
   * @param String
   *          uFrom �����û�,���null����config.xml���趨�ʼ�����Ա�ʼ������ʼ�
   * @param String
   *          uTo �������û��б���','�ָ�������sa,zhangsan,lisi
   * @param String
   *          uCc �������û��б�
   * @param String
   *          uBcc �������û��б�
   * @param String
   *          subject ����
   * @param String
   *          body ����
   * @param String
   *          mimeType Ĭ��text/html
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
        throw new MsgException("û���ռ���");
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
        throw new MsgException("smtp��������Ϊ��");
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
   * ��ȡ�����ߵ�Uin�б�
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          receivers �������û��б���','�ָ�������sa,zhangsan,lisi
   * @return String ��,�ָ���1001,1002,1003
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
        throw new Exception(receivers + "�û�����Ϣϵͳ��û��ע��");
      }
      receiverUinList = receiverUinList.substring(0,
          receiverUinList.length() - 1);
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return receiverUinList;
  }

  /**
   * ��ȡ�û���Uin
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          sender �����˵��û���
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
        throw new Exception(sender + "�û�����Ϣϵͳ��û��ע��");
      }
      rs.close();
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return senderUin;
  }

  /**
   * �ı�������б����ʽ ���sa,zhangsan,lisiת��'sa','zhangsan','lisi'
   * 
   * @param String
   *          receivers ԭ�б��ַ���
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
   * ��ȡ�����ߵ��ֻ��б�
   * 
   * @param Statement
   *          stmt statment
   * @param RTXSvrApi
   *          rtxsvrapiObj rtx����
   * @param String
   *          receivers �������û��б���','�ָ�������sa,zhangsan,lisi
   * @return String ��,�ָ���13910332322,13801211212,13702323232
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
        throw new Exception(receivers + "�û�����Ϣϵͳ��û��ע��");
      }
      receiverMobileList = receiverMobileList.substring(0, receiverMobileList
          .length() - 1);
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return receiverMobileList;
  }

  /**
   * ��ȡ�û���Email
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          sender �����˵��û���
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
        throw new Exception(sender + "�û���ƽ̨��û��ע���ʼ�");
      }
      rs.close();
    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return senderEmail;
  }

  /**
   * ��ȡ�����ߵ��ʼ��б�
   * 
   * @param Statement
   *          stmt statment
   * @param String
   *          sender �������û��б���','�ָ�������sa,zhangsan,lisi
   * @return String
   *         ��,�ָ�������sa@ufgov.com.cn,zhangsan@ufgov.com.cn,lisi@ufgov.com.cn
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
        // throw new Exception(receivers + "�û���ƽ̨��û��ע���ʼ�");
        receiverEmailList = receiverEmailList.substring(0, receiverEmailList
            .length() - 1);
      }

    } finally {
      DBHelper.closeConnection(null, null, rs);
    }
    return receiverEmailList;
  }
}
