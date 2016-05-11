package a.b.c.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManager {

    public final static String yMdHms = "yyyy-MM-dd HH:mm:ss";
    public final static String yMdHm = "yyyy-MM-dd HH:mm";
    public final static String yMd = "yyyy-MM-dd";
    public final static String yM = "yyyy-MM";
    public final static String Md = "MM-dd";
    public final static String MdHm = "MM-dd HH:mm";
    public final static String Hms = "HH:mm:ss";
    public final static String Hm = "HH:mm";

    private DateManager() {
    }

    // 切割秒为 小时、分钟、秒
    public static int[] splitSec(int second) {

        int[] splits = new int[3];

        int s = second % 60;
        int m = second / 60 % 60;
        int h = second / 60 / 60;

        splits[0] = h;
        splits[1] = m;
        splits[2] = s;

        return splits;
    }

    // 根据规则，把时间戳转换为字符串
    public static String string(long timestamp, String pattern) {
        return string(timestamp, pattern, Locale.getDefault());
    }

    // 根据规则和时区，把时间戳转换为字符串
    public static String string(long timestamp, String pattern, Locale locale) {
        if (locale == null) locale = Locale.getDefault();
        return new SimpleDateFormat(pattern, locale).format(new Date(timestamp));
    }

    // 根据规则，把字符串转换为时间戳
    public static long timestamp(String datetime, String pattern) {
        return timestamp(datetime, pattern, Locale.getDefault());
    }

    // 根据规则和时区，把字符串转换为时间戳
    public static long timestamp(String datetime, String pattern, Locale locale) {
        try {
            if (locale == null) locale = Locale.getDefault();
            return new SimpleDateFormat(pattern, locale).parse(datetime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static Calendar calendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }

    public static int year(long timestamp) {
        return calendar(timestamp).get(Calendar.YEAR);
    }

    public static int month(long timestamp) {
        return calendar(timestamp).get(Calendar.MONTH) + 1;
    }

    public static int day(long timestamp) {
        return calendar(timestamp).get(Calendar.DAY_OF_MONTH);
    }

    public static int hour(long timestamp) {
        return calendar(timestamp).get(Calendar.HOUR_OF_DAY);
    }

    public static int minute(long timestamp) {
        return calendar(timestamp).get(Calendar.MINUTE);
    }

    public static int second(long timestamp) {
        return calendar(timestamp).get(Calendar.SECOND);
    }

    public static int millis(long timestamp) {
        return calendar(timestamp).get(Calendar.MILLISECOND);
    }
}
