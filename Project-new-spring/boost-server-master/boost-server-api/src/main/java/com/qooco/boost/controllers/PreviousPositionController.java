package com.qooco.boost.controllers;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/4/2018 - 2:58 PM
 */

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessUserCurriculumVitaeService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserPreviousPositionDTO;
import com.qooco.boost.models.user.UserPreviousPositionReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.qooco.boost.enumeration.ProfileStep.PREVIOUS_EXPERIENCE_STEP;

@Api(tags = "User Previous Position", description = "User Previous Position In Curriculum Vitae")
@CrossOrigin
@RestController
@RequestMapping(URLConstants.USER_PATH)
public class PreviousPositionController extends BaseController {

    private static final String PREVIOUS_POSITION_SAVE = URLConstants.USER_PREVIOUS_POSITION_PATH + URLConstants.SAVE_METHOD;
    private static final String PREVIOUS_POSITION_GET = URLConstants.USER_PREVIOUS_POSITION_PATH + URLConstants.GET_METHOD;
    private static final String PREVIOUS_POSITION_DELETE = URLConstants.USER_PREVIOUS_POSITION_PATH + URLConstants.DELETE_METHOD;
    private static final String PREVIOUS_POSITION_GET_USER_PROFILE = URLConstants.USER_PREVIOUS_POSITION_PATH + "/getByUserProfile";

    @Autowired
    private BusinessUserCurriculumVitaeService businessUserCurriculumVitaeService;

    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @ApiOperation(value = "Save user previous experience profile", httpMethod = "POST", response = PreviousPositionsResp.class,
            notes = notesSave
    )
    @PostMapping(PREVIOUS_POSITION_SAVE)
    @PreAuthorize("isAuthenticated()")
    public Object doSavePreviousPosition(@RequestBody UserPreviousPositionReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
		BaseResp result = businessUserCurriculumVitaeService.saveUserPreviousPosition(request, authentication);
        businessProfileAttributeEventService.onUserProfileStep(authentication, PREVIOUS_EXPERIENCE_STEP);
        return success(result);
    }

    @ApiOperation(value = "Get user previous position", httpMethod = "GET", response = PreviousPositionResp.class,
            notes = notes
    )
    @GetMapping(PREVIOUS_POSITION_GET)
    @PreAuthorize("isAuthenticated()")
    public Object doGetPreviousPositionById(@RequestParam(value = "id", required = false) Long id, Authentication authentication) {
        BaseResp result = businessUserCurriculumVitaeService.getUserPreviousPositionById(id, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get user previous position base on user profile id", httpMethod = "GET", response = PreviousPositionsResp.class,
            notes = notes
    )
    @GetMapping(PREVIOUS_POSITION_GET_USER_PROFILE)
    @PreAuthorize("isAuthenticated()")
    public Object doGetPreviousPositionByUserProfileId(@RequestParam(value = "id", required = false) Long id, Authentication authentication) {
        if(Objects.isNull(id)) {
            id = ((AuthenticatedUser)authentication.getPrincipal()).getId();
        }
        BaseResp result = businessUserCurriculumVitaeService.getUserPreviousPositionByUserProfileId(id, authentication);
        return success(result);
    }

    @ApiOperation(value = "Delete user previous position", httpMethod = "DELETE", response = BaseResp.class,
            notes = notes
    )
    @DeleteMapping(PREVIOUS_POSITION_DELETE)
    @PreAuthorize("isAuthenticated() and @boostSecurity.isPreviousPositionOwner(authentication, #id)")
    public Object doDeletePreviousPosition(@RequestParam(value = "id") Long id) {
        BaseResp result = businessUserCurriculumVitaeService.deleteUserPreviousPositions(id);
        return success(result);
    }

    /**
     * ==================== To show swagger ===============
     **/
    final String notes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE;

    final String notesSave = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.COMPANY_NAME_IS_REQUIRED + " : " + StatusConstants.COMPANY_NAME_IS_REQUIRED_MESSAGE
            + "<br>" + StatusConstants.POSITION_NAME_IS_REQUIRED + " : " + StatusConstants.POSITION_NAME_IS_REQUIRED_MESSAGE
            + "<br>" + StatusConstants.START_DATE_IS_REQUIRED + " : " + StatusConstants.START_DATE_IS_REQUIRED_MESSAGE
            + "<br>" + StatusConstants.START_DATE_AFTER_NOW + " : " + StatusConstants.START_DATE_AFTER_NOW_MESSAGE
            + "<br>" + StatusConstants.START_DATE_AFTER_END_DATE + " : " + StatusConstants.START_DATE_AFTER_END_DATE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_USER_PREVIOUS_JOB + " : " + StatusConstants.NOT_FOUND_USER_PREVIOUS_JOB_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_CURRENCY + " : " + StatusConstants.NOT_FOUND_CURRENCY_MESSAGE;

    class PreviousPositionsResp extends BaseResp<List<UserPreviousPositionDTO>> {
    }

    class PreviousPositionResp extends BaseResp<UserPreviousPositionDTO> {
    }
}
