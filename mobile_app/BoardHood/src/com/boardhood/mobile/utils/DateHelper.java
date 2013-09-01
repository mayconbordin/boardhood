package com.boardhood.mobile.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;

import com.boardhood.mobile.R;

import android.content.Context;

public class DateHelper {
	public static String format(int amount, String type, String append) {
		String f = String.format("%d %s", amount, amount > 1 ? type + "s" : type);
		return f + " " + append;
	}
	
	public static String getShortTimeDiff(Context ctx, Date start, Date end) {
		DateTime a = new DateTime(start);
		DateTime b = new DateTime(end);
		
		String abrev[] = new String[] {
				ctx.getResources().getString(R.string.short_year),
				ctx.getResources().getString(R.string.short_month),
				ctx.getResources().getString(R.string.short_day),
				ctx.getResources().getString(R.string.short_hour),
				ctx.getResources().getString(R.string.short_min),
				ctx.getResources().getString(R.string.short_sec)
	    };
		
		int difference = Years.yearsBetween(a, b).getYears();
		if (difference > 0)
			return String.format("%d%s", difference, abrev[0]);
		
		difference = Months.monthsBetween(a, b).getMonths();
		if (difference > 0)
			return String.format("%d%s", difference, abrev[1]);
		
		difference = Days.daysBetween(a, b).getDays();
		if (difference > 0)
			return String.format("%d%s", difference, abrev[2]);
		
		difference = Hours.hoursBetween(a, b).getHours();
		if (difference > 0)
			return String.format("%d%s", difference, abrev[3]);
		
		difference = Minutes.minutesBetween(a, b).getMinutes();
		if (difference > 0)
			return String.format("%d%s", difference, abrev[4]);
		
		difference = Seconds.secondsBetween(a, b).getSeconds();
		if (difference > 0)
			return String.format("%d%s", difference, abrev[5]);
		
		return ctx.getResources().getString(R.string.now);
	}
	
	public static String getTimeDiff(Context ctx, Date start, Date end) {
		DateTime a = new DateTime(start);
		DateTime b = new DateTime(end);
		
		String ago = ctx.getResources().getString(R.string.ago);
		String abrev[] = new String[] {
				ctx.getResources().getString(R.string.year),
				ctx.getResources().getString(R.string.month),
				ctx.getResources().getString(R.string.day),
				ctx.getResources().getString(R.string.hour),
				ctx.getResources().getString(R.string.min),
				ctx.getResources().getString(R.string.sec)
	    };
		
		int difference = Years.yearsBetween(a, b).getYears();
		if (difference > 0)
			return DateHelper.format(difference, abrev[0], ago);
		
		difference = Months.monthsBetween(a, b).getMonths();
		if (difference > 0)
			return DateHelper.format(difference, abrev[1], ago);
		
		difference = Days.daysBetween(a, b).getDays();
		if (difference > 0)
			return DateHelper.format(difference, abrev[2], ago);
		
		difference = Hours.hoursBetween(a, b).getHours();
		if (difference > 0)
			return DateHelper.format(difference, abrev[3], ago);
		
		difference = Minutes.minutesBetween(a, b).getMinutes();
		if (difference > 0)
			return DateHelper.format(difference, abrev[4], ago);
		
		difference = Seconds.secondsBetween(a, b).getSeconds();
		if (difference > 0)
			return DateHelper.format(difference, abrev[5], ago);
		
		return ctx.getResources().getString(R.string.now);
	}
}
