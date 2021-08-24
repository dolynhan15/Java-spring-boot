package com.qooco.boost.models.dto.statistic;

import com.qooco.boost.data.model.StaffStatistic;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.staff.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BestEmployeeDTO {
    private EmployeeDTO staff;
    private int starNumber;

    public BestEmployeeDTO(Staff staff, int starNumber, String locale){
        this.staff = new EmployeeDTO(staff, locale);
        this.starNumber = starNumber;
    }

    public BestEmployeeDTO(StaffStatistic staffStatistic, int starNumber, String locale){
        this.staff = new EmployeeDTO(staffStatistic, locale);
        this.starNumber = starNumber;
    }
}
