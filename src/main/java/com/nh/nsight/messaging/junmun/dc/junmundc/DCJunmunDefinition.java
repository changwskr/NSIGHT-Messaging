package com.nh.nsight.messaging.junmun.dc.junmundc;

import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.repository.JunmunDefinitionRepository;
import com.nh.nsight.messaging.junmun.util.JunmunBizException;
import com.nh.nsight.messaging.junmun.util.JunmunMapperUtil;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class DCJunmunDefinition implements IDCJunmunDefinition {

    private final JunmunDefinitionRepository repository;

    public DCJunmunDefinition(JunmunDefinitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public JunmunDefinitionDDTO get(String messageCode) {
        if (messageCode == null || messageCode.isBlank()) {
            return null;
        }
        return JunmunMapperUtil.toDDto(repository.findByMessageCode(messageCode.trim()));
    }

    @Override
    public List<JunmunDefinitionDDTO> list(JunmunDefinitionDDTO criteria) {
        return JunmunMapperUtil.toDDtoList(repository.findAll(criteria));
    }

    @Override
    public void create(JunmunDefinitionDDTO dto) {
        validateForSave(dto, true);
        if (repository.existsByMessageCode(dto.messageCode())) {
            throw new JunmunBizException("이미 등록된 전문코드입니다: " + dto.messageCode());
        }
        Date now = new Date();
        JunmunDefinition entity = JunmunMapperUtil.toEntity(dto);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        if (entity.getUseYn() == null) {
            entity.setUseYn("Y");
        }
        if (repository.insert(entity) == 0) {
            throw new JunmunBizException("전문 정의 등록에 실패했습니다.");
        }
    }

    @Override
    public void update(JunmunDefinitionDDTO dto) {
        validateForSave(dto, false);
        if (!repository.existsByMessageCode(dto.messageCode())) {
            throw new JunmunBizException("전문 정의를 찾을 수 없습니다: " + dto.messageCode());
        }
        JunmunDefinition entity = JunmunMapperUtil.toEntity(dto);
        entity.setUpdatedAt(new Date());
        if (repository.update(entity) == 0) {
            throw new JunmunBizException("전문 정의 수정에 실패했습니다.");
        }
    }

    @Override
    public void delete(String messageCode) {
        if (messageCode == null || messageCode.isBlank()) {
            throw new JunmunBizException("전문코드는 필수입니다.");
        }
        if (repository.deleteByMessageCode(messageCode.trim()) == 0) {
            throw new JunmunBizException("전문 정의를 찾을 수 없습니다: " + messageCode);
        }
    }

    private void validateForSave(JunmunDefinitionDDTO dto, boolean create) {
        if (dto == null || dto.messageCode() == null || dto.messageCode().isBlank()) {
            throw new JunmunBizException("전문코드는 필수입니다.");
        }
        if (dto.messageName() == null || dto.messageName().isBlank()) {
            throw new JunmunBizException("전문명은 필수입니다.");
        }
        if (dto.layoutJson() == null || dto.layoutJson().isBlank()) {
            throw new JunmunBizException("레이아웃 JSON은 필수입니다.");
        }
        if (dto.transactionId() == null || dto.transactionId().isBlank()) {
            throw new JunmunBizException("거래ID는 필수입니다.");
        }
        if (dto.serviceId() == null || dto.serviceId().isBlank()) {
            throw new JunmunBizException("서비스ID는 필수입니다.");
        }
        if (dto.direction() == null || !List.of("REQ", "RES").contains(dto.direction())) {
            throw new JunmunBizException("송수신구분은 REQ 또는 RES 이어야 합니다.");
        }
        if (create && (dto.createdBy() == null || dto.createdBy().isBlank())) {
            throw new JunmunBizException("등록자는 필수입니다.");
        }
        if (dto.updatedBy() == null || dto.updatedBy().isBlank()) {
            throw new JunmunBizException("수정자는 필수입니다.");
        }
    }
}
