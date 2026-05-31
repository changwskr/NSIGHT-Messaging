package com.nh.nsight.messaging.tracedump.controller;

import com.nh.nsight.messaging.config.TraceDumpProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Path;

@Controller
public class TraceDumpPageController {

    private final TraceDumpProperties traceDumpProperties;

    public TraceDumpPageController(TraceDumpProperties traceDumpProperties) {
        this.traceDumpProperties = traceDumpProperties;
    }

    @GetMapping("/tracedump")
    public String traceDumpPage(Model model) {
        model.addAttribute("defaultEvidencePath", Path.of(traceDumpProperties.getEvidencePath())
                .toAbsolutePath().normalize().toString());
        return "tracedump/analyze";
    }
}
