package com.nh.nsight.messaging.xpilotstyleguide.ac.userac;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserApiResponse;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.as.useras.ASMSU71001;
import com.nh.nsight.messaging.xpilotstyleguide.util.StyleGuideBizException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotstyleguide/users")
public class ACMSU71001 {

    private static final String AC = "ACMSU71001";

    private final ASMSU71001 asmsu71001;

    public ACMSU71001(ASMSU71001 asmsu71001) {
        this.asmsu71001 = asmsu71001;
    }

    @PostMapping
    public ResponseEntity<UserApiResponse<UserProfileCDTO>> create(@RequestBody UserProfileCDTO userProfileCDTO) {
        System.out.println("★★★★★ [" + AC + "] create START userId="
                + (userProfileCDTO != null ? userProfileCDTO.getUserId() : null));
        UserProfileCDTO created = asmsu71001.create(userProfileCDTO);
        ResponseEntity<UserApiResponse<UserProfileCDTO>> result =
                ResponseEntity.ok(UserApiResponse.ok(created, "사용자정보가 생성되었습니다."));
        System.out.println("★★★★★ [" + AC + "] create END userId="
                + (created != null ? created.getUserId() : null));
        return result;
    }

    @ExceptionHandler(StyleGuideBizException.class)
    public ResponseEntity<UserApiResponse<Void>> handleBiz(StyleGuideBizException ex) {
        System.out.println("★★★★★ [" + AC + "] handleBiz " + ex.getMessage());
        return ResponseEntity.badRequest().body(UserApiResponse.fail(ex.getMessage()));
    }
}
