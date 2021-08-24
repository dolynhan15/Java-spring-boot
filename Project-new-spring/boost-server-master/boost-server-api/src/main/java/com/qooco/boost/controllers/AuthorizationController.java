package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessAuthorizationService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.qooco.SocialLoginReq;
import com.qooco.boost.models.qooco.SocialLoginWithClientInfoReq;
import com.qooco.boost.models.request.authorization.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Authorization")
@CrossOrigin
@RestController
@RequestMapping
public class AuthorizationController extends BaseController {

    public static final String HEADER_APP_ID = "boost-appId";
    @Autowired
    private BusinessAuthorizationService businessAuthorizationService;

    @ApiOperation(value = "Login with system account", httpMethod = "POST", response = UserLoginResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_EMPTY + " : " + StatusConstants.USERNAME_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_WRONG_FORMAT + " : " + StatusConstants.USERNAME_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_IS_EMPTY + " : " + StatusConstants.PASSWORD_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG_FORMAT + " : " + StatusConstants.PASSWORD_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_NOT_EXIST + " : " + StatusConstants.USERNAME_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG + " : " + StatusConstants.PASSWORD_WRONG_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.QOOCO_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER + " : " + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER_MESSAGE
    )
    @PostMapping(URLConstants.AUTH_PATH + URLConstants.LOGIN_PATH)
    public Object doLoginWithSystemAccount(@RequestBody final UserLoginReq request,
                                           @RequestHeader(HEADER_APP_ID) String appId) {
        saveRequestBodyToSystemLogger(request);
		BaseResp result = businessAuthorizationService.loginWithSystemAccount(
                request.getUsername(), request.getPassword(), appId);

        if (result instanceof UserLoginResp) {
            return success(result);
        }

        return error(result.getCode(), result.getMessage());
    }

    @ApiOperation(value = "Login with system account and client info", httpMethod = "POST", response = UserLoginResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_EMPTY + " : " + StatusConstants.USERNAME_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_WRONG_FORMAT + " : " + StatusConstants.USERNAME_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_IS_EMPTY + " : " + StatusConstants.PASSWORD_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG_FORMAT + " : " + StatusConstants.PASSWORD_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_ID_IS_EMPTY + " : " + StatusConstants.APP_ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_ID_INVALID + " : " + StatusConstants.APP_ID_INVALID_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_VERSION_IS_EMPTY + " : " + StatusConstants.APP_VERSION_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + "Platform Param: WEB/PC/ANDROID/IOS/WP"
                    + "<br>" + "\n" + StatusConstants.PLATFORM_IS_EMPTY + " : " + StatusConstants.PLATFORM_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_NOT_EXIST + " : " + StatusConstants.USERNAME_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG + " : " + StatusConstants.PASSWORD_WRONG_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.QOOCO_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER + " : " + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER_MESSAGE
    )
    @PostMapping(URLConstants.VERSION_2 + URLConstants.AUTH_PATH + URLConstants.LOGIN_PATH)
    public Object doLoginWithSystemAccountWithClientInfo(@RequestBody final UserLoginWithClientInfoReq request,
                                                         @RequestHeader(HEADER_APP_ID) String appId) {
       saveRequestBodyToSystemLogger(request);
	   BaseResp result = businessAuthorizationService.loginWithSystemAccountWithClientInfo(
                request.getUserLoginReq().getUsername(), request.getUserLoginReq().getPassword(), request.getClientInfo(), appId);

        if (result instanceof UserLoginResp) {
            return success(result);
        }

        return error(result.getCode(), result.getMessage());
    }

    @ApiOperation(value = "Social Login", httpMethod = "POST", response = UserLoginResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.AUTH_ID_EMPTY + " : " + StatusConstants.AUTH_ID_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.AUTH_PROVIDER_EMPTY + " : " + StatusConstants.AUTH_PROVIDER_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.USERNAME_NOT_EXIST + " : " + StatusConstants.USERNAME_NOT_EXIST_MESSAGE
                    + "<br>" + StatusConstants.PASSWORD_WRONG + " : " + StatusConstants.PASSWORD_WRONG_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.QOOCO_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER + " : " + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER_MESSAGE
    )
    @PostMapping(URLConstants.AUTH_PATH + URLConstants.SOCIAL_LOGIN_PATH)
    public Object loginWithSocial(@RequestBody final SocialLoginReq request) {
       saveRequestBodyToSystemLogger(request);
	   return businessAuthorizationService.socialLogin(request);
    }

