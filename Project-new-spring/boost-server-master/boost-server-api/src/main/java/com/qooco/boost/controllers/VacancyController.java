package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessMatchingCandidateService;
import com.qooco.boost.business.BusinessVacancyArchiveService;
import com.qooco.boost.business.BusinessVacancyService;
import com.qooco.boost.constants.Const;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.user.CandidateClosedDTO;
import com.qooco.boost.models.dto.user.CandidateInfoDTO;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.dto.vacancy.VacancyClosedDTO;
import com.qooco.boost.models.dto.vacancy.VacancyClosedShortInfoDTO;
import com.qooco.boost.models.dto.vacancy.VacancyDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import com.qooco.boost.models.request.ClassifyCandidateReq;
import com.qooco.boost.models.request.EditVacancyRequest;
import com.qooco.boost.models.request.PageRequest;
import com.qooco.boost.models.request.VacancyV2Req;
import com.qooco.boost.models.response.CandidatesResp;
import com.qooco.boost.models.response.VacancyCandidateResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Vacancy", value = URLConstants.VACANCY_PATH, description = "Operations about vacancy")
@RestController
@RequestMapping()
public class VacancyController extends BaseController {
    @Autowired
    private BusinessVacancyService businessVacancyService;
    @Autowired
    private BusinessVacancyArchiveService businessVacancyArchiveService;

    @Autowired
    private BusinessMatchingCandidateService matchingCandidateService;


