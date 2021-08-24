package com.qooco.boost.data.model.count;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CountByDate {
    private Date eventDate;
    private int total;

    public CountByDate(Date eventDate, int total) {
        this.eventDate = eventDate;
        this.total = total;
    }
}
