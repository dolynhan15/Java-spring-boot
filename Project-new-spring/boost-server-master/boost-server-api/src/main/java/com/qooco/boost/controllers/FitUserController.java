package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessFitUserService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.RecruiterDTO;
import com.qooco.boost.models.dto.user.UserProfileDTO;
import com.qooco.boost.models.user.FitUserReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

@Api(tags = "Fit User")
@RestController
@RequestMapping(URLConstants.USER_PATH + "/" + URLConstants.FIT_USER_PATH)
public class FitUserController extends BaseController {
    @Autowired
    private BusinessFitUserService businessFitUserService;

    @ApiOperation(value = "Update Fit User Basic Information", httpMethod = "POST", response = UserProfileResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.FIRST_NAME_IS_REQUIRED + " : " + StatusConstants.FIRST_NAME_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.LAST_NAME_IS_REQUIRED + " : " + StatusConstants.LAST_NAME_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.BIRTHDAY_IS_REQUIRED + " : " + StatusConstants.BIRTHDAY_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + "Gender param: MALE, FEMALE, NON_BINARY, TRANS_GENDER, OTHER, DECLINE_TO_STATE and NULL value"
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.BASIC_PROFILE)
    public Object saveBasicUserProfile(@RequestBody final FitUserReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        request.setId(user.getId());
        BaseResp resp = businessFitUserService.saveBasicFitUser(request, authentication);
        return success(resp);
    }

    @ApiOperation(value = "Update Fit User Advance Information", httpMethod = "POST", response = UserProfileResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.LANGUAGES_ARE_REQUIRED + " : " + StatusConstants.LANGUAGES_ARE_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_LANGUAGE + " : " + StatusConstants.NOT_FOUND_LANGUAGE_MESSAGE
                    + "<br>" + "\n" + "Gender param: MALE, FEMALE, NON_BINARY, TRANS_GENDER, OTHER, DECLINE_TO_STATE and NULL value"
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.ADVANCED_PROFILE)
    public Object saveAdvancedUserProfile(@RequestBody FitUserReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        request.setId(user.getId());
        BaseResp resp = businessFitUserService.saveAdvancedFitUser(request, authentication);
        return success(resp);
    }

    @ApiOperation(value = "Get recruiter information", httpMethod = "GET", response = RecruiterInfoResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
                    + "<br>" + "\n" + "Gender param: MALE, FEMALE, NON_BINARY, TRANS_GENDER, OTHER, DECLINE_TO_STATE and NULL value"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_RECRUITER_INFO_METHOD)
    public Object getRecruiterInformation(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = new BaseResp(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        if (SELECT_APP.appId().equals(authenticatedUser.getAppId())) {
            result = businessFitUserService.getRecruiterInfo(authentication);
        }
        return success(result);
    }

    @ApiOperation(value = "Upload user public key", httpMethod = "POST", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.USER_PUBLIC_KEY)
    public Object uploadPublicKey(Authentication authentication, @RequestBody UserUploadKeyReq request) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessFitUserService.uploadPublicKey(authenticatedUser, request);
        return success(result);
    }

    private class UserProfileResp extends BaseResp<UserProfileDTO> {
    }

    private class RecruiterInfoResp extends BaseResp<RecruiterDTO> {
    }
}
