package com.nh.nsight.messaging.xpilotstyleguide.as.useras;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDtoConverter;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.DCUserProfile;
import org.springframework.stereotype.Service;

@Service
public class ASMSU74001 {

    private static final String AS = "ASMSU74001";

    private final DCUserProfile dcUserProfile;

    public ASMSU74001(DCUserProfile dcUserProfile) {
        this.dcUserProfile = dcUserProfile;
    }

    public void delete(UserProfileCDTO userProfileCDTO) {
        System.out.println("★★★★★ [" + AS + "] delete START userId="
                + (userProfileCDTO != null ? userProfileCDTO.getUserId() : null));
        dcUserProfile.deleteUserProfile(UserProfileCDtoConverter.toDDto(userProfileCDTO));
        System.out.println("★★★★★ [" + AS + "] delete END userId="
                + (userProfileCDTO != null ? userProfileCDTO.getUserId() : null));
    }
}
