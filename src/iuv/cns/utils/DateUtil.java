package iuv.cns.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {

	private DateUtil() {
	}

	public static Date check(String str, String format) throws ParseException {

		if (str == null) {
			throw new ParseException("date string to check is null", 0);
		}
		if (format == null) {
			throw new ParseException("format string to check date is null", 0);
		}

		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);

		Date date = null;

		try {
			date = formatter.parse(str);
		}

		catch (ParseException e) {
			throw new ParseException(" wrong date:\"" + str + "\" with format \"" + format + "\"", 0);
		}

		if (!formatter.format(date).equals(str)) {
			throw new ParseException("Out of bound date:\"" + str + "\" with format \"" + format + "\"", 0);
		}
		return date;
	}

	public static boolean isValid(String str) throws Exception {
		return DateUtil.isValid(str, "yyyyMMdd");
	}

	public static boolean isNotValid(String str) throws Exception {
		return !DateUtil.isValid(str);
	}

	public static boolean isValid(String str, String format) {
		/*
		 * if ( s == null ) throw new
		 * NullPointerException("date string to check is null"); if ( format ==
		 * null ) throw new
		 * NullPointerException("format string to check date is null");
		 */
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
		Date date = null;
		try {
			date = formatter.parse(str);
		} catch (ParseException e) {
			return false;
		}

		if (!formatter.format(date).equals(str)) {
			return false;
		}

		return true;
	}

	public static boolean isNotValid(String str, String format) {
		return !DateUtil.isValid(str, format);
	}

	public static String getDateString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		return formatter.format(new Date());
	}

	public static int getDay() {
		return getNumberByPattern("dd");
	}

	public static int getYear() {
		return getNumberByPattern("yyyy");
	}

	public static int getMonth() {
		return getNumberByPattern("MM");
	}

	public static int getNumberByPattern(String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
		String dateString = formatter.format(new Date());
		return Integer.parseInt(dateString);
	}

	public static String getStringByPattern(String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
		String dateString = formatter.format(new Date());
		return dateString;
	}

	public static String getFormatString(String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
		String dateString = formatter.format(new Date());
		return dateString;
	}

	public static String getShortDateString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		return formatter.format(new Date());
	}

	public static String getShortTimeString() {
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", Locale.CHINA);
		return formatter.format(new Date());
	}

	public static String getTimeStampString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS", Locale.CHINA);
		return formatter.format(new Date());
	}
	
	public static String getTimeStamp() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return formatter.format(new Date());
	}
	
	public static String getTimeStamp(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA);
		return formatter.format(new Date(time));
	}

	public static String getTimeString() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
		return formatter.format(new Date());
	}
	
	public static String getyyyyMMdd() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		return formatter.format(new Date());
	}

	public static int whichDay(String str) throws ParseException {
		return whichDay(str, "yyyyMMdd");
	}

	public static int whichDay(String str, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
		Date date = check(str, format);

		Calendar calendar = formatter.getCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int todayOfWeek() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int daysBetween(String from, String to) throws ParseException {
		return daysBetween(from, to, "yyyyMMdd");
	}

	public static int daysBetween(String from, String to, String format) throws ParseException {
		Date d1 = check(from, format);
		Date d2 = check(to, format);

		long duration = d2.getTime() - d1.getTime();

		return (int) (duration / (1000 * 60 * 60 * 24));
	}

	public static ArrayList<String> daysBetweenList(String from, String to) throws java.text.ParseException {
		int days = daysBetween(from, to);
		ArrayList<String> list = new ArrayList<String>(days + 1);

		for (int i = 0; i < days + 1; i++) {
			list.add(i, addDays(from, i));
		}

		return list;
	}


	public static int ageBetween(String from, String to) throws ParseException {
		return ageBetween(from, to, "yyyyMMdd");
	}

	public static int ageBetween(String from, String to, String format) throws ParseException {
		return daysBetween(from, to, format) / 365;
	}

	public static String addDays(String str, int day) throws ParseException {
		return addDays(str, day, "yyyyMMdd");
	}

	public static String addDays(String str, int day, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
		Date date = check(str, format);

		date.setTime(date.getTime() + ((long) day * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}

	public static String addDays(Calendar calendar, int addDay, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);

		calendar.add(Calendar.DATE, addDay);

		return formatter.format(calendar.getTime());
	}

	public static String addMonths(String str, int month) throws Exception {
		return addMonths(str, month, "yyyyMMdd");
	}

	public static String addMonths(String str, int addMonth, String format) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
		Date date = check(str, format);

		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.CHINA);
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.CHINA);
		int year = Integer.parseInt(yearFormat.format(date));
		int month = Integer.parseInt(monthFormat.format(date));
		int day = Integer.parseInt(dayFormat.format(date));

		month += addMonth;
		if (addMonth > 0) {
			while (month > 12) {
				month -= 12;
				year += 1;
			}
		} else {
			while (month <= 0) {
				month += 12;
				year -= 1;
			}
		}
		DecimalFormat fourDf = new DecimalFormat("0000");
		DecimalFormat twoDf = new DecimalFormat("00");
		String tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
				+ String.valueOf(twoDf.format(day));
		Date targetDate = null;

		try {
			targetDate = check(tempDate, "yyyyMMdd");
		} catch (ParseException pe) {
			day = lastDay(year, month);
			tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
					+ String.valueOf(twoDf.format(day));
			targetDate = check(tempDate, "yyyyMMdd");
		}

		return formatter.format(targetDate);
	}

	public static String addYears(String str, int year) throws ParseException {
		return addYears(str, year, "yyyyMMdd");
	}

	public static String addYears(String str, int year, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
		Date date = check(str, format);
		date.setTime(date.getTime() + ((long) year * 1000 * 60 * 60 * 24 * (365 + 1)));
		return formatter.format(date);
	}
	public static String lastDayOfMonth(String src, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
		Date date = check(src, format);

		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.CHINA);

		int year = Integer.parseInt(yearFormat.format(date));
		int month = Integer.parseInt(monthFormat.format(date));
		int day = lastDay(year, month);

		DecimalFormat fourDf = new DecimalFormat("0000");
		DecimalFormat twoDf = new DecimalFormat("00");
		String tempDate = String.valueOf(fourDf.format(year)) + String.valueOf(twoDf.format(month))
				+ String.valueOf(twoDf.format(day));
		date = check(tempDate, format);

		return formatter.format(date);
	}

	private static int lastDay(int year, int month) throws ParseException {
		int day = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			if ((year % 4) == 0) {
				if ((year % 100) == 0 && (year % 400) != 0) {
					day = 28;
				} else {
					day = 29;
				}
			} else {
				day = 28;
			}
			break;
		default:
			day = 30;
		}
		return day;
	}

	public static String addMinutes(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

		return format.format(cal.getTime());

	}

	public static String addSeconds(String date, int seconds) throws ParseException {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTime(check(date, "yyyyMMddHHmmss"));
		cal.add(Calendar.SECOND, seconds);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(cal.getTime());
	}

	public static String todayFromAddays(int days, String format) throws ParseException {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.add(Calendar.DATE, days);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.format(cal.getTime());
	}

	public static String getDateFormat(Date targetDate, String pattern) {
		SimpleDateFormat dateStyle = new SimpleDateFormat(pattern);
		String newDate = dateStyle.format(targetDate);
		return newDate;
	}

	public static String getDateFormat(Date targetDate, String pattern, String localeStr) {
		SimpleDateFormat dateStyle = new SimpleDateFormat(pattern, setLocale(localeStr));
		String newDate = dateStyle.format(targetDate);
		return newDate;
	}

	public static String getDateFormat(String targetDateStr, String changeToDatePattern, String changeToStringPattern,
			String localeStr) throws ParseException {

		SimpleDateFormat changeToDateFormat = new SimpleDateFormat(changeToDatePattern);
		SimpleDateFormat changeToStringFormat = new SimpleDateFormat(changeToStringPattern, setLocale(localeStr));

		Date targetDate = changeToDateFormat.parse(targetDateStr);
		String dateStr = changeToStringFormat.format(targetDate);

		return dateStr;
	}

	private static Locale setLocale(String localeStr) {
		Locale locale;

		if (localeStr.indexOf("_") >= 0) {
			locale = new Locale(localeStr.substring(0, localeStr.indexOf("_")), localeStr.substring(localeStr
					.indexOf("_") + 1));
		} else {
			locale = new Locale(localeStr);
		}

		return locale;
	}

}
