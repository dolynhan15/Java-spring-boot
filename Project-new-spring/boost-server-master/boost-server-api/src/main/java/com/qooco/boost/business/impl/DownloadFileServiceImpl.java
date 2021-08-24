package com.qooco.boost.business.impl;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/13/2018 - 8:17 AM
 */

import com.qooco.boost.business.DownloadFileService;
import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class DownloadFileServiceImpl implements DownloadFileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Resource load(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new InvalidParamException(ResponseStatus.FILE_PATH_IS_NULL);
        }
        return fileStorageService.loadResource(filePath);
    }
}
