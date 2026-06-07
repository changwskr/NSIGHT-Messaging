package com.nh.nsight.messaging.zpilotfwk.common.dc.repository;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.common.dc.entity.SpCommonEntity;

import java.util.List;
import java.util.Optional;

/**
 * SP_COMMON Repository — 영속성 접근.
 */
public interface SpCommonRepository {

    SpCommonEntity insert(SpCommonEntity entity);

    Optional<SpCommonEntity> findById(Long id);

    List<SpCommonEntity> findAll(SpCommon7001BIZDDTO condition);

    long countAll(SpCommon7001BIZDDTO condition);

    int update(SpCommonEntity entity);

    int deleteById(Long id);
}
