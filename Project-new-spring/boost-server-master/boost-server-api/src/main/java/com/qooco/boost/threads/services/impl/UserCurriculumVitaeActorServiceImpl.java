package com.qooco.boost.threads.services.impl;

import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.threads.services.UserCurriculumVitaeActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 9/4/2018 - 4:57 PM
 */
@Service
public class UserCurriculumVitaeActorServiceImpl implements UserCurriculumVitaeActorService {

    @Autowired
    private UserDesiredHourService userDesiredHourService;
    @Autowired
    private UserBenefitService userBenefitService;
    @Autowired
    private CurriculumVitaeJobService curriculumVitaeJobService;
    @Autowired
    private UserSoftSkillService userSoftSkillService;
    @Autowired
    private UserPreferredHotelService userPreferredHotelService;

    @Override
    public UserCurriculumVitae updateLazyValue(UserCurriculumVitae userCurriculumVitae) {
        if(Objects.nonNull(userCurriculumVitae) && Objects.nonNull(userCurriculumVitae.getCurriculumVitaeId())) {
            var id = userCurriculumVitae.getCurriculumVitaeId();
            var desiredHours = userDesiredHourService.findByUserCurriculumVitaeId(id);
            var benefits = userBenefitService.findByUserCurriculumVitaeId(id);
            var curriculumVitaeJobs = curriculumVitaeJobService.findByUserCurriculumVitaeId(id);
            var softSkills = userSoftSkillService.findByUserCurriculumVitaeId(id);
            var preferredHotels = userPreferredHotelService.findByUserCurriculumVitaeId(id);

            userCurriculumVitae.setUserDesiredHours(desiredHours);
            userCurriculumVitae.setUserBenefits(benefits);
            userCurriculumVitae.setCurriculumVitaeJobs(curriculumVitaeJobs);
            userCurriculumVitae.setUserSoftSkills(softSkills);
            userCurriculumVitae.setPreferredHotels(preferredHotels);
            return userCurriculumVitae;
        }
        return null;
    }
}