    @ApiOperation(value = "Social Login With Client Info", httpMethod = "POST", response = UserLoginResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.AUTH_ID_EMPTY + " : " + StatusConstants.AUTH_ID_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.AUTH_PROVIDER_EMPTY + " : " + StatusConstants.AUTH_PROVIDER_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_ID_IS_EMPTY + " : " + StatusConstants.APP_ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_ID_INVALID + " : " + StatusConstants.APP_ID_INVALID_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_VERSION_IS_EMPTY + " : " + StatusConstants.APP_VERSION_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + "Platform Param: WEB/PC/ANDROID/IOS/WP"
                    + "<br>" + "\n" + StatusConstants.PLATFORM_IS_EMPTY + " : " + StatusConstants.PLATFORM_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.USERNAME_NOT_EXIST + " : " + StatusConstants.USERNAME_NOT_EXIST_MESSAGE
                    + "<br>" + StatusConstants.PASSWORD_WRONG + " : " + StatusConstants.PASSWORD_WRONG_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.QOOCO_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER + " : " + StatusConstants.USERNAME_FOUND_ON_EXTERNAL_SERVER_MESSAGE
    )
    @PostMapping(URLConstants.VERSION_2 + URLConstants.AUTH_PATH + URLConstants.SOCIAL_LOGIN_PATH)
    public Object loginWithSocialWithClientInfo(@RequestBody final SocialLoginWithClientInfoReq request) {
        saveRequestBodyToSystemLogger(request);
		return businessAuthorizationService.socialLoginWithClientInfo(request);
    }

    @ApiOperation(value = "Generate code for sign up", httpMethod = "POST", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EMPTY + ": " + StatusConstants.EMAIL_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_WRONG_FORMAT + ": " + StatusConstants.EMAIL_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EXISTED + ": " + StatusConstants.EMAIL_IS_EXISTED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + ": " + StatusConstants.QOOCO_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + ": " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.FAILED_TO_SEND_CODE + ": " + StatusConstants.FAILED_TO_SEND_CODE_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NETWORK_ERROR + ": " + StatusConstants.NETWORK_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.BAD_REQUEST + ": " + StatusConstants.BAD_REQUEST_MESSAGE + " with external server error description if available"
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_ALREADY_USED + ": " + StatusConstants.EMAIL_IS_ALREADY_USED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.CONTACT_WRONG_FORMAT + ": " + StatusConstants.CONTACT_WRONG_FORMAT_MESSAGE
    )
    @PostMapping(URLConstants.AUTH_PATH + URLConstants.GENERATE_CODE_PATH)
    public Object doGenerateCode(@RequestBody final GenerateCodeReq request) {
       saveRequestBodyToSystemLogger(request);
	   BaseResp response = businessAuthorizationService.doGenerateCode(request);
        return success(response);
    }

    @ApiOperation(value = "Do verify sign up code", httpMethod = "POST", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EMPTY + " : " + StatusConstants.EMAIL_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_WRONG_FORMAT + " : " + StatusConstants.EMAIL_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.VERIFICATION_CODE_IS_EMPTY + " : " + StatusConstants.VERIFICATION_CODE_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.QOOCO_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.VERIFICATION_CODE_NOT_FOUND + " : " + StatusConstants.VERIFICATION_CODE_NOT_FOUND_MESSAGE
                    + "<br>" + "\n" + StatusConstants.VERIFICATION_CODE_NOT_MATCH + " : " + StatusConstants.VERIFICATION_CODE_NOT_MATCH_MESSAGE
                    + "<br>" + "\n" + StatusConstants.VERIFICATION_CODE_EXPIRED + " : " + StatusConstants.VERIFICATION_CODE_EXPIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
    )
    @PostMapping(URLConstants.AUTH_PATH + URLConstants.VERIFY_CODE_PATH)
    public Object doVerifyCode(@RequestBody final VerifyCodeReq request){
        saveRequestBodyToSystemLogger(request);
         BaseResp result = businessAuthorizationService.doVerifyCode(request);
        if(ResponseStatus.SUCCESS.getCode() == result.getCode()){
            return success(result);
        }
        return error(result.getCode(), result.getMessage());
    }

