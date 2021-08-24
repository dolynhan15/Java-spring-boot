package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.staff.StaffDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 5/8/2019 - 2:59 PM
*/
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffMessageDTO extends StaffDTO {
    private Integer status;

    public StaffMessageDTO(Staff staff, Integer status, String locale) {
        super(staff, locale);
        this.status = status;
    }

    public StaffMessageDTO(StaffEmbedded staff, Integer status, String locale) {
        super(staff, locale);
        this.status = status;
    }
}
