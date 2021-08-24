package com.qooco.boost.threads.services.impl;

import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.threads.services.StaffActorService;
import com.qooco.boost.threads.services.UserProfileActorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffActorServiceImpl implements StaffActorService {
    @Autowired
    private UserProfileActorService userProfileActorService;
    @Autowired
    private StaffService staffService;

    @Override
    public Staff updateLazyValue(Staff staff) {
        if (Objects.nonNull(staff)) {
            if (Objects.nonNull(staff.getUserFit())) {
                UserFit userFit = userProfileActorService.updateLazyValue(staff.getUserFit());
                staff.setUserFit(userFit);
            }
            return staff;
        }
        return null;
    }

    @Override
    public Staff updateLazyValue(Long userProfileId, Long companyId) {
        if (Objects.nonNull(userProfileId) && Objects.nonNull(companyId)) {
            List<Staff> createdBy = staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId);
            return updateLazyValue(createdBy.get(0));
        }
        return null;
    }

    @Override
    public List<Staff> updateLazyValue(List<Staff> staffs) {
        List<Staff> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(staffs)) {
            List<UserFit> userFits = staffs.stream().map(Staff::getUserFit).collect(Collectors.toList());
            List<UserFit> profiles = userProfileActorService.updateLazyValueUserFit(userFits);
            staffs.forEach(s -> {
                Optional<UserFit> optionalUser = profiles.stream().filter(u -> u.getUserProfileId().equals(s.getUserFit().getUserProfileId())).findFirst();
                s.setUserFit(optionalUser.orElse(null));
                result.add(s);
            });
        }
        return result;
    }
}
