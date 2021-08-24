package com.qooco.boost.models.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.models.dto.*;
import com.qooco.boost.models.dto.appointment.AppointmentVacancyDTO;
import com.qooco.boost.models.dto.assessment.AssessmentDTO;
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.utils.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacancyDTO extends VacancyShortInformationDTO {

    @Deprecated
    private CityDTO searchCity;
    @Setter
    @Getter
    @ApiModelProperty(notes = "Applied in version 2, it is instead of searchCity")
    private LocationDTO searchLocation;

    private EducationDTO education;
    private boolean isAsap;
    private Date expectedStartDate;
    private String shortDescription;
    private String fullDescription;
    private Integer searchRange;

    private List<LanguageDTO> nativeLanguages;
    private List<LanguageDTO> languages;
    private List<WorkingHourDTO> desiredHours;
    private List<BenefitDTO> benefits;
    private List<SoftSkillDTO> softSkills;
    private List<AssessmentDTO> assessments;
    private List<AppointmentVacancyDTO> appointments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyDTO that = (VacancyDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String toString() {
        return getJob().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public VacancyDTO(Vacancy vacancy, String qoocoDomain, String locale) {
        super(vacancy, locale);
        setCompany(new CompanyDTO(vacancy.getCompany(), locale));
        this.searchCity = new CityDTO(vacancy.getSearchCity(), locale);
        this.searchLocation = new LocationDTO(vacancy.getSearchLocation(), locale);
        if (Objects.nonNull(vacancy.getExpectedStartDate())) {
            this.expectedStartDate = DateUtils.getUtcForOracle(vacancy.getExpectedStartDate());
        }
        this.fullDescription = vacancy.getFullDescription();
        this.shortDescription = vacancy.getShortDescription();
        this.education = EducationDTO.init(vacancy.getEducation(), locale);
        this.benefits = getBenefitDTOS(vacancy.getVacancyBenefits(), locale);
        this.desiredHours = getWorkingHourDTOS(vacancy.getVacancyDesiredHours());

        if (CollectionUtils.isNotEmpty(vacancy.getVacancyLanguages())) {
            List<VacancyLanguage> languages = vacancy.getVacancyLanguages().stream()
                    .filter(language -> !language.isNative()).collect(Collectors.toList());
            this.languages = getLanguageDTOS(languages, locale);

            List<VacancyLanguage> languagesNative = vacancy.getVacancyLanguages().stream()
                    .filter(VacancyLanguage::isNative).collect(Collectors.toList());
            this.nativeLanguages = getLanguageDTOS(languagesNative, locale);
        } else {
            this.nativeLanguages = new ArrayList<>();
            this.languages = new ArrayList<>();
        }

        this.softSkills = getSoftSkillDTOS(vacancy.getVacancySoftSkills(), locale);
        this.assessments = getAssessment(vacancy.getVacancyAssessmentLevels(), qoocoDomain);
        this.appointments = getAppointmentDTOS(vacancy.getVacancyAppointments(), locale);
        this.searchRange = vacancy.getSearchRange();

    }

    private List<BenefitDTO> getBenefitDTOS(List<VacancyBenefit> vacancyBenefits, String locale) {
        List<BenefitDTO> benefitDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(vacancyBenefits)) return benefitDTOS;
        vacancyBenefits.forEach(vb -> benefitDTOS.add(new BenefitDTO(vb.getBenefit(), locale)));
        return benefitDTOS;
    }

    private List<WorkingHourDTO> getWorkingHourDTOS(List<VacancyDesiredHour> vacancyDesiredHours) {
        List<WorkingHourDTO> workingHourDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(vacancyDesiredHours)) return workingHourDTOS;
        vacancyDesiredHours.forEach(wh -> workingHourDTOS.add(new WorkingHourDTO(wh.getWorkingHour())));
        return workingHourDTOS;
    }

    private List<LanguageDTO> getLanguageDTOS(List<VacancyLanguage> vacancyLanguages, String locale) {
        List<LanguageDTO> languageDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(vacancyLanguages)) return languageDTOS;
        vacancyLanguages.forEach(vl -> languageDTOS.add(new LanguageDTO(vl.getLanguage(), locale)));
        return languageDTOS;
    }

    private List<SoftSkillDTO> getSoftSkillDTOS(List<VacancySoftSkill> vacancySoftSkills, String locale) {
        List<SoftSkillDTO> softSkillDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(vacancySoftSkills)) return softSkillDTOS;
        vacancySoftSkills.forEach(vs -> softSkillDTOS.add(new SoftSkillDTO(vs.getSoftSkill(), locale)));
        return softSkillDTOS;
    }

    private List<AppointmentVacancyDTO> getAppointmentDTOS(List<Appointment> appointments, String locale) {
        if (CollectionUtils.isNotEmpty(appointments)) {
            return appointments.stream().filter(a -> !a.getIsDeleted()).map(it -> new AppointmentVacancyDTO(it, locale)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<AssessmentDTO> getAssessment(List<VacancyAssessmentLevel> vacancyAssessmentLevels, String qoocoDomain) {
        List<AssessmentDTO> assessmentDTOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(vacancyAssessmentLevels)) return assessmentDTOS;

        AssessmentDTO assessmentDTO;
        for (VacancyAssessmentLevel item : vacancyAssessmentLevels) {
            if (Objects.nonNull(item.getAssessmentLevel()) && Objects.nonNull(item.getAssessmentLevel().getAssessment())) {
                assessmentDTO = new AssessmentDTO(item.getAssessmentLevel().getAssessment(), qoocoDomain, Lists.newArrayList(item.getAssessmentLevel()));
                assessmentDTOS.add(assessmentDTO);
            }
        }
        return assessmentDTOS;
    }
}
