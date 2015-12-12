/** $Id: RTXSvrApi.java,v 1.1 2008/02/22 09:12:33 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import java.util.Properties;

/**
 * <p>
 * Title: ������ѶͨC�ӿڵ�java API
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

public class RTXSvrApi {

  // �ͻ�����server���ݵ�Э��
  static int PRO_ADDUSER = 0x0001;

  static int PRO_DELUSER = 0x0002;

  static int PRO_SETUSERSMPLINFO = 0x0003;

  static int PRO_GETUSER = 0x0004;

  static int PRO_SETUSER = 0x0005;

  static int PRO_GETUSERSMPLINFO = 0x0006;

  static int PRO_SETUSERPRIVILEGE = 0x0007;

  static int PRO_IFEXIST = 0x0008;

  static int PRO_TRANUSER = 0X0009;

  static int PRO_ADDDEPT = 0x0101;

  static int PRO_DELDEPT = 0x0102;

  static int PRO_SETDEPT = 0x0103;

  static int PRO_GETCHILDDEPT = 0x0104;

  static int PRO_GETDEPTALLUSER = 0x0105;

  static int PRO_SETDEPTPRIVILEGE = 0x0106;

  static int PRO_GETDEPTSMPLINFO = 0x0107;

  static int PRO_SMS_LOGON = 0x1000;

  static int PRO_SMS_SEND = 0x1001;

  static int PRO_SMS_NICKLIST = 0x1002;

  static int PRO_SMS_FUNCLIST = 0x1003;

  static int PRO_SMS_CHECK = 0x1004;

  static int PRO_SYS_USERLOGIN = 0x2000;

  static int PRO_SYS_GETUSERSTATUS = 0x2001;

  static int PRO_SYS_SENDIM = 0x2002;

  static int PRO_SYS_USERLOGINVERIFY = 0x2003;

  static int PRO_EXT_NOTIFY = 0x2100;

  // ��������
  static String OBJNAME_RTXEXT = "EXTTOOLS";

  static String OBJNAME_RTXSYS = "SYSTOOLS";

  static String OBJNAME_DEPTMANAGER = "DEPTMANAGER";

  static String OBJNAME_USERMANAGER = "USERMANAGER";

  static String OBJNAME_SMSMANAGER = "SMSOBJECT";

  static String KEY_TYPE = "TYPE";

  static String KEY_USERID = "USERID";

  static String KEY_USERNAME = "USERNAME";

  static String KEY_UIN = "UIN";

  static String KEY_NICK = "NICK";

  static String KEY_MOBILE = "MOBILE";

  static String KEY_OUTERUIN = "OUTERUIN";

  static String KEY_LASTMODIFYTIME = "LASTMODIFYTIME";

  static String KEY_FACE = "FACE";

  static String KEY_PASSWORD = "PWD";

  static String KEY_AGE = "AGE";

  static String KEY_GENDER = "GENDER";

  static String KEY_BIRTHDAY = "BIRTHDAY";

  static String KEY_BLOODTYPE = "BLOODTYPE";

  static String KEY_CONSTELLATION = "CONSTELLATION";

  static String KEY_COLLAGE = "COLLAGE";

  static String KEY_HOMEPAGE = "HOMEPAGE";

  static String KEY_EMAIL = "EMAIL";

  static String KEY_PHONE = "PHONE";

  static String KEY_FAX = "FAX";

  static String KEY_ADDRESS = "ADDRESS";

  static String KEY_POSTCODE = "POSTCODE";

  static String KEY_COUNTRY = "COUNTRY";

  static String KEY_PROVINCE = "PROVINCE";

  static String KEY_CITY = "CITY";

  static String KEY_MEMO = "MEMO";

  static String KEY_MOBILETYPE = "MOBILETYPE";

  static String KEY_AUTHTYPE = "AUTHTYPE";

  static String KEY_POSITION = "POSITION";

  static String KEY_OPENGSMINFO = "OPENGSMINFO";

  static String KEY_OPENCONTACTINFO = "OPENCONTACTINFO";

  static String KEY_PUBOUTUIN = "PUBOUTUIN";

  static String KEY_PUBOUTNICK = "PUBOUTNICK";

  static String KEY_PUBOUTNAME = "PUBOUTNAME";

  static String KEY_PUBOUTDEPT = "PUBOUTDEPT";

  static String KEY_PUBOUTPOSITION = "PUBOUTPOSITION";

  static String KEY_PUBOUTINFO = "PUBOUTINFO";

  static String KEY_OUTERPUBLISH = "OUTERPUBLISH";

  static String KEY_LDAPID = "LDAPID";

  static String KEY_DEPTID = "DEPTID";

  static String KEY_PDEPTID = "PDEPTID";

  static String KEY_SORTID = "SORTID";

  static String KEY_NAME = "NAME";

  static String KEY_INFO = "INFO";

  static String KEY_COMPLETEDELBS = "COMPLETEDELBS";

  // Ȩ�����
  static String KEY_DENY = "DENY";

  static String KEY_ALLOW = "ALLOW";

  static String KEY_SESSIONKEY = "SESSIONKEY";

  // �������
  static String KEY_SENDER = "SENDER";

  static String KEY_FUNNO = "FUNCNO";

  static String KEY_RECEIVER = "RECEIVER";

  static String KEY_RECEIVERUIN = "RECEIVERUIN";

  static String KEY_SMS = "SMS";

  static String KEY_CUT = "CUT";

  static String KEY_DELFLAG = "DELFLAG";

  // RTXServerҵ���߼�
  static String KEY_RECVUSERS = "RECVUSERS";

  static String KEY_IMMSG = "IMMSG";

  // ��Ϣ����
  static String KEY_MSGID = "MSGID";

  static String KEY_MSGINFO = "MSGINFO";

  static String KEY_ASSISTANTTYPE = "ASSTYPE";

  static String KEY_TITLE = "TITLE";

  // ������ϵ�����
  static String KEY_RESULT_INCODE = "INNERCODE"; // �ڲ�����

  static String KEY_RESULT_CODE = "CODE"; // ���ش���

  static String KEY_RESULT_TYPE = "TYPE"; // ��������

  static String KEY_RESULT_NAME = "NAME";

  static String KEY_RESULT_VALUE = "VALUE";

  static String KEY_RESULT_VARIANT = "VARIANT";

  static {
    System.loadLibrary("SDKAPIJava");
  }

  public native boolean Init();

  public native void UnInit();

  public native String GetError(int iCode);

  public native String GetVersion();

  public native int GetNewObject(String szObjectName);

  public native String GetObjectName(int iObjectHandle);

  public native int SetObjectName(int iObjectHandle, String szObjectName);

  public native int GetNewPropertys();

  public native int IsHandle(int iHandle);

  public native int AddRefHandle(int iHandle);

  public native int ReleaseHandle(int iHandle);

  public native int AddProperty(int iHandle, String szName, String szValue);

  public native int ClearProperty(int iPropertyHandle, int iIndex);

  public native int RemoveProperty(int iPropertyHandle, String szName);

  public native String GetProperty(int iPropertyHandle, String szName);

  public native int SetServerIP(int iObjectHandle, String szServerIP);

  public native String GetServerIP(int iObjectHandle);

  public native int GetServerPort(int iObjectHandle);

  public native int SetServerPort(int iObjectHandle, int iPort);

  public native int GetPropertysCount(int iHandle);

  public native int GetPropertysItem(int iHandle, int iIndex);

  public native int Call(int iObjectHandle, int iPropertyHandle, int iCmdID);

  public native int GetResultPropertys(int iHandle);

  public native int GetResultInt(int iHandle);

  public native String GetResultString(int iHandle);

  public String GetPropertyItemName(int iHandle) {
    return GetProperty(iHandle, KEY_RESULT_NAME);
  }

  public String GetPropertyItemValue(int iHandle) {
    return GetProperty(iHandle, KEY_RESULT_VALUE);
  }

  public int GetResultInnerCode(int iHandle) {
    String sz = GetProperty(iHandle, KEY_RESULT_INCODE);
    return Integer.parseInt(sz);
  }

  public int GetResultCode(int iHandle) {
    String sz = GetProperty(iHandle, KEY_RESULT_CODE);
    return Integer.parseInt(sz);
  }

  public int GetResultType(int iHandle) {
    String sz = GetProperty(iHandle, KEY_RESULT_TYPE);
    return Integer.parseInt(sz);
  }

  /**
   * ͨ��RTX�û����Ͷ���
   * 
   * @param String
   *          szSender �����ߵ�RTX�û�uin
   * @param String
   *          szReceiver �����ߵ��ֻ��б���','�ָ�������13310681234,13819002333
   * @param String
   *          msg ��Ϣ����
   * @param String
   *          iAutoCut �Ƿ�ض� '1'-�� '0'-��
   * @throws Exception
   */

  public void sendSm2(String szSender, String szReceiver, String szSmInfo,
      String iAutoCut) throws Exception {
    int iObj = GetNewObject(OBJNAME_SMSMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_SENDER, szSender);
    AddProperty(iProp, KEY_RECEIVER, szReceiver);
    AddProperty(iProp, KEY_SMS, szSmInfo);
    if (iAutoCut.equalsIgnoreCase("0")) {
      AddProperty(iProp, KEY_CUT, "0");
    } else {
      AddProperty(iProp, KEY_CUT, "1");
    }
    int iResult = Call(iObj, iProp, PRO_SMS_SEND);

    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("���Ͷ���ʱ����:" + errStr);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * ����ϵͳ��ʾ��Ϣ
   * 
   * @param String
   *          szReceivers �����ߵ��û�uin�б���,�ָ�������1001,1002,1003
   * @param String
   *          szMsg ��Ϣ����
   * @param String
   *          szType ��Ϣ��� 1��0
   * @param String
   *          szTitle ��Ϣ����
   * @throws Exception
   */

  public void notify(String szReceivers, String szMsg, String szType,
      String szTitle) throws Exception {
    int iObj = GetNewObject(OBJNAME_RTXEXT);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_USERNAME, szReceivers);
    AddProperty(iProp, KEY_TITLE, szTitle);
    AddProperty(iProp, KEY_MSGINFO, szMsg);
    AddProperty(iProp, KEY_TYPE, szType);
    AddProperty(iProp, KEY_MSGID, "0");
    AddProperty(iProp, KEY_ASSISTANTTYPE, "0");

    int iResult = Call(iObj, iProp, PRO_EXT_NOTIFY);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);

      throw new Exception("����ϵͳ��Ϣʱ����:" + errStr);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * ������Ϣ
   * 
   * @param String
   *          szSender �����ߵ��û�uin
   * @param String
   *          szReceivers �����ߵ��û��б���,�ָ�������1001,1002,1003
   * @param String
   *          szMsg ��Ϣ����
   * @throws Exception
   */

  public void sendIM(String szSender, String szReceivers, String szMsg)
      throws Exception {
    int iObj = GetNewObject(OBJNAME_RTXSYS);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_SENDER, szSender);
    AddProperty(iProp, KEY_RECVUSERS, szReceivers);
    AddProperty(iProp, KEY_IMMSG, szMsg);

    int iResult = Call(iObj, iProp, PRO_SYS_SENDIM);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("������Ϣʱ����:" + iCode + errStr);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * �ж��û��Ƿ�����
   * 
   * @param String
   *          szUser �û�uin
   * @return int 1-���� 0-������
   * @throws Exception
   */

  public int queryUserState(String szUser) throws Exception {
    int iObj = GetNewObject(OBJNAME_RTXSYS);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_USERNAME, szUser);
    int iResult = Call(iObj, iProp, PRO_SYS_GETUSERSTATUS);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);

      throw new Exception("����ѯ�û�״̬����:" + errStr);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
    return iCode;
  }

  /**
   * ��ȡ�û�����������
   * 
   * @param String
   *          szUser �û�uin
   * @return Properties �û�����
   * @throws Exception
   */

  public Properties getUserInfoProps(String szUser) throws Exception {
    int iObj = GetNewObject(OBJNAME_USERMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_USERNAME, szUser);
    int iResult = Call(iObj, iProp, PRO_GETUSER);
    int iCode = GetResultCode(iResult);
    Properties userProps = new Properties();
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("��ѯ�û���Ϣ����:" + iCode + errStr);
    }
    int iProps = GetResultPropertys(iResult);
    int iCount = GetPropertysCount(iProps);
    for (int i = 0; i < iCount; i++) {
      int iHandle = GetPropertysItem(iProps, i);
      userProps.setProperty(GetPropertyItemName(iHandle),
          GetPropertyItemValue(iHandle));
      ReleaseHandle(iHandle);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
    return userProps;
  }

  /**
   * ��ȡ���ŵ�ĳ������ֵ
   * 
   * @param String
   *          deptId ����id
   * @param String
   *          keyName ����������
   * @return String ��������ֵ
   * @throws Exception
   */

  public String getDeptInfo(String deptId, String keyName) throws Exception {
    int iObj = GetNewObject(OBJNAME_DEPTMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_DEPTID, deptId);
    int iResult = Call(iObj, iProp, PRO_GETDEPTSMPLINFO);
    int iCode = GetResultCode(iResult);
    String deptInfoStr = "";
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("��ѯ�û���Ϣ����:" + iCode + errStr);
    }
    int iProps = GetResultPropertys(iResult);
    int iCount = GetPropertysCount(iProps);
    for (int i = 0; i < iCount; i++) {
      int iHandle = GetPropertysItem(iProps, i);
      if (GetPropertyItemName(iHandle).equalsIgnoreCase(keyName)) {
        deptInfoStr = GetPropertyItemValue(iHandle);
        break;
      }
      ReleaseHandle(iHandle);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
    return deptInfoStr;
  }

  /**
   * ��ȡ�û���ĳ������ֵ
   * 
   * @param String
   *          szUser �û�uin
   * @param String
   *          keyName �û�������
   * @return String �û�����ֵ
   * @throws Exception
   */

  public String getUserInfo(String szUser, String keyName) throws Exception {
    int iObj = GetNewObject(OBJNAME_USERMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_USERNAME, szUser);
    int iResult = Call(iObj, iProp, PRO_GETUSER);
    int iCode = GetResultCode(iResult);
    String userInfoStr = "";
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("��ѯ�û���Ϣ����:" + iCode + errStr);
    }
    int iProps = GetResultPropertys(iResult);
    int iCount = GetPropertysCount(iProps);
    for (int i = 0; i < iCount; i++) {
      int iHandle = GetPropertysItem(iProps, i);
      if (GetPropertyItemName(iHandle).equalsIgnoreCase(keyName)) {
        userInfoStr = GetPropertyItemValue(iHandle);
        break;
      }
      ReleaseHandle(iHandle);
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
    return userInfoStr;
  }

  /**
   * ���Ӳ���
   * 
   * @param RtxDept
   *          dept Ҫ���ӵĲ�����
   * @param String
   *          pDeptId �ϼ�����id
   * @throws Exception
   */

  public void addDept(RtxDept dept, String pDeptId) throws Exception {
    int iObj = GetNewObject(OBJNAME_DEPTMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_PDEPTID, pDeptId);
    AddProperty(iProp, KEY_DEPTID, dept.getDeptId());
    AddProperty(iProp, KEY_NAME, dept.getDeptName());
    if (dept.getDeptInfo() != null) {
      AddProperty(iProp, KEY_INFO, dept.getDeptInfo());
    }
    int iResult = Call(iObj, iProp, PRO_ADDDEPT);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);

      throw new Exception("���Ӳ���ʱ����:" + iCode + errStr);
    }

    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * ɾ������
   * 
   * @param String
   *          deptId Ҫɾ���Ĳ���id
   * @param boolean
   *          complete �Ƿ񳹵�ɾ����<br>
   *          true-����ɾ����ϵͳ��ɾ���ò��ż��������в��ż��û�<br>
   *          false-��ϵͳֻɾ���ò��ţ��Ӳ��ź��û���������һ��
   * @throws Exception
   */

  public void deleteDept(String deptId, boolean complete) throws Exception {
    int iObj = GetNewObject(OBJNAME_DEPTMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_DEPTID, deptId);
    if (complete) {
      AddProperty(iProp, KEY_COMPLETEDELBS, "1");
    } else {
      AddProperty(iProp, KEY_COMPLETEDELBS, "0");
    }
    int iResult = Call(iObj, iProp, PRO_DELDEPT);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("ɾ������ʱ����:" + iCode + errStr);
    }

    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * �����û�
   * 
   * @param RtxUser
   *          user Ҫ���ӵ��û���
   * @param String
   *          pDeptId �ϼ�����id
   * @throws Exception
   */

  public void addUser(RtxUser user, String pDeptId) throws Exception {
    int iObj = GetNewObject(OBJNAME_USERMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_DEPTID, pDeptId);
    if (user.getUserUin().equalsIgnoreCase("0")) {
      AddProperty(iProp, KEY_UIN, user.getUserUin());
    }
    if (user.getUserNick() != null) {
      AddProperty(iProp, KEY_NICK, user.getUserNick());
    }
    if (user.getUserName() != null) {
      AddProperty(iProp, KEY_USERNAME, user.getUserName());
    }
    if (user.getFace() != 0) {
      AddProperty(iProp, KEY_FACE, String.valueOf(user.getFace()));
    }
    if (user.getUserPwd() != null) {
      AddProperty(iProp, KEY_PASSWORD, user.getUserPwd());
    }
    if (user.getUserOuterUin().equalsIgnoreCase("0")) {
      AddProperty(iProp, KEY_OUTERUIN, user.getUserOuterUin());
    }
    if (user.getMobile() != null) {
      AddProperty(iProp, KEY_MOBILE, user.getMobile());
    }

    int iResult = Call(iObj, iProp, PRO_ADDDEPT);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);

      throw new Exception("�����û�ʱ����:" + iCode + errStr);
    }

    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * ɾ���û�
   * 
   * @param String
   *          userId Ҫɾ�����û�uin
   * @throws Exception
   */

  public void deleteUser(String userId) throws Exception {
    int iObj = GetNewObject(OBJNAME_USERMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_UIN, userId);
    int iResult = Call(iObj, iProp, PRO_DELUSER);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);
      throw new Exception("ɾ���û�ʱ����:" + iCode + errStr);
    }

    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * �޸��û�
   * 
   * @param String
   *          userId Ҫ�޸ĵ��û�uin
   * @param RtxUser
   *          rtxUser Ҫ���ӵ��û�
   * @throws Exception
   */

  public void editUser(String userId, RtxUser user) throws Exception {
    int iObj = GetNewObject(OBJNAME_USERMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_UIN, String.valueOf(userId));
    if (user.getDeptID().equalsIgnoreCase("0")) {
      AddProperty(iProp, KEY_DEPTID, user.getDeptID());
    }

    if (user.getUserNick() != null) {
      AddProperty(iProp, KEY_NICK, user.getUserNick());
    }
    if (user.getUserName() != null) {
      AddProperty(iProp, KEY_USERNAME, user.getUserName());
    }
    if (user.getFace() != 0) {
      AddProperty(iProp, KEY_FACE, String.valueOf(user.getFace()));
    }
    if (user.getUserPwd() != null) {
      AddProperty(iProp, KEY_PASSWORD, user.getUserPwd());
    }
    if (user.getUserOuterUin().equalsIgnoreCase("0")) {
      AddProperty(iProp, KEY_OUTERUIN, user.getUserOuterUin());
    }
    if (user.getMobile() != null) {
      AddProperty(iProp, KEY_MOBILE, user.getMobile());
    }

    int iResult = Call(iObj, iProp, PRO_SETUSERSMPLINFO);
    int iCode = GetResultCode(iResult);
    if (iCode != 0) {
      String errStr = GetResultString(iResult);
      ReleaseHandle(iObj);
      ReleaseHandle(iProp);
      ReleaseHandle(iResult);

      throw new Exception("�޸��û�ʱ����:" + iCode + errStr);
    }

    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
  }

  /**
   * �ж��û��Ƿ�����
   * 
   * @param String
   *          userName �û�uin
   * @return boolean true-���� false-������
   * @throws Exception
   */

  public boolean isUserOnline(String userName) throws Exception {
    int iObj = GetNewObject(OBJNAME_RTXSYS);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_USERNAME, String.valueOf(userName));

    int iResult = Call(iObj, iProp, PRO_SYS_GETUSERSTATUS);
    int iCode = GetResultCode(iResult);
    boolean isOnline = false;
    if (iCode != 0) {
      isOnline = false;
    } else {
      isOnline = true;
    }
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
    return isOnline;
  }

  /**
   * �ж��û��Ƿ����
   * 
   * @param String
   *          userId �û�uin
   * @throws MsgException
   */

  public boolean isUserExist(String userId) throws Exception {
    int iObj = GetNewObject(OBJNAME_USERMANAGER);
    int iProp = GetNewPropertys();
    AddProperty(iProp, KEY_UIN, userId);

    int iResult = Call(iObj, iProp, PRO_IFEXIST);
    int iCode = GetResultCode(iResult);
    boolean isExist = false;
    if (iCode != 0) {
      throw new Exception("�ж��û��Ƿ����ʱ����:" + iCode + GetResultString(iResult));
    }
    isExist = true;
    ReleaseHandle(iObj);
    ReleaseHandle(iProp);
    ReleaseHandle(iResult);
    return isExist;
  }

}
