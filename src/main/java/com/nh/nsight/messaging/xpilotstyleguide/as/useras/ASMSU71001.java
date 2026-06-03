package com.nh.nsight.messaging.xpilotstyleguide.as.useras;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDtoConverter;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.DCUserProfile;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;
import com.nh.nsight.messaging.xpilotstyleguide.util.StyleGuideBizException;

import org.springframework.stereotype.Service;

@Service
public class ASMSU71001 {

    private static final String AS = "ASMSU71001";

    private final DCUserProfile dcUserProfile;

    public ASMSU71001(DCUserProfile dcUserProfile) {
        this.dcUserProfile = dcUserProfile;
    }

    public UserProfileCDTO create(UserProfileCDTO userProfileCDTO) {
        System.out.println("★★★★★ [" + AS + "] create START userId="
                + (userProfileCDTO != null ? userProfileCDTO.getUserId() : null));
        validateUserId(userProfileCDTO);
        UserProfileDDTO ddto = UserProfileCDtoConverter.toDDto(userProfileCDTO);
        dcUserProfile.createUserProfile(ddto);
        UserProfileDDTO criteria = new UserProfileDDTO();
        criteria.setUserId(ddto.getUserId());
        UserProfileCDTO result = UserProfileCDtoConverter.toCDto(dcUserProfile.getUserProfile(criteria));
        System.out.println("★★★★★ [" + AS + "] create END userId="
                + (result != null ? result.getUserId() : null));
        return result;
    }

    private void validateUserId(UserProfileCDTO userProfileCDTO) {
        if (userProfileCDTO == null || userProfileCDTO.getUserId() == null
                || userProfileCDTO.getUserId().isBlank()) {
            throw new StyleGuideBizException("사용자ID는 필수 입력입니다.");
        }
    }
}
