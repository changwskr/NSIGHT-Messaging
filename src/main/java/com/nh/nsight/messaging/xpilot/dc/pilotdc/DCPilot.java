package com.nh.nsight.messaging.xpilot.dc.pilotdc;

import com.nh.nsight.messaging.traceenvironment.model.AssessmentRunView;
import com.nh.nsight.messaging.traceenvironment.model.IntegratedEnvironmentView;
import com.nh.nsight.messaging.traceenvironment.service.EnvironmentAssessmentService;
import com.nh.nsight.messaging.traceenvironment.service.TraceEnvironmentService;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.repository.PilotRepository;
import com.nh.nsight.messaging.xpilot.zcommonutil.PilotMapperUtil;
import com.nh.nsight.messaging.xpilot.zcommonutil.XpilotBizException;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pilot DC — 영속성 + traceenvironment 읽기 전용 위임(구조 변경 Pilot용).
 */
@Repository
public class DCPilot implements IDCPilot {

    private final PilotRepository pilotRepository;
    private final TraceEnvironmentService traceEnvironmentService;
    private final EnvironmentAssessmentService assessmentService;

    public DCPilot(
            PilotRepository pilotRepository,
            TraceEnvironmentService traceEnvironmentService,
            EnvironmentAssessmentService assessmentService
    ) {
        this.pilotRepository = pilotRepository;
        this.traceEnvironmentService = traceEnvironmentService;
        this.assessmentService = assessmentService;
    }

    @Override
    public PilotDDTO getPilot(PilotDDTO criteria) {
        if (criteria == null || criteria.getPilotId() == null) {
            return null;
        }
        return PilotMapperUtil.toDDto(pilotRepository.findByPilotId(criteria.getPilotId()));
    }

    @Override
    public void createPilot(PilotDDTO pilotDDTO) {
        if (pilotDDTO == null || pilotDDTO.getPilotId() == null || pilotDDTO.getPilotId().isBlank()) {
            throw new XpilotBizException("pilotId는 필수입니다.");
        }
        if (pilotRepository.existsByPilotId(pilotDDTO.getPilotId())) {
            throw new XpilotBizException("이미 존재하는 pilotId입니다: " + pilotDDTO.getPilotId());
        }
        Date now = new Date();
        if (pilotDDTO.getCreatedDate() == null) {
            pilotDDTO.setCreatedDate(now);
        }
        pilotDDTO.setUpdatedDate(now);
        if (pilotDDTO.getStatus() == null || pilotDDTO.getStatus().isBlank()) {
            pilotDDTO.setStatus("DRAFT");
        }
        if (pilotDDTO.getTargetModule() == null || pilotDDTO.getTargetModule().isBlank()) {
            pilotDDTO.setTargetModule("traceenvironment");
        }
        if (pilotDDTO.getSourceStructure() == null || pilotDDTO.getSourceStructure().isBlank()) {
            pilotDDTO.setSourceStructure("controller/service/model");
        }
        if (pilotDDTO.getTargetStructure() == null || pilotDDTO.getTargetStructure().isBlank()) {
            pilotDDTO.setTargetStructure("ac/as/dc/zcommonutil");
        }
        int rows = pilotRepository.insert(PilotMapperUtil.toEntity(pilotDDTO));
        if (rows == 0) {
            throw new XpilotBizException("Pilot 세션 생성에 실패했습니다.");
        }
    }

    @Override
    public List<PilotDDTO> listPilots(PilotDDTO criteria) {
        List<PilotDDTO> result = new ArrayList<>();
        for (Pilot pilot : pilotRepository.findList(criteria)) {
            result.add(PilotMapperUtil.toDDto(pilot));
        }
        return result;
    }

    @Override
    public Map<String, Object> loadEnvironmentDashboardSummary(String runId) {
        Map<String, Object> summary = new LinkedHashMap<>();
        if (runId == null || runId.isBlank()) {
            IntegratedEnvironmentView settings = traceEnvironmentService.loadIntegratedSettings();
            summary.put("mode", "settings-only");
            summary.put("matchCount", settings.matchCount());
            summary.put("warnCount", settings.warnCount());
            summary.put("totalCompared", settings.totalCompared());
            return summary;
        }
        AssessmentRunView run = assessmentService.getRun(runId);
        summary.put("runId", run.runId());
        summary.put("status", run.status());
        summary.put("passCount", run.passCount());
        summary.put("warnCount", run.warnCount());
        summary.put("failCount", run.failCount());
        summary.put("criticalBlocking", run.criticalBlocking());
        summary.put("timeoutChainValid", run.timeoutMap().chainValid());
        summary.put("concurrentFlowValid", run.concurrentFlowMap().chainValid());
        return summary;
    }

    @Override
    public Map<String, Object> loadEnvironmentSettingsSummary() {
        IntegratedEnvironmentView settings = traceEnvironmentService.loadIntegratedSettings();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("matchCount", settings.matchCount());
        summary.put("warnCount", settings.warnCount());
        summary.put("totalCompared", settings.totalCompared());
        summary.put("categoryCount", settings.categories() == null ? 0 : settings.categories().size());
        return summary;
    }
}
