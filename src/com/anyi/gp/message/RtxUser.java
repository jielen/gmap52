/** $Id: RtxUser.java,v 1.1 2008/02/22 09:12:33 liuxiaoyong Exp $ */
package com.anyi.gp.message;

/**
 * @author   leidaohong
 */

public class RtxUser {
  public static String KEY_USERID = "USERID";

  public static String KEY_USERNAME = "USERNAME";

  public static String KEY_UIN = "UIN";

  public static String KEY_NICK = "NICK";

  public static String KEY_MOBILE = "MOBILE";

  public static String KEY_OUTERUIN = "OUTERUIN";

  public static String KEY_LASTMODIFYTIME = "LASTMODIFYTIME";

  public static String KEY_FACE = "FACE";

  public static String KEY_PASSWORD = "PWD";

  public static String KEY_AGE = "AGE";

  public static String KEY_GENDER = "GENDER";

  public static String KEY_BIRTHDAY = "BIRTHDAY";

  public static String KEY_BLOODTYPE = "BLOODTYPE";

  public static String KEY_CONSTELLATION = "CONSTELLATION";

  public static String KEY_COLLAGE = "COLLAGE";

  public static String KEY_HOMEPAGE = "HOMEPAGE";

  public static String KEY_EMAIL = "EMAIL";

  public static String KEY_PHONE = "PHONE";

  public static String KEY_FAX = "FAX";

  public static String KEY_ADDRESS = "ADDRESS";

  public static String KEY_POSTCODE = "POSTCODE";

  public static String KEY_COUNTRY = "COUNTRY";

  public static String KEY_PROVINCE = "PROVINCE";

  public static String KEY_CITY = "CITY";

  public static String KEY_MEMO = "MEMO";

  public static String KEY_MOBILETYPE = "MOBILETYPE";

  public static String KEY_AUTHTYPE = "AUTHTYPE";

  public static String KEY_POSITION = "POSITION";

  public static String KEY_OPENGSMINFO = "OPENGSMINFO";

  public static String KEY_OPENCONTACTINFO = "OPENCONTACTINFO";

  public static String KEY_PUBOUTUIN = "PUBOUTUIN";

  public static String KEY_PUBOUTNICK = "PUBOUTNICK";

  public static String KEY_PUBOUTNAME = "PUBOUTNAME";

  public static String KEY_PUBOUTDEPT = "PUBOUTDEPT";

  public static String KEY_PUBOUTPOSITION = "PUBOUTPOSITION";

  public static String KEY_PUBOUTINFO = "PUBOUTINFO";

  public static String KEY_OUTERPUBLISH = "OUTERPUBLISH";

  /** 用户RTX唯一号 */
  private String userUin;

  /** 用户部门号 */
  private String deptID;

  /** 用户头像编号 */
  private int face;

  /** 用户密码 */
  private String userPwd;

  /** 用户昵称 */
  private String userNick;

  /** 用户姓名 */
  private String userName;

  /** 用户外部号码 */
  private String userOuterUin;

  /** 用户手机号 */
  private String mobile;

  public RtxUser() {
  }

  /**
 * @return   Returns the deptID.
 * @uml.property   name="deptID"
 */
public String getDeptID() {
	return deptID;
}

  /**
 * @param deptID   The deptID to set.
 * @uml.property   name="deptID"
 */
public void setDeptID(String deptID) {
	this.deptID = deptID;
}

  /**
 * @return   Returns the face.
 * @uml.property   name="face"
 */
public int getFace() {
	return face;
}

  /**
 * @param face   The face to set.
 * @uml.property   name="face"
 */
public void setFace(int face) {
	this.face = face;
}

  /**
 * @return   Returns the mobile.
 * @uml.property   name="mobile"
 */
public String getMobile() {
	return mobile;
}

  /**
 * @param mobile   The mobile to set.
 * @uml.property   name="mobile"
 */
public void setMobile(String mobile) {
	this.mobile = mobile;
}

  /**
 * @return   Returns the userName.
 * @uml.property   name="userName"
 */
public String getUserName() {
	return userName;
}

  /**
 * @param userName   The userName to set.
 * @uml.property   name="userName"
 */
public void setUserName(String userName) {
	this.userName = userName;
}

  /**
 * @return   Returns the userNick.
 * @uml.property   name="userNick"
 */
public String getUserNick() {
	return userNick;
}

  /**
 * @param userNick   The userNick to set.
 * @uml.property   name="userNick"
 */
public void setUserNick(String userNick) {
	this.userNick = userNick;
}

  /**
 * @return   Returns the userOuterUin.
 * @uml.property   name="userOuterUin"
 */
public String getUserOuterUin() {
	return userOuterUin;
}

  /**
 * @param userOuterUin   The userOuterUin to set.
 * @uml.property   name="userOuterUin"
 */
public void setUserOuterUin(String userOuterUin) {
	this.userOuterUin = userOuterUin;
}

  /**
 * @return   Returns the userPwd.
 * @uml.property   name="userPwd"
 */
public String getUserPwd() {
	return userPwd;
}

  /**
 * @param userPwd   The userPwd to set.
 * @uml.property   name="userPwd"
 */
public void setUserPwd(String userPwd) {
	this.userPwd = userPwd;
}

  /**
 * @return   Returns the userUin.
 * @uml.property   name="userUin"
 */
public String getUserUin() {
	return userUin;
}

  /**
 * @param userUin   The userUin to set.
 * @uml.property   name="userUin"
 */
public void setUserUin(String userUin) {
	this.userUin = userUin;
}
}
