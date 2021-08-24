package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.SoftSkillEmbedded;
import com.qooco.boost.data.oracle.entities.SoftSkill;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.SoftSkillDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/13/2018 - 11:04 AM
*/

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoftSkillDTO {
    private Long softSkillId;
    private String name;
    private String description;

    public SoftSkillDTO(SoftSkill softSkill, String locale) {
        if (Objects.nonNull(softSkill)) {
            this.softSkillId = softSkill.getSoftSkillId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(SoftSkillDatabaseMessageSource.class).getMessage(softSkill.getSoftSkillId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = softSkill.getName();
            }
            this.description = name;
        }
    }

    public SoftSkillDTO(SoftSkillEmbedded softSkill, String locale) {
        if (Objects.nonNull(softSkill)) {
            this.softSkillId = softSkill.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(SoftSkillDatabaseMessageSource.class).getMessage(softSkill.getId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = softSkill.getName();
            }
            this.description = name;
        }
    }
}
