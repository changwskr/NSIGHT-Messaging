package com.nh.nsight.messaging.tracedump.analyzer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class ApplicationComponentMapper {

    public String mapFromStackOrClass(String line) {
        if (line == null || line.isBlank()) {
            return "애플리케이션 (미식별)";
        }
        String lower = line.toLowerCase(Locale.ROOT);
        if (lower.contains("com.nh.nsight.messaging.message")) {
            return "NSIGHT 메시지 관리 (MessageController / MessageService / MyBatis)";
        }
        if (lower.contains("com.nh.nsight.messaging.transactionmgr")
                || lower.contains("transactionlog") || lower.contains("transactionmgr")) {
            return "NSIGHT 트랜잭션 관리 (TransactionLog / DB 로그)";
        }
        if (lower.contains("com.nh.nsight.messaging.tracedump")) {
            return "NSIGHT JVM 덤프 분석 (TraceDumpAnalysisService)";
        }
        if (lower.contains("com.nh.nsight.messaging.home")) {
            return "NSIGHT 홈·인증 (HomeAuth / 세션)";
        }
        if (lower.contains("com.nh.nsight.messaging.common.log")
                || lower.contains("messageenvelope") || lower.contains("standardresponse")) {
            return "NSIGHT 표준 로그·전문 캡처 (MessageEnvelope / Aspect)";
        }
        if (lower.contains("com.nh.nsight.messaging.files")) {
            return "NSIGHT 파일 관리";
        }
        if (lower.contains("com.zaxxer.hikari") || lower.contains("hikaripool")) {
            return "HikariCP 커넥션 풀 (DataSource)";
        }
        if (lower.contains("org.apache.ibatis") || lower.contains("org.mybatis")
                || lower.contains("jdbc") || lower.contains("preparedstatement")) {
            return "MyBatis / JDBC DB 접근 계층";
        }
        if (lower.contains("org.springframework.web") || lower.contains("dispatcherservlet")) {
            return "Spring MVC 요청 처리";
        }
        if (lower.contains("httpclient") || lower.contains("resttemplate") || lower.contains("webclient")) {
            return "외부 HTTP 연계 클라이언트";
        }
        if (lower.contains("httpsession") || lower.contains("session")) {
            return "HTTP 세션·세션 저장소";
        }
        if (lower.contains("hashmap") || lower.contains("concurrenthashmap") || lower.contains("arraylist")) {
            return "인메모리 컬렉션·캐시 구조";
        }
        if (lower.contains("byte[]") || lower.contains("[b") || lower.contains("char[]")) {
            return "대용량 배열·버퍼 (응답/파일/JSON)";
        }
        if (lower.contains("com.nh.nsight")) {
            return "NSIGHT 공통 모듈 (" + shorten(line) + ")";
        }
        return "JVM/프레임워크 (" + shorten(line) + ")";
    }

    public String joinPrograms(List<String> lines) {
        Set<String> programs = new LinkedHashSet<>();
        for (String line : lines) {
            programs.add(mapFromStackOrClass(line));
        }
        if (programs.isEmpty()) {
            return "애플리케이션 (스택 미식별)";
        }
        return String.join(" · ", programs);
    }

    public String causeForOomCategory(String oomCategory) {
        return switch (oomCategory) {
            case "HEAP_OOM" -> "Heap 객체 누적·대량 조회 결과·캐시 미해제로 Old Gen 고갈";
            case "METASPACE_OOM" -> "동적 클래스 로딩·리플렉션·과다 ClassLoader";
            case "DIRECT_BUFFER_OOM" -> "Netty/HTTP Direct Buffer·NIO 할당 한도 초과";
            case "NATIVE_THREAD_OOM" -> "Thread 과다 생성·Pool 상한·Xss 설정";
            case "OS_OOM_KILLER" -> "프로세스 RSS·Native+Heap 합산으로 OS 메모리 한도 초과";
            case "JVM_CRASH" -> "Native/JNI 크래시 — Heap OOM과 별도 JVM Fatal";
            default -> "증거 기반 추가 분석 필요";
        };
    }

    private String shorten(String line) {
        int at = line.indexOf('(');
        String name = at > 0 ? line.substring(0, at).trim() : line.trim();
        if (name.length() > 80) {
            return name.substring(0, 77) + "...";
        }
        return name;
    }

    public List<String> extractStackFrames(String content) {
        List<String> frames = new ArrayList<>();
        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.startsWith("at ") && (line.contains("com.nh.") || line.contains("org.springframework")
                    || line.contains("com.zaxxer") || line.contains("java.") || line.contains("org.apache"))) {
                frames.add(line.substring(3).trim());
            }
        }
        return frames;
    }
}
