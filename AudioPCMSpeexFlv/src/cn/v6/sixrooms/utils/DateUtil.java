package cn.v6.sixrooms.utils;

import java.util.*;
import java.text.*;
import java.util.Calendar;

public class DateUtil {

	public static String getStringDateChinese() {
		String current = getStringDate();
		String date = current.substring(0, 10);
		String chineseDate = date.substring(0, 4) + "年" + date.substring(5, 7)
				+ "月" + date.substring(8, 10) + "日";
		return chineseDate;
	}

	public static String getHourDe() {
		String hour = getHour();
		if (hour.length() == 2) {
			return hour.substring(0, 1);
		} else {
			return "0";
		}
	}

	public static String getHourUnit() {
		String hour = getHour();
		if (hour.length() == 2) {
			return hour.substring(1, 2);
		} else {
			return hour;
		}
	}

	public static String getMinuteDe() {
		String minute = getTime();
		if (minute.length() == 2) {
			return minute.substring(0, 1);
		} else {
			return "0";
		}
	}

	public static String getMinuteUnit() {
		String minute = getTime();
		if (minute.length() == 2) {
			return minute.substring(1, 2);
		}
		return minute;

	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 MM-dd HH:mm:ss
	 */
	public static String getStringDates() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateDate
	 * @return
	 */
	public static String dateToStrLong(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式时间转换为字符串 yyyy-MM-dd
	 * 
	 * @param dateDate
	 * @param k
	 * @return
	 */
	public static String dateToStr(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		Date currentTime = new Date();
		return currentTime;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return 字符串 yyyyMMdd HHmmss
	 */
	public static String getStringToday() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到现在小时
	 */
	public static String getHour() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String hour;
		hour = dateString.substring(11, 13);
		return hour;
	}

	/**
	 * 得到当前的消失分钟
	 */
	public static String getHourMinuteCurr() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String hour;
		hour = dateString.substring(11, 16);
		return hour;
	}

	/**
	 * 得到现在小时、分钟
	 */
	public static String getHourTime(String timeMs) {
		long longTime = Long.parseLong(timeMs);
		Date currentTime = new Date(longTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = formatter.format(currentTime);
		String hourTime;
		hourTime = dateString.substring(11);
		return hourTime;
	}

	public static String time2str(String timestampString) {
		long nowTime = Long.parseLong(timestampString) * 1000;
		DateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(nowTime);
		String format = formatter.format(calendar.getTime());
		return format;
	}

	public static String TimeStamp2Date(String timestampString) {
		/*
		 * Date date = new Date(timestampString); String strs = ""; try {
		 * SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm:ss"); strs =
		 * sdf.format(date); } catch (Exception e) { e.printStackTrace(); }
		 */
		return timestampString;
	}

	/**
	 * 得到现在分钟
	 * 
	 * @return
	 */
	public static String getTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String min;
		min = dateString.substring(14, 16);
		return min;
	}

	/**
	 * 根据一个日期，返回是星期几的字符串
	 * 
	 * @param sdate
	 * @return
	 */
	public static String getWeek(String sdate) {
		// 再转换为时间
		Date date = DateUtil.strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	public static String getWeekStr(String sdate) {
		String str = "";
		str = DateUtil.getWeek(sdate);
		if ("1".equals(str)) {
			str = "星期日";
		} else if ("2".equals(str)) {
			str = "星期一";
		} else if ("3".equals(str)) {
			str = "星期二";
		} else if ("4".equals(str)) {
			str = "星期三";
		} else if ("5".equals(str)) {
			str = "星期四";
		} else if ("6".equals(str)) {
			str = "星期五";
		} else if ("7".equals(str)) {
			str = "星期六";
		}
		return str;
	}

	/**
	 * 返回今天是星期
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String dayForWeek() throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}

		String str = null;
		if (7 == dayForWeek) {
			str = "星期日";
		} else if (1 == dayForWeek) {
			str = "星期一";
		} else if (2 == dayForWeek) {
			str = "星期二";
		} else if (3 == dayForWeek) {
			str = "星期三";
		} else if (4 == dayForWeek) {
			str = "星期四";
		} else if (5 == dayForWeek) {
			str = "星期五";
		} else if (6 == dayForWeek) {
			str = "星期六";
		}

		return str;
	}
	
	
	/**
	 * 判断是纳秒还是毫秒
	 * @author liuyue
	 * @2015-2-9 下午8:31:12
	 * @param milliseconds
	 * @return
	 */
	private static long getNanoSeconds(long milliseconds){
		
		if(milliseconds > 9999999999L){
			return milliseconds;
		}else {
			return milliseconds*1000L;
		}
	}
	/**
	 * 返回时间信息
	 * @author liuyue
	 * @2015-2-9 下午7:55:32
	 * @param milliseconds  毫秒的时间戳
	 * @return 今天返回 小时:分钟; 昨天返回"昨天"; 以前返回 月份:日期
	 */
	public static String getTimeInfo(long milliseconds) {
		long nanoSeconds = getNanoSeconds(milliseconds);

		Calendar today = Calendar.getInstance();

		Calendar yesterday = Calendar.getInstance(); // 昨天
		yesterday.set(Calendar.YEAR, today.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, today.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		
		Calendar target = Calendar.getInstance();
		target.setTimeInMillis(nanoSeconds);
		
		
		if(target.after(today)){
			return timeeeee(nanoSeconds).substring(14, 19);
		}else if(target.before(today) && target.after(yesterday)){
			
			return "昨天 ";
		}else{
			return timeeeee(nanoSeconds).substring(5, 10);
		}
	}
	/**
	 * 根据纳秒时间转换成文本时间
	 * @author liuyue
	 * @2015-2-9 下午8:31:35
	 * @param nanoSeconds
	 * @return
	 */
	private static String timeeeee(long nanoSeconds) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date(nanoSeconds));
		return date;
	}

}