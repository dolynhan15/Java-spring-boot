package com.qooco.boost.models.request;

import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.request.appointment.AppointmentVacancyReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 11:26 AM
 */

@Setter
@Getter
public class VacancyV2Req extends VacancyBaseReq {
    private List<AppointmentVacancyReq> appointments;

    public Vacancy updateEntity(Vacancy vacancy, Long updatedOwner) {
        super.updateEntity(vacancy, updatedOwner);
        return vacancy;
    }

    public VacancyV2Req() {
        nativeLanguageIds = new long[0];
        languageIds = new long[0];
        workHourIds = new long[0];
        benefitIds = new long[0];
        softSkillIds = new long[0];
        assessmentLevelIds = new Long[0];
    }
}
