package com.nh.nsight.messaging.zpilotfwk.tcf.support;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class CommonUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmmss");

    private CommonUtil() {
    }

    public static String GetSysDate() {
        return LocalDateTime.now().format(DATE_FMT);
    }

    /**
     * 레거시 프레임워크 호환 8자리 시각 (HHmmssSS).
     * STF/TCF는 systemInTime 등에 8자리를 기대한다.
     */
    public static String GetSysTime() {
        LocalDateTime now = LocalDateTime.now();
        int centiSec = now.getNano() / 10_000_000;
        return now.format(TIME_FMT) + String.format("%02d", centiSec);
    }

    public static String GetHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            return "localhost";
        }
    }

    public static String Int2Str(int value) {
        return Integer.toString(value);
    }

    public static int Str2Int(String value) {
        try {
            return Integer.parseInt(value == null ? "0" : value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int atoi(String value) {
        return Str2Int(value);
    }

    public static int ItvSec(String start, String end) {
        try {
            LocalDateTime s = parseTime(start);
            LocalDateTime e = parseTime(end);
            return (int) ChronoUnit.SECONDS.between(s, e);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String SpaceToStr(String value, int length) {
        String source = value == null ? "" : value;
        if (source.length() >= length) {
            return source.substring(0, length);
        }
        return String.format("%-" + length + "s", source);
    }

    public static String ZeroToStr(String value, int length) {
        String source = value == null ? "" : value;
        if (source.length() >= length) {
            return source.substring(0, length);
        }
        return String.format("%0" + length + "d", Str2Int(source));
    }

    public static boolean CHECKisdigit(String value, int length) {
        if (value == null || value.length() < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String catchSTRINGseq(String value, int index, String delimiter) {
        if (value == null) {
            return "";
        }
        String[] parts = value.split(delimiter);
        if (index <= 0 || index > parts.length) {
            return "";
        }
        return parts[index - 1];
    }

    private static LocalDateTime parseTime(String value) {
        String normalized = value == null ? "00000000" : value.trim();
        if (normalized.length() < 6) {
            normalized = (normalized + "00000000").substring(0, 6);
        }
        return LocalDateTime.parse(normalized.substring(0, 6), TIME_FMT);
    }
}
