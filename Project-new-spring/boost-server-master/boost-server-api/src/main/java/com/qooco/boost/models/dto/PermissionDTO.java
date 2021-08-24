package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.Permission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionDTO {
    private Long permissionId;
    private String name;
    private String description;

    public PermissionDTO(Permission permission) {
        if (Objects.nonNull(permission)) {
            permissionId = permission.getPermissionId();
            name = permission.getName();
            description = permission.getDescription();
        }
    }
}
