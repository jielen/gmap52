package com.anyi.gp.print.service;

import com.anyi.gp.Delta;

/**
 * 
 * �Դ�ӡ���ݽ��ж��δ���Ľӿ�
 * ����spring��aop���������з��������ص���
 */
public interface IPrintModelService {

  /**
   * ����sqlid��keyCondition��ҵ��ϵͳ���Ը��ݴ˽��ж��δ���
   * keyCondition�Ĵ�����Բο��͵���DataGenerator.processKeyCondition����
   * @param sqlid
   * @param keyCondition
   * @return
   */
  public Delta processPrintModel(String sqlid, String keyCondition);
  
  /**
   * ����printData��ҵ��ϵͳ���Զ�����ж��δ���
   * @param printData
   * @return
   */
  public Delta processPrintModel(Delta printData);
  
}
