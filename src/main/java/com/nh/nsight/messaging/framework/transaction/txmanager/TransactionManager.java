package com.nh.nsight.messaging.framework.transaction.txmanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * ThreadLocal + JDBC 수동 트랜잭션 관리 <b>샘플</b> 구현.
 * <p>
 * {@link #execute(Runnable)} 블록 안에서 {@link #getConnection()}으로 동일 {@link Connection}을
 * 여러 DAO/Repository가 공유하는 패턴을 참고용으로 제공합니다.
 * </p>
 * <p><b>주의:</b> Spring {@code @Transactional}과 동시에 같은 작업에 적용하지 마세요.</p>
 */
public class TransactionManager {

    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    /**
     * 트랜잭션 경계 안에서 작업을 실행하고, 성공 시 커밋·실패 시 롤백합니다.
     */
    public void execute(Runnable action) {
        execute(() -> {
            action.run();
            return null;
        });
    }

    /**
     * 트랜잭션 경계 안에서 작업을 실행하고 결과를 반환합니다.
     */
    public <T> T execute(Supplier<T> action) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return run(connection, action);
        } catch (SQLException e) {
            throw new IllegalStateException("DB 연결 처리 중 오류가 발생했습니다.", e);
        }
    }

    private <T> T run(Connection connection, Supplier<T> action) throws SQLException {
        CONNECTION_HOLDER.set(connection);
        connection.setAutoCommit(false);
        try {
            T result = action.get();
            commit(connection);
            return result;
        } catch (RuntimeException e) {
            rollback(connection);
            throw e;
        } finally {
            CONNECTION_HOLDER.remove();
        }
    }

    private void commit(Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            throw new IllegalStateException("커밋에 실패했습니다.", e);
        }
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new IllegalStateException("롤백에 실패했습니다.", e);
        }
    }

    /**
     * 현재 스레드의 활성 트랜잭션 커넥션. {@link #execute(Runnable)} 내부에서만 유효합니다.
     */
    public Connection getConnection() {
        Connection connection = CONNECTION_HOLDER.get();
        if (connection == null) {
            throw new IllegalStateException("트랜잭션이 시작되지 않았습니다. execute() 안에서 호출하세요.");
        }
        return connection;
    }

    /**
     * execute() 블록 안에서 실행 중인지 여부.
     */
    public boolean isActive() {
        return CONNECTION_HOLDER.get() != null;
    }
}
