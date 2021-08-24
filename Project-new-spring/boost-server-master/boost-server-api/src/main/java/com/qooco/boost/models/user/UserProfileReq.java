package com.qooco.boost.models.user;


import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UserProfileReq extends UserBaseReq {
    private Long id;
    private long[] languageIds;
    private Long cityId;
    @Deprecated
    private Long country;
    private Integer educationId;

    public UserProfile updateEntity(UserProfile userProfile, Long updatedOwner) {
        userProfile.setUserProfileId(this.id);
        userProfile.setFirstName(getFirstName());
        userProfile.setLastName(getLastName());
        userProfile.setBirthday(DateUtils.toUtcForOracle(getBirthday()));
        userProfile.setGender(getGender());
        userProfile.setAvatar(ServletUriUtils.getRelativePath(getAvatar()));
        userProfile.setUserLanguageList(getUserLanguages(userProfile, updatedOwner));
        userProfile.setCountry(new Country(this.country));
        userProfile.setCity(new City(this.cityId));
        userProfile.setPhoneNumber(getPhoneNumber());
        userProfile.setAddress(getAddress());
        userProfile.setNationalId(getNationalId());
        userProfile.setPersonalPhotos(StringUtil.convertToJson(ServletUriUtils.getRelativePaths(getPersonalPhotos())));
        return userProfile;
    }

    private List<UserLanguage> getUserLanguages(UserProfile userProfile, Long updatedOwner) {
        List<UserLanguage> userLanguages = new ArrayList<>();
        UserLanguage userLanguage;
        for (Long id : this.languageIds) {
            userLanguage = new UserLanguage(false, new Language(id), userProfile, updatedOwner);
            userLanguages.add(userLanguage);
        }

        for (Long id : getNativeLanguageIds()) {
            userLanguage = new UserLanguage(true, new Language(id), userProfile, updatedOwner);
            userLanguages.add(userLanguage);
        }

        return userLanguages;
    }
}
