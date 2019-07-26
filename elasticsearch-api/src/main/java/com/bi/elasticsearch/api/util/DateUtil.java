package com.bi.elasticsearch.api.util;

import com.bi.elasticsearch.api.entity.HourPeriod;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {

    public static Timestamp timestamp(long time) {
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
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (StringUtils.isBlank(seconds)) {
            return null;
        }
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    /**
     * 获取秒级时间戳
     *
     * @return
     */
    public static int secondTimestamp() {
        Long l = new Date().getTime() / 1000;
        return l.intValue();
    }

    /**
     * 返回今日字符串格式日期
     *
     * @return
     */
    public static String getDateStr(String pattern, Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        String rs = sf.format(date);
        return rs;
    }

    public static Date getDateByStr(String pattern, String dateStr) throws ParseException {
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

    public static Date getOffsetDay(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        date = calendar.getTime();
        return date;
    }

    public static String getOffsetDay(Date date, int offset, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        date = calendar.getTime();
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(date);
    }

    public static String getOffsetMonth(String dateStr, int offset, String pattern) throws ParseException {
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

    public static String getOffsetDay(String dateStr, int offset, String pattern) throws ParseException {
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
     *
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

    /**
     * 获取传入时间的当天凌晨到当前时间的每个小时时间段列表
     *
     * @param date
     * @return 返回List，每个对象包含每个时间段的startTime和endTime
     */
    public static ArrayList<HourPeriod> getToNowHourPeriodList(Date date) {
        ArrayList<HourPeriod> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int now_hour = calendar.get(Calendar.HOUR_OF_DAY);

        String dayStr = getDateStr("yyyy-MM-dd", date);
        String startTime;
        String endTime;
        String periodTime;

        for (int i = 0; i <= now_hour; i++) {
            String SatrtHour = i < 10 ? " 0" + i : " " + i;
            String endHour = i + 1 < 10 ? " 0" + (i + 1) : " " + (i + 1);

            startTime = dayStr + SatrtHour + ":00:00";
            endTime = dayStr + endHour + ":00:00";
            periodTime = dayStr + " " + i + ":00";
            //当前时间段处理
            if (i == now_hour) {
                endTime = getDateStr("yyyy-MM-dd HH:mm:ss", date);
            }
            HourPeriod hourPeriod = new HourPeriod().setIndex(i).setStartTime(startTime).setEndTime(endTime)
                    .setPeriodTime(periodTime);
            list.add(hourPeriod);
        }
        return list;
    }


    public static void main(String[] args) {
        List<HourPeriod> list = getToNowHourPeriodList(new Date());
        for (HourPeriod hourPeriod : list) {
            System.out.println("index:" + hourPeriod.getIndex());
            System.out.println("startTime:" + hourPeriod.getStartTime());
            System.out.println("endTime:" + hourPeriod.getEndTime());
            System.out.println("showTime:" + hourPeriod.getPeriodTime());
            System.out.println("-------------------------");
        }
    }

}
