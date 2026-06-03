package com.nh.nsight.messaging.xpilotstyleguide.as.useras;

import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDTO;
import com.nh.nsight.messaging.xpilotstyleguide.ac.userac.dto.UserProfileCDtoConverter;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.DCUserProfile;
import com.nh.nsight.messaging.xpilotstyleguide.dc.userdc.dto.UserProfileDDTO;
import com.nh.nsight.messaging.xpilotstyleguide.util.StyleGuideBizException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ASMSU72001 {

    private static final String AS = "ASMSU72001";

    private final DCUserProfile dcUserProfile;

    public ASMSU72001(DCUserProfile dcUserProfile) {
        this.dcUserProfile = dcUserProfile;
    }

    public UserProfileCDTO get(String userId) {
        System.out.println("★★★★★ [" + AS + "] get START userId=" + userId);
        UserProfileDDTO criteria = new UserProfileDDTO();
        criteria.setUserId(userId);
        UserProfileDDTO found = dcUserProfile.getUserProfile(criteria);
        if (found == null) {
            throw new StyleGuideBizException("사용자를 찾을 수 없습니다: " + userId);
        }
        UserProfileCDTO result = UserProfileCDtoConverter.toCDto(found);
        System.out.println("★★★★★ [" + AS + "] get END userId=" + userId);
        return result;
    }

    public List<UserProfileCDTO> list(UserProfileCDTO criteria, Integer pageNo, Integer pageSize) {
        System.out.println("★★★★★ [" + AS + "] list START pageNo=" + pageNo + " pageSize=" + pageSize);
        UserProfileDDTO condition = UserProfileCDtoConverter.toDDto(criteria);
        if (condition == null) {
            condition = new UserProfileDDTO();
        }
        condition.setPageNo(pageNo);
        condition.setPageSize(pageSize);
        List<UserProfileCDTO> result =
                UserProfileCDtoConverter.toCDtoList(dcUserProfile.getListUserProfile(condition));
        System.out.println("★★★★★ [" + AS + "] list END size=" + result.size());
        return result;
    }

    public long count(UserProfileCDTO criteria) {
        System.out.println("★★★★★ [" + AS + "] count START");
        UserProfileDDTO condition = UserProfileCDtoConverter.toDDto(criteria);
        if (condition == null) {
            condition = new UserProfileDDTO();
        }
        long total = dcUserProfile.countUserProfile(condition);
        System.out.println("★★★★★ [" + AS + "] count END total=" + total);
        return total;
    }
}
