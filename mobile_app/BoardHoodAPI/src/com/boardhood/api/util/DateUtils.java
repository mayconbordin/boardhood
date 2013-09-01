package com.boardhood.api.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {
	public static String toIsoFormat(Date date) {
		DateTime dt = new DateTime(date);
		DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss");
		return fmt.print(dt);
	}
}
