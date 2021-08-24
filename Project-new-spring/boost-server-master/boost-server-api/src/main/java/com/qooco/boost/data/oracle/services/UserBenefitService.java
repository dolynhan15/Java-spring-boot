package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserBenefit;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/13/2018 - 2:54 PM
*/
public interface UserBenefitService {
    List<UserBenefit> findByUserCurriculumVitaeId(long userCurriculumVitaeId);

    List<UserBenefit> save(List<UserBenefit> lstUserBenefit);

    UserBenefit save(UserBenefit UserBenefit);

    void deleteByUserCurriculumVitaeId(long curriculumVitaeId);

    void deleteByUserCurriculumVitaeIdExceptBenefitIds(long userCurriculumVitaeId, int[] benefitIds);

    List<UserBenefit> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds);
}
