package com.ufgov.workflow.engine.calendar;

import java.io.Serializable;

public class Duration
  implements Serializable
{
  private static final long serialVersionUID = 9083268183425198585L;
  public static final String DAY = "DAY";
  public static final String MONTH = "MONTH";
  public static final String YEAR = "YEAR";
  public static final String HOUR = "HOUR";
  public static final String MINUTE = "MINUTE";
  public static final String SECOND = "SECOND";
  public static final String WEEK = "WEEK";
  private int value;
  private String unit;
  private boolean isBusinessTime = true;

  public Duration(int paramInt, String paramString)
  {
    this.value = paramInt;
    this.unit = paramString;
  }

  public int getValue()
  {
    return this.value;
  }

  public void setValue(int paramInt)
  {
    this.value = paramInt;
  }

  public String getUnit()
  {
    return this.unit;
  }

  public void setUnit(String paramString)
  {
    this.unit = paramString;
  }

  public String getUnit(String paramString)
  {
    if (this.unit == null)
      return paramString;
    return this.unit;
  }

  public long getDurationInMilliseconds(String paramString)
  {
    int i = getValue();
    String str = getUnit(paramString);
    if (i == 0)
      return i;
    long l = i * toMilliseconds(str);
    return l;
  }

  public long toMilliseconds(String paramString)
  {
    if (paramString == null)
      return 0L;
    if (paramString.equals("SECOND"))
      return 1000L;
    if (paramString.equals("MINUTE"))
      return 60000L;
    if (paramString.equals("HOUR"))
      return 3600000L;
    if (paramString.equals("DAY"))
      return 86400000L;
    if (paramString.equals("MONTH"))
      return 2592000000L;
    if (paramString.equals("YEAR"))
      return 946080000000L;
    if (paramString.equals("WEEK"))
      return 604800000L;
    return 0L;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.value);
    if (this.unit != null)
      localStringBuffer.append(this.unit);
    return localStringBuffer.toString();
  }

  public boolean isBusinessTime()
  {
    return this.isBusinessTime;
  }

  public void setBusinessTime(boolean paramBoolean)
  {
    this.isBusinessTime = paramBoolean;
  }
}

/* Location:           C:\Documents and Settings\Administrator\桌面\
 * Qualified Name:     com.ufgov.workflow.engine.calendar.Duration
 * JD-Core Version:    0.5.4
 */