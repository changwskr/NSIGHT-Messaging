package com.nh.nsight.messaging.zpilotfwk.common.ac;

import com.nh.nsight.messaging.zpilotfwk.config.ZpilotFwkProperties;
import com.nh.nsight.messaging.zpilotfwk.common.ac.dto.SpCommonApiResponse;
import com.nh.nsight.messaging.zpilotfwk.common.ac.dto.SpCommon7001REQCDTO;
import com.nh.nsight.messaging.zpilotfwk.common.ac.dto.SpCommon7001RESCDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SP_COMMON 테스트 화면 — 실행 시 {@link AC_SP_COMMON}을 호출한다.
 */
@Controller
public class SpCommonPageController {

    private static final String AC = "SpCommonPageController";

    private final AC_SP_COMMON acSpCommon;
    private final ZpilotFwkProperties properties;

    public SpCommonPageController(AC_SP_COMMON acSpCommon, ZpilotFwkProperties properties) {
        this.acSpCommon = acSpCommon;
        this.properties = properties;
    }

    @GetMapping({ "/zpilotfwk", "/zpilotfwk/" })
    public String root() {
        return "redirect:/zpilotfwk/sp-common";
    }

    @GetMapping("/zpilotfwk/sp-common")
    public String spCommon(Model model) {
        System.out.println("\n\n 22222  ★★★★★ [" + AC + "] sp-common GET");
        model.addAttribute("defaultTransactionMode", properties.getTransaction().getDefaultMode());
        return "zpilotfwk/sp-common";
    }

    /**
     * 화면 [실행] — {@link AC_SP_COMMON#execute} 위임.
     */
    @PostMapping("/zpilotfwk/sp-common/execute")
    @ResponseBody
    public ResponseEntity<SpCommonApiResponse<SpCommon7001RESCDTO>> execute(
            @RequestBody(required = false) SpCommon7001REQCDTO request,
            @RequestParam(required = false) String transactionMode) {
        System.out.println("\n\n ★★★★★ [" + AC + "] execute → AC_SP_COMMON");
        return acSpCommon.execute(request, transactionMode);
    }
}
