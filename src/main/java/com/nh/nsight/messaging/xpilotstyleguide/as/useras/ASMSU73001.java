package com.nh.nsight.messaging.xpilotstyleguide.as.useras;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDtoConverter;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.DCUserProfile;
import org.springframework.stereotype.Service;

@Service
public class ASMSU73001 {

    private static final String AS = "ASMSU73001";

    private final DCUserProfile dcUserProfile;

    public ASMSU73001(DCUserProfile dcUserProfile) {
        this.dcUserProfile = dcUserProfile;
    }

    public UserProfileCDTO update(UserProfileCDTO userProfileCDTO) {
        System.out.println("★★★★★ [" + AS + "] update START userId="
                + (userProfileCDTO != null ? userProfileCDTO.getUserId() : null));
        dcUserProfile.updateUserProfile(UserProfileCDtoConverter.toDDto(userProfileCDTO));
        System.out.println("★★★★★ [" + AS + "] update END userId="
                + (userProfileCDTO != null ? userProfileCDTO.getUserId() : null));
        return userProfileCDTO;
    }
}
