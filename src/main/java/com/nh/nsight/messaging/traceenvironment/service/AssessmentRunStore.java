package com.nh.nsight.messaging.traceenvironment.service;

import com.nh.nsight.messaging.traceenvironment.model.AssessmentRunView;
import com.nh.nsight.messaging.traceenvironment.model.ConfigImportResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AssessmentRunStore {

    private final Map<String, AssessmentRunView> runs = new ConcurrentHashMap<>();
    private ConfigImportResult lastImport;

    public void save(AssessmentRunView run) {
        runs.put(run.runId(), run);
    }

    public Optional<AssessmentRunView> find(String runId) {
        return Optional.ofNullable(runs.get(runId));
    }

    public void saveImport(ConfigImportResult result) {
        this.lastImport = result;
    }

    public Optional<ConfigImportResult> lastImport() {
        return Optional.ofNullable(lastImport);
    }
}
