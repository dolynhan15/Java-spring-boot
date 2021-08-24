package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserDesiredHour;
import com.qooco.boost.data.oracle.reposistories.UserDesiredHourRepository;
import com.qooco.boost.data.oracle.services.UserDesiredHourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 4:47 PM
*/
@Service
public class UserDesiredHourServiceImpl implements UserDesiredHourService {

    @Autowired
    private UserDesiredHourRepository userDesiredHourRepository;

    @Override
    public List<UserDesiredHour> findByUserCurriculumVitaeId(long userCurriculumVitaeId) {
        return userDesiredHourRepository.findByUserCurriculumVitaeId(userCurriculumVitaeId);
    }

    @Override
    public List<UserDesiredHour> save(List<UserDesiredHour> lstUserDesiredHour) {
        return Lists.newArrayList(userDesiredHourRepository.saveAll(lstUserDesiredHour));
    }

    @Override
    public UserDesiredHour save(UserDesiredHour userDesiredHour) {
        return userDesiredHourRepository.save(userDesiredHour);
    }

    @Override
    public void deleteByUserCurriculumVitaeId(long curriculumVitaeId) {
        userDesiredHourRepository.deleteByUserCurriculumVitaeId(curriculumVitaeId);
    }

    @Override
    public void deleteByUserCurriculumVitaeIdExceptWorkingHourIds(long userCurriculumVitaeId, int[] lstWorkingHourId) {
        userDesiredHourRepository.deleteByUserCurriculumVitaeIdExceptWorkingHourIds(
                userCurriculumVitaeId, lstWorkingHourId);
    }

    @Override
    public List<UserDesiredHour> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds) {
        return userDesiredHourRepository.findByUserCurriculumVitaeIds(userCurriculumVitaeIds);
    }
}
