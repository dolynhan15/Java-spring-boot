package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessStaffService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.staff.ContactPersonDTO;
import com.qooco.boost.models.dto.staff.StaffDTO;
import com.qooco.boost.models.request.ActiveTime;
import com.qooco.boost.models.request.RoleAssignedReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Staff", value = URLConstants.STAFF)
@RestController
@RequestMapping(URLConstants.COMPANY_PATH)
public class StaffController extends BaseController {

    @Autowired
    private BusinessStaffService businessStaffService;

    @ApiOperation(value = "Get contact person", httpMethod = "GET", response = ContactPersonPagingResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
                    + "<br>" + StatusConstants.NOT_STAFF_OF_COMPANY + " : " + StatusConstants.NOT_STAFF_OF_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #companyId, 'ADMIN', 'HEAD_RECRUITER', 'RECRUITER')")
    @RequestMapping(value = URLConstants.STAFF + URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getContactPerson(Authentication authentication,
                                   @RequestParam(value = "companyId") long companyId,
                                   @RequestParam(value = "page", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                                   @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp resp = businessStaffService.getContactPersons(companyId, authenticatedUser.getId(), page, size, authentication);
        return success(resp);
    }

    @ApiOperation(value = "Get staff of company", httpMethod = "GET", response = ContactPersonPagingResp.class, notes = GET_STAFF_NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.ID_PATH + URLConstants.STAFF)
    public Object getCompanyStaffs(Authentication authentication,
                                   @PathVariable("id") Long companyId,
                                   @RequestParam(value = "isExcludedMe", required = false, defaultValue = "false") Boolean isExcludedMe,
                                   @RequestParam(value = "page", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                                   @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        BaseResp resp = businessStaffService.getCompanyStaffs(authentication, companyId, isExcludedMe, page, size);
        return success(resp);
    }

    @ApiOperation(value = "Get staff of company", httpMethod = "GET", response = ContactPersonPagingResp.class, notes = GET_STAFF_NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.STAFF)
    public Object getCompanyStaffs(Authentication authentication,
                                   @RequestParam(value = "isExcludedMe", required = false, defaultValue = "false") Boolean isExcludedMe,
                                   @RequestParam(value = "page", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                                   @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        BaseResp resp = businessStaffService.getCompanyStaffs(authentication, isExcludedMe, page, size);
        return success(resp);
    }

    @ApiOperation(value = "Get company roles by authorization", httpMethod = "GET", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br> role: ADMIN, HEAD_RECRUITER, RECRUITER, ANALYST. Leave null to remove role"
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #companyId, 'ADMIN', 'HEAD_RECRUITER')")
    @GetMapping(value = URLConstants.STAFF + URLConstants.GET_METHOD + URLConstants.ROLE_AUTHORIZED)
    public Object getCompanyRolesByUser(@RequestParam long companyId, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = businessStaffService.getCompanyRolesByUser(companyId, authenticatedUser.getId(), authentication);
        return success(result);
    }

    @ApiOperation(value = "Get staff list by role company", httpMethod = "GET", response = StaffPagingResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, #companyId, 'ADMIN', 'HEAD_RECRUITER')")
    @RequestMapping(value = URLConstants.STAFF + URLConstants.GET_BY_ROLE_COMPANY, method = RequestMethod.GET)
    public Object getStaffListByRoleCompany(Authentication authentication,
                                            @RequestParam(value = "companyId") long companyId,
                                            @RequestParam(value = "page", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                                            @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp staffResp = businessStaffService.findStaffsByRoleCompany(companyId, authenticatedUser.getId(), page, size, authentication);
        return success(staffResp);
    }

    @ApiOperation(value = "Set company role", httpMethod = "PATCH", response = BaseResp.class, notes =SET_ROLE_NOTE)
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN')")
    @PatchMapping(value = URLConstants.STAFF + URLConstants.ID_PATH + URLConstants.ASSIGN_ROLE)
    public Object setRoleForCompanyStaff(@PathVariable("id") Long staffId, @RequestBody RoleAssignedReq request, Authentication authentication) {
        request.setStaffId(staffId);
        saveRequestBodyToSystemLogger(request);
        BaseResp result = businessStaffService.setRoleStaffOfCompany(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Delete company staff", httpMethod = "DELETE", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_ACCESS + " : " + StatusConstants.NO_PERMISSION_TO_ACCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_STAFF + " : " + StatusConstants.NOT_FOUND_STAFF_MESSAGE
    )
    @PreAuthorize("isAuthenticated() and @boostSecurity.hasCompanyRoles(authentication, 'ADMIN')")
    @DeleteMapping(URLConstants.STAFF + URLConstants.ID_PATH)
    public Object deleteCompanyStaff(Authentication authentication, @PathVariable("id") long staffId) {
        return success(businessStaffService.deleteStaff(authentication, staffId));
    }

    @ApiOperation(value = "Save Active Times in Company", httpMethod = "POST", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.INVALID_TIME_RANGE + " : " + StatusConstants.INVALID_TIME_RANGE_MESSAGE
                    + "<br>" + StatusConstants.NOT_STAFF_OF_COMPANY + " : " + StatusConstants.NOT_STAFF_OF_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.STAFF + URLConstants.ACTIVE_TIMES, method = RequestMethod.POST)
    public Object saveActiveMinutes(Authentication authentication, @RequestBody List<ActiveTime> request) {
        return success(businessStaffService.saveStaffWorking(authentication, request));
    }

    private static final String SET_ROLE_NOTE = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_ROLE + " : " + StatusConstants.NOT_FOUND_ROLE_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_CHAT_MESSAGE + " : " + StatusConstants.NOT_FOUND_CHAT_MESSAGE_MESSAGE
                    + "<br> role: ADMIN, HEAD_RECRUITER, RECRUITER, ANALYST, NORMAL_USER. Leave null to remove role";

    private static final String GET_STAFF_NOTE = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
            + "<br>" + StatusConstants.NOT_STAFF_OF_COMPANY + " : " + StatusConstants.NOT_STAFF_OF_COMPANY_MESSAGE
            + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE;

    private class StaffPagingResp extends BaseResp<PagedResult<StaffDTO>> {
    }

    private class ContactPersonPagingResp extends BaseResp<PagedResult<ContactPersonDTO>> {
    }
}
