package com.nh.nsight.messaging.zpilotfwk.common.dc.repository;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.common.dc.entity.SpCommonEntity;
import com.nh.nsight.messaging.zpilotfwk.common.dc.mapper.SpCommonMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SpCommonRepositoryImpl implements SpCommonRepository {

    private final SpCommonMapper spCommonMapper;

    public SpCommonRepositoryImpl(SpCommonMapper spCommonMapper) {
        this.spCommonMapper = spCommonMapper;
    }

    @Override
    public SpCommonEntity insert(SpCommonEntity entity) {
        if (entity == null) {
            return null;
        }
        spCommonMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<SpCommonEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(spCommonMapper.selectById(id));
    }

    @Override
    public List<SpCommonEntity> findAll(SpCommon7001BIZDDTO condition) {
        SpCommon7001BIZDDTO criteria = condition != null ? condition : new SpCommon7001BIZDDTO();
        return spCommonMapper.selectAll(criteria);
    }

    @Override
    public long countAll(SpCommon7001BIZDDTO condition) {
        SpCommon7001BIZDDTO criteria = condition != null ? condition : new SpCommon7001BIZDDTO();
        return spCommonMapper.countAll(criteria);
    }

    @Override
    public int update(SpCommonEntity entity) {
        if (entity == null || entity.getId() == null) {
            return 0;
        }
        return spCommonMapper.update(entity);
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            return 0;
        }
        return spCommonMapper.deleteById(id);
    }
}
