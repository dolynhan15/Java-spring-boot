package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessPersonalAssessmentService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.assessment.PersonalAssessmentFullDTO;
import com.qooco.boost.models.dto.assessment.UserPersonalityDTO;
import com.qooco.boost.models.dto.assessment.UserPersonalityResultDTO;
import com.qooco.boost.models.request.SubmitPersonalAssessmentReq;
import com.qooco.boost.models.response.SubmitPersonalAssessmentResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 2:18 PM
*/
@Api(tags = "Personal Assessment", value = URLConstants.PERSONAL_ASSESSMENT_PATH, description = "Personal Assessment Controller")
@RestController
@RequestMapping()
public class PersonalAssessmentController extends BaseController {

    @Autowired
    private BusinessPersonalAssessmentService businessPersonalAssessmentService;

    @ApiOperation(value = "Get all active personal assessment", httpMethod = "GET", response = PersonalAssessmentResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br> Param locale = "
                    + "<br>" + " LOCALE_EN_US = 'en_US'"
                    + "<br>" + " LOCALE_ZH_CN = 'zh_CN'"
                    + "<br>" + " LOCALE_ZH_TW = 'zh_TW'"
                    + "<br>" + " LOCALE_ID_ID = 'id_ID'"
                    + "<br>" + " LOCALE_JA_JP = 'ja_JP'"
                    + "<br>" + " LOCALE_TH_TH = 'th_TH'"
                    + "<br>" + " LOCALE_VI_VN = 'vi_VN'"
                    + "<br>" + " LOCALE_KO_KR = 'ko_KR'"
                    + "<br>" + " LOCALE_MY_MM = 'my_MM'"
                    + "<br> typeGraph: 1-TEQ, 2-BIG-5"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.PERSONAL_ASSESSMENT_PATH)
    public Object getPersonalAssessment(Authentication authentication,
                                        @RequestParam(value = "userProfileId", required = false) Long userProfileId,
                                        @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale) {
        BaseResp result = businessPersonalAssessmentService.getPersonalAssessment(userProfileId, locale, authentication);
        return success(result);
    }

    @ApiOperation(value = "Submit personal assessment test", httpMethod = "POST", response = UserPersonalityResult.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY_MESSAGE
                    + "<br> Param locale = "
                    + "<br>" + " LOCALE_EN_US = 'en_US'"
                    + "<br>" + " LOCALE_ZH_CN = 'zh_CN'"
                    + "<br>" + " LOCALE_ZH_TW = 'zh_TW'"
                    + "<br>" + " LOCALE_ID_ID = 'id_ID'"
                    + "<br>" + " LOCALE_JA_JP = 'ja_JP'"
                    + "<br>" + " LOCALE_TH_TH = 'th_TH'"
                    + "<br>" + " LOCALE_VI_VN = 'vi_VN'"
                    + "<br>" + " LOCALE_KO_KR = 'ko_KR'"
                    + "<br>" + " LOCALE_MY_MM = 'my_MM'"
                    + "<br> typeGraph: 1-TEQ, 2-BIG-5"
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.VERSION_2 + URLConstants.PERSONAL_ASSESSMENT_PATH + URLConstants.ID_PATH, method = RequestMethod.POST)
    public Object submitPersonalAssessmentV2(Authentication authentication,
                                             @PathVariable long id,
                                             @RequestBody SubmitPersonalAssessmentReq request) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessPersonalAssessmentService.savePersonalAssessmentAnswerV2(authenticatedUser.getId(), id, request);
        return success(result);
    }

    @ApiOperation(value = "Get list question per personal assessment", httpMethod = "GET", response = PersonalAssessmentQuestionList.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_PERSONAL_ASSESSMENT + " : " + StatusConstants.NOT_FOUND_PERSONAL_ASSESSMENT_MESSAGE
                    + "<br> typeGraph: 1-TEQ, 2-BIG-5"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.PERSONAL_ASSESSMENT_PATH + URLConstants.ID_PATH)
    public Object getQuestions(@PathVariable(value = "id") Long id,
                               @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale,
                               Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessPersonalAssessmentService.getQuestions(id, locale, authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Get personal test result", httpMethod = "GET", response = UserPersonalityResult.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_PERSONAL_ASSESSMENT + " : " + StatusConstants.NOT_FOUND_PERSONAL_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br> typeGraph: 1-TEQ, 2-BIG-5"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.VERSION_2 + URLConstants.PERSONAL_ASSESSMENT_PATH + URLConstants.ID_PATH + URLConstants.TEST_RESULT)
    public Object getPersonalTestResultV2(@PathVariable Long id,
                                          @RequestParam(value = "userProfileId", required = false) Long userProfileId,
                                          @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale,
                                          Authentication authentication) {
        BaseResp result = businessPersonalAssessmentService.getPersonalTestResultV2(userProfileId, locale, id, authentication);
        return success(result);
    }

    /* =========================================== Deprecate ============================================ */
    @Deprecated
    @ApiOperation(value = "Submit personal assessment test", httpMethod = "POST", response = SubmitPersonalAssessmentResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE_MESSAGE
                    + "<br>" + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY + " : " + StatusConstants.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY_MESSAGE
                    + "<br> Param locale = "
                    + "<br>" + " LOCALE_EN_US = 'en_US'"
                    + "<br>" + " LOCALE_ZH_CN = 'zh_CN'"
                    + "<br>" + " LOCALE_ZH_TW = 'zh_TW'"
                    + "<br>" + " LOCALE_ID_ID = 'id_ID'"
                    + "<br>" + " LOCALE_JA_JP = 'ja_JP'"
                    + "<br>" + " LOCALE_TH_TH = 'th_TH'"
                    + "<br>" + " LOCALE_VI_VN = 'vi_VN'"
                    + "<br>" + " LOCALE_KO_KR = 'ko_KR'"
                    + "<br>" + " LOCALE_MY_MM = 'my_MM'"
                    + "<br> typeGraph: 1-TEQ, 2-BIG-5"
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.PERSONAL_ASSESSMENT_PATH + URLConstants.ID_PATH, method = RequestMethod.POST)
    public Object submitPersonalAssessment(Authentication authentication,
                                           @PathVariable long id,
                                           @RequestBody SubmitPersonalAssessmentReq request) {
        saveRequestBodyToSystemLogger(request);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessPersonalAssessmentService.savePersonalAssessmentAnswer(authenticatedUser.getId(), id, request);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Get personal test result", httpMethod = "GET", response = UserPersonalityList.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_PERSONAL_ASSESSMENT + " : " + StatusConstants.NOT_FOUND_PERSONAL_ASSESSMENT_MESSAGE
                    + "<br> typeGraph: 1-TEQ, 2-BIG-5"
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = URLConstants.PERSONAL_ASSESSMENT_PATH + URLConstants.ID_PATH + URLConstants.TEST_RESULT)
    public Object getPersonalTestResult(@PathVariable Long id,
                                        @RequestParam(value = "userProfileId", required = false) Long userProfileId,
                                        @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale,
                                        Authentication authentication) {
        BaseResp result = businessPersonalAssessmentService.getPersonalTestResult(userProfileId, locale, id, authentication);
        return success(result);
    }

    private class PersonalAssessmentResp extends BaseResp<List<PersonalAssessmentFullDTO>> {
    }

    private class PersonalAssessmentQuestionList extends BaseResp<PersonalAssessmentFullDTO> {
    }

    private class UserPersonalityList extends BaseResp<List<UserPersonalityDTO>> {
    }

    private class UserPersonalityResult extends BaseResp<UserPersonalityResultDTO> {
    }
}
