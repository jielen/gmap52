package com.kingdrive.framework.db;

public class Sequence {

  private String name;

  private String tableName;

  private static final int DEFAULT_INCREMENT = 1;

  private static final int DEFAULT_CACHESIZE = 20;

  private long maxValue;

  private long minValue;

  private long lastNumber;

  private int cacheSize;

  private int cycleFlag;

  private int orderFlag;

  private int increment;

  public Sequence() {
    maxValue = 9223372036854775807L;
    minValue = 0L;
    increment = DEFAULT_INCREMENT;
    lastNumber = 0L;
    cacheSize = DEFAULT_CACHESIZE;
    cycleFlag = 0;
    orderFlag = 0;
  }

  public int getCacheSize() {
    return cacheSize;
  }

  public int getCycleFlag() {
    return cycleFlag;
  }

  public int getIncrement() {
    return increment;
  }

  public long getLastNumber() {
    return lastNumber;
  }

  public long getMaxValue() {
    return maxValue;
  }

  public long getMinValue() {
    return minValue;
  }

  public String getName() {
    return name;
  }

  public int getOrderFlag() {
    return orderFlag;
  }

  public String getTableName() {
    return tableName;
  }

  public void setCacheSize(int newCacheSize) {
    cacheSize = newCacheSize;
  }

  public void setCycleFlag(int newCycleFlag) {
    cycleFlag = newCycleFlag;
  }

  public void setIncrement(int newIncrement) {
    increment = newIncrement;
  }

  public void setLastNumber(long newLastNumber) {
    lastNumber = newLastNumber;
  }

  public void setMaxValue(long newMaxValue) {
    maxValue = newMaxValue;
  }

  public void setMinValue(long newMinValue) {
    minValue = newMinValue;
  }

  public void setName(String newName) {
    name = newName;
  }

  public void setOrderFlag(int newOrderFlag) {
    orderFlag = newOrderFlag;
  }

  public void setTableName(String newTableName) {
    tableName = newTableName;
  }
}
