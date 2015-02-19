package com.waterR8.util;

import java.util.Date;

public class DateUtils {

	static public String getTimeSinceLabel(Date dateSent) {
		
		long timeNow = System.currentTimeMillis();
		long timeThen = dateSent.getTime();
		
		long timeDiff = timeNow - timeThen;
		
		// how many minutes ago?
		
		long daysAgo = timeDiff / (1000 * 60 * 60 * 24);
		if(daysAgo > 0) {
			return daysAgo + " " + (daysAgo==1?"day":"days") + " ago";
		}
		else {
			long hoursAgo = timeDiff / (1000 * 60 * 60);
			if(hoursAgo > 0) {
				return hoursAgo + " " + (hoursAgo==1?"hour":"hours") + " ago";
			}
			else {
				long minAgo = timeDiff / (1000 * 60);
				if(minAgo > 0) {
					return minAgo + " " + (minAgo==1?"minute":"minutes") + " ago";
				}
				else {
					long secondsAgo = timeDiff / (1000);
					return secondsAgo + " " + (secondsAgo==1?"second":"seconds") + " ago";
				}
			}
		}
		// __formatter.format(dateSent);		
    }

}
