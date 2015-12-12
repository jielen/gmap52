package com.kingdrive.workflow.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTime {

  public DateTime() {
  }

  public static String getSysTime() {
    return (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
  }

  public static String getTime(int type, int interval) {
    String date = getSysTime();
    return getTime(date, type, interval);
  }

  public static String getTime(String date, int type, int interval) {
    SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
    Calendar calendar = Calendar.getInstance();
    ParsePosition position = new ParsePosition(0);
    calendar.setTime(formater.parse(date, position));
    calendar.add(type, interval);
    return formater.format(calendar.getTime());
  }

  public static String getLongTime(String str) {
    if (str == null)
      return null;
    if (str.length() < 8) {
      return str;
    }
    int nYear = Integer.parseInt(str.substring(0, 4), 10);
    int nMonth = Integer.parseInt(str.substring(4, 6), 10);
    int nDay = Integer.parseInt(str.substring(6, 8), 10);
    int nHour = Integer.parseInt(str.substring(8, 10), 10);
    int nMinute = Integer.parseInt(str.substring(10, 12), 10);
    int nSecond = Integer.parseInt(str.substring(12, 14), 10);
    Calendar tmpCalendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
    tmpCalendar.set(nYear, nMonth - 1, nDay, nHour, nMinute, nSecond);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date tmpDate = tmpCalendar.getTime();
    return formatter.format(tmpDate);
  }

  public static String getLongDate(String str) {
    if (str == null)
      return null;
    if (str.length() < 8) {
      return str;
    }
    int nYear = Integer.parseInt(str.substring(0, 4), 10);
    int nMonth = Integer.parseInt(str.substring(4, 6), 10);
    int nDay = Integer.parseInt(str.substring(6, 8), 10);
    int nHour = Integer.parseInt(str.substring(8, 10), 10);
    int nMinute = Integer.parseInt(str.substring(10, 12), 10);
    int nSecond = Integer.parseInt(str.substring(12, 14), 10);
    Calendar tmpCalendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
    tmpCalendar.set(nYear, nMonth - 1, nDay, nHour, nMinute, nSecond);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date tmpDate = tmpCalendar.getTime();
    return formatter.format(tmpDate);
  }

  public static String getSimpleDate(String str) {
    if (str == null)
      return null;
    if (str.length() < 8) {
      return str;
    }
    int nYear = Integer.parseInt(str.substring(0, 4), 10);
    int nMonth = Integer.parseInt(str.substring(5, 7), 10);
    int nDay = Integer.parseInt(str.substring(8, 10), 10);
    Calendar tmpCalendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
    tmpCalendar.set(nYear, nMonth - 1, nDay);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    Date tmpDate = tmpCalendar.getTime();
    return formatter.format(tmpDate);
  }

  /**
   * 
   * @param from
   *          时间格式为yyyyMMddHHmmss
   * @param now
   *          时间格式为yyyyMMddHHmmss
   * @return
   */
  public static float getLeavingHours(String limitTime) {
    SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
    Calendar calendar = Calendar.getInstance();
    ParsePosition position = new ParsePosition(0);
    calendar.setTime(formater.parse(limitTime, position));
    Date limitDate = calendar.getTime();

    return ((float) limitDate.getTime() - (new Date()).getTime())
        / (1000 * 3600);
  }

  public static void main(String args[]) {
    String date = getSysTime();
    String date1 = getTime(date, Calendar.HOUR, 24);

    System.out.println(date);
    System.out.println(date1);
  }
}
