package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessAssessmentService;
import com.qooco.boost.business.BusinessAssessmentTestHistoryService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BasePagerResp;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.assessment.AssessmentDTO;
import com.qooco.boost.models.dto.assessment.AssessmentHistoryDTO;
import com.qooco.boost.models.dto.assessment.QualificationDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Assessment")
@RestController
@RequestMapping(URLConstants.ASSESSMENT_PATH)
public class AssessmentController extends BaseController {

    @Autowired
    private BusinessAssessmentService assessmentService;
    @Autowired
    private BusinessAssessmentTestHistoryService testHistoryService;

    @ApiOperation(value = "Get assessments", httpMethod = "GET", response = AssessmentResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
                    + "Type description:"
                    + "<br>" + "1 : FREE"
                    + "<br>" + "2 : CLAIM"
                    + "<br>" + "3 : BUY"
                    + "<br>" + "4 : ALL"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_METHOD)
    public Object getAssessmentsByType(@RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                                       @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
                                       @RequestParam(value = "type", required = false) Integer type) {

        BaseResp assessments = assessmentService.getAssessmentsByType(page, size, type);
        return success(assessments);
    }

    @ApiOperation(value = "Delete assessment qualifications", httpMethod = "DELETE", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(URLConstants.ID_PATH)
    public Object deleteAssessment(@PathVariable(value = "id") Long id) {
        BaseResp assessments = assessmentService.deleteAssessment(id);
        return success(assessments);
    }

    @ApiOperation(value = "The api to get get my assessment qualifications.", httpMethod = "GET", response = QualificationResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ASSESSMENT_QUALIFICATION + URLConstants.GET_METHOD)
    public Object getUserQualification(Authentication authentication, @RequestParam(value = "scaleId", required = false) String scaleId) {
        AuthenticatedUser authenticatedUser= (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = assessmentService.getUserQualification(authenticatedUser.getId(), scaleId,false, authentication);
        return success(result);
    }

    @ApiOperation(value = "The api to get assessment qualifications by user profile", httpMethod = "GET", response = QualificationResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ASSESSMENT_QUALIFICATION)
    public Object getQualificationOfUser(Authentication authentication,
                                         @RequestParam(value = "scaleId", required = false) String scaleId,
                                         @RequestParam(value = "userProfileId", required = false) Long userProfileId) {
        BaseResp result = assessmentService.getUserQualification(userProfileId, scaleId,false, authentication);
        return success(result);
    }

    @ApiOperation(value = "The api to get my assessment qualifications at homepage screen. It is been oder oldest on the top", httpMethod = "GET", response = QualificationResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ASSESSMENT_QUALIFICATION_HOMEPAGE + URLConstants.GET_METHOD)
    public Object getUserQualificationForHomePage(Authentication authentication,
                                                  @RequestParam(value = "scaleId", required = false) String scaleId) {
        AuthenticatedUser authenticatedUser= (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = assessmentService.getUserQualification(authenticatedUser.getId(), scaleId, true, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get test history by assessment", httpMethod = "GET", response = TestHistoryResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ASSESSMENT_ID_NOT_EXIST + " : " + StatusConstants.ASSESSMENT_ID_NOT_EXIST_MESSAGE
    )

    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ASSESSMENT_TEST_HISTORY + URLConstants.GET_METHOD)
    public Object getTestHistoryByAssessmentId(@RequestParam(value = "assessmentId") Long assessmentId,
                                               @RequestParam(value = "userProfileId", required = false) Long userProfileId,
                                               @RequestParam(value = "timezone", required = false, defaultValue = "UTC") String timezone,
                                               Authentication authentication) {

        BaseResp testHistories = testHistoryService.getTestHistoryByAssessment(userProfileId, assessmentId, timezone, authentication);
        return success(testHistories);
    }

    @ApiOperation(value = "Synchronize data of each user", httpMethod = "GET", response = StringResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ASSESSMENT_SYNC_DATA_USER + URLConstants.GET_METHOD)
    public Object syncDataOfEachUser(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = assessmentService.syncDataOfEachUser(authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Synchronize all data from Qooco Service", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.SYNC_DATA)
    public Object syncData(Authentication authentication) {
        BaseResp result = assessmentService.syncDataFromQooco(authentication);
        return success(result);
    }

    private class TestHistoryResp extends BaseResp<AssessmentHistoryDTO> {}
    private class AssessmentResp extends BasePagerResp<PagedResult<AssessmentDTO>> {}
    private class QualificationResp extends BaseResp<List<QualificationDTO>> {}
    private class StringResp extends BaseResp<ResponseStatus> {}

}
