package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessCompanyService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.company.*;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.request.CompanyReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qooco.boost.constants.PaginationConstants.DEFAULT_PAGE_NUMBER;
import static com.qooco.boost.constants.PaginationConstants.DEFAULT_PAGINATION;
import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.URLConstants.FIND_APPROVED_COMPANY_BY_COUNTRY;

@Api(tags = "Company")
@RestController
@RequestMapping(URLConstants.COMPANY_PATH)
public class CompanyController extends BaseController {

    @Autowired
    private BusinessCompanyService businessCompanyService;

    @ApiOperation(value = "Get company", httpMethod = "GET", response = CompanyResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ID_PATH)
    public Object getCompanyById(@RequestParam(value = "id") Long id, Authentication authentication) {
        BaseResp company = businessCompanyService.get(id, authentication);
        return success(company);
    }

    @ApiOperation(value = "Get company by status", httpMethod = "GET", response = CompaniesResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE    )

    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_BY_STATUS)
    public Object getCompanyNeedApproval(@RequestParam(value = "status") String status, Authentication authentication) {
        BaseResp company = businessCompanyService.getCompanyByStatus(status, authentication);
        return success(company);
    }

    @ApiOperation(value = "Get working companies of user by type", httpMethod = "GET", response = CompaniesWorkedResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
                    + "<br>" + "Types: "
                             + "<br> PENDING_AUTHORIZE_COMPANY = 1"
                             + "<br> PENDING_JOIN_COMPANY = 2"
                             + "<br> APPROVED_AUTHORIZE_COMPANY = 3"
                             + "<br> APPROVED_JOIN_COMPANY = 4"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.WORKING_COMPANY)
    public Object getWorkingCompanies(@RequestParam(value = "type", required = false) List<Integer> type, Authentication authentication) {
        BaseResp company = businessCompanyService.getWorkingCompanies(type, authentication);
        return success(company);
    }

    @ApiOperation(value = "Approve new company request", httpMethod = "PUT", response = ResponseStatus.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND + " : " + StatusConstants.NOT_FOUND_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
                    + "<br>" + "0 : No permission to access"
                    + "<br>" + "1 : Is Root Admin in the system"
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.APPROVAL_REQUEST)
    @PutMapping(URLConstants.APPROVAL_REQUEST)
    public Object approveNewCompany(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.approveNewCompany(id, authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Save company", httpMethod = "POST", response = CompanyResp.class, notes = saveCompanyNote)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.SAVE_METHOD)
    public Object createCompany(@RequestBody CompanyReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
        return businessCompanyService.saveCompany(request, authentication);
    }

    @ApiOperation(value = "Update company", httpMethod = "PUT", response = CompanyResp.class, notes = saveCompanyNote)
    @PreAuthorize("isAuthenticated()")
    @PutMapping(URLConstants.ID_PATH)
    public Object editCompany(@PathVariable Long id, @RequestBody CompanyReq request, Authentication authentication) {
        request.setId(id);
        saveRequestBodyToSystemLogger(request);
        return businessCompanyService.saveCompany(request, authentication);
    }

    @ApiOperation(value = "Get companies of user", httpMethod = "GET", response = CompanyShortResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_COMPANY_OF_USER)
    public Object getCompanyOfUser(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp companies = businessCompanyService.getCompanyOfUser(authenticatedUser.getId(), authentication);
        return success(companies);
    }

    @ApiOperation(value = "Search company by name", httpMethod = "GET", response = CompanyShortPaging.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.SEARCH_COMPANY_BY_NAME)
    public Object searchByName(@RequestParam(value = "keyword", required = false, defaultValue = PaginationConstants.DEFAULT_KEYWORD) String keyword,
                               @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGINATION) int size) {
        BaseResp result = businessCompanyService.searchCompanyByName(keyword, page, size);
        return success(result);
    }

    @ApiOperation(value = "Search company by name for join company, excepted company user is staff", httpMethod = "GET", response = CompanyShortPaging.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.SEARCH_COMPANY_BY_NAME_FOR_JOIN_COMPANY)
    public Object searchByNameForJoiningCompany(@RequestParam(value = "keyword", required = false, defaultValue = PaginationConstants.DEFAULT_KEYWORD) String keyword,
                               @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGINATION) int size,
                                Authentication authentication) {
        BaseResp result = businessCompanyService.searchCompanyByNameForJoinCompany(keyword, page, size, authentication);
        return success(result);
    }

    @ApiOperation(value = "Find approved company by country", httpMethod = "GET", response = CompanyShortPaging.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(FIND_APPROVED_COMPANY_BY_COUNTRY)
    public Object findByCountryAndStatusApproved(@RequestParam long countryId,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGINATION) int size) {
        return success(businessCompanyService.findByCountryAndStatusApproved(countryId, page, size));
    }

    @ApiOperation(value = "Get company's short information", httpMethod = "GET", response = ShortCompanyResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
    )

    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ID_PATH + URLConstants.SHORT_INFORMATION)
    public Object getCompanyShortInformation(@PathVariable Long id, Authentication authentication) {
        BaseResp company = businessCompanyService.getShortCompany(id, authentication);
        return success(company);
    }

    @ApiOperation(value = "Send join company request", httpMethod = "PATCH", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.COMPANY_ID_IS_EMPTY + " : " + StatusConstants.COMPANY_ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_JOIN_REQUEST + " : " + StatusConstants.NOT_FOUND_JOIN_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.PENDING_STATUS + " : " + StatusConstants.PENDING_STATUS_MESSAGE
                    + "<br>" + StatusConstants.DISAPPROVED_STATUS + " : " + StatusConstants.DISAPPROVED_STATUS_MESSAGE
                    + "<br>" + StatusConstants.JOINED_COMPANY_REQUEST + " : " + StatusConstants.JOINED_COMPANY_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.PENDING_JOIN_COMPANY_REQUEST + " : " + StatusConstants.PENDING_JOIN_COMPANY_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.DECLINED_JOIN_COMPANY_REQUEST + " : " + StatusConstants.DECLINED_JOIN_COMPANY_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.SAVE_FAIL + " : " + StatusConstants.SAVE_FAIL_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.isStaff(authentication, #id)")
    @PatchMapping(URLConstants.JOIN_REQUEST)
    public Object sendJoinCompany(@PathVariable long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.joinCompanyRequest(id, authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Authority or decline the join company request", httpMethod = "PATCH", response = MessageResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_INVALID + " : " + StatusConstants.ID_INVALID_MESSAGE
                    + "<br>" + StatusConstants.STATUS_INVALID + " : " + StatusConstants.STATUS_INVALID_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_CHAT_MESSAGE + " : " + StatusConstants.NOT_FOUND_CHAT_MESSAGE_MESSAGE
                    + "<br>" + StatusConstants.NOT_AUTHORIZATION_COMPANY_MESSAGE + " : " + StatusConstants.NOT_AUTHORIZATION_COMPANY_MESSAGE_MESSAGE
                    + "<br>" + StatusConstants.NOT_ADMIN_OF_COMPANY + " : " + StatusConstants.NOT_ADMIN_OF_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_JOIN_REQUEST + " : " + StatusConstants.NOT_FOUND_JOIN_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.UPDATE_JOIN_COMPANY_REQUEST_AUTHORIZED + " : " + StatusConstants.UPDATE_JOIN_COMPANY_REQUEST_AUTHORIZED_MESSAGE
                    + "<br>" + StatusConstants.UPDATE_JOIN_COMPANY_REQUEST_DECLINED + " : " + StatusConstants.UPDATE_JOIN_COMPANY_REQUEST_DECLINED_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.UPDATE_JOIN_REQUEST)
    public Object updateJoinCompanyRequest(@RequestParam(value = "messageId") String messageId,
                                           @RequestParam(value = "status") int status, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.updateJoinCompanyStatusRequest(messageId, status, authenticatedUser.getId(), authentication);
        return success(result);
    }


    @ApiOperation(value = "Get join company requests", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.COMPANY_ID_IS_EMPTY + " : " + StatusConstants.COMPANY_ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #id, 'ADMIN', 'HEAD_RECRUITER')")
    @GetMapping(URLConstants.GET_JOIN_COMPANY_REQUEST)
    public Object getJoinCompanyRequest(@RequestParam(value = "id") Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.getJoinCompanyRequest(id, authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Get company profile in dashboard screen", httpMethod = "GET", response = CompanyProfile.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + BAD_REQUEST + " : " + BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.COMPANY_ID_IS_EMPTY + " : " + StatusConstants.COMPANY_ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE

    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ID + URLConstants.PROFILE_PATH)
    public Object getCompanyProfileInDashboard(@PathVariable(value = "id") Long id, Authentication authentication) {
        BaseResp result = businessCompanyService.getCompanyProfileInfo(id, authentication);
        return success(result);
    }

    @ApiOperation(value = "Set default company request", httpMethod = "PATCH", response = CompanyProfile.class,
            notes = "Response code description:"
                    + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + StatusConstants.SAVE_FAIL + " : " + StatusConstants.SAVE_FAIL_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.SWITCH_COMPANY + URLConstants.ID_PATH)
    public Object switchCompany(@PathVariable Long id, Authentication authentication) {
        BaseResp result = businessCompanyService.switchCompany(id, authentication);
        return success(result);
    }

//========================@Deprecated==========================================================
    @Deprecated
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_SHORT_COMPANY)
    public Object getShortCompany(@RequestParam(value = "id") Long id, Authentication authentication) {
        BaseResp company = businessCompanyService.getShortCompany(id, authentication);
        return success(company);
    }

    @Deprecated
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.GET_METHOD)
    public Object getCompany(@RequestParam(value = "id") Long id, Authentication authentication) {
        BaseResp company = businessCompanyService.get(id, authentication);
        return success(company);
    }


    @Deprecated
    @PreAuthorize("isAuthenticated()")
    @PutMapping(URLConstants.APPROVAL_REQUEST)
    public Object approveNewCompanyPut(@PathVariable Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.approveNewCompany(id, authenticatedUser.getId());
        return success(result);
    }

    @Deprecated
    @PreAuthorize("isAuthenticated()")
    @PutMapping(URLConstants.UPDATE_JOIN_REQUEST)
    public Object updateJoinCompanyRequestPut(@RequestParam(value = "messageId") String messageId,
                                           @RequestParam(value = "status") int status, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.updateJoinCompanyStatusRequest(messageId, status, authenticatedUser.getId(), authentication);
        return success(result);
    }

    @Deprecated
    @PreAuthorize("isAuthenticated() and @boostSecurity.isStaff(authentication, #id)")
    @PutMapping(URLConstants.JOIN_REQUEST)
    public Object sendJoinCompanyPut(@PathVariable long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessCompanyService.joinCompanyRequest(id, authenticatedUser.getId());
        return success(result);
    }

    private class CompanyResp extends BaseResp<CompanyDTO> {
    }

    private class CompaniesResp extends BaseResp<List<CompanyDTO>> {
    }

    private class CompaniesWorkedResp extends BaseResp<List<CompanyWorkedDTO>> {
    }

    private class CompanyShortResp extends BaseResp<List<CompanyShortInformationDTO>> {
    }

    private class ShortCompanyResp extends BaseResp<CompanyShortInformationDTO> {
    }

    private class CompanyShortPaging extends BaseResp<PagedResult<CompanyBaseDTO>> {
    }

    private class CompanyProfile extends BaseResp<CompanyProfileDTO> {
    }

    private class MessageResp extends BaseResp<List<MessageDTO>> {
    }

    private static final String saveCompanyNote = "Create new company with id is NULL, update with specific long value"
            + "<br>" + "Response code description:"
            + "<br>" + SUCCESS + " : " + SUCCESS_MESSAGE
            + "<br>" + StatusConstants.HTTP_OR_HTTPS_WRONG_FORMAT + " : " + StatusConstants.HTTP_OR_HTTPS_WRONG_FORMAT_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_LOGO + " : " + StatusConstants.SAVE_COMPANY_EMPTY_LOGO_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_NAME + " : " + StatusConstants.SAVE_COMPANY_EMPTY_NAME_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_ADDRESS + " : " + StatusConstants.SAVE_COMPANY_EMPTY_ADDRESS_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_PHONE_NUMBER + " : " + StatusConstants.SAVE_COMPANY_EMPTY_PHONE_NUMBER_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_EMAIL + " : " + StatusConstants.SAVE_COMPANY_EMPTY_EMAIL_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_WEB + " : " + StatusConstants.SAVE_COMPANY_EMPTY_WEB_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_AMADEUS + " : " + StatusConstants.SAVE_COMPANY_EMPTY_AMADEUS_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_GALILEO + " : " + StatusConstants.SAVE_COMPANY_EMPTY_GALILEO_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_WORLDSPAN + " : " + StatusConstants.SAVE_COMPANY_EMPTY_WORLDSPAN_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_SABRE + " : " + StatusConstants.SAVE_COMPANY_EMPTY_SABRE_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EMPTY_DESCRIPTION + " : " + StatusConstants.SAVE_COMPANY_EMPTY_DESCRIPTION_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_LOGO + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_LOGO_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_COMPANY_NAME + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_COMPANY_NAME_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_ADDRESS + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_ADDRESS_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_PHONE + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_PHONE_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_WEBPAGE + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_WEBPAGE_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_AMADEUS + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_AMADEUS_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_GALILEO + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_GALILEO_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_WORLDSPAN + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_WORLDSPAN_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_SABRE + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_SABRE_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_DESCRIPTION + " : " + StatusConstants.SAVE_COMPANY_EXCEED_MAX_LENGTH_DESCRIPTION_MESSAGE
            + "<br>" + StatusConstants.SAVE_COMPANY_HAS_PENDING_COMPANY + " : " + StatusConstants.SAVE_COMPANY_HAS_PENDING_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_CITY + " : " + StatusConstants.NOT_FOUND_CITY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_HOTEL_TYPE + " : " + StatusConstants.NOT_FOUND_HOTEL_TYPE_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE;
}
