package com.kingdrive.framework.db;

import com.kingdrive.workflow.db.DBHelper;


public final class SequenceManager {

  private static SequenceManager sequenceManager = null;

  private SequenceManager() throws Exception {
  }

  public long getCurrentId(String sequenceName) {
    String sql = "select " + sequenceName + ".currval from dual";
    Object lastNumber = DBHelper.queryOneValue(sql, new Object[] {});
    if (null != lastNumber) {
      return Long.parseLong(lastNumber.toString(), 10);
    }
    throw new RuntimeException("Error041491: 找不到 " + sequenceName + " 的ID");
  }

  public long getNextId(String sequenceName) {
    String sql = "select " + sequenceName + ".nextval from dual";
    String[] params = new String[] {};
    Object lastNumber = DBHelper.queryOneValue(sql, params);
    if (null != lastNumber) {
      return Long.parseLong(lastNumber.toString(), 10);
    }
    throw new RuntimeException("Error041491: 找不到 " + sequenceName + " 的ID");
  }

  public static SequenceManager getSequenceManager() {
    if (sequenceManager == null)
      try {
        sequenceManager = new SequenceManager();
      } catch (Exception e) {
        e.printStackTrace();
        sequenceManager = null;
      }
    return sequenceManager;
  }

}
