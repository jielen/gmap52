package com.anyi.gp.pub;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.TableData;

/**
 * <p>
 * �����ѯ�ӿ�
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author �Ƴб�
 * @version 1.0
 */
public interface QueryReport {

  /**
   * @param parameters
   *          ������������ֵ�ļ�ֵ��
   * @return ���ݰ�
   */
  public Datum getReportData(TableData parameters) throws BusinessException;
}