    @ApiOperation(value = "Get vacancy", httpMethod = "GET", response = VacancyResp.class, notes = note)
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH)
    @PreAuthorize("isAuthenticated()")
    public Object getVacancy(@PathVariable("id") Long id, Authentication authentication) {
        BaseResp result = businessVacancyService.get(id, authentication);
        return success(result);
    }

    @ApiOperation(value = "Delete vacancy", httpMethod = "DELETE", response = BaseResp.class, notes = noteDelete)
    @DeleteMapping(URLConstants.VACANCY_PATH + URLConstants.DELETE_METHOD)
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    public Object doDeletePreviousPosition(@PathVariable(value = "id") Long id) {
        BaseResp result = businessVacancyService.delete(id);
        return success(result);
    }

    @GetMapping(URLConstants.VACANCY_PATH + "/sync" + URLConstants.ID_PATH)
    public Object doSync(@PathVariable(value = "id") Long id) {
        BaseResp result = businessVacancyService.sync(id);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Get list opening vacancies by company", httpMethod = "GET", response = VacancyCandidateListResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.GET_VACANCIES_OF_COMPANY)
    public Object getOpeningVacanciesOfCompany(@RequestParam(value = "companyId") long companyId, Authentication authentication) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp vacancies = businessVacancyService.getOpeningVacanciesOfCompany(companyId, userProfileId, authentication);
        return success(vacancies);
    }

    @ApiOperation(value = "Matching candidates for vacancy", httpMethod = "GET", response = MatchingCandidateResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE)
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.MATCH_CANDIDATES)
    @PreAuthorize("isAuthenticated()")
    public Object matchingCandidateForVacancy(Authentication authentication, @RequestParam(value = "id") Long id,
                                              @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp result = matchingCandidateService.findMatchingCvForVacancy(userProfileId, id, size, authentication);
        return success(result);
    }

    @ApiOperation(value = "Matching candidates for vacancy, with comparison", httpMethod = "GET", response = MatchingCandidateComparisonResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
                    + "<br>" + StatusConstants.INVALID_SORT_TYPE + " : " + StatusConstants.INVALID_SORT_TYPE_MESSAGE
    )
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.CANDIDATE_OF_VACANCY + URLConstants.MATCH_CANDIDATES_COMPARE)
    @PreAuthorize("isAuthenticated()")
    public Object matchingCandidateForVacancyWithComparison(Authentication authentication,
                                                            @RequestParam(value = "id") Long id,
                                                            @RequestParam(value = "sortType") int sortType,
                                                            @RequestParam(value = "offset", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int offset,
                                                            @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp result = matchingCandidateService.findMatchingCvForVacancyWithComparison(userProfileId, id, size, offset, sortType, authentication);
        return success(result);
    }

    @ApiOperation(value = "Matching candidates for vacancy, with assessment sorting", httpMethod = "GET", response = MatchingCandidateComparisonResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
                    + "<br>" + StatusConstants.ASSESSMENT_NOT_BELONG_TO_VACANCY + " : " + StatusConstants.ASSESSMENT_NOT_BELONG_TO_VACANCY_MESSAGE
    )
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.CANDIDATE_OF_VACANCY + URLConstants.CERTIFICATE)
    @PreAuthorize("isAuthenticated()")
    public Object matchingCandidateForVacancyWithAssessmentSorting(Authentication authentication,
                                                                   @RequestParam(value = "id") Long id,
                                                                   @RequestParam(value = "assessmentId") long assessmentId,
                                                                   @RequestParam(value = "offset", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int offset,
                                                                   @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp result = matchingCandidateService.findMatchingCvForVacancyWithAssessmentSort(userProfileId, id, size, offset, assessmentId, authentication);
        return success(result);
    }

    @ApiOperation(value = "Classify the candidate for a vacancy", httpMethod = "POST", response = BaseResp.class, notes = noteClassifyCandidate)
    @PostMapping(URLConstants.VACANCY_PATH + URLConstants.CLASSIFY_CANDIDATE_FOR_VACANCY)
    @PreAuthorize("isAuthenticated()")
    public Object doClassifyCandidateForVacancy(@RequestBody ClassifyCandidateReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessVacancyService.classifyCandidate(request, authentication);
        return success(result);
    }

    /**
     * ==================== Version 2 ===============
     **/

    @ApiOperation(value = "Save vacancy which default appointment", httpMethod = "POST", response = VacancyResp.class, notes = noteV2)
    @PostMapping(URLConstants.VERSION_2 + URLConstants.VACANCY_PATH)
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #request.companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    public Object doSaveVacancyV2(@RequestBody VacancyV2Req request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessVacancyService.saveV2(null, request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Edit opening vacancy", httpMethod = "PUT", response = VacancyResp.class, notes = noteEditVacancy)
    @PutMapping(URLConstants.VERSION_2 + URLConstants.VACANCY_PATH + URLConstants.ID_PATH)
    @PreAuthorize("isAuthenticated()")
    public Object editVacancy(@PathVariable(value = "id") Long id, @RequestBody EditVacancyRequest request, Authentication authentication) {
        request.setId(id);
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessVacancyService.editVacancy(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get list opening vacancies of company - Version 2", httpMethod = "GET", response = VacancyCandidateRespPage.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH)
    public Object getOpeningVacancies(@RequestParam(value = "companyId") long companyId,
                                      @Valid PageRequest request,
                                      Authentication authentication) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp vacancies = businessVacancyService.getOpeningVacancies(companyId, userProfileId, request.getPage(), request.getSize(), authentication);
        return success(vacancies);
    }

    @ApiOperation(value = "Get list vacancies having appointments of company", httpMethod = "GET", response = VacancyCandidateRespPage.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.APPOINTMENT)
    public Object getOpeningVacanciesHavingAppointments(@RequestParam(value = "companyId") long companyId,
                                                        @Valid PageRequest request,
                                                        Authentication authentication) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp result = businessVacancyService.getOpeningVacanciesHavingAppointments(companyId, userProfileId, request.getPage(), request.getSize(), authentication);
        return success(result);
    }


    @ApiOperation(value = "Get list candidates to close for a vacancy ", httpMethod = "GET", response = VacancyCandidatesToClose.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH + URLConstants.CANDIDATE_OF_VACANCY)
    public Object getCandidatesOfVacancy(@PathVariable(value = "id") long id,
                                         @Valid PageRequest request,
                                         Authentication authentication) {
        BaseResp result = businessVacancyService.getCandidatesOfVacancy(id, request.getPage(), request.getSize(), authentication);
        return success(result);
    }

    @ApiOperation(value = "Close candidate of vacancy ", httpMethod = "PATCH", response = VacancyResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_CLOSED + " : " + StatusConstants.VACANCY_IS_CLOSED_MESSAGE
                    + "<br>" + StatusConstants.CANDIDATE_IS_ALREADY_CLOSED + " : " + StatusConstants.CANDIDATE_IS_ALREADY_CLOSED_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @PatchMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH + URLConstants.CANDIDATE_OF_VACANCY + URLConstants.CANDIDATE_ID + URLConstants.CLOSE)
    public Object closeCandidateOfVacancy(@PathVariable(value = "id") long id,
                                          @PathVariable(value = "candidateId") long candidateId,
                                          Authentication authentication) {
        BaseResp result = businessVacancyService.closeCandidateOfVacancy(id, candidateId, authentication);
        return success(result);
    }

    @ApiOperation(value = "Decline candidate of vacancy", httpMethod = "PATCH", response = VacancyResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.CANDIDATE_IS_ALREADY_CLOSED + " : " + StatusConstants.CANDIDATE_IS_ALREADY_CLOSED_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL + " : " + StatusConstants.NOT_FOUND_APPOINTMENT_DETAIL_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @PatchMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH + URLConstants.CANDIDATE_OF_VACANCY + URLConstants.CANDIDATE_ID + URLConstants.DECLINE)
    public Object declineCandidateOfVacancy(@PathVariable(value = "id") long id,
                                            @PathVariable(value = "candidateId") long candidateId,
                                            Authentication authentication) {
        return success(businessVacancyService.declineCandidateOfVacancy(id, candidateId, authentication));
    }

    @ApiOperation(value = "Suspend a vacancy ", httpMethod = "PATCH", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.SUSPENDED_DAYS_IS_INVALID + " : " + StatusConstants.SUSPENDED_DAYS_IS_INVALID_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
                    + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
                    + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_CURRENCY + " : " + StatusConstants.NOT_FOUND_CURRENCY_MESSAGE
                    + "<br>" + StatusConstants.INVALID_SALARY_RANGE + " : " + StatusConstants.INVALID_SALARY_RANGE_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @PatchMapping(URLConstants.VACANCY_PATH + URLConstants.ID + URLConstants.SUSPEND_VACANCY)
    public Object suspendVacancy(@PathVariable(value = "id") long id,
                                 @RequestParam(value = "suspendedDays", required = false) Integer suspendedDays,
                                 Authentication authentication) {
        BaseResp result = businessVacancyService.suspendVacancy(id, suspendedDays, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get suspend vacancies of company with short information ", httpMethod = "GET", response = VacancySuspendShortInfoResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.SUSPEND_VACANCY + URLConstants.SHORT_INFORMATION)
    public Object getSuspendVacancyWithShortInfo(@Valid PageRequest pageRequest,
                                                 Authentication authentication) {
        BaseResp result = businessVacancyArchiveService.getSuspendVacancyWithShortInfo(pageRequest.getPage(), pageRequest.getSize(), authentication);
        return success(result);
    }

    @ApiOperation(value = "Get vacancies with short information of company which have closed candidate", httpMethod = "GET", response = VacancyClosedShortInfoResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.CLOSE + URLConstants.SHORT_INFORMATION)
    public Object getClosedVacancyWithShortInfo(@Valid PageRequest pageReq,
                                                Authentication authentication) {
        BaseResp result = businessVacancyArchiveService.getClosedVacancyWithShortInfo(pageReq.getPage(), pageReq.getSize(), authentication);
        return success(result);
    }

    @ApiOperation(value = "Get closed candidates of vacancy", httpMethod = "GET", response = VacancyCandidateClosedResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH + URLConstants.CANDIDATE_OF_VACANCY + URLConstants.CLOSE)
    public Object getClosedCandidateOfVacancy(@PathVariable("id") Long id,
                                              @Valid PageRequest pageReq,
                                              Authentication authentication) {
        BaseResp result = businessVacancyArchiveService.getClosedCandidateOfVacancy(id, pageReq.getPage(), pageReq.getSize(), authentication);
        return success(result);
    }

    @ApiOperation(value = "Restore vacancy", httpMethod = "PUT", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.CURRENCY_ID_IS_REQUIRED + " : " + StatusConstants.CURRENCY_ID_IS_REQUIRED_MESSAGE
                    + "<br>" + StatusConstants.INVALID_NUMBER_OF_SEAT + " : " + StatusConstants.INVALID_NUMBER_OF_SEAT_MESSAGE
                    + "<br>" + StatusConstants.INVALID_SALARY + " : " + StatusConstants.INVALID_SALARY_MESSAGE
                    + "<br>" + StatusConstants.INVALID_SALARY_RANGE + " : " + StatusConstants.INVALID_SALARY_RANGE_MESSAGE
                    + "<br>" + StatusConstants.INVALID_VACANCY_SEARCH_RANGE + " : " + StatusConstants.INVALID_VACANCY_SEARCH_RANGE_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY_MESSAGE
                    + "<br>" + StatusConstants.NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES + " : " + StatusConstants.NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @PutMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH + URLConstants.RESTORE_VACANCY)
    public Object restoreOrCloneVacancy(@PathVariable("id") Long id,
                                        @RequestBody EditVacancyRequest request,
                                        Authentication authentication) {
        BaseResp result = businessVacancyArchiveService.restoreOrCloneVacancy(id, request, authentication);
        return success(result);
    }

    /**
     * ==================== To show swagger ===============
     **/

    @Deprecated
    @ApiOperation(value = "Get suspend vacancies of company ", httpMethod = "GET", response = VacancySuspendResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.SUSPEND_VACANCY)
    public Object getSuspendVacancy(@Valid PageRequest pageRequest,
                                    Authentication authentication) {
        BaseResp result = businessVacancyArchiveService.getSuspendVacancy(pageRequest.getPage(), pageRequest.getSize(), authentication);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Get vacancies of company which have closed candidate", httpMethod = "GET", response = VacancyClosedResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.CLOSE)
    public Object getClosedVacancy(@Valid PageRequest pageReq,
                                   Authentication authentication) {
        BaseResp result = businessVacancyArchiveService.getClosedVacancy(pageReq.getPage(), pageReq.getSize(), authentication);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Get vacancy", httpMethod = "GET", response = VacancyResp.class, notes = note)
    @GetMapping(URLConstants.VACANCY_PATH + URLConstants.GET_METHOD)
    @PreAuthorize("isAuthenticated()")
    public Object doGetVacancy(@RequestParam(value = "id") Long id, Authentication authentication) {
        BaseResp result = businessVacancyService.get(id, authentication);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Save vacancy which default appointment", httpMethod = "POST", response = VacancyResp.class, notes = noteWithAppointment)
    @PostMapping(URLConstants.VACANCY_PATH)
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #request.companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    public Object doSaveVacancyWithAppointment(@RequestBody VacancyV2Req request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessVacancyService.save(null, request, authentication);
        return success(result);
    }

    @Deprecated
    @ApiOperation(value = "Update vacancy which default appointment", httpMethod = "PUT", response = VacancyResp.class, notes = noteWithAppointment)
    @PatchMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH)
    @PutMapping(URLConstants.VACANCY_PATH + URLConstants.ID_PATH)
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #request.companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    public Object doUpdateVacancyWithAppointment(@PathVariable(value = "id") Long id, @RequestBody VacancyV2Req request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessVacancyService.save(id, request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Sync all vacancies", httpMethod = "PATCH", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + "0 : No permission to access"
                    + "<br>" + "1 : Is Root Admin in the system"
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.VACANCY_PATH + URLConstants.SYNC_DATA)
    public Object syncVacancies(Authentication authentication) {
        BaseResp result = businessVacancyService.syncAllVacancies(authentication);
        return success(result);
    }

    /**
     * ==================== To show swagger ===============
     **/

    private static final String noteClassifyCandidate = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE + " : " + StatusConstants.NOT_FOUND_USER_CURRICULUM_VITAE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
            + "<br>" + StatusConstants.VACANCY_IS_INACTIVE + " : " + StatusConstants.VACANCY_IS_INACTIVE_MESSAGE
            + "<br>" + StatusConstants.VACANCY_IS_SUSPENDED + " : " + StatusConstants.VACANCY_IS_SUSPENDED_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br> type: APPLED = " + Const.Vacancy.ClassifyAction.APPLIED
            + "<br> type: REJECTED = " + Const.Vacancy.ClassifyAction.REJECTED;

    private static final String note = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE;

    private static final String noteDelete = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE;

    private final String notes = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.HTTP_OR_HTTPS_WRONG_FORMAT + " : " + StatusConstants.HTTP_OR_HTTPS_WRONG_FORMAT_MESSAGE
            + "<br>" + StatusConstants.SHORT_DESCRIPTION_IS_EMPTY + " : " + StatusConstants.SHORT_DESCRIPTION_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.FULL_DESCRIPTION_IS_EMPTY + " : " + StatusConstants.FULL_DESCRIPTION_IS_EMPTY_MESSAGE
            + "<br>" + StatusConstants.INVALID_NUMBER_OF_SEAT + " : " + StatusConstants.INVALID_NUMBER_OF_SEAT_MESSAGE
            + "<br>" + StatusConstants.INVALID_SALARY + " : " + StatusConstants.INVALID_SALARY_MESSAGE
            + "<br>" + StatusConstants.EXPECTED_START_DATE_IS_NULL + " : " + StatusConstants.EXPECTED_START_DATE_IS_NULL_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_JOB + " : " + StatusConstants.NOT_FOUND_JOB_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_CITY + " : " + StatusConstants.NOT_FOUND_CITY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_STAFF + " : " + StatusConstants.NOT_FOUND_STAFF_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_CURRENCY + " : " + StatusConstants.NOT_FOUND_CURRENCY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_BENEFIT + " : " + StatusConstants.NOT_FOUND_BENEFIT_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_SOFT_SKILL + " : " + StatusConstants.NOT_FOUND_SOFT_SKILL_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_WORKING_HOUR + " : " + StatusConstants.NOT_FOUND_WORKING_HOUR_MESSAGE
            + "<br>" + StatusConstants.CONFLICT_WORKING_HOUR + " : " + StatusConstants.CONFLICT_WORKING_HOUR_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_LANGUAGE + " : " + StatusConstants.NOT_FOUND_LANGUAGE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE;

    private final String noteWithAppointment = notes
            + "<br>" + StatusConstants.APPOINTMENTS_IS_DUPLICATE + " : " + StatusConstants.APPOINTMENTS_IS_DUPLICATE_MESSAGE
//            + "<br>" + StatusConstants.APPOINTMENTS_SELECTED_DATE_NULL + " : " + StatusConstants.APPOINTMENTS_SELECTED_DATE_NULL_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENTS_VACANCY_NOT_IN_COMPANY + " : " + StatusConstants.APPOINTMENTS_VACANCY_NOT_IN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_ROLE + " : " + StatusConstants.NOT_FOUND_ROLE_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE;
    private final String noteV2 = notes
            + "<br>" + StatusConstants.APPOINTMENTS_IS_DUPLICATE + " : " + StatusConstants.APPOINTMENTS_IS_DUPLICATE_MESSAGE
//            + "<br>" + StatusConstants.APPOINTMENTS_SELECTED_DATE_NULL + " : " + StatusConstants.APPOINTMENTS_SELECTED_DATE_NULL_MESSAGE
            + "<br>" + StatusConstants.APPOINTMENTS_VACANCY_NOT_IN_COMPANY + " : " + StatusConstants.APPOINTMENTS_VACANCY_NOT_IN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_ROLE + " : " + StatusConstants.NOT_FOUND_ROLE_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE;

    private final String noteEditVacancy = +StatusConstants.CURRENCY_ID_IS_REQUIRED + " : " + StatusConstants.CURRENCY_ID_IS_REQUIRED_MESSAGE
            + "<br>" + StatusConstants.INVALID_NUMBER_OF_SEAT + " : " + StatusConstants.INVALID_NUMBER_OF_SEAT_MESSAGE
            + "<br>" + StatusConstants.INVALID_SALARY + " : " + StatusConstants.INVALID_SALARY_MESSAGE
            + "<br>" + StatusConstants.INVALID_SALARY_RANGE + " : " + StatusConstants.INVALID_SALARY_RANGE_MESSAGE
            + "<br>" + StatusConstants.INVALID_VACANCY_SEARCH_RANGE + " : " + StatusConstants.INVALID_VACANCY_SEARCH_RANGE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_CURRENCY + " : " + StatusConstants.NOT_FOUND_CURRENCY_MESSAGE
            + "<br>" + StatusConstants.INVALID_SALARY_RANGE + " : " + StatusConstants.INVALID_SALARY_RANGE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_VACANCY + " : " + StatusConstants.NOT_FOUND_VACANCY_MESSAGE
            + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_VACANCY_MESSAGE
            + "<br>" + StatusConstants.ASSESSMENT_ID_NOT_EXIST + " : " + StatusConstants.ASSESSMENT_ID_NOT_EXIST_MESSAGE
            + "<br>" + StatusConstants.NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES + " : " + StatusConstants.NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES_MESSAGE;

    class VacancyResp extends BaseResp<VacancyDTO> {
    }

    class VacancySuspendResp extends BaseResp<PagedResult<VacancyDTO>> {
    }

    class VacancySuspendShortInfoResp extends BaseResp<PagedResult<VacancyShortInformationDTO>> {
    }

    class VacancyClosedResp extends BaseResp<PagedResult<VacancyClosedDTO>> {
    }

    class VacancyClosedShortInfoResp extends BaseResp<PagedResult<VacancyClosedShortInfoDTO>> {
    }

    class VacancyCandidateClosedResp extends BaseResp<PagedResult<CandidateClosedDTO>> {
    }

    class VacancyCandidateRespPage extends BaseResp<PagedResult<VacancyCandidateResp>> {
    }

    class VacancyCandidateListResp extends BaseResp<List<VacancyCandidateResp>> {
    }

    class MatchingCandidateResp extends BaseResp<List<UserCurriculumVitaeDTO>> {
    }

    class VacancyCandidatesToClose extends BaseResp<PagedResult<CandidateInfoDTO>> {
    }

    class MatchingCandidateComparisonResp extends BaseResp<CandidatesResp> {
    }
}
