package com.nh.nsight.messaging.zpilotfwk.order.dc;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.order.dc.entity.SpOrderEntity;
import com.nh.nsight.messaging.zpilotfwk.order.dc.repository.SpOrderRepository;
import com.nh.nsight.messaging.zpilotfwk.order.dc.util.SpOrderDcMapperUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DC_ORDER implements IDC_ORDER {

    private static final String DC = "DC_ORDER";

    private final SpOrderRepository spOrderRepository;

    public DC_ORDER(SpOrderRepository spOrderRepository) {
        this.spOrderRepository = spOrderRepository;
    }

    @Override
    public SpOrder7101BIZDDTO create(SpOrder7101BIZDDTO dto) {
        System.out.println("***** [" + DC + "] create START orderNo=" + (dto != null ? dto.getOrderNo() : null));
        validateWritable(dto);
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("CREATED");
        }

        SpOrderEntity saved = spOrderRepository.insert(SpOrderDcMapperUtil.toEntity(dto));
        SpOrder7101BIZDDTO result = SpOrderDcMapperUtil.toBizDto(saved);
        System.out.println("***** [" + DC + "] create END id=" + (result != null ? result.getId() : null));
        return result;
    }

    @Override
    public SpOrder7101BIZDDTO get(Long id) {
        if (id == null) {
            return null;
        }
        return spOrderRepository.findById(id)
                .map(SpOrderDcMapperUtil::toBizDto)
                .orElse(null);
    }

    @Override
    public List<SpOrder7101BIZDDTO> search(SpOrder7101BIZDDTO condition) {
        List<SpOrder7101BIZDDTO> result = new ArrayList<>();
        for (SpOrderEntity entity : spOrderRepository.findAll(condition)) {
            result.add(SpOrderDcMapperUtil.toBizDto(entity));
        }
        return result;
    }

    @Override
    public long count(SpOrder7101BIZDDTO condition) {
        return spOrderRepository.countAll(condition);
    }

    private void validateWritable(SpOrder7101BIZDDTO dto) {
        if (dto == null) {
            throw new ZpilotFwkBizException("업무 데이터는 필수입니다.");
        }
        if (dto.getOrderNo() == null || dto.getOrderNo().isBlank()) {
            throw new ZpilotFwkBizException("orderNo는 필수입니다.");
        }
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new ZpilotFwkBizException("customerName은 필수입니다.");
        }
        if (dto.getAmount() == null) {
            throw new ZpilotFwkBizException("amount는 필수입니다.");
        }
    }
}
