package com.qooco.boost.threads.services.impl;

import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.threads.services.AppointmentActorService;
import com.qooco.boost.threads.services.UserProfileActorService;
import com.qooco.boost.threads.services.VacancyActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VacancyActorServiceImpl implements VacancyActorService {
    @Autowired
    private VacancyLanguageService vacancyLanguageService;
    @Autowired
    private VacancyDesiredHourService vacancyDesiredHourService;
    @Autowired
    private VacancySoftSkillService vacancySoftSkillService;
    @Autowired
    private VacancyBenefitService vacancyBenefitService;
    @Autowired
    private VacancyAssessmentLevelService vacancyAssessmentLevelService;
    @Autowired
    private UserProfileActorService userProfileActorService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AppointmentActorService appointmentActorService;

    @Override
    public Vacancy updateLazyValue(Vacancy vacancy) {
        if (Objects.nonNull(vacancy) && Objects.nonNull(vacancy.getId())) {
            List<VacancyLanguage> languages = vacancyLanguageService.findByVacancyId(vacancy.getId());
            List<VacancyDesiredHour> desiredHours = vacancyDesiredHourService.findByVacancyId(vacancy.getId());
            List<VacancySoftSkill> softSkills = vacancySoftSkillService.findByVacancyId(vacancy.getId());
            List<VacancyBenefit> benefits = vacancyBenefitService.findByVacancyId(vacancy.getId());
            List<VacancyAssessmentLevel> assessmentLevels = vacancyAssessmentLevelService.findByVacancyId(vacancy.getId());
            List<Appointment> appointments = appointmentService.findByVacancyId(vacancy.getId());
            UserFit userFit = userProfileActorService.updateLazyValue(vacancy.getContactPerson().getUserFit());

            vacancy.setVacancyLanguages(languages);
            vacancy.setVacancyDesiredHours(desiredHours);
            vacancy.setVacancySoftSkills(softSkills);
            vacancy.setVacancyBenefits(benefits);
            vacancy.setVacancyAssessmentLevels(assessmentLevels);
            vacancy.setVacancyAppointments(appointmentActorService.updateLazyValue(appointments));
            vacancy.getContactPerson().setUserFit(userFit);
            return vacancy;
        }
        return null;
    }



}
