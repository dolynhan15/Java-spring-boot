package com.qooco.boost.threads.models;

import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.oracle.entities.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SetRoleInMongo {
    private Staff staff;
    private CompanyRole oldRole;
    private CompanyRole newRole;

    public SetRoleInMongo(Staff staff, CompanyRole oldRole, CompanyRole newRole) {
        this.staff = staff;
        this.oldRole = oldRole;
        this.newRole = newRole;
    }
}
