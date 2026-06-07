package com.nh.nsight.messaging.zpilotfwk.comrc.dc.repository;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.entity.SpComrcEntity;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.util.SpComrcDcMapperUtil;
import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;
import com.nh.nsight.messaging.zpilotfwk.order.dc.repository.SpOrderRepository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** order와 동일 테이블(TB_ZPF_SP_ORDER)을 사용해 같은 값을 반환한다. */
@Repository
public class SpComrcRepositoryImpl implements SpComrcRepository {

    private final SpOrderRepository spOrderRepository;

    public SpComrcRepositoryImpl(SpOrderRepository spOrderRepository) {
        this.spOrderRepository = spOrderRepository;
    }

    @Override
    public SpComrcEntity insert(SpComrcEntity entity) {
        if (entity == null) {
            return null;
        }
        SpOrderEntity saved = spOrderRepository.insert(SpComrcDcMapperUtil.toOrderEntity(entity));
        return SpComrcDcMapperUtil.fromOrderEntity(saved);
    }

    @Override
    public Optional<SpComrcEntity> findById(Long id) {
        return spOrderRepository.findById(id).map(SpComrcDcMapperUtil::fromOrderEntity);
    }

    @Override
    public List<SpComrcEntity> findAll(SpComrc7201BIZDDTO condition) {
        SpOrder7101BIZDDTO orderCondition = SpComrcDcMapperUtil.toOrderDto(condition);
        List<SpComrcEntity> result = new ArrayList<>();
        for (SpOrderEntity entity : spOrderRepository.findAll(orderCondition)) {
            result.add(SpComrcDcMapperUtil.fromOrderEntity(entity));
        }
        return result;
    }

    @Override
    public long countAll(SpComrc7201BIZDDTO condition) {
        return spOrderRepository.countAll(SpComrcDcMapperUtil.toOrderDto(condition));
    }

    @Override
    public int update(SpComrcEntity entity) {
        if (entity == null || entity.getId() == null) {
            return 0;
        }
        return spOrderRepository.update(SpComrcDcMapperUtil.toOrderEntity(entity));
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            return 0;
        }
        return spOrderRepository.deleteById(id);
    }
}
