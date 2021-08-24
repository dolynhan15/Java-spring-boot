package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessAdminService;
import com.qooco.boost.business.BusinessDataFeedbackService;
import com.qooco.boost.business.BusinessSelectDataDemoService;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.data.mongo.entities.SocketConnectionDoc;
import com.qooco.boost.data.mongo.entities.SystemLoggerDoc;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.AppVersionDTO;
import com.qooco.boost.models.request.AppVersionReq;
import com.qooco.boost.models.request.admin.CloneStressTestUserReq;
import com.qooco.boost.models.request.demo.SelectDataReq;
import com.qooco.boost.threads.services.SyncUserCVService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "Admin page", value = URLConstants.ADMIN_PATH, description = "Manage all api for admin")
@RestController
@RequestMapping(URLConstants.ADMIN_PATH)
public class AdminController extends BaseController {
    @Autowired
    private BusinessAdminService businessAdminService;
    @Autowired
    private BusinessSelectDataDemoService businessSelectDataDemoService;
    @Autowired
    private BusinessDataFeedbackService businessDataFeedbackService;
    @Autowired
    private SyncUserCVService syncUserCVService;

    @ApiOperation(value = "Create new app version into system", httpMethod = "POST", response = AppVersionResp.class)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.APP_VERSION_PATH)
    public Object saveAppVersion(@RequestBody AppVersionReq request, Authentication authentication) {
        BaseResp result = businessAdminService.saveAppVersion(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Update new app version into system", httpMethod = "PUT", response = AppVersionResp.class)
    @PreAuthorize("isAuthenticated()")
    @PutMapping(URLConstants.APP_VERSION_PATH + URLConstants.ID_PATH)
    public Object updateAppVersion(@PathVariable Long id, @RequestBody AppVersionReq request, Authentication authentication) {
        request.setId(id);
        BaseResp result = businessAdminService.saveAppVersion(request, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get all app version in system", httpMethod = "GET", response = AppVersionsResp.class)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.APP_VERSION_PATH)
    public Object getAll(Authentication authentication) {
        BaseResp result = businessAdminService.getAllAppVersion(authentication);
        return success(result);
    }

    @ApiOperation(value = "Update participant and created by base on first message and message center", httpMethod = "PUT", response = BaseResp.class)
    @PreAuthorize("isAuthenticated()")
    @PutMapping(URLConstants.UPDATE_PARTICIPANT_CONVERSATION)
    public Object updateParticipantAndCreatedBy(Authentication authentication,
                                                @RequestParam(value = "total", required = false, defaultValue = "0") int total) {
        BaseResp result = businessAdminService.updateParticipantAndCreatedBy(authentication, total);
        return success(result);
    }

    @ApiOperation(value = "Get Socket Connection status by duration time", httpMethod = "GET", response = SocketConnectionResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @GetMapping(URLConstants.ADMIN_SOCKET_CONNECTION_PATH)
    public Object getSocketConnection(Authentication authentication,
                                      @RequestParam(value = "from", required = false) Long from,
                                      @RequestParam(value = "to", required = false) Long to) {
        BaseResp result = businessAdminService.getSocketConnection(authentication, from, to);
        return success(result);
    }

    @ApiOperation(value = "Get System Logger from system by duration time", httpMethod = "GET", response = SystemLoggerResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @GetMapping(URLConstants.ADMIN_SYSTEM_LOGGER_PATH)
    public Object getSystemLogger(Authentication authentication,
                                  @RequestParam(value = "from", required = false) Long from,
                                  @RequestParam(value = "to", required = false) Long to) {
        BaseResp result = businessAdminService.getSystemLogger(authentication, from, to);
        return success(result);
    }

    @ApiOperation(value = "Patch Appointment Detail Doc", httpMethod = "PATCH", response = SystemLoggerResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @PatchMapping(URLConstants.ADMIN_SYSTEM_PATCH_APPOINTMENT_DETAIL)
    public Object patchAppointmentDetailDoc() {
        BaseResp result = businessAdminService.patchAppointmentDetailDoc();
        return success(result);
    }

    @ApiOperation(value = "Clone user to stress test (without register on Qooco system), userProfileId = 5,xxx,xxx,xxx ", httpMethod = "POST", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @PostMapping(URLConstants.ADMIN_SYSTEM_PATCH_CLONE_USER)
    public Object patchCloneUserProfile(@RequestBody CloneStressTestUserReq req,
                                        @RequestParam(value = "onlyFromMongo", required = false, defaultValue = "false") boolean onlyFromMongo,
                                        Authentication authentication) {
        if (onlyFromMongo) {
            businessAdminService.cloneUserCVDoc(authentication, req);
        } else {
            businessAdminService.cloneUserProfile(authentication, req);
        }
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Synchronize all curriculum vitae users from Oracle to Mongo", httpMethod = "GET", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @GetMapping(URLConstants.SYNC_USER_CV)
    public Object syncProfileStrengthOfUsers(@RequestParam(value = "isOnlyMissingOnMongo", required = false, defaultValue = "false") boolean isOnlyMissingOnMongo) {
        syncUserCVService.syncUserCVToMongo(isOnlyMissingOnMongo);
        return success(new BaseResp<>());
    }

    @ApiOperation(value = "Synchronize all curriculum vitae users from Oracle to Mongo", httpMethod = "GET", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @GetMapping(URLConstants.ADMIN_SYSTEM_PATCH_VACANCY_SEAT)
    public Object patchVacancySeat() {
        return success(businessAdminService.patchVacancySeat());
    }

    @ApiOperation(value = "Save locale data to MongoBD by json file", httpMethod = "PATCH", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @PatchMapping(URLConstants.ADMIN_SYSTEM_PATCH_LOCALE_DATA + URLConstants.NAME)
    public Object patchLocaleData(@PathVariable(value = "name", required = false, name = "")String fileName) {
        return success(businessAdminService.patchLocaleDataInMongo(fileName));
    }

    //===========SELECT DATA DEMO
    @ApiOperation(value = "Generate data demo for Select app", httpMethod = "POST", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @PostMapping(URLConstants.ADMIN_SYSTEM_GENERATE_DEMO_DATA + URLConstants.ID)
    public Object generateDemoData(@PathVariable(value = "id") Long companyId, @RequestBody SelectDataReq req) {
        req.setCompanyId(companyId);
        return success(businessSelectDataDemoService.genSelectDataDemo(req));
    }

    @ApiOperation(value = "Delete data demo for Select app by company id", httpMethod = "DELETE", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @DeleteMapping(URLConstants.ADMIN_SYSTEM_GENERATE_DEMO_DATA + URLConstants.ID)
    public Object deleteDemoData(@PathVariable(value = "id") Long companyId ) {
        return success(businessSelectDataDemoService.deleteSelectDataDemo(companyId));
    }

    @ApiOperation(value = "Send data feedback message", httpMethod = "POST", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @PostMapping(URLConstants.ADMIN_SYSTEM_SEND_FEEDBACK_MESSAGE)
    public Object sendDataFeedbackMessage(@RequestParam(value = "feedbackType") int feedbackType) {
        return success(businessDataFeedbackService.sendDataFeedbackMessage(feedbackType));
    }

    @ApiOperation(value = "Patch localization from excel file", httpMethod = "POST", response = BaseResp.class)
    @PreAuthorize("@boostSecurity.isSystemAdmin(authentication)")
    @PostMapping(URLConstants.ADMIN_SYSTEM_PATCH_LOCALIZATION)
    public Object patchLocalization(@RequestParam("file") MultipartFile file, Authentication authentication) {
        return success(businessAdminService.patchLocalization(file, authentication));
    }

    //==============================================


    private class AppVersionsResp extends BaseResp<List<AppVersionDTO>> {
    }

    private class AppVersionResp extends BaseResp<AppVersionDTO> {
    }

    private class SystemLoggerResp extends BaseResp<List<SystemLoggerDoc>> {
    }

    private class SocketConnectionResp extends BaseResp<List<SocketConnectionDoc>> {
    }
}
