package com.nh.nsight.messaging.zpilotfwk.order.dc.repository;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;
import com.nh.nsight.messaging.zpilotfwk.order.dc.mapper.SpOrderMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SpOrderRepositoryImpl implements SpOrderRepository {

    private final SpOrderMapper spOrderMapper;

    public SpOrderRepositoryImpl(SpOrderMapper spOrderMapper) {
        this.spOrderMapper = spOrderMapper;
    }

    @Override
    public SpOrderEntity insert(SpOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        spOrderMapper.insert(entity);
        return entity;
    }

    @Override
    public Optional<SpOrderEntity> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(spOrderMapper.selectById(id));
    }

    @Override
    public List<SpOrderEntity> findAll(SpOrder7101BIZDDTO condition) {
        SpOrder7101BIZDDTO criteria = condition != null ? condition : new SpOrder7101BIZDDTO();
        return spOrderMapper.selectAll(criteria);
    }

    @Override
    public long countAll(SpOrder7101BIZDDTO condition) {
        SpOrder7101BIZDDTO criteria = condition != null ? condition : new SpOrder7101BIZDDTO();
        return spOrderMapper.countAll(criteria);
    }

    @Override
    public int update(SpOrderEntity entity) {
        if (entity == null || entity.getId() == null) {
            return 0;
        }
        return spOrderMapper.update(entity);
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            return 0;
        }
        return spOrderMapper.deleteById(id);
    }
}
