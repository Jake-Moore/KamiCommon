package com.kamikazejamplugins.kamicommon.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class TimeUtil {
    public static String getSecondsToTimeString(int seconds) {
        return getSecondsToTimeString((long) seconds);
    }

    public static String getSecondsToTimeString(long seconds) {
        //Anything past 1 day gets weird because days in a month varies
        if (seconds >= 86400) { //1 day
            int days = (int) Math.floor(seconds / 86400D);
            long left = (seconds - (days * 86400L));

            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("HH'h, 'mm'm, 'ss's'");
            df.setTimeZone(tz);

            return days + "d " + df.format(new Date((left)*1000L));
        }else if (seconds >= 36000) { //10 hours
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("HH'h, 'mm'm, 'ss's'");
            df.setTimeZone(tz);

            return df.format(new Date(seconds*1000L));
        }else if (seconds >= 3600) { //1 hour
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("H'h, 'mm'm, 'ss's'");
            df.setTimeZone(tz);

            return df.format(new Date(seconds*1000L));
        }else if (seconds >= 600) { //10 minutes
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("mm'm, 'ss's'");
            df.setTimeZone(tz);

            return df.format(new Date(seconds*1000L));
        }else if (seconds >= 60) { //1 minute
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("m'm, 'ss's'");
            df.setTimeZone(tz);

            return df.format(new Date(seconds*1000L));
        }else if (seconds >= 10) { //10 seconds
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("ss's'");
            df.setTimeZone(tz);

            return df.format(new Date(seconds*1000L));
        }else { //1 second
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("s's'");
            df.setTimeZone(tz);

            return df.format(new Date(seconds*1000L));
        }
    }
}
