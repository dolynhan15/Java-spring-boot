package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserSoftSkill;
import com.qooco.boost.data.oracle.reposistories.UserSoftSkillRepository;
import com.qooco.boost.data.oracle.services.UserSoftSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/13/2018 - 1:21 PM
*/
@Service
public class UserSoftSkillServiceImpl implements UserSoftSkillService {

    @Autowired
    private UserSoftSkillRepository userSoftSkillRepository;

    @Override
    public List<UserSoftSkill> findByUserCurriculumVitaeId(long userCurriculumVitaeId) {
        return userSoftSkillRepository.findByUserCurriculumVitaeId(userCurriculumVitaeId);
    }

    @Override
    public List<UserSoftSkill> save(List<UserSoftSkill> lstUserSoftSkill) {
        return Lists.newArrayList(userSoftSkillRepository.saveAll(lstUserSoftSkill));
    }

    @Override
    public UserSoftSkill save(UserSoftSkill userSoftSkill) {
        return userSoftSkillRepository.save(userSoftSkill);
    }

    @Override
    public void deleteByUserCurriculumVitaeId(long curriculumVitaeId) {
        userSoftSkillRepository.deleteByUserCurriculumVitaeId(curriculumVitaeId);
    }

    @Override
    public void deleteByUserCurriculumVitaeIdExceptSoftSkillIds(long userCurriculumVitaeId, int[] lstSoftSkillId) {
        userSoftSkillRepository.deleteByUserCurriculumVitaeIdExceptSoftSkillIds(userCurriculumVitaeId, lstSoftSkillId);
    }

    @Override
    public List<UserSoftSkill> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds) {
        return userSoftSkillRepository.findByUserCurriculumVitaeIds(userCurriculumVitaeIds);
    }
}
