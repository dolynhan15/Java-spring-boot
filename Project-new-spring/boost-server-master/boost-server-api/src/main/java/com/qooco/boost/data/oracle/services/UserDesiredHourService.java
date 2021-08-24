package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserDesiredHour;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 4:40 PM
*/
public interface UserDesiredHourService {
    List<UserDesiredHour> findByUserCurriculumVitaeId(long userCurriculumVitaeId);

    List<UserDesiredHour> save(List<UserDesiredHour> lstUserDesiredHour);

    UserDesiredHour save(UserDesiredHour userDesiredHour);

    void deleteByUserCurriculumVitaeId(long curriculumVitaeId);

    void deleteByUserCurriculumVitaeIdExceptWorkingHourIds(long userCurriculumVitaeId, int[] lstWorkingHourId);

    List<UserDesiredHour> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds);

}
