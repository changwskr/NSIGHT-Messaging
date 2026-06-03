package com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.repository;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.UserProfile;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;

import java.util.List;

public interface UserProfileRepository {

    UserProfile findByUserId(String userId);

    List<UserProfile> findAll(UserProfileDDTO criteria);

    long countAll(UserProfileDDTO criteria);

    boolean existsByUserId(String userId);

    int insert(UserProfile userProfile);

    int update(UserProfile userProfile);

    int deleteByUserId(String userId);
}
