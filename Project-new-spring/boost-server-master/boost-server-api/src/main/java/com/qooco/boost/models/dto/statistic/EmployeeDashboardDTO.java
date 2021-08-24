package com.qooco.boost.models.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EmployeeDashboardDTO {
    private BestEmployeeDTO staff;
    private int closedSeats;
    private int openSeats;
    private int processedCandidates;
    private int appointments;
    private int activeMinutes;

    private int totalClosedSeats;
    private int totalOpenSeats;
    private int totalProcessedCandidates;
    private int totalAppointments;
    private int totalActiveMinutes;
}
