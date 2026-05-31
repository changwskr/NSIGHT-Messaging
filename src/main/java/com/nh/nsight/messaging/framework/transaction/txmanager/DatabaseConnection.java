package com.nh.nsight.messaging.framework.transaction.txmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * {@link TransactionManager} 샘플 전용 JDBC 연결 유틸.
 * <p>운영 애플리케이션은 Spring {@code DataSource}(Hikari)를 사용합니다.</p>
 */
public final class DatabaseConnection {

    /**
     * 로컬 H2 in-memory URL (application.yml local 프로파일과 동일 스키마명).
     */
    static final String SAMPLE_JDBC_URL =
            "jdbc:h2:mem:nsightmsg;MODE=Oracle;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1";
    static final String SAMPLE_USER = "sa";
    static final String SAMPLE_PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(SAMPLE_JDBC_URL, SAMPLE_USER, SAMPLE_PASSWORD);
    }
}
