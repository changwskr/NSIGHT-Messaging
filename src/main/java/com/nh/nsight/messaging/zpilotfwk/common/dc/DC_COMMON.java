package com.nh.nsight.messaging.zpilotfwk.common.dc;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;
import com.nh.nsight.messaging.zpilotfwk.common.dc.entity.SpCommonEntity;
import com.nh.nsight.messaging.zpilotfwk.common.dc.repository.SpCommonRepository;
import com.nh.nsight.messaging.zpilotfwk.common.dc.util.SpCommonDcMapperUtil;
import com.nh.nsight.messaging.zpilotfwk.tcf.ZpilotFwkBizException;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SP_COMMON DC(Data Component) — 업무 데이터 CRUD.
 * <p>
 * AS({@link com.nh.nsight.messaging.zpilotfwk.common.as.SP_COMMON})에서 호출한다.
 * </p>
 */
@Repository
public class DC_COMMON implements IDC_COMMON {

    private static final String DC = "DC_COMMON";

    private final SpCommonRepository spCommonRepository;

    public DC_COMMON(SpCommonRepository spCommonRepository) {
        this.spCommonRepository = spCommonRepository;
    }

    @Override
    public SpCommon7001BIZDDTO create(SpCommon7001BIZDDTO dto) {
        System.out.println("★★★★★ [" + DC + "] create START name=" + (dto != null ? dto.getName() : null));
        validateWritable(dto);
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        SpCommonEntity saved = spCommonRepository.insert(SpCommonDcMapperUtil.toEntity(dto));
        SpCommon7001BIZDDTO result = SpCommonDcMapperUtil.toBizDto(saved);
        System.out.println("★★★★★ [" + DC + "] create END id=" + (result != null ? result.getId() : null));
        return result;
    }

    @Override
    public SpCommon7001BIZDDTO get(Long id) {
        System.out.println("★★★★★ [" + DC + "] get START id=" + id);
        if (id == null) {
            return null;
        }
        SpCommon7001BIZDDTO result = spCommonRepository.findById(id)
                .map(SpCommonDcMapperUtil::toBizDto)
                .orElse(null);
        System.out.println("★★★★★ [" + DC + "] get END id=" + id);
        return result;
    }

    @Override
    public List<SpCommon7001BIZDDTO> search(SpCommon7001BIZDDTO condition) {
        System.out.println("★★★★★ [" + DC + "] search START");
        List<SpCommon7001BIZDDTO> result = new ArrayList<>();
        for (SpCommonEntity entity : spCommonRepository.findAll(condition)) {
            result.add(SpCommonDcMapperUtil.toBizDto(entity));
        }
        System.out.println("★★★★★ [" + DC + "] search END size=" + result.size());
        return result;
    }

    @Override
    public long count(SpCommon7001BIZDDTO condition) {
        System.out.println("★★★★★ [" + DC + "] count START");
        long total = spCommonRepository.countAll(condition);
        System.out.println("★★★★★ [" + DC + "] count END total=" + total);
        return total;
    }

    @Override
    public SpCommon7001BIZDDTO update(SpCommon7001BIZDDTO dto) {
        System.out.println("★★★★★ [" + DC + "] update START id=" + (dto != null ? dto.getId() : null));
        validateWritable(dto);
        if (dto.getId() == null) {
            throw new ZpilotFwkBizException("id는 필수입니다.");
        }
        SpCommonEntity existing = spCommonRepository.findById(dto.getId())
                .orElseThrow(() -> new ZpilotFwkBizException("데이터를 찾을 수 없습니다: id=" + dto.getId()));

        SpCommonEntity updated = SpCommonDcMapperUtil.toEntity(dto);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now());

        if (spCommonRepository.update(updated) == 0) {
            throw new ZpilotFwkBizException("수정에 실패했습니다: id=" + dto.getId());
        }
        SpCommon7001BIZDDTO result = get(dto.getId());
        System.out.println("★★★★★ [" + DC + "] update END id=" + dto.getId());
        return result;
    }

    @Override
    public void delete(Long id) {
        System.out.println("★★★★★ [" + DC + "] delete START id=" + id);
        if (id == null) {
            throw new ZpilotFwkBizException("id는 필수입니다.");
        }
        if (spCommonRepository.deleteById(id) == 0) {
            throw new ZpilotFwkBizException("데이터를 찾을 수 없습니다: id=" + id);
        }
        System.out.println("★★★★★ [" + DC + "] delete END id=" + id);
    }

    private void validateWritable(SpCommon7001BIZDDTO dto) {
        if (dto == null) {
            throw new ZpilotFwkBizException("업무 데이터는 필수입니다.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ZpilotFwkBizException("name은 필수입니다.");
        }
    }
}
