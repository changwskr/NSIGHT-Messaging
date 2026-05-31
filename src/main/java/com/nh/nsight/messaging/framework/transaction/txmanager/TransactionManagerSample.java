package com.nh.nsight.messaging.framework.transaction.txmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * {@link TransactionManager} 사용 예시 (참고용, 운영에서 자동 실행되지 않음).
 */
public final class TransactionManagerSample {

    private TransactionManagerSample() {
    }

    /**
     * H2가 기동·스키마 로드된 동일 JVM에서만 동작합니다.
     * Spring Boot 애플리케이션 실행 후 별도 main으로 호출하거나 테스트에서 사용하세요.
     */
    public static void main(String[] args) {
        TransactionManager txManager = new TransactionManager();

        txManager.execute(() -> {
            Connection connection = txManager.getConnection();
            logActiveTransactionCount(connection);
        });

        System.out.println("[SAMPLE] TransactionManager execute completed.");
    }

    private static void logActiveTransactionCount(Connection connection) {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.println("[SAMPLE] PUBLIC schema table count=" + rs.getInt(1));
            }
        } catch (Exception e) {
            throw new IllegalStateException("샘플 조회 실패", e);
        }
    }
}
