package com.elasticsearch.api.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  public static Timestamp timestamp(long time){
    try {
      if (time <= 1) {
        return null;
      }
      return new Timestamp(time);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取秒级时间戳
   * @return
     */
  public static int secondTimestamp(){
    Long l = new Date().getTime()/1000;
    return l.intValue();
  }

  /** 返回今日字符串格式日期
   * 
   * @return
   */
  public static String getDateStr(String pattern,Date date){
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    String rs = sf.format(date);
    return rs;
  }
  
  public static Date getDateByStr(String pattern,String dateStr) throws ParseException{
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    return sf.parse(dateStr);
  }
  
  public static Date getYesterDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    date = calendar.getTime();
    return date;
  }
  
  public static Date getOffsetDay(Date date,int offset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, offset);
    date = calendar.getTime();
    return date;
  }
  
  public static String getOffsetDay(Date date,int offset,String pattern) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, offset);
    date = calendar.getTime();
    SimpleDateFormat sf = new SimpleDateFormat(pattern);  
    return sf.format(date);
  }
  
  public static String getOffsetMonth(String dateStr,int offset,String pattern) throws ParseException {
    SimpleDateFormat sf = new SimpleDateFormat(pattern);  
    Date date = sf.parse(dateStr);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, offset);
    date = calendar.getTime();
    return sf.format(date);
  }

  public static String getOffsetMonth(Date date, int offset, String pattern) throws ParseException {
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    return sf.format(getOffsetMonth(date, offset));
  }

  public static Date getOffsetMonth(Date date, int offset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, offset);
    date = calendar.getTime();
    return date;
  }

  public static String getOffsetWeek(Date date, int offset, String pattern) throws ParseException {
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    return sf.format(getOffsetWeek(date, offset));
  }

  public static Date getOffsetWeek(Date date, int offset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.WEEK_OF_MONTH, offset);
    date = calendar.getTime();
    return date;
  }

  public static String getOffsetDay(String dateStr,int offset,String pattern) throws ParseException {
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    Date date = sf.parse(dateStr);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, offset);
    date = calendar.getTime();
    return sf.format(date);
  }

  public static Date getNowyyyyMMdd() {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  public static Date getNowyyyyMM() {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH, 0);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime();
  }

  /**
   * 获取指定天的指定小时
   * @param offsetDay
   * @param hour
   * @return
   */
  public static Date getSpecificHourOfDay(int offsetDay, int hour) {

    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR_OF_DAY, hour);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + offsetDay);
    return c.getTime();
  }

}
