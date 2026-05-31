package com.nh.nsight.messaging.traceoompgm.controller;

import com.nh.nsight.messaging.traceoompgm.config.OomInspectorProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Path;

@Controller
public class OomInspectorPageController {

    private final OomInspectorProperties properties;

    public OomInspectorPageController(OomInspectorProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/oominspector")
    public String scanPage(Model model) {
        model.addAttribute("defaultSourceRoot", Path.of(properties.getDefaultSourceRoot())
                .toAbsolutePath().normalize().toString());
        model.addAttribute("defaultMapperRoot", Path.of(properties.getDefaultMapperRoot())
                .toAbsolutePath().normalize().toString());
        model.addAttribute("defaultConfigPath", Path.of(properties.getDefaultConfigPath())
                .toAbsolutePath().normalize().toString());
        model.addAttribute("profileName", properties.getProfileName());
        return "oominspector/scan";
    }
}
