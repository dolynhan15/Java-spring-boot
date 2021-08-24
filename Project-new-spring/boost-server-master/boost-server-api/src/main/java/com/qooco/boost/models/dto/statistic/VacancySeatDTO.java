package com.qooco.boost.models.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VacancySeatDTO {
   private int closedSeat;
   private int openSeat;
}
