package com.nh.nsight.messaging.operations.controller;

import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.operations.service.OperationsInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/operations")
public class OperationsApiController {

    private final OperationsInfoService operationsInfoService;

    public OperationsApiController(OperationsInfoService operationsInfoService) {
        this.operationsInfoService = operationsInfoService;
    }

    @GetMapping("/info")
    public StandardResponse<Map<String, Object>> info() {
        return StandardResponse.success("OPS-INFO-001", "operationsInfo", operationsInfoService.buildInfo());
    }
}
