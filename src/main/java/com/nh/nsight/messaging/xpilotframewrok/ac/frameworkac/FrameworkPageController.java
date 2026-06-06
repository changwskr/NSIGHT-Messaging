package com.nh.nsight.messaging.xpilotframewrok.ac.frameworkac;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AC(Application Controller) 계층 — xpilotFramework 로그 조회 화면(MVC) 컨트롤러.
 * REST API가 아닌 Thymeleaf 등 서버 사이드 뷰 렌더링을 담당한다.
 */
@Controller
public class FrameworkPageController {

    /** 로그 출력용 컨트롤러 식별자 */
    private static final String AC = "FrameworkPageController";

    /**
     * xpilotFramework 로그 조회 페이지 뷰 이름을 반환한다.
     *
     * @return 뷰 템플릿 경로 (xpilotframewrok/logs)
     */
    @GetMapping("/xpilotframewrok/logs")
    public String logsPage() {
        // START/END 로그 (단순 뷰 라우팅)
        System.out.println("★★★★★★★ [" + AC + "] logsPage START/END");
        return "xpilotframewrok/logs";
    }
}
