package com.kingdrive.workflow.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Calendar;

public class StringUtil {

  public StringUtil() {
  }

  public static String replaceString(String sourceString, String oldString,
      String newString, boolean all) {
    String result = "";
    if (sourceString == null || oldString == null || newString == null)
      return null;
    result = sourceString;
    int i = 0;
    do {
      if ((i = result.indexOf(oldString, i)) == -1)
        break;
      result = String.valueOf((new StringBuffer(result.substring(0, i)))
          .append(newString).append(result.substring(i + oldString.length())));
      if (!all)
        break;
      i += newString.length();
    } while (true);
    return result;
  }

  public static String[] splitDate(String date) {
    String ret[] = new String[3];
    ret[0] = date.substring(0, 4);
    ret[1] = date.substring(4, 6);
    ret[2] = date.substring(6, 8);
    return ret;
  }

  public static String[] spliteString(String str) {
    if (str == null || str.trim().length() == 0) {
      return null;
    }

    StringTokenizer st = new StringTokenizer(str, ",");
    String result[] = new String[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++)
      result[i] = string2string(st.nextToken());

    return result;
  }

  public static String formatAmount(String amount) {
    try {
      DecimalFormat nf = new DecimalFormat();
      nf.applyPattern("#,###,###,###,###.00");
      amount = nf.format(Double.parseDouble(amount));
      if (amount.substring(0, 1).equals("."))
        amount = "0".concat(String.valueOf(String.valueOf(amount)));
    } catch (Exception e) {
      String s = "";
      return s;
    }
    return amount;
  }

  public static boolean string2boolean(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return false;
    return true;
  }

  // Convert String to long
  public static long string2long(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return 0;
    return Long.parseLong(value.trim());

  }

  // Convert String to int
  public static int string2int(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return 0;
    return Integer.parseInt(value.trim());
  }

  // Convert String to double
  public static double string2double(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return 0;
    return Double.parseDouble(value.trim());
  }

  // Convert String to java.sql.Date,String格式为yyyy-mm-dd
  public static java.sql.Date string2sqldate(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return null;
    java.util.Date d = constructDate(value);
    if (d == null)
      return null;
    return new java.sql.Date(d.getTime());
  }

  // Convert String to java.sql.Time,String格式为hh:mm:ss
  public static java.sql.Time string2sqltime(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return null;
    java.sql.Time time = null;
    try {
      time = java.sql.Time.valueOf(value);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
    if (time == null)
      return null;
    return time;
  }

  // Convert String to java.sql.Timestamp,String格式为yyyy-mm-dd hh:mm:ss
  public static java.sql.Timestamp string2sqldatetime(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return null;
    if (value.trim().length() <= 10) {
      value = value.trim() + " 00:00:00";
    }
    java.sql.Timestamp datetime = null;
    try {
      datetime = java.sql.Timestamp.valueOf(value);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
    if (datetime == null)
      return null;
    return datetime;
  }

  // 将(request)得到的字符串转换成数据库字符串形式
  public static String string2string(String value) {
    if (value == null || "null".equals(value) || value.trim().length() == 0)
      return null;
    return value.trim();
  }

  // construct java.util.Date by String,String格式为yyyy-mm-dd
  public static java.util.Date constructDate(String sDate) {
    if (sDate == null || sDate.trim().length() == 0)
      return null;
    try {
      String year = "", month = "", day = "";
      sDate = sDate.trim();
      int i = sDate.indexOf("-");
      year = sDate.substring(0, i);
      int j = sDate.indexOf("-", i + 1);
      month = sDate.substring(i + 1, j);
      day = sDate.substring(j + 1);
      year = year.trim();
      month = month.trim();
      day = day.trim();
      Calendar cal = Calendar.getInstance(java.util.Locale.getDefault());
      cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer
          .parseInt(day), 0, 0, 0);
      return cal.getTime();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String space(String data) {
    return data == null ? "" : data;
  }

  public static String space(double data) {
    return data == 0 ? "" : String.valueOf(data);
  }

  public static String space(int data) {
    return data == 0 ? "" : String.valueOf(data);
  }

  public static String space(long data) {
    return data == 0 ? "" : String.valueOf(data);
  }

  public static String space(java.util.Date data) {
    return data == null ? "" : data.toString();
  }

  public static String encode(String value) {
    String result = "";

    String seed = "$#TGDF*FAA&21we@VGXD532w23413!";
    int lenght = seed.length();
    if (value == null) {
      return null;
    }
    for (int i = 0; i < value.length(); i++) {
      result = result + (char) (value.charAt(i) ^ seed.charAt(i % lenght));
    }
    return result;
  }
  
  public static void makeDynaParam(String prop, Object value, List columnList, List valueList) {
    columnList.add(prop);
    if (value != null) {
      valueList.add(value);
    } else {
      valueList.add("null");
    }
  }
}
