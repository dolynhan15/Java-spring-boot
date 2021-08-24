package com.qooco.boost.business;

import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.models.request.message.MediaMessageReq;
import com.qooco.boost.models.user.UserCvPrint;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface FileStorageService {
    List<PataFile> storeFile(Long userProfileId, String subPath, List<MultipartFile> files);

    PataFile storeFile(Long userProfileId, String subPath, MediaMessageReq mediaMessageReq);

    Resource loadResource(String filePath);

    void deleteExpiredFile(Date expiredDate);

    void deleteFileById(Long id);

    void deleteFileByAbsolutePath(String absolutePath);

    void deleteFileByRelativePath(String relativePath);

    String createPdfFile(UserCvPrint userCvPrint, Long userProfileId, String subPath);
}
