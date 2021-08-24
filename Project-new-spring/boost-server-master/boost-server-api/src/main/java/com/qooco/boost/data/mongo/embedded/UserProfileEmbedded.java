package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserFitLanguage;
import com.qooco.boost.data.oracle.entities.UserLanguage;
import com.qooco.boost.data.oracle.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@SuperBuilder(builderMethodName = "userProfileEmbeddedBuilder", toBuilder = true)
public class UserProfileEmbedded extends UserProfileBasicEmbedded {
    private Date birthday;
    private String nationalId;
    private List<String> personalPhotos;
    private CountryEmbedded country;
    private CityEmbedded city;
    private String address;
    private List<LanguageEmbedded> nativeLanguages;
    private List<LanguageEmbedded> languages;

    public UserProfileEmbedded(Long userProfileId, int userType) {
        super(userProfileId, userType);
    }

    public UserProfileEmbedded(UserProfileBasicEmbedded embedded) {
        super(embedded);
    }

    public UserProfileEmbedded(UserProfileEmbedded embedded) {
        super(embedded);
        this.birthday = embedded.getBirthday();
        this.nationalId = embedded.getNationalId();
        this.personalPhotos = embedded.getPersonalPhotos();
        this.country = embedded.getCountry();
        this.city = embedded.getCity();
        this.address = embedded.getAddress();
        this.nativeLanguages = embedded.getNativeLanguages();
        this.languages = embedded.getLanguages();
    }

    public UserProfileEmbedded(UserProfile userProfile, int userType) {
        super(userProfile, userType);
        if (Objects.nonNull(userProfile)) {
            this.birthday = userProfile.getBirthday();
            this.nationalId = userProfile.getNationalId();
            this.personalPhotos = Collections.singletonList(userProfile.getPersonalPhotos());
            this.address = userProfile.getAddress();

            Optional.ofNullable(userProfile.getCountry()).ifPresent(country -> this.country = new CountryEmbedded(country));
            Optional.ofNullable(userProfile.getCity()).ifPresent(city -> this.city = new CityEmbedded(city));
            Optional.ofNullable(userProfile.getUserLanguageList()).filter(CollectionUtils::isNotEmpty).ifPresent(languages -> {
                this.nativeLanguages = languages.stream().filter(UserLanguage::isNative).map(LanguageEmbedded::new).collect(Collectors.toList());
                this.languages = languages.stream().filter(l -> !l.isNative()).map(LanguageEmbedded::new).collect(Collectors.toList());
            });
        }
    }

    public UserProfileEmbedded(UserFit userFit) {
        super(userFit, UserType.SELECT);
        if (Objects.nonNull(userFit)) {
            this.birthday = userFit.getBirthday();
            this.nationalId = userFit.getNationalId();
            this.personalPhotos = Collections.singletonList(userFit.getPersonalPhotos());
            this.address = userFit.getAddress();

            Optional.ofNullable(userFit.getCountry()).ifPresent(country -> this.country = new CountryEmbedded(country));
            Optional.ofNullable(userFit.getDefaultCompany())
                    .ifPresent(company -> Optional.ofNullable(company.getCity())
                            .ifPresent(city -> this.city = new CityEmbedded(city)));
            Optional.ofNullable(userFit.getUserFitLanguages()).filter(CollectionUtils::isNotEmpty).ifPresent(languages -> {
                this.nativeLanguages = languages.stream().filter(UserFitLanguage::isNative).map(LanguageEmbedded::new).collect(Collectors.toList());
                this.languages = languages.stream().filter(l -> !l.isNative()).map(LanguageEmbedded::new).collect(Collectors.toList());
            });
        }
    }
}
