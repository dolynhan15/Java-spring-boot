package com.qooco.boost.utils;

import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.models.dto.user.UserProfileDTO;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;

import java.util.Date;
import java.util.Objects;

public class UserProfileUtils {

    public static int calculateProfileStrength(UserProfileDTO userProfile,
                                               Date expectedStartDate,
                                               boolean isAsap,
                                               boolean hasPreviousPosition,
                                               boolean hasSoftSkill,
                                               boolean hasSocialLink,
                                               boolean hasPersonality,
                                               boolean hasQualification
    ) {
        int strength = 0;
        if (Objects.nonNull(userProfile)) {
            strength = userProfile.getProfileStrength();

            if (Objects.nonNull(expectedStartDate) || isAsap) strength += 1;
            if (hasPreviousPosition) strength += 1;

            if (StringUtils.isNotBlank(userProfile.getNationalId())
                    || hasSoftSkill
                    || hasSocialLink
                    || hasPersonality) strength += 1;

            if (hasQualification) strength += 1;
        }
        return strength;
    }

    public static int calculateProfileStrength(UserProfile userProfile, UserCurriculumVitae userCV, String locale) {
        return calculateProfileStrength(new UserProfileDTO(userProfile, locale),
                userCV.getExpectedStartDate(),
                userCV.isAsap(),
                false,
                CollectionUtils.isNotEmpty(userCV.getUserSoftSkills()),
                StringUtils.isNotBlank(userCV.getSocialLinks()),
                userCV.isHasPersonality(),
                false
        );
    }

    public static int calculateProfileStrength(UserCvDoc userCvDoc, String locale) {
        return calculateProfileStrength(new UserProfileDTO(userCvDoc.getUserProfile(), locale),
                userCvDoc.getExpectedStartDate(),
                userCvDoc.isAsap(),
                CollectionUtils.isNotEmpty(userCvDoc.getPreviousPositions()),
                CollectionUtils.isNotEmpty(userCvDoc.getSoftSkills()),
                CollectionUtils.isNotEmpty(userCvDoc.getSocialLinks()),
                userCvDoc.isHasPersonality(),
                CollectionUtils.isNotEmpty(userCvDoc.getQualifications())
        );
    }

}
