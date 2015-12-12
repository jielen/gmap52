package com.anyi.gp.pub;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Datum;
import com.anyi.gp.TableData;

/**
 * <p>
 * 报表查询接口
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
 * @author 黄承彪
 * @version 1.0
 */
public interface QueryReport {

  /**
   * @param parameters
   *          参数名、参数值的键值对
   * @return 数据包
   */
  public Datum getReportData(TableData parameters) throws BusinessException;
}
