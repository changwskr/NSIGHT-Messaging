package com.nh.nsight.messaging.zpilotfwk.comrc.dc.repository;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.entity.SpComrcEntity;

import java.util.List;
import java.util.Optional;

public interface SpComrcRepository {

    SpComrcEntity insert(SpComrcEntity entity);

    Optional<SpComrcEntity> findById(Long id);

    List<SpComrcEntity> findAll(SpComrc7201BIZDDTO condition);

    long countAll(SpComrc7201BIZDDTO condition);

    int update(SpComrcEntity entity);

    int deleteById(Long id);
}
