package com.kingdrive.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.business.Delegation;
import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.dto.DelegationMeta;
import com.kingdrive.workflow.exception.WorkflowException;

/**
 * 任务代理接口
 * <p>
 * Title: 工作流系统
 * </p>
 * <p>
 * Description: 任务代理接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 上海精驱科技有限公司
 * </p>
 * 
 * @author 上海精驱科技有限公司
 * @version 1.1
 */
public class DelegateFacade {

  public DelegateFacade() {
  }


  public DelegationMeta getDelegation(int id)
      throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegation(id);
  }

  /**
   * 取得指定员工的有效代理,包括可委托给他人的节点任务与他人委托过来的节点任务
   * 
   * @param executor
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationList(String executor)
      throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationList(executor);
  }

  /**
   * 取得指定员工的有效代理,包括可委托给他人的节点任务与他人委托过来的节点任务
   * 
   * @param executor
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationList(String executor, String templateType,
      Connection conn) throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationList(executor, templateType);
  }

  /**
   * 对相应的节点任务进行委托代理
   * 
   * @param meta
   *          DelegationMeta
   * @throws WorkflowException
   */
  public static void delegate(DelegationMeta meta)
      throws WorkflowException {
    Delegation delegation = new Delegation();
    delegation.delegate(meta);
  }


  /**
   * 取得指定员工委托给他人的代理(员工为节点任务的原拥有者)
   * 
   * @param owner
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationListByOwner(String ownerection)
      throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationListByOwner(ownerection);
  }



  /**
   * 取得指定员工委托给他人的代理(员工为节点任务的原拥有者)
   * 
   * @param nodeId
   *          int
   * @param owner
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationListByOwner(int nodeId, String owner,
      Connection conn) throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationListByOwner(nodeId, owner);
  }


  /**
   * 取得指定员工(非节点任务的原拥有者)的委托给他人的代理
   * 
   * @param sender
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationListBySender(String senderection)
      throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationListBySender(senderection);
  }


  /**
   * 取得指定员工(非节点任务的原拥有者)的委托给他人的代理
   * 
   * @param nodeId
   *          int
   * @param sender
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationListBySender(int nodeId, String sender,
      Connection conn) throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationListBySender(nodeId, sender);
  }

  /**
   * 取得指定员工接受到的代理
   * 
   * @param receiver
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationListByReceiver(String receiver,
      Connection conn) throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationListByReceiver(receiver);
  }


  /**
   * 取得指定员工接受到的代理
   * 
   * @param nodeId
   *          int
   * @param receiver
   *          String
   * @throws WorkflowException
   * @return List a list of DelegationMeta objects.
   */
  public static List getDelegationListByReceiver(int nodeId, String receiver,
      Connection conn) throws WorkflowException {
    Delegation delegation = new Delegation();
    return delegation.getDelegationListByReceiver(nodeId, receiver);
  }


  /**
   * 取消指定代理id的代理
   * 
   * @param delegationId
   *          int
   * @throws WorkflowException
   */
  public static void cancelDelegation(int delegationId)
      throws WorkflowException {
    Delegation delegation = new Delegation();
    delegation.cancel(delegationId);
  }

  /**
   * 清除代理时间失效的所有代理
   * 
   * @throws WorkflowException
   */
  public static void removeOutOfDate() throws WorkflowException {
    Delegation delegation = new Delegation();
    delegation.removeOutOfDate();
  }
}
