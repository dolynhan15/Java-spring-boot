package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.AppVersionReq;
import com.qooco.boost.models.request.admin.CloneStressTestUserReq;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface BusinessAdminService extends BaseBusinessService {
    BaseResp saveAppVersion(AppVersionReq request, Authentication authentication);

    BaseResp getAllAppVersion(Authentication authentication);

    BaseResp updateParticipantAndCreatedBy(Authentication authentication, int total);

    BaseResp getSystemLogger(Authentication authentication, Long fromDate, Long toDate);

    BaseResp getSocketConnection(Authentication authentication, Long fromDate, Long toDate);

    BaseResp patchAppointmentDetailDoc();

    BaseResp cloneUserProfile(Authentication authentication, CloneStressTestUserReq req);

    BaseResp cloneUserCVDoc(Authentication authentication, CloneStressTestUserReq req);

    BaseResp patchVacancySeat();

    BaseResp patchLocaleDataInMongo(String jsonFile);

    BaseResp patchLocalization(MultipartFile file, Authentication authentication);
}
