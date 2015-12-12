package com.ufgov.workflow.engine.calendar;

import java.text.ParseException;
import java.util.Date;

public abstract interface ICalendarService
{
  public abstract Date dateAfter(Date paramDate, Duration paramDuration)
    throws ParseException;

  public abstract boolean isBusinessDay(Date paramDate)
    throws ParseException;

  public abstract Date getSysDate();
}

/* Location:           C:\Documents and Settings\Administrator\桌面\
 * Qualified Name:     com.ufgov.workflow.engine.calendar.ICalendarService
 * JD-Core Version:    0.5.4
 */