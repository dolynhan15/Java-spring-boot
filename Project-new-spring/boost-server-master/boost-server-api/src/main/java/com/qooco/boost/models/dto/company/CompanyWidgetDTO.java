package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyWidgetDTO {
    private int vacancies;
    private int staffs;
    private int locations;
    private int statistics;

    public CompanyWidgetDTO(int vacancies, int staffs, int locations, int statistics) {
        this.vacancies = vacancies;
        this.staffs = staffs;
        this.locations = locations;
        this.statistics = statistics;
    }
}
