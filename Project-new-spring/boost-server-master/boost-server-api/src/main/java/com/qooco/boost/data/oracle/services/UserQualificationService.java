package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserQualification;

import java.util.Date;
import java.util.List;

public interface UserQualificationService {
    List<UserQualification> findByUserProfileIdAndScaleId(Long userProfileId, String scaleId);

    List<UserQualification> findByUserProfileId(Long userProfileId);

    List<UserQualification> findByUserProfileIdAndScaleIdForHomePage(Long userProfileId, String scaleId);

    List<UserQualification> findByUserProfileIdForHomePage(Long userProfileId);

    UserQualification findById(Long id);

    UserQualification save(UserQualification qualification);

    List<UserQualification> save(List<UserQualification> qualification);

    int countValidQualification(Long userProfileId, Date submissionValidDate);

    List<UserQualification> findValidQualification(Long userProfileId, Date submissionValidDate);

    boolean exists(Long id);
}
