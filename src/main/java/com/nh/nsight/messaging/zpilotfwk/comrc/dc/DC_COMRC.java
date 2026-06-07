package com.nh.nsight.messaging.zpilotfwk.comrc.dc;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.entity.SpComrcEntity;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.repository.SpComrcRepository;
import com.nh.nsight.messaging.zpilotfwk.comrc.dc.util.SpComrcDcMapperUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DC_COMRC implements IDC_COMRC {

    private static final String DC = "DC_COMRC";

    private final SpComrcRepository spComrcRepository;

    public DC_COMRC(SpComrcRepository spComrcRepository) {
        this.spComrcRepository = spComrcRepository;
    }

    @Override
    public SpComrc7201BIZDDTO create(SpComrc7201BIZDDTO dto) {
        System.out.println("***** [" + DC + "] create START orderNo=" + (dto != null ? dto.getOrderNo() : null));
        validateWritable(dto);
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("CREATED");
        }

        SpComrcEntity saved = spComrcRepository.insert(SpComrcDcMapperUtil.toEntity(dto));
        SpComrc7201BIZDDTO result = SpComrcDcMapperUtil.toBizDto(saved);
        System.out.println("***** [" + DC + "] create END id=" + (result != null ? result.getId() : null));
        return result;
    }

    @Override
    public SpComrc7201BIZDDTO get(Long id) {
        if (id == null) {
            return null;
        }
        return spComrcRepository.findById(id)
                .map(SpComrcDcMapperUtil::toBizDto)
                .orElse(null);
    }

    @Override
    public List<SpComrc7201BIZDDTO> search(SpComrc7201BIZDDTO condition) {
        List<SpComrc7201BIZDDTO> result = new ArrayList<>();
        for (SpComrcEntity entity : spComrcRepository.findAll(condition)) {
            result.add(SpComrcDcMapperUtil.toBizDto(entity));
        }
        return result;
    }

    @Override
    public long count(SpComrc7201BIZDDTO condition) {
        return spComrcRepository.countAll(condition);
    }

    private void validateWritable(SpComrc7201BIZDDTO dto) {
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
