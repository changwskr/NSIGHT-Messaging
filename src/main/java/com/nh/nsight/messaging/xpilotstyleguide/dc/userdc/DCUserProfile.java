package com.nh.nsight.messaging.xpilotstyleguide.dc.userdc;

import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.repository.UserProfileRepository;
import com.nh.nsight.messaging.xpilotstyleguide.util.StyleGuideBizException;
import com.nh.nsight.messaging.xpilotstyleguide.util.UserProfileMapperUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DCUserProfile implements IDCUserProfile {

    private static final String DC = "DCUserProfile";

    private final UserProfileRepository userProfileRepository;

    public DCUserProfile(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfileDDTO getUserProfile(UserProfileDDTO criteria) {
        System.out.println("★★★★★ [" + DC + "] getUserProfile START userId="
                + (criteria != null ? criteria.getUserId() : null));
        if (criteria == null || criteria.getUserId() == null) {
            return null;
        }
        UserProfile userProfile = userProfileRepository.findByUserId(criteria.getUserId());
        UserProfileDDTO result = UserProfileMapperUtil.toDDto(userProfile);
        System.out.println("★★★★★ [" + DC + "] getUserProfile END userId=" + criteria.getUserId());
        return result;
    }

    @Override
    public void createUserProfile(UserProfileDDTO userProfileDDTO) {
        System.out.println("★★★★★ [" + DC + "] createUserProfile START userId="
                + (userProfileDDTO != null ? userProfileDDTO.getUserId() : null));
        if (userProfileDDTO == null || userProfileDDTO.getUserId() == null) {
            throw new StyleGuideBizException("사용자ID는 필수입니다.");
        }
        if (userProfileRepository.existsByUserId(userProfileDDTO.getUserId())) {
            throw new StyleGuideBizException("이미 존재하는 사용자ID입니다: " + userProfileDDTO.getUserId());
        }
        Date now = new Date();
        if (userProfileDDTO.getCreatedDate() == null) {
            userProfileDDTO.setCreatedDate(now);
        }
        userProfileDDTO.setUpdatedDate(now);
        int rows = userProfileRepository.insert(UserProfileMapperUtil.toEntity(userProfileDDTO));
        if (rows == 0) {
            throw new StyleGuideBizException("사용자 생성에 실패했습니다.");
        }
        System.out.println("★★★★★ [" + DC + "] createUserProfile END userId=" + userProfileDDTO.getUserId());
    }

    @Override
    public void updateUserProfile(UserProfileDDTO userProfileDDTO) {
        System.out.println("★★★★★ [" + DC + "] updateUserProfile START userId="
                + (userProfileDDTO != null ? userProfileDDTO.getUserId() : null));
        if (userProfileDDTO == null || userProfileDDTO.getUserId() == null) {
            throw new StyleGuideBizException("사용자ID는 필수입니다.");
        }
        UserProfile existing = userProfileRepository.findByUserId(userProfileDDTO.getUserId());
        if (existing == null) {
            throw new StyleGuideBizException("사용자를 찾을 수 없습니다: " + userProfileDDTO.getUserId());
        }
        UserProfile updated = UserProfileMapperUtil.toEntity(userProfileDDTO);
        updated.setCreatedDate(existing.getCreatedDate());
        updated.setUpdatedDate(new Date());
        int rows = userProfileRepository.update(updated);
        if (rows == 0) {
            throw new StyleGuideBizException("사용자 수정에 실패했습니다.");
        }
        System.out.println("★★★★★ [" + DC + "] updateUserProfile END userId=" + userProfileDDTO.getUserId());
    }

    @Override
    public void deleteUserProfile(UserProfileDDTO userProfileDDTO) {
        System.out.println("★★★★★ [" + DC + "] deleteUserProfile START userId="
                + (userProfileDDTO != null ? userProfileDDTO.getUserId() : null));
        if (userProfileDDTO == null || userProfileDDTO.getUserId() == null) {
            throw new StyleGuideBizException("사용자ID는 필수입니다.");
        }
        int rows = userProfileRepository.deleteByUserId(userProfileDDTO.getUserId());
        if (rows == 0) {
            throw new StyleGuideBizException("사용자를 찾을 수 없습니다: " + userProfileDDTO.getUserId());
        }
        System.out.println("★★★★★ [" + DC + "] deleteUserProfile END userId=" + userProfileDDTO.getUserId());
    }

    @Override
    public List<UserProfileDDTO> getListUserProfile(UserProfileDDTO criteria) {
        System.out.println("★★★★★ [" + DC + "] getListUserProfile START pageNo="
                + (criteria != null ? criteria.getSafePageNo() : null));
        List<UserProfileDDTO> result = new ArrayList<>();
        for (UserProfile userProfile : userProfileRepository.findAll(criteria)) {
            result.add(UserProfileMapperUtil.toDDto(userProfile));
        }
        System.out.println("★★★★★ [" + DC + "] getListUserProfile END size=" + result.size());
        return result;
    }

    @Override
    public long countUserProfile(UserProfileDDTO criteria) {
        System.out.println("★★★★★ [" + DC + "] countUserProfile START");
        long total = userProfileRepository.countAll(criteria);
        System.out.println("★★★★★ [" + DC + "] countUserProfile END total=" + total);
        return total;
    }
}
