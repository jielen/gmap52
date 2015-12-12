package com.anyi.gp.persistence;

import com.anyi.gp.BusinessException;
import com.anyi.gp.pub.BoolMessage;

public interface RowManager {

  public final static String ACTION_NONE = "none";

  public final static String ACTION_INSERT = "insert";

  public final static String ACTION_DELETE = "delete";

  public final static String ACTION_UPDATE = "update";

  public abstract void init();

  public abstract BoolMessage check(String userId);

  public abstract BoolMessage save(String userId) throws BusinessException;

  public abstract String getNewDigestRet();

  /**
   * @param data
   *            The data to set.
   * @uml.property name="data"
   */
  public abstract void setData(String data);

  /**
   * @return Returns the isdigest.
   * @uml.property name="isdigest"
   */
  public abstract boolean isIsdigest();

  /**
   * @param isdigest
   *            The isdigest to set.
   * @uml.property name="isdigest"
   */
  public abstract void setIsdigest(boolean isdigest);

}