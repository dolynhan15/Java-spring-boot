package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.oracle.entities.Location;
import com.qooco.boost.data.oracle.entities.Staff;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationFullDTO extends LocationDTO{

    private boolean isEditable;
    private boolean isDeletable;

    public LocationFullDTO(Location location, String locale) {
        super(location, locale);
        if (location.getIsPrimary() || location.getIsUsed()) {
            this.isDeletable = false;
            this.isEditable = false;
        } else {
            this.isDeletable = true;
            this.isEditable = true;
        }
    }

    public LocationFullDTO(Location location, Staff userStaff, String locale) {
        this(location, locale);
        if (Objects.nonNull(userStaff) && Objects.nonNull(userStaff.getRole())) {
            if (CompanyRole.ANALYST.getName().equals(userStaff.getRole().getName())
                    || CompanyRole.NORMAL_USER.getName().equals(userStaff.getRole().getName())) {
                this.isDeletable = false;
                this.isEditable = false;
            }
        }
    }

    @JsonProperty("isEditable")
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    @JsonProperty("isDeletable")
    public void setDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }
}
