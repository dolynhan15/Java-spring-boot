package com.qooco.boost.models.response;

import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CandidatesResp {

    private List<UserCurriculumVitaeDTO> userCurriculumVitaes;
    private Integer maxExperience;
    private Double maxSalary;
    private boolean hasMore;
}
