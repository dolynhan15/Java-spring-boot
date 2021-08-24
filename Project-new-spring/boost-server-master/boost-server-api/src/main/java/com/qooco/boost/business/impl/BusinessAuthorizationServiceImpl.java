package com.qooco.boost.business.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooco.boost.business.BusinessAuthorizationService;
import com.qooco.boost.business.QoocoService;
import com.qooco.boost.constants.Api;
import com.qooco.boost.constants.Const;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.SocketConnectionDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserInfoDTO;
import com.qooco.boost.models.qooco.*;
import com.qooco.boost.models.request.authorization.*;
import com.qooco.boost.threads.notifications.constants.DeviceType;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.HttpHelper;
import com.qooco.boost.utils.StringUtil;
import com.qooco.boost.utils.Validation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static com.qooco.boost.data.enumeration.BoostApplication.*;

@Service
public class BusinessAuthorizationServiceImpl implements BusinessAuthorizationService {
    protected Logger logger = LogManager.getLogger(BusinessAuthorizationServiceImpl.class);
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private UserAccessTokenService userAccessTokenService;
    @Autowired
    private QoocoService qoocoService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ClientInfoService clientInfoService;
    @Autowired
    private SocketConnectionDocService socketConnectionDocService;
    @Autowired
    private ConversationDocService conversationDocService;

    private static final boolean LOGIN_VERIFIED_QOOCO_SYSTEM = true;

    @Override
    public BaseResp signUpWithSystem(SignUpSystemReq signUpSystemReq, String appId) {
        BaseResp result = signUpByQoocoSystem(signUpSystemReq, appId);

        if (ResponseStatus.SUCCESS.getCode() == result.getCode()) {
            return result;
        }
        ResponseStatus status = (ResponseStatus) result.getData();
        throw new InvalidParamException(status);
    }

    @Override
    public BaseResp signUpByQoocoSystem(SignUpSystemReq signUpSystemReq, String appId) {
        Validation.validateEmail(signUpSystemReq.getEmail());
        Validation.validateUsername(signUpSystemReq.getUsername());
        Validation.validatePassword(signUpSystemReq.getPassword());
        SignUpSystem signUpSystem = new SignUpSystem();
        signUpSystem.setUsername(signUpSystemReq.getUsername());
        signUpSystem.setEmail(signUpSystemReq.getEmail());
        signUpSystem.setPassword(signUpSystemReq.getPassword());

        //Integrate with external API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Gson gson = new GsonBuilder().create();
        String mapBody = QoocoApiConstants.REPORT_DATA
                .replace(QoocoApiConstants.REPLACEMENT_REQUEST_BODY, gson.toJson(signUpSystem));
        SignUpWithSystemAccount response = HttpHelper.doPost(qoocoService.getQoocoApiPath(Api.SERVICE_REGISTER_SYSTEM_ACCOUNT),
                mapBody, headers, SignUpWithSystemAccount.class, false);

        switch (response.getErrorCode()) {
            case QoocoApiConstants.OK_CODE:
                UserInfoDTO userInfoDTO = doSaveProfileUserOrFitUser(
                        response.getUserId(),
                        signUpSystem.getUsername(),
                        signUpSystem.getEmail(),
                        response.getSignInId(),
                        null,
                        appId);

                return new BaseResp<>(userInfoDTO, ResponseStatus.SUCCESS);
            case QoocoApiConstants.SIGN_UP_EXISTING_USERNAME:
                return new BaseResp<>(ResponseStatus.USER_NAME_EXISTING, ResponseStatus.USER_NAME_EXISTING);
            case QoocoApiConstants.SIGN_UP_USER_NOT_CREATE_SUCCESS:
                return new BaseResp<>(ResponseStatus.USER_NOT_CREATE_SUCCESS, ResponseStatus.USER_NOT_CREATE_SUCCESS);
            case QoocoApiConstants.GET_CODE_NETWORK_ERROR:
                return new BaseResp<>(ResponseStatus.NETWORK_ERROR, ResponseStatus.NETWORK_ERROR);
            case QoocoApiConstants.SIGN_UP_EXISTED_EMAIL:
                return new BaseResp<>(ResponseStatus.EMAIL_IS_EXISTED, ResponseStatus.EMAIL_IS_EXISTED);
            default:
                return new BaseResp<>(ResponseStatus.BAD_REQUEST, ResponseStatus.BAD_REQUEST);
        }
    }

