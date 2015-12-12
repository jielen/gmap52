/** $Id: IDeptUsrMng.java,v 1.1 2008/02/22 09:12:31 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import java.util.Properties;

/**
 * <p>
 * Title: ��Ϣ�����û�����ӿ�
 * </p>
 * <p>
 * Description: ��Ϣ�����û�����ӿ�
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

public interface IDeptUsrMng {
  /**
   * ���Ӳ���
   * 
   * @param RtxDept
   *          dept Ҫ���ӵĲ�����
   * @param String
   *          pDeptId �ϼ�����id
   * @throws MsgException
   */

  public void addDept(RtxDept dept, String pDeptId) throws MsgException;

  /**
   * ɾ������
   * 
   * @param String
   *          deptId Ҫɾ���Ĳ���id
   * @param boolean
   *          completeDel �Ƿ񳹵�ɾ����<br>
   *          true-����ɾ����ϵͳ��ɾ���ò��ż��������в��ż��û�<br>
   *          false-��ϵͳֻɾ���ò��ţ��Ӳ��ź��û���������һ��
   * @throws MsgException
   */

  public void deleteDept(String deptId, boolean completeDel)
      throws MsgException;

  /**
   * �����û�
   * 
   * @param RtxUser
   *          rtxUser Ҫ���ӵ��û���
   * @param String
   *          deptId �ϼ�����id
   * @throws MsgException
   */

  public void addUser(RtxUser rtxUser, String deptId) throws MsgException;

  /**
   * ɾ���û�
   * 
   * @param String
   *          userId Ҫɾ�����û�uin
   * @throws MsgException
   */

  public void deleteUser(String userId) throws MsgException;

  /**
   * �޸��û�
   * 
   * @param String
   *          userId Ҫ�޸ĵ��û�uin
   * @param RtxUser
   *          rtxUser Ҫ���ӵ��û���
   * @throws MsgException
   */

  public void editUser(String userId, RtxUser rtxUser) throws MsgException;

  /**
   * �ж��û��Ƿ����
   * 
   * @param String
   *          userId �û�uin
   * @return boolean true-���� false-������
   * @throws MsgException
   */

  public boolean isUserExist(String userId) throws MsgException;

  /**
   * �ж��û��Ƿ�����
   * 
   * @param String
   *          userName �û�uin
   * @return boolean true-���� false-������
   * @throws MsgException
   */
  public boolean isUserOnline(String userName) throws MsgException;

  /**
   * ��ȡ���ŵ�ĳ������ֵ
   * 
   * @param String
   *          deptId ����id
   * @param String
   *          keyName ����������������������������RtxDept����
   * @return String ��������ֵ
   * @throws MsgException
   */

  public String getDeptInfo(String deptId, String keyName) throws MsgException;

  /**
   * ��ȡ�û���ĳ������ֵ
   * 
   * @param String
   *          szUser �û�uin
   * @param String
   *          keyName �û�������������������������RtxUser����
   * @return String �û�����ֵ
   * @throws MsgException
   */

  public String getUserInfo(String szUser, String keyName) throws MsgException;

  /**
   * ��ȡ�û�����������
   * 
   * @param String
   *          szUser �û�uin
   * @return Properties �û����ԣ�����������������RtxUser����
   * @throws MsgException
   */

  public Properties getUserInfoProps(String szUser) throws MsgException;

}
