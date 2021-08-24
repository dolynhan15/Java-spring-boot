package com.qooco.boost.models.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CompanyDashboardDTO{
    private Map<String, VacancySeatDTO> seats;
    private List<BestEmployeeDTO> bestEmployees;
}
