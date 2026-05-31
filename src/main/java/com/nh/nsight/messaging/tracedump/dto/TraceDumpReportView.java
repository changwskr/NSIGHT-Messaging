package com.nh.nsight.messaging.tracedump.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TraceDumpReportView(
        LocalDateTime analyzedAt,
        String evidencePath,
        String oomCategory,
        FourStepGuide fourStepGuide,
        List<PipelineStep> analysisPipeline,
        KeyIndicatorsSection keyIndicators,
        OverviewSection overview,
        List<EvidenceRow> evidenceInventory,
        GcAnalysisSection gcAnalysis,
        HeapAnalysisSection heapAnalysis,
        ThreadAnalysisSection threadAnalysis,
        PrimaryJudgmentSection primaryJudgment,
        List<OomCorrelationRow> oomCorrelations,
        List<CauseHypothesisRow> causeHypotheses,
        List<ConclusionRow> causeConclusions,
        ActionPlanSection actionPlan
) {
    public record FourStepGuide(
            String step1Read,
            String step2Evidence,
            String step3Judgment,
            String step4Action
    ) {
    }

    public record PipelineStep(int order, String phase, String description) {
    }

    public record KeyIndicatorsSection(
            boolean javaHeapOom,
            int fullGcCount,
            boolean oldRegionNotReduced,
            boolean heapNotReducedAfterFullGc,
            boolean deadlockFound,
            int hikariWaitingThreads,
            int cruzApimWaitingThreads,
            int nmtThreadCount,
            boolean sessionCacheHint,
            boolean queryResultCacheHint,
            boolean heapDumpCollected
    ) {
    }

    public record OverviewSection(
            String occurredAt,
            String targetSystem,
            String apVm,
            String jvmOptions,
            String faultType,
            String userImpact
    ) {
    }

    public record EvidenceRow(String evidenceType, String fileName, String collectedAt, String note) {
    }

    public record GcAnalysisSection(
            String summary,
            int fullGcCount,
            long maxPauseMs,
            int oldRegionUnchangedCount,
            int heapUnchangedCount,
            List<String> watchPatterns
    ) {
    }

    public record PrimaryJudgmentSection(
            String heapUsage,
            String oldRegionGrowth,
            String fullGcRepeat,
            String deadlock,
            String hikariPoolWait,
            String nativeMemoryGrowth
    ) {
    }

    public record HeapAnalysisSection(
            String topRetainedObject,
            String suspectClasses,
            String gcRootPath,
            String leakCauseEstimate,
            List<String> heapDumpFiles
    ) {
    }

    public record ThreadAnalysisSection(
            int runnableCount,
            int waitingCount,
            int blockedCount,
            String deadlock,
            String dbPoolWait,
            String externalWait,
            List<ThreadDumpRow> dumps
    ) {
    }

    public record ThreadDumpRow(
            String sourceFile,
            int totalThreads,
            int runnable,
            int waiting,
            int blocked,
            boolean deadlock,
            int hikariWait,
            int jdbcWait,
            int cruzApimWait
    ) {
    }

    public record OomCorrelationRow(
            String id,
            String problemArea,
            String logEvidence,
            String relatedProgram,
            String probableCause,
            String evidenceFile,
            String severity
    ) {
    }

    public record CauseHypothesisRow(
            int priority,
            String causeName,
            String severity,
            String confidence,
            String evidence,
            String ruleIds,
            String recommendedAction
    ) {
    }

    public record ConclusionRow(String causeType, String judgment) {
    }

    public record ActionPlanSection(
            String immediate,
            String config,
            String source,
            String operations,
            String performanceTest
    ) {
    }
}
