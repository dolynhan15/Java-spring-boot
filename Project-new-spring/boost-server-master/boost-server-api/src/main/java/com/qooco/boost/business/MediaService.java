package com.qooco.boost.business;

import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.message.MediaMessageReq;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    BaseResp store(MultipartFile file, String folderName, Authentication authentication);

    BaseResp stores(MultipartFile[] files, String folderName, Authentication authentication);

    BaseResp delete(String absolutePath);

    PataFile storeMediaFileToServer(MediaMessageReq mediaMessageReq, String folderName, Authentication authentication);
}
