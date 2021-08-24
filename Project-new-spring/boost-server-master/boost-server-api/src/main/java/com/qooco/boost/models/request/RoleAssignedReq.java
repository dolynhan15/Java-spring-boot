package com.qooco.boost.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.data.enumeration.CompanyRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter @Getter @NoArgsConstructor
public class RoleAssignedReq {
    @JsonIgnore
    private Long staffId;
    @JsonIgnore
    private String messageId;

    @ApiModelProperty(notes = "The staff Id who is assigned all task from the guy is set role. It is null able")
    private Long assigneeId;
    private CompanyRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleAssignedReq that = (RoleAssignedReq) o;
        return staffId.equals(that.staffId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId);
    }
}
