package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class BaseUserCVDTO {
    private Long id;
    private ShortUserDTO userProfile;

    protected BaseUserCVDTO(UserProfileCvEmbedded cvEmbedded, String locale) {
        ofNullable(cvEmbedded).ifPresent(it -> {
            this.id = it.getUserProfileCvId();
            this.userProfile = new ShortUserDTO(it, locale);
        });
    }

    protected BaseUserCVDTO(UserCvDoc userCvDoc, String locale) {
        ofNullable(userCvDoc).ifPresent(it -> {
            this.id = it.getId();
            this.userProfile = new ShortUserDTO(it.getUserProfile(), locale);
        });
    }

    public BaseUserCVDTO(UserCurriculumVitae curriculumVitae, String locale) {
        ofNullable(curriculumVitae).ifPresent(it -> {
            this.id = it.getCurriculumVitaeId();
            this.userProfile = new ShortUserDTO(it.getUserProfile(), locale);
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUserCVDTO that = (BaseUserCVDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