    @ApiOperation(value = "Sign up with system Account", httpMethod = "POST", response = UserLoginResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EMPTY + ": " + StatusConstants.EMAIL_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_WRONG_FORMAT + ": " + StatusConstants.EMAIL_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_IS_EMPTY + ": " + StatusConstants.USER_NAME_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_WRONG_FORMAT + ": " + StatusConstants.USER_NAME_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_IS_EMPTY + ": " + StatusConstants.PASSWORD_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG_FORMAT + ": " + StatusConstants.PASSWORD_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_EXISTING + ": " + StatusConstants.USER_NAME_EXISTING_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NETWORK_ERROR + ": " + StatusConstants.NETWORK_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EXISTED + ": " + StatusConstants.EMAIL_IS_EXISTED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.BAD_REQUEST + ": " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PostMapping(URLConstants.AUTH_PATH + URLConstants.SIGN_UP_WITH_SYSTEM)
    public Object signUpWithSystemAccount(@RequestBody final SignUpSystemReq request,
                                          @RequestHeader(HEADER_APP_ID) String appId) {
        saveRequestBodyToSystemLogger(request);
		BaseResp baseResp = businessAuthorizationService.signUpWithSystem(request, appId);
        return success(baseResp);
    }

    @ApiOperation(value = "Sign up with System Account and Client Info", httpMethod = "POST", response = UserLoginResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EMPTY + ": " + StatusConstants.EMAIL_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_WRONG_FORMAT + ": " + StatusConstants.EMAIL_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_IS_EMPTY + ": " + StatusConstants.USER_NAME_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_WRONG_FORMAT + ": " + StatusConstants.USER_NAME_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_IS_EMPTY + ": " + StatusConstants.PASSWORD_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG_FORMAT + ": " + StatusConstants.PASSWORD_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_ID_IS_EMPTY + " : " + StatusConstants.APP_ID_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_ID_INVALID + " : " + StatusConstants.APP_ID_INVALID_MESSAGE
                    + "<br>" + "\n" + StatusConstants.APP_VERSION_IS_EMPTY + " : " + StatusConstants.APP_VERSION_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + "Platform Param: WEB/PC/ANDROID/IOS/WP"
                    + "<br>" + "\n" + StatusConstants.PLATFORM_IS_EMPTY + " : " + StatusConstants.PLATFORM_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_EXISTING + ": " + StatusConstants.USER_NAME_EXISTING_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NETWORK_ERROR + ": " + StatusConstants.NETWORK_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_IS_EXISTED + ": " + StatusConstants.EMAIL_IS_EXISTED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.BAD_REQUEST + ": " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_EXISTING + ": " + StatusConstants.USER_NAME_EXISTING_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NOT_CREATE_SUCCESS + ": " + StatusConstants.USER_NOT_CREATE_SUCCESS_MESSAGE
    )
    @PostMapping(URLConstants.VERSION_2 + URLConstants.AUTH_PATH + URLConstants.SIGN_UP_WITH_SYSTEM)
    public Object signUpWithSystemAccountAndClientInfo(@RequestBody final SignUpSystemWithClientInfoReq request) {
        saveRequestBodyToSystemLogger(request);
		BaseResp baseResp = businessAuthorizationService.signUpWithSystemAndClientInfo(request);
        return success(baseResp);
    }

    @ApiOperation(value = "Forgot password", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_OR_EMAIL_EMPTY_CODE + ": " + StatusConstants.USERNAME_OR_EMAIL_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INTERNAL_SERVER_ERROR + ": " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.FORGOT_PASSWORD_UNKNOWN_CODE + ": " + StatusConstants.FORGOT_PASSWORD_UNKNOWN_MESSAGE
                    + "<br>" + "\n" + StatusConstants.FORGOT_PASSWORD_USER_NOT_EXIST_CODE + ": " + StatusConstants.FORGOT_PASSWORD_USER_NOT_EXIST_MESSAGE
                    + "<br>" + "\n" + StatusConstants.FORGOT_PASSWORD_NOT_FOUND_CODE + ": " + StatusConstants.FORGOT_PASSWORD_NOT_FOUND_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USERNAME_NOT_EXIST + ": " + StatusConstants.USERNAME_NOT_EXIST_MESSAGE
    )
    @RequestMapping(value = URLConstants.AUTH_PATH + URLConstants.FORGOT_PASSWORD_PATH, method = RequestMethod.GET)
    public Object doForgotPassword(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email
    ) {
        ForgotPasswordReq forgotPasswordReq = new ForgotPasswordReq(username, email);
        BaseResp baseResp = businessAuthorizationService.doForgotPassword(forgotPasswordReq);
        return success(baseResp);
    }

    @ApiOperation(value = "Do logout", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.AUTH_PATH + URLConstants.LOGOUT_PATH, method = RequestMethod.GET)
    public Object doLogoutWithSystemAccount(Authentication authentication) {
        BaseResp baseResp = businessAuthorizationService.doLogoutWithSystemAccount(authentication);
        return success(baseResp);
    }

    @ApiOperation(value = "Update channel Id", httpMethod = "PUT", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOTIFICATION_CHANNEL_ID_EMPTY + ": " + StatusConstants.NOTIFICATION_CHANNEL_ID_EMPTY_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.AUTH_PATH + URLConstants.UPDATE_CHANNEL_ID_PATH)
    @PutMapping(URLConstants.AUTH_PATH + URLConstants.UPDATE_CHANNEL_ID_PATH)
    public Object updateChannelId(@RequestBody String channelId, Authentication authentication) {
       saveRequestBodyToSystemLogger(channelId);
	   BaseResp baseResp = businessAuthorizationService.updateChannelId(channelId, authentication);
        return success(baseResp);
    }

    @ApiOperation(value = "Update client info", httpMethod = "PUT", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.APP_ID_IS_EMPTY + " : " + StatusConstants.APP_ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.APP_ID_INVALID + " : " + StatusConstants.APP_ID_INVALID_MESSAGE
                    + "<br>" + StatusConstants.APP_VERSION_IS_EMPTY + " : " + StatusConstants.APP_VERSION_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.PLATFORM_IS_EMPTY + " : " + StatusConstants.PLATFORM_IS_EMPTY_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = URLConstants.VERSION_2 + URLConstants.AUTH_PATH + URLConstants.UPDATE_CLIENT_INFO)
    @PutMapping(value = URLConstants.VERSION_2 + URLConstants.AUTH_PATH + URLConstants.UPDATE_CLIENT_INFO)
    public Object updateDeviceToken(@RequestBody ClientInfoReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
		AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessAuthorizationService.updateClientInfo(request, authenticatedUser.getToken());
        return success(result);
    }
}
