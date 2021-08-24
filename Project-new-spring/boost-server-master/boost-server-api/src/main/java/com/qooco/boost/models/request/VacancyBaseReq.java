package com.qooco.boost.models.request;

import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 11:26 AM
 */

@Setter @Getter @NoArgsConstructor
public class VacancyBaseReq {
    private String logo;
    private Long companyId;
    private Long jobId;

    @Deprecated
    @ApiModelProperty(notes = "It is deprecated from v2")
    private Long cityId;
    @Deprecated
    @ApiModelProperty(notes = "It is deprecated from v2")
    private Long searchCityId;

    @ApiModelProperty(notes = "Applied in version 2, it is instead of cityId")
    private Long jobLocationId;
    @ApiModelProperty(notes = "Applied in version 2, it is instead of searchCityId")
    private Long searchLocationId;

    private Long contactPersonId;
    private Long educationId;
    private Long currencyId;
    private Integer numberOfSeat;
    private double salary;
    private double salaryMax;
    private boolean isHourSalary;
    private boolean isFullTime;
    private boolean isAsap;
    private Date expectedStartDate;
    private String shortDescription;
    private String fullDescription;

    protected long[] nativeLanguageIds;
    protected long[] languageIds;
    protected long[] workHourIds;
    protected long[] benefitIds;
    protected long[] softSkillIds;
    protected Long[] assessmentLevelIds;

    public Vacancy updateEntity(Vacancy vacancy, Long updatedOwner){
        vacancy.setLogo(ServletUriUtils.getRelativePath(this.logo));
        vacancy.setCompany(new Company(this.companyId));

        vacancy.setCity(new City(this.cityId));
        vacancy.setSearchCity(new City(this.searchCityId));

        vacancy.setJobLocation(new Location(jobLocationId));
        vacancy.setSearchLocation(new Location(searchLocationId));

        vacancy.setJob(new Job(this.jobId));
        vacancy.setContactPerson(new Staff(this.contactPersonId));
        vacancy.setEducation(new Education(this.educationId));
        vacancy.setNumberOfSeat(this.numberOfSeat);
        vacancy.setSalary(this.salary);
        vacancy.setHourSalary(this.isHourSalary);
        vacancy.setWorkingType(this.isFullTime);
        vacancy.setIsAsap(this.isAsap);
        vacancy.setShortDescription(this.shortDescription);
        vacancy.setFullDescription(this.fullDescription);
        vacancy.setCurrency(new Currency(this.currencyId));
        vacancy.setSalary(this.salary);
        vacancy.setSalaryMax(this.salaryMax);
        vacancy.setExpectedStartDate(DateUtils.toUtcForOracle(this.expectedStartDate));
        vacancy.setVacancyBenefits(getVacancyBenefits(vacancy, updatedOwner));
        vacancy.setVacancySoftSkills(getVacancySoftSkills(vacancy, updatedOwner));
        vacancy.setVacancyDesiredHours(getVacancyDesiredHours(vacancy, updatedOwner));
        vacancy.setVacancyLanguages(getVacancyLanguages(vacancy, updatedOwner));
        vacancy.setVacancyAssessmentLevels(getVacancyAssessmentLevels(vacancy, updatedOwner));
        return vacancy;
    }

    private List<VacancyBenefit> getVacancyBenefits(Vacancy vacancy, Long updatedOwner){
        List<VacancyBenefit> benefits = new ArrayList<>();
        VacancyBenefit benefit;
        for (Long id : this.benefitIds) {
            benefit = new VacancyBenefit(new Benefit(id), vacancy, updatedOwner);
            benefits.add(benefit);
        }
        return benefits;
    }

    private List<VacancyDesiredHour> getVacancyDesiredHours(Vacancy vacancy, Long updatedOwner){
        List<VacancyDesiredHour> desiredHours = new ArrayList<>();
        VacancyDesiredHour desiredHour;
        for (Long id : this.benefitIds) {
            desiredHour = new VacancyDesiredHour(new WorkingHour(id), vacancy, updatedOwner);
            desiredHours.add(desiredHour);
        }
        return desiredHours;
    }

    private List<VacancySoftSkill> getVacancySoftSkills(Vacancy vacancy, Long updatedOwner){
        List<VacancySoftSkill> softSkills = new ArrayList<>();
        VacancySoftSkill softSkill;
        for (Long id : this.softSkillIds) {
            softSkill = new VacancySoftSkill(new SoftSkill(id), vacancy, updatedOwner);
            softSkills.add(softSkill);
        }
        return softSkills;
    }

    private List<VacancyAssessmentLevel> getVacancyAssessmentLevels(Vacancy vacancy, Long updatedOwner){
        List<VacancyAssessmentLevel> vacancyAssessmentLevels = new ArrayList<>();
        VacancyAssessmentLevel vacancyAssessmentLevel;
        for (Long id : this.assessmentLevelIds) {
            vacancyAssessmentLevel = new VacancyAssessmentLevel(new AssessmentLevel(id), vacancy, updatedOwner);
            vacancyAssessmentLevels.add(vacancyAssessmentLevel);
        }
        return vacancyAssessmentLevels;
    }

    private List<VacancyLanguage> getVacancyLanguages(Vacancy vacancy, Long updatedOwner){
        List<VacancyLanguage> vacancyLanguages = new ArrayList<>();
        VacancyLanguage vacancyLanguage;
        for (Long id : this.languageIds) {
            vacancyLanguage = new VacancyLanguage(false, new Language(id), vacancy, updatedOwner);
            vacancyLanguages.add(vacancyLanguage);
        }

        for (Long id : this.nativeLanguageIds) {
            vacancyLanguage = new VacancyLanguage(true, new Language(id), vacancy, updatedOwner);
            vacancyLanguages.add(vacancyLanguage);
        }

        return vacancyLanguages;
    }
}
