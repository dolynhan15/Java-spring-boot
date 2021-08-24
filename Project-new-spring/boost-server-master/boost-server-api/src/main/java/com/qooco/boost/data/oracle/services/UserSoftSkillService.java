package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserSoftSkill;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 4:40 PM
*/
public interface UserSoftSkillService {

    List<UserSoftSkill> findByUserCurriculumVitaeId(long userCurriculumVitaeId);

    List<UserSoftSkill> save(List<UserSoftSkill> lstUserSoftSkill);

    UserSoftSkill save(UserSoftSkill UserSoftSkill);

    void deleteByUserCurriculumVitaeId(long curriculumVitaeId);

    void deleteByUserCurriculumVitaeIdExceptSoftSkillIds(long userCurriculumVitaeId, int[] lstSoftSkillId);

    List<UserSoftSkill> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds);

}
