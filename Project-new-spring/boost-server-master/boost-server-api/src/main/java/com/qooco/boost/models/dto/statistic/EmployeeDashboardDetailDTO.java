package com.qooco.boost.models.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EmployeeDashboardDetailDTO {
    private Map<String, VacancySeatDTO> seats;
    private Map<String, ProcessedCandidateDTO> processedCandidates;
    private Map<String, AppointmentStatDTO> appointmentStats;
    private Map<String, Integer> activeMinutes;
}
