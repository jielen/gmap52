package com.kingdrive.workflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.kingdrive.workflow.business.Delegation;
import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.dto.DelegationMeta;
import com.kingdrive.workflow.exception.WorkflowException;

/**
 * �������ӿ�
 * <p>
 * Title: ������ϵͳ
 * </p>
 * <p>
 * Description: �������ӿ�
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: �Ϻ������Ƽ����޹�˾
 * </p>
 * 
 * @author �Ϻ������Ƽ����޹�˾
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
   * ȡ��ָ��Ա������Ч����,������ί�и����˵Ľڵ�����������ί�й����Ľڵ�����
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
   * ȡ��ָ��Ա������Ч����,������ί�и����˵Ľڵ�����������ί�й����Ľڵ�����
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
   * ����Ӧ�Ľڵ��������ί�д���
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
   * ȡ��ָ��Ա��ί�и����˵Ĵ���(Ա��Ϊ�ڵ������ԭӵ����)
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
   * ȡ��ָ��Ա��ί�и����˵Ĵ���(Ա��Ϊ�ڵ������ԭӵ����)
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
   * ȡ��ָ��Ա��(�ǽڵ������ԭӵ����)��ί�и����˵Ĵ���
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
   * ȡ��ָ��Ա��(�ǽڵ������ԭӵ����)��ί�и����˵Ĵ���
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
   * ȡ��ָ��Ա�����ܵ��Ĵ���
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
   * ȡ��ָ��Ա�����ܵ��Ĵ���
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
   * ȡ��ָ������id�Ĵ���
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
   * �������ʱ��ʧЧ�����д���
   * 
   * @throws WorkflowException
   */
  public static void removeOutOfDate() throws WorkflowException {
    Delegation delegation = new Delegation();
    delegation.removeOutOfDate();
  }
}
