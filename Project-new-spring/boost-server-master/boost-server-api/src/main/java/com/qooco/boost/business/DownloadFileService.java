package com.qooco.boost.business;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/13/2018 - 8:17 AM
 */

import org.springframework.core.io.Resource;

public interface DownloadFileService {
    Resource load(String filePath);
}
