package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Deprecated
@Setter @Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaitingCompanyDTO {
    private int type;
    private CompanyBaseDTO company;
}
