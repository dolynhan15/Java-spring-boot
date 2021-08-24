package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.UserFitLanguage;
import com.qooco.boost.data.oracle.entities.UserLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class LanguageEmbedded {

    private Long id;
    private String name;
    private String code;

    public LanguageEmbedded(UserLanguage language) {
        if (Objects.nonNull(language)) {
            this.id = language.getId();
            if (Objects.nonNull(language.getLanguage())) {
                this.name = language.getLanguage().getName();
                this.code = language.getLanguage().getCode();
            }
        }
    }

    public LanguageEmbedded(UserFitLanguage language) {
        if (Objects.nonNull(language)) {
            this.id = language.getId();
            if (Objects.nonNull(language.getLanguage())) {
                this.name = language.getLanguage().getName();
                this.code = language.getLanguage().getCode();
            }
        }
    }
}
