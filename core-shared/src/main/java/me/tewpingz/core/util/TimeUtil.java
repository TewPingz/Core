package me.tewpingz.core.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    public static Long parseTime(String time) {
        if (time == null) {
            return null;
        }

        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];

            switch (type) {
                case "s" -> {
                    totalTime += value;
                    found = true;
                }
                case "m" -> {
                    totalTime += value * 60;
                    found = true;
                }
                case "h" -> {
                    totalTime += value * 60 * 60;
                    found = true;
                }
                case "d" -> {
                    totalTime += value * 60 * 60 * 24;
                    found = true;
                }
                case "w" -> {
                    totalTime += value * 60 * 60 * 24 * 7;
                    found = true;
                }
                case "M" -> {
                    totalTime += value * 60 * 60 * 24 * 30;
                    found = true;
                }
                case "y" -> {
                    totalTime += value * 60 * 60 * 24 * 365;
                    found = true;
                }
            }
        }

        return !found ? null : totalTime * 1000;
    }

    public static String formatIntoMMSS(int secs) {
        return formatToMMS(secs);
    }

    public static String formatToMMS(int secs) {
        int seconds = secs % 60;
        secs -= seconds;
        long minutesCount = (long) (secs / 60);
        long minutes = minutesCount % 60L;
        minutesCount -= minutes;
        long hours = minutesCount / 60L;
        return (hours > 0L ? (hours < 10L ? "0" : "") + hours + ":" : "") + (minutes < 10L ? "0" : "")
                + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public static String formatIntoDetailedString(int secs) {
        if (secs <= 0) {
            return "0 seconds";
        }
        int remainder = secs % 86400;

        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = (remainder / 60) - (hours * 60);
        int seconds = (remainder % 3600) - (minutes * 60);

        String fDays = (days > 0 ? " " + days + " day" + (days > 1 ? "s" : "") : "");
        String fHours = (hours > 0 ? " " + hours + " hour" + (hours > 1 ? "s" : "") : "");
        String fMinutes = (minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "");
        String fSeconds = (seconds > 0 ? " " + seconds + " second" + (seconds > 1 ? "s" : "") : "");

        return ((fDays + fHours + fMinutes + fSeconds).trim());
    }

    public static String formatLongIntoString(long millis) {
        return formatIntoString((int) millis / 1000);
    }

    public static String formatIntoString(int secs) {
        if (secs == 0) {
            return "0s";
        }

        int remainder = secs % 86400;
        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = (remainder / 60) - (hours * 60);
        int seconds = (remainder % 3600) - (minutes * 60);

        String fDays = (days > 0 ? days + "d " : "");
        String fHours = (hours > 0 ? hours + "h " : "");
        String fMinutes = (minutes > 0 ? minutes + "m " : "");
        String fSeconds = (seconds > 0 ? seconds + "s " : "");

        return ((fDays + fHours + fMinutes + fSeconds).trim());
    }

    public static int parseTime0(String time) {
        if (time.equals("0") || time.equals("")) {
            return (0);
        }

        String[] lifeMatch = new String[]{"w", "d", "h", "m", "s"};
        int[] lifeInterval = new int[]{604800, 86400, 3600, 60, 1};
        int seconds = 0;

        for (int i = 0; i < lifeMatch.length; i++) {
            Matcher matcher = Pattern.compile("([0-9]*)" + lifeMatch[i]).matcher(time);

            while (matcher.find()) {
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }
        }

        return (seconds);
    }

    public static int getSecondsBetween(Date a, Date b) {
        return (Math.abs((int) (a.getTime() - b.getTime()) / 1000));
    }

    public static String formatTime(long millis) {
        final int sec = (int) (millis / 1000 % 60);
        final int min = (int) (millis / 60000 % 60);
        final int hr = (int) (millis / 3600000 % 24);
        return ((hr > 0) ? String.format("%02d:", hr) : "") + String.format("%02d:%02d", min, sec);
    }

    public static String millisToTimer(long millis) {
        long seconds = millis / 1000L;
        if (seconds > 3600L) {
            return String.format("%02d:%02d:%02d", seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        }
        return String.format("%02d:%02d", seconds / 60L, seconds % 60L);
    }

    public static String formatLongIntoDetailedString(long secs) {
        int unconvertedSeconds = (int) secs;
        return formatIntoDetailedString(unconvertedSeconds);
    }

}
