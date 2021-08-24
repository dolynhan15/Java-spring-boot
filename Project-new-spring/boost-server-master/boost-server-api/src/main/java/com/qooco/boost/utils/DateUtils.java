package com.qooco.boost.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.zone.ZoneRulesException;
import java.util.*;

public class DateUtils {
    private static String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static String DATE_PATTERN_SPLIT = "MM/dd/yyyy/HH/mm/ss/Z";
    private static String SPLIT = "/";
    private static String SHORT_DATE_PATTERN = "YYYYMMdd";
    private static String EXPECT_START_DATE_FORMAT = "dd MMMM yyyy";

    private static int DATE_INDEX = 1;
    private static int MONTH_INDEX = 0;
    private static int YEAR_INDEX = 2;
    private static int HOUR_INDEX = 3;
    private static int MINUTE_INDEX = 4;
    private static int SECOND_INDEX = 5;

    private static int START_VALUE = 0;
    private static int ONE_DAY = 1;
    private static int TWO_DAYS = 2;
    private static int THREE_DAYS = 3;
    private static int FOUR_DAYS = 4;
    private static int FIVE_DAYS = 5;
    private static int SIX_DAYS = 6;
    private static long ONE_MINUTE_TO_MILLISECOND = 1000L;
    public static int SECOND_OF_HOUR = 3600;
    public static final long ONE_DAY_IN_DAY_UNIT = 86400000;
    public static final Date MAX_DATE = new Date(95629865716000L);
    private static int QUARTER_HOUR = 15;
    public static int QUARTER_HOUR_IN_SECOND = 900;
    public static long ONE_DAY_IN_MILLISECOND = 60 * 60 * 24 * 1000;

    public static long MAX_DATE_ORACLE = 253402298639000L;

    public static Date nowUtcForOracle() {
        return nowUtc();
    }

    public static Date nowUtc() {
        Date now = new Date();
        LocalDateTime dateTime = LocalDateTime.ofInstant(now.toInstant(), ZoneOffset.UTC);
        return Date.from(dateTime.toInstant(OffsetDateTime.now().getOffset()));
    }

