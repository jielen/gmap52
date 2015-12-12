/** $Id: RtxDept.java,v 1.1 2008/02/22 09:12:30 liuxiaoyong Exp $ */
package com.anyi.gp.message;

/**
 * @author   leidaohong
 */

public class RtxDept {
  public static String KEY_LDAPID = "LDAPID";

  public static String KEY_DEPTID = "DEPTID";

  public static String KEY_PDEPTID = "PDEPTID";

  public static String KEY_SORTID = "SORTID";

  public static String KEY_NAME = "NAME";

  public static String KEY_INFO = "INFO";

  public static String KEY_COMPLETEDELBS = "COMPLETEDELBS";

  /** 部门Id */
  private String deptId;

  /** 父部门Id */
  private String pDeptId;

  /** 部门名称 */
  private String deptName;

  /** 部门信息 */
  private String deptInfo;

  public RtxDept() {
  }

  /**
 * @return   Returns the deptId.
 * @uml.property   name="deptId"
 */
public String getDeptId() {
	return deptId;
}

  /**
 * @param deptId   The deptId to set.
 * @uml.property   name="deptId"
 */
public void setDeptId(String deptId) {
	this.deptId = deptId;
}

  /**
 * @return   Returns the deptInfo.
 * @uml.property   name="deptInfo"
 */
public String getDeptInfo() {
	return deptInfo;
}

  /**
 * @param deptInfo   The deptInfo to set.
 * @uml.property   name="deptInfo"
 */
public void setDeptInfo(String deptInfo) {
	this.deptInfo = deptInfo;
}

  /**
 * @return   Returns the deptName.
 * @uml.property   name="deptName"
 */
public String getDeptName() {
	return deptName;
}

  /**
 * @param deptName   The deptName to set.
 * @uml.property   name="deptName"
 */
public void setDeptName(String deptName) {
	this.deptName = deptName;
}

  /**
 * @return   Returns the pDeptId.
 * @uml.property   name="pDeptId"
 */
public String getPDeptId() {
	return pDeptId;
}

  /**
 * @param pDeptId   The pDeptId to set.
 * @uml.property   name="pDeptId"
 */
public void setPDeptId(String pDeptId) {
	this.pDeptId = pDeptId;
}

}
