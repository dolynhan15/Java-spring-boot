package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserFromExcelService extends BaseBusinessService {
    BaseResp uploadUserProfile(MultipartFile file, Authentication authentication);
}
