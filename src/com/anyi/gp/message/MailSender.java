package com.anyi.gp.message;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author   leidaohong
 */
public class MailSender {

  /** �����˵��û��ʼ� */
  private String from;

  /** �����˵��û��ʼ� */
  private String to;

  /** �����˵��û��ʼ� */
  private String cc;

  /** �����˵��û��ʼ� */
  private String bcc;

  /** �ʼ����� */
  private String subject;

  /** �ʼ����� */
  private String body;

  /** Ҫ��֤���û��� */
  private String user;

  /** Ҫ��֤���û����� */
  private String passWord;

  /** ��Ҫsmtp��������֤ */
  private boolean needAuth = false;

  /** smtp������ */
  private String smtpHost;

  /** �ʼ���mimeType */
  private String mimeType = "text/html";

  /** �ʼ��ĸ������� */
  private List FileAffixPathList;

  /** mailSession���� */
  private Session mailSession;

  public MailSender(Session mailSession, String from, String to, String cc,
      String bcc, String subject, String body) {
    this.mailSession = mailSession;
    this.from = from;
    this.to = to;
    this.cc = cc;
    this.bcc = bcc;
    this.subject = subject;
    this.body = body;
  }

  public MailSender(String smtpHost, boolean needAuth, String from, String to,
      String cc, String bcc, String subject, String body, String user,
      String passWord) {
    this.smtpHost = smtpHost;
    this.needAuth = needAuth;
    this.from = from;
    this.to = to;
    this.cc = cc;
    this.bcc = bcc;
    this.subject = subject;
    this.body = body;
    this.user = user;
    this.passWord = passWord;
  }

  public MailSender(String smtpHost, String from, String to, String cc,
      String bcc, String subject, String body) {
    this.smtpHost = smtpHost;
    this.from = from;
    this.to = to;
    this.cc = cc;
    this.bcc = bcc;
    this.subject = subject;
    this.body = body;
  }

  /**
 * @return   Returns the bcc.
 * @uml.property   name="bcc"
 */
public String getBcc() {
	return bcc;
}

  /**
 * @param bcc   The bcc to set.
 * @uml.property   name="bcc"
 */
public void setBcc(String bcc) {
	this.bcc = bcc;
}

  /**
 * @return   Returns the body.
 * @uml.property   name="body"
 */
public String getBody() {
	return body;
}

  /**
 * @param body   The body to set.
 * @uml.property   name="body"
 */
public void setBody(String body) {
	this.body = body;
}

  /**
 * @return   Returns the cc.
 * @uml.property   name="cc"
 */
public String getCc() {
	return cc;
}

  /**
 * @param cc   The cc to set.
 * @uml.property   name="cc"
 */
public void setCc(String cc) {
	this.cc = cc;
}

  /**
 * @return   Returns the from.
 * @uml.property   name="from"
 */
public String getFrom() {
	return from;
}

  /**
 * @param from   The from to set.
 * @uml.property   name="from"
 */
public void setFrom(String from) {
	this.from = from;
}

  /**
 * @return   Returns the mimeType.
 * @uml.property   name="mimeType"
 */
public String getMimeType() {
	return mimeType;
}

  /**
 * @param mimeType   The mimeType to set.
 * @uml.property   name="mimeType"
 */
public void setMimeType(String mimeType) {
	this.mimeType = mimeType;
}

  /**
 * @return   Returns the needAuth.
 * @uml.property   name="needAuth"
 */
public boolean isNeedAuth() {
	return needAuth;
}

  /**
 * @param needAuth   The needAuth to set.
 * @uml.property   name="needAuth"
 */
public void setNeedAuth(boolean needAuth) {
	this.needAuth = needAuth;
}

  /**
 * @return   Returns the smtpHost.
 * @uml.property   name="smtpHost"
 */
public String getSmtpHost() {
	return smtpHost;
}

  /**
 * @param smtpHost   The smtpHost to set.
 * @uml.property   name="smtpHost"
 */
public void setSmtpHost(String smtpHost) {
	this.smtpHost = smtpHost;
}

  /**
 * @return   Returns the subject.
 * @uml.property   name="subject"
 */
public String getSubject() {
	return subject;
}

  /**
 * @param subject   The subject to set.
 * @uml.property   name="subject"
 */
public void setSubject(String subject) {
	this.subject = subject;
}

  /**
 * @return   Returns the to.
 * @uml.property   name="to"
 */
public String getTo() {
	return to;
}

