package com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.mapper;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.UserProfile;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProfileMapper {

    boolean LOG_INITIALIZED = initializeLog();

    private static boolean initializeLog() {
        System.out.println("★★★★★ [UserProfileMapper] mapper interface loaded");
        return true;
    }

    UserProfile selectByUserId(@Param("userId") String userId);

    List<UserProfile> selectAll(@Param("criteria") UserProfileDDTO criteria);

    long countAll(@Param("criteria") UserProfileDDTO criteria);

    int countByUserId(@Param("userId") String userId);

    int insert(UserProfile userProfile);

    int update(UserProfile userProfile);

    int deleteByUserId(@Param("userId") String userId);
}
