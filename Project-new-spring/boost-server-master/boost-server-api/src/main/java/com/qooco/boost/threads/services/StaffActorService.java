package com.qooco.boost.threads.services;

import com.qooco.boost.data.oracle.entities.Staff;

import java.util.List;

public interface StaffActorService {
    Staff updateLazyValue(Staff staff);

    Staff updateLazyValue(Long userProfileId, Long companyId);

    List<Staff> updateLazyValue(List<Staff> staffs);
}
