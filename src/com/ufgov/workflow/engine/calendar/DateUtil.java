package com.ufgov.workflow.engine.calendar;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil
{
  public static String getDate()
  {
    return getDate(new Date(System.currentTimeMillis()));
  }

  public static String getDate(Date paramDate)
  {
    return formatDateTime(paramDate, "yyyy-MM-dd");
  }

  public static String getTime()
  {
    return getTime(new Date(System.currentTimeMillis()));
  }

  public static String getTime(Date paramDate)
  {
    return formatDateTime(paramDate, "yyyy-MM-dd HH:mm:ss");
  }

  public static int getYear()
  {
    return getYear(new Date(System.currentTimeMillis()));
  }

  public static int getYear(Date paramDate)
  {
    return getCalendarField(paramDate, 1);
  }

  public static int getMonth()
  {
    return getYear(new Date(System.currentTimeMillis()));
  }

  public static int getMonth(Date paramDate)
  {
    return getCalendarField(paramDate, 2);
  }

  public static int getCalendarField(Date paramDate, int paramInt)
  {
    Calendar localCalendar = GregorianCalendar.getInstance();
    localCalendar.setTime(paramDate);
    return localCalendar.get(paramInt);
  }

  public static Date parseTime(String paramString)
    throws ParseException
  {
    if (paramString.indexOf("-") > 0)
      return parseDate(paramString, "yyyy-MM-dd HH:mm:ss");
    return parseDate(paramString, "yyyyMMddHHmmss");
  }

  public static Date parseHourTime(String paramString)
    throws ParseException
  {
    if (paramString.indexOf("-") > 0)
      return parseDate(paramString, "yyyy-MM-dd HH:mm");
    return parseDate(paramString, "yyyyMMddHHmmss");
  }

  public static Date parseDate(String paramString1, String paramString2)
    throws ParseException
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString2);
    return (Date)localSimpleDateFormat.parseObject(paramString1);
  }

  public static String formatDateTime(Object paramObject, String paramString)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString);
    return localSimpleDateFormat.format(paramObject);
  }

  public static void main(String[] paramArrayOfString)
  {
    try
    {
      Date localDate1 = parseHourTime("20100129233658");
      Date localDate2 = parseHourTime("2009-10-09 09:26");
      System.out.println(localDate1.compareTo(localDate2));
    }
    catch (ParseException localParseException)
    {
      localParseException.printStackTrace();
    }
  }
}

/* Location:           C:\Documents and Settings\Administrator\桌面\
 * Qualified Name:     com.ufgov.workflow.engine.calendar.DateUtil
 * JD-Core Version:    0.5.4
 */