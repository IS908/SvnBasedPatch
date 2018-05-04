package com.dcits.patchtools.svn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2017-11-07 09:33.
 *
 * @author kevin
 */
public class DateUtil {
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 将时间转化为时间戳
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static String dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        String res = String.valueOf(ts);
        return res;
    }

    /**
     * 获得一天的起始时间的时间戳
     *
     * @return
     */
    public static long getDayBeginTimestamp(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage() + " : " + e.getCause());
        }
        long ts = date.getTime();
        return ts;
    }

    public static long getDayBeginTimestamp(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long timestamp = 0;
        try {
            timestamp = simpleDateFormat.parse(simpleDateFormat.format(date)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp / 1000 - (7 * 60 * 60);
    }

    /**
     * 将时间戳转化为时间
     *
     * @param s
     * @return
     */
    public static String timestamp2Date(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.getLong(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String timestamp2Date(long stamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(stamp);
        return simpleDateFormat.format(date);
    }

    /**
     * @return date
     * @authot qiqsa
     * @desc 生成当前日期 yyMMdd
     */
    public static String getRunDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        long time = date.getTime();
        return simpleDateFormat.format(time);
    }
}
