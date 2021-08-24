package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.RoleCompanyEmbedded;
import com.qooco.boost.data.oracle.entities.RoleCompany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleCompanyDTO extends RoleCompanyBaseDTO{
    private Long roleId;
    private List<PermissionDTO> permissions;

    public RoleCompanyDTO(RoleCompany roleCompany, String locale) {
        super(roleCompany, locale);
        if (Objects.nonNull(roleCompany)) {
            roleId = roleCompany.getRoleId();
        }
    }

    public RoleCompanyDTO(RoleCompanyEmbedded roleCompany, String locale) {
        super(roleCompany, locale);
        if (Objects.nonNull(roleCompany)) {
            roleId = roleCompany.getRoleId();
        }
    }
}
