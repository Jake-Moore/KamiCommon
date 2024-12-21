package com.kamikazejam.kamicommon.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class TimeUtil {
    public enum TimeFormat {
        LETTERS,
        WORDS,
    }

    /**
     * Compiles current duration in the {@link TimeFormat#LETTERS} format: <br>
     * {@code "2d, 10h, 5m, 3s"} when days > 0 <br>
     * {@code "10h, 5m, 3s"} when hours > 0 <br>
     * {@code "5m, 3s"} when minutes > 0 <br>
     * {@code "3s"} when seconds > 0
     */
    @NotNull
    public static String getSecondsToTimeString(long seconds) {
        // Using the Duration Java Class
        Duration duration = Duration.ofSeconds(seconds);
        
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long secs = duration.toSecondsPart();
        
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append("d ");
        }
        if (days > 0 || hours > 0) {
            result.append(hours).append("h");
            result.append(", ");
        }
        if (days > 0 || hours > 0 || minutes > 0) {
            result.append(minutes).append("m");
            result.append(", ");
        }
        result.append(secs).append("s");
        
        return result.toString();
    }

    /**
     * Returns the current time in the following formats: <br>
     * <br>
     * {@link TimeFormat#LETTERS} <br>
     * {@code "2d, 10h, 5m, 3s"} when days > 0 <br>
     * {@code "10h, 5m, 3s"} when hours > 0 <br>
     * {@code "5m, 3s"} when minutes > 0 <br>
     * {@code "3s"} when seconds > 0 <br>
     * <br>
     * {@link TimeFormat#WORDS} <br>
     * {@code "2 days, 10 hours, 5 minutes, 3 seconds"} when days > 0 <br>
     * {@code "10 hours, 5 minutes, 3 seconds"} when hours > 0 <br>
     * {@code "5 minutes, 3 seconds"} when minutes > 0 <br>
     * {@code "3 seconds"} when seconds > 0 <br>
     * (The time word only includes the plural "s" when appropriate)
     */
    @NotNull
    public static String getSecondsToTimeString(long seconds, @NotNull TimeFormat format) {
        Preconditions.checkNotNull(format, "Time format cannot be null");
        if (format == TimeFormat.LETTERS) {
            return getSecondsToTimeString(seconds);
        }
        
        Duration duration = Duration.ofSeconds(seconds);
        
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long secs = duration.toSecondsPart();
        
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(days == 1 ? " day " : " days ");
        }
        if (days > 0 || hours > 0) {
            result.append(hours).append(hours == 1 ? " hour" : " hours");
            result.append(", ");
        }
        if (days > 0 || hours > 0 || minutes > 0) {
            result.append(minutes).append(minutes == 1 ? " minute" : " minutes");
            result.append(", ");
        }
        result.append(secs).append(secs == 1 ? " second" : " seconds");
        
        return result.toString();
    }

    /**
     * @param clockTime A 24-hour time string formatted like "HH:mm" or "HH:mm:ss"
     * @param timeZone The time zone to use
     * @return a Date object representing that time of day in the nearest future
     */
    public static @NotNull Date getDateBy24HourTime(@NotNull String clockTime, @NotNull TimeZone timeZone) throws IllegalArgumentException {
        Preconditions.checkNotNull(clockTime, "Clock time cannot be null");
        Preconditions.checkNotNull(timeZone, "Time zone cannot be null");

        String[] split = clockTime.split(":");
        if (split.length < 2 || split.length > 3) {
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
    public static @NotNull Date getDateBy24HourTime(int hour, int minute, int second, @NotNull TimeZone timeZone) {
        Preconditions.checkArgument(hour >= 0 && hour <= 23, "Hour must be in the range [0, 23]");
        Preconditions.checkArgument(minute >= 0 && minute <= 59, "Minute must be in the range [0, 59]");
        Preconditions.checkArgument(second >= 0 && second <= 59, "Second must be in the range [0, 59]");
        Preconditions.checkNotNull(timeZone, "Time zone cannot be null");

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
}
