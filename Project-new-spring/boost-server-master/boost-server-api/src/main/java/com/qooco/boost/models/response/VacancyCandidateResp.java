package com.qooco.boost.models.response;

import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.dto.user.CandidateInfoDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class VacancyCandidateResp {
    private VacancyShortInformationDTO vacancyShortInformation;
    private List<CandidateInfoDTO> candidateInfo;
    private long numberOfCandidate;
    private long numberOfAppointment;


    public VacancyCandidateResp(VacancyShortInformationDTO vacancyShortInformation,
                                List<CandidateInfoDTO> candidateInfo, long numberOfCandidate) {
        this.vacancyShortInformation = vacancyShortInformation;
        this.candidateInfo = candidateInfo;
        this.numberOfCandidate = numberOfCandidate;
    }

    public VacancyCandidateResp(VacancyShortInformationDTO vacancyShortInformation,
                                List<CandidateInfoDTO> candidateInfo,
                                long numberOfCandidate,
                                long numberOfAppointment) {
        this(vacancyShortInformation, candidateInfo, numberOfCandidate);
        this.numberOfAppointment = numberOfAppointment;

    }

    public VacancyCandidateResp(Vacancy vacancy, List<CandidateInfoDTO> candidateInfo, int numberOfAppointment, String locale) {
        if (Objects.nonNull(vacancy)) {
            this.vacancyShortInformation = new VacancyShortInformationDTO(vacancy, locale);
        }
        if (CollectionUtils.isNotEmpty(candidateInfo)) {
            this.candidateInfo = candidateInfo;
        }
        this.numberOfAppointment = numberOfAppointment;
    }
}
