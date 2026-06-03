package com.nh.nsight.messaging.xpilotstyleguide.dc.userdc;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;

import java.util.List;

public interface IDCUserProfile {

    UserProfileDDTO getUserProfile(UserProfileDDTO criteria);

    void createUserProfile(UserProfileDDTO userProfile);

    void updateUserProfile(UserProfileDDTO userProfile);

    void deleteUserProfile(UserProfileDDTO userProfile);

    List<UserProfileDDTO> getListUserProfile(UserProfileDDTO criteria);

    long countUserProfile(UserProfileDDTO criteria);
}
