package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.RoleCompany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class RoleCompanyEmbedded {
    private Long roleId;
    private String name;
    private String displayName;

    public RoleCompanyEmbedded(Long id, String name) {
            this.roleId = id;
            this.name = name;
    }

    public RoleCompanyEmbedded(RoleCompany roleCompany) {
        if (Objects.nonNull(roleCompany)) {
            this.roleId = roleCompany.getRoleId();
            this.name = roleCompany.getName();
            this.displayName = roleCompany.getDisplayName();
        }
    }

    public RoleCompanyEmbedded(RoleCompanyEmbedded roleCompanyEmbedded) {
        if (Objects.nonNull(roleCompanyEmbedded)) {
            this.roleId = roleCompanyEmbedded.getRoleId();
            this.name = roleCompanyEmbedded.getName();
            this.displayName = roleCompanyEmbedded.getDisplayName();
        }
    }
}
