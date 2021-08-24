package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import com.qooco.boost.data.oracle.entities.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class CompanyBaseDTO {
    private Long id;
    private String name;

    public CompanyBaseDTO(@NotNull Company company) {
        this(company.getCompanyId(), company.getCompanyName());
    }

    public CompanyBaseDTO(CompanyDoc company) {
        this(company.getId(), company.getName());
    }

    public CompanyBaseDTO(CompanyEmbedded company) {
        this(company.getId(), company.getName());
    }

    public CompanyBaseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
