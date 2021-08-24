package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.EducationDTO;
import com.qooco.boost.models.dto.LanguageDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO extends UserDTO {
    private List<LanguageDTO> languages;
    private CityDTO city;
    private EducationDTO education;

    public UserProfileDTO(UserProfile userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            city = Objects.nonNull(userProfile.getCity()) ? new CityDTO(userProfile.getCity(), locale) : null;
            languages = getLanguageDTOFromEmbedded(userProfile.getUserLanguageList(), false, locale);
        }
    }

    public UserProfileDTO(UserFit userFit, String locale) {
        super(userFit, locale);
    }

    public UserProfileDTO(UserProfile userProfile, UserCurriculumVitae curriculumVitae, String locale) {
        this(userProfile, locale);
        if (Objects.nonNull(curriculumVitae)) {
            this.education = EducationDTO.init(curriculumVitae.getEducation(), locale);
        }
    }

    public UserProfileDTO(UserProfileEmbedded userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            city = Objects.nonNull(userProfile.getCity()) ? new CityDTO(userProfile.getCity(), locale) : null;
            languages = this.getLanguageDTOFromEmbedded(userProfile.getLanguages(), locale);
        }
    }

    public UserProfileDTO(UserProfileCvEmbedded userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            city = Objects.nonNull(userProfile.getCity()) ? new CityDTO(userProfile.getCity(), locale) : null;
            languages = this.getLanguageDTOFromEmbedded(userProfile.getLanguages(), locale);
        }
    }

    @Override
    public int calculateBasicProfileStrength() {
        int strength = 0;
        if (Objects.nonNull(getLastName()) && !getLastName().isEmpty()) {
            strength += 1;
        }
        if (CollectionUtils.isNotEmpty(getNativeLanguages())) {
            strength += 1;
        }
        return strength;
    }
}
