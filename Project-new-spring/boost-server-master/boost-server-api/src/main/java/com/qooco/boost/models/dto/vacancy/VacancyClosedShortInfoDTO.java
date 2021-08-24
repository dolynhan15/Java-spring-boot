package com.qooco.boost.models.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.dto.user.CandidateInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacancyClosedShortInfoDTO extends VacancyShortInformationDTO {
    private List<CandidateInfoDTO> candidates;

    public VacancyClosedShortInfoDTO(Vacancy vacancy, List<CandidateInfoDTO> candidates, String locale) {
        super(vacancy, locale);
        this.candidates = CollectionUtils.isNotEmpty(candidates) ? candidates : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyClosedShortInfoDTO that = (VacancyClosedShortInfoDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getJob().toString();
    }
}
