package com.qooco.boost.models.response;

import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class CandidateFeedbackResp {
    private UserCurriculumVitaeDTO candidate;
    private VacancyShortInformationDTO vacancy;

    public CandidateFeedbackResp(Long userCVId, Long vacancyId) {
        this.candidate = new UserCurriculumVitaeDTO(userCVId);
        this.vacancy = new VacancyShortInformationDTO(vacancyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidate, vacancy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateFeedbackResp that = (CandidateFeedbackResp) o;
        return Objects.equals(candidate.getId(), that.candidate.getId())
                && Objects.equals(vacancy.getId(), that.vacancy.getId());
    }
}