package com.nh.nsight.messaging.xpilotstyleguide.ac.userac;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserApiResponse;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.as.useras.ASMSU72001;
import com.nh.nsight.messaging.xpilotstyleguide.zcommonutil.StyleGuideBizException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/xpilotstyleguide/users")
public class ACMSU72001 {

    private static final String AC = "ACMSU72001";

    private final ASMSU72001 asmsu72001;

    public ACMSU72001(ASMSU72001 asmsu72001) {
        this.asmsu72001 = asmsu72001;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserApiResponse<UserProfileCDTO>> get(@PathVariable String userId) {
        System.out.println("★★★★★ [" + AC + "] get START userId=" + userId);
        ResponseEntity<UserApiResponse<UserProfileCDTO>> result =
                ResponseEntity.ok(UserApiResponse.ok(asmsu72001.get(userId)));
        System.out.println("★★★★★ [" + AC + "] get END userId=" + userId);
        return result;
    }

    @GetMapping
    public ResponseEntity<UserApiResponse<List<UserProfileCDTO>>> list(
            @ModelAttribute UserProfileCDTO criteria,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "3") Integer pageSize
    ) {
        System.out.println("★★★★★ [" + AC + "] list START pageNo=" + pageNo + " pageSize=" + pageSize);
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 3 : pageSize;
        List<UserProfileCDTO> users = asmsu72001.list(criteria, safePageNo, safePageSize);
        long total = asmsu72001.count(criteria);
        ResponseEntity<UserApiResponse<List<UserProfileCDTO>>> result =
                ResponseEntity.ok(UserApiResponse.okPage(users, total, safePageNo, safePageSize));
        System.out.println("★★★★★ [" + AC + "] list END total=" + total + " size=" + users.size());
        return result;
    }

    @ExceptionHandler(StyleGuideBizException.class)
    public ResponseEntity<UserApiResponse<Void>> handleBiz(StyleGuideBizException ex) {
        System.out.println("★★★★★ [" + AC + "] handleBiz " + ex.getMessage());
        return ResponseEntity.badRequest().body(UserApiResponse.fail(ex.getMessage()));
    }
}
