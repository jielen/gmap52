/** $Id: IDeptUsrMng.java,v 1.1 2008/02/22 09:12:31 liuxiaoyong Exp $ */
package com.anyi.gp.message;

import java.util.Properties;

/**
 * <p>
 * Title: 消息部门用户管理接口
 * </p>
 * <p>
 * Description: 消息部门用户管理接口
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
 * @time: 2004/04/30
 */

public interface IDeptUsrMng {
  /**
   * 增加部门
   * 
   * @param RtxDept
   *          dept 要增加的部门类
   * @param String
   *          pDeptId 上级部门id
   * @throws MsgException
   */

  public void addDept(RtxDept dept, String pDeptId) throws MsgException;

  /**
   * 删除部门
   * 
   * @param String
   *          deptId 要删除的部门id
   * @param boolean
   *          completeDel 是否彻底删除。<br>
   *          true-彻底删除，系统将删除该部门及以下所有部门及用户<br>
   *          false-否，系统只删除该部门，子部门和用户向上提升一级
   * @throws MsgException
   */

  public void deleteDept(String deptId, boolean completeDel)
      throws MsgException;

  /**
   * 增加用户
   * 
   * @param RtxUser
   *          rtxUser 要增加的用户类
   * @param String
   *          deptId 上级部门id
   * @throws MsgException
   */

  public void addUser(RtxUser rtxUser, String deptId) throws MsgException;

  /**
   * 删除用户
   * 
   * @param String
   *          userId 要删除的用户uin
   * @throws MsgException
   */

  public void deleteUser(String userId) throws MsgException;

  /**
   * 修改用户
   * 
   * @param String
   *          userId 要修改的用户uin
   * @param RtxUser
   *          rtxUser 要增加的用户类
   * @throws MsgException
   */

  public void editUser(String userId, RtxUser rtxUser) throws MsgException;

  /**
   * 判断用户是否存在
   * 
   * @param String
   *          userId 用户uin
   * @return boolean true-存在 false-不存在
   * @throws MsgException
   */

  public boolean isUserExist(String userId) throws MsgException;

  /**
   * 判断用户是否在线
   * 
   * @param String
   *          userName 用户uin
   * @return boolean true-在线 false-不在线
   * @throws MsgException
   */
  public boolean isUserOnline(String userName) throws MsgException;

  /**
   * 获取部门的某个属性值
   * 
   * @param String
   *          deptId 部门id
   * @param String
   *          keyName 部门属性名，属性名常量定义在RtxDept类中
   * @return String 部门属性值
   * @throws MsgException
   */

  public String getDeptInfo(String deptId, String keyName) throws MsgException;

  /**
   * 获取用户的某个属性值
   * 
   * @param String
   *          szUser 用户uin
   * @param String
   *          keyName 用户属性名，属性名常量定义在RtxUser类中
   * @return String 用户属性值
   * @throws MsgException
   */

  public String getUserInfo(String szUser, String keyName) throws MsgException;

  /**
   * 获取用户的所有属性
   * 
   * @param String
   *          szUser 用户uin
   * @return Properties 用户属性，属性名常量定义在RtxUser类中
   * @throws MsgException
   */

  public Properties getUserInfoProps(String szUser) throws MsgException;

}
