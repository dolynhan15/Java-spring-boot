package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.user.ExportUserCvPdfReq;
import com.qooco.boost.models.user.UserCurriculumVitaeReq;
import com.qooco.boost.models.user.UserPreviousPositionReq;
import com.qooco.boost.models.user.UserProfileStep;
import org.springframework.security.core.Authentication;

public interface BusinessUserCurriculumVitaeService {
    BaseResp saveUserPreviousPosition(UserPreviousPositionReq req, Authentication authentication);

    BaseResp getUserPreviousPositionById(Long id, Authentication authentication);

    BaseResp getUserPreviousPositionByUserProfileId(Long userProfileId, Authentication authentication);

    BaseResp deleteUserPreviousPositions(Long id);

    BaseResp saveJobProfile(UserCurriculumVitaeReq userCurriculumVitaeStep3Req, Authentication authentication);

    BaseResp saveUserCurriculumVitae(UserCurriculumVitaeReq saveUserCvReq, Authentication authentication);

    BaseResp savePersonalInformation(UserCurriculumVitaeReq saveUserCvReq, Authentication authentication);

    BaseResp getUserCurriculumVitaeByUserProfileId(long userCVId, String locale);

    BaseResp deleteSocialLink(long userProfileId, String socialLink);

    BaseResp saveProfileStep(Long userProfileId, UserProfileStep profileStep, Authentication authentication);

    BaseResp exportUserCvPdf(ExportUserCvPdfReq exportUserCvPdfReq, Authentication authentication);
}
