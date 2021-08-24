package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserFit;

import java.util.List;

public interface UserFitService {
    UserFit save(UserFit userProfile);

    List<UserFit> save(List<UserFit> userFits);

    UserFit findById(Long id);

    List<UserFit> findByIds(List<Long> userIds);

    void updateDefaultCompany(Long userId, Long companyId);

    boolean isExist(Long userId);
}
