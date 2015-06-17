package com.android.tweetsnearby;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

//"Mon May 18 00:06:34 +0000 2015"
public class CalcTime {
	
	public static long getTimeDist(String UTC) {
		
		Log.v("breakpoint",UTC);

		Calendar cal = Calendar.getInstance();

		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		Date now = cal.getTime();
		Date twt = null;
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss '+0000' yyyy");

		try {
			twt = df.parse(UTC);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long diff = now.getTime() - twt.getTime();
		Log.v("breakpoint","TimeDist"+String.valueOf(diff/1000));
		return diff/1000;

	}
}
