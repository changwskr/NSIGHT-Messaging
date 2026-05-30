package com.nh.nsight.messaging.file.util;

import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Tomcat/Servlet multipart 파싱 시 UTF-8 파일명이 ISO-8859-1로 잘못 해석되는 경우 복원.
 */
public final class MultipartFilenameDecoder {

    private MultipartFilenameDecoder() {
    }

    public static String decode(String filename) {
        if (!StringUtils.hasText(filename)) {
            return filename;
        }
        if (isValidUtf8KoreanOrAscii(filename)) {
            return filename;
        }
        String recovered = reinterpret(filename, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
        if (StringUtils.hasText(recovered) && !recovered.contains("\uFFFD")) {
            return recovered;
        }
        recovered = reinterpret(filename, Charset.forName("MS949"), StandardCharsets.UTF_8);
        if (StringUtils.hasText(recovered) && !recovered.contains("\uFFFD")) {
            return recovered;
        }
        return filename;
    }

    private static String reinterpret(String value, Charset from, Charset to) {
        return new String(value.getBytes(from), to);
    }

    private static boolean isValidUtf8KoreanOrAscii(String value) {
        if (value.chars().anyMatch(ch -> (ch >= 0xAC00 && ch <= 0xD7A3) || (ch >= 0x3131 && ch <= 0x318E))) {
            return true;
        }
        return value.chars().allMatch(ch -> ch < 128 || ch > 255);
    }
}
