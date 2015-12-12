package com.ufgov.workflow.engine.calendar;

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultCalendarService
  implements ICalendarService
{
  private Properties businessCalendarProperties = new Properties();

  public DefaultCalendarService()
  {
    try
    {
      this.businessCalendarProperties.load(DefaultCalendarService.class.getResourceAsStream("calendarConfig.properties"));
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public Date dateAfter(String paramString, Double paramDouble)
    throws ParseException
  {
    if (paramString.indexOf("-") > 0)
      return dateAfter(DateUtil.parseDate(paramString, "yyyy-MM-dd HH:mm"), new Duration(paramDouble.intValue() * 60, "MINUTE"));
    return dateAfter(DateUtil.parseDate(paramString, "yyyyMMddHHmmss"), new Duration(paramDouble.intValue() * 60, "MINUTE"));
  }

  public Date dateAfter(Date paramDate, Duration paramDuration)
    throws ParseException
  {
    if (((paramDuration.getUnit().equals("SECOND")) && (!paramDuration.isBusinessTime())) || ((paramDuration.getUnit().equals("MINUTE")) && (!paramDuration.isBusinessTime())) || (paramDuration.getUnit().equals("WEEK")) || (paramDuration.getUnit().equals("MONTH")) || (paramDuration.getUnit().equals("YEAR")) || ((paramDuration.getUnit().equals("DAY")) && (!paramDuration.isBusinessTime())) || ((paramDuration.getUnit().equals("HOUR")) && (!paramDuration.isBusinessTime())))
    {
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTime(paramDate);
      if (paramDuration.getUnit().equals("MONTH"))
        localCalendar.add(2, paramDuration.getValue());
      else if (paramDuration.getUnit().equals("YEAR"))
        localCalendar.add(1, paramDuration.getValue());
      else if (paramDuration.getUnit().equals("DAY"))
        localCalendar.add(5, paramDuration.getValue());
      else if (paramDuration.getUnit().equals("HOUR"))
        localCalendar.add(10, paramDuration.getValue());
      else if (paramDuration.getUnit().equals("MINUTE"))
        localCalendar.add(12, paramDuration.getValue());
      else if (paramDuration.getUnit().equals("SECOND"))
        localCalendar.add(13, paramDuration.getValue());
      else if (paramDuration.getUnit().equals("WEEK"))
        localCalendar.add(5, paramDuration.getValue() * 7);
      return localCalendar.getTime();
    }
    if ((paramDuration.getUnit().equals("DAY")) && (paramDuration.isBusinessTime()))
    {
      float f = Float.parseFloat(getBusinessCalendarProperties().getProperty("business.day.expressed.in.hours"));
      int j = (int)(paramDuration.getValue() * f * 60.0F * 60.0F * 1000.0F);
      return businessDateAfter(paramDate, j);
    }
    int i;
    if ((paramDuration.getUnit().equals("HOUR")) && (paramDuration.isBusinessTime()))
    {
      i = paramDuration.getValue() * 60 * 60 * 1000;
      return businessDateAfter(paramDate, i);
    }
    if ((paramDuration.getUnit().equals("MINUTE")) && (paramDuration.isBusinessTime()))
    {
      i = paramDuration.getValue() * 60 * 1000;
      return businessDateAfter(paramDate, i);
    }
    if ((paramDuration.getUnit().equals("SECOND")) && (paramDuration.isBusinessTime()))
    {
      i = paramDuration.getValue() * 1000;
      return businessDateAfter(paramDate, i);
    }
    return null;
  }

  protected Date businessDateAfter(Date paramDate, int paramInt)
    throws ParseException
  {
    int i = paramInt;
    int j = 0;
    Date localDate = paramDate;
    int k = getBusinessHourPart(localDate);
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty("hour.format"));
    int l = 0;
    int i1 = 0;
    Object localObject1;
    Object localObject2;
    int i2;
    Object localObject3;
    Calendar localCalendar = null;
    while (i > 0)
    {
      if (i1 == 0)
        try
        {
          if (k != 1)
            j = getReleaseWorkingTime(localDate);
          i1 = 1;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      else
        j = getTotalWorkingTime(localDate);
      i -= j;
      if (i <= 0)
        continue;
      localObject1 = Calendar.getInstance();
      ((Calendar)localObject1).setTime(localDate);
      if (k == 1)
        ((Calendar)localObject1).add(5, 1);
      while (!isBusinessDay(DateUtil.parseDate(DateUtil.getDate(((Calendar)localObject1).getTime()), this.businessCalendarProperties.getProperty("day.format"))))
        ((Calendar)localObject1).add(5, 1);
      l = 1;
      localObject2 = getBusinessTime(((Calendar)localObject1).getTime());
      i2 = ((String)localObject2).indexOf("-");
      localObject3 = null;
      try
      {
        localObject3 = localSimpleDateFormat.parse(((String)localObject2).substring(0, i2));
      }
      catch (ParseException localParseException)
      {
        Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, localParseException);
      }
      localCalendar = Calendar.getInstance();
      localCalendar.setTime((Date)localObject3);
      ((Calendar)localObject1).set(11, localCalendar.get(11));
      ((Calendar)localObject1).set(12, localCalendar.get(12));
      ((Calendar)localObject1).set(13, localCalendar.get(13));
      ((Calendar)localObject1).set(14, localCalendar.get(14));
      localDate = ((Calendar)localObject1).getTime();
    }
    if (i <= 0)
    {
      i += j;
      localObject1 = getBusinessTime(localDate);
      if (localObject1 != null)
      {
        localObject2 = new StringTokenizer((String)localObject1, "&");
        i2 = 0;
        while (true)
        {
          if (!((StringTokenizer)localObject2).hasMoreTokens())
            break;
          localObject3 = ((StringTokenizer)localObject2).nextToken().trim();
          localCalendar = Calendar.getInstance();
          localCalendar.setTime(localDate);
          int i3 = testTimeInTheTimeSpan(localDate, (String)localObject3);
          int i5;
          if ((l != 0) || (i3 == -1))
          {
            int i4 = ((String)localObject3).indexOf(":");
            i5 = ((String)localObject3).indexOf("-");
            int i6 = Integer.parseInt(((String)localObject3).substring(0, i4));
            int i7 = Integer.parseInt(((String)localObject3).substring(i4 + 1, i5));
            localCalendar.set(11, i6);
            localCalendar.set(12, i7);
            localDate = localCalendar.getTime();
          }
          else
          {
            if (i3 == 1)
              continue;
            if (i3 == 0)
            {
              String str = localSimpleDateFormat.format(localDate);
              i5 = ((String)localObject3).indexOf("-");
              localObject3 = str + ((String)localObject3).substring(i5);
            }
          }
          i2 = getTotalWorkingTime((String)localObject3);
          if (i2 >= i)
            break;
          i -= i2;
        }
        localCalendar.add(14, i);
        localDate = localCalendar.getTime();
      }
    }
    return (Date)(Date)(Date)localDate;
  }

  public int getTotalWorkingTime(Date paramDate)
    throws ParseException
  {
    if (!isBusinessDay(paramDate))
      return 0;
    String str1 = getBusinessTime(paramDate);
    if (str1 == null)
      return 0;
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty("hour.format"));
    String str2 = localSimpleDateFormat.format(paramDate);
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, "&");
    ArrayList localArrayList = new ArrayList();
    String str4;
    while (localStringTokenizer.hasMoreTokens())
    {
      String str3 = localStringTokenizer.nextToken();
      int j = testTimeInTheTimeSpan(paramDate, str3);
      if (j == -1)
        localArrayList.add(str3);
      if (j != 0)
        continue;
      str4 = str2 + str3.substring(str3.indexOf("-"));
      localArrayList.add(str4);
    }
    int i = 0;
    for (int j = 0; j < localArrayList.size(); ++j)
    {
      str4 = (String)localArrayList.get(j);
      i += getTotalWorkingTime(str4);
    }
    return i;
  }

  private int testTimeInTheTimeSpan(Date paramDate, String paramString)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty("day.format"));
    String str = localSimpleDateFormat.format(paramDate);
    localSimpleDateFormat.applyPattern(this.businessCalendarProperties.getProperty("day.format") + " " + this.businessCalendarProperties.getProperty("hour.format"));
    if (paramString.indexOf("&") > 0)
    {
      String[] arrayOfString = paramString.split("&");
      int i = processTimeSpan(paramDate, arrayOfString[0], localSimpleDateFormat, str);
      if (i == 0)
        return 0;
      if (i == 1)
      {
        int j = processTimeSpan(paramDate, arrayOfString[1], localSimpleDateFormat, str);
        if (j == 0)
          return 0;
        if (j == 1)
          return 1;
        return -1;
      }
      return -1;
    }
    return processTimeSpan(paramDate, paramString, localSimpleDateFormat, str);
  }

  private int processTimeSpan(Date paramDate, String paramString1, SimpleDateFormat paramSimpleDateFormat, String paramString2)
  {
    int i = paramString1.indexOf("-");
    String str1 = paramString2 + " " + paramString1.substring(0, i);
    String str2 = paramString2 + " " + paramString1.substring(i + 1);
    Date localDate1 = null;
    Date localDate2 = null;
    try
    {
      localDate2 = paramSimpleDateFormat.parse(str2);
    }
    catch (ParseException localParseException1)
    {
      Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, localParseException1);
    }
    try
    {
      localDate1 = paramSimpleDateFormat.parse(str1);
    }
    catch (ParseException localParseException2)
    {
      Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, localParseException2);
    }
    if (paramDate.before(localDate1))
      return -1;
    if (paramDate.after(localDate2))
      return 1;
    return 0;
  }

  private String getBusinessTime(Date paramDate)
  {
    Object localObject = this.businessCalendarProperties.getProperty("business.time");
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(paramDate);
    String str = null;
    int i = localCalendar.get(7);
    switch (i)
    {
    case 1:
      str = this.businessCalendarProperties.getProperty("business.week.sunday");
      break;
    case 2:
      str = this.businessCalendarProperties.getProperty("business.week.monday");
      break;
    case 3:
      str = this.businessCalendarProperties.getProperty("business.week.tuesday");
      break;
    case 4:
      str = this.businessCalendarProperties.getProperty("business.week.wednesday");
      break;
    case 5:
      str = this.businessCalendarProperties.getProperty("business.week.thursday");
      break;
    case 6:
      str = this.businessCalendarProperties.getProperty("business.week.friday");
      break;
    case 7:
      str = this.businessCalendarProperties.getProperty("business.week.saturday");
      break;
    default:
      str = this.businessCalendarProperties.getProperty("business.week.defaultday");
    }
    List localList = extractWorkdayOfYear();
    try
    {
      Date localDate = DateUtil.parseDate(DateUtil.getDate(paramDate), this.businessCalendarProperties.getProperty("day.format"));
      if (localList.contains(localDate))
        str = this.businessCalendarProperties.getProperty("business.week.defaultday");
    }
    catch (ParseException localParseException)
    {
      localParseException.printStackTrace();
    }
    if (str != null)
      localObject = str;
    return (String)localObject;
  }

  private int getTotalWorkingTime(String paramString)
  {
    if (paramString == null)
      return 0;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "-");
    String str1 = null;
    String str2 = null;
    if (localStringTokenizer.hasMoreTokens())
      str1 = localStringTokenizer.nextToken();
    if (localStringTokenizer.hasMoreTokens())
      str2 = localStringTokenizer.nextToken();
    if ((str1 == null) || (str2 == null))
      return 0;
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty("hour.format"));
    Date localDate1 = null;
    try
    {
      localDate1 = localSimpleDateFormat.parse(str1);
    }
    catch (ParseException localParseException1)
    {
      Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, localParseException1);
    }
    Date localDate2 = null;
    try
    {
      localDate2 = localSimpleDateFormat.parse(str2);
    }
    catch (ParseException localParseException2)
    {
      Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, localParseException2);
    }
    if ((localDate1 == null) || (localDate2 == null))
      return 0;
    return (int)(localDate2.getTime() - localDate1.getTime());
  }

  private int getReleaseWorkingTime(Date paramDate)
    throws ParseException, Exception
  {
    if (!isBusinessDay(paramDate))
      return 0;
    String str1 = getBusinessTime(paramDate);
    String str2 = DateUtil.getDate(paramDate);
    if (str1 != null)
    {
      String[] arrayOfString1 = str1.split("&");
      String[] arrayOfString2 = arrayOfString1[0].trim().split("-");
      String str3 = str2 + " " + arrayOfString2[0].trim();
      String str4 = str2 + " " + arrayOfString2[1].trim();
      String[] arrayOfString3 = arrayOfString1[1].trim().split("-");
      String str5 = str2 + " " + arrayOfString3[0].trim();
      String str6 = str2 + " " + arrayOfString3[1].trim();
      Date localDate1 = DateUtil.parseHourTime(str3);
      Date localDate2 = DateUtil.parseHourTime(str4);
      Date localDate3 = DateUtil.parseHourTime(str5);
      Date localDate4 = DateUtil.parseHourTime(str6);
      int i = (int)(paramDate.getTime() - localDate1.getTime());
      int j = (int)(localDate2.getTime() - paramDate.getTime());
      int k = (int)(paramDate.getTime() - localDate3.getTime());
      int l = (int)(localDate4.getTime() - paramDate.getTime());
      int i1 = (int)(localDate2.getTime() - localDate1.getTime() + (localDate4.getTime() - localDate3.getTime()));
      if ((i > 0) && (j > 0))
        return i1 - i;
      if ((k > 0) && (l > 0))
        return i1 - (int)(localDate2.getTime() - localDate2.getTime() + k);
      if ((j < 0) && (k < 0))
        return i1 - (int)(localDate2.getTime() - localDate2.getTime());
      if (l < 0)
        return (int)(localDate2.getTime() - localDate2.getTime() + (localDate4.getTime() - localDate3.getTime()));
    }
    return 0;
  }

  public boolean isBusinessDay(Date paramDate)
    throws ParseException
  {
    if (paramDate == null)
      return false;
    List localList1 = holidayDateofYear();
    if (localList1.contains(DateUtil.parseDate(DateUtil.getDate(paramDate), this.businessCalendarProperties.getProperty("day.format"))))
      return false;
    List localList2 = extractWorkdayOfYear();
    if (localList2.contains(DateUtil.parseDate(DateUtil.getDate(paramDate), this.businessCalendarProperties.getProperty("day.format"))))
      return true;
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTime(paramDate);
    int i = localCalendar.get(7);
    return (i != 1) && (i != 7);
  }

  public int getBusinessHourPart(Date paramDate)
  {
    int i = 0;
    String str1 = getBusinessTime(paramDate);
    String str2 = DateUtil.getDate(paramDate);
    if ((str1 != null) && (str1.length() > 0))
    {
      String[] arrayOfString1 = str1.split("&");
      String[] arrayOfString2 = arrayOfString1[0].trim().split("-");
      String str3 = str2 + " " + arrayOfString2[0].trim();
      String[] arrayOfString3 = arrayOfString1[1].trim().split("-");
      String str4 = str2 + " " + arrayOfString3[1].trim();
      try
      {
        Date localDate1 = DateUtil.parseHourTime(str3);
        Date localDate2 = DateUtil.parseHourTime(str4);
        if (paramDate.compareTo(localDate1) < 0)
          i = -1;
        if (paramDate.compareTo(localDate2) > 0)
          i = 1;
      }
      catch (ParseException localParseException)
      {
        localParseException.printStackTrace();
      }
    }
    return i;
  }

  public List extractWorkdayOfYear()
  {
    ArrayList localArrayList = new ArrayList();
    Enumeration localEnumeration = this.businessCalendarProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = localEnumeration.nextElement().toString();
      if (!str1.toLowerCase().startsWith("business.day.expressed.workday"))
        continue;
      String str2 = this.businessCalendarProperties.getProperty(str1);
      try
      {
        localArrayList.add(DateUtil.parseDate(str2, this.businessCalendarProperties.getProperty("day.format")));
      }
      catch (ParseException localParseException)
      {
        localParseException.printStackTrace();
      }
    }
    return localArrayList;
  }

  public List holidayDateofYear()
  {
    ArrayList localArrayList = new ArrayList();
    Calendar localCalendar = Calendar.getInstance();
    Enumeration localEnumeration = this.businessCalendarProperties.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str1 = localEnumeration.nextElement().toString();
      if (!str1.toLowerCase().startsWith("holiday"))
        continue;
      String str2 = this.businessCalendarProperties.getProperty(str1);
      String[] arrayOfString = str2.split("&");
      try
      {
        if (arrayOfString.length == 1)
        {
          localArrayList.add(DateUtil.parseDate(arrayOfString[0].trim(), this.businessCalendarProperties.getProperty("day.format")));
        }
        else
        {
          Date localDate1 = DateUtil.parseDate(arrayOfString[0].trim(), this.businessCalendarProperties.getProperty("day.format"));
          Date localDate2 = DateUtil.parseDate(arrayOfString[1].trim(), this.businessCalendarProperties.getProperty("day.format"));
          localCalendar.setTime(localDate1);
          while (localCalendar.getTimeInMillis() <= localDate2.getTime())
          {
            localArrayList.add(localCalendar.getTime());
            localCalendar.add(6, 1);
          }
        }
      }
      catch (ParseException localParseException)
      {
        localParseException.printStackTrace();
      }
    }
    return localArrayList;
  }

  public Properties getBusinessCalendarProperties()
  {
    return this.businessCalendarProperties;
  }

  public void setBusinessCalendarProperties(Properties paramProperties)
  {
    this.businessCalendarProperties.putAll(paramProperties);
  }

  public Date getSysDate()
  {
    return new Date();
  }

  public static void main(String[] paramArrayOfString)
    throws ParseException
  {
    DefaultCalendarService localDefaultCalendarService = new DefaultCalendarService();
    System.out.println(DateUtil.parseHourTime("20101202202607"));
    System.out.println(localDefaultCalendarService.dateAfter(DateUtil.parseDate("20101202202607", "yyyyMMddHHmmss"), new Duration(300, "MINUTE")));
  }
}

/* Location:           C:\Documents and Settings\Administrator\桌面\
 * Qualified Name:     com.ufgov.workflow.engine.calendar.DefaultCalendarService
 * JD-Core Version:    0.5.4
 */