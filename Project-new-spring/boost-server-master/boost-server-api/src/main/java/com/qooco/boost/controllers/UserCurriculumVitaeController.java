package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessUserCurriculumVitaeService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserCVAttributeDTO;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.request.SocialLinkReq;
import com.qooco.boost.models.user.ExportUserCvPdfReq;
import com.qooco.boost.models.user.UserCurriculumVitaeReq;
import com.qooco.boost.models.user.UserProfileStep;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static com.qooco.boost.enumeration.ProfileStep.JOB_PROFILE_STEP;
import static com.qooco.boost.enumeration.ProfileStep.PERSONAL_INFORMATION_STEP;

@Api(tags = "UserCurriculumVitae")
@CrossOrigin
@RestController
@RequestMapping(URLConstants.USER_PATH + "/" + URLConstants.USER_CV_PATH)
public class UserCurriculumVitaeController extends BaseController {

    @Autowired
    private BusinessUserCurriculumVitaeService businessUserCurriculumVitaeService;

    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @ApiOperation(value = "Save user cv", httpMethod = "POST", response = UserCurriculumVitaeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INVALID_SALARY_RANGE + ": " + StatusConstants.INVALID_SALARY_RANGE_MESSAGE
                    + "<br>" + "\n" + StatusConstants.REQUIRED_JOB + ": " + StatusConstants.REQUIRED_JOB_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_JOB + ": " + StatusConstants.NOT_FOUND_JOB_MESSAGE
                    + "<br>" + "\n" + StatusConstants.CURRENCY_CODE_IS_REQUIRED + ": " + StatusConstants.CURRENCY_CODE_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_CURRENCY + ": " + StatusConstants.NOT_FOUND_CURRENCY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_EDUCATION + ": " + StatusConstants.NOT_FOUND_EDUCATION_MESSAGE
                    + "<br>" + "\n" + StatusConstants.REQUIRED_WORKING_HOUR + ": " + StatusConstants.REQUIRED_WORKING_HOUR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_WORKING_HOUR + ": " + StatusConstants.NOT_FOUND_WORKING_HOUR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.CONFLICT_WORKING_HOUR + ": " + StatusConstants.CONFLICT_WORKING_HOUR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_BENEFIT + ": " + StatusConstants.NOT_FOUND_BENEFIT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_SOFT_SKILL + ": " + StatusConstants.NOT_FOUND_SOFT_SKILL_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PREVIOUS_JOB + ": " + StatusConstants.NOT_FOUND_USER_PREVIOUS_JOB_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_YOUR_PREVIOUS_JOB + ": " + StatusConstants.NOT_YOUR_PREVIOUS_JOB_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + ": " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD)
    public Object saveUserCurriculumVitaeService(@RequestBody final UserCurriculumVitaeReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        request.setUserProfileId(authenticatedUser.getId());
        BaseResp resp = businessUserCurriculumVitaeService.saveUserCurriculumVitae(request, authentication);
        return success(resp);
    }

    @ApiOperation(value = "Save and Update user personal information", httpMethod = "POST", response = UserCurriculumVitaeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_SOFT_SKILL + ": " + StatusConstants.NOT_FOUND_SOFT_SKILL_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + ": " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD + URLConstants.PERSONAL_INFORMATION)
    public Object savePersonalInformation(@RequestBody final UserCurriculumVitaeReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        request.setUserProfileId(authenticatedUser.getId());
        BaseResp resp = businessUserCurriculumVitaeService.savePersonalInformation(request, authentication);
        businessProfileAttributeEventService.onUserProfileStep(authentication, PERSONAL_INFORMATION_STEP);
        return success(resp);
    }

    @ApiOperation(value = "Create and Update job profile", httpMethod = "POST", response = UserCurriculumVitaeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.REQUIRED_JOB + ": " + StatusConstants.REQUIRED_JOB_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_JOB + ": " + StatusConstants.NOT_FOUND_JOB_MESSAGE
                    + "<br>" + "\n" + StatusConstants.INVALID_SALARY_RANGE + ": " + StatusConstants.INVALID_SALARY_RANGE_MESSAGE
                    + "<br>" + "\n" + StatusConstants.SALARY_RANGE_IS_REQUIRED + ": " + StatusConstants.SALARY_RANGE_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.CURRENCY_CODE_IS_REQUIRED + ": " + StatusConstants.CURRENCY_CODE_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_CURRENCY + ": " + StatusConstants.NOT_FOUND_CURRENCY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_WORKING_HOUR + ": " + StatusConstants.NOT_FOUND_WORKING_HOUR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.CONFLICT_WORKING_HOUR + ": " + StatusConstants.CONFLICT_WORKING_HOUR_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_BENEFIT + ": " + StatusConstants.NOT_FOUND_BENEFIT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EXPECTED_START_DATE_IS_REQUIRED + ": " + StatusConstants.EXPECTED_START_DATE_IS_REQUIRED_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + ": " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD + URLConstants.JOB_PROFILE)
    public Object saveJobProfile(@RequestBody final UserCurriculumVitaeReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        BaseResp resp = new BaseResp();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        request.setUserProfileId(authenticatedUser.getId());
        if (authenticatedUser.getAppId().equals(PROFILE_APP.appId())) {
            resp = businessUserCurriculumVitaeService.saveJobProfile(request, authentication);
            businessProfileAttributeEventService.onUserProfileStep(authentication, JOB_PROFILE_STEP);
        }
        return success(resp);
    }

