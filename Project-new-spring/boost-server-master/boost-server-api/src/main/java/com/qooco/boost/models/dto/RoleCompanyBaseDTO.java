package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.RoleCompanyEmbedded;
import com.qooco.boost.data.oracle.entities.RoleCompany;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.CompanyRoleDatabaseMessageSource;
import com.qooco.boost.localization.ProvinceDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleCompanyBaseDTO {
    private String name;
    private String displayName;

    public RoleCompanyBaseDTO(RoleCompany roleCompany, String locale) {
        if (Objects.nonNull(roleCompany)) {
            name = roleCompany.getName();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.displayName = ctx.getBean(CompanyRoleDatabaseMessageSource.class).getMessage(roleCompany.getRoleId().toString(), locale);
            }
            if (StringUtils.isBlank(displayName)) {
                this.displayName = roleCompany.getDisplayName();
            }
        }
    }

    public RoleCompanyBaseDTO(RoleCompanyEmbedded roleCompany, String locale) {
        if (Objects.nonNull(roleCompany)) {
            name = roleCompany.getName();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.displayName = ctx.getBean(CompanyRoleDatabaseMessageSource.class).getMessage(roleCompany.getRoleId().toString(), locale);
            }
            if (StringUtils.isBlank(displayName)) {
                this.displayName = roleCompany.getDisplayName();
            }
        }
    }
}
