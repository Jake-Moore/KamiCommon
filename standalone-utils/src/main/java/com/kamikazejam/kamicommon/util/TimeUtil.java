package com.kamikazejam.kamicommon.util;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class TimeUtil {
    /**
     * Returns the current time in the format like 10h, 5m, 3s <p>
     * If there are no hours it returns 5m, 3s <p>
     * If there are no hours or minutes it returns 3s
     */
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

    /**
     * @param clockTime A 24-hour time string formatted like "HH:mm" or "HH:mm:ss"
     * @param timeZone The time zone to use
     * @return a Date object representing that time of day in the nearest future
     */
    public static @NotNull Date getDateBy24HourTime(String clockTime, TimeZone timeZone) throws IllegalArgumentException {
        String[] split = clockTime.split(":");
        if (split.length < 2) {
            throw new IllegalArgumentException("Invalid time format: " + clockTime + " (expected HH:mm:ss or HH:mm)");
        }

        int hour = Integer.parseInt(split[0]);
        int minute = Integer.parseInt(split[1]);
        int second = split.length > 2 ? Integer.parseInt(split[2]) : 0;

        return getDateBy24HourTime(hour, minute, second, timeZone);
    }

    /**
     * @param hour The 24-hour time hour [0, 23]
     * @param minute The minute, [0, 59]
     * @param second The second, [0, 59]
     * @param timeZone The time zone to use
     * @return a Date object representing that time of day in the nearest future
     */
    public static @NotNull Date getDateBy24HourTime(int hour, int minute, int second, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(timeZone);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);

        // Find the earliest time, start by backing up a bit (timezones can be weird)
        cal.add(Calendar.DAY_OF_MONTH, -2);

        // Loop until we have a time in the future
        while (cal.getTime().before(new Date())) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return cal.getTime();
    }
    private static final SimpleDateFormat DF_FULL = new SimpleDateFormat("MMM dd yyyy hh:mm aa zzz");

    public static void main(String[] args) {
        String time = "19:00";
        String tz = "America/Los_Angeles";
        final SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy hh:mm aa zzz");

        Date date = getDateBy24HourTime(time, TimeZone.getTimeZone(tz));
        System.out.println(df.format(date));
    }
}
