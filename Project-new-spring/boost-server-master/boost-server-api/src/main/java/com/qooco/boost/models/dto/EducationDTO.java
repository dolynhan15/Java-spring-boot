package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.EducationEmbedded;
import com.qooco.boost.data.oracle.entities.Education;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.EducationDatabaseMessageSource;
import com.qooco.boost.localization.LanguageDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EducationDTO {
    private Long educationId;
    private String name;
    private String description;

    public static EducationDTO init(Education education, String locale){
        if (Objects.nonNull(education)) {
            return new EducationDTO(education, locale);
        }
        return null;
    }

    public static EducationDTO init(EducationEmbedded education, String locale){
        if (Objects.nonNull(education)) {
            return new EducationDTO(education, locale);
        }
        return null;
    }

    private EducationDTO(Education education, String locale) {
        if (Objects.nonNull(education)) {
            this.educationId = education.getEducationId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(EducationDatabaseMessageSource.class).getMessage(education.getEducationId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = education.getName();
            }
            this.description = name;
        }
    }

    private EducationDTO(EducationEmbedded education, String locale) {
        if (Objects.nonNull(education)) {
            this.educationId = education.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(LanguageDatabaseMessageSource.class).getMessage(education.getId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = education.getName();
            }
            this.description = name;
        }
    }
}
