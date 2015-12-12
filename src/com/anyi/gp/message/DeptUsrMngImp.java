/** $Id: DeptUsrMngImp.java,v 1.1 2008/02/22 09:12:35 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import java.util.Properties;

/**
 * <p>
 * Title: ��Ϣ���Ѳ�����Ա������ؽӿ�ʵ��
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

public class DeptUsrMngImp implements IDeptUsrMng {
  /**
   * ���Ӳ���
   * 
   * @param RtxDept
   *          dept Ҫ���ӵĲ�����
   * @param String
   *          pDeptId �ϼ�����id
   * @throws MsgException
   */

  public void addDept(RtxDept dept, String pDeptId) throws MsgException {
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        RtxsvrapiObj.addDept(dept, pDeptId);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
  }

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
      throws MsgException {
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        RtxsvrapiObj.deleteDept(deptId, completeDel);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }

  }

  /**
   * �����û�
   * 
   * @param RtxUser
   *          rtxUser Ҫ���ӵ��û���
   * @param String
   *          deptId �ϼ�����id
   * @throws MsgException
   */

  public void addUser(RtxUser rtxUser, String deptId) throws MsgException {
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        RtxsvrapiObj.addUser(rtxUser, deptId);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }

  }

  /**
   * ɾ���û�
   * 
   * @param String
   *          userId Ҫɾ�����û�uin
   * @throws MsgException
   */

  public void deleteUser(String userId) throws MsgException {
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        RtxsvrapiObj.deleteUser(userId);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }

  }

  /**
   * �޸��û�
   * 
   * @param String
   *          userId Ҫ�޸ĵ��û�uin
   * @param RtxUser
   *          rtxUser Ҫ���ӵ��û���
   * @throws MsgException
   */

  public void editUser(String userId, RtxUser rtxUser) throws MsgException {
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        RtxsvrapiObj.editUser(userId, rtxUser);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
  }

  /**
   * �ж��û��Ƿ����
   * 
   * @param String
   *          userId �û�uin
   * @throws MsgException
   */

  public boolean isUserExist(String userId) throws MsgException {
    boolean isExist = false;
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        if (RtxsvrapiObj.isUserExist(userId)) {
          isExist = true;
        }
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
    return isExist;
  }

  /**
   * �ж��û��Ƿ�����
   * 
   * @param String
   *          userName �û�uin
   * @return boolean true-���� false-������
   * @throws MsgException
   */

  public boolean isUserOnline(String userName) throws MsgException {
    boolean isOnline = false;
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        if (RtxsvrapiObj.isUserOnline(userName)) {
          isOnline = true;
        }
        RtxsvrapiObj.UnInit();

      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
    return isOnline;
  }

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

  public String getDeptInfo(String deptId, String keyName) throws MsgException {
    String deptInfo = "";
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        deptInfo = RtxsvrapiObj.getDeptInfo(deptId, keyName);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
    return deptInfo;

  }

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

  public String getUserInfo(String szUser, String keyName) throws MsgException {
    String userInfo = "";
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        userInfo = RtxsvrapiObj.getDeptInfo(szUser, keyName);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
    return userInfo;

  }

  /**
   * ��ȡ�û�����������
   * 
   * @param String
   *          szUser �û�uin
   * @return Properties �û����ԣ�����������������RtxUser����
   * @throws MsgException
   */

  public Properties getUserInfoProps(String szUser) throws MsgException {
    Properties userInfo = new Properties();
    try {
      RTXSvrApi RtxsvrapiObj = new RTXSvrApi();
      if (RtxsvrapiObj.Init()) {
        userInfo = RtxsvrapiObj.getUserInfoProps(szUser);
        RtxsvrapiObj.UnInit();
      }
    } catch (Exception ex) {
      throw new MsgException(ex.getMessage());
    }
    return userInfo;
  }
}
