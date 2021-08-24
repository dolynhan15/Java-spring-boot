package com.qooco.boost.models.dto.staff;

import com.qooco.boost.data.model.StaffStatistic;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.CountryDTO;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmployeeDTO {
    //StaffId
    private Long id;
    private String avatar;
    private String username;
    private String lastName;
    private String firstName;
    private String phone;
    private String nationalId;
    private CountryDTO country;

    public EmployeeDTO(Staff staff, String locale){
        this.id = staff.getStaffId();
        this.avatar = ServletUriUtils.getAbsolutePath(staff.getUserFit().getAvatar());
        this.username = staff.getUserFit().getUsername();
        this.lastName = staff.getUserFit().getLastName();
        this.firstName = staff.getUserFit().getFirstName();
        this.phone = staff.getUserFit().getPhoneNumber();
        this.nationalId = staff.getUserFit().getNationalId();
        if (Objects.nonNull(staff.getUserFit()) && Objects.nonNull(staff.getUserFit().getCountry())) {
            this.country = new CountryDTO(staff.getUserFit().getCountry(), locale);
        }
    }

    public EmployeeDTO(StaffStatistic staffStatistic, String locale) {
        this.id = staffStatistic.getId();
        this.avatar = ServletUriUtils.getAbsolutePath(staffStatistic.getAvatar());
        this.username = staffStatistic.getUsername();
        this.lastName = staffStatistic.getLastName();
        this.firstName = staffStatistic.getFirstName();
        this.phone = staffStatistic.getPhone();
        this.nationalId = staffStatistic.getNationalId();
        if (Objects.nonNull(staffStatistic.getCountry())) {
            this.country = new CountryDTO(staffStatistic.getCountry(), locale);
        }
    }
}