    public static Date addDays(Date date, int days) {
        if (Objects.isNull(date)) {
            date = new Date();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date atStartOfDate(Date date, String timeZone) {
        TimeZone timezone = TimeZone.getTimeZone(ZoneId.of(timeZone));
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(timezone);
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date atStartOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date atEndOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date atEndOfDateFromStart(Date date) {
        Date nextDate = addDays(date, 1);
        return addSecond(nextDate, -1);
    }

    public static Date atEndOfHour(Date date) {
        Date nextHour = addSecond(date, 3600);
        return addSecond(nextHour, -1);
    }

    public static Date atStartOfWeek(Date date) {
        Date startOfDate = atStartOfDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfDate);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return calendar.getTime();
    }

    public static Date atStartOfMonth(Date date) {
        Date startOfDate = atStartOfDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startOfDate);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static Date addSecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    public static Date toUtcForOracle(Date date) {
        if (Objects.isNull(date)) {
            return nowUtcForOracle();
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
        return Date.from(dateTime.toInstant(OffsetDateTime.now().getOffset()));
    }

    public static Date getUtcForOracle(Date date) {
        if (Objects.isNull(date)) {
            return nowUtcForOracle();
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    public static Date toServerTimeForMongo(Date date) {
        if (Objects.isNull(date)) {
            return new Date();
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
        return Date.from(dateTime.toInstant(ZoneOffset.UTC));
    }

    public static Date toServerTimeForMongo() {
        return toServerTimeForMongo(null);
    }

    public static long diffHours(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return diff / (60 * 60 * 1000);
    }

    public static Date convertUTCtoLocalTimeByTimeZone(Date date, String timezone) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timezone)));
        String formated = dateFormat.format(date);
        try {
            return dateFormat.parse(formated);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public static String formatShortLocalTimeByTimeZone(Date date, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(SHORT_DATE_PATTERN);
        TimeZone zone = TimeZone.getTimeZone(ZoneId.of(timeZone));
        dateFormat.setTimeZone(zone);
        try {
            return dateFormat.format(date);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatExpectedStartDate(Date date, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(EXPECT_START_DATE_FORMAT);
        TimeZone zone = TimeZone.getTimeZone(ZoneId.of(timeZone));
        dateFormat.setTimeZone(zone);
        try {
            return dateFormat.format(date);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalDateTime convertDateBetweenTimeZones(Date date, String timeZone) {
        return convertDateBetweenTimeZones(date, null, timeZone);
    }

    private static LocalDateTime convertDateBetweenTimeZones(Date date, String zoneA, String zoneB) {
        DateFormat formatter = new SimpleDateFormat(DATE_PATTERN_SPLIT);
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        String[] times = formatter.format(date).split(SPLIT);
        int dayOfMonth = Integer.parseInt(times[DATE_INDEX]);
        Month month = Month.of(Integer.valueOf(times[MONTH_INDEX]));
        int year = Integer.parseInt(times[YEAR_INDEX]);
        int hour = Integer.parseInt(times[HOUR_INDEX]);
        int minute = Integer.parseInt(times[MINUTE_INDEX]);
        int second = Integer.parseInt(times[SECOND_INDEX]);
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);

        ZoneId oldZone;
        if (StringUtils.isBlank(zoneA)) {
            oldZone = ZoneId.systemDefault();
        } else {
            try {
                oldZone = ZoneId.of(zoneA);
            } catch (ZoneRulesException ex) {
                Logger logger = LogManager.getLogger(DateUtils.class);
                logger.error(ex);
                return null;
            }
        }
        ZoneId newZone;
        if (StringUtils.isBlank(zoneB)) {
            newZone = ZoneId.systemDefault();
        } else {
            try {
                newZone = ZoneId.of(zoneB);
            } catch (DateTimeException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return localDateTime.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();
    }

    public static LocalDateTime getStartDayOfWeek(LocalDateTime localDate) {
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        LocalDateTime firstWorkingDay;
        switch (dayOfWeek) {
            case MONDAY:
                firstWorkingDay = localDate.minusDays(ONE_DAY);
                break;
            case TUESDAY:
                firstWorkingDay = localDate.minusDays(TWO_DAYS);
                break;
            case WEDNESDAY:
                firstWorkingDay = localDate.minusDays(THREE_DAYS);
                break;
            case THURSDAY:
                firstWorkingDay = localDate.minusDays(FOUR_DAYS);
                break;
            case FRIDAY:
                firstWorkingDay = localDate.minusDays(FIVE_DAYS);
                break;
            case SATURDAY:
                firstWorkingDay = localDate.minusDays(SIX_DAYS);
                break;
            case SUNDAY:
            default:
                firstWorkingDay = localDate;
                break;
        }
        return firstWorkingDay.withHour(START_VALUE).withMinute(START_VALUE).withSecond(START_VALUE).withNano(START_VALUE);
    }

    public static LocalDateTime getStartDayOfMonth(LocalDateTime localDate) {
        LocalDateTime firstDayOfMonth = localDate.withDayOfMonth(ONE_DAY);
        return firstDayOfMonth.withHour(START_VALUE).withMinute(START_VALUE).withSecond(START_VALUE);
    }

    public static long getMillisecond(LocalDateTime dateTime, ZoneId zoneId) {
        return (dateTime.toEpochSecond(zoneId.getRules().getOffset(dateTime)) * ONE_MINUTE_TO_MILLISECOND);
    }

    public static Date convertMillisecondToDate(long millisecond) {
        Date date = new Date();
        date.setTime(millisecond);
        return date;
    }

    public static Date setHourByTimeStamp(Date date, Date timeStamp) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        Calendar hourCalendar = Calendar.getInstance();
        hourCalendar.setTime(timeStamp);

        dateCalendar.set(Calendar.HOUR_OF_DAY, hourCalendar.get(Calendar.HOUR_OF_DAY));
        dateCalendar.set(Calendar.MINUTE, hourCalendar.get(Calendar.MINUTE));
        dateCalendar.set(Calendar.SECOND, hourCalendar.get(Calendar.SECOND));
        dateCalendar.set(Calendar.MILLISECOND, hourCalendar.get(Calendar.MILLISECOND));
        return dateCalendar.getTime();
    }

    public static boolean isStartOfCurrentDate(final Date timeOfStartDate){
        final String timeZoneId = getTimeZoneByClientDate(timeOfStartDate);
        final Date date = convertUTCByTimeZone(nowUtc(), timeZoneId);
        return !timeOfStartDate.before(date);
    }

    public static Date getStartDateByTimeZone(final String timeZone){
        return convertUTCByTimeZone(nowUtc(), timeZone);
    }

    private static String getTimeZoneByClientDate(final Date timeOfStartDate) {
        final Instant timeStampZone = Instant.ofEpochSecond(timeOfStartDate.getTime());
        for (final Map.Entry<String, String> entry : ZoneId.SHORT_IDS.entrySet()) {
            final ZonedDateTime timeWithZoneId = timeStampZone.atZone(ZoneId.of(entry.getValue()));
            if (timeWithZoneId.getHour() == 0
                    && timeWithZoneId.getMinute() == 0
                    && timeWithZoneId.getSecond() == 0) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Date convertUTCByTimeZone(Date date, String timeZone){
        final long nowMillisecond = date.getTime();
        final TimeZone zone = TimeZone.getTimeZone(timeZone);
        final long offset = zone.getRawOffset();
        return new Date(nowMillisecond + offset);
    }


    public static Date roundDateToQuarterHour(Date date) {
        if (Objects.nonNull(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int unroundedMinutes = calendar.get(Calendar.MINUTE);
            int mod = unroundedMinutes % QUARTER_HOUR;
            calendar.add(Calendar.MINUTE, -mod);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }
        return null;

    }

    public static String convert24HourTimeTo12HourTime(String time) {
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");

        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        try {
            return date12Format.format(date24Format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static int countDays(long startDate, long endDate) {
        return (int)((endDate - startDate) / ONE_DAY_IN_DAY_UNIT + ((endDate - startDate) % ONE_DAY_IN_DAY_UNIT == 0 ? 0 : 1));
    }
}
