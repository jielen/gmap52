package com.ufgov.workflow.engine.calendar;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;

public class DayTime
{
  private String datePart;
  private String timePart;
  private Calendar calendar;
  private Double expressedOfDay;

  public DayTime(Calendar paramCalendar, int paramInt, Double paramDouble)
  {
    this(paramCalendar, Double.valueOf(Integer.toString(paramInt)), paramDouble);
  }

  public DayTime(Calendar paramCalendar, Double paramDouble1, Double paramDouble2)
  {
    this.expressedOfDay = paramDouble2;
    int i = getDayOfWorkday(paramDouble1);
    paramCalendar.add(5, i);
    int j = getHourOfWorkday(paramDouble1).intValue();
    paramCalendar.add(11, j);
    int k = getMinuteOfWorkDay(paramDouble1).intValue();
    paramCalendar.add(12, k);
    this.calendar = paramCalendar;
    this.datePart = DateUtil.formatDateTime(this.calendar.getTime(), "yyyy-MM-dd");
    this.timePart = DateUtil.formatDateTime(this.calendar.getTime(), "HH:mm");
  }

  public Date getDureDatetime()
  {
    return this.calendar.getTime();
  }

  public String getDateParte()
  {
    return this.datePart;
  }

  public String getTimePart()
  {
    return this.timePart;
  }

  private int getDayOfWorkday(Double paramDouble)
  {
    return (int)(paramDouble.doubleValue() / this.expressedOfDay.doubleValue());
  }

  private Double getHourOfWorkday(Double paramDouble)
  {
    int i = getDayOfWorkday(paramDouble);
    return Double.valueOf(Integer.toString(paramDouble.intValue() - i));
  }

  private Double getMinuteOfWorkDay(Double paramDouble)
  {
    int i = paramDouble.intValue();
    return Double.valueOf(Integer.toString((paramDouble.intValue() - i) * 60));
  }

  public static void main(String[] paramArrayOfString)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(new Date());
    DayTime localDayTime = new DayTime(localCalendar, new Double(32.5D), new Double(7.5D));
    System.out.println(localDayTime.getDateParte());
    System.out.println(localDayTime.getTimePart());
    System.out.println(localDayTime.getDureDatetime());
  }
}

/* Location:           C:\Documents and Settings\Administrator\桌面\
 * Qualified Name:     com.ufgov.workflow.engine.calendar.DayTime
 * JD-Core Version:    0.5.4
 */