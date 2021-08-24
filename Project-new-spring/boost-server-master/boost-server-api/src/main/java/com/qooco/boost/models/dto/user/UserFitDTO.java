package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.oracle.entities.UserFit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFitDTO extends UserDTO {
    public UserFitDTO(UserFit userFit, String locale) {
        super(userFit, locale);
    }

    public UserFitDTO(UserProfileEmbedded embedded, String locale) {
        super(embedded, locale);
    }

    @Override
    public int calculateBasicProfileStrength() {
        int strength = 0;
        if (Objects.nonNull(getLastName()) && !getLastName().isEmpty()) {
            strength += 1;
        }
        if (CollectionUtils.isNotEmpty(getNativeLanguages())
                || Objects.nonNull(getCountry())
                || StringUtils.isNotBlank(getPhone())
                || StringUtils.isNotBlank(getAddress())
                || StringUtils.isNotBlank(getNationalId())
                || CollectionUtils.isNotEmpty(getPersonalPhotos())) {
            strength += 1;
        }
        return strength;
    }
}