    @ApiOperation(value = "Get user cv", httpMethod = "GET", response = UserCurriculumVitaeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + ": " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_WRONG_FORMAT + ": " + StatusConstants.EMAIL_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_IS_EMPTY + ": " + StatusConstants.USER_NAME_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_WRONG_FORMAT + ": " + StatusConstants.USER_NAME_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_EXISTING + ": " + StatusConstants.USER_NAME_EXISTING_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_IS_EMPTY + ": " + StatusConstants.PASSWORD_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG_FORMAT + ": " + StatusConstants.PASSWORD_WRONG_FORMAT_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_METHOD)
    @Deprecated
    public Object getUserCurriculumVitaeService(@RequestParam(value = "userProfileId") long userProfileId,
                                                @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale) {
        BaseResp userProfile = businessUserCurriculumVitaeService.getUserCurriculumVitaeByUserProfileId(userProfileId, locale);
        return success(userProfile);
    }

    @ApiOperation(value = "Get user cv", httpMethod = "GET", response = UserCVAttributeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "\n" + StatusConstants.NOT_FOUND_USER_PROFILE + ": " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + "\n" + StatusConstants.EMAIL_WRONG_FORMAT + ": " + StatusConstants.EMAIL_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_IS_EMPTY + ": " + StatusConstants.USER_NAME_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_WRONG_FORMAT + ": " + StatusConstants.USER_NAME_WRONG_FORMAT_MESSAGE
                    + "<br>" + "\n" + StatusConstants.USER_NAME_EXISTING + ": " + StatusConstants.USER_NAME_EXISTING_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_IS_EMPTY + ": " + StatusConstants.PASSWORD_IS_EMPTY_MESSAGE
                    + "<br>" + "\n" + StatusConstants.PASSWORD_WRONG_FORMAT + ": " + StatusConstants.PASSWORD_WRONG_FORMAT_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public Object getUserCurriculumVitaeServiceV2(@RequestParam(value = "userProfileId") long userProfileId,
                                                  @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale) {
        BaseResp userProfile = businessUserCurriculumVitaeService.getUserCurriculumVitaeByUserProfileId(userProfileId, locale);
        return success(userProfile);
    }

    @ApiOperation(value = "Delete social link", httpMethod = "DELETE", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE + ": " + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_SOCIAL_LINK + ": " + StatusConstants.NOT_FOUND_SOCIAL_LINK_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(URLConstants.DELETE_METHOD)
    public Object deleteSocialLink(@RequestBody SocialLinkReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp resp = businessUserCurriculumVitaeService.deleteSocialLink(authenticatedUser.getId(), request.getSocialLink());
        return success(resp);
    }

    @ApiOperation(value = "Save value", httpMethod = "POST", response = UserCurriculumVitaeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD + URLConstants.STEP_PROFILE)
    public Object saveProfileStep(@RequestBody final UserProfileStep request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp resp = new BaseResp();
        if (PROFILE_APP.appId().equals(authenticatedUser.getAppId()) || (SELECT_APP.appId().equals(authenticatedUser.getAppId()))) {
            resp = businessUserCurriculumVitaeService.saveProfileStep(authenticatedUser.getId(), request, authentication);
        }
        return success(resp);
    }

    @ApiOperation(value = "Export user cv in pdf format", httpMethod = "POST", response = ExportUserCvPdfResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + ": " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE + ": " + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + ": " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.EXPORT_PDF)
    public Object exportCvPdf(@RequestBody ExportUserCvPdfReq exportUserCvPdfReq, Authentication authentication) {
        return success(businessUserCurriculumVitaeService.exportUserCvPdf(exportUserCvPdfReq, authentication));
    }

    private class UserCurriculumVitaeResp extends BaseResp<UserCurriculumVitaeDTO> {

    }

    private class UserCVAttributeResp extends BaseResp<UserCVAttributeDTO> {

    }

    private class ExportUserCvPdfResp extends BaseResp<String> {}
}
