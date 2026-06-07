package com.nh.nsight.messaging.zpilotfwk.tcf.routing;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;
import com.nh.nsight.messaging.zpilotfwk.tcf.ISpService;

import org.springframework.stereotype.Component;

@Component
public class SpServiceRegistry {

    private final Map<String, ISpService> servicesById;

    public SpServiceRegistry(List<ISpService> services) {
        this.servicesById = services.stream()
                .collect(Collectors.toUnmodifiableMap(ISpService::serviceId, Function.identity(),
                        (left, right) -> {
                            throw new IllegalStateException("Duplicate ISpService serviceId: " + left.serviceId());
                        }));
    }

    public ISpService resolve(EPlatonEvent event) {
        String serviceId = SpRouteKeyResolver.serviceId(event);
        ISpService service = servicesById.get(serviceId);
        if (service == null) {
            throw new SpServiceRoutingException("ETCF0003",
                    "No ISpService registered for serviceId=" + serviceId);
        }
        return service;
    }
}
