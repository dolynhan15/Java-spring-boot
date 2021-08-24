package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.enumeration.Gender;
import com.qooco.boost.data.mongo.embedded.LanguageEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserFitLanguage;
import com.qooco.boost.data.oracle.entities.UserLanguage;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.models.dto.CountryDTO;
import com.qooco.boost.models.dto.LanguageDTO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class UserDTO extends ShortUserDTO {
    private Gender gender;
    private Date birthday;
    private String email;
    private List<LanguageDTO> nativeLanguages;
    private CountryDTO country;

    private Integer profileStep;
    private Date updatedDate;

    abstract int calculateBasicProfileStrength();

    public Integer getProfileStrength() {
        return calculateBasicProfileStrength();
    }

    public UserDTO(UserProfile userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            this.gender = userProfile.getGender();
            this.birthday = DateUtils.getUtcForOracle(userProfile.getBirthday());
            this.email = userProfile.getEmail();
            this.updatedDate = DateUtils.getUtcForOracle(userProfile.getUpdatedDate());
            this.profileStep = userProfile.getProfileStep();
            nativeLanguages = getLanguageDTOFromEmbedded(userProfile.getUserLanguageList(), true, locale);
        }
    }

    public UserDTO(UserFit userFit, String locale) {
        super(userFit);
        if (Objects.nonNull(userFit)) {
            this.setAddress(userFit.getAddress());
            this.setPhone(userFit.getPhoneNumber());
            this.setNationalId(userFit.getNationalId());
            this.setPersonalPhotos(ServletUriUtils.getAbsolutePaths(StringUtil.convertToList(userFit.getPersonalPhotos())));

            this.gender = userFit.getGender();
            this.birthday = DateUtils.getUtcForOracle(userFit.getBirthday());
            this.email = userFit.getEmail();

            country = Objects.nonNull(userFit.getCountry()) && Objects.nonNull(userFit.getCountry().getCountryId()) ? new CountryDTO(userFit.getCountry(), locale) : null;
            nativeLanguages = getLanguageDTOs(userFit.getUserFitLanguages(), locale);
            this.profileStep = userFit.getProfileStep();
        }
    }

    public UserDTO(UserProfileEmbedded userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            this.gender = Gender.fromValue(userProfile.getGender());
            this.birthday = userProfile.getBirthday();
            this.email = userProfile.getEmail();

            country = Objects.nonNull(userProfile.getCountry()) && Objects.nonNull(userProfile.getCountry().getId()) ? new CountryDTO(userProfile.getCountry(), locale) : null;
            nativeLanguages = this.getLanguageDTOFromEmbedded(userProfile.getNativeLanguages(), locale);
            setNationalId(userProfile.getNationalId());
            setAddress(userProfile.getAddress());
            setPersonalPhotos(ServletUriUtils.getAbsolutePaths(userProfile.getPersonalPhotos()));
        }
    }

    public UserDTO(UserProfileCvEmbedded userProfile, String locale) {
        super(userProfile, locale);
        if (Objects.nonNull(userProfile)) {
            this.birthday = userProfile.getBirthday();
            this.gender = Gender.fromValue(userProfile.getGender());
            this.email = userProfile.getEmail();

            //TODO: Check 4 field again because it is set in parent
            this.setNationalId(userProfile.getNationalId());
            this.setPersonalPhotos(userProfile.getPersonalPhotos());
            this.setPhone(userProfile.getPhone());
            this.setAddress(userProfile.getAddress());

            country = Objects.nonNull(userProfile.getCountry()) && Objects.nonNull(userProfile.getCountry().getId()) ? new CountryDTO(userProfile.getCountry(), locale) : null;
            nativeLanguages = this.getLanguageDTOFromEmbedded(userProfile.getNativeLanguages(), locale);
        }
    }


    protected List<LanguageDTO> getLanguageDTOFromEmbedded(List<LanguageEmbedded> userLanguages, String locale) {
        if (CollectionUtils.isNotEmpty(userLanguages)) {
            return userLanguages.stream()
                    .map(it -> new LanguageDTO(it, locale))
                    .distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    protected List<LanguageDTO> getLanguageDTOFromEmbedded(List<UserLanguage> userLanguages, boolean isNative, String locale) {
        if (CollectionUtils.isNotEmpty(userLanguages)) {
            return userLanguages.stream()
                    .filter(ul -> ul.isNative() == isNative)
                    .map(ul -> new LanguageDTO(ul.getLanguage(), locale))
                    .distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    protected List<LanguageDTO> getLanguageDTOs(List<UserFitLanguage> userLanguages, String locale) {
        if (CollectionUtils.isNotEmpty(userLanguages)) {
            return userLanguages.stream()
                    .map(ul -> new LanguageDTO(ul.getLanguage(), locale))
                    .distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
