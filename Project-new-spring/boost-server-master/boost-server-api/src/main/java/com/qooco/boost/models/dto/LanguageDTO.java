package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.LanguageEmbedded;
import com.qooco.boost.data.oracle.entities.Language;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.LanguageDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

@Setter @Getter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanguageDTO {
    private Long languageId;
    private String name;
    private String code;

    public LanguageDTO(Language language, String locale) {
        if (Objects.nonNull(language)) {
            this.languageId = language.getLanguageId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(LanguageDatabaseMessageSource.class).getMessage(language.getLanguageId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = language.getName();
            }
            this.code = language.getCode();
        }
    }

    public LanguageDTO(LanguageEmbedded language, String locale) {
        if (Objects.nonNull(language)) {
            this.languageId = language.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(LanguageDatabaseMessageSource.class).getMessage(language.getId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = language.getName();
            }
            this.code = language.getCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageDTO that = (LanguageDTO) o;
        return Objects.equals(languageId, that.languageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageId);
    }
}
