/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	private static String[] parsePatterns = {
		"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*60*1000);
	}
	
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*1000);
	}
	
	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
    public static String formatDateTime(long timeMillis){
		long day = timeMillis/(24*60*60*1000);
		long hour = (timeMillis/(60*60*1000)-day*24);
		long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
		long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
		long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
		return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }
	
	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	public static double getDistanceOfTwoDate1(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000);
	}

	/**
	 * 获取传入时间是周几
	 *
	 * @param today
	 * @return
	 */
	public static int getWeekNum(Date today) {
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		int weekday = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (0 == weekday) {
			weekday = 7;
		}
		return weekday;
	}

	/**
	 * 取得同一天 某时间段内半小时和整点数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getHeafHourTimeList(Date startTime, Date endTime) {
		if(!startTime.before(endTime)){
			return null;//开始时间在结束时间之前 否则返回null
		}
		if(!(DateUtils.formatDate(startTime, "yyyy").equals(DateUtils.formatDate(endTime, "yyyy"))) ||
				!(DateUtils.formatDate(startTime, "MM").equals(DateUtils.formatDate(endTime, "MM"))) ||
				!(DateUtils.formatDate(startTime, "dd").equals(DateUtils.formatDate(endTime, "dd")))){
			return null;//开始时间和结束时间是同一天 否则返回null
		}

		if(!DateUtils.addMinutes(startTime,30).before(endTime)){
			return null;//开始时间和结束时间之间相隔不到30分钟
		}

		Date heafHourTime = DateUtils.parseDate(DateUtils.formatDate(startTime, "yyyy") + "-" +
				DateUtils.formatDate(startTime, "MM") + "-" +
				DateUtils.formatDate(startTime, "dd") + " " +
				DateUtils.formatDate(startTime, "HH") + ":30:00");
		if(!startTime.before(heafHourTime)){
			heafHourTime = DateUtils.parseDate(DateUtils.formatDate(startTime, "yyyy") + "-" +
					DateUtils.formatDate(startTime, "MM") + "-" +
					DateUtils.formatDate(startTime, "dd") + " " +
					(Integer.parseInt(DateUtils.formatDate(startTime, "HH")) + 1) + ":00:00");
		}
		List<String> heafHourTimeList = new ArrayList<String>();
		heafHourTimeList.add(DateUtils.formatDate(heafHourTime, "HH") + ":" + DateUtils.formatDate(heafHourTime, "mm"));
		for (int i = 0; i < 48; i++) {

			heafHourTime = DateUtils.addMinutes(heafHourTime,30);
			if(endTime.after(heafHourTime)){
				heafHourTimeList.add(DateUtils.formatDate(heafHourTime, "HH") + ":" + DateUtils.formatDate(heafHourTime, "mm"));
				continue;
			}
			break;
		}
		return heafHourTimeList;
	}
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
//		System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
//		System.out.println(time/(24*60*60*1000));
		System.out.println(getDistanceOfTwoDate1(parseDate("2010-3-6 8:01:00"),parseDate("2010-3-6 8:01:00")));

		String week = DateUtils.formatDate(parseDate("2018-1-3 8:01:00"),"E");
		System.out.println(week);
	}
}
