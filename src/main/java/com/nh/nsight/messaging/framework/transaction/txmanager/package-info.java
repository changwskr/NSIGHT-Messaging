/**
 * JDBC {@link ThreadLocal} 기반 수동 트랜잭션 관리 <b>샘플</b> 패키지입니다.
 * <p>
 * 운영 비즈니스 코드는 Spring {@code @Transactional}, HikariCP, MyBatis를 사용합니다.
 * ({@code MessageFacade}, {@code FileFacade}, {@code TransactionMgrFacade} 등)
 * </p>
 * <p>
 * 이 패키지는 학습·참고용이며, DAO에서 {@link TransactionManager#getConnection()}으로
 * 동일 커넥션을 공유하는 패턴을 보여 줍니다.
 * </p>
 */
package com.nh.nsight.messaging.framework.transaction.txmanager;
