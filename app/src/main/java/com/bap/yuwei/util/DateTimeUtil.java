package com.bap.yuwei.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_LONG_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_HOUR_MIN = "yyyy-MM-dd HH:mm";
    public static final String HOUR_MIN = "HH:mm";
    public static final String DATE_FORMAT_CHINESE = "MM月dd日";
    public static final String DATE_MONTH_FORMAT = "yyyy-MM";
    public static final String DATE_MONTH_FORMAT_CHINESE = "yyyy年MM月";
    public static final DateTimeFormatter format = DateTimeFormat.forPattern(DATE_TIME_FORMAT);


    /**
     * 获取当前时间String
     *
     * @param format
     * @return DateTimeStr
     */
    public static String getNowTimeStr(String format) {
        DateTime dt = new DateTime();
        return dt.toString(format);
    }

    /**
     * 获取当前时间String  yyyy-MM-dd HH:mm:ss
     *
     * @return DateTimeLongStr
     */
    public static String getNowTimeLongStr() {
        DateTime dt = new DateTime();
        return dt.toString(DATE_TIME_FORMAT);
    }

    /**
     * 获取当前日期String  yyyy-MM-dd
     *
     * @return DateTimeLongStr
     */
    public static String getNowDateStr() {
        DateTime dt = new DateTime();
        return dt.toString(DATE_FORMAT);
    }

    /**
     * 获取当前时间DateTime
     *
     * @return DateTime
     */
    public static DateTime getNowTime() {
        return DateTime.now();
    }

    /**
     * 获取当前日期的unix时间戳
     *
     * @return
     * @throws Exception
     */
    public static long getNowTimeMillis() {
        return DateTime.parse(getNowTimeStr(DATE_FORMAT)).getMillis();
    }

    public static String unix2String(long unixTime) {
        DateTime datetime = new DateTime(unixTime);
        return datetime.toString(DATE_TIME_FORMAT);
    }

    /**
     * 时间比较，dt1是否在dt2后面
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static boolean isAfter(DateTime dt1, DateTime dt2) {
        return dt1.isAfter(dt2);
    }

    /**
     * 计算两个时间相差的毫秒数
     *
     * @param begin
     * @param end
     * @return long
     */
    public static long getDurationMillis(DateTime begin, DateTime end) {
        return new Duration(begin, end).getMillis();
    }

    /**
     * 计算两个时间相差的秒数
     *
     * @param begin
     * @param end
     * @return long
     */
    public static long getDurationSeconds(DateTime begin, DateTime end) {
        return new Duration(begin, end).getStandardSeconds();
    }

    /**
     * 计算两个时间相差的分钟数
     *
     * @param begin
     * @param end
     * @return long
     */
    public static long getDurationMinutes(DateTime begin, DateTime end) {
        return new Duration(begin, end).getStandardMinutes();
    }

    /**
     * 计算两个时间相差的小时数
     *
     * @param begin
     * @param end
     * @return long
     */
    public static long getDurationHours(DateTime begin, DateTime end) {
        return new Duration(begin, end).getStandardHours();
    }

    /**
     * 计算两个时间相差的天数
     *
     * @param begin
     * @param end
     * @return long
     */
    public static long getDurationDays(DateTime begin, DateTime end) {
        return new Duration(begin, end).getStandardDays();
    }

    /**
     * dt减去X分钟后的日期
     *
     * @param dt
     * @return
     */
    public static DateTime minusMins(DateTime dt, int minusMins) {
        return dt.minusMinutes(minusMins);
    }

    /**
     * dt加上X分钟后的日期
     *
     * @param dt
     * @return
     */
    public static DateTime plusMins(DateTime dt, int plusMins) {
        return dt.plusMinutes(plusMins);
    }

    /**
     * dt减去X天后的日期
     *
     * @param dt
     * @return
     */
    public static DateTime minusDays(DateTime dt, int minusDays) {
        return dt.minusDays(minusDays);
    }

    /**
     * dt加上X天后的日期
     *
     * @param dt
     * @return
     */
    public static DateTime plusDays(DateTime dt, int plusDays) {
        return dt.minusDays(plusDays);
    }

    /**
     * dt减去X月后的日期
     *
     * @param dt
     * @return
     */
    public static DateTime minusMonths(DateTime dt, int minusMonths) {
        return dt.minusMonths(minusMonths);
    }

    /**
     * dt加上X月后的日期
     *
     * @param dt
     * @return
     */
    public static DateTime plusMonths(DateTime dt, int plusMonths) {
        return dt.plusMonths(plusMonths);
    }

    /**
     * 字符串转时间
     *
     * @return
     */
    public static DateTime parse2Time(String time) {
        return DateTime.parse(time, format);
    }

    /**
     * 字符串转时间
     *
     * @return
     */
    public static DateTime parse2Time(String time,String format) {
        return DateTime.parse(time, DateTimeFormat.forPattern(format));
    }

    /**
     * 获取时间对应的是星期几
     *
     * @param dt
     * @return
     */
    public static String getDayOfWeek(DateTime dt) {
        String dayOfWeek = null;
        switch (dt.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "星期日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "星期一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "星期二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "星期三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "星期四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "星期五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "星期六";
                break;
        }
        return dayOfWeek;
    }


    /***************************************
     * 转中文时间
     ***************************************************************/

    private final static String[] arrayStrs = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    /**
     * 年月日转成中文时间
     * e.g 2015-11-24 ==> 二零一五年十一月二十四日
     *
     * @return
     */
    public static String date2ChineseStr(String date) {
        StringBuilder sb = new StringBuilder();
        //分割年月日
        String[] ss = date.split("-");

        //年份
        String yearStr = ss[0];
        //获取年份上每一位的数字
        for (int i = 0; i < yearStr.length(); i++) {
            int n = Integer.valueOf(yearStr.substring(i, i + 1));
            sb.append(arrayStrs[Integer.valueOf(n)]);
        }
        sb.append("年");

        //月份
        String monthStr = ss[1];
        int month = Integer.valueOf(monthStr);
        if (month > 10) {
            sb.append("十");
            //取个位数
            sb.append(arrayStrs[month % 10]);
        } else {
            sb.append(arrayStrs[month]);
        }
        sb.append("月");

        //日
        String dayStr = ss[2];
        int day = Integer.valueOf(dayStr);
        if (day > 9) {
            //取十位数
            sb.append(arrayStrs[day / 10]);
            sb.append("十");
            //取个位数
            int lastDay = day % 10;
            if (lastDay != 0) {
                //个位数不为0时
                sb.append(arrayStrs[lastDay]);
            }
        } else {
            sb.append(arrayStrs[day]);
        }
        sb.append("日");
        return sb.toString();
    }


    /**
     * string to Date
     *
     * @param date
     * @param formatPattern //时间格式
     * @return
     */
    public static Date string2Date(String date, String formatPattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format2 = new SimpleDateFormat(formatPattern);
        try {
            return format2.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * date to String
     *
     * @param date
     * @param formatPattern //时间格式
     * @return
     */
    public static String date2String(Date date, String formatPattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format2 = new SimpleDateFormat(formatPattern);
        return format2.format(date);


    }


	public static String getChineseTimeLevel(){
		String timeLevel=null;
		int hourOfDay=DateTime.now().getHourOfDay();
		if(hourOfDay>=0 && hourOfDay<=8){
			timeLevel="早上";
		}else if(hourOfDay>8 && hourOfDay<=12){
			timeLevel="上午";
		}else if(hourOfDay>12 && hourOfDay<=18){
			timeLevel="下午";
		}else{
			timeLevel="晚上";
		}
		return timeLevel;

	}
}

