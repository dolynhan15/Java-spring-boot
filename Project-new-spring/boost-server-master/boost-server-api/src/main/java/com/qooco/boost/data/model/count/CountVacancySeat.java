package com.qooco.boost.data.model.count;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CountVacancySeat {

    private Date countOnDate;
    private int closedSeats;
    private int openSeats;

    public CountVacancySeat(Date countOnDate, int closedSeats, int openSeats) {
        this.countOnDate = countOnDate;
        this.closedSeats = closedSeats;
        this.openSeats = openSeats;
    }
}
