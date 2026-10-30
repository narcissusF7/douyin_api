package com.dsh.douyin.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具：抖店要求 timestamp 格式为 yyyy-MM-dd HH:mm:ss，时区 GMT+8。
 */
public final class DateTimeUtils {

    public static final ZoneId GMT8 = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtils() {
    }

    /** 当前 GMT+8 时间字符串 */
    public static String nowGmt8() {
        return ZonedDateTime.now(GMT8).format(FMT);
    }

    /** epoch 毫秒 -> GMT+8 时间字符串 */
    public static String formatGmt8(long epochMillis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), GMT8).format(FMT);
    }
}