    @Override
    public BaseResp<UserInfoDTO> signUpWithSystemAndClientInfo(SignUpSystemWithClientInfoReq req) {
        Validation.validateEmail(req.getSignUpSystemReq().getEmail());
        Validation.validateUsername(req.getSignUpSystemReq().getUsername());
        Validation.validatePassword(req.getSignUpSystemReq().getPassword());
        this.validateClientInfo(req.getClientInfo());

        SignUpSystem signUpSystem = new SignUpSystem();
        signUpSystem.setUsername(req.getSignUpSystemReq().getUsername());
        signUpSystem.setEmail(req.getSignUpSystemReq().getEmail());
        signUpSystem.setPassword(req.getSignUpSystemReq().getPassword());

        //Integrate with external API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Gson gson = new GsonBuilder().create();
        String mapBody = QoocoApiConstants.REPORT_DATA.replace(QoocoApiConstants.REPLACEMENT_REQUEST_BODY, gson.toJson(signUpSystem));
        SignUpWithSystemAccount response = HttpHelper.doPost(qoocoService.getQoocoApiPath(Api.SERVICE_REGISTER_SYSTEM_ACCOUNT),
                mapBody, headers, SignUpWithSystemAccount.class, false);

        switch (response.getErrorCode()) {
            case QoocoApiConstants.OK_CODE:
                UserInfoDTO userInfoDTO = doSaveProfileUserOrFitUser(
                        response.getUserId(),
                        signUpSystem.getUsername(),
                        signUpSystem.getEmail(),
                        response.getSignInId(),
                        null,
                        req.getClientInfo().getAppId(),
                        req.getClientInfo());

                return new BaseResp<>(userInfoDTO, ResponseStatus.SUCCESS);
            case QoocoApiConstants.SIGN_UP_EXISTING_USERNAME:
                throw new InvalidParamException(ResponseStatus.USER_NAME_EXISTING);
            case QoocoApiConstants.SIGN_UP_USER_NOT_CREATE_SUCCESS:
                throw new InvalidParamException(ResponseStatus.USER_NOT_CREATE_SUCCESS);
            case QoocoApiConstants.GET_CODE_NETWORK_ERROR:
                throw new InvalidParamException(ResponseStatus.NETWORK_ERROR);
            case QoocoApiConstants.SIGN_UP_EXISTED_EMAIL:
                throw new InvalidParamException(ResponseStatus.EMAIL_IS_EXISTED);
            default:
                throw new InvalidParamException(ResponseStatus.BAD_REQUEST);
        }
    }

