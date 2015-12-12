/**
 * @author hmgkevin
 * @date 2008-03-07
 */
package com.anyi.gp.print.service;

import java.util.List;

public interface PrintSetService {
  /**
   * ����ӡ���ò������ֵ����ݿ���
   * @param setInfo String
   */  
  public void setPrintSetInfo(String setInfo);
  
  /**
   * �����ݿ�����ȡ��ӡ���ò�����Ϣ
   * @param setInfo
   * @return
   */
  public String getPrintSetInfo(String setInfo);
  
  /**
   * ��ȡ��������Ͳ�������
   * @param condition
   * @param componame
   * @return
   */
  public List getCompoTplInfo(String condition, String componame);
  
  /**
   * ���ļ���ȡ��ģ���HTML����
   * @param prn_tpl_code ��ȡ���ļ���
   * @return inTextData String ���ļ����ڣ�����ģ���HTML���룻�������ڣ������Զ����ֵı�־��
   */
  public String getTextDataFromFile(String prn_tpl_code);
  
  /**
   * ͨ�������ݿ���д����ֶ�Text���ַ���ֵ
   * @param tableName Ҫд��Ĵ��ֶα���
   * @param fieldName Ҫд��Ĵ��ֶ��ֶ���
   * @param condition where��ѯ����
   * @param textData Ҫд��Ĵ��ֶ��ַ�������
   * @param rowCount ��¼����
   * @return String
   */
  public String setJasperTextData(String tableName, String fieldName, String condition, String textData, String rowCount);
}
