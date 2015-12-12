package com.anyi.gp.access;

import java.sql.Connection;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Delta;
import com.anyi.gp.TableData;

public interface QueryReportWithConn {
  public Delta getReportData(Connection conn, TableData param) throws BusinessException;
}
