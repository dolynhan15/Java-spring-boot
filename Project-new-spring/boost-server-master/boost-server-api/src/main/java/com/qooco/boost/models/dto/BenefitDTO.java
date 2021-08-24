package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.BenefitEmbedded;
import com.qooco.boost.data.oracle.entities.Benefit;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.BenefitDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/10/2018 - 1:27 PM
*/

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BenefitDTO {
    private Long benefitId;
    private String description;
    private String name;

    public BenefitDTO(Benefit benefit, String locale) {
        if (Objects.nonNull(benefit)) {
            benefitId = benefit.getBenefitId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(BenefitDatabaseMessageSource.class).getMessage(benefit.getBenefitId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = benefit.getName();
            }
            description = name;
        }
    }
    public BenefitDTO(BenefitEmbedded benefit, String locale) {
        if (Objects.nonNull(benefit)) {
            benefitId = benefit.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(BenefitDatabaseMessageSource.class).getMessage(benefit.getId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = benefit.getName();
            }
            description = name;
        }
    }
}
