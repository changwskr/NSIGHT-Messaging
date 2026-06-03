package com.nh.nsight.messaging.xpilotstyleguide.ac.userac;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserApiResponse;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.as.useras.ASMSU73001;
import com.nh.nsight.messaging.xpilotstyleguide.as.useras.ASMSU74001;
import com.nh.nsight.messaging.xpilotstyleguide.zcommonutil.StyleGuideBizException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/xpilotstyleguide/users")
public class ACMSU73001 {

    private static final String AC = "ACMSU73001";

    private final ASMSU73001 asmsu73001;
    private final ASMSU74001 asmsu74001;

    public ACMSU73001(ASMSU73001 asmsu73001, ASMSU74001 asmsu74001) {
        this.asmsu73001 = asmsu73001;
        this.asmsu74001 = asmsu74001;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserApiResponse<UserProfileCDTO>> update(
            @PathVariable String userId,
            @RequestBody UserProfileCDTO userProfileCDTO) {
        System.out.println("★★★★★ [" + AC + "] update START userId=" + userId);
        userProfileCDTO.setUserId(userId);
        ResponseEntity<UserApiResponse<UserProfileCDTO>> result =
                ResponseEntity.ok(UserApiResponse.ok(asmsu73001.update(userProfileCDTO), "사용자정보가 수정되었습니다."));
        System.out.println("★★★★★ [" + AC + "] update END userId=" + userId);
        return result;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserApiResponse<Void>> delete(@PathVariable String userId) {
        System.out.println("★★★★★ [" + AC + "] delete START userId=" + userId);
        UserProfileCDTO criteria = new UserProfileCDTO();
        criteria.setUserId(userId);
        asmsu74001.delete(criteria);
        ResponseEntity<UserApiResponse<Void>> result =
                ResponseEntity.ok(UserApiResponse.ok(null, "사용자정보가 삭제되었습니다."));
        System.out.println("★★★★★ [" + AC + "] delete END userId=" + userId);
        return result;
    }

    @ExceptionHandler(StyleGuideBizException.class)
    public ResponseEntity<UserApiResponse<Void>> handleBiz(StyleGuideBizException ex) {
        System.out.println("★★★★★ [" + AC + "] handleBiz " + ex.getMessage());
        return ResponseEntity.badRequest().body(UserApiResponse.fail(ex.getMessage()));
    }
}
