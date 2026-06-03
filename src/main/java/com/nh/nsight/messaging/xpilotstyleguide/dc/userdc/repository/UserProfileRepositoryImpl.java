package com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.repository;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.UserProfile;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.mapper.UserProfileMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final UserProfileMapper userProfileMapper;

    public UserProfileRepositoryImpl(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public UserProfile findByUserId(String userId) {
        if (userId == null) {
            return null;
        }
        return userProfileMapper.selectByUserId(userId);
    }

    @Override
    public List<UserProfile> findAll(UserProfileDDTO criteria) {
        return userProfileMapper.selectAll(criteria);
    }

    @Override
    public long countAll(UserProfileDDTO criteria) {
        return userProfileMapper.countAll(criteria);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userId != null && userProfileMapper.countByUserId(userId) > 0;
    }

    @Override
    public int insert(UserProfile userProfile) {
        if (userProfile == null || userProfile.getUserId() == null) {
            return 0;
        }
        return userProfileMapper.insert(userProfile);
    }

    @Override
    public int update(UserProfile userProfile) {
        if (userProfile == null || userProfile.getUserId() == null) {
            return 0;
        }
        return userProfileMapper.update(userProfile);
    }

    @Override
    public int deleteByUserId(String userId) {
        if (userId == null) {
            return 0;
        }
        return userProfileMapper.deleteByUserId(userId);
    }
}
