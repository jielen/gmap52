/** $Id: IMessage.java,v 1.1 2008/02/22 09:12:32 liuxiaoyong Exp $ */
package com.anyi.gp.message;

/**
 * <p>
 * Title: ��Ϣ���ѽӿ�
 * </p>
 * <p>
 * Description: ��Ϣ���ѽӿ�
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
 * @time: 2004/03/30
 */

public interface IMessage {
  /**
   * ������Ϣ(�û����û�)
   * 
   * @param String
   *          sender �����ߵ��û���
   * @param String
   *          receivers �����ߵ��û��б���','�ָ�������sa,zhangsan,lisi
   * @param String
   *          msg ��Ϣ����
   * @throws MsgException
   */

  public void sendInfomation(String sender, String receivers, String msg)
      throws MsgException;

  /**
   * ͨ��RTX�û�������Ϣ(RTX�û���RTX�û�)
   * 
   * @param String
   *          sender �����ߵ�RTX�û�uin
   * @param String
   *          receivers �����ߵ�RTX�û�uin�б���','�ָ�������1001,900,800
   * @param String
   *          msg ��Ϣ����
   * @throws MsgException
   */

  public void sendInfomationByRtxUser(String sender, String receivers,
      String msg) throws MsgException;

  /**
   * ��ʱ��Ϣ����(ϵͳ���û�)
   * 
   * @param String
   *          receivers �����ߵ��û��б���','�ָ�������sa,zhangsan,lisi
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          type ��Ϣ��� 1��0
   * @param String
   *          title ��Ϣ����
   * @throws MsgException
   */

  public void notify(String receivers, String msg, String type, String title)
      throws MsgException;

  /**
   * ��ʱ��Ϣ���Ѹ�RTX�û�(ϵͳ��RTX�û�)
   * 
   * @param String
   *          receivers �����ߵ�RTX�û�uin�б���','�ָ�������1001,900,800
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          type ��Ϣ��� 1��0
   * @param String
   *          title ��Ϣ����
   * @throws MsgException
   */

  public void notifyToRtxUser(String receivers, String msg, String type,
      String title) throws MsgException;

  /**
   * ���Ͷ���(�û����ֻ�)
   * 
   * @param String
   *          sender �����ߵ��û���
   * @param String
   *          receivers �����ߵ��ֻ��б���','�ָ�������13310681234,13819002333
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          cut �Ƿ�ض� '1'-�� '0'-��
   * @throws MsgException
   */

  public void sendShortMessage(String sender, String receivers, String msg,
      String cut) throws MsgException;

  /**
   * ͨ��RTX�û����Ͷ���(RTX�û����ֻ�)
   * 
   * @param String
   *          sender �����ߵ�RTX�û�uin
   * @param String
   *          receivers �����ߵ��ֻ��б���','�ָ�������13310681234,13819002333
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          cut �Ƿ�ض� '1'-�� '0'-��
   * @throws MsgException
   */

  public void sendShortMessageByRtxUser(String sender, String receivers,
      String msg, String cut) throws MsgException;

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
      String msg, String cut) throws MsgException;

  /**
   * �ж���Ѷͨ�����Ƿ����
   * 
   * @return boolean true-���� false-������
   */

  public boolean isMessageCanBeUsed();

  /**
   * �����ʼ�
   * 
   * @param String
   *          from ������
   * @param String
   *          to
   *          �������б���','�ָ�������sa@ufgov.com.cn,zhangsan@ufgov.com.cn,lisi@ufgov.com.cn
   * @param String
   *          cc ������
   * @param String
   *          bcc ������
   * @param String
   *          subject ����
   * @param String
   *          body ����
   * @param String
   *          mimeType Ĭ��text/html
   * @throws Exception
   */

  public void sendEmail(String from, String to, String cc, String bcc,
      String subject, String body, String mimeType) throws Exception;

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
      throws Exception;

  /**
   * �ж��ʼ��Ƿ����
   * 
   * @return boolean true-���� false-������
   */

  public boolean isEmailCanBeUsed();

}