  /**
 * @param to   The to to set.
 * @uml.property   name="to"
 */
public void setTo(String to) {
	this.to = to;
}

  /**
 * @return   Returns the user.
 * @uml.property   name="user"
 */
public String getUser() {
	return user;
}

  /**
 * @param user   The user to set.
 * @uml.property   name="user"
 */
public void setUser(String user) {
	this.user = user;
}

  /**
 * @return   Returns the passWord.
 * @uml.property   name="passWord"
 */
public String getPassWord() {
	return passWord;
}

  /**
 * @param passWord   The passWord to set.
 * @uml.property   name="passWord"
 */
public void setPassWord(String passWord) {
	this.passWord = passWord;
}

  /**
 * @return   Returns the fileAffixPathList.
 * @uml.property   name="fileAffixPathList"
 */
public List getFileAffixPathList() {
	return FileAffixPathList;
}

  /**
 * @param fileAffixPathList   The fileAffixPathList to set.
 * @uml.property   name="fileAffixPathList"
 */
public void setFileAffixPathList(List FileAffixPathList) {
	this.FileAffixPathList = FileAffixPathList;
}

  /**
 * @return   Returns the mailSession.
 * @uml.property   name="mailSession"
 */
public Session getMailSession() {
	return mailSession;
}

  /**
 * @param mailSession   The mailSession to set.
 * @uml.property   name="mailSession"
 */
public void setMailSession(Session mailSession) {
	this.mailSession = mailSession;
}

  /**
   * �����ʼ�
   * 
   * @throws Exception
   */

  public void send() throws Exception {
    Transport transport = null;
    Properties props; // ϵͳ����
    // Session mailSession; //�ʼ��Ự����
    MimeMessage mimeMsg; // MIME�ʼ�����

    try {
      // ����ϵͳ����
      if (this.mailSession == null) {
        props = System.getProperties(); // ���ϵͳ���Զ���
        props.put("mail.smtp.host", this.smtpHost); // ����SMTP����
        if (this.needAuth) {
          props.put("mail.smtp.auth", "true"); // �Ƿ���Ҫsmtp��֤
        }
        mailSession = Session.getDefaultInstance(props);
      }

      // ����MIME�ʼ�����
      mimeMsg = new MimeMessage(this.mailSession);
      // ���÷�����
      if (from != null && from.length() > 0) {
        mimeMsg.setFrom(new InternetAddress(from));
      }
      // } else{
      // throw new Exception("�����߲���Ϊ��");
      // }

      // ����������
      if (to != null && to.length() > 0) {
        mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
            to, false));
      } else {
        throw new Exception("�ռ��˲���Ϊ��");
      }

      // ���ó�����
      if (cc != null && cc.length() > 0) {
        mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(
            cc, false));
      }
      // ���ð�����
      if (bcc != null && bcc.length() > 0) {
        mimeMsg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(
            bcc, false));
      }

      // �����ʼ�����
      mimeMsg.setSubject(subject);

      // �����ʼ�����ʱ��
      mimeMsg.setSentDate(new Date());
      Multipart mp = new MimeMultipart();
      BodyPart bp = new MimeBodyPart();
      bp.setDataHandler(new DataHandler(new ByteArrayDataSource(
          body.getBytes(), this.mimeType)));
      mp.addBodyPart(bp);

      if (FileAffixPathList != null && FileAffixPathList.size() > 0) {
        Iterator list = FileAffixPathList.iterator();
        while (list.hasNext()) {
          String filePath = (String) list.next();
          BodyPart bpAffix = new MimeBodyPart();
          FileDataSource fileds = new FileDataSource(filePath);
          bpAffix.setDataHandler(new DataHandler(fileds));
          bpAffix.setFileName(fileds.getName());
          mp.addBodyPart(bpAffix);
        }
      }
      mimeMsg.setContent(mp);
      // �����ʼ�
      // Transport.send(mimeMsg);
      transport = mailSession.getTransport("smtp");
      transport.connect(this.smtpHost, this.user, this.passWord);
      // transport.connect(mailSession.getProperty("mail.smtp.host"),
      // mailSession.getProperty("mail.user"),
      // mailSession.getProperty("mail.password"));
      // transport.connect();
      transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
      transport.close();

    } catch (AddressException e) {
      throw new Exception("�ʼ���ַ����ȷ");
    } catch (MessagingException e1) {
      e1.printStackTrace();
      throw new Exception("��������smtp����������ʧ��");
    } catch (Exception ex) {
      throw ex;
    } finally {
      try {
        if (transport != null) {
          transport.close();
        }
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
