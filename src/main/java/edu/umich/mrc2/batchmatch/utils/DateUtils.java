////////////////////////////////////////////////////////////////////
//DateUtils.java
//Written by Jan Wigginton 
////////////////////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
	public static Date todaysDate() {
		Date d = new Date();
		return d;
	}

	public static String dateAsString(Date date) {
		return dateAsString(date, "MM/dd/yyyy");
	}

	public static String dateAsString(Date date, String format) {
		String dateStr;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			dateStr = df.format(date);
		} catch (IllegalArgumentException e) {
			return "Error";
		} catch (NullPointerException e) {
			return "Error";
		}

		return dateStr;
	}

	public static String todaysDateAsString(String format) {
		String dateStr;
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);

			Date dt = todaysDate();
			dateStr = df.format(dt);
		} catch (IllegalArgumentException e) {
			return "Error";
		} catch (NullPointerException e) {
			return "Error";
		}
		return dateStr;
	}

	public static String dateStrFromCalendar(String format, Calendar cal) {
		if (cal == null)
			return "";

		if (format.trim().equals(""))
			return cal.getTime().toString();

		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date = cal.getTime();
		return df.format(date);
	}

	// null when can't read
///	public static Calendar calendarFromUnknownDateStr(String str)
//		{
//		CalendarValidator validator = CalendarValidator.getInstance();
//		return validator.validate(str);
//		}

	public static Calendar calendarFromDateStr(String dateString) {
		return calendarFromDateStr(dateString, "MM/dd/yyyy");
	}

	public static Date dateFromDateStr(String dateString, String format) {
		Date date;
		DateFormat formatter = new SimpleDateFormat(format);
		try {
			date = (Date) formatter.parse(dateString);
		} catch (ParseException e) {
			date = null;
		}

		return date;
	}

	public static Calendar calendarFromDateStr(String dateString, String format) {
		Calendar cal = Calendar.getInstance();
		Date date;
		DateFormat formatter = new SimpleDateFormat(format);
		try {
			date = (Date) formatter.parse(dateString);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return cal;
	}

	public static Calendar todaysDateAsCalendar() {
		Date dt = todaysDate();

		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(dt);

		return dateCal;
	}

	public static Calendar dateAsCalendar(Date date) {
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(date);
		return dateCal;
	}

	public static String grabMonthString(String parsedStr) throws ParseException, NullPointerException {
		Date dt;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		String dateStr;
		try {
			dateStr = parsedStr != null ? parsedStr.replace('-', '/') : "";
			dt = df.parse(dateStr);
			String monthAsStr = new SimpleDateFormat("MMMM").format(dt);
			return monthAsStr;
		} catch (ParseException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		}
	}

	@SuppressWarnings("deprecation")
	public static String grabYearString(String parsedStr) throws ParseException, NullPointerException {
		Date dt;
		Integer year;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		String dateStr = "";

		try {
			dateStr = parsedStr != null ? parsedStr.replace('-', '/') : "";
			dt = df.parse(dateStr);
			year = 1900 + dt.getYear();
			return year.toString();
		} catch (ParseException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		}
	}

	public static String grabYYYYmmddString(String parsedStr) throws ParseException, NullPointerException {
		return grabYYYYmmddString(parsedStr, "MM/dd/yy");
	}

	@SuppressWarnings("deprecation")
	public static String grabYYYYmmddString(String parsedStr, String dateFormat)
			throws ParseException, NullPointerException {
		Date dt;
		Integer year = -1, month = -1, day = -1;
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		String dateStr = "";

		List<Integer> monthDays = Arrays.asList(new Integer[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 });
		try {
			dateStr = (parsedStr != null ? parsedStr.replace('-', '/') : "");
			String[] date_tokens = parsedStr != null ? parsedStr.split("/") : null;

			for (int i = 0; i < date_tokens.length; i++)
				if (date_tokens[i].length() != 2) {
					if (!((i == 0 || i == 2) && date_tokens[i].length() == 4
							&& (date_tokens[i].startsWith("19") || date_tokens[i].startsWith("20")
									|| date_tokens[i].startsWith("21") || date_tokens[i].startsWith("18")
									|| date_tokens[i].startsWith("17") || date_tokens[i].startsWith("16"))))

					{
						System.out.println("Throwing parse exception for date token with incorrect length "
								+ date_tokens[i] + " in " + dateStr);
						throw new ParseException("Parse message", i);
					}
				}

			if (date_tokens != null && date_tokens.length == 3) {
				int dayToken = Integer.parseInt(date_tokens[1]);
				int monthToken = Integer.parseInt(date_tokens[0]);

				if (monthToken < 1 || monthToken > 12) {
					throw new ParseException("Parse message", monthToken);
				}

				if (dayToken > monthDays.get(monthToken - 1) || dayToken < 1) {
					throw new ParseException("Parse message", dayToken);
				}
			}

			dt = df.parse(dateStr);
			year = 1900 + dt.getYear();
			month = dt.getMonth() + 1;
			day = dt.getDate();
		} catch (ParseException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		}

		String monthStr = getMonthString(Integer.valueOf(month));
		String yearStr = getYearString(Integer.valueOf(year));
		String dayStr = getDayString(Integer.valueOf(day));

		return yearStr + monthStr + dayStr;
	}

	public static String getDayString(Integer day) {
		return day < 10 ? "0" + day.toString() : day.toString();
	}

	public static String getMonthString(Integer month) {
		return month < 10 ? "0" + month.toString() : month.toString();
	}

	public static String getYearString(Integer year) {
		String yearStr = year.toString();
		if (year < 10)
			yearStr = "000" + year.toString();
		else if (year < 100)
			yearStr = "00" + year.toString();
		else if (year < 1000)
			yearStr = "0" + year.toString();

		return yearStr;
	}
}
