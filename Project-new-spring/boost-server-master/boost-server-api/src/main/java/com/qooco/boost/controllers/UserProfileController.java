package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessUserService;
import com.qooco.boost.business.UserFromExcelService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.CareerDTO;
import com.qooco.boost.models.dto.user.UserProfileDTO;
import com.qooco.boost.models.user.UserProfileReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static com.qooco.boost.enumeration.ProfileStep.ADVANCED_PROFILE_STEP;
import static com.qooco.boost.enumeration.ProfileStep.BASIC_PROFILE_STEP;

@Api(tags = "User profile")
@RestController
@RequestMapping(URLConstants.USER_PATH + "/" + URLConstants.USER_PROFILE_PATH)
public class UserProfileController extends BaseController {

    @Autowired
    private BusinessUserService businessUserService;
    @Autowired
    private UserFromExcelService uploadUserProfile;

    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @ApiOperation(value = "Create and Update user profile", httpMethod = "POST", response = UserProfileResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + "Gender param: MALE, FEMALE, NON_BINARY, TRANS_GENDER, OTHER, DECLINE_TO_STATE and NULL value"
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD)
    public Object save(@RequestBody final UserProfileReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        request.setId(authenticatedUser.getId());
        BaseResp savedUserProfile = businessUserService.saveUserProfile(request, authentication);
        return success(savedUserProfile);
    }

    @ApiOperation(value = "Create and Update basic user profile", httpMethod = "POST", response = UserProfileResp.class,
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
    @PostMapping(URLConstants.SAVE_METHOD + URLConstants.BASIC_PROFILE)
    public Object saveBasicUserProfile(@RequestBody final UserProfileReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp resp = new BaseResp();
        if (authenticatedUser.getAppId().equals(PROFILE_APP.appId()) || SELECT_APP.appId().equals(authenticatedUser.getAppId())) {
            request.setId(authenticatedUser.getId());
            resp = businessUserService.saveBasicUserProfile(request, authentication);
            businessProfileAttributeEventService.onUserProfileStep(authentication, BASIC_PROFILE_STEP);
        }
        return success(resp);
    }

    @ApiOperation(value = "Create and Update advanced user profile", httpMethod = "POST", response = UserProfileResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.LANGUAGES_ARE_REQUIRED + " : " + StatusConstants.LANGUAGES_ARE_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_LANGUAGE + " : " + StatusConstants.NOT_FOUND_LANGUAGE_MESSAGE
                    + "<br>" + "\n" + "Gender param: MALE, FEMALE, NON_BINARY, TRANS_GENDER, OTHER, DECLINE_TO_STATE and NULL value"
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD + URLConstants.ADVANCED_PROFILE)
    public Object saveAdvancedUserProfile(@RequestBody final UserProfileReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp resp = new BaseResp();
        if (authenticatedUser.getAppId().equals(PROFILE_APP.appId()) || SELECT_APP.appId().equals(authenticatedUser.getAppId())) {
            request.setId(authenticatedUser.getId());
            resp = businessUserService.saveAdvancedUserProfile(request, authenticatedUser.getAppId(), authenticatedUser.getLocale());
            businessProfileAttributeEventService.onUserProfileStep(authentication, ADVANCED_PROFILE_STEP);
        }
        return success(resp);
    }

    @ApiOperation(value = "Get career information", httpMethod = "GET", response = CareerInfoResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + "\n" + "Gender param: MALE, FEMALE, NON_BINARY, TRANS_GENDER, OTHER, DECLINE_TO_STATE and NULL value"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_CAREER_INFO_METHOD)
    public Object getCareerInformation(
            @RequestParam(value = "timeZone", required = false, defaultValue = "UTC") String timeZone,
            Authentication authentication) {
        BaseResp result = businessUserService.getCareerInfo(authentication, timeZone);
        return success(result);
    }

    @ApiOperation(value = "Increase coin of user", httpMethod = "PUT", response = IncreaseCoinsResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.USER_WALLET_METHOD)
    @PutMapping(URLConstants.USER_WALLET_METHOD)
    public Object increaseCoins(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessUserService.increaseCoin(authenticatedUser.getId());
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
        BaseResp result = businessUserService.uploadPublicKey(authenticatedUser, request);
        return success(result);
    }

    @ApiOperation(value = "Upload user profile excel to create account", httpMethod = "POST", response = BaseResp.class)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.USER_PROFILE_UPLOAD)
    public Object uploadUserProfileUser(@RequestParam("file") MultipartFile file, Authentication authentication) {
        BaseResp result = uploadUserProfile.uploadUserProfile(file, authentication);
        return success(result);
    }

    @ApiOperation(value = "Check accessToken. It is using when separate message project", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.LOGIN_UNAUTHORIZED + " : " + StatusConstants.LOGIN_UNAUTHORIZED_MESSAGE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.USER_ACCESS_TOKEN_PATH)
    public Object checkAccessToken() {
        return success(new BaseResp<>());
    }

    private class UserProfileResp extends BaseResp<UserProfileDTO> {
    }

    private class CareerInfoResp extends BaseResp<CareerDTO> {
    }

    private class IncreaseCoinsResp extends BaseResp<Integer> {
    }
}
