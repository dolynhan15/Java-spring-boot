package com.qooco.boost.controllers;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/13/2018 - 8:05 AM
 */

import com.qooco.boost.business.DownloadFileService;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.enumeration.ResponseStatus;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLConnection;

@Api(tags = "Download File")
@CrossOrigin
@RestController
public class DownloadFileController extends BaseController {

    @Autowired
    private DownloadFileService downloadFileService;


    @GetMapping(URLConstants.DOWNLOAD_IMAGE_PATH + "/**")
    public Object load(HttpServletRequest servletRequest) {
        String requestPath = servletRequest.getRequestURI();
        String[] urls = requestPath.split(URLConstants.DOWNLOAD_IMAGE_PATH);
        String url = urls != null && urls.length > 1? urls[1]: "";
        Resource resource = downloadFileService.load(url);
        if(null == resource){
            return success(ResponseStatus.MALFORMED_URL_NOT_FOUND);
        }
        String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
