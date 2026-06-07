package com.nh.nsight.messaging.zpilotfwk.order.dc.repository;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;

import java.util.List;
import java.util.Optional;

public interface SpOrderRepository {

    SpOrderEntity insert(SpOrderEntity entity);

    Optional<SpOrderEntity> findById(Long id);

    List<SpOrderEntity> findAll(SpOrder7101BIZDDTO condition);

    long countAll(SpOrder7101BIZDDTO condition);

    int update(SpOrderEntity entity);

    int deleteById(Long id);
}
