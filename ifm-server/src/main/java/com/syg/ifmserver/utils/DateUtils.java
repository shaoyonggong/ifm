package com.syg.ifmserver.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/5/18
 */
public class DateUtils {

    /**
     * LocalDateTime将转化为long
     *
     * @param localDateTime
     * @return
     */
    public static Long getTimestampByLocalDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * long将转化为LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime getLocalDateTimeByTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);

    }

    /**
     * 获取半点或整点时间（向后） 12:12 -->12:30  12:43 -->13:00
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime getHalfOrHourTimeAfterPattern(long timestamp) {

        LocalDateTime dateTime = getLocalDateTimeByTimestamp(timestamp);

        if (dateTime.getMinute() > 30) {

            return dateTime.plusHours(1L).minusMinutes(dateTime.getMinute()).minusSeconds(dateTime.getSecond());

        } else if (dateTime.getMinute() == 0) {

            return dateTime.minusSeconds(dateTime.getSecond());

        } else {

            return dateTime.minusMinutes(dateTime.getMinute()).plusMinutes(30L).minusSeconds(dateTime.getSecond());
        }
    }

    /**
     * 获取半点或整点时间（向前） 12:12 -->12:00  12:43 -->12:30
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime getHalfOrHourTimeBeforePattern(long timestamp) {

        LocalDateTime dateTime = getLocalDateTimeByTimestamp(timestamp);

        if (dateTime.getMinute() >= 30) {

            return dateTime.minusMinutes(dateTime.getMinute()).plusMinutes(30L).minusSeconds(dateTime.getSecond());

        } else {

            return dateTime.minusMinutes(dateTime.getMinute()).minusSeconds(dateTime.getSecond());
        }
    }

    /**
     * string类型转换为LocalDateTime
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String dateTime, String pattern) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    /**
     * string类型转换为LocalDateTime
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String dateTime) {
        return strToLocalDateTime(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTimeToString(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将时间转换为时间戳（字符串格式）
     *
     * @param format   传入时间格式
     * @param dateTime 时间字符串
     * @return
     * @throws ParseException
     */
    public static Long dateToStamp(String format, String dateTime) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

            Date date = simpleDateFormat.parse(dateTime);

            return date.getTime();
        } catch (Exception e) {
            return new Long("0");
        }
    }

    /**
     * long 转换成 YYYY-MM-dd HH:mm
     */
    public static String convertTime(Long time, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        if (time != null) {
            Date date = new Date(time);
            String format = formatter.format(date);
            return format;
        }
        return null;
    }

    /**
     * Date将转化为long
     *
     * @param date       时间
     * @param dateFormat 时间格式
     * @return 时间戳
     */
    public static Long getTimestampOfDate(Date date, String dateFormat) {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            return formatter.parse(String.valueOf(date), new ParsePosition(0)).getTime();
        }
        return null;
    }
}
