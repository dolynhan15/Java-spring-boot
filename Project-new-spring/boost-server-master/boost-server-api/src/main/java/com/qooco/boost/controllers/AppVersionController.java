package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessAppVersionService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.AppVersionDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "App version", value = URLConstants.APP_VERSION_PATH, description = "App version Controller")
@RestController
@RequestMapping(URLConstants.APP_VERSION_PATH)
public class AppVersionController extends BaseController {
    @Autowired
    private BusinessAppVersionService appVersionService;

    @Value(ApplicationConstant.BOOST_PATA_VERSION)
    private String boostApiVersion = "";


    @ApiOperation(value = "Get latest app version", httpMethod = "GET", response = AppVersionResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.GET_LATEST_APP_VERSION_INVALID_APP_ID + " : " + StatusConstants.GET_LATEST_APP_VERSION_INVALID_APP_ID_MESSAGE
                    + "<br>" + StatusConstants.GET_LATEST_APP_VERSION_INVALID_APP_VERSION + " : " + StatusConstants.GET_LATEST_APP_VERSION_INVALID_APP_VERSION_MESSAGE
                    + "<br>" + StatusConstants.GET_LATEST_APP_VERSION_INVALID_OS + " : " + StatusConstants.GET_LATEST_APP_VERSION_INVALID_OS_MESSAGE
                    + "<br> Param os:"
                    + "<br> Android platform: android"
                    + "<br> Ios platform: ios"
    )

    @RequestMapping(value = URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getAppVersion(
            @RequestParam(value = "appVersion") Integer appVersion,
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "os") String os) {
        BaseResp result = appVersionService.getLatestVersion(appVersion, appId, os);
        return success(result);
    }

    @ApiOperation(value = "Get api version", httpMethod = "GET", response = APIVersionResponse.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
    )

    @RequestMapping(value = URLConstants.BOOST_API + URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getAPIVersion() {
        BaseResp result = new BaseResp(boostApiVersion);
        return success(result);
    }

    private class AppVersionResp extends BaseResp<AppVersionDTO> {
    }


    private class APIVersionResponse extends BaseResp<String> {
    }
}