    @Override
    public BaseResp doGenerateCode(GenerateCodeReq request) {
        ResponseStatus status = Validation.validateEmail(request.getEmail());
        if (ResponseStatus.SUCCESS != status) {
            logger.warn(status);
            throw new InvalidParamException(status);
        }
        QoocoResponseBase response;
        String urlContact = qoocoService.getQoocoApiPath(Api.SERVICE_CHECK_USER_CONTACT).replace(QoocoApiConstants.CONTACT_DATA, request.getEmail());
        try {
            QoocoResponseBase checkExistedEmail = HttpHelper.doGet(urlContact, QoocoResponseBase.class, false);

            if (checkExistedEmail.isExist()) {
                throw new InvalidParamException(ResponseStatus.EMAIL_IS_EXISTED);
            }
            response = qoocoService.generateCodeToQoocoSystem(request.getEmail());
        } catch (ResourceAccessException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
        }

        if (Objects.nonNull(response)) {
            switch (response.getErrorCode()) {
                case QoocoApiConstants.OK_CODE:
                    logger.warn(ResponseStatus.SUCCESS);
                    return new BaseResp(ResponseStatus.SUCCESS);

                case QoocoApiConstants.GET_CODE_INTERNAL_SERVER_ERROR:
                    throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);

                case QoocoApiConstants.GET_CODE_FAILED_TO_SEND:
                    throw new InvalidParamException(ResponseStatus.FAILED_TO_SEND_CODE);

                case QoocoApiConstants.GET_CODE_EMAIL_IS_ALREADY_USED:
                    throw new InvalidParamException(ResponseStatus.EMAIL_IS_ALREADY_USED);

                case QoocoApiConstants.GET_CODE_NETWORK_ERROR:
                    throw new InvalidParamException(ResponseStatus.NETWORK_ERROR);

                case QoocoApiConstants.GET_CODE_MOBILE_NUM_FORMAT_INCORRECT:
                    throw new InvalidParamException(ResponseStatus.CONTACT_WRONG_FORMAT);

                default:
                    throw new InvalidParamException(ResponseStatus.BAD_REQUEST, response.getErrorDescription());
            }
        }
        throw new InvalidParamException(ResponseStatus.BAD_REQUEST);
    }

    @Override
    public BaseResp doVerifyCode(VerifyCodeReq verifyCodeReq) {
        Validation.validateEmail(verifyCodeReq.getEmail());

        Validation.validateVerificationCode(verifyCodeReq.getCode());
        QoocoResponseBase response;
        //Call service of other system API
        try {
            response = qoocoService.verifyCodeToQoocoSystem(verifyCodeReq.getEmail(), verifyCodeReq.getCode());
        } catch (ResourceAccessException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
        }

        if (Objects.nonNull(response)) {
            switch (response.getErrorCode()) {
                case QoocoApiConstants.OK_CODE:
                    logger.warn(ResponseStatus.SUCCESS);
                    return new BaseResp<>(ResponseStatus.SUCCESS);

                case QoocoApiConstants.VERIFICATION_CODE_NOT_FOUND:
                    throw new InvalidParamException(ResponseStatus.VERIFICATION_CODE_NOT_FOUND);

                case QoocoApiConstants.VERIFICATION_CODE_NOT_MATCH:
                    throw new InvalidParamException(ResponseStatus.VERIFICATION_CODE_NOT_MATCH);

                case QoocoApiConstants.VERIFICATION_CODE_EXPIRED:
                    throw new InvalidParamException(ResponseStatus.VERIFICATION_CODE_EXPIRED);

                case QoocoApiConstants.VERIFICATION_CODE_SERVER_ERROR:
                    throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    @Deprecated
    @Override
    public BaseResp loginWithSystemAccount(String username, String password, String appId) {
        validateLoginRequest(username, password);
        if (LOGIN_VERIFIED_QOOCO_SYSTEM) {
            try {
                LoginResponse response = qoocoService.loginToQoocoSystem(username, password);
                return handleQoocoLogin(response, appId);
            } catch (ResourceAccessException ex) {
                logger.error(ex.getMessage());
                throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
            }
        } else {
            // FIXME REMOVE AFTER SERVER IS BACK
            return fixLoginDev(username, null, appId);
        }

    }

    private BaseResp fixLoginDev(String username, ClientInfoReq clientInfo, String appId) {
        UserProfile userProfile = userProfileService.findByUsername(username);
        if (Objects.nonNull(userProfile)) {
            UserInfoDTO userInfoDTO = doSaveProfileUserOrFitUser(
                    userProfile.getUserProfileId(),
                    userProfile.getUsername(),
                    userProfile.getEmail(),
                    "XXXXXX",
                    "XXXXXX",
                    appId,
                    clientInfo);
            return new UserLoginResp(userInfoDTO);
        } else {
            throw new InvalidParamException(ResponseStatus.USERNAME_NOT_EXIST);
        }
    }

    @Override
    public BaseResp loginWithSystemAccountWithClientInfo(String username, String password, ClientInfoReq clientInfo, String appId) {
        validateLoginRequest(username, password);
        this.validateClientInfo(clientInfo);
        if (LOGIN_VERIFIED_QOOCO_SYSTEM) {
            try {
                LoginResponse response = qoocoService.loginToQoocoSystem(username, password);
                return handleQoocoLoginWithClientInfo(response, clientInfo, appId);
            } catch (ResourceAccessException ex) {
                logger.error(ex.getMessage());
                throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
            }
        } else {
            // FIXME REMOVE AFTER SERVER IS BACK
            return fixLoginDev(username, clientInfo, appId);
        }
    }

    private void validateLoginRequest(String username, String password) {
        if (StringUtils.isBlank(username)) {
            throw new InvalidParamException(ResponseStatus.USERNAME_EMPTY);
        }

        if (StringUtils.isBlank(password)) {
            throw new InvalidParamException(ResponseStatus.PASSWORD_IS_EMPTY);
        }
    }

    @Override
    public BaseResp socialLogin(SocialLoginReq loginReq) {
        if (Objects.isNull(loginReq.getOauthId())) {
            throw new InvalidParamException(ResponseStatus.SOCIAL_LOGIN_EMPTY_AUTH_ID);
        }

        if (Objects.isNull(loginReq.getOauthProvider())) {
            throw new InvalidParamException(ResponseStatus.SOCIAL_LOGIN_EMPTY_AUTH_PROVIDER);
        }
        try {
            LoginResponse response = qoocoService.socialLogin(loginReq);
            return handleQoocoLogin(response, PROFILE_APP.appId());
        } catch (ResourceAccessException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
        }
    }

    @Override
    public BaseResp socialLoginWithClientInfo(SocialLoginWithClientInfoReq req) {
        if (Objects.isNull(req.getLoginReq().getOauthId())) {
            throw new InvalidParamException(ResponseStatus.SOCIAL_LOGIN_EMPTY_AUTH_ID);
        }

        if (Objects.isNull(req.getLoginReq().getOauthProvider())) {
            throw new InvalidParamException(ResponseStatus.SOCIAL_LOGIN_EMPTY_AUTH_PROVIDER);
        }
        this.validateClientInfo(req.getClientInfo());
        try {
            LoginResponse response = qoocoService.socialLogin(req.getLoginReq());
            return handleQoocoLoginWithClientInfo(response, req.getClientInfo(), PROFILE_APP.appId());
        } catch (ResourceAccessException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
        }
    }

    @Deprecated
    private BaseResp handleQoocoLogin(@NotNull LoginResponse loginResponse, String appId) {
        switch (loginResponse.getErrorCode()) {
            case QoocoApiConstants.LOGIN_SUCCESS:
                UserInfoDTO userInfoDTO = doSaveProfileUserOrFitUser(
                        loginResponse.getUserId(),
                        loginResponse.getUsername(),
                        loginResponse.getEmail(),
                        loginResponse.getSessionId(),
                        loginResponse.getSessionId(),
                        appId);
                return new UserLoginResp(userInfoDTO);
            case QoocoApiConstants.LOGIN_USERNAME_ERROR:
                throw new InvalidParamException(ResponseStatus.USERNAME_NOT_EXIST);
            case QoocoApiConstants.LOGIN_PASSWORD_ERROR:
                throw new InvalidParamException(ResponseStatus.PASSWORD_WRONG);
            case QoocoApiConstants.LOGIN_USERNAME_FOUND_ON_EXTERNAL_SERVER_ERROR:
                throw new InvalidParamException(ResponseStatus.USERNAME_FOUND_ON_EXTERNAL_SERVER);
            case QoocoApiConstants.LOGIN_SERVER_ERROR:
            default:
                throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private UserInfoDTO doSaveProfileUserOrFitUser(Long userId, String username, String email, String signingId, String sessionId, String appId) {
        return doSaveProfileUserOrFitUser(userId, username, email, signingId, sessionId, appId, null);
    }

    private UserInfoDTO doSaveProfileUserOrFitUser(Long userId, String username, String email, String signingId, String sessionId, String appId, ClientInfoReq clientInfo) {
        Company company = null;
        BaseEntity user = null;
        if(SELECT_APP.appId().equals(appId)){
            user = doSaveFitUserWhenLogin(userId, username, email);
            //TODO: Get Last company in next time
            company = companyService.getDefaultCompanyOfUser(userId);
        } else if(PROFILE_APP.appId().equals(appId)){
            user = doSaveProfileUserWhenLogin(userId, username, email);
        }

        UserAccessToken userAccessToken = doCreateUserAccessToken(userId, username, signingId, sessionId, clientInfo, company);
        return new UserInfoDTO(user, company, userAccessToken, null);
    }

    private BaseResp handleQoocoLoginWithClientInfo(@NotNull LoginResponse loginResponse, ClientInfoReq clientInfo, String appId) {
        switch (loginResponse.getErrorCode()) {
            case QoocoApiConstants.LOGIN_SUCCESS:
                UserInfoDTO userInfoDTO = doSaveProfileUserOrFitUser(
                        loginResponse.getUserId(),
                        loginResponse.getUsername(),
                        loginResponse.getEmail(),
                        loginResponse.getSessionId(),
                        loginResponse.getSessionId(),
                        appId,
                        clientInfo);
                return new UserLoginResp(userInfoDTO);
            case QoocoApiConstants.LOGIN_USERNAME_ERROR:
                throw new InvalidParamException(ResponseStatus.USERNAME_NOT_EXIST);
            case QoocoApiConstants.LOGIN_PASSWORD_ERROR:
                throw new InvalidParamException(ResponseStatus.PASSWORD_WRONG);
            case QoocoApiConstants.LOGIN_USERNAME_FOUND_ON_EXTERNAL_SERVER_ERROR:
                throw new InvalidParamException(ResponseStatus.USERNAME_FOUND_ON_EXTERNAL_SERVER);
            case QoocoApiConstants.LOGIN_SERVER_ERROR:
            default:
                throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserFit doSaveFitUserWhenLogin(Long userId, String username, String email) {
        UserFit userFit = userFitService.findById(userId);
        if (Objects.isNull(userFit)) {
            userFit = new UserFit(userId);
            userFit.setUsername(username);
            userFit.setEmail(email);
            userFit = userFitService.save(userFit);
        }
        return userFit;
    }

    private UserProfile doSaveProfileUserWhenLogin(Long userId, String username, String email) {
        UserProfile userProfile = userProfileService.findById(userId);
        if (Objects.isNull(userProfile)) {
            userProfile = new UserProfile(userId);
            userProfile.setUsername(username);
            userProfile.setEmail(email);
            userProfile = userProfileService.save(userProfile);
        }
        return userProfile;
    }

    @Override
    public BaseResp doForgotPassword(@NotNull ForgotPasswordReq forgotPasswordReq) {
        if (StringUtils.isBlank(forgotPasswordReq.getUsername())
                && StringUtils.isBlank(forgotPasswordReq.getEmail())) {
            throw new InvalidParamException(ResponseStatus.USERNAME_OR_EMAIL_EMPTY);
        }
        try {
            return qoocoService.retrievePassword(
                    forgotPasswordReq.getUsername(),
                    forgotPasswordReq.getEmail());
        } catch (ResourceAccessException ex) {
            logger.error(ex.getMessage());
            throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
        }
    }

    private UserAccessToken doCreateUserAccessToken(Long userId, String username, String signInId, String sessionId, ClientInfoReq clientInfoReq, Company company) {
        if (Objects.nonNull(userId) && Objects.nonNull(username)) {
            String channelId = null;
            int deviceType = 0;
            String accessToken = StringUtil.generateAccessToken(username, userId.toString());
            if (Objects.nonNull(clientInfoReq)) {
                channelId = clientInfoReq.getChannelId();
                List<ClientInfo> foundClientInfoList = clientInfoService.findClientInfo(clientInfoReq.getAppId(), clientInfoReq.getPlatform(), userId, channelId);
                if (CollectionUtils.isNotEmpty(foundClientInfoList)) {
                    foundClientInfoList.forEach(ci -> updateLogoutStatusByToken(ci.getToken()));
                }
                ClientInfo clientInfo = new ClientInfo(clientInfoReq);
                clientInfo.setUserProfileId(userId);
                clientInfo.setToken(accessToken);
                clientInfo.setLoginTime(DateUtils.nowUtcForOracle());
                clientInfoService.save(clientInfo);

                switch (clientInfo.getPlatform().toUpperCase()) {
                    case Const.Platform.WEB:
                        deviceType = DeviceType.WEB;
                        break;
                    case Const.Platform.PC:
                        deviceType = DeviceType.PC;
                        break;
                    case Const.Platform.ANDROID:
                        deviceType = DeviceType.ANDROID;
                        break;
                    case Const.Platform.IOS:
                        deviceType = DeviceType.IOS;
                        break;
                    case Const.Platform.WP:
                        deviceType = DeviceType.WP;
                        break;
                    default:
                        break;
                }
            }
            UserAccessToken userAccessToken = new UserAccessToken(accessToken, accessToken, userId, new UserProfile(userId));
            userAccessToken.setSignInId(signInId);
            userAccessToken.setSessionId(sessionId);
            userAccessToken.setChannelId(channelId);
            userAccessToken.setDeviceType(deviceType);
            if (Objects.nonNull(company)) {
                userAccessToken.setCompanyId(company.getCompanyId());
            }
            return userAccessTokenService.save(userAccessToken);
        }
        return null;
    }

    @Override
    public BaseResp doLogoutWithSystemAccount(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        String token = authenticatedUser.getToken();
        updateLogoutStatusByToken(token);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private void updateLogoutStatusByToken(String token) {
        clientInfoService.updateLogoutTime(token);
        userAccessTokenService.deleteByToken(token);
        socketConnectionDocService.updateLogout(token);
        conversationDocService.removeAccessTokenInUserKey(token);
    }

    @Override
    public BaseResp updateChannelId(String channelId, Authentication authentication) {
        if (StringUtils.isEmpty(channelId)) {
            throw new InvalidParamException(ResponseStatus.NOTIFICATION_CHANNEL_ID_EMPTY);
        }
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        userAccessTokenService.updateChannelIdByAccessToken(authenticatedUser.getToken(), channelId);
        return new BaseResp();
    }

    @Override
    public BaseResp updateClientInfo(ClientInfoReq clientInfoReq, String token) {
        userAccessTokenService.updateChannelIdByAccessToken(token, clientInfoReq.getChannelId());

        this.validateClientInfo(clientInfoReq);
        ClientInfo clientInfo = clientInfoService.findByToken(token);
        clientInfo.setAppId(clientInfoReq.getAppId());
        clientInfo.setAppVersion(clientInfoReq.getAppVersion());
        clientInfo.setDeviceModel(clientInfoReq.getDeviceModel());
        clientInfo.setPlatform(clientInfoReq.getPlatform());
        clientInfo.setOsVersion(clientInfoReq.getOsVersion());
        clientInfo.setDeviceToken(clientInfoReq.getDeviceToken());
        clientInfo.setChannelId(clientInfoReq.getChannelId());
        ClientInfo updated = clientInfoService.save(clientInfo);
        if (Objects.nonNull(updated)) {
            return new BaseResp<>(ResponseStatus.SUCCESS);
        }
        return new BaseResp<>(ResponseStatus.SAVE_FAIL);
    }

    private void validateClientInfo(ClientInfoReq clientInfo) {
        if (Objects.isNull(clientInfo.getAppId())) {
            throw new InvalidParamException(ResponseStatus.APP_ID_IS_EMPTY);
        }

        if (!(SELECT_APP.appId().equals(clientInfo.getAppId())
                || clientInfo.getAppId().equals(PROFILE_APP.appId())
                || clientInfo.getAppId().equals(WEB_ADMIN_APP.appId()))) {
            throw new InvalidParamException(ResponseStatus.APP_ID_INVALID);
        }

        if (Objects.isNull(clientInfo.getAppVersion())) {
            throw new InvalidParamException(ResponseStatus.APP_VERSION_IS_EMPTY);
        }

        if (Objects.isNull(clientInfo.getPlatform())) {
            throw new InvalidParamException(ResponseStatus.PLATFORM_IS_EMPTY);
        }
    }
}
